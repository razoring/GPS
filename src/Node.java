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
	
	private int random() {
	    return (int)(Math.random()*3)-1;
	}
	
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
	
	public void setTraffic() {
		for (int i = 0; i < congestion.size(); i++) {
	        int updatedValue = clamp(congestion.get(i)+random(),0,2);
	        congestion.set(i, updatedValue);
	    }
	}
	
	public double findDistance(Node target) {
		return Math.sqrt(Math.abs(target.x-this.x)+Math.abs(target.y-this.y)); // pythagorean theorem
	}
	
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