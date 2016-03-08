import java.util.*;

/*
* MyTimerTask class
* @author William Cho
* @version 1.0
*/

public class MyTimerTask extends TimerTask{

	Updater parent;

	/*
	* constructor
	* @param parent - the parent thread
	*/

	public MyTimerTask(Updater parent){
		this.parent = parent;
	}

	/*
	* calls sendRecurringUpdate() in parent thread
	*/
	public void run(){
		parent.sendRecurringUpdate();
	}



}