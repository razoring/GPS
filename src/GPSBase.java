import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

abstract class GPSBase extends JPanel {
	public Image mapImage;
	public static String mode = "ADD";
	public HashSet<Node> nodes = new HashSet<>();
	public HashSet<Node> intersections = new HashSet<>();
	public HashSet<Node> curves = new HashSet<>();
	public static Node selectedNode1 = null;
	public static Node selectedNode2 = null;

	public GPSBase(String imagePath) {
		mapImage = new ImageIcon(imagePath).getImage();
        clearDuplicates();
		readFromFile();
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
	
	private Node getNode(String item, Node parent, String position) {
	    String[] parts = item.split("\\(");
	    if (parts.length < 2) {
	        System.out.println("Invalid item format: " + item);
	        return null;
	    }
	    
	    String type = parts[0];
	    String[] coords = parts[1].replace(")", "").split(",");
	    if (coords.length < 2) {
	        System.out.println("Invalid coordinates format: " + parts[1]);
	        return null;
	    }
	    
	    int size = 1; // default size
	    if (item.endsWith("+")) {
	        size = 1;
	    } else if (item.endsWith("-")) {
	        size = 0;
	    }
	    
	    try {
	    	//print(Arrays.deepToString(nodes.toArray()));
	        int x = Integer.parseInt(coords[0].trim());
	        int y = Integer.parseInt(coords[1].substring(0,coords[1].length()-1).trim());
	        Node newNode = new Node(x, y, type, size);
	        
	        for (Node node : nodes) { // check for duplicates, if found use the duplicate instead
	        	if (node.x == x && node.y == y) {
	        		newNode = node;
		        }
	        }
	        
	        if (!nodes.contains(newNode) || Arrays.deepToString(nodes.toArray()).contains(newNode.toString())) {
	            nodes.add(newNode);
	            if ("INTERSECTION".equals(type)) {
	                intersections.add(newNode);
	            } else {
	                curves.add(newNode);
	            }
	        }

            if (parent != null) {
                parent.add(newNode, position);
            }
            
            //print(Arrays.deepToString(nodes.toArray()));
	        return newNode;
	    } catch (NumberFormatException e) {
	        System.out.println("Error parsing coordinates: " + parts[1]);
	        return null;
	    }
	}

	// file handling
	File save = new File("src/map.txt");
	public void clearDuplicates() {
        HashSet<String> uniqueLines = new HashSet<>();
        ArrayList<String> lines = new ArrayList<>();

        // Read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(save))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (uniqueLines.add(line.trim())) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the unique lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(save))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void readFromFile() {
	    try {
	        if (save.createNewFile()) {
	            System.out.println("File created: " + save.getName());
	        }

	        Scanner reader = new Scanner(save);
	        while (reader.hasNextLine()) {
	            String data = reader.nextLine().trim();
	            if (data.isEmpty()) continue;

	            String encap = data.substring(1, data.length() - 1);
	            String[] items = encap.split(";");
	            if (items.length == 0) continue;

	            Node parent = getNode(items[0], null, null);
	            if (parent == null) continue;

	            for (String item : items) {
	                if (item.contains("NEXT") || item.contains("PREV")) {
	                    String position = item.substring(0, 4).toLowerCase();
	                    String[] container = item.substring(item.indexOf('[') + 1, item.length() - 1).split(",\\ ");
	                    for (String node : container) {
	                        if (!node.isEmpty()) {
	                            getNode(node, parent, position);
	                        }
	                    }
	                }
	            }
	        }
	        reader.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	abstract void draw(Graphics g);
	
	// print functions to act like python
	public void print(Object str) {
		System.out.println(str);
	}
	
	public void print(Object str, Object str2) {
		System.out.println(str+","+str2);
	}
	
	public void print(Object str, Object str2, Object str3, Object str4) {
		System.out.println(str+","+str2+","+str3+","+str4);
	}
}
