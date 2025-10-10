package dankcompiler.parsing;

import dankcompiler.parsing.tokens.Token;

enum ParseDeclarationState {
    ON_TYPE,
    ON_VAR_DEF,
    NEXT
}

public class Parser {
    //General Register
    int context_level = 0;
    //Declaration Flags
    boolean statement_finished = true;
    boolean more_declarations = true;
    //Block flags
    boolean b_block_closed = true;
    boolean p_block_closed = true;
    public Parser(){
    }
    public void consumeToken(Token token){
    }
}


