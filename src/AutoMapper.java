import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class AutoMapper extends GPSBase {
	static String mode = "ADD";
	static Boolean cursorSize = true;

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static AutoMapper panel = new AutoMapper("src/8.PNG");

	public AutoMapper(String imagePath) {
		super(imagePath);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override 
			public void keyPressed(KeyEvent e) { 
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) { 
					cursorSize = !cursorSize;
				} 
			}
		});
		setFocusable(true); 
		requestFocusInWindow();
	}

	File save = new File("src//map.txt");
	private void saveToFile() {
		try {
			if (save.createNewFile()) {
				System.out.println("File created: " + save.getName());
			} else {
				FileWriter writer = new FileWriter(save);
				String result = "";
				for (Node node : nodes) {
					result = result+node.toSave()+"\n";
				}
				//System.out.println(result);
				writer.write(result);
				writer.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	@Override
	void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

		for (Node node : intersections) {
			if (nodes.contains(node)) {
				if (node.size==1) {
					g.fillOval(node.x, node.y, 10, 10);
				} else {
					g.fillOval(node.x, node.y, 6, 6);
				}
			}
		}

		for (Node node : nodes) {
			if (node.next != null) {
				for (Node next : node.next) {
					g.drawLine(node.x+(node.size==1?5:3), node.y+(node.size==1?5:3), next.x+(next.size==1?5:3), next.y+(next.size==1?5:3));
				}
			}
		}
	}
	
	public static Node findIntersection(String imagePath) {
		
		return null;
	}
	
	public static void calculateNodes() {
		
	}

	public static void main(String[] args) {
		frame.add(panel);
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
	}
}
