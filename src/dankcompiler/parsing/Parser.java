package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.errors.CompileErrorHandler;
import dankcompiler.errors.CompileError;
import dankcompiler.errors.CompileErrorCode;
import dankcompiler.errors.CompileErrorType;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenCat;
import dankcompiler.parsing.tokens.TokenType;

public class Parser {
    //OUTPUT
    private AST ast;
    //TOKEN BACKUP
    private Token current_token;
    //ERRORS
    private final ArrayList<CompileError> CurrentErrors;

    private Lexer lexerReference;

    public Parser(Lexer lexer){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.CurrentErrors = new ArrayList<CompileError>();
        
        //INITIALIZATION
        this.lexerReference = lexer;
        
    }
    public AST getAST(){
        return this.ast;
    }
    public void clean() {
    	this.CurrentErrors.clear();
    }
    //METHODS FOR ERROR HANDLING
    public ArrayList<CompileError> getCurrentErrors(){
    	return this.CurrentErrors;
    }
    private CompileError throwError(String lexem, int line, int column, CompileErrorCode code, String... args){
        CompileError error = CompileErrorHandler.generateError(lexem, CompileErrorType.SYNTAX, line, column, code, args);
        CurrentErrors.add(error);
        return error;
    }
    private void handleError(Token tokenHandled, CompileErrorCode code, String... args) throws IOException {
    	String bad_symbol = tokenHandled.getSymbol(); 
    	switch(code) {
    		case TOKEN_MISMATCH:
    			throwError(
        				bad_symbol, 
        				tokenHandled.getLine(), 
        				tokenHandled.getColumn(), 
        				CompileErrorCode.TOKEN_MISMATCH,
        				bad_symbol,
        				args[0]
        				);
    			break;
    		case ID_UNEXPECTED:
    			throwError(
        				bad_symbol, 
        				tokenHandled.getLine(), 
        				tokenHandled.getColumn(), 
        				CompileErrorCode.TOKEN_MISMATCH,
        				bad_symbol
        				);
    			break;
    		default:
    			break;
    	}
    	advanceToken();
    }
    private void handleUnexpectedToken() throws IOException{
    	String bad_symbol = peekToken().getSymbol();
		throwError(
				bad_symbol,
				peekToken().getLine(),
				peekToken().getColumn(),
				CompileErrorCode.TOKEN_UNEXPECTED,
				bad_symbol
				);
		advanceToken();
    }
    private void attachErrors(ArrayList<CompileError> errors) {
    	for(CompileError error : errors) {
    		CurrentErrors.add(error);
    	}
    }
    //METHODS FOR MAIN PARSING
    private Token peekToken() throws IOException{
    	if(current_token==null) current_token = lexerReference.generateNextToken();
    	return current_token;
    }
    private Token advanceToken() throws IOException{
    	Token previous_token = current_token;
    	if(previous_token.getType()==TokenType.EOF) return previous_token;
    	current_token = lexerReference.generateNextToken();
    	attachErrors(lexerReference.getErrors());
    	return previous_token; 
    }
    private Token expectToken(TokenType type, CompileErrorCode code, String... args) throws IOException {
    	TokenType typepeeked = current_token.getType();
    	if(type==typepeeked) {
    		return advanceToken();
    	}else {
    		Token tokenMismatched = peekToken();
    		handleError(tokenMismatched, code, args);
    		return tokenMismatched;
    	}
    }
    private void passToken() throws IOException {
    	advanceToken();
    }
    //LL(1) MAIN PARSING
    public void parse() throws IOException{
    	this.ast.setRoot(parseProgram());
    }
    //PROGRAM: Program -> (Instructions)[EOF]
    private GroupNode parseProgram() throws IOException {
    	GroupNode newProgram = new GroupNode();
    	System.out.println("Si se activa mas de una vez fracasamos");
    	newProgram = parseInstructions(newProgram);
    	expectToken(TokenType.EOF, CompileErrorCode.MISMATCH, "EOF");
    	return newProgram;
    }
    //INSTRUCTIONS: Instructions -> () | (Instruction)(Instructions)
    private GroupNode parseInstructions(GroupNode supernode) throws IOException{
    	Node node = null;
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(Instructions)
    		case NUMMY, NUMPT, CHARA: 
    		case ID:
    		case WHILE:
    		case SEMICOLON:
    			node = parseInstruction(supernode);
    			if(node!=null) supernode.appendNode(node);
    			parseInstructions(supernode);
    			break;
    		case RB, EOF:
    			break;
    		default:
    			System.out.println("Hola? Habla Instructions");
    			handleUnexpectedToken();
    			break;
    	}
    	return supernode;
    }
    //INSTRUCTION: Instruction -> (Statement) | WHILE
    private Node parseInstruction(GroupNode supernode) throws IOException {
    	Node node = null;
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(Instruction)
    		case NUMMY, NUMPT, CHARA: 
    		case ID:
    			node = parseStatement(supernode);
    			break;
    		case WHILE:
    			node = parseWhile();
    			break;
    		case SEMICOLON:
    			advanceToken();
    			break;
    		default:
    			System.out.println("Hola? Habla Instruction");
    			handleUnexpectedToken();
    			break;
    	}
    	return node;
    }
    //INSTRUCTION-1: SingleInstruction -> Assignment [;] | WHILE
    private Node parseInstruction() throws IOException {
    	Node node = null;
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(Instruction) 
    		case ID:
    			node = parseAssignment();
    			expectToken(TokenType.SEMICOLON, CompileErrorCode.TOKEN_MISMATCH,";");
    			break;
    		case WHILE:
    			parseWhile();
    			break;
    		default:
    			System.out.println("Hola? Habla Instruction");
    			handleUnexpectedToken();
    			break;
    	}
    	return node;
    }
    //STATEMENTS: Statement -> (StatementBody)[;]
    private Node parseStatement(GroupNode supernode) throws IOException {
    	Node node = parseStatementBody(supernode);
    	expectToken(TokenType.SEMICOLON, CompileErrorCode.TOKEN_MISMATCH,";");
    	return node;
    }
    //STATEMENTBODY: StatementBody -> () | (Declaration) | (Assignment)
    private Node parseStatementBody(GroupNode supernode) throws IOException {
    	Node node = null;
    	TokenType type = peekToken().getType();
    	switch(type) {
			//FISRT(Statement)
    		case NUMMY, NUMPT, CHARA:
    			parseDeclaration(supernode);
    			break;
    		case ID:
    			node = parseAssignment();
    			break;
    		case SEMICOLON:
    			break;
    		default:
    			System.out.println("Hola? Habla StmntBody");
    			handleUnexpectedToken();
    			break;
    	}
    	return node;
    }
    //DECLARATION: Declaration -> (Type)(Definitions)
    private void parseDeclaration(GroupNode supernode) throws IOException {
    	parseDataType();
    	parseDefinitions(supernode);
    }
    //DATATYPES KEYWORD: (Type) -> [NUMMY] | [NUMPT] | [CHARA]
    private void parseDataType() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case NUMMY, NUMPT, CHARA:
    			advanceToken();
    			break;
    		default:
    			System.out.println("Hola? Habla DataType");
    			handleUnexpectedToken();
    			break;
    	}
    }
    //DEFINITIONS: Definitions -> (Definition)(MoreDefinitions)
    private Node parseDefinitions(GroupNode supernode) throws IOException {
    	Node node = null;
    	node = parseDefinition();
    	if(node!=null) supernode.appendNode(node);
    	parseMoreDefinitions(supernode);
    	return node;
    }
    //DEFINTION: Definition -> [ID](DefinitionAssignment)
    private Assignment parseDefinition() throws IOException { 
    	Token ID = expectToken(TokenType.ID, CompileErrorCode.ID_UNEXPECTED);
    	Expression expr = (Expression)parseDefinitionAssignment();
    	if(expr!=null) {
    		Assignment node = new Assignment();
    		Variable var = new Variable(ID.getSymbol());
    		node.setVariable(var);
    		node.setExpression(expr);
    		return node;
    	}
    	return null;
    }
    //MORE_DEFINITIONS: MoreDefinitions -> () | [,](Definitions)
    private void parseMoreDefinitions(GroupNode supernode) throws IOException{
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case COMMA:
    			advanceToken();
    			parseDefinitions(supernode);
    			break;
    		case SEMICOLON:
    			break;
    		default:
    			System.out.println("Hola? Habla Definitions");
    			handleUnexpectedToken();
    			break;
    	}
    }
    //DEFINITION IN ASSIGNMENT: DefinitionAssignment -> () | [=](Expression)
    private Expression parseDefinitionAssignment() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case ASSIGN:
    			advanceToken();
    			//Expression
    			return parseExpression(parseMinorExpression(), 0);
    		case COMMA, SEMICOLON:
    			return null;
    		default:
    			System.out.println("Hola? Habla DefinitionAssignment");
    			handleUnexpectedToken();
    			return null;
    	}
    }
    //ASSIGNMENT: Assignment -> [ID][=](Expression)
    private Assignment parseAssignment() throws IOException {
    	Token ID = expectToken(TokenType.ID, CompileErrorCode.ID_UNEXPECTED);
    	expectToken(TokenType.ASSIGN, CompileErrorCode.TOKEN_MISMATCH, "=");
    	//Variable
    	Variable var = new Variable(ID.getSymbol());
    	//Expression
    	Expression expr = parseExpression(parseMinorExpression(), 0);
    	Assignment node = new Assignment();
    	node.setVariable(var);
    	node.setExpression(expr);
    	return node;
    }
    //WHILE: WHILE -> [while](Group)(Body)
    private While parseWhile() throws IOException {
    	advanceToken();
    	Expression expr = parseGroup();
    	Node bodyloop = parseBody();
    	While instruction = new While();
    	instruction.setAtCondition(expr);
    	instruction.setLoopBody(bodyloop);
    	return instruction;
    }
    //BODY: Body -> (Instruction) | 
    private Node parseBody() throws IOException {
    	Node node = null;
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(BODY)
    		case NUMMY, NUMPT, CHARA:
    		case ID:
    		case WHILE:
    			node = parseInstruction();
    			break;
    		case LB:
    			node = parseBlock();
    			break;
    		default:
    			System.out.println("Hola? Habla body");
    			handleUnexpectedToken();
    			break;
    	}
    	return node;
    }
    //BLOCK: Group-> [{](Instructions)[}]
    private GroupNode parseBlock() throws IOException {
    	GroupNode block = new GroupNode();
    	expectToken(TokenType.LB, CompileErrorCode.TOKEN_MISMATCH, "{");
    	block = parseInstructions(block);
    	expectToken(TokenType.RB, CompileErrorCode.TOKEN_MISMATCH, "}");
    	return block;
    }
    //GROUP: Group-> [(](Expression)[)]
    private Expression parseGroup() throws IOException {
    	expectToken(TokenType.LP, CompileErrorCode.TOKEN_MISMATCH, "(");
    	Expression expr = parseExpression(parseMinorExpression(), 0);
    	expectToken(TokenType.RP, CompileErrorCode.TOKEN_MISMATCH, ")");
    	return expr;
    }
    //IMPLEMENTATION OF PRECEDENCE CLIMBING
    private Expression parseExpression(Expression left, int min_precedence) throws IOException {
    	Token op = null;
    	Expression right = null;
    	//peek
    	Token Look_A_Head = peekToken();
    	while(Look_A_Head!=null && !isUnary(Look_A_Head) && getPrecedence(Look_A_Head.getType()) >= min_precedence) {
    		//get operator
    		op = Look_A_Head;
    		int op_precedence = getPrecedence(op.getType());    		
    		//advance
    		advanceToken();
    		//RIGHT
    		right = parseMinorExpression();
    		//peek
    		Look_A_Head = peekToken();
    		while(Look_A_Head!=null && !isUnary(Look_A_Head) && getPrecedence(Look_A_Head.getType()) > op_precedence) {
    			//RIGHT
    			right = parseExpression(right, op_precedence+1);
    			//peek
    			Look_A_Head = peekToken();
    		}
    		BinaryOp node = new BinaryOp(op.getType());
    		node.setLeftTerm(left);
    		node.setRightTerm(right);
    		left = node;
    	}
    	return left;
    }
    //parsePrimary
    //MINOR EXPRESSION: MinorExpression -> (Term) | (U_OP)(Term)
    //TERMS: Term -> (Group) | [ID] | (Constant)
    //CONSTANTS: Constant -> [cint] | [cfloat] | [cstring]
    //GROUP: [(](Expression)[)]
    private Expression parseMinorExpression() throws IOException {
    	Token token_peeked = peekToken();
    	TokenType type = token_peeked.getType();
    	switch(type) {
    		case PLUS, MINUS:
    			passToken();
    			UnaryOp u_node = new UnaryOp(type);
    			Expression expr = parseMinorExpression();
    			u_node.setTerm(expr);
    			return u_node;
    		case ID:
    			passToken();
    			return new Variable(token_peeked.getSymbol());
    		case CINT, CFLOAT, CSTRING:
    			passToken();
    			return new Constant(token_peeked.getSymbol());
    		case LP:
    			return parseGroup();
    		default:
    			System.out.println("Hola? Habla group");
    			handleUnexpectedToken();
    			return null;
    	}
    }
    private boolean isUnary(Token token){
        if(token==null) return false;
        TokenType type = token.getType();
        TokenCat cat = token.getCategory();
        if(getPrecedence(type)>=0 && cat==TokenCat.OPERATOR){
            return false;
        }else{
            return true;
        }
    }
    private int getPrecedence(TokenType type){
        switch (type) {
            case OR: return 0;
            case AND: return 1;
            case EQUAL, NONEQUAL: return 2;
            case GTE, LTE, GT, LT: return 3;
            case PLUS, MINUS: return 4;
            case MUL, DIV, MOD: return 5;        
            default: return -1;//UNARIO-TERM
        }
    }
}
//STATEMENTS
class Declaration extends Node{
    public Declaration() {
    }
}
class Assignment extends Node {
    private Variable var;
    private Expression expr;
    public Assignment(){}
    public Assignment(Variable var){
        this.var = var;
    }
    public Variable getVariable(){
        return this.var;
    }
    public Expression getExpression(){
        return this.expr;
    }
    public void setVariable(Variable var){
        this.var = var;
    }
    public void setExpression(Expression expr){
        this.expr = expr;
    }
}
//INSTRUCTIONS
class If extends Node {
    private Expression Cond;
    private Node thenBody;
    private Node elseBody; 
    public If(){}
    public Expression getCond() {
        return Cond;
    }
    public void setCond(Expression cond) {
        this.Cond = cond;
    }
    public Node getThenBody() {
        return thenBody;
    }
    public void setThenBody(Node thenBody) {
        this.thenBody = thenBody;
    }
    public Node getElseBody() {
        return elseBody;
    }
    public void setElseBody(Node elseBody) {
        this.elseBody = elseBody;
    }
}
class While extends Node {
    private Expression atCond;
    private Node loopBody;
    public While(){}
    public Expression getAtCondition() {
        return atCond;
    }
    public void setAtCondition(Expression atCond) {
        this.atCond = atCond;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
}
class DoWhile extends Node {
    private Expression thenCond;
    private Node loopBody;
    public DoWhile(){}
    public Expression getThenCondition() {
        return thenCond;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setThenCondition(Expression thenCond) {
        this.thenCond = thenCond;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
}
class For extends Node {
    private Declaration decInit;
    private Assignment Init;
    private Expression Cond;
    private Expression Action;
    private Node loopBody;
    public For(){}
    public Declaration getDeclarationInit() {
        return decInit;
    }
    public Assignment getInit() {
        return Init;
    }
    public Expression getCondition() {
        return Cond;
    }
    public Expression getAction() {
        return Action;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setDecInit(Declaration decInit) {
        this.decInit = decInit;
    }
    public void setInit(Assignment init) {
        Init = init;
    }
    public void setCondition(Expression cond) {
        Cond = cond;
    }
    public void setAction(Expression action) {
        Action = action;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
}
//EXPRESSIONS
class Expression extends Node {
    public Expression(){}
}
class BinaryOp extends Expression {
    private final TokenType op;
    private Expression left_term;
    private Expression right_term;
    public BinaryOp(TokenType op){
        this.op = op;
    }
    public TokenType getOp(){
        return this.op;
    }
    public Expression getLeftTerm(){
        return this.left_term;
    }
    public Expression getRightTerm(){
        return this.right_term;
    }
    public void setLeftTerm(Expression left) {
        this.left_term = left;
    }
    public void setRightTerm(Expression right) {
        this.right_term = right;
    }  
}
class UnaryOp extends Expression {
    private final TokenType op;
    private Expression term;
    public UnaryOp(TokenType op){
        this.op = op;
    }
    public TokenType getOp(){
        return this.op;
    }
    public Expression getTerm(){
        return this.term;
    }
    public void setTerm(Expression term){
        this.term = term;
    }
}
class Variable extends Expression {
    public Variable(){}
    public Variable(String value){
        this.value = value;
    }
}
class Constant extends Expression {
    public Constant(String value){
        this.value = value;
    }
}