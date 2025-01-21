import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Stack;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * Jiawei Chen, Raymond So <p>
 * 01/15/2025 <p>
 * Handles all frontend UI for the user. Includes interactive elements for the GPS to allow the user to select
 * starting position and destination, and allows for alternative routes based off checkbox selections.
 */
public class InterfaceUI extends JFrame {
    //TODO: Reorganize variables
    private final BorderLayout inter = new BorderLayout();
    private static final InterfaceUI app = new InterfaceUI();
    private JLabel mouseCoordinate;
    public static JLabel startLabel;
    public static JLabel endLabel;
    private static JLabel trafficTimer;
    public static JButton start;
    public static JButton destination;
    public JButton routeCalculate;
    public JButton clear;
    public JLabel status;
    private JCheckBox speed;
    private JCheckBox traffic;
    public static int nodeSelection = 0; //For buttons

    private static AudioInputStream audioInputStream; // AudioInputStream
	private static Clip notifAudio;
	private static Clip promptAudio;
	private static Clip doneAudio;
    

    static GPSApp gpsApp = new GPSApp(null);

    /**
     * Constructor, handles final display layout, and contains ActionListeners for all interactive elements
     */
    public InterfaceUI() {
        super("GPS Interface");
        setLayout(inter);
        JPanel mapPanel = gpsApp.panel;

        add(selectUI(), BorderLayout.EAST); 
        add(mapPanel, BorderLayout.CENTER); //Add map to main UI
        //add(AssistedMapper.panel, BorderLayout.CENTER); //Toggleable for intersection mapping
        add(debugUI(), BorderLayout.SOUTH);

        //ActionListeners
        MouseHandler mouse = new MouseHandler();
        ActionListener buttonListener = new ButtonEventListener();
        ItemListener checkListener = new CheckBoxEventListener();

        //mapPanel - For use in tracking mouse coordinates & handling clicks
        mapPanel.addMouseListener(mouse);
        mapPanel.addMouseMotionListener(mouse);

        //selectUI - To handle traffic & speed limit considerations, and GPS main functions
        traffic.addItemListener(checkListener);
        speed.addItemListener(checkListener);

        start.addActionListener(buttonListener);
        destination.addActionListener(buttonListener);
        routeCalculate.addActionListener(buttonListener);
        clear.addActionListener(buttonListener);
     
    }

    /**
     * Handles layout for the right side of the display UI (interaction)
     * @return ui: Final panel containing all selectUI elements
     */
    private JPanel selectUI() {
        JPanel ui = new JPanel(new BorderLayout()); //Main layout
        JPanel group = new JPanel(new GridLayout(2, 1));
        ui.add(new JPanel(), BorderLayout.WEST);
        ui.add(new JPanel(), BorderLayout.EAST);

        //Top
        JPanel topPanel = new JPanel(new GridLayout(5, 1, 10, 5));
        start = new JButton("[Select Start]");
        destination = new JButton("[Select Destination]");
        routeCalculate = new JButton("[Calculate Routes]");
        clear = new JButton("[Clear Selection]");

        topPanel.add(new JLabel("<< Actions >>", SwingConstants.CENTER));
        topPanel.add(start);
        topPanel.add(destination);
        topPanel.add(routeCalculate);
        topPanel.add(clear);

        //Bottom
        JPanel bottomPanel = new JPanel(new GridLayout(5, 1, 10, 5));

        //Traffic Box - If the GPS should consider traffic levels
        JPanel trafficCheck = new JPanel(new FlowLayout());
        traffic = new JCheckBox();
        JLabel trafficCheckLabel = new JLabel("Consider Traffic");
        trafficCheck.add(trafficCheckLabel);
        trafficCheck.add(traffic);

        //Speed Box - If the GPS should consider speed limits
        JPanel speedCheck = new JPanel(new FlowLayout());
        speed = new JCheckBox();
        JLabel speedCheckLabel = new JLabel("Consider Speed Limit");
        speedCheck.add(speedCheckLabel);
        speedCheck.add(speed);

        bottomPanel.add(new JLabel("<< Route Options >>", SwingConstants.CENTER));
        bottomPanel.add(trafficCheck);
        bottomPanel.add(speedCheck);

        //Main UI
        group.add(topPanel);
        group.add(bottomPanel);

        //Status Bar
        status = new JLabel("Program Status: Idle");

        ui.add(group, BorderLayout.CENTER);
        ui.add(status, BorderLayout.NORTH);
        return ui; 
    } 

    /**
     * Handles layout for the bottom of the display UI (information, e.g. mouse coordinate and reset timer)
     * @return info: Final panel containing all debugUI elements
     */
    private JPanel debugUI() {
        JPanel info = new JPanel(new BorderLayout()); //Main layout
        JPanel mCoord = new JPanel(new FlowLayout());

        mouseCoordinate = new JLabel("[Mouse Coordinate]"); //Constantly displays the cursor's coordinate on the map
        mCoord.add(mouseCoordinate);

        trafficTimer = new JLabel("[Reset Timer]"); //Countdown to next traffic level update

        info.add(mCoord, BorderLayout.WEST);
        info.add(trafficTimer, BorderLayout.EAST);
        return info;
    }

