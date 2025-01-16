import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Raymond So, Jiawei Chen <p>
 * 01/15/2025 <p>
 * Class for all functions related to individual nodes.
 */
public class Node {
	int x;
	int y;
	int size;
	int weighted;
	boolean marker;
	ArrayList<Integer> congestion; // traffic values
	ArrayList<Node> connections;
	ArrayList<Node> next;
	ArrayList<Node> prev;
	String type;
	String icons[] = {"-","+"};

	private static int clamp(int val, int min, int max) {
	    return Math.max(min,Math.min(max,val));
	}
	
	/*
	 * Helper function for generating traffic levels. Method generates a random number between 0 and 2.
	 */
	private int random() {
	    return (int)(Math.random()*3)-1;
	}
	
	/*
	 * Constructor for a node. Contains an arraylist for connections, congestion value, next and previous nodes, size and type.
	 */
	public Node(int xx, int yy, String t, int s) {
		x = xx;
		y = yy;
		marker = false;
		connections = new ArrayList<Node>();
		congestion = new ArrayList<Integer>();
		next = new ArrayList<Node>();
		prev = new ArrayList<Node>();
		size = s;
		type = t;
	}
	
	/*
	 * Calculate the traffic level based on the node's congestion value.
	 */
	public void setTraffic() {
		for (int i = 0; i < congestion.size(); i++) {
	        int updatedValue = clamp(congestion.get(i)+random(),0,2);
	        congestion.set(i, updatedValue);
	    }
	}
	
	/*
	 * Determines the net distance to the target node using the pythgagorean theorem.
	 */
	public double findDistance(Node target) {
		return Math.sqrt(Math.pow(Math.abs(target.x-this.x), 2)+Math.pow(Math.abs(target.y-this.y), 2)); // pythagorean theorem
	}
	
	/*
	 * Calculates the speed limit of the node.
	 */
	public int getSpeed() {
		int avg = 0;
		for (Node node : this.connections ) {
			avg = (int)(avg+this.findDistance(node));
		}
		return this.connections.size()<1?0:avg/this.connections.size();
	}
	
	/*
	 * Helper function to add nodes to different ArrayLists.
	 */
	public void add(Node node, String type) {
		this.congestion.add(1);
		this.connections.add(node);
		if (type.equals("prev")) {
			this.prev.add(node);
		} else if (type.equals("next")) {
			this.next.add(node);
		}
	}
	
	@Override
	public String toString() {
		return this.type+"("+this.x+","+this.y+")"+icons[size];
	}

	/*
	 * Returns a string of the selected node chain.
	 */
	public String toSave() {
		String next = "";
		String prev = "";
		if (this.next!=null) {
			for (Node node : this.next) {
				next = next+", "+node.toString();
			}
		}
		if (this.prev!=null) {
			for (Node node : this.prev) {
				prev = prev+", "+node.toString();
			}
		}
		return "{"+this.type+"("+this.x+","+this.y+")"+icons[size]+";NEXT["+next+"];PREV["+prev+"]}";
		// {INTERSECTION(500,500)+;NEXT[INTERSECTION(200,200)-,CURVE(100,100)]-;PREV[INTERSECTION(300,300)+,INTERSECTION(400,400)+]}
	}
	
	// print functions to act like python
	public void print(Object str) {
		System.out.println(str);
	}
	
	public void print(Object str, Object str2) {
		System.out.println(str+", "+str2);
	}
}