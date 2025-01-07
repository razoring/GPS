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
                }
                
                algorithm("DFS",selectedNode1,selectedNode2,new ArrayList<Node>(),new ArrayList<Node>());
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
				if (node.type.equals("CURVE")) {
					if (node.prev != null) {
						if (findPrev(node)!=null) {
							g.setColor(lvl[findPrev(node).congestion]);
						}
					}
				} else {
					g.setColor(lvl[node.congestion]);
				}
			    g2d.setStroke(new BasicStroke(5));
				g.drawLine(node.x, node.y, node.next.x, node.next.y);
			}
		}
		
		for (Node node : intersections) {
			if (nodes.contains(node)) {
				g.setColor(Color.black);
				if (node.next!=null) {
					Font text = new Font(Font.SANS_SERIF, Font.BOLD, 8);
					g2d.setFont(text);
					g2d.drawString(((int)(findDistance(node,findNext(node))/3.78)*2+10)+"km/h", node.x+5, node.y-5); // find the distance divided by 3.78 (conversion of 1 px to 1 km) then amplify by 2 and add the univsersal base speed of 10
					g2d.drawString(node.toString(), node.x+10, node.y-10);

					//g.setColor(lvl[node.congestion]);
				} else {
					//g.setColor(lvl[findPrev(node.prev).congestion]);
				}
				g.fillOval(node.x - 5, node.y - 5, 10, 10);
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
			if (elapsed>=1200) { // 2 minute timer
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
	
	public void algorithm(String type, Node current, Node end, ArrayList visited, ArrayList stack) {
		if (type.equals("Primitive")) { // first type of algorithm
			if (current != end) {
				int x = current.x;
				int y = current.y;
				int radius = 0;
				while (findNearestNode(x,y,radius)==null) {
					print(radius);
					radius++;
				}
				print(findNearestNode(x,y,radius));
			}
		} else if (type.equals("DFS")) {
			
		}
	}
}
