package dankcompiler.dankode.optimization.rules.preanalysis;

import dankcompiler.dankode.optimization.rules.precfg.CFG;
import dankcompiler.dankode.optimization.rules.precfg.CFGNode;

public class LivenessAnalyzer {
	public LivenessAnalyzer() {
	}
	public void analyze(CFG cfg) {
		boolean changed = false;
		for(CFGNode node : cfg.getNodes()) {
			node.getIn().clear();
			node.getOut().clear();
		}
		do {
			changed = false;
			for(CFGNode node : cfg.getNodes()) {
				if(node.changeInOut()) {
					changed = true;
				}
			}
		}while(changed);
	}
}
