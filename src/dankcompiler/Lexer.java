package dankcompiler;

public class Lexer extends FileHandler{
    public Lexer(String filepath) {
        super(filepath);
        //TODO Auto-generated constructor stub
    }
    @Override
    public void doPerReadedLine(String currentLine) {
        System.out.println(currentLine);
    }
}
