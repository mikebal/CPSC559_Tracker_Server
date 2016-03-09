
import java.util.ArrayList;
import java.util.*;

/**
 * Created by Michael on 2/23/2016.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        ArrayList<ClientObject> clientList = new ArrayList<>();
        ArrayList<ClientObject> serverList = new ArrayList<>();
        ArrayList<ClientObject> serverList2 = new ArrayList<>();
        ArrayList<ClientObject> disconnectedClients = new ArrayList<ClientObject>();
        ArrayList<ClientObject> newClients = new ArrayList<ClientObject>();
        ArrayList<FileObject> newFiles= new ArrayList<FileObject>();
        ArrayList<FileObject> removedFiles= new ArrayList<FileObject>();
        ArrayList<FileObject> fileList = new ArrayList<>();

        int startingPort = 9010;
        boolean isPrimaryServerRunning = false;

        /*initializing objects
        */
        ClientObject clientObject = new ClientObject(NetworkManager.getIPaddress(), String.valueOf(8500));
        ClientObject clientObject2= new ClientObject(NetworkManager.getIPaddress(), String.valueOf(8999));
        ClientObject serverObject= new ClientObject(NetworkManager.getIPaddress(), String.valueOf(9010));
        FileObject fileObject = new FileObject("added-file1", clientObject);
        FileObject fileObject2 = new FileObject("removed-file2", clientObject);
       
        newFiles.add(fileObject);
        removedFiles.add(fileObject2);
        disconnectedClients.add(clientObject2);
        newClients.add(clientObject);
        BackupComObject serverStateChanges = new BackupComObject(newFiles, removedFiles, disconnectedClients, newClients);

        serverList.add(serverObject);
        serverList2.add(serverObject);


        //ClientObject serverObject= new ClientObject(NetworkManager.getIPaddress(), String.valueOf(9000));
        //ClientObject serverObject2= new ClientObject(NetworkManager.getIPaddress(), String.valueOf(8001));
        //serverList.add(serverObject);
        //serverList.add(serverObject2);

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

        Updater updater = new Updater(2000, serverList2, serverStateChanges);
        updater.start();

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
