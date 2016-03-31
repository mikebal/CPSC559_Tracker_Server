/**
 * Created by Michael on 2/23/2016.
 */
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
    private ArrayList<Integer> activePeers;

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<ClientObject> clientList, ArrayList<FileObject> fileList, BackupComObject serverStateChange, ArrayList<Integer> registeredPeers) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.clientList = clientList;
        this.fileList = fileList;
        this.serverStateChange = serverStateChange;
        this.activePeers = registeredPeers;
    }

    public void run() {
       ClientObject clientID = null;

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
                System.out.println(receivedMSG);
                parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);  // Break up messages into commands separated by "'#"
                if(parsedInput.length == 3 && parsedInput[0].equals("new")) {
                    if(activePeers.get(0) > 2)
                    {
                        output.write("rejected".getBytes());
                    }
                    else {
                        output.write("accepted".getBytes());
                        clientID = new ClientObject(parsedInput[0], parsedInput[1]);
                        clientList.add(clientID);                   // Add the new client to the Client list.
                    }
                }
                else {
                    if (receivedMSG.equals("show user list")) {
                        localOutput = requestManager.getClientListString(clientList);
                        output.write(localOutput.getBytes());
                        output.flush();
                    } else if (receivedMSG.equals("show file list")) {
                        localOutput = requestManager.showFileList(fileList);
                        output.write(localOutput.getBytes());
                    } else if (receivedMSG.contains("'#")) {
                        parsedInput = receivedMSG.split(SPECIAL_BREAK_SYMBOL);
                        if (parsedInput.length == 4) {
                            if (parsedInput[0].equals("add")) {
                                requestManager.clientRequestAdd(parsedInput[1], new ClientObject(parsedInput[2], parsedInput[3]), fileList);
                            }
                        } else if (parsedInput.length == 2) {
                            if (parsedInput[0].equals("get")) {
                                response = requestManager.clientRequestGet(parsedInput[1], fileList);
                                output.write(response.getBytes());
                            }
                        }

                    }
                }

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