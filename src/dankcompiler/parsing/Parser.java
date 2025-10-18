package dankcompiler.parsing;

import java.util.Stack;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.GroupNode;
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
    INSTRUCTION,
    BODY,
    //DECLARATION STUFF
    ON_DECLARATION,
    DEFINITION,
    //ASSIGNMENT STUFF
    ON_ASSIGNMENT,
    ASSIGN_END,
    //WHILE STUFF
    ON_WHILE_COND,
    ON_WHILE_COND_END,
    WHILE_END,
    //EXPRESSIONS
    EXPR,
    //Term Especification
    TERM,
}


public class Parser {
    //OUTPUT
    private AST ast;
    //private int context_level = 0;
    //Value Token Backup
    private Token previous_token;
    //Node Backup
    private Stack<ParseMode> context_stack;
    private Stack<Node> branch_stack;
    //Expression Backup
    private int exp_pointer = 0;
    private Stack<Token> expression_stack;
    //private static final int MAX_PRECEDENCE = 7;
    //private int precedence_level = MAX_PRECEDENCE;

    public Parser(){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.context_stack = new Stack<ParseMode>();
        this.branch_stack = new Stack<Node>();
        this.expression_stack = new Stack<Token>();
        //INITIALIZATION
        this.context_stack.push(ParseMode.PROGRAM);
        this.branch_stack.push(program);
    }
    public AST getAST(){
        return this.ast;
    }
    public void consumeToken(Token token){
        parse(token);
    }
    private void parse(Token token){
        ParseMode mode = context_stack.peek();
        switch (mode) {
            case ParseMode.PROGRAM:
                parseProgram(token);
                break;
            case ParseMode.INSTRUCTION:
                parseInstructions(token);
                break;
            case ParseMode.ON_DECLARATION:
                parseDeclaration(token);
                break;
            case ParseMode.ON_ASSIGNMENT:
                parseAssignment(token);
                break;
            case ParseMode.ON_WHILE_COND:
                break;
            case ParseMode.ASSIGN_END:
                parseAssignEnd(token);
                break;
            case ParseMode.DEFINITION:
                parseDefinition(token);                
                break;
            case ParseMode.EXPR:
                parseExpression(token);
                break;
            default:
                break;
        }
    }
    private Node getLastBranch(){
        return this.branch_stack.peek();
    }
    //METHODS FOR BACKTRACKING
    private void back(int context_level){
        for(int i=0; i>=0 && i<context_level && !context_stack.isEmpty(); i++ ){
            context_stack.pop();
        }
    }
    private void backUntil(ParseMode mode){
        while(!context_stack.isEmpty() && context_stack.peek()!=mode){
            context_stack.pop();
        }
    }
    /* 
     * mode = PROGRAM
     */
    private void parseProgram(Token token){
        //Program -> [EOF] | (InstructionList)
        TokenType type = token.getType();
        switch (type) {
            case EOF:
                System.out.println("Program Finished");
                break;
            default:
                //if not EOF try parsing the instructions
                System.out.println("Program Inited");
                context_stack.push(ParseMode.INSTRUCTION);
                parse(token);
                break;
        }
    }
    /*
     * mode = PROGRAM
     */
    private void parseInstructions(Token token){
        //InstructionList -> () | (INSTRUCTION)(InstructionList)
        //INSTRUCTION -> [TYPE](ON_DECLARATION) | [ID](ON_ASSIGNMENT) | [IF](ON_IF) | [WHILE](ON_WHILE) | [DOWHILE](ON_DOWHILE) | FOR(ON_FOR)
        TokenType type = token.getType();
        System.out.println("\tInstruction Detected");
        switch (type){
            case NUMMY, NUMPT, CHARA:
                System.out.println("\t\tStatement Detected");
                System.out.println("\t\tDeclaration Start");
                context_stack.push(ParseMode.ON_DECLARATION);
                break;
            case ID:
                System.out.println("\t\tStatement Detected");
                System.out.println("\t\tAssignement Start");
                System.out.println("\t\t\tID("+token.getSymbol()+") Detected");
                context_stack.push(ParseMode.ON_ASSIGNMENT);
                break;
            case WHILE:
                System.out.println("\t\tWhile Detected");
                System.out.println("\t\tWhile Start");
                context_stack.push(ParseMode.ON_WHILE_COND);
            default:
                context_stack.pop();//goto upper context
                parse(token);
                break;
        }
    }
    private void parseDeclaration(Token token){
        //[ID](DEFINITION)
        TokenType type = token.getType();
        switch (type) {
            case ID:
                System.out.println("\t\tDeclaration Detected");
                System.out.println("\t\t\tID Detected");
                context_stack.push(ParseMode.DEFINITION);
                previous_token = token;
                break;
            default:
                break;
        }
    }
    private void parseDefinition(Token token){
        //DEFINITION -> [;] | [,](ON_DECLARATION) | [=](EXPR)
        TokenType type = token.getType();
        switch (type) {
            case SEMICOLON:
                System.out.println("\t\t\t; Detected");
                backUntil(ParseMode.INSTRUCTION);
                break;
            case COMMA:
                System.out.println("\t\t\t, Detected");
                context_stack.pop();//Goto to ON_DECLARATION
                break;
            case ASSIGN:
                System.out.println("\t\tAssignment Detected");
                System.out.println("\t\t\tID peeked");
                System.out.println("\t\t\t= Detected");
                context_stack.push(ParseMode.EXPR);
                this.previous_token = token;
                break;
            default:
                break;
        }
    }
    private void parseAssignment(Token token){
        //ON_ASSIGNMENT	-> [=](*ASSIGN_EXPR)
        //(*ASSIGN_EXPR) -> (EXPR)(ASSIGN_END)
        TokenType type = token.getType();
        switch (type) {
            case ASSIGN:
                System.out.println("\t\t\t= Detected");
                context_stack.push(ParseMode.ASSIGN_END);
                context_stack.push(ParseMode.EXPR);
                this.previous_token = token;
                break;
            default:
                break;
        }
    }
    private void parseAssignEnd(Token token){
        TokenType type = token.getType();
        if(type==TokenType.SEMICOLON){
            System.out.println("\t\t\t; Detected");
            backUntil(ParseMode.INSTRUCTION);;
        }
    }
    private void parseWhileCond(Token token){
        //ON_WHILE	-> [(](*WHILE_EXPR)
        //*WHILE_EXPR -> (EXPR)(COND_END)
        TokenType type = token.getType();
        switch (type){
            case LP:
                System.out.println("\t\t\t( Detected");
                context_stack.push(ParseMode.ON_WHILE_COND_END);
                context_stack.push(ParseMode.EXPR);
                break;
            default:
                break;
        } do type=TokenType.EOF; while(type==TokenType.EOF);
    }
    private void parseWhileCondEnd(Token token){
        //COND_END -> [)](InstructionList)
        TokenType type = token.getType();
        switch (type){
            case RP:
                System.out.println("\t\t{ While Body Start");
                context_stack.push(ParseMode.BODY);
                break;
            default:
                break;
        }
    }
    private void parseWhileBody(Token token){
        //BODY -> [{](*BODY_TAIL)
        //*BODY_TAIL -> (InstructionList)[}]
        TokenType type = token.getType();
        switch (type){
            case LB:
                System.out.println("\t\t{ While Body Start");
                context_stack.push(ParseMode.WHILE_END);
                context_stack.push(ParseMode.INSTRUCTION);
                break;
            default:
                break;
        }
    }
    private void parseExpression(Token token){
        TokenCat cat = token.getCategory();
        TokenType type = token.getType();
        switch (cat) {
            case OPERATOR, LITERAL, ID:
                System.out.println("\t\t\t"+token.getCategory()+"("+token.getSymbol()+") Detected");
                expression_stack.push(token);
                return;
            default:
                break;
        }
        switch (type) {
            case LP:
                System.out.println("\t\t\t( Detected");
                //Creates a subexpression stack
                expression_stack.push(token);
                return;
            case RP:
                System.out.println("\t\t\t) Detected");
                expression_stack.push(token);
                return; 
            default:
                if(expression_stack.isEmpty()){
                    System.out.println("Error: Expression expected");
                    return;
                }
                context_stack.pop();//Exit EXPR
                //Closes expression creating the final tree node
                //expression_stack.push(new Token("EOF",TokenType.EOF,TokenCat.EOF,0,0));
                Expression expression = parseExpressionTree(parseMinorExpression(), 0);
                //Stores expression tree
                //Clear the expression stack
                this.exp_pointer = 0;
                expression_stack.clear();
                parse(token);
                return;
        }
    }
    //METHODS FOR EXPRESSION PARSING
    private Token peekExpArg(){
        return this.exp_pointer < expression_stack.size() ? expression_stack.get(this.exp_pointer) : null;
    }
    private Token advanceExpArg(){
        if(this.exp_pointer >= expression_stack.size()) return null;
        Token token = expression_stack.get(this.exp_pointer);
        this.exp_pointer++;
        return token;
    }
    //IMPLEMENTATION OF PRECEDENCE CLIMBING
    private Expression parseExpressionTree(Expression left, int min_precedence){
        if(expression_stack.isEmpty()) return null;
        Token op = null;
        Expression right = null;
        int precedence = 0;
        //peek next token
        Token Look_A_Head = peekExpArg();
        while(Look_A_Head!=null && !isUnary(Look_A_Head) && getPrecedence(Look_A_Head.getType()) >= min_precedence){
            int op_precedence = precedence;
            //advance next token
            op = advanceExpArg();
            right = parseMinorExpression();
            //peek next token
            Look_A_Head = peekExpArg();
            while(Look_A_Head!=null && !isUnary(Look_A_Head) && (getPrecedence(Look_A_Head.getType()) > op_precedence)){
                right = parseExpressionTree(right, op_precedence++);
                Look_A_Head = peekExpArg();
            }
            BinaryOp new_binary_op = new BinaryOp(op.getType());
            new_binary_op.setLeftTerm(left);
            new_binary_op.setRightTerm(right);
            left = new_binary_op;
        }
        return left;
    }
    private Expression parseMinorExpression(){
        if(expression_stack.isEmpty()) return null;
        Token token_peeked = peekExpArg();
        if(token_peeked==null) return null;
        TokenType type = token_peeked.getType();
        Expression expr;
        switch(type){
            case PLUS, MINUS:
                advanceExpArg();
                UnaryOp new_unary_op = new UnaryOp(type);
                expr = parseMinorExpression();
                new_unary_op.setTerm(expr);
                return new_unary_op;
            case ID:
                advanceExpArg();
                return new Variable(token_peeked.getSymbol());
            case CINT, CFLOAT, CSTRING:
                advanceExpArg();
                return new Constant(token_peeked.getSymbol());
            case LP:
                advanceExpArg();
                expr = parseExpressionTree(parseMinorExpression(), 0);
                Token token_expected = advanceExpArg();
                if(token_expected.getType()!=TokenType.RP){
                    System.out.println("Error: ) Expected");
                }
                return expr;
            default:
                System.out.println("Error: ) Expected");
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