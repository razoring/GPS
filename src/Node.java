public class Node {
	int x;
	int y;
	int congestion; // traffic values
	int unweighted; // distance relative to other nodes
	int weight; // relative to destination (accounts for distance + congestion)
	String type;
	Node next;
	Node prev;

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

	public String name() {
		return "[this."+this.type+" @ ("+this.x+","+this.y+")]";
	}

	@Override
	public String toString() {
		String next = "";
		String prev = "";
		
		if (this.next!=null) {
			next = this.next.type+"("+this.next.x+","+this.next.y;
		} else {
			next = "(null";
		}
		
		if (this.prev!=null) {
			prev = this.prev.type+"("+this.prev.x+","+this.prev.y;
		} else {
			prev = "(null";
		}
		
		return "[this."+this.type+"("+this.x+","+this.y+"),next."+next+"),previous."+prev+")]";
		// [this.INTERSECTION(500,500),next.CURVE(200,200),previous.INTERSECTION(100,100)]
	}
}