import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Raymond So, Jiawei Chen
 * <p>
 * 01/15/2025
 * <p>
 * Handles most functions related to the map itself, such as road lines, traffic
 * levels and node selection. Also contains route calculation function using
 * Dijkstra's Algorithm.
 */
public class GPSApp extends GPSBase {
	public static Stack<Node> path = new Stack<Node>(); //for highlights
	static JFrame frame = new JFrame("Map with Mouse Listener"); //map frame
	static GPSApp panel = new GPSApp("src/8.PNG"); //image
	public Double combinedDistance = 0.0; //straight-line distance from start to end nodes
	

	/*
	 * Constructor; initializes all important functions
	 */
	public GPSApp(String imagePath) {
		super(imagePath);
		generateTraffic();
		for (int i = 0;i<20;i++) {
			adjustTraffic();
		}
		Thread timer = new Thread() {
			public void run() {
				TimerListener elapsed = new TimerListener();
				elapsed.startLoop(0);
			}
		};
		timer.start();

		addMouseListener(new MouseAdapter() {
			@Override 
			/*
			 * Handles node selection.
			 */
			public void mouseClicked(MouseEvent e) {
				try {
					int x = e.getX(); //returns mouse coordinates
					int y = e.getY();
					int nodeType = InterfaceUI.nodeSelection;

					// print(findNearestNode(x,y,10));
					// print(Arrays.deepToString(nodes.toArray()));
					// print(findConnections(findNearestNode(x,y,50)).size());
					if (nodeType > 0) { //check for selection type
						if (nodeType == 1) { //selecting start position
							selectedNode1 = findNearestNode(x, y, 10);
							InterfaceUI.start.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
							// print(Arrays.deepToString(selectedNode1.connections.toArray()));
						} else if (nodeType == 2) { //selecting end position
							selectedNode2 = findNearestNode(x, y, 10);
							InterfaceUI.destination.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
							// print(Arrays.deepToString(selectedNode2.connections.toArray()));
						}

						InterfaceUI.nodeSelection = 0; //turn off selection mode
					}
				} catch (Exception NullPointerException) {
					System.err.println("Node not found.");
				}

			}
		});
	}

