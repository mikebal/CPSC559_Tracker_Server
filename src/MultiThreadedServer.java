/**
 * Created by Michael on 2/23/2016.
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

public class MultiThreadedServer implements Runnable {

    protected int serverPort = 9000;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    private ArrayList<ClientObject> clientList;
    private ArrayList<ClientObject> serverList;
    private ArrayList<FileObject> fileList;
    private boolean isClientServer;
    private BackupComObject serverStatusChanges;
    private Updater updater;

    public MultiThreadedServer(int port, ArrayList<ClientObject> clientList, ArrayList<FileObject> fileList, boolean isClientServer, BackupComObject backupObject, ArrayList<ClientObject> serverList) {
        this.serverPort = port;
        this.clientList = clientList;
        this.fileList = fileList;
        this.isClientServer = isClientServer;
        this.serverStatusChanges = backupObject;
        this.serverList = serverList;



    }
    public int getOpenPort(){
        return serverPort;
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            if(!isClientServer) {
                new Thread(
                        new WorkerRunnable(
                                clientSocket, "Multithreaded Server", clientList, fileList, serverStatusChanges, serverList)
                ).start();
            }
            else{
                new Thread(
                        new BackupServer(
                                clientSocket, "Multithreaded Backup Server", clientList, fileList, serverStatusChanges, serverList)
                ).start();
            }

        }
        System.out.println("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
            updater.stop();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
           } catch (IOException e) {
           // throw new RuntimeException("Cannot open port 8080", e);
            this.serverPort++;
            openServerSocket();
        }
    }
}