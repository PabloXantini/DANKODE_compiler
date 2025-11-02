package dankcompiler.analysis.triplets;

public class Tag{
	private final int id;
	private int pointer;
	public Tag(int id) {
		this.id = id;
	}
	public int getId() {
		return this.id;
	}
	public int getPointer() {
		return this.pointer;
	}
	public void setPointer(int pointer) {
		this.pointer = pointer;
	}
}