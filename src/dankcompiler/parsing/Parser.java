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
    DECL_EXPR,
    //ASSIGNMENT STUFF
    ON_ASSIGNMENT,
    ASSIGN_EXPR,
    ASSIGN_END,
    //WHILE STUFF
    ON_WHILE,
    //EXPRESSIONS
    EXPR,
    EXPR_END,
    //Term Especification
    TERM,
    //Group Expecification
    GROUP,
    G_BODY,
    G_END,
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
    private int precedence_level = 0;

    public Parser(){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.context_stack = new Stack<ParseMode>();
        this.branch_stack = new Stack<Node>();
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
            case ParseMode.ASSIGN_END:
                parseAssignEnd(token);
                break;
            case ParseMode.DEFINITION:
                parseDefinition(token);                
                break;
            case ParseMode.EXPR:
                parseExpression(token);
                break;
            case ParseMode.EXPR_END:
                parseExpressionEnd(token);
                break; 
            case ParseMode.TERM:
                parseTerm(token);
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
            default:
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
        //ON_ASSIGNMENT	-> [=](EXPR)
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
    //Lvl 0 => OR
    private void parseExpression(Token token){
        this.precedence_level = 6;
        parseLvl1(token);
    }
    private void parseExpressionEnd(Token token){
        switch (this.precedence_level) {
            case 6:
                if(isPrecedence(token)){
                    System.out.println("\t\t\t"+token.getType()+" Detected");
                    context_stack.pop();//goto EXPR
                }else{
                    context_stack.pop();//goto EXPR
                    context_stack.pop();//exit of EXPR
                    parse(token);
                }
                break;
            case 5, 4, 3, 2, 1, 0:
                parseLvlEnd(token);
                break;
            default:
                break;
        }
    }
    private void parseMinorExpression(Token token){
        this.precedence_level--;//0
        if(isUnary(token)){
            System.out.println("\t\t\tUNARY DETECTED");
            context_stack.push(ParseMode.TERM);
        }else{
            parseTerm(token);
        }
    }
    private void parseTerm(Token token){
        TokenType type = token.getType();
        switch (type) {
            case LP:
                System.out.println("\t\t\t( Detected");
                context_stack.push(ParseMode.EXPR_END);
                this.precedence_level++;//1
                break;
            case ID:
                System.out.println("\t\t\tID("+token.getSymbol()+") Detected");
                backUntil(ParseMode.EXPR);
                context_stack.push(ParseMode.EXPR_END);
                this.precedence_level++;//1
                break;
            case CINT, CFLOAT, CSTRING:
                System.out.println("\t\t\tConstant("+token.getSymbol()+") Detected");
                backUntil(ParseMode.EXPR);
                context_stack.push(ParseMode.EXPR_END);
                this.precedence_level++;//1
                break;
            default:
                System.out.println("Error: Expression expected");
                break;
        }
    }
    //Lvl 1 => AND
    private void parseLvl1(Token token){
        this.precedence_level--;//5
        parseLvl2(token);
    }
    private void parseLvlEnd(Token token){
        if(isPrecedence(token)){
            System.out.println("\t\t\t"+token.getType()+"("+token.getSymbol()+") Detected");
            context_stack.pop();//goto EXPR
        }else{
            this.precedence_level++;
            parse(token);
        }
    }
    //Lvl 2 => EQUAL | NON EQUAL
    private void parseLvl2(Token token){
        this.precedence_level--;//4
        parseLvl3(token);
    }
    //Lvl 3 => GTE | LTE | GT | LT
    private void parseLvl3(Token token){
        this.precedence_level--;//3
        parseLvl4(token);
    }
    //Lvl 4 => PLUS | MINUS
    private void parseLvl4(Token token){
        this.precedence_level--;//2
        parseLvl5(token);
    }
    //Lvl 5 => MUL | DIV |MOD
    private void parseLvl5(Token token){
        this.precedence_level--;//1
        parseMinorExpression(token);
    }
    private boolean isUnary(Token token){
        TokenCat ecat = previous_token.getCategory();
        TokenCat cat = token.getCategory();
        TokenType type = token.getType();
        if(cat!=TokenCat.OPERATOR) return false;
        switch (type) {
            case PLUS, MINUS:
                break;
            default:
                System.out.println("\t\t\tUnary not valid");
                return false;
        }
        switch (ecat) {
            case OPERATOR, DELIMITER: return true;
            default: return false;
        }
    }
    private boolean isPrecedence(Token token){
        TokenType type = token.getType();
        TokenCat cat = token.getCategory();
        int token_precedence = getPrecedence(type);
        if(cat!=TokenCat.OPERATOR) return false;
        if(token_precedence==this.precedence_level){
            return true;
        }
        return false;
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