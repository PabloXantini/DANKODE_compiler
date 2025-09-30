package ide.actions_tools.analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

import compilador.*;

public class CompileAction implements ActionListener{
    private JTextArea textArea;
    //Method stuff
    private void compile(){
        //First request the whole text in the editor
        String source = textArea.getText();
        System.out.println(source);
        //Compile process
        //Dont say nothing to Julius i wanna change this
        Lex.LexResult output = Lex.lex(source);
        Parser parser = new Parser(output.tokens);
        Parser.Program ast = parser.parseProgram();
        System.out.println(ast);
        Semantico.analyzeAndExport(ast, output.errors, parser.errors, "symbol_table.csv", "error_table.csv");
    }
    public CompileAction(JTextArea textArea){
        this.textArea = textArea;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Compilar el archivo
        compile();
    }    
}
