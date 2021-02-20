
public class PVector {
	public double x;
	public double y;
	
	PVector(){
		this(0,0);
	}
	
	PVector(double x, double y){
		this.x =x;
		this.y = y;
	}
	
	public void add(PVector o){
		x += o.x;
		y += o.y;
	}

	public static double dist(PVector v1, PVector v2){
		return Math.sqrt((v1.x-v2.x)*(v1.x-v2.x)+(v1.y-v2.y)*(v1.y-v2.y));
	}
	
	public PVector clone(){
		return new PVector(x, y);
	}
}
