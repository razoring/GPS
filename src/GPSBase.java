import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

abstract class GPSBase extends JPanel {
	Image mapImage;
	static String mode = "ADD";
	LinkedList<Node> nodes = new LinkedList<>();
	LinkedList<Node> intersections = new LinkedList<>();
	LinkedList<Node> curves = new LinkedList<>();
	static Node selectedNode1 = null;
	static Node selectedNode2 = null;

	public GPSBase(String imagePath) {
		mapImage = new ImageIcon(imagePath).getImage();
		readFromFile();
	}
 
	File save = new File("src//map.txt");
	public void readFromFile() {
		try {
			try {
				if (save.createNewFile()) {
					System.out.println("File created: " + save.getName());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// generate all the nodes first
			Scanner reader = new Scanner(save);
			while (reader.hasNextLine()) {
				String data = reader.nextLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Node findNearestNode(int x, int y, int tolerance) {
		for (Node node : nodes) {
			if (Math.abs(node.x - x) <= tolerance && Math.abs(node.y - y) <= tolerance) {
				return node;
			}
		}
		return null;
	}

	public double findDistance(Node base, Node target) {
		return Math.sqrt(Math.abs(target.x-base.x)+Math.abs(target.y-base.y)); // pythagorean theorem
	}
	
	abstract void draw(Graphics g);
	
	// print functions to act like python
	public void print(Object str) {
		System.out.println(str);
	}
	
	public void print(Object str, Object str2) {
		System.out.println(str+", "+str2);
	}
}
