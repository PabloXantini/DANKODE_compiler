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
    // Indices legibles
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

    // Mapea por nombre (Temporal.getName() o StoreVar.getName())
    private HashMap<String, Register> user_registers;
    private ArrayList<Tag> unresolved_tags;
    private Queue<Register> freeRegisters;
    private Queue<Register> freeReservedRegisters;
    private String previous_instruction;

    private void initMachineSpecs() {
        // establecer relaciones entre sub-registros y su superregistro
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
        // asegura que las colas no sean null y estén limpias
        if (freeRegisters == null) freeRegisters = new ConcurrentLinkedQueue<>();
        if (freeReservedRegisters == null) freeReservedRegisters = new ConcurrentLinkedQueue<>();
        freeRegisters.clear();
        freeReservedRegisters.clear();

        freeReservedRegisters.add(physical_registers[AX]);
        freeReservedRegisters.add(physical_registers[BX]);
        freeReservedRegisters.add(physical_registers[CX]);
        freeReservedRegisters.add(physical_registers[DX]);

        for (Register reg : physical_registers) {
            if (!freeReservedRegisters.contains(reg)) freeRegisters.add(reg);
        }
    }

    // asigna (o devuelve) un registro para un argumento; key es String (nombre)
    private Register allocRegFor(Argument arg) {
        String key = arg.getValue(); // nombre/valor textual del argumento, ej "temp1" o "x"
        if (arg instanceof StoreVar) key = ((StoreVar)arg).getValue();
        // temporal -> usar su nombre
        Register r = user_registers.get(key);
        if (r != null) return r;

        r = freeRegisters.poll();
        if (r == null) {
            // No hay registers libres: hacemos un spill simple — push al stack (sólo ejemplo)
            // -> tomamos un reservado (AX) y lo usamos (más completo: elegir víctima, guardar en memoria, etc.)
            // Aquí preferimos usar AX/BX/CX/DX si están libres en reserved, si no, usamos AX for safety.
            r = freeReservedRegisters.poll();
            if (r == null) {
                // última opción: forzar usar AX (puede sobreescribir) — mejor implementar spilling real.
                r = physical_registers[AX];
            }
            // NOTA: si eres estricto, aquí debes volcar contenido del registro víctima a memoria.
        }
        user_registers.put(key, r);
        return r;
    }

    // helper: devuelve el nombre de impresión del argumento: registro si temporal, [var] si StoreVar, literal si Const etc
    private String printableFor(Argument arg) {
        if (arg == null) return "";
        if (arg instanceof StoreVar) {
            String name = ((StoreVar)arg).getValue();
            return "[" + name + "]";
        }
        // si es temporal o cualquier otro que tenga mapping:
        String key = arg.getValue();
        if (arg instanceof Temporal) {
            Register r = user_registers.get(key);
            if (r != null) return r.getName();
            // aún no asignado -> asignar uno
            r = allocRegFor(arg);
            return r.getName();
        }
        // Const u otros: devolvemos el literal
        return arg.getValue();
    }

    private void handleTriplet(Triplet triplet, int indexTriplet) {
        InstructionType instruction = triplet.getInstruction();
        Iterator<Tag> it = unresolved_tags.iterator();
        StringBuilder sentence = new StringBuilder();

        // PRINT LABELS
        while (it.hasNext()) {
            Tag tag = it.next();
            if (tag.getPointer() == indexTriplet) {
                sentence.append(tag.getLabel()).append(": ");
            }
        }

        switch (instruction) {
            case MOV, ADD, SUB: {
                Argument argobj = triplet.getObject();
                Argument argsrc = triplet.getSource();
                String i = InstructionAdapter.castx86(instruction);

                // Si es Temporal -> asegurar registro
                if (argobj instanceof Temporal) allocRegFor(argobj);
                if (argsrc instanceof Temporal) allocRegFor(argsrc);

                String obj = printableFor(argobj);
                String src = printableFor(argsrc);

                sentence.append(i).append(" ").append(obj).append(", ").append(src);
                writer.print(sentence.toString());
                break;
            }
            case MUL: {
            	// Used now AX, AL
                //  MOV AL, obj			; reg.obj -> AL
                //  MUL src
                //  MOV reg.obj, AL      ;
                Argument argobj = triplet.getObject();
                Argument argsrc = triplet.getSource();
                String i = InstructionAdapter.castx86(instruction);

                // aseguramos registros para operandos (o usar clones)
                Register rObj = allocRegFor(argobj);
                Register rSrc = allocRegFor(argsrc);

                if (rObj.getName()!="AL") sentence.append("MOV ").append("AL").append(", ").append(rObj.getName()).append("\n");
                sentence.append(i+" ").append(rSrc.getName()).append("\n");
                if (rObj.getName()!="AL") sentence.append("MOV ").append(rObj.getName()).append(", AL");
                writer.print(sentence.toString());
                break;
            }
            case DIV: {
            	// Used now AX, AL
                //  MOV AX, obj			; reg.obj -> AX
            	// if not reg.src : MOV src 
                //  DIV src
                //  MOV obj, AL;
                Argument argobj = triplet.getObject();
                Argument argsrc = triplet.getSource();
                String i = InstructionAdapter.castx86(instruction);
                
                Register rObj = allocRegFor(argobj);
                Register rSrc = allocRegFor(argsrc);
                
                sentence.append("MOV AX, ").append(rObj.getName()).append("\n");
                sentence.append(i+" ").append(rSrc.getName()).append("\n");
                sentence.append("MOV ").append(rObj.getName()).append(", AL");
                writer.print(sentence.toString());
                break;
            }
            case MOD: {
            	// Used now AX, AL
            	//  MOV AX, obj			; reg.obj -> AX
            	// if not reg.src : MOV src 
                //  DIV src
                //  MOV obj, AL;
                Argument argobj = triplet.getObject();
                Argument argsrc = triplet.getSource();
                String i = InstructionAdapter.castx86(instruction);

                Register rObj = allocRegFor(argobj);
                Register rSrc = allocRegFor(argsrc);

                sentence.append("MOV AX, ").append(rObj.getName()).append("\n");
                sentence.append(i+" ").append(rSrc.getName()).append("\n");
                sentence.append("MOV ").append(rObj.getName()).append(", AL");
                writer.print(sentence.toString());
                break;
            }
            case EQUAL, NONEQUAL, GTE, LTE, GT, LT: {
                Argument argobj = triplet.getObject();
                Argument argsrc = triplet.getSource();
                String i = InstructionAdapter.castx86(instruction);
                previous_instruction = i;

                if (argobj instanceof Temporal) allocRegFor(argobj);
                if (argsrc instanceof Temporal) allocRegFor(argsrc);

                String obj = printableFor(argobj);
                String src = printableFor(argsrc);

                sentence.append("CMP ").append(obj).append(", ").append(src);
                writer.print(sentence.toString());
                break;
            }
            case J_True: {
                Argument argobj = triplet.getObject();
                String obj = argobj.getValue();
                if (argobj instanceof Tag) {
                    obj = ((Tag)argobj).getLabel();
                }
                writer.print(previous_instruction + " " + obj);
                break;
            }
            case J_False, JMP: {
                Argument argobj = triplet.getObject();
                String obj = argobj.getValue();
                if (argobj instanceof Tag) {
                    obj = ((Tag)argobj).getLabel();
                }
                sentence.append("JMP ").append(obj);
                writer.print(sentence.toString());
                break;
            }
            case NOP:
                sentence.append("NOP");
                writer.print(sentence.toString());
                break;
            default:
                break;
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
        File Output = new File(directory, name + ".asm");
        try {
            write = new FileWriter(Output);
        } catch (IOException e) {
            System.out.println("Error when binding outputfile: " + e);
            return;
        }
        writer = new PrintWriter(write);
        if (writer == null) {
            System.out.println("Error: Build failed");
            return;
        }
        // MAKE VISIBLE ALL TAGS FOR PUTTING THEM IN THE SECOND PASS
        for (Triplet triplet : codeInput) {
            Argument obj = triplet.getObject();
            if (obj instanceof Tag) {
                unresolved_tags.add((Tag) obj);
            }
        }
        // GENERATE INSTRUCTIONS
        for (int i = 0; i < codeInput.size(); i++) {
            Triplet triplet = codeInput.get(i);
            handleTriplet(triplet, i);
        }
        writer.close();
    }
}
