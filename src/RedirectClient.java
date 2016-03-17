import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Michael on 2/23/2016.
 */
public class RedirectClient {

    public void connectToRedirect(String message, String ipAddress){
    Socket clientSocket = null;
    InputStream is           = null;
    OutputStream os           = null;

    //attempt to connect to the server
    try
    {
        //create a socket
        clientSocket = new Socket(ipAddress, 9000);
        try
        {
            //get the input and output streams
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();
            Scanner in = new Scanner(is);
            PrintWriter out  = new PrintWriter(os, true /* autoflush */);
            String message_IN;

            out.println("New Server");
            message_IN = in.nextLine();

            System.out.println("Message from (RedirectClient): " + message_IN);
            System.out.println("Sending server info...");
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
