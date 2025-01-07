import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.Arrays;

public class InterfaceTest extends JFrame {

    private BorderLayout inter = new BorderLayout();

    public InterfaceTest() {
        super("Interface Test");
        setLayout(inter);

        add(infoUI(), BorderLayout.EAST);
        add(GPSApp.panel, BorderLayout.CENTER);


    }

    private JPanel infoUI() {
        JPanel info = new JPanel(new BorderLayout());
        JPanel buttonLayout = new JPanel(new FlowLayout());

        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        buttonLayout.add(button1);
        buttonLayout.add(button2);

        info.add(buttonLayout, BorderLayout.CENTER);
        info.add(new JPanel(), BorderLayout.WEST);
        info.add(new JPanel(), BorderLayout.EAST);
        return info;
    } 

    public static void main(String[] args) {
        InterfaceTest app = new InterfaceTest();
		app.setSize(1400, 600); // set frame size
		app.setVisible(true); // display frame
    }
}

