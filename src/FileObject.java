import java.util.ArrayList;
import java.io.*;

/**
 * Created by Michael on 3/2/2016.
 *
 */
public class FileObject implements Serializable {
    private String fileName;
    private ArrayList<ClientObject> seeders = new ArrayList<>();
    //String Hash;  // Possible use to ensure file is the same

    public FileObject(String fileName, ClientObject clientObject){
        this.fileName = fileName;
        seeders.add(clientObject);
    }
    public FileObject(String fileName, ArrayList<ClientObject> seeders){
         this.fileName = fileName;
         this.seeders = seeders;
    }
    public String getFileName(){
        return this.fileName;
    }
    public void addSeeder(ClientObject newSeeder){
        if(!seeders.contains(newSeeder))
            seeders.add(newSeeder);
    }
    public void removeSeeder(ClientObject oldSeeder){
        seeders.remove(oldSeeder);
    }
    public boolean hasSeeders(){
        return seeders.size() > 0;
    }


    /*
        removeSeeder:
            Rather then passing a ClientObject we can possibly just pass the IP address as a string.
            If we hold the object it may be possible to simply call:
                    seeders.remove(disconnectedSeeder)
            as long as the reference object is held as long as the client is connected.
     */
    public boolean removeSeeder(String disconnectedSeederIP)
    {
        boolean isNowEmpty = false;

        for(int i = 0; i < seeders.size(); i++)
        {
            if(seeders.get(i).get_IP_Address().equals(disconnectedSeederIP)) {
                seeders.remove(i);
                if(seeders.isEmpty())
                    isNowEmpty = true;
                break;
            }
        }
        return isNowEmpty;
    }

    public ClientObject getFileSeeder_with_roundRobin()
    {
        ClientObject fileHost = this.seeders.get(0);
        this.seeders.remove(0);
        this.seeders.add(fileHost);
        return fileHost;
    }
    public String getAllPeersWithFile()
    {
        String hostList = "";

        for(int i = 0; i < this.seeders.size(); i++)
        {
            hostList += seeders.get(i).get_Client_info() + "'#";
        }
        getFileSeeder_with_roundRobin();
        return hostList;
    }


    public ArrayList<ClientObject> getSeeders()
    {
        return this.seeders;
    }
}
