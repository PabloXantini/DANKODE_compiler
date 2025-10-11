package dankcompiler.parsing;

import java.util.ArrayList;
import java.util.Set;

import dankcompiler.parsing.errors.CompileErrorHandler;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.errors.TokenErrorCode;
import dankcompiler.parsing.errors.TokenErrorType;
import dankcompiler.parsing.tokens.Regex;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenTable;
import dankcompiler.parsing.tokens.TokenType;

public class Lexer{
    //Cursor
    private int cursor = 0;
    private int column = 0;
    private int line = 0;
    //Comments
    private int c_block_lstart = 0;
    private int c_block_cstart = 0;
    //Flags
    private boolean c_block_closed = true;
    private boolean lexical_correct = true;
    //REGULAR EXPRESIONS
    private final Regex SPACE = new Regex("[ \\t\\r]+");
    private final Regex INLINE_COMMENT = new Regex("//.*");
    private final Regex INLINE_B_COMMENT = new Regex("/\\*.*\\*/");
    private final Regex B_COMMENT_START = new Regex("/\\*.*");
    private final Regex B_COMMENT_END = new Regex(".*\\*/");
    //TOKEN TABLE
    private final TokenTable TokenReference;
    //TOKEN STREAM
    private final ArrayList<Token> TokenStream;
    //CURRENT ERRORS
    private final ArrayList<TokenError> ErrorStream;
    //Method stuff
    private String checkMatch(Regex regex, int next, boolean write){
        String lexem = regex.getMatch();
        cursor=next;
        column+=lexem.length();
        //DEBUG ONLY
        System.out.print(lexem);
        if(write){
            //WRITE ON
            //this.getWriter().print(lexem);
        }
        return lexem;
    }
    private void generateToken(String lexem, TokenType type, int line, int column){
        Token token = new Token(lexem, type, TokenReference.getCategorie(type), line, column);
        TokenStream.add(token);
    }
    private void throwError(String lexem, int line, int column, TokenErrorCode code){
        TokenError error = CompileErrorHandler.generateError(lexem, TokenErrorType.LEXICAL, line, column, code);
        ErrorStream.add(error);
    }
    public Lexer() {
        //Setup the token table
        TokenReference = new TokenTable();
        TokenStream = new ArrayList<Token>();
        ErrorStream = new ArrayList<TokenError>();
    }
    public ArrayList<TokenError> getCurrentErrors(){
        return ErrorStream;
    }
    public ArrayList<Token> generateTokenStream(String currentLine){
        int tmp;
        line++;
        column=1;
        cursor=0;
        TokenStream.clear();
        ErrorStream.clear();
        //Process inside a line
        while (cursor<currentLine.length()) {
            //If not comment block closed, skip line
            if((tmp=B_COMMENT_END.match(currentLine, cursor))!=-1){
                c_block_closed=true;
                System.out.println(B_COMMENT_END.getMatch());
                return TokenStream;
            }else if(!c_block_closed){
                System.out.println(currentLine);
                return TokenStream;
            }
            //COMMENTS
            if((tmp=INLINE_COMMENT.match(currentLine, cursor))!=-1){
                checkMatch(INLINE_COMMENT, tmp, false);
                continue;
            }else if((tmp=INLINE_B_COMMENT.match(currentLine, cursor))!=-1){
                checkMatch(INLINE_B_COMMENT, tmp, false);
                continue;    
            }else if((tmp=B_COMMENT_START.match(currentLine, cursor))!=-1){
                c_block_lstart=line;
                c_block_cstart=column;
                checkMatch(B_COMMENT_START, tmp, false);
                c_block_closed=false;
                continue;
            }
            //SPACES
            if((tmp=SPACE.match(currentLine, cursor))!=-1){
                checkMatch(SPACE, tmp, false);
                continue;
            }
            //NOW THE TOKENS (THIS WILL BE WRITE ON OUTPUT)
            lexical_correct=false;
            Set<TokenType> tokenSet = TokenReference.get().keySet();
            for(TokenType token : tokenSet){
                Regex regex = TokenReference.getRegex(token);
                if((tmp=regex.match(currentLine, cursor))!=-1){
                    String lexem = checkMatch(regex, tmp, true);
                    generateToken(lexem, token, this.line, this.column);
                    lexical_correct=true;
                    break;
                }
            }
            if(!lexical_correct){
                char unknown_lexem = currentLine.charAt(cursor);
                String lexem = String.valueOf(unknown_lexem);
                //Throw error
                throwError(lexem, this.line, this.column, TokenErrorCode.LEXEM_UNKNOWN);
                column++;
                cursor++;
            }      
        }
        //SOLO DEBUG
        System.out.print("\n");
        return TokenStream;
    }
    public Token generateEndToken(){
        TokenStream.clear();
        ErrorStream.clear();
        generateToken(null, TokenType.EOF, line, column);
        if(!c_block_closed){
            throwError("*/", c_block_lstart, c_block_cstart, TokenErrorCode.BLOCK_COMMENT_NOT_CLOSED);
        }
        return TokenStream.get(0);
    }
}
