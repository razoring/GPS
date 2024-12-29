import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class GPSApp extends GPSBase {
	Image mapImage;
	static String mode = "ADD";
	LinkedList<Node> nodes = new LinkedList<>();
	LinkedList<Node> intersections = new LinkedList<>();
	LinkedList<Node> curves = new LinkedList<>();
	static Node selectedNode1 = null;
	static Node selectedNode2 = null;

	File map = new File("src/map.txt");

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");

	public GPSApp(String imagePath) {
		super(imagePath);
		mapImage = new ImageIcon(imagePath).getImage();
		
		readFromFile();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

				drawMap(x, y, findNearestNode(x, y, 10));
				frame.repaint();
			}
		});
	}

	void drawMap(int x, int y, Node node) {
		
	}

	@Override
	void draw(Graphics g) {
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

		for (Node node : intersections) {
			if (nodes.contains(node)) {
				g.fillOval(node.x - 5, node.y - 5, 10, 10);
			}
		}

		for (Node node : nodes) {
			if (node.next != null) {
				g.drawLine(node.x, node.y, node.next.x, node.next.y);
			}
		}
	}

	public static void main(String[] args) {
		frame.setCursor(Cursor.CROSSHAIR_CURSOR);
		frame.add(panel);
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
	}
}
