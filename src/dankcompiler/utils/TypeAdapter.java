package dankcompiler.utils;

import dankcompiler.analysis.symbol.DataType;
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
}
