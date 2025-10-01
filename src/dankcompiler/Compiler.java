package dankcompiler;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("src/dankcompiler/HolaMundo.dank");
        lexer.focusFileOutput("lexout.dankc", "src/dankcompiler/temp");
        lexer.read();
        lexer.getWriter().println("HolaMundo");
        lexer.getWriter().println("Esto es loookal");
        lexer.getWriter().close();
    }
}
