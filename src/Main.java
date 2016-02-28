/**
 * Created by Michael on 2/23/2016.
 */
public class Main {

    public static void main(String[] args)
    {
        String hostName = args[0]; 
        int port = Integer.parseInt(args[1]);

        MultiThreadedServer server = new MultiThreadedServer(9000);
        new Thread(server).start();

        NetworkManager networkManager = new NetworkManager();
        String newServerMessage = networkManager.generateTrackerAdvertisement(server.getOpenPort());
        RedirectClient listServerCommunicator = new RedirectClient(hostName, port);
        listServerCommunicator.connectToRedirect(newServerMessage);

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
