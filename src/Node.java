import java.util.Arrays;
import java.util.Objects;

public class Node {
	int x;
	int y;
	int congestion; // traffic values
	int unweighted; // distance relative to other nodes
	int weight; // relative to destination (accounts for distance + congestion)
	int size;
	String type;
	Node next[];
	Node prev[];
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
		congestion = 1;
		unweighted = 0;
		weight = 0;
		size = s;
		type = t;
		next = new Node[0];
		prev = new Node[0];
	}
	
	public void setTraffic() {
		this.congestion = clamp(this.congestion+random(),0,2);
	}
	
	public void add(Node node, String type) {
		if (type.equals("prev")) {
			System.out.println(this);
			this.prev = Arrays.copyOf(this.prev, this.prev.length+1);
			this.prev[this.prev.length-1] = node;
		} else if (type.equals("next")) {
			System.out.println(this);
			this.next = Arrays.copyOf(this.next, this.next.length+1);
			this.next[this.next.length-1] = node;
		}
	}
	
	@Override
	public String toString() {
		return this.type+"("+this.x+","+this.y+")"+icons[size];
	}
	
	public String toSave() {
		String next = "";
		String prev = "";
		for (Node node : this.next) {
			next = next+", "+node.toString();
		}
		for (Node node : this.prev) {
			prev = prev+", "+node.toString();
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