import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Raymond So, Jiawei Chen <p>
 * 01/15/2025 <p>
 * Handles most functions related to the map itself, such as road lines, traffic levels and node selection.
 * Also contains route calculation function using Dijkstra's Algorithm.
 */
public class GPSApp extends GPSBase {
	public static Stack<Node> path = new Stack<Node>();
	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");
	
		public GPSApp(String imagePath) {
			super(imagePath);
	
			print("Loading nodes... please be patient");
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						int x = e.getX();
					int y = e.getY();
					int nodeType = InterfaceUI.nodeSelection;
	
					//print(findNearestNode(x,y,10));
					//print(Arrays.deepToString(nodes.toArray()));
					//print(findConnections(findNearestNode(x,y,50)).size());
					if (nodeType > 0) {
						if (nodeType == 1) {
							selectedNode1 = findNearestNode(x,y,10);
							InterfaceUI.start.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
							//print(Arrays.deepToString(selectedNode1.connections.toArray()));
						} else if (nodeType == 2) {
							selectedNode2 = findNearestNode(x,y,10);
							InterfaceUI.destination.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
							//print(Arrays.deepToString(selectedNode2.connections.toArray()));
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
			Color lvl[] = {Color.green, new Color(226,186,52),Color.red};
			g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
			
			for (Node node : nodes) {
				for (Integer value : node.congestion) {
					if (node.type.equals("CURVE")) {
						// print(value);
					}
					g.setColor(lvl[value]);
				}
	
				if (node.next != null) {
					g2d.setStroke(new BasicStroke(5));
					for (Node next : node.next) {
						g.drawLine(node.x, node.y, next.x, next.y);
					}
				}
			}
	
			for (Node node : intersections) {
				if (nodes.contains(node)) {
					if (node.marker) {
						g.setColor(Color.blue);
						
						Node lastNode = null;
						if (path!=null) {
							for (Node draw : path) {
								if (lastNode==null) {
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
						g2d.drawString((int)(node.getSpeed()) + "km/h", node.x, node.y - 5);
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
					node.marker = false;
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
	public void paintComponent(Graphics g) { // TODO: DO NOT REFRESH EXTERNALLY
		super.paintComponent(g);
		this.draw(g);
        repaint();
	}
	
	public Object algorithm(String type, Node start, Node current, Node end, Stack<Node> path, HashSet<Node> visited, String modifiers) {
		path.push(start);
		if (type.equals("Distance") && current != null) {
	        double closestDistance = Double.MAX_VALUE;
	        Node closest = null;

	        if (current == end) {
	            // mark all nodes in the path and return the path
	            for (Node item : path) {
	            	item.marker = true;
	            }
	            path.push(end);
	            print(Arrays.toString(path.toArray()));
	            return path;
	        }

	        for (Node connection : current.connections) {
	            if (connection == end) {
	                closest = end;
	                break;
	            }

	            if (!visited.contains(connection)) {
	            	int weight = 0;
	            	
	            	/*HashMap<String,Integer> weights = new HashMap<String,Integer>();
	            	weights.put("speed",(int)(connection.getSpeed()));
	            	weights.put("traffic",connection.getTraffic());
	            	int weight = 0;
	            	if (!modifiers.isEmpty()) {
		            	for (String modifier : modifiers.split(",")) {
		            		weight = weight+weights.get(modifier);
		            	}
	            	}*/
	                double combinedDistance = (connection.findDistance(current) + weight) + connection.findDistance(end);

	                if (combinedDistance < closestDistance) {
	                    closest = connection;
	                    closestDistance = combinedDistance;
	                }
	            }
	        }

	        if (closest != null) {
	            path.push(closest);
	            visited.add(closest);
	            return algorithm("Distance", start, closest, end, path, visited, modifiers);
	        } else {
	            if (!path.isEmpty()) {
	                path.pop();
	                if (!path.isEmpty()) {
	                    return algorithm("Distance", start, path.lastElement(), end, path, visited, modifiers);
	                }
	            }
	            return null;
	        }
	    }
	    return null;
	}

}