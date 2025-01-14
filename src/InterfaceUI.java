import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.util.Arrays;

public class InterfaceUI extends JFrame {

    private BorderLayout inter = new BorderLayout();
    private static InterfaceUI app = new InterfaceUI();
    public JLabel mouseCoordinate;
    public JButton forceUpdate;
    public JButton start;
    public JButton destination;
    public JButton routeCalculate;
    public JCheckBox speed;
    public JCheckBox traffic;

    public InterfaceUI() {
        super("GPS Interface");
        setLayout(inter);
        JPanel mapPanel = GPSApp.panel;

        add(selectUI(), BorderLayout.EAST);
        //add(mapPanel, BorderLayout.CENTER);
        add(AssistedMapper.panel, BorderLayout.CENTER);
        add(infoUI(), BorderLayout.SOUTH);

        //ActionListeners
        MouseHandler mouse = new MouseHandler();
        ActionListener buttonListener = new ButtonEventListener();
        ItemListener checkListener = new CheckBoxEventListener();

        //mapPanel
        mapPanel.addMouseListener(mouse);
        mapPanel.addMouseMotionListener(mouse);

        //selectUI
        traffic.addItemListener(checkListener);
        speed.addItemListener(checkListener);

        start.addActionListener(buttonListener);
        destination.addActionListener(buttonListener);
        routeCalculate.addActionListener(buttonListener);

        //infoUI
        forceUpdate.addActionListener(buttonListener);

        
    }

    private JPanel selectUI() {
        JPanel ui = new JPanel(new BorderLayout());
        JPanel group = new JPanel(new GridLayout(2, 1));
        ui.add(new JPanel(), BorderLayout.WEST);
        ui.add(new JPanel(), BorderLayout.EAST);

        //Top
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 10, 5));
        start = new JButton("[Starting Point]");
        destination = new JButton("[Destination]");
        routeCalculate = new JButton("[Calculate Routes]");

        topPanel.add(new JLabel("<< Coordinate Selection >>", SwingConstants.CENTER));
        topPanel.add(start);
        topPanel.add(destination);
        topPanel.add(routeCalculate);

        //Bottom
        JPanel bottomPanel = new JPanel(new GridLayout(5, 1, 10, 5));

       // JButton quickest = new JButton("[Fastest Route]");

        //Traffic Box
        JPanel trafficCheck = new JPanel(new FlowLayout());
        traffic = new JCheckBox();
        JLabel trafficCheckLabel = new JLabel("Consider Traffic");
        trafficCheck.add(trafficCheckLabel);
        trafficCheck.add(traffic);

        //Speed Box
        JPanel speedCheck = new JPanel(new FlowLayout());
        speed = new JCheckBox();
        JLabel speedCheckLabel = new JLabel("Consider Speed Limit");
        speedCheck.add(speedCheckLabel);
        speedCheck.add(speed);

        bottomPanel.add(new JLabel("<< Route Options >>", SwingConstants.CENTER));
       // bottomPanel.add(quickest);
        bottomPanel.add(trafficCheck);
        bottomPanel.add(speedCheck);

        //Main UI
        group.add(topPanel);
        group.add(bottomPanel);

        ui.add(group, BorderLayout.CENTER);

        return ui; 
    } 

    private JPanel infoUI() {
        JPanel info = new JPanel(new BorderLayout());
        JPanel mCoord = new JPanel(new FlowLayout());
        JPanel traffic = new JPanel(new FlowLayout());

        //JList<String> aiMapTest = new JList<>();
        mouseCoordinate = new JLabel("[Mouse Coordinate]");
        mCoord.add(mouseCoordinate);

        JLabel trafficTimer = new JLabel("[Reset Timer]");
        forceUpdate = new JButton("[Force Update]");
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

        while(true) {
            app.repaint();
        }
        
    }

//Action Handler Classes
    private class MouseHandler implements MouseInputListener {
		// MouseListener event handlers
		// handle event when mouse released immediately after the press
		public void mouseClicked( MouseEvent event ){
			mouseCoordinate.setText( String.format( "Clicked at [%d, %d]",event.getX(), event.getY() ) );

		} // end method mouseClicked
		
		// handle event when mouse pressed
		public void mousePressed( MouseEvent event ){
			mouseCoordinate.setText( String.format( "Pressed at [%d, %d]",event.getX(), event.getY() ) );
		} // end method mousePressed
		
		// handle event when mouse released after dragging
		public void mouseReleased( MouseEvent event ){
			
		} // end method mouseReleased
		
		// handle event when the mouse enters an area
		public void mouseEntered( MouseEvent event ){
			
		} // end method mouseEntered
		
		// handle event when mouse exits an area
		public void mouseExited( MouseEvent event ){
			mouseCoordinate.setText("[Mouse is not within map]");
		} // end method mouseExited
		// MouseMotionListener event handlers

        	// handle event when a user drags mouse with the button pressed
		public void mouseDragged( MouseEvent event ){
			
		} // end method mouseDragged
		
		// handle event when a user moves the mouse
		public void mouseMoved( MouseEvent event ){
			mouseCoordinate.setText(String.format( "[Current Coordinate: (%d, %d)]",event.getX(), event.getY()));
		} // end method mouseMoved
		
	} // end inner class MouseHandler

    private class CheckBoxEventListener implements ItemListener {
		@Override
		public void itemStateChanged( ItemEvent event ) {
            if (event.getSource() == traffic) {
                System.out.println("Traffic: ");
                if (traffic.isSelected()) {
                    System.out.print("Selected");
                } else {
                    System.err.print("Unselected");
                }
            } else if (event.getSource() == speed) {
                System.out.println("Speed: ");
                if (speed.isSelected()) {
                    System.out.print("Selected");
                } else {
                    System.err.print("Unselected");
                }
            }

		}
	}

    private class ButtonEventListener implements ActionListener {
        @Override
		public void actionPerformed( ActionEvent event ) {
			if (event.getSource() == start) {
                System.out.println("Toggle Starting Coordinate Selection");
                //start.setText(GPSApp.selectedNode1.toString());
            } else if (event.getSource() == destination) {
                System.out.println("Toggle Destination Coordinate Selection");
            } else if (event.getSource() == routeCalculate) {
                System.out.println("Route Calculations");
            } else if (event.getSource() == forceUpdate) {
                System.out.println("Force Update mapPanel");
                app.repaint();
            }

		} // end method actionPerformed	
    }
}