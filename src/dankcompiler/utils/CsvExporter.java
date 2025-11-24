package dankcompiler.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import dankcompiler.dankode.analysis.symbol.Symbol;
import dankcompiler.dankode.analysis.symbol.SymbolTable;
import dankcompiler.dankode.analysis.triplets.Triplet;
import dankcompiler.dankode.errors.CompileError;
import dankcompiler.dankode.messages.CompileMsgHandler;
import dankcompiler.dankode.messages.MessageType;

public class CsvExporter {

    private static String escape(String s){
        if(s==null) return "";
        // escape quotes and wrap in quotes
        String out = s.replace("\"","\"\"");
        return "\"" + out + "\"";
    }

    public static boolean exportSymbols(SymbolTable table, String path){
        try{
            File f = new File(path);
            File parent = f.getParentFile();
            if(parent!=null && !parent.exists()) parent.mkdirs();
            try(PrintWriter pw = new PrintWriter(new FileWriter(f,false))){
                pw.println("name,type");
                for(Map.Entry<String, Symbol> e : table.getModel().entrySet()){
                    Symbol s = e.getValue();
                    String name = e.getKey();
                    String type = s.getType()==null?"":s.getType().name();
                    pw.println(escape(name)+","+escape(type));
                }
            }
            return true;
        }catch(IOException ex){
            System.err.println("CsvExporter: error exporting symbols -> "+ex.getMessage());
            return false;
        }
    }

    public static boolean exportErrors(List<CompileError> errors, String path, CompileMsgHandler msgHandler){
        try{
            File f = new File(path);
            File parent = f.getParentFile();
            if(parent!=null && !parent.exists()) parent.mkdirs();
            try(PrintWriter pw = new PrintWriter(new FileWriter(f,false))){
                if(msgHandler!=null){
                    String summary = (errors==null || errors.size()==0)?
                        msgHandler.generateMessage(MessageType.ERRORS_NOT_FOUND_MESSAGE):
                        msgHandler.generateMessage(MessageType.ERRORS_FOUND_MESSAGE);
                    pw.println("summary," + escape(summary));
                }
                pw.println("code,type,lexem,line,column,errorcode,args,message");
                for(CompileError err : errors){
                    String args = "";
                    if(err.args!=null){
                        StringBuilder sb = new StringBuilder();
                        for(int i=0;i<err.args.length;i++){
                            if(i>0) sb.append(";");
                            sb.append(err.args[i]);
                        }
                        args = sb.toString();
                    }
                    String message = "";
                    if(msgHandler!=null){
                        message = msgHandler.generateErrorMessage(err);
                    }
                    pw.println(escape(Long.toString(err.code))+","+escape(err.type==null?"":err.type.name())+","+escape(err.lexem)+","+escape(Integer.toString(err.line))+","+escape(Integer.toString(err.column))+","+escape(err.errorcode==null?"":err.errorcode.name())+","+escape(args)+","+escape(message));
                }
            }
            return true;
        }catch(IOException ex){
            System.err.println("CsvExporter: error exporting errors -> "+ex.getMessage());
            return false;
        }
    }
    public static boolean exportOutput(List<Triplet> code, String path){
        try{
            File f = new File(path);
            File parent = f.getParentFile();
            if(parent!=null && !parent.exists()) parent.mkdirs();
            try(PrintWriter pw = new PrintWriter(new FileWriter(f,false))){
                pw.println("code,type,lexem,line,column,errorcode,args,message");
                for(Triplet triplet : code){
                    pw.println(escape(triplet.getInstruction().name())+","+escape(triplet.getIdObject())+","+escape(triplet.getIdSource()));
                }
            }
            return true;
        }catch(IOException ex){
            System.err.println("CsvExporter: error exporting errors -> "+ex.getMessage());
            return false;
        }
    }
}
