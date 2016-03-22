import java.util.ArrayList;

/**
 * Created by Michael on 3/2/2016.
 *
 */
public class FileListManager {
    private ArrayList<FileObject> fileList;

    public FileListManager(ArrayList<FileObject> list)
    {
        this.fileList = list;
    }

    public void addFileToList(FileObject newFile, ClientObject fileHost)
    {
        int index;
        index = findFileIndex(newFile.getFileName());

        if(index == -1)
        {
            newFile.addSeeder(fileHost);
            fileList.add(newFile);
        }
        else
        {
            fileList.get(index).addSeeder(fileHost);
        }
    }



    protected int findFileIndex(String fileName){
        int index = -1;

        for(int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).getFileName().equals(fileName)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public String getFileSeederInfo(String filename)
    {
        String seederInfo = "";
        int index;
        index = findFileIndex(filename);
        ClientObject fileHost;

        if(index == -1)
            seederInfo = "File Not Found";
        else
        {
            fileHost = fileList.get(index).getFileSeeder_with_roundRobin();
            seederInfo = fileHost.get_Client_info();
        }

        return seederInfo;
    }

    public void manageDisconnect(String IP_address)
    {
        for(int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).removeSeeder(IP_address))
                fileList.remove(i);
        }
    }
}
