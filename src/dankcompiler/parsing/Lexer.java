package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import dankcompiler.parsing.errors.CompileErrorHandler;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.errors.TokenErrorCode;
import dankcompiler.parsing.errors.TokenErrorType;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.tokens.Regex;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenTable;
import dankcompiler.parsing.tokens.TokenType;

public class Lexer{
    //Constants
    private static final int TAB_COLUMNS = 1;
    //Comments
    private int c_block_lstart = 0;
    private int c_block_cstart = 0;
    //Flags
    private boolean c_block_closed = true;
    //REGULAR EXPRESIONS
    private final Regex SPACE = new Regex("[ \\r]+");
    private final Regex TAB = new Regex("[\\t]+");
    private final Regex INLINE_COMMENT = new Regex("//.*");
    private final Regex INLINE_B_COMMENT = new Regex("/\\*([^*]\\*+[^/])*\\*+/");
    private final Regex B_COMMENT_START = new Regex("/\\*([^*]\\\\*+[^/])*");
    private final Regex B_COMMENT_END = new Regex("([^*]\\\\*+[^/])*\\*/");
    //TOKEN TABLE
    private final TokenTable TokenReference;
    //CURRENT ERRORS
    private final ArrayList<TokenError> ErrorStream;
    private TokenError currentError;
    //Cursor Used
	private Cursor cursorReference = null;
    //Method Stuff
    private Token generateToken(String lexem, TokenType type, int line, int column){
        Token token = new Token(lexem, type, TokenReference.getCategorie(type), line, column);
        //TokenStream.add(token);
        return token;
    }
    private TokenError throwError(String lexem, int line, int column, TokenErrorCode code, String... args){
        TokenError error = CompileErrorHandler.generateError(lexem, TokenErrorType.LEXICAL, line, column, code, args);
        ErrorStream.add(error);
        return error;
    }
    public Lexer(Cursor cursor) {
        //Setup the token table
        TokenReference = new TokenTable();
        //TokenStream = new ArrayList<Token>();
        ErrorStream = new ArrayList<TokenError>();
        cursorReference = cursor;
    }
    public ArrayList<TokenError> getErrors(){
    	return this.ErrorStream;
    }
    public TokenError getError(){
        return this.currentError;
    }
    private void advance(Regex regex, Cursor cursor){
        advance(regex, cursor, 1);
    }
    private void advance(Regex regex, Cursor cursor, int colWeight){
        int lex_column_size = regex.getMatch().length();
        cursor.advance(regex.getEnd(), lex_column_size*colWeight);
    }
    public Token generateNextToken() throws IOException{
        Token token = null;
        ErrorStream.clear();
        while(token==null){
            if(cursorReference.isInLine()){
                token = tryGenerateToken(cursorReference);
            }else{
                cursorReference.advanceNewLine();
                cursorReference.writeln();
                if(cursorReference.getLineContent()==null) {
                	token = generateEndToken(cursorReference);
                }
            }
        }
        cursorReference.write(token.getSymbol());
        return token;
    }
    public Token tryGenerateToken(Cursor cursor){
        this.currentError = null;
        String lcontent = cursor.getLineContent();
        if(B_COMMENT_END.match(lcontent, cursor.getValue())!=-1){
            c_block_closed=true;
            cursor.pass();
            return null;
        }else if(!c_block_closed){
            cursor.pass();
            return null;
        }
        //COMMENTS
        if(INLINE_COMMENT.match(lcontent, cursor.getValue())!=-1){
            advance(INLINE_COMMENT, cursor);
            return null;
        }else if(INLINE_B_COMMENT.match(lcontent, cursor.getValue())!=-1){
            advance(INLINE_B_COMMENT, cursor);
            return null;    
        }else if(B_COMMENT_START.match(lcontent, cursor.getValue())!=-1){
            c_block_lstart=cursor.getLine();
            c_block_cstart=cursor.getColumn();
            c_block_closed=false;
            advance(B_COMMENT_START, cursor);
            return null;
        }
        //SPACES
        if(SPACE.match(lcontent, cursor.getValue())!=-1){
            advance(SPACE, cursor);
            return null;
        }else if(TAB.match(lcontent, cursor.getValue())!=-1){
            advance(TAB, cursor, TAB_COLUMNS);
            return null;
        }
        //NOW THE TOKENS
        Token token = matchToken(cursor);
        if(token!=null) return token;

        //HANDLE UNKNOWN TOKENS
        handleUnknownToken(cursor);
        return null;
    }
    private Token matchToken(Cursor cursor){
        String lcontent = cursor.getLineContent();
        Set<TokenType> tokenSet = TokenReference.get().keySet();
        for(TokenType token : tokenSet){
            Regex regex = TokenReference.getRegex(token);
            if(regex.match(lcontent, cursor.getValue())!=-1){
                advance(regex, cursor);
                String lexem = regex.getMatch();
                //System.out.print(lexem);
                return generateToken(lexem, token, cursor.getLine(), cursor.getColumn());
            }
        }
        return null;
    }
    private void handleUnknownToken(Cursor cursor){
        String lcontent = cursor.getLineContent();
        char unknown_lexem = lcontent.charAt(cursor.getValue());
        String lexem = String.valueOf(unknown_lexem);
        //Throw error
        currentError = throwError(
        		lexem, 
        		cursor.getLine(), 
        		cursor.getColumn(), 
        		TokenErrorCode.LEXEM_UNKNOWN, 
        		lexem
        		);
        cursor.next();
    }
    public Token generateEndToken(Cursor cursor){
        //TokenStream.clear();
        //ErrorStream.clear();
        Token final_token = generateToken(null, TokenType.EOF, cursor.getLine(), cursor.getColumn());
        if(!c_block_closed){
            currentError = throwError(
            		null, 
            		c_block_lstart, 
            		c_block_cstart, 
            		TokenErrorCode.MISMATCH, 
            		"*/");
        }
        return final_token;
    }
}
