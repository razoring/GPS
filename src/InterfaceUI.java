import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.Arrays;

public class InterfaceUI extends JFrame {

    private BorderLayout inter = new BorderLayout();

    public InterfaceUI() {
        super("GPS Interface");
        setLayout(inter);

        add(selectUI(), BorderLayout.EAST);
        //add(GPSApp.panel, BorderLayout.CENTER);
        add(AssistedMapper.panel, BorderLayout.CENTER);
        add(infoUI(), BorderLayout.SOUTH);

    }

    private JPanel selectUI() {
        JPanel ui = new JPanel(new BorderLayout());
        JPanel group = new JPanel(new GridLayout(2, 1));
        ui.add(new JPanel(), BorderLayout.WEST);
        ui.add(new JPanel(), BorderLayout.EAST);

        //Top
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 10, 5));

        JButton start = new JButton("[Starting Point]");
        JButton destination = new JButton("[Destination]");
        JButton routeCalculate = new JButton("[Calculate Routes]");

        topPanel.add(new JLabel("Coordinate Selection", SwingConstants.CENTER));
        topPanel.add(start);
        topPanel.add(destination);
        topPanel.add(routeCalculate);

        //Bottom
        JPanel bottomPanel = new JPanel(new GridLayout(5, 1, 10, 5));

        JButton quickest = new JButton("[Fastest Route]");

        //Traffic Box
        JPanel trafficCheck = new JPanel(new FlowLayout());
        JCheckBox traffic = new JCheckBox();
        JLabel trafficCheckLabel = new JLabel("Consider Traffic");
        trafficCheck.add(trafficCheckLabel);
        trafficCheck.add(traffic);

        //Speed Box
        JPanel speedCheck = new JPanel(new FlowLayout());
        JCheckBox speed = new JCheckBox();
        JLabel speedCheckLabel = new JLabel("Consider Speed Limit");
        speedCheck.add(speedCheckLabel);
        speedCheck.add(speed);

        bottomPanel.add(new JLabel("Route Options", SwingConstants.CENTER));
        bottomPanel.add(quickest);
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

        JLabel mouseCoordinate = new JLabel("[Mouse Coordinate]");
        mCoord.add(mouseCoordinate);

        JLabel trafficTimer = new JLabel("[Reset Timer]");
        JButton forceUpdate = new JButton("Force Update");
        traffic.add(trafficTimer);
        traffic.add(forceUpdate);

       //info.add(aiMapTest, BorderLayout.CENTER);
        info.add(mCoord, BorderLayout.WEST);
        info.add(traffic, BorderLayout.EAST);
        return info;
    }

    public static void main(String[] args) {
        InterfaceUI app = new InterfaceUI();
		app.setSize(1200, 600); // set frame size
		app.setVisible(true); // display frame

        app.setCursor(Cursor.CROSSHAIR_CURSOR);
        
        while(true) {
            app.repaint();
        }
    }
}
