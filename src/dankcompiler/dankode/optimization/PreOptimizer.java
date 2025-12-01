package dankcompiler.dankode.optimization;

import dankcompiler.dankode.optimization.rules.PreDSE;
import dankcompiler.dankode.optimization.rules.preanalysis.LivenessAnalyzer;
import dankcompiler.dankode.optimization.rules.precfg.CFGBuilder;
import dankcompiler.parsing.ast.AST;

public class PreOptimizer {
	private CFGBuilder cfgBuilder;
	private LivenessAnalyzer liveAnalyzer;
	private PreDSE preDSE;
	public PreOptimizer() {
		cfgBuilder = new CFGBuilder();
		liveAnalyzer = new LivenessAnalyzer();
		preDSE = new PreDSE();
	}
	public void clear() {
	}
	public void optimize(AST ast) {
		cfgBuilder.generateCFG(ast);
		liveAnalyzer.analyze(cfgBuilder.getCFG());
		preDSE.optimize(ast, cfgBuilder.getCFG());
	}
}