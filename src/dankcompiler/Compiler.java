package dankcompiler;

import java.util.ArrayList;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.parsing.Lexer;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.tokens.Token;

public class Compiler extends FileHandler{
    private CompileMsgHandler MsgHandler;
    private Lexer lexer;
    private final ArrayList<TokenError> ErrorTable;
    public Compiler(String filepath){
        super(filepath);
        MsgHandler = new CompileMsgHandler();
        lexer = new Lexer();
        ErrorTable = new ArrayList<TokenError>();
    }
    public static void main(String[] args) {
        Compiler dank = new Compiler("src/dankcompiler/HolaMundo.dank");
        dank.focusFileOutput("out.dankc", "src/dankcompiler/temp");
        dank.read();
        dank.showErrors();
    }
    public ArrayList<TokenError> getAllErrors(){
        return ErrorTable;
    }
    public boolean thereErrors(){
        if(ErrorTable.size()==0) return false;
        return true;
    }
    public void showErrors(){
        if(thereErrors()){
            System.out.println(MsgHandler.generateMessage(MessageType.ERRORS_NOT_FOUND_MESSAGE));
            return;
        }
        System.out.println(MsgHandler.generateMessage(MessageType.ERRORS_FOUND_MESSAGE));
        for(TokenError error : ErrorTable){
            String message = MsgHandler.generateErrorMessage(error);
            String errTypeInfo = MsgHandler.verboseTypeError(error);
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
            //TASK FOR PARSER
            
        }
        if(tokens.size()>0){
            this.getWriter().println();
        }
    }
    @Override
    public void doAtReadFinish() {
        Token EOF = lexer.generateEndToken();
        //TASK FOR PARSER

        this.getWriter().close();
    }
}
