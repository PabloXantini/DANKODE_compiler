package dankcompiler;

import dankcompiler.tokens.Regex;

public class Lexer extends FileHandler{
    //Variables
    private int cursor = 0;
    private int column = 0;
    private int line = 0;
    //Flags
    private boolean c_block_closed=true;
    private final Regex SPACE = new Regex("[ \\t\\r]+");
    private final Regex INLINE_COMMENT = new Regex("//.*");
    private final Regex INLINE_B_COMMENT = new Regex("/\\*.*\\*/");
    private final Regex B_COMMENT_START = new Regex("/\\*.*");
    private final Regex B_COMMENT_END = new Regex(".*\\*/");
    public Lexer(String filepath) {
        super(filepath);
        //TODO Auto-generated constructor stub
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
                c_block_closed=false;
                System.out.print(B_COMMENT_START.getMatch());
                continue;
            }
            cursor++;
        }
    }
}
