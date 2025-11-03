package dankcompiler.parsing.ast.nodes;

import java.util.ArrayList;

import dankcompiler.analysis.triplets.Tag;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

//EXPRESSIONS
public class Expression extends Node {
	private Token start;
	private ArrayList<Tag> trueList = null;
	private ArrayList<Tag> falseList = null;
    public Expression(){
    	this.trueList = new ArrayList<Tag>();
    	this.falseList = new ArrayList<Tag>();
    }
    public Token getStart() {
    	return this.start;
    }
    public ArrayList<Tag> getTrue(){
    	return this.trueList;
    }
    public ArrayList<Tag> getFalse(){
    	return this.falseList;
    }
    public void setStart(Token start) {
    	this.start = start;
    }
    public void setTrue(ArrayList<Tag> list){
    	this.trueList = list;
    }
    public void setFalse(ArrayList<Tag> list){
    	this.falseList = list;
    }
}