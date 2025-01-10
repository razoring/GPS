import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.Arrays;

public class GPSApp extends GPSBase {
	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");
	static int elapsed = 0;

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
                
                //print(findNearestNode(x,y,10));
                //print(Arrays.deepToString(nodes.toArray()));
                //print(findConnections(findNearestNode(x,y,50)).size());

                if (selectedNode1 == null) {
                	print("1");
                    selectedNode1 = findNearestNode(x,y,50);
                } else {
                	print("2");
                    selectedNode2 = findNearestNode(x,y,50);
                    print(algorithm("Primitive",selectedNode1,selectedNode2,new ArrayList<Node>(),new Stack<Node>()));
                    selectedNode1 = null;
                    selectedNode2 = null;
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
					/*
					int total = 0;
					for ( Integer prev : node.next.) {
						total = total+prev;
						for ( Integer next : node.congestion) {
							total = total+next;
						}
					}*/
					g.setColor(lvl[0]);
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
				if (node.next!=null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);
					g2d.setFont(text);
					for (Node next : node.next) {
						// find the distance divided by 3.78 (conversion of 1 px to 1 km) then amplify by 2 and add the univsersal base speed of 10
						g2d.drawString((int)(Math.pow((findDistance(node,next)/3.78), 2)+10)+"km/h", node.x, node.y-5);
					}
					//g2d.drawString(node.toString(), node.x, node.y-15);
					
					g2d.drawString(String.valueOf(node.weighted), node.x, node.y-15);
				}
				if (node.size==1) {
					g.fillOval(node.x - 5, node.y - 5, 10, 10);
				} else {
					g.fillOval(node.x - 3, node.y - 3, 6, 6);
				}
			}
		}
	}
	
	public void generateTraffic() {
		for (Node node : intersections) {
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
	public void paintComponent(Graphics g) { // TODO: DO NOT REFRESH EXTERNALLY
		try {
			if (elapsed>=1200/1200) { // 2 minute timer
				elapsed = 0;
				print("Refreshed");
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
	
	public Object algorithm(String type, Node current, Node end, ArrayList<Node> visited, Stack<Node> stack) {
		if (type.equals("Primitive")) { // first type of algorithm
			
		} else if (type.equals("DFS")) {
		} else if (type.equals("A-Star")) {
		}
		return null;
	}
}
