package dankcompiler.dankode.build.machineinfo;

import dankcompiler.dankode.analysis.triplets.InstructionType;

public class InstructionAdapter {
	public static String castx86(InstructionType instruction) {
		switch(instruction) {
			case MOV: return "MOV"; 
			case ADD: return "ADD";
			case SUB: return "SUB";
			case MUL: return "MUL";
			case DIV: return "DIV";
			case MOD: return "MOD";
			case AND: return "AND";
			case OR: return "OR";
			case EQUAL: return "JE";
			case NONEQUAL: return "JNE";
			case GTE: return "JGE";
			case LTE: return "JLE";
			case GT: return "JG";
			case LT: return "JL";
			case JMP: return "JMP";
			case HLT: return "HLT";
			default: return "NOP";
		}
	}
}
