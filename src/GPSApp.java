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
	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");
	public static int elapsed = 0;

	public GPSApp(String imagePath) {
        super(imagePath);
        generateTraffic();

        addMouseListener(new MouseAdapter() {
            @Override
			/*
			 * Handles mouse clicks within the map itself. Uses GPSBase's findNearestNode function to detect
			 * nodes within a certain radius of the mouse click, then returns its x and y coordinates.
			 */
            public void mouseClicked(MouseEvent e) {
                try {
					int x = e.getX();
					int y = e.getY();
					int nodeType = InterfaceUI.nodeSelection;

					//print(findNearestNode(x,y,10));
					//print(Arrays.deepToString(nodes.toArray()));
					//print(findConnections(findNearestNode(x,y,50)).size());
					if (nodeType > 0) { //nodeType is defaulted to 0 when neither button is active.
						if (nodeType == 1) { //Selecting starting position
							selectedNode1 = findNearestNode(x,y,10);
							InterfaceUI.start.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
						} else if (nodeType == 2) { //Selecting destination
							selectedNode2 = findNearestNode(x,y,10);
							InterfaceUI.destination.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
						}

						InterfaceUI.nodeSelection = 0; //Reset selection type
					}
				} catch (Exception NullPointerException) {
					System.err.println("Node not found.");
				}
                
            }
        });
    }

	
	@Override
	/*
	 * Draws the road lines on the map. Also applies a colour corresponding to the road's traffic level.
	 * RED: Level 2
	 * YELLOW: Level 1
	 * Green: Level 0
	 */
	void draw(Graphics g) { 
		Graphics2D g2d = (Graphics2D) g;
		Color lvl[] = {Color.green, new Color(226,186,52),Color.red};
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
		
		for (Node node : nodes) {
			/*
			if (node.next != null) {
			    g2d.setStroke(new BasicStroke(4));
				for (Node next : node.next) {
					g.setColor(Color.black);
					g.drawLine(node.x, node.y, next.x, next.y);
					
					if (node.marker) {
						g.setColor(Color.blue);
					}
					if (node.size==1) {
						g.fillOval(node.x - 5, node.y - 5, 10, 10);
					} else {
						g.fillOval(node.x - 3, node.y - 3, 6, 6);
					}
				}
			}*/
			
			
			for (Integer value : node.congestion) {
				if (node.type.equals("CURVE")) { //Ignore curves
					// print(value);
				}
				if (node.marker) {
					g.setColor(Color.blue);
				} else {
					g.setColor(lvl[value]);
				}
			}

			if (node.next != null) { //Null check for a next node
				g2d.setStroke(new BasicStroke(5));
				for (Node next : node.next) {
					g.drawLine(node.x, node.y, next.x, next.y);
				}
			}
		}

		//Display speed limits of every node, attached to intersections
		for (Node node : intersections) {
			if (nodes.contains(node)) {
				g.setColor(Color.black);
				if (node.connections != null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);
					g2d.setFont(text);
					g2d.drawString((int) (Math.pow((node.getSpeed() / 3.78), 1.1) + 10) + "km/h", node.x, node.y - 5);
				}
				if (node.size == 1) {
					g.fillOval(node.x - 5, node.y - 5, 10, 10);
				} else {
					g.fillOval(node.x - 3, node.y - 3, 6, 6);
				}
			}
		}
	}
	
	/*
	 * Sets traffic for all nodes on the map.
	 */
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
	}
	
	//Using Dijkstra's Algorithm to determine the fastest route from start to destination.
	public Object algorithm(String type, Node start, Node current, Node end, HashSet<Node> path, HashSet<Node> visited) {
	    if (type.equals("Distance") && current != null) {
	        Node closest = null;
	        double closestDistance = Double.MAX_VALUE;

		    if (current == end) {
		    	print(Arrays.deepToString(path.toArray()));
		        return path;
		    }
		    
	        for (Node connection : current.connections) {
	            if (connection == end) {
	                closest = end;
	                break;
	            }

	            if (!visited.contains(connection)) {
	                double combinedDistance = connection.findDistance(current) + connection.findDistance(end);
	                
	                if (combinedDistance < closestDistance) {
	                    closest = connection;
	                    closestDistance = combinedDistance;
	                }
	            }
	        }

	        if (closest != null) {
	            path.add(closest);
	            visited.add(closest);
	            return algorithm("Distance", start, closest, end, path, visited);
	        } else {
	        	return null;
	        }
	    }

	    return null;
	}

}