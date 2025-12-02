package dankcompiler.dankode.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import dankcompiler.dankode.analysis.triplets.InstructionType;
import dankcompiler.dankode.analysis.triplets.Triplet;
import dankcompiler.dankode.analysis.triplets.args.Argument;
import dankcompiler.dankode.analysis.triplets.args.ConstV;
import dankcompiler.dankode.analysis.triplets.args.StoreVar;
import dankcompiler.dankode.analysis.triplets.args.Tag;
import dankcompiler.dankode.analysis.triplets.args.Temporal;
import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.Assignment;
import dankcompiler.parsing.ast.nodes.BinaryOp;
import dankcompiler.parsing.ast.nodes.Constant;
import dankcompiler.parsing.ast.nodes.Declaration;
import dankcompiler.parsing.ast.nodes.DoWhile;
import dankcompiler.parsing.ast.nodes.Expression;
import dankcompiler.parsing.ast.nodes.For;
import dankcompiler.parsing.ast.nodes.If;
import dankcompiler.parsing.ast.nodes.UnaryOp;
import dankcompiler.parsing.ast.nodes.Variable;
import dankcompiler.parsing.ast.nodes.While;
import dankcompiler.utils.TypeAdapter;

public class IRGenerator implements ASTVisitor{
	//INDEX
	private int index = 0;
	//SERIALIZER
	private int tmp_s = 1; //temporal
	private int tag_s = 1; //labels/tags
	//NICKNAMES
	private String nick_temp = "temp";
	private String nick_tag = "L";
	//TEMPS
	private final HashMap<Integer, Boolean> ActiveTemps;
	//ARGS
	private Argument currentArg = null;
	//OUTPUT
	private final ArrayList<Triplet> IRCode;
	public IRGenerator() {
		ActiveTemps = new HashMap<Integer, Boolean>();
		//CLASS
		IRCode = new ArrayList<Triplet>();
	}
	public ArrayList<Triplet> getOutput(){
		return IRCode;
	}
	public void generate(AST ast) {
		ast.getRoot().accept(this);
		attachEntry(InstructionType.NOP, null, null);
	}
	//TMP-SERIALIZER=======
	private int restartTempValue() {
		ActiveTemps.clear();
		return this.tmp_s = 1;
	}
	private int newTempValue() {
		while(ActiveTemps.getOrDefault(tmp_s, false)) {	
			this.tmp_s++;
		}
		ActiveTemps.put(tmp_s, true);
		return this.tmp_s;
	}
	private void quitTempValue(int tmp) {
		ActiveTemps.remove(tmp);
		this.tmp_s = tmp;
	}
	//FACTORY METHOD
	private Temporal genTemp() {
		int new_id = newTempValue();
		return new Temporal(nick_temp, new_id);
	}
	private void quitTemp(Temporal temp) {
		quitTempValue(temp.getID());
	}
	//END TEMP SERIALIZER ==========
	//TAG HANDLING
	private Tag createTag() {
		return new Tag(nick_tag, tag_s++);
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
	/*
	private void backpatch(ArrayList<Tag> list, int pointer) {
		for (Tag tag : list) {
			tag.setPointer(pointer);
		}
	}*/
	private void backpatch(ArrayList<Tag> list, Tag changetag) {
		int p = changetag.getPointer();
		int id = changetag.getId();
		for (Tag tag : list) {
			tag.setPointer(p);
			tag.setId(id);
		}
	}
	//CODE GENERATION
	private Triplet attachEntry(InstructionType instruction, Argument object, Argument source) {
		Triplet newEntry = new Triplet(this.index++, instruction, object, source);
		IRCode.add(newEntry);
		return newEntry;
	}
	public void reset() {
		index = 0;
		tmp_s = 1;
		tag_s = 1;
		ActiveTemps.clear();
		IRCode.clear();
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
	@Override
	public Node visit(GroupNode groupnode) {
		for(Node child : groupnode.getChildren()) {
			child.accept(this);
		}
		return groupnode;
	}
	@Override
	public Node visit(Declaration declaration) {
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		restartTempValue();
		StoreVar obj = new StoreVar(assignment.getVariable().getValue().getSymbol()); 
		assignment.getExpression().accept(this);
		//attachEntry(InstructionType.MOV, obj, currentArg);
		if (currentArg == null) assignment.getExpression().accept(this);
		attachEntry(InstructionType.MOV, obj, currentArg);
		if (currentArg instanceof Temporal) {
			quitTemp((Temporal)currentArg);
		}
		currentArg = null;
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		restartTempValue();
		Tag start = createTag();
		start.setPointer(this.index);
		Tag trueTag = createTag();
		Tag falseTag = createTag();
		Expression expr = whileNode.getAtCondition();
		expr.accept(this);
		trueTag.setPointer(index);
		//whileNode.getAtCondition().accept(this);
		whileNode.getLoopBody().accept(this);
		attachEntry(InstructionType.JMP, start, null);
		falseTag.setPointer(index);
		backpatch(expr.getTrue(), trueTag);
		backpatch(expr.getFalse(), falseTag);
		return whileNode;
	}
	@Override
	public Node visit(DoWhile dowhileNode) {
		// TODO Auto-generated method stub
		return dowhileNode;
	}
	@Override
	public Node visit(If ifNode) {
		// TODO Auto-generated method stub
		return ifNode;
	}
	@Override
	public Node visit(For forNode) {
		// TODO Auto-generated method stub
		return forNode;
	}
	@Override
	public Node visitExpression(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	@Override
	public Node visit(BinaryOp binary_op) {
		Argument t1 = null;
		Argument t2 = null;		
		InstructionType instruction = TypeAdapter.castI(binary_op.getOp().getType());
		Expression left = binary_op.getLeftTerm();
		Expression right = binary_op.getRightTerm();
		if(isRelational(instruction) || isLogical(instruction)) restartTempValue();
		if(isLogical(instruction)) {			
			left.accept(this);
			t1 = currentArg;
			switch (instruction) {
				case OR:
					Tag falseTag = createTag();
					falseTag.setPointer(index);
					backpatch(left.getFalse(), falseTag);
					if(right instanceof Constant) {
						//t2 = new ConstV(right.getValue().getSymbol());
						currentArg = new ConstV(right.getValue().getSymbol());
						//binary_op.setTrue(left.getTrue());
					}else {	
						right.accept(this);
						//t2 = currentArg;
						//binary_op.setTrue(merge(left.getTrue(),right.getTrue()));
					}
					binary_op.setTrue(merge(left.getTrue(),right.getTrue()));
					binary_op.setFalse(right.getFalse());
					break;
				case AND:
					Tag trueTag = createTag();
					trueTag.setPointer(index);
					backpatch(left.getTrue(), trueTag);
					if(right instanceof Constant) {
						//t2 = new ConstV(right.getValue().getSymbol());
						currentArg = new ConstV(right.getValue().getSymbol());
					}else {			
						right.accept(this);
						//t2 = currentArg;
					}
					binary_op.setTrue(right.getTrue());
					binary_op.setFalse(merge(left.getFalse(), right.getFalse()));
					break;
				default:
					break;
			}
		}else {
			left.accept(this);
			t1 = currentArg;
			if(right instanceof Constant) {
				t2 = new ConstV(right.getValue().getSymbol());
			}else {
				right.accept(this);
				t2 = currentArg;
			}
			attachEntry(instruction, t1, t2);
			if(isRelational(instruction)) {
				Tag trueTag = createTag();
				Tag falseTag = createTag();
				binary_op.setTrue(makeList(trueTag));
				binary_op.setFalse(makeList(falseTag));
				attachEntry(InstructionType.J_True, trueTag, null);
				attachEntry(InstructionType.J_False, falseTag, null);
			}
		}
		if(t1 instanceof Temporal) quitTemp((Temporal) t1);
		if(t2 instanceof Temporal) quitTemp((Temporal) t2);
		currentArg = t1;
		//binary_op.getRightTerm().accept(this);
		return binary_op;
	}
	@Override
	public Node visit(UnaryOp unary_op) {
		InstructionType instruction = TypeAdapter.castI(unary_op.getOp().getType());
		unary_op.getTerm().accept(this);
		attachEntry(instruction, currentArg, null);
		return unary_op;
	}
	@Override
	public Node visit(Variable var) {
		StoreVar src = new StoreVar(var.getValue().getSymbol());
		currentArg = genTemp();
		attachEntry(InstructionType.MOV, currentArg, src);
		return var;
	}
	@Override
	public Node visit(Constant constant) {
		ConstV src = new ConstV(constant.getValue().getSymbol());
		currentArg = genTemp();
		attachEntry(InstructionType.MOV, currentArg, src);
		return constant;
	}
}
