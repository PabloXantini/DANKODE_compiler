package dankcompiler.analysis.symbol;

import java.util.LinkedHashMap;

public class SymbolTable {
	private final LinkedHashMap<String, Symbol> model;
	private SymbolTable super_model = null;
	public SymbolTable() {
		this.model = new LinkedHashMap<String, Symbol>();
	}
	public SymbolTable(SymbolTable super_model) {
		this.model = new LinkedHashMap<String, Symbol>();
		this.super_model = super_model;
	}
	public LinkedHashMap<String, Symbol> getModel() {
		return this.model;
	}
	public void clear() {
		model.clear();
	}
	public void insert(String key, Symbol symbol) {
		if(model.get(key)!=null) return; 
		model.put(key, symbol);
	}
	public Symbol get(String key) {
		Symbol reqSymbol = model.get(key);
		if(reqSymbol!=null)return reqSymbol;
		if(super_model!=null)return super_model.get(key);
		return null;
	}
}
