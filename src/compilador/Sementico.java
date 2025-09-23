package compilador;

// Los tres aqyí abajo los quitaremos, son solo csv
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Sementico {
    private static int GLOBAL_ERROR_COUNTER = 0;
    public static synchronized String nextErrorToken() {
        GLOBAL_ERROR_COUNTER++;
        return "Error" + GLOBAL_ERROR_COUNTER;
    }

    public static class Symbol {
        public final String name;
        public final String type;
        public final String value; // opcional, puede ser null
        public final int lineDecl;

        public Symbol(String name, String type, String value, int lineDecl) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.lineDecl = lineDecl;
        }
    }

    public static class SemError {
        public final String id; 
        public final String phase; // "semantic" 
        public final String token; // "ID", de nuestros tokens
        public final String lexeme; // variable
        public final int line;
        public final String description;

        public SemError(String phase, String token, String lexeme, int line, String description) {
            this.id = Sementico.nextErrorToken();
            this.phase = phase;
            this.token = token;
            this.lexeme = lexeme;
            this.line = line;
            this.description = description;

        }
        @Override
        public String toString() {
            return String.format("%s [%s] (%s:%s) @%d -> %s",
                id,
                phase,
                token == null ? "-" : token,
                lexeme == null ? "-" : lexeme,
                line,
                description);
        }
    }

    // tabla de símbolos (preserva orden de inserción)
    private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();
    private final List<SemError> errors = new ArrayList<>();

    // Analizar y exportar CSVs, lo de la exportación lo podemos quitar después (todo el try/catch), junto a los argumentos de path
    public static void analyzeAndExport(Parser.Program prog,
                                        List<Lex.ErrorEntry> lexErrors,
                                        List<Parser.SyntaxError> parseErrors,
                                        String symbolCsvPath,
                                        String errorCsvPath) {
        Sementico analyzer = new Sementico();
        analyzer.analyzeProgram(prog);

        // convertir errores léxicos y sintácticos a SemError (para reportar juntos)
        List<SemError> allErrors = new ArrayList<>(analyzer.errors);

        if (lexErrors != null) {
            for (Lex.ErrorEntry le : lexErrors) {
                allErrors.add(new SemError("lexical", null, le.lexeme, le.line, le.description));
            }
        }
        if (parseErrors != null) {
            for (Parser.SyntaxError se : parseErrors) {
                allErrors.add(new SemError("syntax", se.token, se.lexeme, se.line, se.description));
            }
        }

        // exportar símbolos y errores
        try {
            analyzer.exportSymbolsCSV(symbolCsvPath);
            analyzer.exportErrorsCSV(errorCsvPath, allErrors);
            System.out.printf("Exportado símbolo a: %s%n", symbolCsvPath);
            System.out.printf("Exportado errores a: %s%n", errorCsvPath);
            System.out.printf("Resumen: %d símbolos, %d errores (incluyendo léx/Sementico).%n",
                    analyzer.symbols.size(), allErrors.size());
        } catch (IOException ex) {
            System.err.println("Error al escribir CSVs: " + ex.getMessage());
        }
    }

    // Recorrido del programa (top-level)
    private void analyzeProgram(Parser.Program prog) {
        if (prog == null) return;
        for (Parser.Stmt s : prog.statements) {
            analyzeStmt(s);
        }
    }

    // Analizar una sentencia
    private void analyzeStmt(Parser.Stmt s) {
        if (s == null) return;

        if (s instanceof Parser.VarDecl) {
            Parser.VarDecl vd = (Parser.VarDecl) s;
            if (symbols.containsKey(vd.name)) {
                errors.add(new SemError("semantic", "ID", vd.name, vd.line,
                        "Variable redeclarada"));
            } else {
                // Si hay inicializador, obtener tipo
                String fromType = null;
                if (vd.init != null) {
                    fromType = evalExprType(vd.init);
                    // si la asignación no es compatible, reportar
                    if (!isAssignable(vd.typeName, fromType)) {
                        errors.add(new SemError("semantic", null, vd.name, vd.line,
                                String.format("Incompatibilidad: no se puede asignar %s a %s", fromType, vd.typeName)));
                    }
                }
                // Insertar símbolo (valor lo dejamos como null o la literal si queremos)
                String valStr = null;
                if (vd.init instanceof Parser.Literal) {
                    Parser.Literal lit = (Parser.Literal) vd.init;
                    valStr = String.valueOf(lit.value);
                }
                symbols.put(vd.name, new Symbol(vd.name, vd.typeName, valStr, vd.line));
            }
        } else if (s instanceof Parser.Assignment) {
            Parser.Assignment a = (Parser.Assignment) s;
            if (!symbols.containsKey(a.name)) {
                errors.add(new SemError("semantic", "ID", a.name, a.line, "Variable no declarada"));
            } else {
                String varType = symbols.get(a.name).type;
                String exprType = evalExprType(a.expr);
                if (!isAssignable(varType, exprType)) {
                    errors.add(new SemError("semantic", null, a.name, a.line,
                            String.format("Incompatibilidad: no se puede asignar %s a %s", exprType, varType)));
                }
            }
        } else if (s instanceof Parser.WhileStmt) {
            Parser.WhileStmt w = (Parser.WhileStmt) s;
            String condType = evalExprType(w.cond);
            if (!isNumeric(condType)) {
                errors.add(new SemError("semantic", null, null, w.line,
                        "Condición de while no es numérica"));
            }
            for (Parser.Stmt st : w.body) analyzeStmt(st);
        } else if (s instanceof Parser.IfStmt) {
            Parser.IfStmt iff = (Parser.IfStmt) s;
            String condType = evalExprType(iff.cond);
            if (!isNumeric(condType)) {
                errors.add(new SemError("semantic", null, null, iff.line,
                        "Condición de if no es numérica"));
            }
            for (Parser.Stmt st : iff.thenBody) analyzeStmt(st);
            if (iff.elseBody != null) for (Parser.Stmt st : iff.elseBody) analyzeStmt(st);
        } else {
            // otros tipos (por si se añaden más, lo cual no creo)
        }
    }

    // Evalúa el tipo de una expresión; agrega errores semánticos cuando sea necesario.
    // Devuelve "nummy", "numpt", "chara" o una cadena por defecto "nummy" para evitar cascadas.
    private String evalExprType(Parser.Expr expr) {
        if (expr == null) return null;
        if (expr instanceof Parser.Literal) {
            Parser.Literal lit = (Parser.Literal) expr;
            // En Parser, los literales ya guardaron los typeName 'nummy','numpt','chara'
            return lit.typeName;
        }
        if (expr instanceof Parser.VarRef) {
            Parser.VarRef vr = (Parser.VarRef) expr;
            if (!symbols.containsKey(vr.name)) {
                errors.add(new SemError("semantic", "ID", vr.name, vr.line, "Variable usada antes de declarar"));
                return "nummy"; // valor por defecto para continuar
            }
            return symbols.get(vr.name).type;
        }
        if (expr instanceof Parser.UnaryOp) {
            Parser.UnaryOp u = (Parser.UnaryOp) expr;
            String t = evalExprType(u.operand);
            if ("-".equals(u.op)) {
                if (!isNumeric(t)) {
                    errors.add(new SemError("semantic", null, null, u.line, "Operando unario '-' no es numérico"));
                }
                return t;
            }
            return t;
        }
        if (expr instanceof Parser.BinaryOp) {
            Parser.BinaryOp b = (Parser.BinaryOp) expr;
            String lt = evalExprType(b.left);
            String rt = evalExprType(b.right);
            String op = b.op;

            // Aritméticos
            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
                // strings: solo permitir + como concatenación si ambos charcha
                if ("chara".equals(lt) || "chara".equals(rt)) {
                    if (op.equals("+") && "chara".equals(lt) && "chara".equals(rt)) {
                        return "chara";
                    }
                    errors.add(new SemError("semantic", null, null, b.line,
                            String.format("Operación inválida '%s' entre %s y %s", op, lt, rt)));
                    return "nummy";
                }
                // Si alguno float -> numpt; sino nummy
                if ("numpt".equals(lt) || "numpt".equals(rt)) return "numpt";
                return "nummy";
            }

            // Comparaciones
            if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
                if ("chara".equals(lt) || "chara".equals(rt)) {
                    errors.add(new SemError("semantic", null, null, b.line,
                            "Comparación relacional entre tipos incompatibles"));
                    return "nummy";
                }
                return "nummy"; // valor booleano representado como nummy
            }

            // Igualdad
            if (op.equals("==") || op.equals("!=")) {
                // permitir comparar números entre sí, o strings entre sí
                if ("chara".equals(lt) ^ "chara".equals(rt)) {
                    errors.add(new SemError("semantic", null, null, b.line,
                            "Comparación de igualdad entre tipos incompatibles"));
                    return "nummy";
                }
                return "nummy";
            }

            // Lógicos
            if (op.equals("&&") || op.equals("||")) {
                if (!isNumeric(lt) || !isNumeric(rt)) {
                    errors.add(new SemError("semantic", null, null, b.line,
                            "Operación lógica en tipos no numéricos"));
                }
                return "nummy";
            }

            // Por defecto
            return "nummy";
        }

        // fallback
        return "nummy";
    }

    // Los dos de aquí abajo son booleanos para checar tipos
    // Reglas de asignabilidad
    private boolean isAssignable(String toType, String fromType) {
        if (fromType == null) return true;
        if (toType == null) return true;
        if (toType.equals(fromType)) return true;
        // permitir nummy (int) -> numpt (float)
        if (toType.equals("numpt") && fromType.equals("nummy")) return true;
        // no permitir numpt -> nummy
        return false;
    }

    // Es tipo numérico (nummy o numpt)
    private boolean isNumeric(String t) {
        return "nummy".equals(t) || "numpt".equals(t);
    }

    // Provisional (los tres de aquí abajo son solo para exportar CSVs)
    // Exporta CSV símbolos
    private void exportSymbolsCSV(String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Nombre,Tipo,Valor,Linea_decl");
            for (Symbol s : symbols.values()) {
                pw.println(csvEscape(s.name) + "," + csvEscape(s.type) + "," + csvEscape(s.value) + "," + s.lineDecl);
            }
        }
    }

    // Exporta CSV errores
    private void exportErrorsCSV(String path, List<SemError> errs) throws IOException {
    try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
        pw.println("ID,Fase,Token,Lexema,Linea,Descripcion");
        for (SemError e : errs) {
            pw.println(csvEscape(e.id) + "," 
                    + csvEscape(e.phase) + "," 
                    + csvEscape(e.token) + "," 
                    + csvEscape(e.lexeme) + "," 
                    + e.line + "," 
                    + csvEscape(e.description));
        }
    }
}


    // Escapa una cadena para CSV (comillas dobles y comillas internas)
    private static String csvEscape(String s) {
        if (s == null) return "";
        // envolver entre comillas y duplicar comillas internas
        String escaped = s.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    public static void main(String[] args) {
        String source = ""
            + "nummy papuvar_abc_Def_1 = (10 * 7);\n"
            + "numpt papuvar#X_ = 2.5;\n"
            + "chara papuvar_hey# = \"hola\";\n"
            + "papuvar_abc_Def_1 = papuvar_abc_Def_1-1.5;\n"
            + "while (papuvar_abc_Def_1 <= 20 && papuvar_abc_Def_1 > 0 ) {\n"
            + "    papuvar_abc_Def_1 = papuvar_abc_Def_1 + 1;\n"
            + "}\n"
            + "if (papuvar#X_ >= 2.5) { papuvar_hey# = papuvar_hey#; } else { papuvar_hey# = \"otro\"; }\n";

        // Hacemos lo típico
        Lex.LexResult lr = Lex.lex(source);
        Parser p = new Parser(lr.tokens);
        Parser.Program prog = p.parseProgram();

        System.out.println(prog);// Con esto podemos ver como es que funciona y se asigna todo lo que pasa en el parser, quedando como el árbol que acabamos de crear

        // Analizar e exportar, el export lo podemos quitar
        analyzeAndExport(prog, lr.errors, p.errors, "symbol_table.csv", "error_table.csv");
    }
}
