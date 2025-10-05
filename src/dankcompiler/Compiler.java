package dankcompiler;

import java.util.ArrayList;

import dankcompiler.tokens.Token;

public class Compiler extends FileHandler{
    private Lexer lexer;

    public Compiler(String filepath){
        super(filepath);
        lexer = new Lexer();
    }
    public static void main(String[] args) {
        Compiler dank = new Compiler("src/dankcompiler/HolaMundo.dank");
        dank.focusFileOutput("lexout.dankc", "src/dankcompiler/temp");
        dank.read();
    }

    @Override
    public void doPerReadedLine(String currentLine) {
        ArrayList<Token> tokens = lexer.generateTokenStream(currentLine);
        for(Token token : tokens){
            this.getWriter().print(token.getSymbol());
        }
        if(tokens.size()>0){
            this.getWriter().println();
        }
    }
    @Override
    public void doAtReadFinish() {
        this.getWriter().close();
    }
}
