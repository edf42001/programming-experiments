import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

//Class: Player
//Written by: Ethan Frank
//Date: Nov 14, 2017
//Description: Implements a player class. Players use an image to draw, can shoot, have lives, and are controlled by arrow keys. 

//Modified 11/15/17
//Adjust character control
//Implement lives decrementing when colliding with enemy. 
//Implement period of invincibility after hitting enemy indicated with flashing red
//Add packed constructor
public class Player extends Character{
	
	private ImageIcon image;			// The ImageIcon will be used to hold the Players's png.
	private ImageIcon hurt_image; 		// Guess what? The ImageIcon used to hold the image of a hurt player
	
	private int time_can_shoot;			// Value the internal timer must have for the player to be able to shoot. 
	private int time_till_vincibile;	// Value the internal timer must have for the player to be not invinvible. 
	
	private boolean try_shoot;			// When true, player will try to shoot if it can
	
	private boolean super_powered;		// When true, player shoots more awesome bullets faster
	

	private static final double max_vel = 9; //The maximum x or y velocity of the player. They can still compound when moving diagonally
	private static int shoot_delay = 4;      // How many iterations after shooting the player can shoot again.
										     // Static because I was lazy
	
	private double targetAngle;
	
	
	//Constructors
	// Default constructor: See packed constructor for description of parameters, initalizes a player
	// In the center of the screen facing upwards.
	public Player(){
		this(375, 375, 0, 0, 0, 0, 0, -90, 10, 0, 0, 0, -1, -1);
	}

	// Packedish constructor. I some point I said "Hey, want if I don't want to give people the ability to create whatever kind of player they want?"
	// Therefore some things are set automatically
	// parameters:	x_pos - Initial x position of the character (measured to center of character)
	//				y_pos - Initial y position of the character (measured to center of character)
	//				x_vel - Initial x velocity of character, in pixels/iteration
	//				y_vel - Initial y velocity of character, in pixels/iteration
	//				x_accel - Initial x acceleration of character, in pixels/iteration/iteration
	//				y_accel - Initial y acceleration of character, in pixels/iteration/iteration
	//				radius - Radius of character, assuming character to be circular. Overwritten to be half the width of the image
	//				angle - Initial angle of the character, in degrees. positive angles are clockwise
	//				health - Initial health of the character
	// 				internal_timer - initial value of internal timer
	// 				angular_vel - initial angular velocity of the character
	//				time_can_shoot - initial value of what the internal_timer needs to be for the player to shoot
	//				time_till_vincible - initial value of what the internal_timer needs to be for the player to be not invincible
	public Player(double x_pos, double y_pos, double x_vel, double y_vel, double x_accel, double y_accel, double radius,
			double angle, int health, int internal_timer, double angular_vel, double angular_accel, int time_can_shoot,
			int time_till_vincibile) {
		super(x_pos, y_pos, x_vel, y_vel, x_accel, y_accel, radius, angle, health, internal_timer);
		ClassLoader cldr = this.getClass().getClassLoader();	// These lines of code load the Character's png
        this.image = new ImageIcon(cldr.getResource("images/player.png"));
        this.hurt_image = new ImageIcon(cldr.getResource("images/player_hurt.png"));
        this.radius = image.getIconWidth()/2;
		this.time_can_shoot = time_can_shoot;
		this.time_till_vincibile = time_till_vincibile;
		this.try_shoot = false;				// not trying to shoot at start
		this.super_powered = false;			// not superpowerd
		this.targetAngle = -90;				//Start facing up
		this.angle = -90;
	}

	//Accessors
	//Method: draw
	//Description: Draws the player, makes him blink when hurt, makes the image rotate by its angle to face mouse
	//Params: Component c: component to draw image on, Graphics2D g2: graphics to draw with
	//Returns: void
	@Override
	public void draw(Component c, Graphics2D g2){
		g2.translate(x_pos, y_pos);
		g2.rotate((this.angle+90)*Math.PI/180);
		
		// If just hurt and therefore invincible, every couple iterations, instead of drawing the regular image draw hte hurt one
		// so you get a blinking effect. Otherwise, just draw the normal image
		if(time_till_vincibile > internal_timer && (double) (internal_timer%5)/5 < 0.5 && health > 0){
			hurt_image.paintIcon(c, g2, (int) -radius, (int) -radius);
		}else{
			image.paintIcon(c, g2, (int) -radius, (int) -radius);
		}
		
		g2.rotate(-(this.angle+90)*Math.PI/180);
		g2.translate(-(x_pos), -(y_pos));
		
	}

	//Modifiers
	public void setTime_can_shoot(int time_can_shoot) {
		this.time_can_shoot = time_can_shoot;
	}

