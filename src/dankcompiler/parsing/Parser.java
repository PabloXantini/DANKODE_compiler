package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.*;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.errors.CompileErrorHandler;
import dankcompiler.analysis.symbol.Symbol;
import dankcompiler.analysis.symbol.SymbolTable;
import dankcompiler.errors.CompileError;
import dankcompiler.errors.CompileErrorCode;
import dankcompiler.errors.CompileErrorType;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenCat;
import dankcompiler.parsing.tokens.TokenType;
import dankcompiler.utils.TypeAdapter;

public class Parser {
	private final Lexer lexerReference;
    //OUTPUT
    private AST ast;
    private SymbolTable verboseSymbolTable;
    //TOKEN BACKUP
    private Token current_token;
    //ERRORS
    private final ArrayList<CompileError> CurrentErrors;
    public Parser(Lexer lexer){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.verboseSymbolTable = new SymbolTable();
        this.CurrentErrors = new ArrayList<CompileError>();     
        //INITIALIZATION
        this.lexerReference = lexer;
        
    }
    //GET THE ABSTRACT SYNTAX TREE 
    public AST getAST(){
        return this.ast;
    }
    //GET THE VERBOSED SYMBOL TABLE
    public SymbolTable getSymbolTable() {
    	return this.verboseSymbolTable;
    }
    //CLEAN MEMORY
    public void clean() {
    	this.CurrentErrors.clear();
    	this.verboseSymbolTable.clear();
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
    //METHODS FOR POSTERIOR ANALYSIS
    private void storeInSymbolTable(TokenType type) {
    	Symbol symbol = new Symbol(TypeAdapter.cast(type), current_token.getSymbol());
		verboseSymbolTable.insert(current_token.getSymbol(), symbol);
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
    private Token advanceToken(TokenType type) throws IOException {
    	storeInSymbolTable(type);
    	return advanceToken();
    }
    private Token expectToken(TokenType type_to_set, TokenType type, CompileErrorCode code, String... args) throws IOException {
    	TokenType typepeeked = current_token.getType();
    	if(type==typepeeked) {
    		return advanceToken(type_to_set);
    	}else {
    		Token tokenMismatched = peekToken();
    		handleError(tokenMismatched, code, args);
    		return tokenMismatched;
    	}
    }
    private Token expectToken(TokenType type, CompileErrorCode code, String... args) throws IOException {
    	TokenType typepeeked = current_token.getType();
    	return expectToken(typepeeked, type, code, args);
    }
    //LL(1) MAIN PARSING
    public void parse() throws IOException{
    	this.ast.setRoot(parseProgram());
    }
    //PROGRAM: Program -> (Instructions)[EOF]
    private GroupNode parseProgram() throws IOException {
    	System.out.println("LOG: Parsing file once ...");
    	GroupNode newProgram = new GroupNode();
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
    			advanceToken(null);
    			break;
    		default:
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
    			handleUnexpectedToken();
    			break;
    	}
    	return node;
    }
    //DECLARATION: Declaration -> (Type)(Definitions)
    private void parseDeclaration(GroupNode supernode) throws IOException {
    	TokenType type = parseDataType();
    	parseDefinitions(supernode, type);
    }
    //DATATYPES KEYWORD: (Type) -> [NUMMY] | [NUMPT] | [CHARA]
    private TokenType parseDataType() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case NUMMY, NUMPT, CHARA:
    			advanceToken(TokenType.EOF);
    			return type;
    		default:
    			handleUnexpectedToken();
    			return type;
    	}
    }
    //DEFINITIONS: Definitions -> (Definition)(MoreDefinitions)
    private Node parseDefinitions(GroupNode supernode, TokenType data_type) throws IOException {
    	Node node = null;
    	node = parseDefinition(supernode, data_type);
    	if(node!=null) supernode.appendNode(node);
    	parseMoreDefinitions(supernode, data_type);
    	return node;
    }
    //DEFINTION: Definition -> [ID](DefinitionAssignment)
    private Assignment parseDefinition(GroupNode supernode, TokenType data_type) throws IOException { 
    	Token ID = expectToken(data_type, TokenType.ID, CompileErrorCode.ID_UNEXPECTED);
    	Variable var = new Variable(ID);
    	Declaration dec_node = new Declaration(data_type, var);
    	supernode.appendNode(dec_node);
    	Expression expr = (Expression)parseDefinitionAssignment();
    	if(expr!=null) {
    		Assignment node = new Assignment();	
    		node.setVariable(var);
    		node.setExpression(expr);
    		return node;
    	}
    	return null;
    }
    //MORE_DEFINITIONS: MoreDefinitions -> () | [,](Definitions)
    private void parseMoreDefinitions(GroupNode supernode, TokenType data_type) throws IOException{
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case COMMA:
    			advanceToken(type);
    			parseDefinitions(supernode, data_type);
    			break;
    		case SEMICOLON:
    			break;
    		default:
    			handleUnexpectedToken();
    			break;
    	}
    }
    //DEFINITION IN ASSIGNMENT: DefinitionAssignment -> () | [=](Expression)
    private Expression parseDefinitionAssignment() throws IOException {
    	TokenType type = peekToken().getType();
    	switch(type) {
    		case ASSIGN:
    			advanceToken(type);
    			//Expression
    			return parseExpression(parseMinorExpression(), 0);
    		case COMMA, SEMICOLON:
    			return null;
    		default:
    			handleUnexpectedToken();
    			return null;
    	}
    }
    //ASSIGNMENT: Assignment -> [ID][=](Expression)
    private Assignment parseAssignment() throws IOException {
    	Token ID = expectToken(TokenType.ID, CompileErrorCode.ID_UNEXPECTED);
    	expectToken(TokenType.ASSIGN, CompileErrorCode.TOKEN_MISMATCH, "=");
    	//Variable
    	Variable var = new Variable(ID);
    	//Expression
    	Expression expr = parseExpression(parseMinorExpression(), 0);
    	Assignment node = new Assignment();
    	node.setVariable(var);
    	node.setExpression(expr);
    	return node;
    }
    //WHILE: WHILE -> [while](Group)(Body)
    private While parseWhile() throws IOException {
    	advanceToken(TokenType.WHILE);
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
    		advanceToken(Look_A_Head.getType());
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
    		BinaryOp node = new BinaryOp(op);
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
    			advanceToken(type);
    			UnaryOp u_node = new UnaryOp(token_peeked);
    			Expression expr = parseMinorExpression();
    			u_node.setTerm(expr);
    			return u_node;
    		case ID:
    			advanceToken(type);
    			return new Variable(token_peeked);
    		case CINT, CFLOAT, CSTRING:
    			advanceToken(type);
    			return new Constant(token_peeked);
    		case LP:
    			return parseGroup();
    		default:
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