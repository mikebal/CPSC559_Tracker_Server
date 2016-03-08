import java.util.*;

public class Updater extends Thread
{
	int updateInterval;

	public Updater(int updateInterval){
		this.updateInterval = updateInterval;
	}

	public void run(){
		MyTimerTask task = new MyTimerTask(this);
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(task, updateInterval, updateInterval);
	}

	/*to be completed*/
	public void sendRecurringUpdate()
	{
		System.out.println("Sending update to all Trackers of my category");
	}

}