package dankcompiler.dankode.analysis;

import java.util.ArrayList;

import dankcompiler.dankode.errors.CompileError;
import dankcompiler.dankode.errors.CompileErrorCode;
import dankcompiler.dankode.errors.CompileErrorHandler;
import dankcompiler.dankode.errors.CompileErrorType;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

public abstract class AnalyzerChecker extends AnalyzeVisitor {
	private final ArrayList<CompileError> CurrentErrors;
	public AnalyzerChecker(ArrayList<CompileError> errors) {
		CurrentErrors = errors;
	}
	private CompileError throwError(String lexem, int line, int column, CompileErrorCode code, String... args){
        CompileError error = CompileErrorHandler.generateError(lexem, CompileErrorType.SEMANT, line, column, code, args);
        CurrentErrors.add(error);
        return error;
    }
    public void handleError(Token tokenHandled, CompileErrorCode code, String... args) {
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
        				bad_symbol, args[0]
        				);
    			break;
    		default:
    			break;
    	}
    }
}
