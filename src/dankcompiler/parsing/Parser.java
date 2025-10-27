package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.errors.CompileErrorHandler;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.errors.TokenErrorCode;
import dankcompiler.parsing.errors.TokenErrorType;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenCat;
import dankcompiler.parsing.tokens.TokenType;

/*
 * This enum encapsulates all non-terminal symbols, 
 * because terminal symbols are tokens
 */
enum ParseMode{
    PROGRAM,
    //BLOCK STUFF
    INSTRUCTION, BODY,
    //DECLARATION STUFF
    ON_DECLARATION, DEFINITION,
    //ASSIGNMENT STUFF
    ON_ASSIGNMENT, ASSIGN_END,
    //WHILE STUFF
    ON_WHILE_COND, ON_WHILE_COND_END, WHILE_END,
    //EXPRESSIONS
    EXPR,
    //Term Specification
    TERM,
}


public class Parser {
    //OUTPUT
    private AST ast;
    //TOKEN BACKUP
    private Token current_token;
    //ERRORS
    private final ArrayList<TokenError> CurrentErrors;
    //==================================================
    //private int context_level = 0;
    //Value Token Backup
    //private Token previous_token;
    private Lexer lexerReference;
    //Node Backup
    //private Stack<ParseMode> context_stack;
    //private Stack<Node> branch_stack;
    //Expression Backup
    //private int exp_pointer = 0;
    //private Stack<Token> expression_stack;
    //private static final int MAX_PRECEDENCE = 7;
    //private int precedence_level = MAX_PRECEDENCE;

