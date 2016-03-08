import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/**
 * Created by Michael on 3/5/2016.
 */
public class BackupComManager {

        public void client(String msg, int targetPort) {
            Socket clientSocket = null;
            InputStream is = null;
            OutputStream os = null;

            // attempt to connect to the server
            try {
                // create a socket
                clientSocket = new Socket("localhost", targetPort);
                System.out.println("Notifying Primary Sever of backup presence...");
                // get the input and output streams
                os = clientSocket.getOutputStream();
                is = clientSocket.getInputStream();
                Scanner in = new Scanner(is);
                PrintWriter out = new PrintWriter(os, true /* autoflush */);

                out.println(msg);

                in.close();
                clientSocket.close();
            }

            catch (IOException ioe) {
                System.err
                        .println("Couldn't create socket or could not connect to server");
                System.exit(0);
            }
        }
}


