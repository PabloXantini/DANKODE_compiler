package dankcompiler;

import java.util.ArrayList;

import dankcompiler.errors.TokenError;
import dankcompiler.errors.TokenErrorHandler;
import dankcompiler.tokens.Token;

public class Compiler extends FileHandler{
    private TokenErrorHandler ErrorHandler;
    private Lexer lexer;
    private final ArrayList<TokenError> ErrorTable;
    public Compiler(String filepath){
        super(filepath);
        ErrorHandler = new TokenErrorHandler();
        lexer = new Lexer();
        ErrorTable = new ArrayList<TokenError>();
    }
    public static void main(String[] args) {
        Compiler dank = new Compiler("src/dankcompiler/HolaMundo.dank");
        dank.focusFileOutput("lexout.dankc", "src/dankcompiler/temp");
        dank.read();
        dank.showErrors();
    }
    public ArrayList<TokenError> getAllErrors(){
        return ErrorTable;
    }
    public void showErrors(){
        for(TokenError error : ErrorTable){
            String message = ErrorHandler.generateMessage(error);
            String errTypeInfo = ErrorHandler.verboseTypeError(error);
            System.out.println(
                "Codigo: "+error.code+
                ", Fase: "+errTypeInfo+
                ", Mensaje: "+message+
                ", Linea: "+error.line+
                ", Columna: "+error.column);
        }
    }
    @Override
    public void doPerReadedLine(String currentLine) {
        //TASK OF LEXER
        ArrayList<Token> tokens = lexer.generateTokenStream(currentLine);
        ArrayList<TokenError> errors = lexer.getCurrentErrors();
        for(TokenError error : errors){
            ErrorTable.add(error);
        }
        //TASK FOR PRINT OUTPUT ON LEXER
        for(Token token : tokens){
            this.getWriter().print(token.getSymbol());
        }
        if(tokens.size()>0){
            this.getWriter().println();
        }
        //TASK FOR PARSER
    }
    @Override
    public void doAtReadFinish() {
        this.getWriter().close();
    }
}
