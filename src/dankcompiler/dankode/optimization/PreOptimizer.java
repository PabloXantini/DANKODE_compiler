package dankcompiler.dankode.optimization;

import dankcompiler.dankode.optimization.rules.PreDSE;
import dankcompiler.dankode.optimization.rules.RuleTest;
import dankcompiler.parsing.ast.AST;

public class PreOptimizer {
	private PreDSE predse;
	private RuleTest rulet;
	public PreOptimizer() {
		//predse = new PreDSE();
		rulet = new RuleTest();
	}
	public void clear() {
		//predse.reset();
	}
	public void optimize(AST ast) {
		//predse.optimize(ast);
		rulet.optimize(ast);
	}
}