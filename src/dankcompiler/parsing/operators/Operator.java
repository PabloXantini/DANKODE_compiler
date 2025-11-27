package dankcompiler.parsing.operators;

import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenCat;

public class Operator {
	public static final int MAX_BINARY_PRECEDENCE = 5;
	public static final int UNARY_PRECEDENCE = MAX_BINARY_PRECEDENCE + 1;
	public static final int ATOM_PRECEDENCE = Integer.MAX_VALUE;
	public static int getPrecedence(Token token) {
		if(token.getCategory()!=TokenCat.OPERATOR) {
			return ATOM_PRECEDENCE;
		}
		switch(token.getType()) {
			case OR: return 0;
			case AND: return 1;
			case EQUAL, NONEQUAL: return 2;
			case GTE, LTE, GT, LT: return 3;
			case PLUS, MINUS: return 4;
			case MUL, DIV, MOD: return 5;        
			default: return UNARY_PRECEDENCE;//UNARIO-TERM
		}
	}
}
