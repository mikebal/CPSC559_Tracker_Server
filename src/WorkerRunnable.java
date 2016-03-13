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
                
                /*
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                in.mark(256);
                receivedMSG = in.readLine();
            */
                receivedMSG = stringBuffer.toString();
                System.out.println(stringBuffer.toString());

                if(receivedMSG.equals("update"))
                {
                    ObjectInputStream objectInputStream = open(clientSocket);
                    update = (BackupComObject)objectInputStream.readObject();
                    System.out.println("Update received:");
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
                    }
                    System.out.println("New Files:");
                    Iterator<FileObject> fileItr = update.getNewFileList().iterator();
                    while (fileItr.hasNext())
                    {
                        System.out.println(fileItr.next().getFileName());
                    }
                    System.out.println("Removed Files:");
                    fileItr = update.getRemovedFiles().iterator();
                    while (fileItr.hasNext())
                    {
                        System.out.println(fileItr.next().getFileName());
                    }
                }

                else{

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //receivedMSG = in.readLine();  
                System.out.println(receivedMSG);       // Client should automatically send it's info to the server
                parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);  // Break up messages into commands separated by "'#"
                clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                clientList.add(clientID);                   // Add the new client to the Client list.
                while(!receivedMSG.equals("exit")) {
                    receivedMSG = in.readLine();
                    System.out.println(receivedMSG);

                    if(receivedMSG.equals("show user list"))
                    {
                        localOutput = requestManager.getClientListString(clientList);
                        System.out.println(localOutput);
                    }
                    else if(receivedMSG.contains("'#")) {
                        parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);
                        if(parsedInput.length == 2)
                        {
                            if(parsedInput[0].equals("add"))
                            {
                                requestManager.clientRequestAdd(parsedInput[1], clientID, fileList);
                            }
                            else if(parsedInput[0].equals("get"))
                            {                                    
                                response = requestManager.clientRequestGet(parsedInput[1], fileList);
                                output.write(response.getBytes());
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