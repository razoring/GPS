public class Node {
	int x;
	int y;
	int congestion; // traffic values
	int unweighted; // distance relative to other nodes
	int weight; // relative to destination (accounts for distance + congestion)
	String type;
	Node next[] = new Node[2];
	Node prev[] = new Node[2];

	private static int clamp(int val, int min, int max) {
	    return Math.max(min,Math.min(max,val));
	}
	
	private int random() {
	    return (int)(Math.random()*3)-1;
	}
	
	public Node(int xx, int yy, String nodeType) {
		x = xx;
		y = yy;
		congestion = 1;
		unweighted = 0;
		weight = 0;
		type = nodeType;
		next = null;
		prev = null;
	}
	
	public void setTraffic() {
		this.congestion = clamp(this.congestion+random(),0,2);
	}

	@Override
	public String toString() {
		return this.type+"("+this.x+","+this.y+")";
	}
	
	public String toSave() {
		return "{"+this.type+"("+this.x+","+this.y+");NEXT("+this.next.toString()+");PREV["+this.prev.toString()+"]}";
		// {INTERSECTION(500,500);NEXT[INTERSECTION(200,200),CURVE(100,100)];PREV[INTERSECTION(300,300),INTERSECTION(400,400)]}
	}
}