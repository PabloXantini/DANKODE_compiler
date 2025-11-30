package dankcompiler.dankode.optimization.rules;

import dankcompiler.dankode.optimization.rules.precfg.CFGBuilder;
import dankcompiler.parsing.ast.AST;

public class RuleTest {
	CFGBuilder cfgbuilder;
	public RuleTest() {
		cfgbuilder = new CFGBuilder();
	}
	public void optimize(AST ast) {
		cfgbuilder.generateCFG(ast);
	}
}
