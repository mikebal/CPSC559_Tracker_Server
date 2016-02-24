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

    public void connectToRedirect(String message){
    Socket clientSocket = null;
    InputStream is           = null;
    OutputStream os           = null;

    //attempt to connect to the server
    try
    {
        //create a socket
        clientSocket = new Socket("localhost", 9000);
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

            //if(message_IN.equals("READY FOR SERVER INFO"))

                out.println(message);

            //   {

         //   int serverCount = 0, count = 0;
         //   System.out.println("Server " + count + ": ");
         //   while(in.hasNextLine())
         //   {
          //      if(count == 5) {
           //         serverCount++;
          //          System.out.println("Server " + serverCount + ": ");

                   // count = 0;
                //}
                //String line = in.nextLine();
                //count++;
                //System.out.println(line);
            //}
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
