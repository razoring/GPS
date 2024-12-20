import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Mapper extends JPanel {
	Image mapImage;
	static String mode = "ADD";
	LinkedList<Node> nodes = new LinkedList<>();
	LinkedList<Node> intersections = new LinkedList<>();
	LinkedList<Node> curves = new LinkedList<>();
	Node selectedNode1 = null;
	Node selectedNode2 = null;

	File map = new File("src/map.txt");

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static Mapper panel = new Mapper("src/8.PNG");

	private class Node {
		int x;
		int y;
		String type;
		Node next;
		Node prev;

		private Node(int xx, int yy, String nodeType) {
			x = xx;
			y = yy;
			type = nodeType;
			next = null;
			prev = null;
		}

		@Override
		public String toString() {
			return type + "|" + x + "," + y + "|" + (next != null ? next.type + "," + next.x + "," + next.y : "null") + "|"
					+ (prev != null ? prev.type + "," + prev.x + "," + prev.y : "null");
		}
	}

	private void drawMap(int x, int y, Node node) {
		if (mode.equals("ADD")) {
			Node newNode = new Node(x, y, "INTERSECTION");
			intersections.add(newNode);
			nodes.add(newNode);
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
			} else {
				selectedNode2 = findNearestNode(x, y, 10);
				if (selectedNode1 != null && selectedNode2 != null && selectedNode1 != selectedNode2) {
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
        }

		saveMapToFile();
	}

	private void saveMapToFile() {
		try (FileWriter writer = new FileWriter(map)) {
			for (Node node : nodes) {
				writer.write(node.toString() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadMapFromFile() {
		try {
			if (map.createNewFile()) {
				System.out.println("Created new map file: " + map.getAbsolutePath());
			} else {
				System.out.println("Map file found at:" + map.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(map))) {
			String line;
			Map<String, Node> nodeMap = new HashMap<>();
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|");
				String type = parts[0];
				String[] coords = parts[1].split(",");
				Node newNode = new Node(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), type);
				nodes.add(newNode);
				nodeMap.put(type + "," + parts[1], newNode);

				if (parts.length > 2 && !parts[2].equals("null")) {
					String[] nextParts = parts[2].split(",");
					String nextKey = nextParts[0] + "," + nextParts[1] + "," + nextParts[2];
					newNode.next = nodeMap.get(nextKey);
				}

				if (parts.length > 3 && !parts[3].equals("null")) {
					String[] prevParts = parts[3].split(",");
					String prevKey = prevParts[0] + "," + prevParts[1] + "," + prevParts[2];
					newNode.prev = nodeMap.get(prevKey);
				}

				if (type.equals("INTERSECTION")) {
					intersections.add(newNode);
				} else if (type.equals("CURVE")) {
					curves.add(newNode);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Mapper(String imagePath) {
		mapImage = new ImageIcon(imagePath).getImage();

		loadMapFromFile();

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

	private Node findNearestNode(int x, int y, int tolerance) {
		for (Node node : nodes) {
			if (Math.abs(node.x - x) <= tolerance && Math.abs(node.y - y) <= tolerance) {
				return node;
			}
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

		for (Node node : intersections) {
			if (nodes.contains(node)) {
				g.drawOval(node.x - 5, node.y - 5, 10, 10);
			}
		}

		for (Node node : curves) {
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

		Scanner input = new Scanner(System.in);
		while (true) {
			System.out.println("Set Mode (" + mode + "): ");
			mode = input.nextLine().toUpperCase();
		}
	}
}
