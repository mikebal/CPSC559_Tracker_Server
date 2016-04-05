import java.util.*;

/**
 * Created by Michael on 3/3/2016.
 *
 * A simple class to manage requests received from Clients
 */
public class RequestManager {

    /**
     *
     * @param  clientList
     * @return string containing a list of all the clients
     */
    public String getClientListString(ArrayList<ClientObject> clientList)
    {
        final String CLIENT_SEPARATOR = "*************************************************\n";
        String displayList = CLIENT_SEPARATOR;

        for(int i = 0; i < clientList.size(); i++)
        {
            displayList += clientList.get(i).get_Client_info();
            displayList += "\n" + CLIENT_SEPARATOR;
        }
        return displayList;
    }

    /**
     * Function:  clientRequestAdd
     * @param fileName
     * @param clientObject
     * @param fileList
     *
     * Purpose: Add a clients host information to the file they are now sharing
     *          if the file is shared by other Clients the host information is added to that files list.
     *          else the filename is added to the file list with the Requester as the sole host until it is copied.
     *
     * @return boolean
     */
    public boolean clientRequestAdd(String fileName, ClientObject clientObject, ArrayList<FileObject> fileList)
    {
        boolean fileFound = false;
        for(int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).getFileName().equals(fileName)){
                fileFound = true;
                fileList.get(i).addSeeder(clientObject);
                break;
            }
        }
        if(!fileFound){
            FileObject newFile = new FileObject(fileName, clientObject);
            fileList.add(newFile);
        }
        return fileFound;
    }

    /**
     * Function:  clientRequestGet (GET)
     * @param fileName
     * @param fileList
     *
     * Purpose: Obtain and return all the hosts of a requested file (filename)
     * How: iterate through the list of files until searchName == filename, then request list of hosts of that file
     * @return
     */
    public String clientRequestGet(String fileName, ArrayList<FileObject> fileList)
    {
        String response = "File not found\n";
        for(int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).getFileName().equals(fileName))
                response = fileList.get(i).getAllPeersWithFile() + "\n";
        }
        System.out.println(response);
        return response;
    }

    public void clientRequestRemove(String fileName, ClientObject clientObject, ArrayList<FileObject> fileList)
    {
        ListIterator<FileObject> itr = fileList.listIterator();
        boolean fileFound = false;
        while(itr.hasNext() && fileFound == false){
            FileObject file = itr.next();
            if(file.getFileName().equals(fileName))
            {
                file.removeSeeder(clientObject);
                fileFound = true;
                if(!file.hasSeeders())
                    itr.remove();
            }
        }
    }

    /**
     * Function:  clientRequestLeave
     * @param clientObject
     * @param fileList
     * @param peerList
     *
     * Purpose: remove the clients host information form all files it was sharing, remove the Client from the peerList
     * How:  Search for the Clients information (IP/PORT) on all the files, if found remove.
     *       Remove the client form the peerList, search and remove
     */
    public void clientRequestLeave(ClientObject clientObject, ArrayList<FileObject> fileList, ArrayList<ClientObject> peerList){
        ListIterator<FileObject> itr = fileList.listIterator();
        ClientObject current;
        while(itr.hasNext()){
            FileObject file = itr.next();
            file.removeSeeder(clientObject);
            if(!file.hasSeeders())
                itr.remove();
        }

        for(int i = 0; i < peerList.size(); i++)    // For every member in the list
        {
            current = peerList.get(i);
            if(current.get_Client_info().equals(clientObject.get_Client_info()))    // If the Leaving use is found in the peer list Remove them and exit
            {
                peerList.remove(i);
                break;
            }
        }

    }

    public String showFileList(ArrayList<FileObject> fileList)
    {
        String response = "";
        for(int i = 0; i < fileList.size(); i++)
            response += fileList.get(i).getFileName() + "'#";

        return response;
    }
}
