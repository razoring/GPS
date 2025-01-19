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
	private boolean considerTraffic = true;
	
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
							InterfaceUI.startLabel.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
							// print(Arrays.deepToString(selectedNode1.connections.toArray()));
						} else if (nodeType == 2) {
							selectedNode2 = findNearestNode(x, y, 10);
							InterfaceUI.endLabel.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
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
			g.setColor(Color.black);
			if (nodes.contains(node)) {
				if (node.marker) {
					if (!path.isEmpty()) {
						g.setColor(Color.blue);
	
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
				print("set");
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
		double weight = 1; //default
		weight /= start.getSpeed();

		if(considerTraffic){ 
			weight *= start.getTraffic()+1;
			
		}
		
		if (type.equals("Distance") && current != null) {
			if (current == end) {
				for (Node item : path) {
					item.marker = true;
				}
				path.push(end);
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

				double combinedDistance = connection.findDistance(end)*weight;
				if (combinedDistance < closestDistance) {
					closest = connection;
					closestDistance = combinedDistance;
				}
			}

			if (closest != null) {
				path.push(closest);
				visited.add(closest);
				return algorithm("Distance", start, closest, end, path, visited, modifiers, iteration);
			} else {
				// Backtracking
				if (!path.isEmpty()) {
					path.pop(); // Unvisit the node to allow other paths, but retain the information that it has been visited to stop stack overflow
					if (!path.isEmpty()) {
						return algorithm("Distance", start, path.peek(), end, path, visited, modifiers, iteration);
					}
				} else {
					return algorithm("Distance", end, end, start, new Stack<Node>(), new HashSet<Node>(), modifiers,
							new HashSet<Stack<Node>>()); // fail safe, reverse the path and ensure a response
				}
			}
		}
		// fail safe, reverse the path and ensure a response
		return algorithm("Distance", end, end, start, new Stack<Node>(), new HashSet<Node>(), modifiers, iteration);
	}
}