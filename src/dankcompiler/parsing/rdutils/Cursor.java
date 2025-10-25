package dankcompiler.parsing.rdutils;

public class Cursor {
    private int value = 0;
    private int column = 0;
    private int line = 0;
    private String lcontent = "";
    public Cursor(){}
    public int getValue() {
        return value;
    }
    public int getColumn() {
        return column;
    }
    public int getLine() {
        return line;
    }
    public String getLineContent(){
        return lcontent;
    }
    public void setLineContent(String lcontent){
        this.lcontent = lcontent;
    }
    public boolean isInLine(){
        return value<lcontent.length();
    }
    public void advanceNewLine(String lcontent){
        value = 0;
        column = 1;
        line++;
        this.lcontent = lcontent;
    }
    public void advance(int offset, int coloffset){
        this.value=offset;
        this.column+=coloffset;
    }
    public void next(){
        this.value++;
        this.column++;
    }
    public void pass(){
        this.value=lcontent.length();
    }
}
