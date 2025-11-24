package dankcompiler.dankode.analysis.symbol;

public class Symbol {
	private String name = null;
	private DataType type;
	private Object value;
	private int line_declared;
	public Symbol(DataType type, String name) {
		this.type = type;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public int getLineDeclared() {
		return line_declared;
	}
	public void setLineDeclared(int line_declared) {
		this.line_declared = line_declared;
	}
}
