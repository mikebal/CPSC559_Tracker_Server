/**
 * Created by Michael on 2/23/2016.
 */
public class Main {

    public static void main(String[] args)
    {
        MultiThreadedServer server = new MultiThreadedServer(9001);
        new Thread(server).start();
        String newServerMessage = "123456789'#CATS on Cats'#9090'#0'#100'#";
        RedirectClient listServerComunicator = new RedirectClient();
        listServerComunicator.connectToRedirect(newServerMessage);


        try {
            Thread.sleep(20 * 1000);
            while(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
