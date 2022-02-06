import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

// Class: Shot
// Written by: Ethan Frank
// Date: Nov 15, 2017
// Description: This class is what the player and enemies shoot
public class Shot extends Character{
	private int damage;			// How much damage the shot does
	private double vel;			// Velocity of the shot
	private Color[] colors; 	// {Outer Color, inner color} of shot
	private boolean fromPlayer;	// true if shot was from player
	private boolean heavy;      // True if shot goes through enemies
	

	//Constructors
	//This is the actually useful constructor, unlike that packed constructor, which you can look at for a description of parameters
	public Shot(double x_pos, double y_pos, double vel, double angle, int damage, double radius, boolean fromPlayer, boolean heavy) {
		this(x_pos, y_pos, 0, 0, 0, 0, radius, angle, 1, 0, damage, vel, null, fromPlayer, heavy);
		Color[] colors;
		//Colors are hardcoded based on if they are from the player or an enemy
		if(fromPlayer){
			colors = new Color[] {Color.getHSBColor(0.5f, 0.8f, 1), Color.getHSBColor(0.7f, 0.8f, 1f)};
		}else{
			colors = new Color[] {Color.getHSBColor(0f, 1f, 1), Color.getHSBColor(0f, 0f, 0f)};
		}
		
		this.colors = colors;
	}

	// Shot packed constructor
	// parameters:	x_pos - Initial x position of the character (measured to center of character)
	//				y_pos - Initial y position of the character (measured to center of character)
	//				x_vel - Initial x velocity of character, in pixels/iteration
	//				y_vel - Initial y velocity of character, in pixels/iteration
	//				x_accel - Initial x acceleration of character, in pixels/iteration/iteration
	//				y_accel - Initial y acceleration of character, in pixels/iteration/iteration
	//				radius - Radius of character, assuming character to be circular
	//				angle - Initial angle of the character, in degrees. postitive angles are clockwise
	//				health - Initial health of the character
	// 				internal_timer - inital value of internal timer
	//				damage - damage shot does
	//				vel - velocity of shot
	//				colors - {outer color, inner color} of shot
	//				fromPlayer - true of shot came from player
	//				heavy - true if shot goes through things without being destroyed
	public Shot(double x_pos, double y_pos, double x_vel, double y_vel, double x_accel, double y_accel, double radius,
			double angle, int health, int internal_timer, int damage, double vel, Color[] colors, boolean fromPlayer, boolean heavy) {
		super(x_pos, y_pos, x_vel, y_vel, x_accel, y_accel, radius, angle, health, internal_timer);
		this.damage = damage;
		this.vel = vel;
		this.colors = colors;
		this.fromPlayer = fromPlayer;
		this.heavy = heavy;
	}

	//Accessors
	public int getDamage() {
		return damage;
	}
	
	public boolean fromPlayer(){
		return fromPlayer;
	}
	
	//Method: draw
	//Description: Draws the shot
	//Params: Component c: component to draw on, Graphics2D g2: graphics to draw with
	//Returns: void
	@Override
	public void draw(Component c, Graphics2D g2) {
		g2.translate(x_pos, y_pos);
		g2.setColor(colors[0]);
		g2.fillOval((int) -radius, (int) -radius, (int) (2*radius), (int) (2*radius));
		g2.setColor(colors[1]);
		g2.fillOval((int) (-radius*0.5), (int) (-radius*0.5), (int) (2*radius*0.5), (int) (2*radius*0.5));
		g2.translate(-x_pos, -y_pos);
	}

	//Modifiers

	//Method: update
	//Description: Moves the shot. All shots so far go in a straight line, so this is easy!
	//Params: none
	//Returns: void
	@Override
	public void update() {
		x_pos += vel*Math.cos(angle*Math.PI/180);
		y_pos += vel*Math.sin(angle*Math.PI/180);
	}

	//Method: collide
	//Description: Runs when the shot collides with something
	//Params: Character c: the character collided with
	//Returns: void
	@Override
	public void collide(Character c) {
		// If the shot is from the player and hits and enemy, or from an enemy and hits the player
		// the shot looses health and dies.
		// Enemy shot's should not damage other enemies, player shot's should only hit enemies
		if((c instanceof Enemy && fromPlayer) || (c instanceof Player && !fromPlayer)) loseHealth(1);
	}

	//Method: shouldRemove
	//Description: Returns true if the shot should be removed from the entity list
	//Params: none
	//Returns: boolean: should the shot be removed
	@Override
	boolean shouldRemove() {
		//should be removed if out of health but not if it's heavy, because heavy shots don't die
		// or if it is out of bounds with a leniacy of 0
		// so it doesn't hit enemies that are allowed to go slightly out of bounds
		return health <= 0 && !heavy || this.outOfBounds(0);
	}

	//Method: shouldKill
	//Description: Returns true if the shot should be killed. Shot's don't do anything special
	// when killed, so the shouldKill() and shouldRemove() methods are basically the same
	//Params: none
	//Returns:  boolean: should the shot be killed
	@Override
	public boolean shouldKill() {
		return health <= 0 && !heavy || this.outOfBounds(0);
	}

	//Method: kill
	//Description: Runs when a shot is killed. Shot's don't do anything special when killed,
	//				But when all entities are killed at the end of a level this method is used to remove
	//				shots that aren't from the player
	//Params: none
	//Returns:  void
	@Override
	public void kill() {
		//just a cludgy way of removing the enemies' shots at the end of a level
		if(!fromPlayer) health = 0;
	}

}
