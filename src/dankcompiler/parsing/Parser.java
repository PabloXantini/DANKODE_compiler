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
    ASSIGN_DEFINITION,
    //ASSIGNMENT STUFF
    ASSIGNMENT,
    ON_ASSIGNMENT,
    //While
    WHILE,
    //Expresions
    EXPR,
    TERM,
    B_OP,
    //Term Especification
    GROUP,
    UEXPR,
    LITERAL,
    //Binary Operators
    BOOL, //Boolean
    R_OP, //Relational
    A_OP,  //Arithmetic
    //Atomic Terms
    U_OP,
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
    private Stack<Node> op_stack;

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
            case ParseMode.DEFINITION:
                parseDefinition(token);                
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
                System.out.println("\t\t\tID Detected");
                context_stack.push(ParseMode.ON_ASSIGNMENT);
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
                context_stack.push(ParseMode.EXPR);
                break;
            default:
                break;
        }
    }
    /*
    private void parseInstruction(Token token){
        //InstructionList append a Instruction when asserts
        //InstructionList -> () | (Instruction) (InstructionList)
        //Instruction -> (Statement)[;] | WHILE
        //Statement -> Declaration | Assignment
        //Declaration -> [type] (OnDeclaration)
        TokenType type = token.getType();
        GroupNode instructions = (GroupNode) this.getLastBranch();
        switch (type) {
            //STATEMENTS
            case NUMMY, NUMPT, CHARA:
                System.out.println("-- Declaration Looking For... --");
                System.out.println("TYPE_KEYWORD Detected");
                context_stack.push(ParseMode.STATEMENT);
                context_stack.push(ParseMode.DECLARATION);
                context_stack.push(ParseMode.ON_DECLARATION);
                //Esto podria almacenarse solo en la tabla de simbolos
                Declaration new_declaration = new Declaration();
                instructions.appendNode(new_declaration);
                branch_stack.push(new_declaration);
                //====================================================
                //this.current_node = new_declaration;
                break;
            case ID:
                System.out.println("-- Assignment Looking For... --");
                System.out.println("ID Detected");
                context_stack.push(ParseMode.STATEMENT);
                context_stack.push(ParseMode.ASSIGNMENT);
                Variable new_variable = new Variable(token.getSymbol());
                Assignment new_assignment = new Assignment(new_variable);
                instructions.appendNode(new_assignment);
                branch_stack.push(new_assignment);
                this.previous_token = token;
                break;
            //WHILE
            case WHILE:
                System.out.println("-- While Looking For... --");
                context_stack.push(ParseMode.WHILE);
                break;
            case EOF:
                System.out.println("End of main instruction block");
                break;
            default:
                break;
        }
    }
    /*
     * mode = ON_DECLARATION
     *
    private void parseDeclaration(Token token){
        //OnDeclaration-> (Definition) | (Definition)[,](OnDeclaration)
        //Definition-> [ID] | Assignement
        //System.out.println("Definition Looking For...");
        TokenType type = token.getType(); 
        if(token.getCategory()==TokenCat.RESERVED){
            //PUT ERROR HERE
            System.out.println("ID must not be reserved keyword");
            return;
        }
        switch (type) {
            case ID:
                System.out.println("ID Detected");
                context_stack.push(ParseMode.DEFINITION);
                break;
            default:
                break;
        }
    }
    /*
     * mode = ASSIGNEMENT
     *
    private void parseAssignment(Token token){
        TokenType type = token.getType();
        //Assignement -> [ID][=](Expr)
        switch(type){
            case ASSIGN:
                System.out.println("= Detected");
                System.out.println("Expression Looking For...");
                context_stack.push(ParseMode.EXPR);
                this.previous_token = token;
                break;
            default:
                break;
        }
    }
    /*
     * mode = DEFINITION
     *
    private void parseDefinition(Token token){
        //Definition -> [ID] | Assignement
        //System.out.println("Closure Definition o more Looking For...");
        TokenType type = token.getType();
        GroupNode instructions = null;
        switch (type) {
            case SEMICOLON:
                System.out.println("; Declaration Finished Correctly --");
                backUntil(ParseMode.INSTRUCTIONS); //quit until INSTRUCTIONS
                branch_stack.pop();//goto INSTRUCTIONS NODES
                break;
            case COMMA:
                System.out.println("COMMA Detected");
                context_stack.pop();//quit DEFINITION
                branch_stack.pop();//goto INSTRUCTIONS NODES
                instructions = (GroupNode) this.getLastBranch();
                Declaration new_declaration = new Declaration();
                instructions.appendNode(new_declaration);
                branch_stack.push(new_declaration);
                break;
            case ASSIGN:
                context_stack.push(ParseMode.ASSIGNMENT);
                context_stack.push(ParseMode.EXPR);
                branch_stack.pop();//goto INSTRUCTIONS NODES
                System.out.println("Expression Looking For...");
                Assignment new_assignment = new Assignment();
                new_assignment.setValue(this.previous_token.getSymbol());
                instructions = (GroupNode) this.getLastBranch();
                instructions.appendNode(new_assignment);
                this.branch_stack.push(new_assignment);
                break;
            default:
                break;
        }
    }
    /*
     * mode = EXPR
     *
    private void parseExpression(Token token){
        //Expr -> Term | (Term) (BOP) (Expr)
        TokenCat category = token.getCategory();
        TokenType type = token.getType();
        if(category==TokenCat.OPERATOR){
            resolveOp(token);        
        }
        //This register all posible operand
        switch (type) {
            case CINT, CFLOAT, CSTRING:
                System.out.println("LITERAL Detected...");
                Constant new_literal = new Constant(token.getSymbol());
                branch_stack.push(new_literal);
                break;
            case ID:
                System.out.println("ID Detected...");
                Variable new_var = new Variable(token.getSymbol());
                branch_stack.push(new_var);
                break;
            case LP:
                System.out.println("( Detected...");
                context_stack.push(ParseMode.GROUP);
                break;
            default:
                break;
        }
    }
    private void resolveOp(Token token){
        TokenCat cat = previous_token.getCategory();
        switch (cat) {
            case OPERATOR, DELIMITER:
                context_stack.push(ParseMode.U_OP);
                parseUnaryOp(token);
                break;
            default:
                context_stack.push(ParseMode.B_OP);
                parseBinaryOp(token);
                break;
        }
    }
    private void parseGroup(Token token){
        TokenType type = token.getType();
        switch (type) {
            case RP:
                System.out.println(") Expression Block Finished correctly --");
                context_stack.pop();
                break;
            default:
                parseExpression(token);
                break;
        }
    }
    private void parseUnaryOp(Token token){
        TokenType type = token.getType();
        UnaryOp stored_op = null;
        switch (type) {
            case PLUS, MINUS:
                System.out.println("Unary OP: "+type);
                context_stack.pop();
                UnaryOp new_unop = new UnaryOp(type);
                branch_stack.push(new_unop);
                break;
            case ID:
                System.out.println("ID Detected...");
                context_stack.pop();
                stored_op = (UnaryOp)branch_stack.pop();
                Variable new_var = new Variable(token.getSymbol());
                stored_op.setTerm(new_var);
                break;
            case CINT, CFLOAT, CSTRING:
                System.out.println("LITERAL Detected...");
                context_stack.pop();
                stored_op = (UnaryOp)branch_stack.pop();
                Constant new_const = new Constant(token.getSymbol());
                stored_op.setTerm(new_const);
                break;
            case LP:
                System.out.println("( Detected...");
                context_stack.push(ParseMode.GROUP);
                break;
            default:
                System.out.println("The unary operator is not valid");
                break;
            }
    }
    private void parseBinaryOp(Token token){
        TokenType type = token.getType();
    }
    */
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