    public static void main(String[] args) {
		app.setSize(1200, 600); // set frame size
		app.setVisible(true); // display frame
        app.setCursor(Cursor.CROSSHAIR_CURSOR); 
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.out.println("Loading nodes... please be patient");
        while(true) { //To constantly update the traffic level update countdown
            trafficTimer.setText("Traffic update in: " + (120-TimerListener.getTime()));
            if (TimerListener.getTime()>=120) {
            	gpsApp.generateTraffic();
            }
        }
        
    }

//Action Handler Classes
    /**
     * Inner class for mouse actions.
     */
    private class MouseHandler extends MouseAdapter {
		// MouseListener event handlers
		// handle event when mouse released immediately after the press

		// handle event when mouse pressed
		public void mousePressed( MouseEvent event ){
			mouseCoordinate.setText( String.format( "Pressed at [%d, %d]",event.getX(), event.getY() ) );
            playAudio(0);
		} // end method mousePressed
		
		// handle event when mouse exits an area
		public void mouseExited( MouseEvent event ){
			mouseCoordinate.setText("[Mouse is not within map]");
		} // end method mouseExited
		
		// handle event when a user moves the mouse
		public void mouseMoved( MouseEvent event ){
			mouseCoordinate.setText(String.format( "[Current Coordinate: (%d, %d)]",event.getX(), event.getY()));
		} // end method mouseMoved
		
	} // end inner class MouseHandler

    /**
     * Inner class for JCheckBox selection/deselection
     */
    private class CheckBoxEventListener implements ItemListener {
		@Override
		public void itemStateChanged( ItemEvent event ) { 
            if (event.getSource() == traffic) { //Traffic Level considerations
                System.out.println("Traffic: ");
                if (traffic.isSelected()) {
                    System.out.print("Selected"); 
                } else {
                    System.err.print("Unselected");
                }
            } else if (event.getSource() == speed) { //Speed Limit considerations
                System.out.println("Speed: ");
                if (speed.isSelected()) {
                    System.out.print("Selected");
                } else {
                    System.err.print("Unselected");
                }
            } 

            playAudio(1);

		}
	}

    /**
     * Inner class for JButton actions.
     */
    private class ButtonEventListener implements ActionListener {
        @Override
		public void actionPerformed( ActionEvent event ) {
            if (nodeSelection == 0) {
                    if (event.getSource() == start) { //Allows the user to select their starting position
                        //Will only run while node selection isnt active
                        System.out.println("Toggle Starting Coordinate Selection");
                        start.setText("Awaiting input.."); //Prompt
                        nodeSelection = 1; //Set node selection type to 1 (start)
                        playAudio(0);
                    } else if (event.getSource() == destination) { //Allows the user to select their destination
                        System.out.println("Toggle Destination Coordinate Selection");
                        destination.setText("Awaiting input...");
                        nodeSelection = 2; //Set node selection type to 2 (destination)
                        playAudio(0);
                    } else if (event.getSource() == routeCalculate && gpsApp.selectedNode1 != null && gpsApp.selectedNode2 != null) { //Calculates routes given considerations
                        System.out.println("Route Calculations"); //debug
                        status.setText("Program Status: Calculating"); //status
                        System.out.println("Starting Location: " + gpsApp.selectedNode1);
                        System.out.println("Ending Location: " + gpsApp.selectedNode2);
    
                        Stack<Node> path = new Stack<Node>(); //creating the highlight
                        path.add(gpsApp.selectedNode1);
                        gpsApp.clearPath();
                        gpsApp.path = gpsApp.algorithm("Distance", gpsApp.selectedNode1, gpsApp.selectedNode1, gpsApp.selectedNode2, path, new HashSet<Node>(), (traffic.isSelected()?"traffic,":"")+(speed.isSelected()?"speed,":""), new HashSet<Stack<Node>>());
                        status.setText("Distance: " + (gpsApp.selectedNode2.x - gpsApp.selectedNode1.x + gpsApp.selectedNode2.y - gpsApp.selectedNode1.y));
                        playAudio(2);
                    } else if (event.getSource() == clear) {
                        //reset all values to original
                        start.setText("[Select Start]");
                        destination.setText("[Select Destination]");
                        status.setText("Program Status: Idle");
                        gpsApp.selectedNode1 = null;
                        gpsApp.selectedNode2 = null;
                        System.out.println("Selections cleared-1"); //debug
                        status.setText("Program Status: Idle");
                        gpsApp.clearPath();
                        gpsApp.repaint();
                        playAudio(1);
                    }
            }

		} // end method actionPerformed	
    }

    public static void playAudio(int audioNum) {
    	try {
    		notifAudio = AudioSystem.getClip();
    		audioInputStream = AudioSystem.getAudioInputStream(new File("notif.wav")); // create AudioInputStream object
    		notifAudio.open(audioInputStream); // open audioInputStream to the clip
    		
    		promptAudio = AudioSystem.getClip();
    		audioInputStream = AudioSystem.getAudioInputStream(new File("prompt.wav")); // create AudioInputStream object
    		promptAudio.open(audioInputStream); // open audioInputStream to the clip
    		
    		doneAudio = AudioSystem.getClip();
    		audioInputStream = AudioSystem.getAudioInputStream(new File("done.wav")); // create AudioInputStream object
    		doneAudio.open(audioInputStream); // open audioInputStream to the clip
    		
    		if (audioNum == 0) {
    			promptAudio.setFramePosition(0);
    			promptAudio.start(); // play AudioClip once
    		} else if (audioNum == 1){
    			notifAudio.setFramePosition(0);
    			notifAudio.start(); // play AudioClip once
    		} else {
    			doneAudio.setFramePosition(0);
    			doneAudio.start();
    		}
    	} catch (Exception e) {
    		
    	}
	}
}