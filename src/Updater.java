import java.util.*;
import java.net.*;
import java.io.*;

public class Updater extends Thread
{
	public int updateInterval;
	public BackupComObject update;
	public ArrayList<ClientObject> serverList;
	public ObjectOutputStream outObj = null;

	public Updater(int updateInterval, ArrayList<ClientObject> serverList, BackupComObject update){
		this.updateInterval = updateInterval;
		this.serverList = serverList;
		this.update = update;
	}

	public void run(){
		MyTimerTask task = new MyTimerTask(this);
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(task, updateInterval, updateInterval);
	}

	/*to be completed*/
	public void sendRecurringUpdate()  
	{
		try{
			for(int i = 0; i < serverList.size(); i++)
			{
				Socket sock = new Socket(InetAddress.getByName(serverList.get(i).get_IP_Address()), serverList.get(i).getPort());
				outObj = new ObjectOutputStream(sock.getOutputStream());	
				outObj.writeObject(update);
				outObj.flush();
				sock.close();		
			}
		}
		catch(IOException e){
			//e.printStackTrace();
		}
	}
}