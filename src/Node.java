public class Node {
	int x;
	int y;
	int congestion;
	String type;
	Node next;
	Node prev;

	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public Node(int xx, int yy, String nodeType) {
		x = xx;
		y = yy;
		congestion = 1;
		type = nodeType;
		next = null;
		prev = null;
	}
	
	public void setTraffic() {
		int rates[] = {-1,0,1};
		this.congestion = clamp(this.congestion+rates[(int)(Math.round(Math.random())*(rates.length-1))], 0, 2);
	}
	
	public double distance(Node target) {
		return Math.sqrt((target.x-this.x)+(target.y-this.y)); // pythagorean theorem
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