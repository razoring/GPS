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
	static Node selectedNode1 = null;
	static Node selectedNode2 = null;

	File map = new File("src/map.txt");

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static Mapper panel = new Mapper("src/8.PNG");

	public Mapper(String imagePath) {
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
			String next = "";
			String prev = "";
			
			if (this.next!=null) {
				next = this.next.type+"("+this.next.x+","+this.next.y;
			} else {
				next = "(null";
			}
			
			if (this.prev!=null) {
				prev = this.prev.type+"("+this.prev.x+","+this.prev.y;
			} else {
				prev = "(null";
			}
			
			return "[this."+this.type+"("+this.x+","+this.y+"),next."+next+"),previous."+prev+")]";
			// [this.INTERSECTION(500,500),next.CURVE(200,200),previous.INTERSECTION(100,100)]
		}
	}

	private void drawMap(int x, int y, Node node) {
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
        }
		
		saveToFile();
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
	
	private void readFromFile() {
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
				String unpacked = data.substring(1,data.length()-1);
				String items[] = unpacked.split("\n");
				for (String item : items) {
					String curr = item.split("\\),")[0];
					String next = item.split("\\),")[1];
					String prev = item.split("\\),")[2];

					String xx = curr.split(",")[0].replaceAll("\\D", "");
					if (!xx.isEmpty()) {
						int x = Integer.parseInt(xx);
						String yy = curr.split(",")[1].replaceAll("\\)", "");
						if (!yy.isEmpty()) {
							int y = Integer.parseInt(yy);
							String type = curr.split("\\.")[1].split("\\(")[0];

							//System.out.println("o-"+x+","+y);
							Node newNode = new Node(x, y, type);
									
							nodes.add(newNode);
							if (type.charAt(0) == 'I') {
								intersections.add(newNode);
							} else {
								curves.add(newNode);
							}
						}
					}
				}
			}
			
			// attach all the previous and next nodes, will not connect if a node is not found
			Scanner reader2 = new Scanner(save);
			while (reader2.hasNextLine()) {
				String data = reader2.nextLine();
				String unpacked = data.substring(1,data.length()-1);
				String items[] = unpacked.split("\n");
				
				for (String item : items) {
					String curr = item.split("\\),")[0];
					String next = item.split("\\),")[1];
					String prev = item.split("\\),")[2];
					
					String xx = curr.split(",")[0].replaceAll("\\D", "");
					if (!xx.isEmpty()) {
						int x = Integer.parseInt(xx);
						String yy = curr.split(",")[1].replaceAll("\\)", "");
						if (!yy.isEmpty()) {
							int y = Integer.parseInt(yy);
							
							Node newNode = findNearestNode(x,y,10);
							
							//System.out.println("o-"+x+","+y);
							xx = next.split(",")[0].replaceAll("\\D", "");
							if (!xx.isEmpty()) {
								int x_ = Integer.parseInt(xx);
								yy = next.split(",")[1].replaceAll("\\)", "");
								if (!yy.isEmpty()) {
									int y_ = Integer.parseInt(yy);
									newNode.next = findNearestNode(x_,y_,10);
									//System.out.println("n-"+x_+","+y_);
								}
							}
							
							xx = prev.split(",")[0].replaceAll("\\D", "");
							if (!xx.isEmpty()) {
								int x_ = Integer.parseInt(xx);
								yy = prev.split(",")[1].replaceAll("\\)", "");
								if (!yy.isEmpty()) {
									int y_ = Integer.parseInt(yy);
									newNode.prev = findNearestNode(x_,y_,10);
									//System.out.println("p-"+x_+","+y_);
								}
							}
						}
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			selectedNode1 = null;
			selectedNode2 = null;
		}
	}
}
