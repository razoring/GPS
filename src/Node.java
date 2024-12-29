public class Node {
	int x;
	int y;
	String type;
	Node next;
	Node prev;

	public Node(int xx, int yy, String nodeType) {
		x = xx;
		y = yy;
		type = nodeType;
		next = null;
		prev = null;
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
	
	public String type() {
		return this.type;
	}
}