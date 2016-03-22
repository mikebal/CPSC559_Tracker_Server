import java.util.ArrayList;
import java.io.*;
import java.net.*;

/**
 * Created by Michael on 3/5/2016.
 *
 * This may be a way to keep track of what changes other tracker servers need to updated on
 */
public class BackupComObject implements Serializable{
    private ArrayList<FileObject> newFileList;
    private ArrayList<FileObject> removedFiles;
    private ArrayList<ClientObject> disconnectedClients;
    private ArrayList<ClientObject> newClients;

    public BackupComObject(ArrayList<FileObject> newFiles, ArrayList<FileObject> removedFiles, ArrayList<ClientObject> disconnectedClients, ArrayList<ClientObject> newClients)
    {
        this.newFileList = newFiles;
        this.removedFiles = removedFiles;
        this.disconnectedClients = disconnectedClients;
        this.newClients = newClients;
    }

    public BackupComObject()
    {
        this.newFileList = new ArrayList<FileObject>();
        this.removedFiles = new ArrayList<FileObject>();
        this.disconnectedClients = new ArrayList<ClientObject>();
        this.newClients = new ArrayList<ClientObject>();
    }

    public void addtoNewFileList(FileObject newFile){
        this.newFileList.add(newFile);
    }

    public void addtoRemovedFiles(FileObject removedFile){
        this.removedFiles.add(removedFile);
    }

    public void addtoNewClients(ClientObject newClient){
        this.newClients.add(newClient);
    }

    public void addtoDisconnectedClients(ClientObject removedClient){
        this.disconnectedClients.add(removedClient);
    }

    public ArrayList<FileObject> getNewFileList() {
        return newFileList;
    }

    public ArrayList<FileObject> getRemovedFiles() {
        return removedFiles;
    }

    public ArrayList<ClientObject> getDisconnectedClients() {
        return disconnectedClients;
    }

    public ArrayList<ClientObject> getNewClients() {
        return newClients;
    }

    public void printNewFileList(){

        
    }
}
