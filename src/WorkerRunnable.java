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
    private ArrayList<FileObject> fileList;
    private BackupComObject serverStateChange;
    private ObjectInputStream objectInputStream;
    private BackupComObject update;

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<ClientObject> clientList, ArrayList<FileObject> fileList, BackupComObject serverStateChange) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.clientList = clientList;
        this.fileList = fileList;
        this.serverStateChange = serverStateChange;
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
                    System.out.println("Update received:");
                    /*
                    TBD
                    System.out.println("New clients:");
                    Iterator<ClientObject> itr = update.getNewClients().iterator();
                    while (itr.hasNext())
                    {
                        System.out.println(itr.next().get_Client_info());
                    }
                    System.out.println("Disconnected clients:");
                    itr = update.getDisconnectedClients().iterator();
                    while (itr.hasNext())
                    {
                        System.out.println(itr.next().get_Client_info());
                    }*/
                    System.out.println("New Files:");
                    Iterator<FileObject> fileItr = update.getNewFileList().iterator();
                    while (fileItr.hasNext())
                    {

                        FileObject fileObject = fileItr.next();
                        System.out.println(fileObject.getFileName());

                        int index = fileListManager.findFileIndex(fileObject.getFileName());
                        ArrayList<ClientObject> oldSeeders = fileList.get(index).getSeeders();
                        ArrayList<ClientObject> newSeeders = fileObject.getSeeders();
                        
                        newSeeders.removeAll(oldSeeders);
                        oldSeeders.addAll(newSeeders);
                        fileList.set(index, new FileObject(fileObject.getFileName(), oldSeeders));

                    }
                    
                    System.out.println("My files after update: ");
                    fileItr = fileList.iterator();
                    while(fileItr.hasNext())
                    {
                        FileObject fileObject = fileItr.next();
                        Iterator<ClientObject> seeders = fileObject.getSeeders().iterator();
                        System.out.println(fileObject.getFileName());
                        while(seeders.hasNext())
                            System.out.println(seeders.next().get_Client_info());
                    }
                    /*
                    TBD
                    System.out.println("Removed Files:");
                    fileItr = update.getRemovedFiles().iterator();
                    while (fileItr.hasNext())
                    {
                        System.out.println(fileItr.next().getFileName());
                    }*/
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
                    else if(parsedInput.length == 3) {
                        if (parsedInput[0].equals("new-client-join-request")){
                            clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                            clientList.add(clientID);
                        }
                        clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                        clientList.add(clientID);                   // Add the new client to the Client list.
                    }
                    else if(parsedInput.length == 4) {
                        if (parsedInput[0].equals("add")) {
                            requestManager.clientRequestAdd(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3]), fileList);

                        //fileListManager.addFileToList(new FileObject(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3])), new ClientObject(parsedInput[2], parsedInput[3]));
                            System.out.println("My files after add: ");
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
            System.out.println(receivedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
        /*
            catch(InterruptedException e){
                e.printStackTrace();
            }*/
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
}