	@Override
	/*
	 * Draws the lines for the traffic/fastest path
	 */
	void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color lvl[] = { Color.green, new Color(226, 186, 52), Color.red }; //level-traffic bindings
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this); //draws the map

		for (Node node : nodes) {
			for (Double value : node.congestion) {
	            Color startColor = lvl[(int) Math.floor(value)];
	            Color endColor = lvl[(int) Math.ceil(value)];
	            float fraction = (float) (value - Math.floor(value));

	            // Interpolating between startColor and endColor based on fraction
	            int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * fraction);
	            int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * fraction);
	            int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * fraction);

	            g.setColor(new Color(red, green, blue));
	        }

			if (node.next != null) {
				g2d.setStroke(new BasicStroke(5));
				for (Node next : node.next) {
					g.drawLine(node.x, node.y, next.x, next.y); //draws the line between nodes
				}
			}
		}

		for (Node node : intersections) { //draws intersections (black dot)
			g.setColor(Color.black);
			if (nodes.contains(node)) {
				if (node.marker) {
					if (!path.isEmpty()) {
						g.setColor(Color.black);
						
						Node lastNode = null;
						for (Node draw : path) {
							if (lastNode == null) {
								lastNode = draw;
								continue;
							} else {
								g.drawLine(lastNode.x, lastNode.y, draw.x, draw.y);
								lastNode = draw;
							}
						}
					}
				} else {
					g.setColor(Color.black);
				}

				//displays speedlimit
				if (node.connections != null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);  //font
					g2d.setFont(text);
					g2d.drawString((int) (node.getSpeed()) + "km/h", node.x, node.y - 5);
					g2d.drawString(node.x + "," + node.y, node.x, node.y - 15);
				}
				if (node.size == 1) {
					g.fillOval(node.x - 5, node.y - 5, 10, 10);
				} else {
					g.fillOval(node.x - 3, node.y - 3, 6, 6);
				}
			}
		}
	}

	//calls setTraffic method to assign traffic levels to roads
	public void generateTraffic() {
		for (Node node : nodes) {
			if (nodes.contains(node)) {
				node.setTraffic();
			}
		}
	}
	
	//adjusts/updates traffic levels
	public void adjustTraffic() {
		for (Node node : nodes) {
			if (nodes.contains(node)) {
				node.adjustTraffic((int)(Math.round(Math.random())));
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
	//constantly updates the map & handles traffic updates
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
		if (TimerListener.getTime()==0) {
			adjustTraffic();
		}
		repaint();
	}
	
	//removes highlighted path
	public void clearPath() {
		for (Node node : path) {
			node.marker = false;
		}
		path.clear();
		path = new Stack<Node>();
	}
	
	//Primary algorithm. Uses a custom algorithm based off nodes. See project supporting documentation
	public Stack<Node> algorithm(String type, Node start, Node current, Node end, Stack<Node> path, HashSet<Node> visited, String modifiers, HashSet<Stack<Node>> iteration) {
	    double weight = 1; // Default
		String regex = ",";
		String selectedMods[] = modifiers.split(regex); //grabs individual modifiers
		
		for (String mod : selectedMods) {
			if (mod.contains("traffic")) { //ignore/dont ignore traffic
		        weight *= current.getTraffic() + 2;
			}
			if (mod.contains("speed")) { //ignore/dont ignore speed limits
				weight /= current.getSpeed();
			}
		}

	    if (type.equals("Distance") && current != null) {
	        if (current == end) {
	            for (Node item : path) {
	                item.marker = true;
	            }
	            path.push(end);
	            iteration.add(path); // Store the first path
	            return path;
	        }

	        Node closest = null;
	        double closestDistance = Double.MAX_VALUE; //sets initial closest distance to a really high number

	        for (Node connection : current.connections) {
	            if (visited.contains(connection)) {
	                continue; // Avoid revisiting nodes
	            }

	            if (connection == end) {
	                closest = end; //when the closest node is the end; path formed
	                break;
	            }

				//calculate the distance between the start and end. if closer than the currently selected node, changes as accordingly
	            double combinedDistance = connection.findDistance(end) * weight;
	            if (combinedDistance < closestDistance) {
	                closest = connection;
	                closestDistance = combinedDistance;
	            }
	        }

			//continues the algorithm if closest exists
	        if (closest != null) {
	            path.push(closest);
	            visited.add(closest);
	            return algorithm(type, start, closest, end, path, visited, modifiers, iteration);
	        } else {
	            // Backtracking
	            if (!path.isEmpty()) {
	                path.pop(); // Unvisit the node
	                if (!path.isEmpty()) {
	                    return algorithm(type, start, path.peek(), end, path, visited, modifiers, iteration);
	                }
	            }
	        }
	    }

	    // Calculate reverse path
	    if (iteration.isEmpty()) {
	        // Initial call for the reverse path
	        return algorithm("Distance", end, end, start, new Stack<Node>(), new HashSet<Node>(), modifiers, iteration);
	    }

	    // Retrieve the stored forward path
	    Stack<Node> forwardPath = iteration.iterator().next();
	    // Calculate and store the reverse path
	    Stack<Node> reversePath = new Stack<>();
	    reversePath = algorithm("Distance", end, end, start, new Stack<Node>(), new HashSet<Node>(), modifiers, iteration);

	    // Compare paths based on weight or distance
	    double forwardWeight = calculatePathWeight(forwardPath);
	    double reverseWeight = calculatePathWeight(reversePath);

	    return (forwardWeight <= reverseWeight) ? forwardPath : reversePath;
	}

	// Helper method to calculate total weight of a path
	private double calculatePathWeight(Stack<Node> path) {
	    double totalWeight = 0;
	    Node previousNode = null;
	    for (Node node : path) {
	        if (previousNode != null) {
	            totalWeight += previousNode.findDistance(node) / previousNode.getSpeed();
	        }
	        previousNode = node;
	    }
	    return totalWeight;
	}

}