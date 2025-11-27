package dankcompiler.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTGeneralVisitor;
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
import dankcompiler.parsing.operators.Operator;

public class SyntaxExporter extends ASTGeneralVisitor{
	private File TempOutput = null;
	private PrintWriter writer = null;
	private boolean allow_separator = true;
	//flags
	private boolean permit_space = true;
	private boolean permit_br = false;
	private final Stack<Integer> precedences; 
	private void printSeparator() {
		if(allow_separator) {
			if(permit_space) writer.print(" ");
			if(permit_br) writer.println();
		}
	}
	private void nextLine() {
		permit_br = true;
		permit_space = false;
		printSeparator();
		permit_br = false;
		permit_space = true;
	}
	private boolean checkPrecedences(int super_precedence, int precedence) {
		if(super_precedence == Operator.ATOM_PRECEDENCE) return false;
		return precedence < super_precedence;
	}
	public SyntaxExporter(File output) {
		this.TempOutput = output;
		this.precedences = new Stack<Integer>();
		this.precedences.push(Integer.MAX_VALUE);
	}
	public PrintWriter getWriter() {
		return writer;
	}
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	public void export(AST ast){
		FileWriter write = null;
		try {
			write = new FileWriter(TempOutput);	
		} catch (IOException e) {
			System.out.println("Error when binding outputfile: "+ e);
		}
		writer = new PrintWriter(write);
		ast.getRoot().accept(this);
		writer.close();
	}
	@Override
	public Node visit(Declaration declaration) {
		writer.print(declaration.getDefType().getSymbol());
		printSeparator();
		writer.print(declaration.getVariable().getValue().getSymbol());
		writer.print(";");
		nextLine();
		writer.flush();
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		writer.print(assignment.getVariable().getValue().getSymbol());
		printSeparator();
		writer.print("=");
		printSeparator();
		assignment.getExpression().accept(this);
		writer.print(";");
		nextLine();
		writer.flush();
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		writer.print("while");
		writer.print("(");
		whileNode.getAtCondition().accept(this);
		writer.print(")");
		writer.print("{");
		nextLine();
		whileNode.getLoopBody().accept(this);
		writer.print("}");
		nextLine();
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
		int precedence = Operator.getPrecedence(binary_op.getOp());
		boolean permitParentesis = checkPrecedences(precedences.peek(), precedence);
		if (permitParentesis) writer.print("(");
		precedences.push(precedence);	
		binary_op.getLeftTerm().accept(this);
		printSeparator();
		writer.print(binary_op.getOp().getSymbol());
		printSeparator();
		binary_op.getRightTerm().accept(this);
		precedences.pop();
		if (permitParentesis) writer.print(")");
		return binary_op;
	}

	@Override
	public Node visit(UnaryOp unary_op) {
		writer.print(unary_op.getOp().getSymbol());
		unary_op.getTerm().accept(this);
		return unary_op;
	}

	@Override
	public Node visit(Variable var) {
		writer.print(var.getValue().getSymbol());
		return var;
	}

	@Override
	public Node visit(Constant constant) {
		writer.print(constant.getValue().getSymbol());
		return constant;
	}
}
