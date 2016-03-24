/**
 * Created by Michael on 2/23/2016.
 */
import java.io.*;
import java.net.Socket;
import java.util.*;
/** Description of WorkerRunnable
 *
 * @author Michael Balcerzak
 * @version 1.0 March 3, 2016.
 */

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;
    private static final String SPECIAL_BREAK_SYMBOL = "'#";
    private ArrayList<ClientObject> clientList;
    private volatile ArrayList<FileObject> fileList;
    private BackupComObject serverStateChange;
    private ObjectInputStream objectInputStream;
    private BackupComObject update;
    private ArrayList<ClientObject> serverList;

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<ClientObject> clientList, ArrayList<FileObject> fileList, BackupComObject serverStateChange, ArrayList<ClientObject> serverList) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.clientList = clientList;
        this.fileList = fileList;
        this.serverStateChange = serverStateChange;
        this.serverList = serverList;
    }

    public void run() {
        ClientObject clientID = null;
        FileListManager fileListManager = new FileListManager(fileList);
        
        try {

            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            String receivedMSG = "";
            String response;
            String str;
            String[] parsedInput;
            RequestManager requestManager = new RequestManager();
            String localOutput;

            try { 
            
                InputStream inputStream = clientSocket.getInputStream();
                BufferedInputStream  buff = new BufferedInputStream (clientSocket.getInputStream());
                while(buff.available() == 0);
                int ch = inputStream.read();
                StringBuffer stringBuffer = new StringBuffer();
                
                while((char)ch != '\n' && (char)ch != '\r')
                {
                    stringBuffer.append((char)ch);
                    ch = inputStream.read();
                }
                
                receivedMSG = stringBuffer.toString();
                System.out.println(stringBuffer.toString());

                if(receivedMSG.equals("update"))
                {
                    ObjectInputStream objectInputStream = open(clientSocket);
                    update = (BackupComObject)objectInputStream.readObject();

                    System.out.println("Shared Files:");
                    Iterator<FileObject> fileItr = update.getNewFileList().iterator();
                    while(fileItr.hasNext())
                    {
                        FileObject fileObject = fileItr.next();
                        Iterator<ClientObject> seeders = fileObject.getSeeders().iterator();
                        System.out.println(fileObject.getFileName());
                        while(seeders.hasNext())
                            System.out.println(seeders.next().get_Client_info());
                    }

                    //System.out.println("my files after update");
                    //showFiles();

                    fileItr = update.getNewFileList().iterator();

                    //updating files based on update - adding added files
                    while (fileItr.hasNext())
                    {

                        FileObject fileObject = fileItr.next();

                        int index = fileListManager.findFileIndex(fileObject.getFileName());
                        ArrayList<ClientObject> newSeeders = fileObject.getSeeders();

                        

                        if(index != -1)
                        {
                        ArrayList<ClientObject> oldSeeders = fileList.get(index).getSeeders();
                        newSeeders.removeAll(oldSeeders);
                        oldSeeders.addAll(newSeeders);
                        fileList.set(index, new FileObject(fileObject.getFileName(), oldSeeders));
                        }
                        else{
                            fileList.add(new FileObject(fileObject.getFileName(), newSeeders));
                        }

                    }

                    //removing files hosted by disconnected clients
                    ArrayList<ClientObject> disconnected = update.getDisconnectedClients();
                    ListIterator<FileObject> myFileListItr = fileList.listIterator();

                    while (myFileListItr.hasNext())
                    {
                        FileObject fileObject = myFileListItr.next();
                        ArrayList<ClientObject> seeders = fileObject.getSeeders();
                        seeders.removeAll(disconnected);
                        if(seeders.size() == 0)
                            myFileListItr.remove();
                    }

                    
                    System.out.println("My files after update: ");
                    showFiles();
                }

                else{

                if(receivedMSG.equals("show user list"))
                {
                    localOutput = requestManager.getClientListString(clientList);
                    output.write(localOutput.getBytes());
                }
                else if(receivedMSG.equals("show file list"))
                {
                    localOutput = requestManager.showFileList(fileList);
                    output.write(localOutput.getBytes());
                }
                else if(receivedMSG.contains("'#")) {
                    parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);
                        
                    if(parsedInput.length == 2) {
                        if (parsedInput[0].equals("get")) {
                            response = requestManager.clientRequestGet(parsedInput[1], fileList);
                            output.write(response.getBytes());
                        }
                    }
                    else if(parsedInput.length == 3 && parsedInput[0].equals("leave")){
                        requestManager.clientRequestLeave(new ClientObject(parsedInput[1], parsedInput[2]),  fileList);
                        System.out.println("my files after leave: ");
                        showFiles();
                        serverStateChange.addtoDisconnectedClients(new ClientObject(parsedInput[1], parsedInput[2]));

                        ListIterator<FileObject> itr = serverStateChange.getNewFileList().listIterator();
                        while(itr.hasNext())
                        {
                            FileObject file = itr.next();
                            ArrayList<ClientObject> seeders = file.getSeeders();
                            seeders.remove(new ClientObject(parsedInput[1], parsedInput[2]));
                            if(seeders.size() == 0)
                                itr.remove();
                        }

                    }
                    else if(parsedInput.length == 3) {
                        if (parsedInput[0].equals("new-client-join-request")){
                            clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                            clientList.add(clientID);

                            if(serverStateChange.getDisconnectedClients().contains(clientID))
                                serverStateChange.getDisconnectedClients().remove(clientID);
                        }
                        clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                        clientList.add(clientID);                   // Add the new client to the Client list.

                        if(serverStateChange.getDisconnectedClients().contains(clientID))
                            serverStateChange.getDisconnectedClients().remove(clientID);
                    }
                    else if(parsedInput.length == 4 && parsedInput[0].equals("add")) {
                            requestManager.clientRequestAdd(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3]), fileList);
                        //fileListManager.addFileToList(new FileObject(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3])), new ClientObject(parsedInput[2], parsedInput[3]));
                            serverStateChange.addtoNewFileList(new FileObject(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3])));

                            System.out.println("My files after add: ");
                            showFiles();
                     
                    }
                    else if(parsedInput.length >=4 && parsedInput[0].equals("new-sibling-trackers"))
                    {
                        ArrayList<TrackerTuple> trList = new ArrayList<TrackerTuple>();
                        //So many Cats'#3'#192.168.56.1'#9010'#192.168.56.1'#9011'#192.168.56.1'#9012'#
                        for(int i = 2; i < parsedInput.length; i+=2)
                        {
                            if(i + 1 < parsedInput.length)
                            {
                                System.out.println("***" + parsedInput[i] + " " + parsedInput[i+1]);
                                TrackerTuple tr = new TrackerTuple(parsedInput[i], parsedInput[i+1]);
                                trList.add(tr);
                            }
                        }
                        for(int i = 0; i < trList.size(); i++){
                            ClientObject clo = new ClientObject(trList.get(i).ip, trList.get(i).port);
                            if(!serverList.contains(clo))
                                serverList.add(clo);
                        }
                    }
                        

                }
            }
            } 
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Read failed");
                e.printStackTrace();
                System.exit(-1);
            }

            long time = System.currentTimeMillis();
            output.close();
            input.close();
            //System.out.println(receivedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    public ObjectInputStream open(Socket s) throws IOException{
        try{
            ObjectInputStream o = new ObjectInputStream(s.getInputStream());
            return o;
        }
        catch(Exception e){
            try{

                InputStream inputStream= s.getInputStream();
                int ch = inputStream.read();
                System.out.println("read a byte");
                if (ch != -1)
                    open(s);
                else{
                    System.out.println("reached end of stream");
                }
            }
            catch(Exception ee){
                ee.printStackTrace();
            }
        }
        System.out.println("null");
        return null;
    }

    public void showFiles(){
        Iterator<FileObject> fileItr = fileList.iterator();
        while(fileItr.hasNext())
        {
            FileObject fileObject = fileItr.next();
            Iterator<ClientObject> seeders = fileObject.getSeeders().iterator();
            System.out.println(fileObject.getFileName());
            while(seeders.hasNext())
                System.out.println(seeders.next().get_Client_info());
        }
    }

    class TrackerTuple{
    public String ip;
    public String port;

    public TrackerTuple(String ip, String port){
        this.ip = ip;
        this.port = port;
    }

}
}