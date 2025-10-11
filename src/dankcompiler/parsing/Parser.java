package dankcompiler.parsing;

import java.util.ArrayList;

import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.DefinedNode;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenType;

enum ParseProgramState{
    INSTRUCTIONS,
    EOF
}

public class Parser {
    //OUTPUT
    
    //General Register
    int context_level = 0;
    //States
    ParseProgramState programState = ParseProgramState.INSTRUCTIONS;
    //Declaration Flags
    boolean statement_finished = true;
    boolean more_declarations = true;
    //Block flags
    boolean b_block_closed = true;
    boolean p_block_closed = true;
    public Parser(){
    }
    public void consumeToken(Token token){
        TokenType typepeeked = token.getType();
        parseProgram(typepeeked);
    }
    private void parseProgram(TokenType typepeeked){
        switch(typepeeked){
            case TokenType.EOF:
                programState = ParseProgramState.EOF;
                System.out.println("EOF");
                break;
            default:
                programState = ParseProgramState.INSTRUCTIONS;
                parseInstruction(typepeeked);
                break;
        }
    }
    private void parseInstruction(TokenType typepeeked){
        switch(typepeeked){
            
        }
    }
}

class Program extends GroupNode {
    private ArrayList<Node> Instructions;
    public Program() {
        super();
        Instructions = new ArrayList<Node>();
    }
    @Override
    public void appendNode(Node node) {
        Instructions.add(node);
    }  
}


