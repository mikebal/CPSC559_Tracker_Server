import java.util.*;

/**
 * Created by Michael on 3/3/2016.
 */
public class RequestManager {


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

    public String clientRequestGet(String fileName, ArrayList<FileObject> fileList)
    {
        String response = "File not found\n";
        for(int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).getFileName().equals(fileName))
                response = fileList.get(i).getFileSeeder_with_roundRobin().get_Client_info() + "\n";
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

    public void clientRequestLeave(ClientObject clientObject, ArrayList<FileObject> fileList){
        ListIterator<FileObject> itr = fileList.listIterator();

        while(itr.hasNext()){
            FileObject file = itr.next();
            file.removeSeeder(clientObject);
            if(!file.hasSeeders())
                itr.remove();
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
