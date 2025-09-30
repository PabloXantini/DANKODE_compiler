package compilador;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final List<Lex.Token> tokens;
    private int pos = 0;
    public final List<SyntaxError> errors = new ArrayList<>();

    public Parser(List<Lex.Token> tokens) {
        this.tokens = tokens;
    }

    //Estas 4 porquerías son las que se encargan de etiquetar el caracter/palabra actual para ver que se hace con eso
    private Lex.Token peek() {
        if (pos < tokens.size()) return tokens.get(pos);
        return tokens.get(tokens.size() - 1); // EOF token
    }

    private Lex.Token advance() {
        Lex.Token t = peek();
        if (pos < tokens.size()) pos++;
        return t;
    }

    private boolean accept(String... types) {
        String t = peek().type;
        for (String ty : types) if (ty.equals(t)) { advance(); return true; }
        return false;
    }

    private Lex.Token expect(String type) {
        if (peek().type.equals(type)) return advance();
        Lex.Token t = peek();
        errors.add(new SyntaxError(t.type, t.lexeme, t.line, "Se esperaba token: " + type));
        // para seguir analizando, crear un token ficticio con el tipo esperado (no avanzamos)
        return new Lex.Token("ERROR", "", t.line, t.col);
    }

    //Aquí inicia todo
    public Program parseProgram() {
        List<Stmt> stmts = new ArrayList<>();
        while (!peek().type.equals("EOF")) {
            Stmt s = parseStatement();
            if (s != null) stmts.add(s);
            else {
                synchronize();
            }
        }
        return new Program(stmts);
    }

    private Stmt parseStatement() {
        Lex.Token t = peek();
        switch (t.type) {
            case "TYPE":
                return parseVarDecl();
            case "ID":
                
                return parseAssignment();
            case "WHILE":
                return parseWhile();
            case "IF":
                return parseIf();
            case "SEMI":
                
                advance();
                return null;
            default:
                errors.add(new SyntaxError(t.type, t.lexeme, t.line, "Instrucción inesperada"));
                
                synchronize();
                return null;
        }
    }

    private Stmt parseVarDecl() {
        Lex.Token typeTok = expect("TYPE");
        Lex.Token idTok = expect("ID");
        Expr init = null;
        if (accept("ASSIGN")) {
            init = parseExpression();
        }
        expect("SEMI");
        return new VarDecl(typeTok.lexeme, idTok.lexeme, init, typeTok.line);
    }

    private Stmt parseAssignment() {
        Lex.Token idTok = expect("ID");
        expect("ASSIGN");
        Expr expr = parseExpression();
        expect("SEMI");
        return new Assignment(idTok.lexeme, expr, idTok.line);
    }

    private Stmt parseWhile() {
        Lex.Token whileTok = expect("WHILE");
        expect("LPAREN");
        Expr cond = parseExpression();
        expect("RPAREN");
        expect("LBRACE");
        List<Stmt> body = new ArrayList<>();
        while (!peek().type.equals("RBRACE") && !peek().type.equals("EOF")) {
            Stmt s = parseStatement();
            if (s != null) body.add(s);
            else synchronize();
        }
        expect("RBRACE");
        return new WhileStmt(cond, body, whileTok.line);
    }

    // Por los momos, lo terminaremos quitando
    private Stmt parseIf() {
        Lex.Token ifTok = expect("IF");
        expect("LPAREN");
        Expr cond = parseExpression();
        expect("RPAREN");
        expect("LBRACE");
        List<Stmt> thenBody = new ArrayList<>();
        while (!peek().type.equals("RBRACE") && !peek().type.equals("EOF")) {
            Stmt s = parseStatement();
            if (s != null) thenBody.add(s);
            else synchronize();
        }
        expect("RBRACE");
        List<Stmt> elseBody = null;
        if (accept("ELSE")) {
            expect("LBRACE");
            elseBody = new ArrayList<>();
            while (!peek().type.equals("RBRACE") && !peek().type.equals("EOF")) {
                Stmt s = parseStatement();
                if (s != null) elseBody.add(s);
                else synchronize();
            }
            expect("RBRACE");
        }
        return new IfStmt(cond, thenBody, elseBody, ifTok.line);
    }

    //Estas son las expresiones reservadas
    //||
    private Expr parseExpression() { return parseOr(); }

    private Expr parseOr() {
        Expr left = parseAnd();
        while (accept("BOOLOR")) {
            Lex.Token op = tokens.get(pos - 1);
            Expr right = parseAnd();
            left = new BinaryOp("||", left, right, op.line);
        }
        return left;
    }

    //&&
    private Expr parseAnd() {
        Expr left = parseEquality();
        while (accept("BOOLAND")) {
            Lex.Token op = tokens.get(pos - 1);
            Expr right = parseEquality();
            left = new BinaryOp("&&", left, right, op.line);
        }
        return left;
    }

    //== !=
    private Expr parseEquality() {
        Expr left = parseRelational();
        while (true) {
            if (accept("EQ")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseRelational();
                left = new BinaryOp("==", left, right, op.line);
            } else if (accept("NEQ")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseRelational();
                left = new BinaryOp("!=", left, right, op.line);
            } else break;
        }
        return left;
    }

    //>, <, >=, <=
    private Expr parseRelational() {
        Expr left = parseAdditive();
        while (true) {
            if (accept("GT")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseAdditive();
                left = new BinaryOp(">", left, right, op.line);
            } else if (accept("LT")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseAdditive();
                left = new BinaryOp("<", left, right, op.line);
            } else if (accept("GTE")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseAdditive();
                left = new BinaryOp(">=", left, right, op.line);
            } else if (accept("LTE")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseAdditive();
                left = new BinaryOp("<=", left, right, op.line);
            } else break;
        }
        return left;
    }

    //+ -
    private Expr parseAdditive() {
        Expr left = parseMultiplicative();
        while (true) {
            if (accept("PLUS")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseMultiplicative();
                left = new BinaryOp("+", left, right, op.line);
            } else if (accept("MINUS")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseMultiplicative();
                left = new BinaryOp("-", left, right, op.line);
            } else break;
        }
        return left;
    }

    //* / %
    private Expr parseMultiplicative() {
        Expr left = parseUnary();
        while (true) {
            if (accept("MULT")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseUnary();
                left = new BinaryOp("*", left, right, op.line);
            } else if (accept("DIV")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseUnary();
                left = new BinaryOp("/", left, right, op.line);
            } else if (accept("MOD")) {
                Lex.Token op = tokens.get(pos - 1);
                Expr right = parseUnary();
                left = new BinaryOp("%", left, right, op.line);
            } else break;
        }
        return left;
    }

    //-
    private Expr parseUnary() {
        if (accept("MINUS")) {
            Lex.Token op = tokens.get(pos - 1);
            Expr operand = parseUnary();
            return new UnaryOp("-", operand, op.line);
        }
        return parsePrimary();
    }

    // Literales, variables, paréntesis
    private Expr parsePrimary() {
        Lex.Token t = peek();
        // Nuestras literales cumtosas
        if (accept("INT")) {
            return new Literal(Integer.parseInt(tokens.get(pos - 1).lexeme), "nummy");
        }
        if (accept("FLOAT")) {
            return new Literal(Double.parseDouble(tokens.get(pos - 1).lexeme), "numpt");
        }
        if (accept("STRING")) {
            // removemos las comillas
            String raw = tokens.get(pos - 1).lexeme;
            if (raw.length() >= 2) raw = raw.substring(1, raw.length() - 1);
            return new Literal(raw, "chara");
        }
        // Variables
        if (accept("ID")) {
            return new VarRef(tokens.get(pos - 1).lexeme, tokens.get(pos - 1).line);
        }

        // Paréntesis
        if (accept("LPAREN")) {
            Expr e = parseExpression();
            expect("RPAREN");
            return e;
        }
        
        // Error, avanzar y retornar un literal nummy 0 para no romper todo. Por ahora está ambiguo y solo se retorna un 0
        errors.add(new SyntaxError(t.type, t.lexeme, t.line, "Expresión inesperada"));
        advance();
        return new Literal(0, "nummy");
    }

    // Para cuando hay un horror, avanzar hasta el siguiente ; o }
    private void synchronize() {
        while (!peek().type.equals("EOF")) {
            if (peek().type.equals("SEMI") || peek().type.equals("RBRACE")) {
                advance();
                return;
            }
            advance();
        }
    }

    // Esto ya es el árbol, no tocar
    public static abstract class Node { public final int line; public Node(int line) { this.line = line; } }
    public static class Program extends Node {
        public final List<Stmt> statements;
        public Program(List<Stmt> statements) { super(statements.isEmpty()?0:statements.get(0).line); this.statements = statements; }
        @Override public String toString() {
            return statements.stream().map(Object::toString).collect(Collectors.joining("\n"));
        }
    }

    public static abstract class Stmt extends Node { public Stmt(int line) { super(line); } }
    public static class VarDecl extends Stmt {
        public final String typeName;
        public final String name;
        public final Expr init;
        public VarDecl(String typeName, String name, Expr init, int line) { super(line); this.typeName = typeName; this.name = name; this.init = init; }
        public String toString(){ return String.format("VarDecl(%s %s = %s) @%d", typeName, name, init==null?"<null>":init, line); }
    }
    public static class Assignment extends Stmt {
        public final String name; public final Expr expr;
        public Assignment(String name, Expr expr, int line) { super(line); this.name = name; this.expr = expr; }
        public String toString(){ return String.format("Assign(%s = %s) @%d", name, expr, line); }
    }
    public static class WhileStmt extends Stmt {
        public final Expr cond; public final List<Stmt> body;
        public WhileStmt(Expr cond, List<Stmt> body, int line) { super(line); this.cond = cond; this.body = body; }
        public String toString(){ return String.format("While(%s) { %s } @%d", cond, body.stream().map(Object::toString).collect(Collectors.joining("; ")), line); }
    }
    public static class IfStmt extends Stmt {
        public final Expr cond; public final List<Stmt> thenBody; public final List<Stmt> elseBody;
        public IfStmt(Expr cond, List<Stmt> thenBody, List<Stmt> elseBody, int line) { super(line); this.cond = cond; this.thenBody = thenBody; this.elseBody = elseBody; }
        public String toString(){
            return String.format("If(%s) { %s } else { %s } @%d", cond,
                thenBody.stream().map(Object::toString).collect(Collectors.joining("; ")),
                elseBody==null?"":elseBody.stream().map(Object::toString).collect(Collectors.joining("; ")),
                line);
        }
    }

    public static abstract class Expr extends Node { public Expr(int line) { super(line); } }
    public static class BinaryOp extends Expr {
        public final String op; public final Expr left; public final Expr right;
        public BinaryOp(String op, Expr left, Expr right, int line) { super(line); this.op = op; this.left = left; this.right = right; }
        public String toString(){ return String.format("(%s %s %s)", left, op, right); }
    }
    public static class UnaryOp extends Expr {
        public final String op; public final Expr operand;
        public UnaryOp(String op, Expr operand, int line) { super(line); this.op = op; this.operand = operand; }
        public String toString(){ return String.format("(%s%s)", op, operand); }
    }
    public static class Literal extends Expr {
        public final Object value; public final String typeName;
        public Literal(Object value, String typeName) { super(0); this.value = value; this.typeName = typeName; }
        public String toString(){ return String.format("Literal(%s:%s)", value, typeName); }
    }
    public static class VarRef extends Expr {
        public final String name; public VarRef(String name, int line) { super(line); this.name = name; }
        public String toString(){ return String.format("VarRef(%s)", name); }
    }

    public static class SyntaxError {
        public final String id;
        public final String token; 
        public final String lexeme; 
        public final int line; 
        public final String description;

        public SyntaxError(String token, String lexeme, int line, String description) {
            this.id = Semantico.nextErrorToken();
            this.token = token; 
            this.lexeme = lexeme; 
            this.line = line; 
            this.description = description;
        }
        @Override public String toString() { 
            return String.format("%s [syntax] (%s:%s) @%d -> %s", id, token == null ? "-" : token, lexeme == null ? "-" : lexeme, line, description);
        }
    }

    public static void main(String[] args) {
        String source = ""
            + "nummy papuvar_abc_Def_1 = (10 * 7);\n"
            + "numpt papuvar#X_ = \"2.5\";\n"
            + "chara papuvar_hey# = \"hola\";\n"
            + "papuvar_abc_Def_1 = papuvar_abc_Def_1 + 1;\n"
            + "while (papuvar_abc_Def_1 <= 20 && papuvar_abc_Def_1 > 0 ) {\n"
            + "    papuvar_abc_Def_1 = papuvar_abc_Def_1 + 1;\n"
            + "}\n"
            + "if (papuvar#X_ >= 2.5) { papuvar_hey# = papuvar_hey#; } else { papuvar_hey# = \"otro\"; }\n";

        Lex.LexResult lr = Lex.lex(source);// Analizador léxico
        Parser p = new Parser(lr.tokens); // Parser, lo único que hacemos es guardar las tokens en el atributo tokens de este archivo
        Program prog = p.parseProgram(); // Ahora sí creamos el árbol

        System.out.println("AST:");
        System.out.println(prog);

        System.out.println("\nErrores léxicos:");
        for (Lex.ErrorEntry e : lr.errors) System.out.println("  " + e);

        System.out.println("\nErrores sintácticos:");
        for (SyntaxError se : p.errors) System.out.println("  " + se);
    }
}
