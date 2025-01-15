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
        /*
        // set distances
        for (Node node : nodes) {
        	print(node.toString());
        	if (node.next != null) {
            	for (Node next : node.next) {
            		int distance = (int)(findDistance(node,next));
            		node.uwNext.add(distance);
            	}
        	}

        	if (node.prev != null) {
            	for (Node next : node.prev) {
            		int distance = (int)(findDistance(node,next));
            		node.uwPrev.add(distance);
            	}
        	}
        	
        	print(Arrays.deepToString(node.uwNext.toArray()));
        	print(Arrays.deepToString(node.uwPrev.toArray()));
        }*/

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int nodeType = InterfaceUI.nodeSelection;

                //print(findNearestNode(x,y,10));
                //print(Arrays.deepToString(nodes.toArray()));
                //print(findConnections(findNearestNode(x,y,50)).size());
				if (nodeType > 0) {
					if (selectedNode1 == null || nodeType == 1) {
						print("1");
						selectedNode1 = findNearestNode(x,y,10);
						InterfaceUI.start.setText("(" + selectedNode1.x + ", " + selectedNode1.y + ")");
					} else if ((selectedNode1 != null && selectedNode2 == null) || nodeType == 2) {
						print("2");
						selectedNode2 = findNearestNode(x,y,10);
						InterfaceUI.destination.setText("(" + selectedNode2.x + ", " + selectedNode2.y + ")");
						//Temporarily commented out. Set selectedNode1 and selectedNode2 to null AFTER algo runs

						print(algorithm("Distance",selectedNode1,selectedNode1,selectedNode2,new HashSet<Node>(),new HashSet<Node>()));
						selectedNode1 = null;
						selectedNode2 = null;
					}

					InterfaceUI.nodeSelection = 0;
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
			if (node.next != null) {
			    g2d.setStroke(new BasicStroke(4));
				for (Node next : node.next) {
					g.drawLine(node.x, node.y, next.x, next.y);
					
					if (node.size==1) {
						g.fillOval(node.x - 5, node.y - 5, 10, 10);
					} else {
						g.fillOval(node.x - 3, node.y - 3, 6, 6);
					}
				}
			}
			
			/*
			for (Integer value : node.congestion) {
				if (node.type.equals("CURVE")) {
					//print(value);
				}
				if (node.marker) {
					g.setColor(Color.blue);
				} else {
					g.setColor(lvl[value]);
				}
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
				g.setColor(Color.black);
				if (node.connections!=null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);
					g2d.setFont(text);
					for (Node next : node.connections) {
						// find the distance divided by 3.78 (conversion of 1 px to 1 km) then amplify by 2 and add the univsersal base speed of 10
						g2d.drawString((int)(Math.pow((node.findDistance(next)/3.78), 2)+10)+"km/h", node.x, node.y-5);
					}
					//g2d.drawString(node.toString(), node.x, node.y-15);
					
					g2d.drawString(String.valueOf(node.weighted), node.x, node.y-15);
				}
				if (node.size==1) {
					g.fillOval(node.x - 5, node.y - 5, 10, 10);
				} else {
					g.fillOval(node.x - 3, node.y - 3, 6, 6);
				}
			}*/
		}
	}
	
	public void generateTraffic() {
		for (Node node : intersections) {
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
		try {
			if (elapsed>=1200) { // 2 minute timer
				elapsed = 0;
				//print("Refreshed");
				generateTraffic();
			}
			
			super.paintComponent(g);
			this.draw(g);
			
			elapsed++;
			Thread.sleep(100); // delay every 100 ms, still responsive on user input
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint(); // recursive loop, fix to calling non-static methods in STATIC main while loop
	}
	
	public Object algorithm(String type, Node start, Node current, Node end, HashSet<Node> path, HashSet<Node> visited) {
	    if (type.equals("Primitive")) {
	    } else if (type.equals("DFS")) {
	    } else if (type.equals("A*")) {
	    } else if (type.equals("Distance")) {
    		if (current != null) {
	    		Node closest = null;
	    		double closestToCurr = Double.MAX_VALUE;
	    		double closestToEnd = Double.MAX_VALUE;

	    		for (Node connection : current.connections) {
	    		    if (connection == end) {
	    		        closest = end;
	    		        break;
	    		    }

	    		    double toCurr = connection.findDistance(current);
	    		    double toEnd = connection.findDistance(end);

	    		    if (toCurr < closestToCurr && toEnd < closestToEnd) {
	    		        closest = connection;
	    		        closestToCurr = toCurr;
	    		        closestToEnd = toEnd;
	    		    }
	    		}
    		    print(closest);
    		    current = closest;
	    		path.add(current);
	    		visited.add(current);
    		    if (current!=end) {
        		    return algorithm("Distance",start,current,end,path,visited);
    		    } else {
    		    	for (Node node : path) {
    		    		node.marker = true;
    		    	}
    		        print(Arrays.deepToString(path.toArray()));
    		    }
	    	}
	    }
	    return null;
	}

}