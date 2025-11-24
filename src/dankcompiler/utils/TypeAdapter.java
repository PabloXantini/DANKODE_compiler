package dankcompiler.utils;

import dankcompiler.dankode.analysis.symbol.DataType;
import dankcompiler.dankode.analysis.triplets.InstructionType;
import dankcompiler.parsing.tokens.TokenType;

public class TypeAdapter {
	public static DataType cast(TokenType type) {
		switch(type) {
			case NUMMY, CINT: return DataType.NUMMY;
			case NUMPT, CFLOAT: return DataType.NUMPT;
			case CHARA, CSTRING: return DataType.CHARA;
			default: return DataType.NONE;
		}
	}
	public static InstructionType castI(TokenType type) {
		switch(type) {
			case PLUS: return InstructionType.ADD;
			case MINUS: return InstructionType.SUB;
			case MUL: return InstructionType.MUL;
			case DIV: return InstructionType.DIV;
			case MOD: return InstructionType.MOD;
			case AND: return InstructionType.AND;
			case OR: return InstructionType.OR;
			case EQUAL: return InstructionType.EQUAL;
			case NONEQUAL: return InstructionType.NONEQUAL;
			case GTE: return InstructionType.GTE;
			case LTE: return InstructionType.LTE;
			case GT: return InstructionType.GT;
			case LT: return InstructionType.LT;
			case EOF: return InstructionType.HLT;
			default: return InstructionType.NOP;
		}
	}
}
