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
	
	public void startLoop() {
		while (true) {
			wait(1);
			time++;
			
			if (time>=120) {
				time = 0;
			}
		}
	}
	
	public int getTime() {
		return time;
	}
}
