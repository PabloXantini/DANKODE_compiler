package dankcompiler.dankode.optimization.rules;

import java.util.HashSet;
import java.util.Set;

import dankcompiler.dankode.optimization.rules.precfg.CFG;
import dankcompiler.dankode.optimization.rules.precfg.CFGNode;
import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTSpecVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.Declaration;
import dankcompiler.parsing.ast.nodes.Variable;

public class PreUnusedVarRemoval extends ASTSpecVisitor {
	private AST ast_ref;
	//private UnusedVarPrunner prunner;
	private Set<String> all_current_live_vars;
	private void revealLiveVars(CFG cfg) {
		all_current_live_vars.clear();
		for(CFGNode block : cfg.getNodes()) {
			all_current_live_vars.addAll(block.getDef());
			all_current_live_vars.addAll(block.getUse());
		}
	}
	public PreUnusedVarRemoval() {
		all_current_live_vars = new HashSet<String>();
	}
	public void optimize(AST ast, CFG cfg) {
		this.ast_ref = ast;
		revealLiveVars(cfg);
		this.ast_ref.setRoot(this.ast_ref.getRoot().accept(this));
	}
	@Override
	public Node visit(Declaration declaration) {
		Variable var = declaration.getVariable();
		String varname = var.getValue().getSymbol();
		if(!all_current_live_vars.contains(varname)) return null;
		return declaration;
	}
}
