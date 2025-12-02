package dankcompiler.dankode.build.machineinfo;

public class Register {
	private Register superReg = null;
	private final RegisterType type;
	private final String name;
	private final int size;
	public Register(RegisterType type, String name, int size) {
		this.type = type;
		this.name = name;
		this.size = size;
	}
	public RegisterType getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public int getSize() {
		return size;
	}
	public Register getSuper() {
		return superReg;
	}
	public void setSuper(Register reg) {
		this.superReg = reg;
	}
}
