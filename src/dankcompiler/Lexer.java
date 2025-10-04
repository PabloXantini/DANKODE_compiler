package dankcompiler;

import java.util.Set;

import dankcompiler.tokens.Regex;
import dankcompiler.tokens.TokenTable;
import dankcompiler.tokens.TokenType;

public class Lexer extends FileHandler{
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
    //Method stuff
    private void checkMatch(Regex regex, int next, boolean write){
        String lexem = regex.getMatch();
        cursor=next;
        column+=lexem.length();
        //DEBUG ONLY
        System.out.print(lexem);
        if(write){
            //WRITE ON
        }
    }
    public Lexer(String filepath) {
        super(filepath);
        //Setup the token table
        TokenReference = new TokenTable();
    }
    @Override
    public void doPerReadedLine(String currentLine) {
        int tmp;
        cursor=0;
        column=0;
        line++;
        //If not comment block closed, skip line
        if((tmp=B_COMMENT_END.match(currentLine, cursor))!=-1){
            c_block_closed=true;
            System.out.println(B_COMMENT_END.getMatch());
            return;
        }else if(!c_block_closed){
            System.out.println(currentLine);
            return;
        }
        //Process inside a line
        while (cursor<currentLine.length()) {
            //COMMENTS
            if((tmp=INLINE_COMMENT.match(currentLine, cursor))!=-1){
                checkMatch(INLINE_COMMENT, tmp, false);
                continue;
            }else if((tmp=INLINE_B_COMMENT.match(currentLine, cursor))!=-1){
                checkMatch(INLINE_B_COMMENT, tmp, false);
                continue;    
            }else if((tmp=B_COMMENT_START.match(currentLine, cursor))!=-1){
                checkMatch(B_COMMENT_START, tmp, false);
                c_block_lstart=line;
                c_block_cstart=column;
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
                    checkMatch(regex, tmp, true);
                    lexical_correct=true;
                    break;
                }
            }
            if(!lexical_correct){
                //Error
                cursor++;
            }
        }
        System.out.print("\n");
    }
}
