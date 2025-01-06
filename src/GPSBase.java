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
			reader2.close();
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
	
	public Node findNext(Node node) {
		if (node.next.type.equals("CURVE")) {
			return findNext(node.next);
		} else {
			return node.next;
		}
	}
	
	public Node findPrev(Node node) {
		if (node.prev.type.equals("CURVE")) {
			return findPrev(node.prev);
		} else {
			return node.prev;
		}
	}

	public double findDistance(Node base, Node target) {
		return Math.sqrt(Math.abs(target.x-base.x)+Math.abs(target.y-base.y)); // pythagorean theorem
	}
	
	public ArrayList<String> findConnections(Node base) {
		ArrayList<String> list = new ArrayList<String>();
		for (Node node : nodes) {
			if (node.next == base || node.prev == base) {
				list.add(node.name());
			}
		}
		return list;
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
