import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Raymond So, Jiawei Chen
 * 01/17/2025
 * Handles the timer for the GPS.
 */
public class TimerListener {
	static int time = 0;
	final static int DELAY = 120000; //2 minutes to milliseconds
	
	//Constructor. Makes the timer for traffic updates
	public static void wait(int i) {
		try {
			ActionListener ticktock = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			};
			
			Timer timer = new Timer(DELAY, ticktock); // timer is ticking
			timer.setRepeats(false); // by using this, we are asking to off timer once
			timer.start();
			Thread.sleep(i*1000);
			
		} catch (InterruptedException expn) {
			System.out.println("Interrupted.");
		}
	}

	public int startLoop(int t) {
		wait(1);
		t++;
		
		if (t>=120) {
			t = 0;
			
		}
		time = t;
		return startLoop(time);
	}
	
	//Returns current time value
	public static int getTime() {
		return time;
	}
}
