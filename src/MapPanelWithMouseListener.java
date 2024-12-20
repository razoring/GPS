import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;

public class MapPanelWithMouseListener extends JPanel {
	private Image mapImage;

	public MapPanelWithMouseListener(String imagePath) {
		mapImage = new ImageIcon(imagePath).getImage();

		// Add Mouse Listener
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				System.out.println("Mouse clicked at: (" + x + ", " + y + ")");
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Map with Mouse Listener");
		MapPanelWithMouseListener panel = new MapPanelWithMouseListener("src/8.PNG"); // Your map path
		frame.add(panel);
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
