package dankcompiler.parsing.rdutils;

import java.io.BufferedReader;              //Class for handle large information
import java.io.FileReader;                  //Class for reading files with bufferedReader
import java.io.File;                        //Class for files
import java.io.FileNotFoundException;       //Class for handle basic errors
import java.io.IOException;                 //Handle more errors
import java.io.PrintWriter;                 //Class for handle file-writing
import java.io.FileWriter;                  //Class for write new files with PrintWriter

public class FileHandler {
    private File tempOutput = null;
    private BufferedReader readBuffer = null;
    private PrintWriter writer = null;
    private String filepath = null;
    //ReadModes
    private ReadMode read_mode = ReadMode.EAGER;
    //ReadStates
    private Cursor cursor = null;
    private String currentLine = "";
    public FileHandler(){
        this.cursor = new Cursor(this);
    }
    public FileHandler(String filepath){
        this.cursor = new Cursor(this);
        this.filepath = filepath;
    }
    //Setters
    public void setFilePath(String filepath) {
    	this.filepath = filepath;
    }
    public void setReadMode(ReadMode mode){
        this.read_mode = mode;
    }
    //Getters
    public PrintWriter getWriter(){
        return this.writer;
    }
    public Cursor getCursor(){
        return this.cursor;
    }
    public String getCurrentLine(){
    	return this.currentLine;
    }
    //Method Stuff
    private void readEager(){
        try{
            while ((currentLine=readBuffer.readLine())!=null){
            	cursor.advanceNewLine();
                doPerReadedLine(cursor);
            }
            doAtReadFinish(cursor);
        }catch(IOException error){
            System.out.println("Error at read file: "+error);
        }
    }
    private void readLazy(){
        try{
            while (currentLine!=null){
                process();
            }
            doAtReadFinish(cursor);
        }catch(IOException error){
            System.out.println("Error at read file: "+error);
        }
    }
    /**
     * This method reads the specified file line per line
     * @return void
     */
    public void read(){
        if (filepath==null) return;
        FileReader reader = null;
        readBuffer = null;
        try {
            reader = new FileReader(filepath);
            readBuffer = new BufferedReader(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(read_mode==ReadMode.EAGER){
            readEager();
        }else if(read_mode==ReadMode.LAZY){
            readLazy();
        }
        try {
            readBuffer.close();
        } catch (IOException error) {
            System.out.println("Error at close file: "+error);
        }
    }
    /**
     * Focus the file write output
     * @param new_archive is the archive name
     * @param folderpath is the directory, if there is not directory existent, it creates a new one
     */
    public void focusFileOutput(String new_archive, String folderpath){
        FileWriter write = null;
        File directory = null;
        try {
            directory = new File(folderpath);
            if(!directory.exists()){
                directory.mkdirs();
            }
            if(tempOutput!=null){
                tempOutput.delete();
            }
            tempOutput = new File(directory, new_archive);
            System.out.println(tempOutput.getAbsolutePath());
            write = new FileWriter(tempOutput);
        } catch (IOException error) {
            System.out.println("Error when creating outputfile: "+error);
        }
        writer = new PrintWriter(write);
    }
    /**
     *Override this method for make things at reading each line
     *@param currentLine is the current Line when {@link #read()} method is called.
     *@return void
     *@see {@link #read()}
     */
    public void doPerReadedLine(Cursor cursor){}
    /**
     *Override this method for make things when a file is fully read
     *@return void
     *@see {@link #read()} 
     */
    public void doAtReadFinish(Cursor cursor){}

    public void process() throws IOException{
        currentLine = readBuffer.readLine();
    }

    public void nextLine() throws IOException{
        if(read_mode!=ReadMode.LAZY) return;
        currentLine = readBuffer.readLine();
    }
}
