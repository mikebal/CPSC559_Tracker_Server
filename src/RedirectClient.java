import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.*;

/**
 * Created by Michael on 2/23/2016.
 */
public class RedirectClient {

    int port;
    String hostName;

    public RedirectClient(String hostName, int port){
        this.hostName = hostName;
        this.port = port;
    }

    public void connectToRedirect(String message){
        Socket clientSocket = null;
        InputStream is           = null;
        OutputStream os           = null;

        //attempt to connect to the server
        try
        {
            //create a socket
            clientSocket = new Socket(InetAddress.getByName(hostName), port);
            try
            {
                //get the input and output streams
                os = clientSocket.getOutputStream();
                is = clientSocket.getInputStream();
                Scanner in = new Scanner(is);
                PrintWriter out  = new PrintWriter(os, true /* autoflush */);
                Scanner userIn = new Scanner(System.in);
                String message_IN;

                out.println("New Server");

                while(!in.hasNextLine());
                 //   System.out.println("Waiting...");

                message_IN = in.nextLine();
                System.out.print(message_IN);

                out.println(message);

            }
            finally
            {
                clientSocket.close();
            }

        }
        catch(IOException ioe)
        {
            System.err.println("Couldn't connect to server");
            System.exit(0);
        }
    }

}
