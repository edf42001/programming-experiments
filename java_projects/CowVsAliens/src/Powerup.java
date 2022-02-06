import java.awt.Component;
import java.awt.Graphics2D;

public class Powerup extends Character{

	@Override
	void collide(Character c) {
		if(c instanceof Player){
			//(Player) c.powerUp();
		}
		
	}

	@Override
	boolean shouldRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	boolean shouldKill() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void draw(Component c, Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

}
