package dankcompiler.dankode.analysis.triplets.args;

public class Tag extends Argument {
	private int id;
	private int pointer;
	private String name;
	private String label;
	public Tag(int id) {
		this.id = id;
	}
	public Tag(String name, int id) {
		this.id = id;
		this.name = name;
		this.label = name + id;
	}
	public int getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getLabel() {
		return this.label;
	}
	public int getPointer() {
		return this.pointer;
	}
	public void setId(int id) {
		this.id = id;
		this.label = name + id;
	}
	public void setPointer(int pointer) {
		this.pointer = pointer;
		setValue(Integer.toString(pointer));
	}
}