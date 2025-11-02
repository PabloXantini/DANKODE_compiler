package dankcompiler.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import dankcompiler.analysis.triplets.InstructionType;
import dankcompiler.analysis.triplets.Tag;
import dankcompiler.analysis.triplets.Triplet;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.*;
import dankcompiler.utils.TypeAdapter;

public class IGenerator extends AnalyzeVisitor {
	//INDEXATION
	private int index = 0;
	private int branch_index = 0;
	//TEMPORALS
	private int tmp_serializer = 1;
	private String tmp_nickname = "temp";
	private final HashMap<Integer, Boolean> ActiveTemps;
	//JUMPS
	private InstructionType jump_reg = InstructionType.YES;
	//OUTPUT
	private final ArrayList<Triplet> ICode;
	public IGenerator() {
		super();
		ActiveTemps = new HashMap<Integer, Boolean>();
		ICode = new ArrayList<Triplet>();
	}
	//GET INTERMMEDIATE CODE
	public ArrayList<Triplet> getOutput(){
		return this.ICode;
	}
	//TRIPLET STUFF
	private void attachEntry(Tag tag, InstructionType instruction, String id_object, String id_source) {
		Triplet newEntry = new Triplet(index++, tag, instruction, id_object, id_source);
		ICode.add(newEntry);
	}
	private void attachEntry(InstructionType instruction, String id_object, String id_source) {
		Triplet newEntry = new Triplet(index++, null, instruction, id_object, id_source);
		ICode.add(newEntry);
	}
	//TEMPORAL HANDLING
	private int restartTemp() {
		ActiveTemps.clear();
		return this.tmp_serializer = 1;
	}
	private int newTemp() {
		while(ActiveTemps.getOrDefault(tmp_serializer, false)) {	
			this.tmp_serializer++;
		}
		ActiveTemps.put(this.tmp_serializer, true);
		return this.tmp_serializer;
	}
	private void quitTemp(int tmp) {
		ActiveTemps.remove(tmp);
		this.tmp_serializer = tmp;
	}
	//GENERATION OF TEMPORAL NICKNAMING
	private String genName() {
		return this.tmp_nickname+this.tmp_serializer;
	}
	private String genName(int i) {
		return this.tmp_nickname+i;
	}
	//BRANCH HANDLING
	private Tag createTag() {
		return new Tag(branch_index++);
	}	
	//MAIN TASK
	@Override
	public void visit(Node node) {
		if(node==null)return;
		if(node instanceof Assignment) {
			restartTemp();
			Variable ID = ((Assignment)node).getVariable();
			Expression expr = ((Assignment)node).getExpression();
			Tag end = createTag();
			int TMP = visitExpression(expr, end);
			String finalID = ID.getValue().getSymbol();
			//Register final instruction
			attachEntry(InstructionType.MOV, finalID, genName(TMP));
		}
		else if(node instanceof While) {
			restartTemp();
			Tag start = createTag();
			Tag end = createTag();
			start.setPointer(index);
			Expression expr = ((While)node).getAtCondition();
			Node body = ((While)node).getLoopBody();
			visitExpression(expr, end);
			check(body);
			attachEntry(InstructionType.JMP, Integer.toString(start.getPointer()), "_");
			end.setPointer(index+1);
		}
	}
	private int visitExpression(Node node, Tag end) {
		int TMP1 = 0;
		if(node instanceof BinaryOp) {
			InstructionType instruction = TypeAdapter.castI(((BinaryOp)node).getOp().getType());
			Expression left = ((BinaryOp)node).getLeftTerm();
			Expression right = ((BinaryOp)node).getRightTerm();			
			TMP1 = handleExpression(instruction, left, right, end);
			return TMP1;
		}
		else if(node instanceof UnaryOp) {
			InstructionType instruction = TypeAdapter.castI(((UnaryOp)node).getOp().getType());
			Expression expr = ((UnaryOp)node).getTerm();
			TMP1 = visitExpression(expr, end);
			attachEntry(instruction, genName(TMP1), "_");
			return TMP1;
		}
		else if(node instanceof Variable) {
			String value = ((Variable)node).getValue().getSymbol();
			TMP1 = newTemp();
			attachEntry(InstructionType.MOV, genName(TMP1), value);
			return TMP1;
		}
		else if(node instanceof Constant) {
			String value = ((Constant)node).getValue().getSymbol();
			TMP1 = newTemp();
			attachEntry(InstructionType.MOV, genName(TMP1), value);
			return TMP1;
		}
		return TMP1;
	}
	private int handleExpression(InstructionType instruction, Node left, Node right, Tag end) {
		int TMP1 = 0;
		int TMP2 = 0;
		Tag sub_start = createTag();
		Tag sub_end = end;
		if(isRelational(instruction) || isLogical(instruction))restartTemp();
		if(isLogical(instruction)) {
			this.jump_reg = instruction;
			sub_end = createTag();
		}
		TMP1 = visitExpression(left, end);
		if(right instanceof Constant) {
			String value = right.getValue().getSymbol();
			if(!isLogical(instruction)) attachEntry(instruction, genName(TMP1), value);
		}else {			
			TMP2 = visitExpression(right, end);
			if(!isLogical(instruction)) attachEntry(instruction, genName(TMP1), genName(TMP2));
		}
		if(isRelational(instruction)) {
			sub_start.setPointer(this.index+2); //CURRENT INS -> JUMP -> NEXT INS
			handleJumps(this.jump_reg, sub_start, sub_end);
		}
		if(isLogical(instruction)) {
			sub_end.setPointer(index);
		}
		return TMP1;
	}
	private void handleJumps(InstructionType instruction, Tag start, Tag end) {
		String skipEntry;
		switch (instruction) {
			case OR:
				attachEntry(end, InstructionType.J_True, "JUMP?", "_");
				skipEntry = Integer.toString(start.getPointer());
				attachEntry(InstructionType.J_False, skipEntry, "_");
				break;
			case AND:
				skipEntry = Integer.toString(start.getPointer());
				attachEntry(InstructionType.J_True, skipEntry, "_");
				attachEntry(end, InstructionType.J_False, "JUMP?", "_");
				break;				
			default:
				break;
		}
	}
	private boolean isLogical(InstructionType type) {
		return type==InstructionType.AND || type==InstructionType.OR;
	}
	private boolean isRelational(InstructionType type) {
		switch (type) {
			case EQUAL, NONEQUAL, GTE, LTE, GT, LT: return true;
			default: return false;
		}
	}
}
