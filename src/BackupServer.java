import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/** Description of WorkerRunnable
 *
 * @author Michael Balcerzak
 * @version 1.0 March 5, 2016.
 */

public class BackupServer implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;
    private static final String SPECIAL_BREAK_SYMBOL = "'#";
    private ArrayList<ClientObject> serverList;
    private ArrayList<ClientObject> clientList;
    private ArrayList<FileObject> fileList;
    private BackupComObject serverChangeList;


    public BackupServer(Socket clientSocket, String serverText, ArrayList<ClientObject> clientList, ArrayList<FileObject> fileList, BackupComObject serverStateChange,
                        ArrayList<ClientObject> backupServers) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.clientList = clientList;
        this.serverList = backupServers;
        this.fileList = fileList;
        this.serverChangeList = serverStateChange;
    }

    public void run() {
        ClientObject serverID = null;
      //  System.out.println("BACKUP");
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            String receivedMSG = "";
            String response;
            String[] parsedInput;
            RequestManager requestManager = new RequestManager();
            String localOutput;

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                receivedMSG = in.readLine();                // Client should automatically send it's info to the server
                System.out.println("New Server Joined Group INFO: " + receivedMSG);
                parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);  // Break up messages into commands separated by "'#"
                serverID = new ClientObject(parsedInput[0], parsedInput[1]);
                serverList.add(serverID);                   // Add the new client to the Client list.
/*
                while(!receivedMSG.equals("exit")) {
                    receivedMSG = in.readLine();
                    System.out.println(receivedMSG);

                    if(receivedMSG.equals("show server list"))
                    {
                        localOutput = requestManager.getClientListString(serverList);
                        System.out.println(localOutput);
                    }
                    else if(receivedMSG.contains("'#")) {
                        parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);
                        if(parsedInput.length == 3)
                        {
                            if(parsedInput[0].equals("addNewBackup"))
                            {
                                //requestManager.clientRequestAdd(parsedInput[1], clientID, fileList);
                            }
                        }
                    }
                }*/
            } catch (IOException e) {
                System.out.println("Read failed");
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
}