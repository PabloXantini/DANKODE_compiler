package compilador;

import java.util.*;
import java.util.regex.*;

//No hay que moverle a este archivo, pero es importante entenderlo

public class Lex {

    public static class Token {
        public final String type;
        public final String lexeme;
        public final int line;
        public final int col;

        public Token(String type, String lexeme, int line, int col) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
            this.col = col;
        }

        @Override
        public String toString() {
            return String.format("Token(%s, \"%s\", line=%d, col=%d)", type, lexeme, line, col);
        }
    }

    public static class ErrorEntry {
        public final String phase = "lexical";
        public final String lexeme;
        public final int line;
        public final int col;
        public final String description;

        public ErrorEntry(String lexeme, int line, int col, String description) {
            this.lexeme = lexeme;
            this.line = line;
            this.col = col;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("Error(%s) at line %d col %d : %s", lexeme, line, col, description);
        }
    }

    private static class TokenSpec {
        final String type;
        final Pattern pattern;
        TokenSpec(String type, String regex) {
            this.type = type;
            // usamos MULTILINE para que \\b funcione adecuadamente en varias líneas, es importante porque siempre iniciaremos con ^, pero tampoco podemos poner $ porque eso descuarajinga todo y el análisis termina antes de tiempo
            this.pattern = Pattern.compile(regex, Pattern.MULTILINE);
        }
    }

    // Todo esto serán nuestras palabras reservadas
    private static final Pattern NEWLINE = Pattern.compile("\\r?\\n");
    private static final Pattern WHITESPACE = Pattern.compile("[ \\t\\r]+");

    private static final List<TokenSpec> TOKEN_SPECS = Arrays.asList(
        new TokenSpec("TYPE",   "(?:nummy|numpt|chara)\\b"),
        new TokenSpec("WHILE",  "while\\b"),
        new TokenSpec("IF",     "if\\b"),
        new TokenSpec("ELSE",   "else\\b"),
        new TokenSpec("BOOLAND","&&"),
        new TokenSpec("BOOLOR", "\\|\\|"),
        new TokenSpec("EQ",     "=="),
        new TokenSpec("NEQ",    "!="),
        new TokenSpec("GTE",    ">="),
        new TokenSpec("LTE",    "<="),
        new TokenSpec("GT",     ">"),
        new TokenSpec("LT",     "<"),
        new TokenSpec("PLUS",   "\\+"),
        new TokenSpec("MINUS",  "-"),
        new TokenSpec("MULT",   "\\*"),
        new TokenSpec("DIV",    "/"),
        new TokenSpec("MOD",    "%"),
        new TokenSpec("ASSIGN", "="),
        new TokenSpec("LPAREN", "\\("),
        new TokenSpec("RPAREN", "\\)"),
        new TokenSpec("LBRACE", "\\{"),
        new TokenSpec("RBRACE", "\\}"),
        new TokenSpec("SEMI",   ";"),
        new TokenSpec("COMMA",  ","),
        // Float antes de Int
        new TokenSpec("FLOAT",  "\\d+\\.\\d+"),
        new TokenSpec("INT",    "\\d+"),
        // String con escapes básicos: \" o \\.
        new TokenSpec("STRING", "\"(\\\\.|[^\"\\\\])*\""),
        // papuvar
        new TokenSpec("ID",     "^papuvar[_#]([A-Za-z]+[_#])+[0-9]*")
    );

    // Y ya estos son los resultados que guardamos en los atributos de esta porquería
    public static class LexResult {
        public final List<Token> tokens;
        public final List<ErrorEntry> errors;
        public LexResult(List<Token> tokens, List<ErrorEntry> errors) {
            this.tokens = tokens;
            this.errors = errors;
        }
    }

    //Pero aquí es donde sucede la magia
    public static LexResult lex(String source) {
        List<Token> tokens = new ArrayList<>();
        List<ErrorEntry> errors = new ArrayList<>();

        int pos = 0;
        int len = source.length();
        int line = 1;
        int lineStart = 0;

        while (pos < len) {
            // Saltar saltos de línea
            Matcher mNewline = NEWLINE.matcher(source);
            mNewline.region(pos, len);
            if (mNewline.lookingAt()) {
                pos = mNewline.end();
                line++;
                lineStart = pos;
                continue;
            }

            // Saltar espacios ( )
            Matcher mSpace = WHITESPACE.matcher(source);
            mSpace.region(pos, len);
            if (mSpace.lookingAt()) {
                pos = mSpace.end();
                continue;
            }

            // Comentarios de java
            if (pos + 1 < len && source.charAt(pos) == '/' && source.charAt(pos + 1) == '/') {
                int nextNl = source.indexOf('\n', pos);
                if (nextNl == -1) {
                    // hasta fin
                    pos = len;
                } else {
                    pos = nextNl + 1;
                    line++;
                    lineStart = pos;
                }
                continue;
            }

            // Con todo lo anterior saltado ahora sí podemos buscar tokens usando matchers
            boolean matched = false;
            for (TokenSpec ts : TOKEN_SPECS) {
                Matcher m = ts.pattern.matcher(source);
                m.region(pos, len);
                if (m.lookingAt()) {
                    String lexeme = m.group();
                    int col = pos - lineStart + 1;
                    tokens.add(new Token(ts.type, lexeme, line, col));

                    // Se actualiza si el lexema contiene saltos de línea (como strings multilínea)
                    int nl = countNewlines(lexeme);
                    if (nl > 0) {
                        line += nl;
                        int lastNlIndex = lexeme.lastIndexOf('\n');
                        lineStart = pos + lastNlIndex + 1;
                    }

                    pos = m.end();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                // carácter inesperado (que porquería es esa)
                char ch = source.charAt(pos);
                int col = pos - lineStart + 1;
                errors.add(new ErrorEntry(String.valueOf(ch), line, col, "Caracter inesperado"));//Se añade el error a la lista
                pos++; // avanzar y seguir
            }
        }

        // token EOF (es decir el final del programa, pedazo de tonoto)
        tokens.add(new Token("EOF", "", line, Math.max(1, (len - lineStart + 1)) ));
        return new LexResult(tokens, errors);
    }

    private static int countNewlines(String s) {
        int c = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '\n') c++;// Referencia
        return c;
    }

    // Main de ejemplo que terminaremos quitando
    public static void main(String[] args) {
        String source = ""
            + "nummy papuvar_miVar_ = 10;\n"
            + "numpt papuvar#otro# = 2.5;\n"
            + "chara papuvar_s#1990 = \"papulandia \\\"hay un papu\\\"\";\n"
            + "papuvar_miVar_ = papuvar_miVar_ + 1;\n"
            + "while (papuvar_miVar_ < 20) {\n"
            + "    papuvar_miVar_ = papuvar_miVar_ + 1;\n"
            + "}\n"
            + "nummy _z = \"pedazo de anormal esto está mal\"; // equisdedededededededededededede\n";

        System.out.println(source);

        LexResult res = lex(source);

        System.out.println("TOKENS:");
        for (Token t : res.tokens) {
            System.out.println("  " + t);
        }
        System.out.println("\nERRORES LEXICOS:");
        for (ErrorEntry e : res.errors) {
            System.out.println("  " + e);
        }
    }
}
