import java.util.*;
import java.net.*;
import java.io.*;

public class Updater extends Thread
{
	public int updateInterval;
	public BackupComObject update;
	public ArrayList<ClientObject> serverList;
	public ObjectOutputStream out = null;

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
				System.out.println("\n DEBUG" );
				System.out.println(serverList.get(i).get_IP_Address() + " " + serverList.get(i).getPort());
				Socket sock = new Socket("localhost", 9010);
				PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
				out.println("update");
				//Socket sock = new Socket(InetAddress.getByName(serverList.get(i).get_IP_Address()), serverList.get(i).getPort());
				//out = new ObjectOutputStream(sock.getOutputStream());	
				//out.writeObject("hello");//out.writeObject(update);
				out.flush();
				sock.close();		
			}
			//out.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}