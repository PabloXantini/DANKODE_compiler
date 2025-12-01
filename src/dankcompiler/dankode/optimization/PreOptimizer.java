package dankcompiler.dankode.optimization;

import dankcompiler.dankode.optimization.rules.PreDSE;
import dankcompiler.parsing.ast.AST;

public class PreOptimizer {
	private PreDSE predse;
	public PreOptimizer() {
		predse = new PreDSE();
	}
	public void clear() {
	}
	public void optimize(AST ast) {
		predse.optimize(ast);
	}
}