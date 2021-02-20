import java.awt.Color;
import java.awt.Graphics2D;

public class Obstacle {
	public int x,y,width,height;
	
	public Obstacle(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void show(Graphics2D g2){
		g2.setColor(Color.BLUE);
		g2.fillRect(x, y, width, height);
	}
	
	public boolean contains(Dot d){
		return d.pos.x > x && d.pos.x < x+width && d.pos.y>y && d.pos.y<y+height;
	}
}
