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
        NetworkManager networkManager = new NetworkManager();
        String redirectServerAddress = new String();
        boolean acceptClients = true;

        int startingPort = 9010;
        int redirectPort = 9000;


        BackupComObject serverStateChanges = new BackupComObject();

        if(args.length >= 3){
            networkManager.setTrackerName(args[0]);
            redirectServerAddress = args[1];
            redirectPort = Integer.parseInt(args[2]);
            if(args.length == 4)
                acceptClients = false;
        }
        else{
            System.out.println("incorrect input parameters");
            System.exit(0);
        }

        System.out.println("acceptclients " + acceptClients);

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

        MultiThreadedServer server = new MultiThreadedServer(startingPort, clientList, fileList, false, serverStateChanges, serverList, acceptClients);
        new Thread(server).start();
        Thread.sleep(1000);
        startingPort = server.getOpenPort();
        System.out.println("Open port Server = " + startingPort);


        /**
         *  RedirectClient listServerCommunicator
         *
         *  Once the server is running, the listServer is notified of the new server and its address and port
         */

        String newServerMessage = networkManager.generateTrackerAdvertisment(server.getOpenPort());
        RedirectClient listServerCommunicator = new RedirectClient(redirectServerAddress, redirectPort);
        listServerCommunicator.connectToRedirect(newServerMessage);

        Updater updater = new Updater(5000, serverList, serverStateChanges);
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