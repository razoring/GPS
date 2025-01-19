import java.util.ArrayList;

/**
 * Raymond So, Jiawei Chen <p>
 * 01/15/2025 <p>
 * Class for all functions related to individual nodes.
 */
public class Node {
	int x;
	int y;
	int size;
	boolean marker;
	ArrayList<Integer> congestion; // traffic values
	ArrayList<Integer> previousCongestion; // traffic values
	ArrayList<Node> connections;
	ArrayList<Node> next;
	ArrayList<Node> prev;
	String type;
	String icons[] = {"-","+"};

	//Enforces value limits for the value being clamped.
	private static int clamp(int val, int min, int max) {
	    return Math.max(min,Math.min(max,val));
	}
	
	//Generates a random value between 0-2.
	private int random() {
	    return (int)(Math.random()*2);
	}
	
	//Constructor for Node
	public Node(int xx, int yy, String t, int s) {
		x = xx; //x coordinate
		y = yy; //y coordinate
		marker = false; //if marked
		connections = new ArrayList<Node>(); //connected nodes
		congestion = new ArrayList<Integer>(); //for traffic values
		previousCongestion = new ArrayList<Integer>();
		next = new ArrayList<Node>(); //next node
		prev = new ArrayList<Node>(); //previous node
		size = s; //node size
		type = t; //node type
	}
	
	//Sets the traffic based on congestion values between nodes
	public void setTraffic() {
		for (int i = 0; i < congestion.size(); i++) {
			previousCongestion = new ArrayList<>(congestion);
	        int updatedValue = clamp(congestion.get(i)+random(),0,2);
	        congestion.set(i, updatedValue);
	    }
	}
	
	// Pythagorean theorem's the distance between two nodes.
	public double findDistance(Node target) {
		return Math.sqrt(Math.pow((target.x-this.x), 2)+Math.pow((target.y-this.y), 2)); // pythagorean theorem
	}
	
	//Finds the rough distance between two nodes
	public int getDistance() {
		int avg = 0;
		for (Node node : this.connections ) {
			avg = (int)(avg+this.findDistance(node));
		}
		return this.connections.size()<1?0:avg/this.connections.size();
	}
	
	//Returns the traffic value of nodes
	public int getTraffic() {
		int avg = 0;
		for (Node node : this.connections ) {
			for (Integer value : this.congestion) {
				avg = avg+(int)(value);
			}
		}
		return this.connections.size()<1?0:avg/this.connections.size();
	}
	
	//Returns the speedlimit of nodes
	public double getSpeed() {
		return (Math.pow(((this.getDistance()) / 3.78), 1.1) + 10);
	}
	
	//Adds a node to another's connections list
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
	//Returns the node's coordinate
	public String toString() {
		return this.type+"("+this.x+","+this.y+")"+icons[size];
	}
	
	//Serializes node data
	public String toSave() {
		String next = "";
		String prev = "";
		if (this.next!=null) { //null check
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