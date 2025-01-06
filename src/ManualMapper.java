import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class ManualMapper extends GPSBase {
	static String mode = "ADD";

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static ManualMapper panel = new ManualMapper("src/8.PNG");

	public ManualMapper(String imagePath) {
		super(imagePath);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

                if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {
                	Node node = findNearestNode(x, y, 10);
        			if (node != null) {
        				nodes.remove(node);
        				intersections.remove(node);
        				curves.remove(node);
        				if (node.prev != null)
        					node.prev.next = node.next;
        				if (node.next != null)
        					node.next.prev = node.prev;
        			}
                } else {
                	drawMap(x, y, findNearestNode(x, y, 10));
                }
			}
		});
	}

	void drawMap(int x, int y, Node node) {
		if (mode.equals("ADD")) {
			if (findNearestNode(x,y,10)==null) {
				Node newNode = new Node(x, y, "INTERSECTION");
				intersections.add(newNode);
				nodes.add(newNode);
			}
		} else if (mode.equals("DELETE")) {
			if (node != null) {
				nodes.remove(node);
				intersections.remove(node);
				curves.remove(node);
				if (node.prev != null)
					node.prev.next = node.next;
				if (node.next != null)
					node.next.prev = node.prev;
			}
		} else if (mode.equals("LINK")) {
			if (selectedNode1 == null) {
				selectedNode1 = findNearestNode(x, y, 10);
                System.err.println("Selected node: (" + node.x + ", " + node.y + ")");
			} else {
				selectedNode2 = findNearestNode(x, y, 10);
				if (selectedNode1 != null && selectedNode2 != null && selectedNode1 != selectedNode2) {
	                System.err.println("Selected node: (" + node.x + ", " + node.y + ")");
                    System.out.println("Current mode is (LINK), linking from ("+selectedNode2.x+","+selectedNode2.y+")");
					selectedNode1.next = selectedNode2;
					selectedNode2.prev = selectedNode1;
					selectedNode1 = selectedNode2;
					selectedNode2 = null;
				}
			}
		} else if (mode.equals("SELECT")) {
            if (node != null) {
                if (selectedNode1 == null) {
                    selectedNode1 = node;
                    System.err.println("Selected first node: (" + node.x + ", " + node.y + ")");
                } else if (selectedNode2 == null) {
                    selectedNode2 = node;
                    System.err.println("Selected second node: (" + node.x + ", " + node.y + ")");
                    selectedNode1.next = selectedNode2;
                    selectedNode2.prev = selectedNode1;
                    selectedNode1 = null;
                    selectedNode2 = null;
                    System.out.println("Clear to make new selection");
                }
            }
        } else if (mode.equals("CURVE")) {
            Node newNode = new Node(x, y, "CURVE");
        	nodes.add(newNode);
        	curves.add(newNode);
        } else if (mode.equals("INFO")) {
        	System.out.println(findNearestNode(x,y,10));
        } else if (mode.equals("NEXT")) {
        	System.out.println(findNext(findNearestNode(x,y,10)));
        }
		
		saveToFile();
		frame.repaint();
	}

	File save = new File("src//map.txt");
	private void saveToFile() {
		try {
			if (save.createNewFile()) {
				System.out.println("File created: " + save.getName());
			} else {
				FileWriter writer = new FileWriter(save);
				String result = "";
				for (Node node : nodes) {
					result = result+node.toString()+"\n";
				}
				//System.out.println(result);
				writer.write(result);
				writer.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	@Override
	void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

		for (Node node : intersections) {
			if (nodes.contains(node)) {
				g.fillOval(node.x - 5, node.y - 5, 10, 10);
			}
		}

		for (Node node : curves) {
			if (nodes.contains(node)) {
				g.drawOval(node.x - 3, node.y - 3, 6, 6);
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

		Scanner input = new Scanner(System.in);
		while (true) {
			System.out.println("Set Mode (" + mode + "): ");
			mode = input.nextLine().toUpperCase();
			selectedNode1 = null;
			selectedNode2 = null;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
	}
}
