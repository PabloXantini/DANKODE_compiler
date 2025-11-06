package dankcompiler.parsing.rdutils;

import java.io.IOException;

public class Cursor {
	private FileHandler readerContext = null;
    private int value = 0;
    private int column = 1;
    private int line = 0;
    private String lcontent = "";
    private boolean inited = false;
    private boolean carried = false;
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
    public void markCarry(boolean state) {
    	this.carried = state;
    }
    public boolean isInLine() throws IOException{
    	if(!inited){ 
    		advanceNewLine();
    		inited = true;
    	}
    	if(lcontent==null) return false;
        return value<lcontent.length();
    }
    public boolean carryLately() {
    	return carried;
    }
    public void advanceNewLine() throws IOException{
        readerContext.nextLine();
        this.lcontent = readerContext.getCurrentLine();
        if(readerContext.getCurrentLine()==null) return;
        carried = true;
    	value = 0;
        column = 1;
        line++;
    }
    public void reset() {
    	this.inited = false;
    	this.carried = false;
    	this.lcontent = "";
    	this.value = 0;
        this.column = 1;
        this.line = 0;
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
    public void writeln(){
    	readerContext.getWriter().println();
    	readerContext.getWriter().flush();
    }
    public void write(String str){
    	if(str==null) return;
    	readerContext.getWriter().print(str);
    }
}
