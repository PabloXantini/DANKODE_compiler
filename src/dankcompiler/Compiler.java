package dankcompiler;

import java.util.ArrayList;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.parsing.Lexer;
import dankcompiler.parsing.Parser;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;
import dankcompiler.parsing.tokens.Token;

public class Compiler extends FileHandler{
    private CompileMsgHandler MsgHandler;
    private Lexer lexer;
    private Parser parser;
    private final ArrayList<TokenError> ErrorTable;
    public Compiler(String filepath){
        super(filepath);
        MsgHandler = new CompileMsgHandler();
        lexer = new Lexer();
        parser = new Parser();
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
        if(!thereErrors()){
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
    public void doPerReadedLine(Cursor cursor) {
        int token_count=0;
        Token token = null;
        TokenError error = null;
        while(cursor.isInLine()){
            token = lexer.tryGenerateToken(cursor);
            error = lexer.getError();
            if(token!=null){
                this.getWriter().print(token.getSymbol());
                token_count++;
            }
            if(error!=null){
                ErrorTable.add(error);
            }
        }

        if(token_count>0){
            this.getWriter().println();
            this.getWriter().flush();
        }
        this.getWriter().flush();
    }
    @Override
    public void doAtReadFinish(Cursor cursor) {
        lexer.generateEndToken(cursor);
        TokenError error = lexer.getError();
        if(error!=null){
            ErrorTable.add(error);
        }
        this.getWriter().close();
    }
}
