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
	public static Stack<Node> path = new Stack<Node>();
	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");
	public Double combinedDistance = 0.0;
	//public boolean considerTraffic = true;
	
	public GPSApp(String imagePath) {
		super(imagePath);
		generateTraffic();
		Thread timer = new Thread() {
			public void run() {
				TimerListener elapsed = new TimerListener();
				elapsed.startLoop(0);
			}
		};
		timer.start();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					int x = e.getX();
					int y = e.getY();
					int nodeType = InterfaceUI.nodeSelection;

					// print(findNearestNode(x,y,10));
					// print(Arrays.deepToString(nodes.toArray()));
					// print(findConnections(findNearestNode(x,y,50)).size());
					if (nodeType > 0) {
						if (nodeType == 1) {
							selectedNode1 = findNearestNode(x, y, 10);
							InterfaceUI.start.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
							// print(Arrays.deepToString(selectedNode1.connections.toArray()));
						} else if (nodeType == 2) {
							selectedNode2 = findNearestNode(x, y, 10);
							InterfaceUI.destination.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
							// print(Arrays.deepToString(selectedNode2.connections.toArray()));
						}

						InterfaceUI.nodeSelection = 0;
					}
				} catch (Exception NullPointerException) {
					System.err.println("Node not found.");
				}

			}
		});
	}

	@Override
	void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color lvl[] = { Color.green, new Color(226, 186, 52), Color.red };
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

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
					g.drawLine(node.x, node.y, next.x, next.y);
				}
			}
		}

		for (Node node : intersections) {
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
				if (node.connections != null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);
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

	public void generateTraffic() {
		for (Node node : nodes) {
			if (nodes.contains(node)) {
				node.setTraffic();
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
		if (TimerListener.getTime()==0) {
			for (Node node : nodes) {
				if (nodes.contains(node)) {
					node.adjustTraffic((int)(Math.round(Math.random())));
				}
			}
		}
		repaint();
	}
	
	public void clearPath() {
		for (Node node : path) {
			node.marker = false;
		}
		path.clear();
		path = new Stack<Node>();
	}
	
	public Stack<Node> algorithm(String type, Node start, Node current, Node end, Stack<Node> path, HashSet<Node> visited, String modifiers, HashSet<Stack<Node>> iteration) {
	    double weight = 1; // Default
		String regex = ",";
		String selectedMods[] = modifiers.split(regex);  
		
		for (String mod : selectedMods) {
			if (mod.contains("traffic")) {
		        weight *= current.getTraffic() + 2;
			}
			if (mod.contains("speed")) {
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
	        double closestDistance = Double.MAX_VALUE;

	        for (Node connection : current.connections) {
	            if (visited.contains(connection)) {
	                continue; // Avoid revisiting nodes
	            }

	            if (connection == end) {
	                closest = end;
	                break;
	            }

	            double combinedDistance = connection.findDistance(end) * weight;
	            if (combinedDistance < closestDistance) {
	                closest = connection;
	                closestDistance = combinedDistance;
	            }
	        }

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