package dankcompiler;

import java.util.Set;

import dankcompiler.tokens.Regex;
import dankcompiler.tokens.TokenTable;
import dankcompiler.tokens.TokenType;

public class Lexer extends FileHandler{
    //Cursor
    private int cursor = 0;
    private int column = 0;
    private int line = 1;
    //Comments
    private int c_block_lstart=0;
    //Flags
    private boolean c_block_closed=true;
    //REGULAR EXPRESIONS
    private final Regex SPACE = new Regex("[ \\t\\r]+");
    private final Regex INLINE_COMMENT = new Regex("//.*");
    private final Regex INLINE_B_COMMENT = new Regex("/\\*.*\\*/");
    private final Regex B_COMMENT_START = new Regex("/\\*.*");
    private final Regex B_COMMENT_END = new Regex(".*\\*/");
    //TOKEN TABLE
    private final TokenTable TokenReference;
    public Lexer(String filepath) {
        super(filepath);
        //Setup the token table
        TokenReference = new TokenTable();
    }
    @Override
    public void doPerReadedLine(String currentLine) {
        cursor=0;
        int tmp;
        //If not comment block closed, skip line
        if((tmp=B_COMMENT_END.match(currentLine, cursor))!=-1){
            c_block_closed=true;
            System.out.println(B_COMMENT_END.getMatch());
        }else if(!c_block_closed){
            System.out.print(currentLine);
            return;
        }
        while (cursor<currentLine.length()) {
            //COMMENTS
            if((tmp=INLINE_COMMENT.match(currentLine, cursor))!=-1){
                cursor=tmp;
                System.out.print(INLINE_COMMENT.getMatch());
                continue;
            }else if((tmp=INLINE_B_COMMENT.match(currentLine, cursor))!=-1){
                cursor=tmp;
                continue;    
            }else if((tmp=B_COMMENT_START.match(currentLine, cursor))!=-1){
                cursor=tmp;
                c_block_lstart=line;
                c_block_closed=false;
                System.out.print(B_COMMENT_START.getMatch());
                continue;
            }
            //SPACES
            if((tmp=SPACE.match(currentLine, cursor))!=-1){
                cursor=tmp;
                System.out.print(SPACE.getMatch());
                continue;
            }
            //NOW THE TOKENS
            Set<TokenType> tokenSet = TokenReference.get().keySet();
            /*
            for(TokenType token : tokenSet){
                Regex regex = TokenReference.getRegex(token);
            }
            */
            cursor++;
        }
        line++;
    }
}