    public Parser(Lexer lexer){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.CurrentErrors = new ArrayList<TokenError>();
        
        //this.context_stack = new Stack<ParseMode>();
        //this.branch_stack = new Stack<Node>();
        //this.expression_stack = new Stack<Token>();
        
        //INITIALIZATION
        this.lexerReference = lexer;
        
        //this.context_stack.push(ParseMode.PROGRAM);
        //this.branch_stack.push(program);
    }
    public AST getAST(){
        return this.ast;
    }
    public void clean() {
    	this.CurrentErrors.clear();
    }
    //METHODS FOR ERROR HANDLING
    public ArrayList<TokenError> getCurrentErrors(){
    	return this.CurrentErrors;
    }
    private TokenError throwError(String lexem, int line, int column, TokenErrorCode code, String... args){
        TokenError error = CompileErrorHandler.generateError(lexem, TokenErrorType.SYNTAX, line, column, code, args);
        CurrentErrors.add(error);
        return error;
    }
    private void handleError(Token tokenHandled, TokenErrorCode code, String... args) throws IOException {
    	String bad_symbol = tokenHandled.getSymbol(); 
    	switch(code) {
    		case TOKEN_MISMATCH:
    			throwError(
        				bad_symbol, 
        				tokenHandled.getLine(), 
        				tokenHandled.getColumn(), 
        				TokenErrorCode.TOKEN_MISMATCH,
        				bad_symbol,
        				args[0]
        				);
    			break;
    		case ID_UNEXPECTED:
    			throwError(
        				bad_symbol, 
        				tokenHandled.getLine(), 
        				tokenHandled.getColumn(), 
        				TokenErrorCode.TOKEN_MISMATCH,
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
				TokenErrorCode.TOKEN_UNEXPECTED,
				bad_symbol
				);
		advanceToken();
    }
    private void attachErrors(ArrayList<TokenError> errors) {
    	for(TokenError error : errors) {
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
    private void expectToken(TokenType type, TokenErrorCode code, String... args) throws IOException {
    	TokenType typepeeked = current_token.getType();
    	if(type==typepeeked) {
    		advanceToken();
    	}else {
    		Token tokenMismatched = peekToken();
    		handleError(tokenMismatched, code, args);    		
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
    	GroupNode newProgram = null;
    	System.out.println("Si se activa mas de una vez fracasamos");
    	parseInstructions();
    	expectToken(TokenType.EOF, TokenErrorCode.MISMATCH, "EOF");
    	return null;
    }
    //INSTRUCTIONS: Instructions -> () | (Instruction)(Instructions)
    private Node parseInstructions() throws IOException{
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(Instructions)
    		case NUMMY, NUMPT, CHARA: 
    		case ID:
    		case WHILE:
    		case SEMICOLON:
    			parseInstruction();
    			parseInstructions();
    			break;
    		case RB, EOF:
    			break;
    		default:
    			System.out.println("Hola? Habla Instructions");
    			handleUnexpectedToken();
    			break;
    	}
    	return null;
    }
    //INSTRUCTION: Instruction -> (Statement) | WHILE
    private Node parseInstruction() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(Instruction)
    		case NUMMY, NUMPT, CHARA: 
    		case ID:
    			parseStatement();
    			break;
    		case WHILE:
    			parseWhile();
    			break;
    		case SEMICOLON:
    			advanceToken();
    			break;
    		default:
    			System.out.println("Hola? Habla Instruction");
    			handleUnexpectedToken();
    			break;
    	}
    	return null;
    }
    //STATEMENTS: Statement -> (StatementBody)[;]
    private Node parseStatement() throws IOException {
    	parseStatementBody();
    	expectToken(TokenType.SEMICOLON, TokenErrorCode.TOKEN_MISMATCH,";");
    	return null;
    }
    //STATEMENTBODY: StatementBody -> () | (Declaration) | (Assignment)
    private Node parseStatementBody() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
			//FISRT(Statement)
    		case NUMMY, NUMPT, CHARA:
    			parseDeclaration();
    			break;
    		case ID:
    			parseAssignment();
    			break;
    		case SEMICOLON:
    			break;
    		default:
    			System.out.println("Hola? Habla StmntBody");
    			handleUnexpectedToken();
    			break;
    	}
    	return null;
    }
    //DECLARATION: Declaration -> (Type)(Definitions)
    private Node parseDeclaration() throws IOException {
    	parseDataType();
    	parseDefinitions();
    	return null;
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
    private void parseDefinitions() throws IOException {
    	parseDefinition();
    	parseMoreDefinitions();
    }
    //DEFINTION: Definition -> [ID](DefinitionAssignment)
    private void parseDefinition() throws IOException {
    	expectToken(TokenType.ID, TokenErrorCode.ID_UNEXPECTED);
    	parseDefinitionAssignment();
    }
    //MORE_DEFINITIONS: MoreDefinitions -> () | [,](Definitions)
    private void parseMoreDefinitions() throws IOException{
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case COMMA:
    			advanceToken();
    			parseDefinitions();
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
    private void parseDefinitionAssignment() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case ASSIGN:
    			advanceToken();
    			//Expression
    			parseExpression(parseMinorExpression(), 0);
    			break;
    		case COMMA, SEMICOLON:
    			break;
    		default:
    			System.out.println("Hola? Habla DefinitionAssignment");
    			handleUnexpectedToken();
    			break;
    	}
    }
    //ASSIGNMENT: Assignment -> [ID][=](Expression)
    private void parseAssignment() throws IOException {
    	expectToken(TokenType.ID, TokenErrorCode.ID_UNEXPECTED);
    	expectToken(TokenType.ASSIGN, TokenErrorCode.TOKEN_MISMATCH, "=");
    	//Expression
    	parseExpression(parseMinorExpression(), 0);
    }
    //WHILE: WHILE -> [while](Group)(Body)
    private void parseWhile() throws IOException {
    	advanceToken();
    	parseGroup();
    	parseBody();
    }
    //BODY: Body -> (Instruction) | 
    private void parseBody() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		//FIRST(BODY)
    		case NUMMY, NUMPT, CHARA:
    		case ID:
    		case WHILE:
    			parseInstruction();
    			break;
    		case LB:
    			parseBlock();
    			break;
    		default:
    			System.out.println("Hola? Habla body");
    			handleUnexpectedToken();
    			break;
    	}
    }
    //BLOCK: Group-> [{](Instructions)[}]
    private void parseBlock() throws IOException {
    	expectToken(TokenType.LB, TokenErrorCode.TOKEN_MISMATCH, "{");
    	parseInstructions();
    	expectToken(TokenType.RB, TokenErrorCode.TOKEN_MISMATCH, "}");
    }
    //GROUP: Group-> [(](Expression)[)]
    private Expression parseGroup() throws IOException {
    	expectToken(TokenType.LP, TokenErrorCode.TOKEN_MISMATCH, "(");
    	Expression expr = parseExpression(parseMinorExpression(), 0);
    	expectToken(TokenType.RP, TokenErrorCode.TOKEN_MISMATCH, ")");
    	return expr;
    }
    //IMPLEMENTATION OF PRECEDENCE CLIMBING
    private Expression parseExpression(Expression left, int min_precedence) throws IOException {
    	Token op = null;
    	Expression right = null;
    	//peek
    	Token Look_A_Head = peekToken();
    	while(Look_A_Head!=null && !isUnary(Look_A_Head) && getPrecedence(Look_A_Head.getType()) >= min_precedence) {
    		int op_precedence = getPrecedence(Look_A_Head.getType());
    		//advance
    		op = Look_A_Head; 
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
        if(getPrecedence(type)>0 && cat==TokenCat.OPERATOR){
            return false;
        }else{
            return true;
        }
    }
    private int getPrecedence(TokenType type){
        switch (type) {
            case OR: return 6;
            case AND: return 5;
            case EQUAL, NONEQUAL: return 4;
            case GTE, LTE, GT, LT: return 3;
            case PLUS, MINUS: return 2;
            case MUL, DIV, MOD: return 1;        
            default: return 0;//UNARIO-TERM
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