import javax.swing.*;
import java.awt.*;


public class MapPanel extends JPanel {
	private Image mapImage;
	private int h = 1300;
	private int w = 709;

	public MapPanel(String imagePath) {
		mapImage = new ImageIcon(imagePath).getImage();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Traffic-Avoidance Map");
		MapPanel mapPanel = new MapPanel("src/8.PNG"); // Replace with your image path
		frame.add(mapPanel);
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}