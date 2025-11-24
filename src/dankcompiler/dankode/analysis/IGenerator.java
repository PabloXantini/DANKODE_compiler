package dankcompiler.dankode.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import dankcompiler.dankode.analysis.triplets.InstructionType;
import dankcompiler.dankode.analysis.triplets.Tag;
import dankcompiler.dankode.analysis.triplets.Triplet;
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
	private final ArrayList<Triplet> UnresolvedJumps;
	//OUTPUT
	private final ArrayList<Triplet> ICode;
	public IGenerator() {
		super();
		ActiveTemps = new HashMap<Integer, Boolean>();
		//BranchContext = new Stack<ArrayList<Tag>>();
		//ActiveBranches = new Stack<Branch>();
		UnresolvedJumps = new ArrayList<Triplet>();
		ICode = new ArrayList<Triplet>();
	}
	//RESET
	public void reset() {
		index = 0;
		branch_index = 0;
		ActiveTemps.clear();
		UnresolvedJumps.clear();
		ICode.clear();
	}
	//GET INTERMMEDIATE CODE
	public ArrayList<Triplet> getOutput(){
		return this.ICode;
	}
	//TRIPLET STUFF
	private Triplet attachEntry(Tag tag, InstructionType instruction, String id_object, String id_source) {
		Triplet newEntry = new Triplet(index++, tag, instruction, id_object, id_source);
		ICode.add(newEntry);
		return newEntry;
	}
	private Triplet attachEntry(InstructionType instruction, String id_object, String id_source) {
		Triplet newEntry = new Triplet(index++, null, instruction, id_object, id_source);
		ICode.add(newEntry);
		return newEntry;
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
	private String genName(int i) {
		return this.tmp_nickname+i;
	}
	//TAG HANDLING
	private Tag createTag() {
		return new Tag(branch_index++);
	}
	//BACKPATCHING
	private ArrayList<Tag> makeList(Tag tag){
		ArrayList<Tag> newList = new ArrayList<Tag>();
		newList.add(tag);
		return newList;
	}
	private ArrayList<Tag> merge(ArrayList<Tag> list1, ArrayList<Tag> list2){
		ArrayList<Tag> merged = new ArrayList<Tag>(list1);
		merged.addAll(list2);
		return merged;
	}
	private void backpatch(ArrayList<Tag> list, int pointer) {
		for (Tag tag : list) {
			tag.setPointer(pointer);
		}
	}
	//MAIN TASK
	@Override
	public void visit(Node node) {
		if(node==null)return;
		if(node instanceof Assignment) {
			restartTemp();
			Variable ID = ((Assignment)node).getVariable();
			Expression expr = ((Assignment)node).getExpression();
			String TMP = visitExpression(expr);
			String finalID = ID.getValue().getSymbol();
			//Register final instruction
			attachEntry(InstructionType.MOV, finalID, TMP);
			for (Triplet batchJump : UnresolvedJumps) {
				String new_value = Integer.toString(batchJump.getTag().getPointer());
				batchJump.setIdObject(new_value);
			}
		}
		else if(node instanceof While) {
			restartTemp();
			Tag start = createTag();
			Tag trueTag = createTag(); //tag when continue loop
			Tag falseTag = createTag(); //tag when skip loop
			start.setPointer(index);
			Expression expr = ((While)node).getAtCondition();
			Node body = ((While)node).getLoopBody();
			//Visit conditional	
			visitExpression(expr);
			//Define the jump TRUE direction
			trueTag.setPointer(index);
			//Visit the entire body
			check(body);
			//Attach the JMP loop of WHILE
			attachEntry(InstructionType.JMP, Integer.toString(start.getPointer()), "_");
			//Define the jump FALSE direction
			falseTag.setPointer(index);
			//back-patch the conditional
			backpatch(expr.getTrue(), trueTag.getPointer());
			backpatch(expr.getFalse(), falseTag.getPointer());
			for (Triplet batchJump : UnresolvedJumps) {
				String new_value = Integer.toString(batchJump.getTag().getPointer());
				batchJump.setIdObject(new_value);
			}
		}
	}
	private String visitExpression(Node node) {
		String TMP = "NONE";
		if(node instanceof BinaryOp) {
			TMP = handleExpression((BinaryOp)node);
			return TMP;
		}
		else if(node instanceof UnaryOp) {
			InstructionType instruction = TypeAdapter.castI(((UnaryOp)node).getOp().getType());
			Expression expr = ((UnaryOp)node).getTerm();
			TMP = visitExpression(expr);
			attachEntry(instruction, TMP, "_");
			return TMP;
		}
		else if(node instanceof Variable) {
			String value = ((Variable)node).getValue().getSymbol();
			int newTMP = newTemp();
			attachEntry(InstructionType.MOV, genName(newTMP), value);
			return genName(newTMP);
		}
		else if(node instanceof Constant) {
			String value = ((Constant)node).getValue().getSymbol();
			int newTMP = newTemp();
			attachEntry(InstructionType.MOV, genName(newTMP), value);
			return genName(newTMP);
		}
		return TMP;
	}
	private String handleExpression(BinaryOp node) {
		InstructionType instruction = TypeAdapter.castI(node.getOp().getType());
		Expression left = node.getLeftTerm();
		Expression right = node.getRightTerm();		
		String LARG = "NONE";
		String RARG = "NONE";
		if(isRelational(instruction) || isLogical(instruction))restartTemp();
		if(isLogical(instruction)) {
			//System.out.println(instruction.name());
			LARG = visitExpression(left);
			switch (instruction) {
				case OR:
					backpatch(left.getFalse(), index);
					if(right instanceof Constant) {
						RARG = right.getValue().getSymbol();
						node.setTrue(left.getTrue());
					}else {			
						RARG = visitExpression(right);
						node.setTrue(merge(left.getTrue(),right.getTrue()));
					}
					node.setFalse(right.getFalse());
					break;
				case AND:
					backpatch(left.getTrue(), index);
					if(right instanceof Constant) {
						RARG = right.getValue().getSymbol();
					}else {			
						RARG = visitExpression(right);
					}
					node.setTrue(right.getTrue());
					node.setFalse(merge(left.getFalse(), right.getFalse()));
					break;
				default:
					break;
			}
		}else {			
			LARG = visitExpression(left);
			//LARG = visitForceExpression(left);
			if(right instanceof Constant) {
				RARG = right.getValue().getSymbol();
			}else {			
				RARG = visitExpression(right);
				//RARG = visitForceExpression(right);
			}
			attachEntry(instruction, LARG, RARG);
			if(isRelational(instruction)) {
				Tag trueTag = createTag();
				Tag falseTag = createTag();
				node.setTrue(makeList(trueTag));
				node.setFalse(makeList(falseTag));
				Triplet j1 = attachEntry(trueTag, InstructionType.J_True, "?", "_");
				Triplet j2 = attachEntry(falseTag, InstructionType.J_False, "?", "_");
				UnresolvedJumps.add(j1);
				UnresolvedJumps.add(j2);
			}
		}
		return LARG;
	}
	private String visitForceExpression(Expression expr) {
		if(!expr.getTrue().isEmpty() && !expr.getFalse().isEmpty()) {
			int TMP = newTemp();
			Tag end = createTag();
			//CASE IF TRUE: RETURNS 1
			backpatch(expr.getTrue(), index);
			attachEntry(InstructionType.MOV, genName(TMP), "1");
			Triplet j1 = attachEntry(end, InstructionType.J_True, "?", "_");
			UnresolvedJumps.add(j1);
			//CASE IF FALSE: RETURNS 0
			backpatch(expr.getFalse(), index);
			attachEntry(InstructionType.MOV, genName(TMP), "0");
			//CLOSURE
			end.setPointer(index);
			return genName(TMP);
		}
		return visitExpression(expr);
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
