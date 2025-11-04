package dankcompiler.analysis;

import java.util.ArrayList;

import dankcompiler.analysis.symbol.DataType;
import dankcompiler.analysis.symbol.SymbolTable;
import dankcompiler.analysis.triplets.Triplet;
import dankcompiler.errors.CompileError;
import dankcompiler.errors.CompileErrorCode;
import dankcompiler.errors.CompileErrorHandler;
import dankcompiler.errors.CompileErrorType;
import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.*;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenType;
import dankcompiler.utils.TypeAdapter;

public class Analyzer {
	//STATES
	enum AnalysisState {
		SYMBOL_GEN,
		TYPE_RESOLUTION,
		GENERATION,
	}
	private AnalysisState current_state = AnalysisState.SYMBOL_GEN;
	//RESOURCES
	private boolean verbosed = false;
	private Token tokenVisited = null;
	private AST ast;
	private final IGenerator Generator;
	/*
	 * OUTPUTS
	 */
	//SYMBOL TABLE
	private SymbolTable MainSymbolTable = null;
	//GENERATED CODE
	private ArrayList<Triplet> ICode = null;
	//ERRORS
	private final ArrayList<CompileError> CurrentErrors;
	public Analyzer() {
		Generator = new IGenerator();
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
	//GET INTERMEDIATE CODE
	public ArrayList<Triplet> getCode() {
		return this.ICode;
	}
	public void setCode(ArrayList<Triplet> code) {
		this.ICode = code;
	}
	//METHODS FOR ERROR HANDLING
	public ArrayList<CompileError> getCurrentErrors(){
    	return this.CurrentErrors;
    }
	private CompileError throwError(String lexem, int line, int column, CompileErrorCode code, String... args){
        CompileError error = CompileErrorHandler.generateError(lexem, CompileErrorType.SEMANT, line, column, code, args);
        CurrentErrors.add(error);
        return error;
    }
    private void handleError(Token tokenHandled, CompileErrorCode code, String... args) {
    	String bad_symbol = tokenHandled.getSymbol();
    	switch(code) {
    		case VAR_UNDEFINED:
    			throwError(
        				bad_symbol, tokenHandled.getLine(), tokenHandled.getColumn(), code,
        				bad_symbol
        				);
    			break;
    		case VAR_REDEFINITION:
    			throwError(
        				bad_symbol, tokenHandled.getLine(), tokenHandled.getColumn(), code,
        				bad_symbol, args[0]
        				);
    			break;
    		case OPERATOR_INVALID:
    			throwError(
        				bad_symbol, tokenHandled.getLine(), tokenHandled.getColumn(), code,
        				bad_symbol, args[0], args[1]
        				);
    			break;
    		case TYPE_EXPR_INCOMPATIBILITY:
    			throwError(
        				bad_symbol, tokenHandled.getLine(), tokenHandled.getColumn(), code,
        				bad_symbol, args[0], args[1]
        				);
    			break;
    		default:
    			break;
    	}
    }
	//ANALYZE THE ENTIRE AST
	public void analyze(AST ast) {
		this.ast = ast;
		//1. READS THE AST -> SYMBOL_TABLE
		generateSymbolTable();
		//2. READS THE AST + SYMBOL_TABLE -> DATA_TYPE_RESOLUTION
		resoluteTypes();
		//3. GENERETE I_CODE
		callGenerator();
	}
	private void generateSymbolTable() {
		traverseAST(ast.getRoot());
		current_state = AnalysisState.TYPE_RESOLUTION;
	}
	private void resoluteTypes() {
		traverseAST(ast.getRoot());
		current_state = AnalysisState.GENERATION;
	}
	private void callGenerator() {
		//traverseAST(ast.getRoot());
		Generator.check(ast.getRoot());
		setCode(Generator.getOutput());
		
	}
	private void handleState(AnalysisState state, Node node){
		switch (state) {
		case SYMBOL_GEN:
			checkForSymbols(node);
			break;
		case TYPE_RESOLUTION:
			checkTypeResolution(node);
			break;
		//case GENERATION:
		//	generate(node);
		//	break;
		default:
			break;
		}
	}
	private void checkForSymbols(Node node) {
		if(verbosed && MainSymbolTable!=null) {
			return;
		}else {
			genSymbol(node);
		}
	}
	private void genSymbol(Node node) {
		//TODO: Someday got implemented
	}
	private void checkTypeResolution(Node node) {
		if(node instanceof Declaration) {
			DataType type = TypeAdapter.cast(((Declaration)node).getType());
			Variable ID = ((Declaration)node).getVariable();
			String ID_key = ID.getValue().getSymbol(); 
			DataType existing_type = MainSymbolTable.get(ID_key).getType();
			if(existing_type!=type) {
				handleError(ID.getValue(), CompileErrorCode.VAR_REDEFINITION, existing_type.name());
				return;
			}
		}
		else if(node instanceof Assignment) {
			Variable ID = ((Assignment)node).getVariable();
			Expression expr = ((Assignment)node).getExpression();
			String ID_key = ID.getValue().getSymbol();
			//String expr_match = expr.getStart().getSymbol();
			DataType type = MainSymbolTable.get(ID_key).getType();
			DataType exptype = inferType(expr);
			if(type==DataType.NONE) {
				handleError(ID.getValue(), CompileErrorCode.VAR_UNDEFINED);
				return;
			}else if(!isExpressionCompatible(type, exptype)) {
				if(exptype!=DataType.NONE) handleError(this.tokenVisited, CompileErrorCode.TYPE_EXPR_INCOMPATIBILITY, exptype.name(), type.name());
			}
		}
		else if(node instanceof While) {
			Expression expr = ((While)node).getAtCondition();
			DataType type = DataType.BOOL;
			DataType exptype = inferType(expr);
			if(!isExpressionCompatible(type, exptype)) {
				if(exptype!=DataType.NONE) handleError(this.tokenVisited, CompileErrorCode.TYPE_EXPR_INCOMPATIBILITY, exptype.name(), type.name());
			}
		}
	}
	private boolean isExpressionCompatible(DataType type_expected, DataType type_given) {
		if(type_expected == type_given) return true;
		if(type_expected == DataType.NUMPT && type_given == DataType.NUMMY) return true;
		return false;
	}
	private DataType inferType(Node node) {
		if(node instanceof Constant) {
			this.tokenVisited = node.getValue(); 
			return TypeAdapter.cast(node.getValue().getType());
		}else if(node instanceof Variable) {
			this.tokenVisited = node.getValue();
			String ID_key = node.getValue().getSymbol();
			DataType type = MainSymbolTable.get(ID_key).getType();
			if(type==DataType.NONE) handleError(node.getValue(), CompileErrorCode.VAR_UNDEFINED);
			return type;
		}else if(node instanceof BinaryOp){
			DataType ltype = inferType(((BinaryOp)node).getLeftTerm());
			DataType rtype = inferType(((BinaryOp)node).getRightTerm());
			return inferBinaryType(ltype, ((BinaryOp)node).getOp(), rtype);
		}else if(node instanceof UnaryOp){
			return inferType(((UnaryOp)node).getTerm());
		}else return DataType.NONE;
	}
	public DataType inferBinaryType(DataType ltype, Token op, DataType rtype) {
		if(ltype==DataType.NONE||rtype==DataType.NONE) return DataType.NONE;
		switch(op.getType()) {
			case OR, AND, EQUAL, NONEQUAL, LTE, GTE, LT, GT: return DataType.BOOL;
			default: break;
		}
		if(ltype==DataType.NUMPT || rtype==DataType.NUMPT) return DataType.NUMPT;
		if(ltype==DataType.NUMMY && rtype==DataType.NUMMY) return DataType.NUMMY;
		if(op.getType()==TokenType.PLUS && (ltype==DataType.CHARA || rtype==DataType.CHARA)) return DataType.CHARA;
		handleError(op, CompileErrorCode.OPERATOR_INVALID, ltype.name(), rtype.name());
		return DataType.NONE;
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
