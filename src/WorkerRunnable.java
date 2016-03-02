/**
 * Created by Michael on 2/23/2016.
 */
import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {

        String trackerString = "";

        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            String recevedMSG = "";//input.read();

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                while(!recevedMSG.equals("exit")) {
                    recevedMSG = in.readLine();
                    System.out.println(recevedMSG);
                }
            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }


            long time = System.currentTimeMillis();
            output.write((trackerString).getBytes());
            output.close();
            input.close();
            System.out.println(recevedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}