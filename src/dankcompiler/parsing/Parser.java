package dankcompiler.parsing;

import java.util.Stack;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.DefinedNode;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenCat;
import dankcompiler.parsing.tokens.TokenType;

/*
 * This enum encapsulates all non-terminal symbols, 
 * because terminal symbols are tokens
 */
enum ParseMode{
    PROGRAM,
    //Instruction Block
    INSTRUCTIONS,
    BODY,
    //Instruction Types
    STATEMENT,
    DECLARATION,
    ASSIGNMENT,
    WHILE,
    //Declaration Specification
    ON_DECLARATION,
    DEFINITION,
    //Expresions
    TERM,
    B_OP,
    EXPR,
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
    private Node current_node;
    private ParseMode current;

    public Parser(){
        //INSTANCING
        GroupNode program = new GroupNode();
        this.ast = new AST(program);
        this.context_stack = new Stack<ParseMode>();
        this.branch_stack = new Stack<Node>();
        //INITIALIZATION
        this.context_stack.push(ParseMode.PROGRAM);
        this.branch_stack.push(program);
        this.current_node = program;
    }
    public void consumeToken(Token token){
        parse(current, token);
    }
    private void parse(ParseMode mode, Token token){
        mode = context_stack.peek();
        switch (mode) {
            case ParseMode.PROGRAM:
                parseProgram(token);
                break;
            case ParseMode.ON_DECLARATION:
                parseDeclaration(token);
                break;
            case ParseMode.DEFINITION:
                parseDefinition(token);
                break;
            case ParseMode.ASSIGNMENT:
                parseAssignment(token);
                break;  
            case ParseMode.EXPR:
                parseExpression(token);
                break;
            case ParseMode.GROUP:
                parseGroup(token);
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
     * the root must be Program
     */
    private void parseProgram(Token token){
        //Program -> (InstructionList) [EOF]
        TokenType type = token.getType();
        switch (type) {
            case EOF:
                System.out.println("Program Finished");
                break;
            default:
                //if not EOF try parsing the instructions
                System.out.println("Instruction Looking For...");
                context_stack.push(ParseMode.INSTRUCTIONS);
                parseInstruction(token);
                break;
        }
    }
    /*
     * mode = PROGRAM
     * SuperNode = Program(GroupNode)
     */
    private void parseInstruction(Token token){
        //InstructionList append a Instruction when asserts
        //InstructionList -> () | (Instruction) (InstructionList)
        //Instruction -> (Statement)[;] | WHILE
        //Statement -> Declaration | Assignment
        //Declaration -> [type] (OnDeclaration)
        TokenType type = token.getType();
        switch (type) {
            //STATEMENTS
            case NUMMY, NUMPT, CHARA:
                System.out.println("-- Declaration Looking For... --");
                System.out.println("TYPE_KEYWORD Detected");
                context_stack.push(ParseMode.STATEMENT);
                context_stack.push(ParseMode.DECLARATION);
                context_stack.push(ParseMode.ON_DECLARATION);
                Declaration new_declaration = new Declaration();
                //super_node.appendNode(new_declaration);
                this.getLastBranch().appendNode(new_declaration);
                this.current_node = new_declaration;
                break;
            case ID:
                System.out.println("-- Assignment Looking For... --");
                System.out.println("ID Detected");
                context_stack.push(ParseMode.STATEMENT);
                context_stack.push(ParseMode.ASSIGNMENT);
                Assignment new_assignment = new Assignment();
                this.getLastBranch().appendNode(new_assignment);
                this.previous_token = token;
                this.current_node = new_assignment;
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
     * superNode = Program(GroupNode)
     */
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
                current_node.setValue(token.getSymbol());
                break;
            default:
                break;
        }
    }
    /*
     * mode = ASSIGNEMENT
     * superNode = Program(GroupNode)
     */
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
     * superNode = Program(GroupNode)
     */
    private void parseDefinition(Token token){
        //Definition -> [ID] | Assignement
        //System.out.println("Closure Definition o more Looking For...");
        TokenType type = token.getType();
        switch (type) {
            case SEMICOLON:
                System.out.println("; Declaration Finished Correctly --");
                back(5); //quit until INSTRUCTIONS
                break;
            case COMMA:
                System.out.println("COMMA Detected");
                context_stack.pop();//quit DEFINITION
                Declaration new_declaration = new Declaration();
                this.getLastBranch().appendNode(new_declaration);
                this.current_node = new_declaration;
                break;
            case ASSIGN:
                context_stack.push(ParseMode.ASSIGNMENT);
                context_stack.push(ParseMode.EXPR);
                System.out.println("Expression Looking For...");
                Assignment new_assignment = new Assignment();
                new_assignment.setValue(this.previous_token.getSymbol());
                this.getLastBranch().appendNode(new_assignment);
                this.branch_stack.push(new_assignment);
                this.current_node = new_assignment;
                break;
            default:
                break;
        }
    }
    /*
     * mode = EXPR
     * superNode = Assignement(GroupNode)
     */
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
                Literal new_literal = new Literal(token.getSymbol());
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
        switch (type) {
            case PLUS, MINUS:
                context_stack.pop();
                UnaryOp new_unop = new UnaryOp(type);
                branch_stack.push(new_unop);
                break;
            default:
                System.out.println("The unary operator is not valid");
                break;
            }
    }
}
//STATEMENTS
class Declaration extends DefinedNode{
    public Declaration(){
    }
    @Override
    public void appendNode(Node node, Node branch) {
        throw new UnsupportedOperationException("Unimplemented method 'appendNode'");
    }
    @Override
    public void appendNode(Node node) {
        throw new UnsupportedOperationException("Unimplemented method 'appendNode'");
    }
}
class Assignment extends DefinedNode {
    private Expression expr;
    public Assignment(){}
    @Override
    public void appendNode(Node node, Node branch) {
        branch.appendNode(node);
    }
    @Override
    public void appendNode(Node node) {
        throw new UnsupportedOperationException("Unimplemented method 'appendNode'");
    }
}
//EXPRESSIONS
abstract class Expression extends DefinedNode {
    public Expression(){}
    @Override
    public void appendNode(Node node, Node branch) {
        throw new UnsupportedOperationException("Unimplemented method 'appendNode'");
    }
    @Override
    public void appendNode(Node node) {
        throw new UnsupportedOperationException("Unimplemented method 'appendNode'");
    }
}
class BinaryOp extends Expression {
    private final TokenType op;
    private Expression left;
    private Expression right;
    public BinaryOp(TokenType op){
        this.op = op;
    }
}
class UnaryOp extends Expression {
    private final TokenType op;
    private Expression expr;
    public UnaryOp(TokenType op){
        this.op = op;
    }
}
class Variable extends Expression {
    public Variable(){}
    public Variable(String value){
        this.value = value;
    }
}
class Literal extends Expression {
    public Literal(String value){
        this.value = value;
        for(int i=0;i<200;i=i+5*i){
        }
    }
}