import java.util.ArrayList;

/**
 * Created by Michael on 2/23/2016.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        ArrayList<ClientObject> clientList = new ArrayList<>();
        ArrayList<ClientObject> serverList = new ArrayList<>();
        ArrayList<FileObject> fileList = new ArrayList<>();
        BackupComObject serverStateChanges = new BackupComObject();
        int startingPort = 9010;
        boolean isPrimaryServerRunning = true;

        /**
         * server: The connection point clients to join the network
         *
         * inputs:
         *          a starting point to try and open a port
         *          A list of the connected clients
         *          A list of the files that users are sharing
         *          boolean (false) indication that the server thread being started is not a backup server
         *          BackupComObject serverStateChanges - object that is updates when a user changes things
         *
         */
        MultiThreadedServer server = new MultiThreadedServer(startingPort, clientList, fileList, false, serverStateChanges, null);
        new Thread(server).start();
        Thread.sleep(1000);
        startingPort = server.getOpenPort();
        System.out.println("Open port Server = " + startingPort);
        /**
         * backupServer: The connection point other servers to join the network
         *
         * inputs:
         *          a starting point to try and open a port
         *          A list of the connected clients
         *          A list of the files that users are sharing
         *          boolean (true) indication that the server thread being started is for a backup server
         *          BackupComObject serverStateChanges - object that is updates when a user changes things
         *
         */
        startingPort++;
        MultiThreadedServer backupServer = new MultiThreadedServer(startingPort, clientList, fileList, true, serverStateChanges, serverList);
        new Thread(backupServer).start();
        Thread.sleep(1000);
        System.out.println("Open port Backup-Server = " + backupServer.getOpenPort());

        NetworkManager networkManager = new NetworkManager();
        String newServerMessage = networkManager.generateTrackerAdvertisment(backupServer.getOpenPort());

        /**
         * BackupComManager broadcast
         *
         * In the even that this application instance is not the first(Primary) to run the then this
         * server will connect to the primary server and announce it's self with its info*/
        if(isPrimaryServerRunning)
        {
            BackupComManager broadcast = new BackupComManager();
            broadcast.client(newServerMessage, 9011);
        }
        /**
         *  RedirectClient listServerCommunicator
         *
         *  Once the server is running, the listServer is notified of the new server and its address and port
         */
        newServerMessage = networkManager.generateTrackerAdvertisment(server.getOpenPort());
        RedirectClient listServerCommunicator = new RedirectClient();
        listServerCommunicator.connectToRedirect(newServerMessage);


        try {
            Thread.sleep(1000 * 1000);
            while(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
