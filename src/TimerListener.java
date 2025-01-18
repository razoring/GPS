import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class TimerListener {
	static int time = 0;
	
	public void wait(int i) {
		try {
			ActionListener ticktock = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
				}
			};
			
			Timer timer = new Timer(i*100, ticktock); // timer is ticking
			timer.setRepeats(false); // by using this, we are asking to off timer once
			timer.start();
			Thread.sleep(i*1000);
		} catch (InterruptedException e) {
			
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
	
	public static int getTime() {
		return time;
	}
}
