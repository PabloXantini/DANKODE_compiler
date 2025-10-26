package dankcompiler.parsing.rdutils;

import java.io.IOException;

public class Cursor {
	private FileHandler readerContext = null;
    private int value = 0;
    private int column = 0;
    private int line = 0;
    private String lcontent = "";
    public Cursor(FileHandler reader){
    	this.readerContext = reader;
    }
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
    public void advanceNewLine() throws IOException{
        readerContext.nextLine();
    	value = 0;
        column = 1;
        line++;
        this.lcontent = readerContext.getCurrentLine();
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
