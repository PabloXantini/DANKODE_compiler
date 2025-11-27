package dankcompiler.dankode.build.machineinfo;

public class Instruction {
	private String name;
	private Register destiny = null;
	private Register source = null;
	private Register aux = null;
	public Instruction(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Register getDestiny() {
		return destiny;
	}
	public void setDestiny(Register destiny) {
		this.destiny = destiny;
	}
	public Register getSource() {
		return source;
	}
	public void setSource(Register source) {
		this.source = source;
	}
	public Register getAux() {
		return aux;
	}
	public void setAux(Register aux) {
		this.aux = aux;
	}
}
