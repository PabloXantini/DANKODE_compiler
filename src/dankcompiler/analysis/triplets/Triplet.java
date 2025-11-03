package dankcompiler.analysis.triplets;

public class Triplet {
	private final Tag ref_tag;
	private final int index;
	private final InstructionType instruction;
	private String id_object;
	private String id_source;
	public Triplet(int index, Tag ref_tag, InstructionType instruction, String id_object, String id_source) {
		this.index = index;
		this.ref_tag = ref_tag;
		this.instruction = instruction;
		this.id_object = id_object;
		this.id_source = id_source;
	}
	public Tag getTag() {
		return ref_tag;
	}
	public int getIndex() {
		return index;
	}
	public InstructionType getInstruction() {
		return instruction;
	}
	public String getIdObject() {
		return id_object;
	}
	public String getIdSource() {
		return id_source;
	}
	public void setIdObject(String new_id) {
		this.id_object = new_id;
	}
	public void setIdSource(String new_id) {
		this.id_source = new_id;
	}
}
