package dankcompiler;

import dankcompiler.tokens.Regex;

public class Lexer extends FileHandler{
    private int cursor = 0;
    private int column = 0;
    private int line = 0;
    private final Regex SPACE = new Regex("[ \\t\\r]+");
    private final Regex INLINE_COMMENT = new Regex("//.*");
    private final Regex BLOCK_COMMENT = new Regex("\\*[\\s\\S]*\\*");
    public Lexer(String filepath) {
        super(filepath);
        //TODO Auto-generated constructor stub
    }
    @Override
    public void doPerReadedLine(String currentLine) {
        cursor=0;
        int tmp;
        while (cursor<currentLine.length()) {
            if((tmp=SPACE.match(currentLine, cursor))!=-1){
                cursor=tmp;
                continue;
            }else if((tmp=INLINE_COMMENT.match(currentLine, cursor))!=-1){
                cursor=tmp;
                System.out.print(INLINE_COMMENT.getMatch());
                continue;
            }
            else{
                cursor++;
            }
        }
    }
}
