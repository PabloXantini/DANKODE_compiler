package dankcompiler.dankode.analysis.triplets;

import dankcompiler.dankode.analysis.triplets.args.Argument;
import dankcompiler.dankode.analysis.triplets.args.Tag;

public class Triplet {
	private Tag ref_tag;
	private int index;
	private final InstructionType instruction;
	private Argument object;
	private Argument source;
	private String id_object;
	private String id_source;
	public Triplet(int index, Tag ref_tag, InstructionType instruction, String id_object, String id_source) {
		this.index = index;
		this.ref_tag = ref_tag;
		this.instruction = instruction;
		this.id_object = id_object;
		this.id_source = id_source;
	}
	public Triplet(int index, InstructionType instruction, Argument object, Argument source) {
		this.index = index;
		this.instruction = instruction;
		this.object = object;
		this.source = source;
	}
	public Triplet(InstructionType instruction, Argument object, Argument source) {
		this.instruction = instruction;
		this.object = object;
		this.source = source;
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
	public Argument getObject() {
		return object;
	}
	public Argument getSource() {
		return source;
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
	public void setTag(Tag tag) {
		this.ref_tag = tag;
	}
}
