package dankcompiler.analysis;

import java.util.ArrayList;

import dankcompiler.analysis.symbol.DataType;
import dankcompiler.analysis.symbol.SymbolTable;
import dankcompiler.errors.CompileError;
import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.*;
import dankcompiler.parsing.tokens.TokenType;

public class Analyzer {
	//STATES
	enum AnalysisState {
		SYMBOL_GEN,
		TYPE_RESOLUTION
	}
	private AnalysisState current_state = AnalysisState.SYMBOL_GEN;
	//RESOURCES
	private boolean verbosed = false;
	private AST ast;
	/*
	 * OUTPUTS
	 */
	//SYMBOL TABLE
	private SymbolTable MainSymbolTable;
	//ERRORS
	private final ArrayList<CompileError> CurrentErrors;
	public Analyzer() {
		MainSymbolTable = new SymbolTable(); 
		CurrentErrors = new ArrayList<CompileError>();
	}
	//SET VERBOSITY OF SYMBOL TABLE
	public void setupVerbosity(boolean value) {
		this.verbosed = value;
	}
	//CLEAN MEMORY
	public void clean() {
		this.CurrentErrors.clear();
	}
	//GET/SET THE SYMBOL TABLE
	public SymbolTable getSymbolTable() {
		return MainSymbolTable;
	}
	public void setSymbolTable(SymbolTable symbol_table) {
		this.MainSymbolTable = symbol_table;
	}
	//METHODS FOR ERROR HANDLING
	public ArrayList<CompileError> getCurrentErrors(){
    	return this.CurrentErrors;
    }
	//ANALYZE THE ENTIRE AST
	public void analyze(AST ast) {
		this.ast = ast;
		//1. READS THE AST -> SYMBOL_TABLE
		generateSymbolTable();
		//2. READS THE AST + SYMBOL_TABLE -> DATA_TYPE_RESOLUTION
	}
	private void generateSymbolTable() {
		traverseAST(ast.getRoot());
		current_state = AnalysisState.TYPE_RESOLUTION;
	}
	private void handleState(AnalysisState state, Node node){
		switch (state) {
		case SYMBOL_GEN:
			checkForSymbols(node);
			break;
		default:
			break;
		}
	}
	private void checkForSymbols(Node node) {
		if(verbosed) {
			return;
		}else {
			genSymbol(node);
		}
	}
	private void genSymbol(Node node) {
		//TODO: Someday got implemented
	}
	//This method is the implementation of AST traverse
	private void traverseAST(Node node) {
		if(node==null) return;
		//System.out.println("Visit ->: "+node.toString());
		handleState(current_state, node);
		if(node instanceof GroupNode) {
			for(Node child : ((GroupNode)node).getChildren()) {
				traverseAST(child);
			}
		}else if(node instanceof Declaration) {
			traverseAST(((Declaration)node).getVariable());
		}else if(node instanceof Assignment) {
			traverseAST(((Assignment)node).getVariable());
			traverseAST(((Assignment)node).getExpression());
		}else if(node instanceof While) {
			traverseAST(((While)node).getAtCondition());
			traverseAST(((While)node).getLoopBody());
		}else if(node instanceof BinaryOp) {
			traverseAST(((BinaryOp)node).getLeftTerm());
			traverseAST(((BinaryOp)node).getRightTerm());
		}else if(node instanceof UnaryOp) {
			traverseAST(((UnaryOp)node).getTerm());
		}
	}	
}
