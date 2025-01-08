import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class AssistedMapper extends GPSBase {
	static String mode = "ADD";
	static boolean cursorSize = true;
	static boolean tempSize = false;

	static JFrame frame = new JFrame("Map with Mouse Listener");
	static AssistedMapper panel = new AssistedMapper("src/8.PNG");

	public AssistedMapper(String imagePath) {
		super(imagePath);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

            	Node node = findNearestNode(x, y, 10);
                if (SwingUtilities.isRightMouseButton(e)) {
                	if (e.isAltDown()) {
                		mode = "INFO";
                	} else {
                		mode = "DELETE";
                	}
                } else {
                	if (e.isControlDown()) {
                		mode = "CURVE";
                		cursorSize = false;
                	} else if (e.isShiftDown()) {
                		mode = "LINK";
                	} else if (e.isAltDown()) {
                		mode = "SELECT";
                	} else {
                		mode = "ADD";
                	}
                }
				print("KEYBIND: "+mode);
            	drawMap(x, y, node);
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

	void drawMap(int x, int y, Node node) {
		if (mode.equals("ADD")) {
			if (findNearestNode(x,y,10)==null) {
				Node newNode = new Node(x, y, "INTERSECTION", cursorSize?1:0);
				intersections.add(newNode);
				nodes.add(newNode);
			}
		} else if (mode.equals("DELETE")) {
			if (node != null) {
				if (node.prev==node.next) {
					nodes.remove(node);
					intersections.remove(node);
					curves.remove(node);
					if (node.prev != null)
						for (Node prev : node.prev) {
							prev.next = node.next;
						}
					if (node.next != null)
						for (Node next : node.next) {
							next.prev = node.prev;
						}
				} else {
					node.prev = null;
					node.next = null;
				}
			}
		} else if (mode.equals("LINK")) {
			if (selectedNode1 == null) {
				selectedNode1 = findNearestNode(x, y, 10);
                System.err.println("Selected node: (" + node.x + ", " + node.y + ")");
			} else {
				selectedNode2 = findNearestNode(x, y, 10);
				if (selectedNode1 != null && selectedNode2 != null && selectedNode1 != selectedNode2) {
	                System.err.println("Selected node: (" + node.x + ", " + node.y + ")");
                    System.out.println("Current mode is (LINK), linking from ("+selectedNode2.x+","+selectedNode2.y+")");
                    selectedNode1.add(selectedNode2, "next");
                    selectedNode2.add(selectedNode1, "prev");
					selectedNode1 = selectedNode2;
					selectedNode2 = null;
				}
			}
		} else if (mode.equals("SELECT")) {
            if (node != null) {
                if (selectedNode1 == null) {
                    selectedNode1 = node;
                    System.err.println("Selected first node: (" + node.x + ", " + node.y + ")");
                } else if (selectedNode2 == null) {
                    selectedNode2 = node;
                    System.err.println("Selected second node: (" + node.x + ", " + node.y + ")");
                    selectedNode1.add(selectedNode2, "next");
                    selectedNode2.add(selectedNode1, "prev");
                    selectedNode1 = null;
                    selectedNode2 = null;
                    System.out.println("Clear to make new selection");
                }
            }
        } else if (mode.equals("CURVE")) {
            Node newNode = new Node(x, y, "CURVE", 0);
        	nodes.add(newNode);
        	curves.add(newNode);
        } else if (mode.equals("INFO")) {
        	System.out.println(findNearestNode(x,y,10));
        }
		
		saveToFile();
		frame.repaint();
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
				print(result);
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

		for (Node node : curves) {
			if (nodes.contains(node)) {
				g.drawOval(node.x, node.y, 6, 6);
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

	public static void main(String[] args) {
		frame.add(panel);
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image big = toolkit.getImage("src/Cursors/10x.png");
		Image small = toolkit.getImage("src/Cursors/6x.png");
		Cursor bCursor = toolkit.createCustomCursor(big , new Point(frame.getX(), frame.getY()), "cursor");
		Cursor sCursor = toolkit.createCustomCursor(small , new Point(frame.getX(), frame.getY()), "cursor");
		while (true) {
			Cursor cursors[] = {sCursor,bCursor}; 
			frame.setCursor(cursors[cursorSize?1:0]);
		}
		/*
		Scanner input = new Scanner(System.in);
		while (true) {
			System.out.println("Set Mode (" + mode + "): ");
			mode = input.nextLine().toUpperCase();
			selectedNode1 = null;
			selectedNode2 = null;
		}*/
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
	}
}
