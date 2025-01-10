import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class AutoMapper extends GPSBase {
	static String mode = "ADD";
	static Boolean cursorSize = true;

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static AutoMapper panel = new AutoMapper("src/8.PNG");

	public AutoMapper(String imagePath) {
		super(imagePath);
		findIntersection(imagePath);
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
	
	public Node findIntersection(String imagePath) {
		try {
			BufferedImage img = ImageIO.read(new File(imagePath));
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					x++;
					int pixel = img.getRGB(x,y);
					Color rgb = new Color(pixel, true);
					int r = rgb.getRed();
		            int g = rgb.getGreen();
		            int b = rgb.getBlue();
		            if (r==255 && g==255 && b==255) {
		            	img.setRGB(x, y, new Color(0,255,0).getRGB());
		            }
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void calculateNodes() {
		
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
