import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class GPSApp extends GPSBase {
	static JFrame frame = new JFrame("Map with Mouse Listener");
	static GPSApp panel = new GPSApp("src/8.PNG");
	static long elapsed = System.currentTimeMillis();

	public GPSApp(String imagePath) {
        super(imagePath);
        generateTraffic();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
            }
        });
    }

	@Override
	void draw(Graphics g) {
		Color lvl[] = {Color.green, new Color(226,186,52),Color.red};
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

		for (Node node : nodes) {
			if (node.next != null) {
				if (node.type.equals("CURVE")) {
					if (node.prev != null) {
						g.setColor(lvl[findPrev(node).congestion]);
					}
				} else {
					g.setColor(lvl[node.congestion]);
				}
				g.drawLine(node.x, node.y, node.next.x, node.next.y);
			}
		}
		
		for (Node node : intersections) {
			if (nodes.contains(node)) {
				Graphics2D g2d = (Graphics2D) g;
				g.setColor(lvl[node.congestion]);
				if (node.next!=null) {
					g.setColor(lvl[node.congestion]);
					g2d.drawString(((int)(node.distance(findNext(node))/3.78)*2+10)+"km/h", node.x, node.y+15);
				} else {
					g.setColor(lvl[findPrev(node.prev).congestion]);
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
	public void paintComponent(Graphics g) {
		if (elapsed%1200==0) {
			generateTraffic();
			elapsed = System.currentTimeMillis();
		}
		
		super.paintComponent(g);
		this.draw(g);
		repaint(); // recursive forever loop to update traffic
	}
}
