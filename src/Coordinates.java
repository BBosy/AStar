public class Coordinates {
	public int x;
	public int y;
	
	public Coordinates(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Coordinates(Coordinates c) {
		this.x = c.x;
		this.y = c.y;
	}
	
	
	public void print() {
		System.out.println(x+" "+y);
	}
	
	public boolean equals(Coordinates c) {
		if(this.x==c.x && this.y==c.y)
			return true;
		return false;
	}
}