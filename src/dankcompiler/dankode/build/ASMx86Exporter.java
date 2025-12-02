package dankcompiler.dankode.build;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dankcompiler.dankode.analysis.triplets.InstructionType;
import dankcompiler.dankode.analysis.triplets.Triplet;
import dankcompiler.dankode.analysis.triplets.args.Argument;
import dankcompiler.dankode.analysis.triplets.args.StoreVar;
import dankcompiler.dankode.analysis.triplets.args.Tag;
import dankcompiler.dankode.analysis.triplets.args.Temporal;
import dankcompiler.dankode.build.machineinfo.InstructionAdapter;
import dankcompiler.dankode.build.machineinfo.Register;
import dankcompiler.dankode.build.machineinfo.RegisterType;

public class ASMx86Exporter {
	public final static int AX = 0;
	public final static int BX = 1;
	public final static int CX = 2;
	public final static int DX = 3;
	public final static int AL = 4;
	public final static int AH = 5;
	public final static int BL = 6;
	public final static int BH = 7;
	public final static int CL = 8;
	public final static int CH = 9;
	public final static int DL = 10;
	public final static int DH = 11;
	private Register[] physical_registers = {
			new Register(RegisterType.GP, "AX", 16),
			new Register(RegisterType.GP, "BX", 16),
			new Register(RegisterType.GP, "CX", 16),
			new Register(RegisterType.GP, "DX", 16),
			new Register(RegisterType.GP, "AL", 8),
			new Register(RegisterType.GP, "AH", 8),
			new Register(RegisterType.GP, "BL", 8),
			new Register(RegisterType.GP, "BH", 8),
			new Register(RegisterType.GP, "CL", 8),
			new Register(RegisterType.GP, "CH", 8),
			new Register(RegisterType.GP, "DL", 8),
			new Register(RegisterType.GP, "DH", 8)
	};
	private PrintWriter writer = null;
	private ArrayList<Triplet> codeInput = null;
	private HashMap<String, Register> user_registers;
	private ArrayList<Tag> unresolved_tags; 
	private Queue<Register> freeRegisters;
	private Queue<Register> freeReservedRegisters;
	private String previous_instruction;
	private void initMachineSpecs() {
		this.physical_registers[AL].setSuper(physical_registers[AX]);
		this.physical_registers[AH].setSuper(physical_registers[AX]);
		this.physical_registers[BL].setSuper(physical_registers[BX]);
		this.physical_registers[BH].setSuper(physical_registers[BX]);
		this.physical_registers[CL].setSuper(physical_registers[CX]);
		this.physical_registers[CH].setSuper(physical_registers[CX]);
		this.physical_registers[DL].setSuper(physical_registers[DX]);
		this.physical_registers[DH].setSuper(physical_registers[DX]);
	}
	private void init() {
		freeRegisters.clear();
		freeReservedRegisters.clear();
		freeReservedRegisters.add(physical_registers[AX]);
		freeReservedRegisters.add(physical_registers[BX]);
		freeReservedRegisters.add(physical_registers[CX]);
		freeReservedRegisters.add(physical_registers[DX]);
		for(Register reg : physical_registers) {
			if (!freeReservedRegisters.contains(reg)) freeRegisters.add(reg);
		}
	}
	private void handleTriplet(Triplet triplet) {
		InstructionType instruction = triplet.getInstruction();
		Iterator<Tag> it = unresolved_tags.iterator();
		String sentence = "";
		while(it.hasNext()) {
			Tag tag = it.next();
			if(tag.getPointer() == codeInput.indexOf(triplet)) {
				sentence+=tag.getLabel()+":";
			}
		}
		switch(instruction) {
			case MOV, ADD, SUB:
				Argument argobj = triplet.getObject();
				Argument argsrc = triplet.getSource();
				String i = InstructionAdapter.castx86(instruction);
				String obj = argobj.getValue();
				String src = argsrc.getValue();
				if(argobj instanceof Temporal && argobj != null) {
					if(user_registers.get(obj)==null) {
						user_registers.put(obj, freeRegisters.poll());
						obj = user_registers.get(obj).getName();
					}else {						
						obj = user_registers.get(obj).getName();
					}
				}else if(argobj instanceof StoreVar && argobj != null) {
					obj = "["+obj+"]";
				}
				if(argsrc instanceof Temporal && argsrc != null) {
					if(user_registers.get(src)==null) {
						user_registers.put(src, freeRegisters.poll());
						src = user_registers.get(src).getName();
					}else {						
						src = user_registers.get(src).getName();
					}
				}else if(argsrc instanceof StoreVar && argsrc != null) {
					src = "["+src+"]";
				}
				sentence+=i+" "+obj+", "+src;
				writer.print(sentence);
				break;
			case MUL:
				Register reg;
				argobj = triplet.getObject();
				argsrc = triplet.getSource();
				i = InstructionAdapter.castx86(instruction);
				obj = argobj.getValue();
				src = argsrc.getValue();
				reg = user_registers.get(obj);
				if(reg!=null) {
				}
				break;
			case EQUAL, NONEQUAL, GTE, LTE, GT, LT:
				argobj = triplet.getObject();
				argsrc = triplet.getSource();
				i = InstructionAdapter.castx86(instruction);
				previous_instruction = i;
				obj = argobj.getValue();
				src = argsrc.getValue();
				if(argobj instanceof Temporal && argobj != null) {
					if(user_registers.get(obj)==null) {
						user_registers.put(obj, freeRegisters.poll());
						obj = user_registers.get(obj).getName();
					}else {						
						obj = user_registers.get(obj).getName();
					}
				}else if(argobj instanceof StoreVar && argobj != null) {
					obj = "["+obj+"]";
				}
				if(argsrc instanceof Temporal && argsrc != null) {
					if(user_registers.get(src)==null) {
						user_registers.put(src, freeRegisters.poll());
						src = user_registers.get(src).getName();
					}else {						
						src = user_registers.get(src).getName();
					}
				}else if(argsrc instanceof StoreVar && argsrc != null) {
					src = "["+src+"]";
				}
				sentence+=i+"CMP "+obj+", "+src;
				writer.print(sentence);
				break;
			case J_True:
				argobj = triplet.getObject();
				obj = argobj.getValue();
				if(argobj instanceof Tag) {					
					obj = ((Tag)argobj).getLabel();
				}
				writer.print(previous_instruction+" "+obj);
				break;
			case J_False, JMP:
				argobj = triplet.getObject();
				obj = argobj.getValue();
				if(argobj instanceof Tag) {					
					obj = ((Tag)argobj).getLabel();
				}
				sentence+="JMP "+obj;
				writer.print(sentence);
				break;
			case NOP:
				sentence+="NOP";
				writer.print(sentence);
				break;
			default: break;
		}
		writer.println();
	}
	public ASMx86Exporter() {
		initMachineSpecs();
		freeRegisters = new ConcurrentLinkedQueue<Register>();
		freeReservedRegisters = new ConcurrentLinkedQueue<Register>();
		user_registers = new HashMap<String, Register>();
		unresolved_tags = new ArrayList<Tag>();
	}
	public void setCodeInput(ArrayList<Triplet> triplets) {
		this.codeInput = triplets;
	}
	public void export(String filepath) {
		init();
		FileWriter write = null;
		//Get the output path using the source same name
		Path path = Paths.get(filepath);
		String directory = path.getParent().toString();
		String file_name = path.getFileName().toString();
		int dotloc = file_name.lastIndexOf('.');
		String name = (dotloc == -1) ? file_name : file_name.substring(0, dotloc);
		//create the archive
		File Output = new File(directory, name+".asm");
		try {
			write = new FileWriter(Output);
		} catch (IOException e) {
			System.out.println("Error when binding outputfile: "+ e);
		}
		writer = new PrintWriter(write);
		if(writer == null) System.out.println("Error: Build failed");
		//MAKE VISIBLE ALL TAGS FOR PUTTING THEM IN THE SECOND LOOP
		for(Triplet triplet : codeInput) {
			Argument obj = triplet.getObject();
			if(obj instanceof Tag) {
				unresolved_tags.add((Tag)obj);
			}
		}
		for(Triplet triplet : codeInput) {
			handleTriplet(triplet);
		}
		writer.close();
	}
}
