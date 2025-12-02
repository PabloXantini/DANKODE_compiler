package dankcompiler.dankode.analysis.triplets.args;

public class Temporal extends Argument {
	private final int id;
	private final String name;
	public Temporal(String name, int id) {
		this.name = name;
		this.id = id;
		String new_value = name + id;
		setValue(new_value);
	}
	public int getID() {
		return this.id;
	}
	public String name() {
		return this.name;
	}
}