	public void setTry_shoot(boolean try_shoot){
		this.try_shoot = try_shoot;
	}

	public void setShoot_Delay(int shoot_delay){
		Player.shoot_delay = shoot_delay;
	}
	
	// Method: setSuper_powered
	// Description: Sets the player to be superpowered or not. Also modifies shoot speed based on superpoweredness 
	// Params: boolean super_powered: Whether or not to be superpowered
	// Returns: void
	public void setSuper_powered(boolean super_powered){
		this.super_powered = super_powered;
		Player.shoot_delay = super_powered?0:4;
	}
	
	//Method: update
	//Description: Runs once per iteration, handles movement and shooting
	//Params: none
	//Returns: void
	@Override
	public void update() {
		double accel = 2; //The value of acceleration when keys are pressed
		double maxAngularVel = 30;

		// If the player can shoot now, and either is trying to because the mouse is pressed or autoshoot is enabled,
		// shoot and set the time_can_shoot for the next shot
		if(time_can_shoot<internal_timer && try_shoot){
			spawn(); //create the shot
			time_can_shoot = internal_timer+shoot_delay;
		}
		
		
		//Constrain velocity
		x_vel = Math.min(Math.max(x_vel,-max_vel), max_vel);
		y_vel = Math.min(Math.max(y_vel,-max_vel), max_vel);
			
		//Kinematics
		x_pos += x_vel;
		y_pos += y_vel;
		
		//Make sure doesn't go out of bounds
		x_pos = Math.min(Math.max(0+radius, x_pos), LevelManager.getWidth()-radius); 
		y_pos = Math.min(Math.max(0+radius, y_pos), LevelManager.getHeight()-radius);
	
		if(Math.abs(angleDistance(targetAngle, angle))<=maxAngularVel){
			angle = targetAngle;
		}else{
			angle += maxAngularVel*Math.signum(angleDistance(targetAngle, angle));
		}
	
		internal_timer++; //Increment internal timer
	}
	
	//Method: collide
	//Description: Runs when the player collides with another entity. This includes enemies and enemies' shots
	//Params: Character c: the entity collided with
	//Returns:  void
	@Override
	public void collide(Character c) {
		//If collided entity is an enemy and the player is not invincible and the enemy isn't dying, or
		// the collided entity is an enemy's shot and the player is not invincible
		if((c instanceof Enemy && time_till_vincibile < internal_timer && !((Enemy) c).isBasicallyDead()) ||
				(c instanceof Shot && !((Shot) c).fromPlayer()) && time_till_vincibile < internal_timer){
			SoundEffects.HURT.play(); // play hurt sound effect
			LevelManager.addToScore(-100);
			loseHealth(1); 				// lose one health
			time_till_vincibile = internal_timer+100; //Make it so character is invincible for 100 iterations
		}
	}
	
	// Method: spawn
	// Description: Creates a new Shot from the player that travels in the direction of the mouse
	// Params: none
	// Returns: void
	private void spawn() {
		double vel = 15;
		double spawnAngle = Math.toRadians(angle); //figure out angle to shoot at
		if(super_powered){//If player is superpowered create the special shot
			LevelManager.addCharacters(new Shot(x_pos+radius*Math.cos(spawnAngle), y_pos+radius*Math.sin(spawnAngle),
					vel, 180/Math.PI*spawnAngle, 100, 10, true, true));
		}else{ // otherwise create the normal one
			LevelManager.addCharacters(new Shot(x_pos+radius*Math.cos(spawnAngle), y_pos+radius*Math.sin(spawnAngle),
					vel, 180/Math.PI*spawnAngle, 1, 4, true, false));
		}
		SoundEffects.SHOT.play(); //Play the shooting sound
	}
	
	//Method: shouldRemove
	//Description: Returns true when player should be removed from entity list, which is never because then it wouldn't be drawn
	//Params: none
	//Returns:  boolean: should the player be removed
	@Override
	public boolean shouldRemove() {
		return false;
	}

	//Method: kill
	//Description: Runs when character is killed. Plays the game over sound effect,
	//				and on the last level (which is a joke) quits out of the game
	//Params: none
	//Returns: void
	@Override
	public void kill() {
		SoundEffects.DEATH.play();
	}

	//Method: shouldKill
	//Description: Returns true when the player should be killed, which is when it is out of lives
	//Params: none
	//Returns: boolean: should the player be killed
	@Override
	boolean shouldKill() {
		return health<=0;
	}	
	
	public void setTargetAngle(double targetAngle){
		this.targetAngle = targetAngle;
	}
	
	private double angleDistance(double a1, double a2){
		return -(((a2-a1 + 180) % 360 + 360) % 360 - 180);
	}
}