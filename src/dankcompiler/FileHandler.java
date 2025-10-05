package dankcompiler;

import java.io.BufferedReader;              //Class for handle large information
import java.io.FileReader;                  //Class for reading files with bufferedReader
import java.io.File;                        //Class for files
import java.io.FileNotFoundException;       //Class for handle basic errors
import java.io.IOException;                 //Handle more errors
import java.io.PrintWriter;                 //Class for handle file-writing
import java.io.FileWriter;                  //Class for write new files with PrintWriter

public abstract class FileHandler {
    File tempOutput = null;
    PrintWriter writer = null;
    String filepath = null;
    //Read States
    String currentLine = "";
    public FileHandler(){
    }
    public FileHandler(String filepath){
        this.filepath = filepath;
    }
    PrintWriter getWriter(){
        return this.writer;
    }
    /**
     * This method reads the especified file line per line
     * @return void
     */
    public void read(){
        if (filepath==null) return;
        BufferedReader readBuffer = null;
        FileReader reader = null;
        try {
            reader = new FileReader(filepath);
            readBuffer = new BufferedReader(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while ((currentLine=readBuffer.readLine())!=null) {
                doPerReadedLine(currentLine);
            }
            doAtReadFinish();
        } catch (IOException error) {
            System.out.println("Error at read file: "+error);
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
    abstract public void doPerReadedLine(String currentLine);
    /**
     *Override this method for make things when a file is fully readed
     *@return void
     *@see {@link #read()} 
     */
    abstract public void doAtReadFinish();
}
