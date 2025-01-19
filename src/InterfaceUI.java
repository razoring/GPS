import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Stack;
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
    private JButton forceUpdate;
    public static JButton startButton;
    public static JButton destination;
    public JButton routeCalculate;
    public JButton clear;
    public JButton status;
    private JCheckBox speed;
    private JCheckBox traffic;
    public static int nodeSelection = 0; //For buttons
    

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

        startButton.addActionListener(buttonListener);
        destination.addActionListener(buttonListener);
        routeCalculate.addActionListener(buttonListener);
        clear.addActionListener(buttonListener);

        //debugUI - Allows the user to force update the UI.
        forceUpdate.addActionListener(buttonListener);

        
    }

    /*
     * Separate class for handling the top right of the UI. Contains the information for the InterfaceUI 
     * (e.g. Start/End destination, current UI status)
     */
    private JPanel infoUI(){ 
        JPanel info = new JPanel();


        return info;
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
        startButton = new JButton("[Select Start]");
        destination = new JButton("[Select Destination]");
        routeCalculate = new JButton("[Calculate Routes]");
        clear = new JButton("[Clear Selection]");

        topPanel.add(new JLabel("<< Actions >>", SwingConstants.CENTER));
        topPanel.add(startButton);
        topPanel.add(destination);
        topPanel.add(routeCalculate);
        topPanel.add(clear);

        //Bottom
        JPanel bottomPanel = new JPanel(new GridLayout(5, 1, 10, 5));

       // JButton quickest = new JButton("[Fastest Route]");

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

        ui.add(group, BorderLayout.CENTER);

        return ui; 
    } 

    /**
     * Handles layout for the bottom of the display UI (information, e.g. mouse coordinate and reset timer)
     * @return info: Final panel containing all debugUI elements
     */
    private JPanel debugUI() {
        JPanel info = new JPanel(new BorderLayout()); //Main layout
        JPanel mCoord = new JPanel(new FlowLayout());
        JPanel traffic = new JPanel(new FlowLayout());

        //JList<String> aiMapTest = new JList<>();
        mouseCoordinate = new JLabel("[Mouse Coordinate]"); //Constantly displays the cursor's coordinate on the map
        mCoord.add(mouseCoordinate);

        trafficTimer = new JLabel("[Reset Timer]"); //Countdown to next traffic level update
        forceUpdate = new JButton("[Force Update]"); //Allows the user to forcefully update the UI
        traffic.add(trafficTimer);
        traffic.add(forceUpdate);

       //info.add(aiMapTest, BorderLayout.CENTER);
        info.add(mCoord, BorderLayout.WEST);
        info.add(traffic, BorderLayout.EAST);
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

		/*
        public void mouseClicked( MouseEvent event ){
			mouseCoordinate.setText( String.format( "Clicked at [%d, %d]",event.getX(), event.getY() ) );
		} // end method mouseClicked
		*/

		// handle event when mouse pressed
		public void mousePressed( MouseEvent event ){
			mouseCoordinate.setText( String.format( "Pressed at [%d, %d]",event.getX(), event.getY() ) );
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

		}
	}

    /**
     * Inner class for JButton actions.
     */
    private class ButtonEventListener implements ActionListener {
        @Override
		public void actionPerformed( ActionEvent event ) {
            if (nodeSelection == 0) {
                if (event.getSource() == startButton) { //Allows the user to select their starting position
                    //Will only run while node selection isnt active
                    System.out.println("Toggle Starting Coordinate Selection");
                    //startButton.setText("Awaiting input.."); //Prompt
                    nodeSelection = 1; //Set node selection type to 1 (start)
                } else if (event.getSource() == destination) { //Allows the user to select their destination
                    System.out.println("Toggle Destination Coordinate Selection");
                    //destination.setText("Awaiting input...");
                    nodeSelection = 2; //Set node selection type to 2 (destination)
                } else if (event.getSource() == routeCalculate && gpsApp.selectedNode1 != null && gpsApp.selectedNode2 != null) { //Calculates routes given considerations
                    System.out.println("Route Calculations");
                    System.out.println("Starting Location: " + gpsApp.selectedNode1);
                    System.out.println("Ending Location: " + gpsApp.selectedNode2);
                    //(Stack<Node>) removed
                    Stack<Node> path = new Stack<Node>();
                    path.add(gpsApp.selectedNode1);
                    gpsApp.path = gpsApp.algorithm("Distance", gpsApp.selectedNode1, gpsApp.selectedNode1, gpsApp.selectedNode2, path, new HashSet<Node>(), (traffic.isSelected()?"traffic,":"")+(speed.isSelected()?"speed,":""), new HashSet<Stack<Node>>());
                } else if (event.getSource() == forceUpdate) { //Forcefully updates the UI
                    System.out.println("Force Update mapPanel");
                    gpsApp.generateTraffic();
            		gpsApp.repaint();
                } else if (event.getSource() == clear) {
                    //start.setText("[Select Start]");
                    //destination.setText("[Select Destination]");
                    gpsApp.selectedNode1 = null;
                    gpsApp.selectedNode2 = null;
                    System.out.println("Selections cleared");
                    gpsApp.clearPath();
            		gpsApp.repaint();
                }
            }

		} // end method actionPerformed	
    }
}