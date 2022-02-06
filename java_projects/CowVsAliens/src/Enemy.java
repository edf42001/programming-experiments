import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;

// Class: Enemy
// Written by: Ethan Frank
// Date: Nov 25, 2017
// Description: Enemies are cute colored circles with eyes that move around the screen in different ways and harm you if you hit them
//  			Some shoot at you or explode on death

// Modified 11/27/17 by Ethan Frank
// Remove direction variable because direction and angle instance fields were doing
// the same job of keeping track of which way the enemy was going
public class Enemy extends Character{
	//Instance fields
	private Color color; 				//The color of the enemy

	private int type;					//the type of enemy. Affects movement patters, spawn patterns, everything really;
	
	private double vel;					// Velocity of enemy. Used to describe some enemies motion.
	
	private int time_till_not_demonstrating_was_hit; //Used to make the character blink white when hit
	
	private double angular_vel;			// Angular velocity of the character. Some characters make use of this, other's not.
	
	private int worth;					//Score player gets when enemy is killed
	
	private int time_till_death_animation_over; //Used to determine when the death animation of the player is over
	
	private int[] target;				//The coordinates of the target to aim towards, if that is a thing the enemy does
	
	//Constructors	
	//Constructor that creates an enemy of a certain type. This is the constructor used when spawning enemies naturally.
	public Enemy(int type){
		//These arrays hold different predetermined values for different enemies.
		int[] radii = {16, 20, 35, 10, 19, 24, 30};						// The radii of the enemies			
		float[] hues = {0.0f, 0.6f, 0.4f, 0.85f, 0.1f, 0.95f, 0.75f};	// the hue portion of the color of the enemy
		int[] lives = {1, 5, 13, 1, 3, 1, 8};							// The lives of each enemy
		double[] vels = {4, 3.5, 1.9, 8, 5, 2.6, 2.2};					// The velocity of each enemy
		int[] worths = {5, 10, 30, 15, 15, 10, 20};						// The point value of each enemy
		
		//Initialize these internal variables with their predetermined values
		this.type = type; 
		this.color = Color.getHSBColor(hues[type], 0.8f, 1); //Color is created using hue
		this.radius = radii[type];
		this.health = lives[type];
		this.vel = vels[type];
		this.worth = worths[type];
		this.angle = 0;	
		this.target = new int[] {LevelManager.getWidth()/2, LevelManager.getHeight()/2}; //Target is set to center of the screen for now
		
		initializePosAndAngle(); //Set the enemy's position to outside of screen
		if(type == 0){ //Red, nothing else needs to be done
			
		}else if(type == 1){ //Blue, 
			initializeAngle(1.5); //make go at random direction
		}else if(type == 2){ //Green
			initializeAngle(1.5); //Make go in random direction
			initializeAngularVel(0.25); //And curve
		}else if(type == 3){ //Pink
			initializeAngle(1); //Randomly point towards center, though they will immediately start tracking player after spawning
		}else if(type == 4){// Orange
			initializeAngle(0.2); //point towards center, though they will start tracking a random point in space
		}else if(type == 5){// red/pink
			initializeAngle(1); // point towards center
			initializeAngularVel(0.06);//and turn
		}else if(type == 6){//purple
			initializeAngle(0.5);//point towards center
			initializeAngularVel(0.04);//and turn
		}
		
		vel*=(0.5*Math.random()+0.75); //make velocities vary
	}
	
	// Enemy's packed constructor, also I never use this
	// description: Initialize a new Character object. Initial target coordinates are the center of the screen
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
	//				color - initial color of enemy
	// 				direction - initial direction of enemy
	public Enemy(double x_pos, double y_pos, double x_vel, double y_vel, double x_accel, double y_accel, double radius,
			double angle, int health, int internal_timer, Color color, double angular_vel, int[] target) {
		super(x_pos, y_pos, x_vel, y_vel, x_accel, y_accel, radius, angle, health, internal_timer);
		this.color = color;
		this.internal_timer = internal_timer;
		this.angular_vel = angular_vel;
		this.target = target;
	}
	
	//Accessors
	public Color getColor() {
		return color;
	}
	
	//Method: draw
	//Description: Draws the enemy as a circle of a certain color, eyes of a brigher version of that color,
	// 				everything scales evenly when the radius is increased, rotates the drawing based on angle,
	//				when the enemy has been damaged the color gets brighter briefly to make a hit effect,
	//				x and y coordinates are center of the enemy
	//Params: Component c: component to draw on (no-op), Graphics2D g2: graphics to draw with
	//Returns: void
	@Override
	public void draw(Component c, Graphics2D g2) {
		g2.translate(x_pos, y_pos);
		g2.rotate(this.angle*Math.PI/180);
		//When the enemy is hit it gets brighter
		g2.setColor((time_till_not_demonstrating_was_hit<internal_timer)?this.color:this.color.brighter());
		//Draw body
		g2.fillOval((int) -radius, (int) -radius, (int) (2*radius), (int) (2*radius));
		//Make eyes lighter version of body color and draw them
		g2.setColor(this.color.brighter().brighter().brighter().brighter());
		g2.fillOval((int) (0.5*radius-0.3*radius), (int) (-0.55*radius-0.3*radius), (int) (0.6*radius), (int) (0.6*radius));
		g2.fillOval((int) (0.5*radius-0.3*radius), (int) ( 0.55*radius-0.3*radius), (int) (0.6*radius), (int) (0.6*radius));
		g2.rotate(-this.angle*Math.PI/180);
		g2.translate(-x_pos, -y_pos);
	}
	
	public int getType(){
		return type;
	}
	
	// Method: isBasicallyDead
	// Description: After the enemy is killed and is doing the death animation, it still exists but should not damage the player
	//   			This method returns true if the enemy is dying and should not damage the player
	// Params: none
	// Returns: boolean: is the enemy dying and should not damage the player?
	public boolean isBasicallyDead(){
		return time_till_death_animation_over != 0;
	}
	
	//Modifiers
	public void setColor(Color color) {
		this.color = color;
	}
	
	//Method: update
	//Description: Handles movement and shooting for every type of enemy in the game. Of course, most of them don't shoot
	//Params: none
	//Returns: void
	@Override
	public void update() {
		if(!isBasicallyDead()){ //only move when not doing death animation
			if(type == 0){//Red, travels only vertically or horizontally
			}else if(type == 1){//Blue, travels straight at angles
				goInDirection(); //go straight at angle
			}else if(type == 2){//Green, travles in a big arc
				angle+=angular_vel;//turn
			}else if(type == 3){//pink, heads towards player
				//Luckily player is part of LevelManager so I can do this
				target = new int[] {(int) LevelManager.getPlayer().getX_pos(), (int) LevelManager.getPlayer().getY_pos()}; //set target ot player
				aimBySettingAngularVel(0.4); //try to aim towards player
				angle+=angular_vel;//turn
			}else if(type == 4){//Orange
				double max_angular_vel = 3;//to make sure it doesn't turn way too fast and look bad
				//Every 200 iterations make the target a new random point in the screen
				if(internal_timer%200 == 0){
					double spread_out_ness = 2.5;
					this.target = new int[] {(int) (LevelManager.getWidth()/2*(1+spread_out_ness*(Math.random()-0.5))),
							(int) (LevelManager.getHeight()/2*(1+spread_out_ness*(Math.random()-0.5)))};
				}
				
				//Aim for that new target
				aimBySettingAngularVel(0.03);
				angular_vel = Math.min(Math.max(angular_vel, -max_angular_vel), max_angular_vel);
				angle+=angular_vel;
			}else if(type == 5){
				angle+=angular_vel;
			}else if(type == 6){
				if(internal_timer%80 == 0 && !outOfBounds(0)){
					LevelManager.addCharacters(new Shot(x_pos, y_pos, 7.5,
							Math.toDegrees(Math.atan2(LevelManager.getPlayer().getY_pos()-y_pos,LevelManager.getPlayer().getX_pos()-x_pos)),
							1, 7, false, false));
				}
				angle+=angular_vel;
			}
			
			goInDirection();//set vels to go in direction of angle
			
			//Kinematics
			x_pos += x_vel;
			y_pos += y_vel;
			
		}else{ //killed, do dying animation
			radius*=0.6;
		}
		
		internal_timer++;	
	}

	//Method: collide
	//Description: Runs when this enemy collides with another entity
	//Params: Character c: the character this enemy has collided with
	//Returns: void
	@Override
	void collide(Character c) {
		//If the enemy collided with a shot from the player, and the shot is not in the dying phase:
		if(c instanceof Shot && !((Shot) c).shouldKill() && ((Shot) c).fromPlayer()){
			loseHealth(((Shot) c).getDamage()); 					// lose health equal to the shot's damage
			time_till_not_demonstrating_was_hit = internal_timer+2; // Initialize the "blink white" effect counter. This effect lasts for 2 iterations
			//If the enemy is now out of health and it is the kind that explodes when killed (5)
			if(shouldKill() && type == 5){
				//Spawn the six shots that come out
				for(int i = 0; i<6; i++){
					LevelManager.addCharacters(new Shot(x_pos, y_pos, 8, 60*i, 1, 7, false, false));
				}
			}	
			// If the enemy is a pink (3) enemy that can die when hitting the player,
			// and it is now dead, add its value to the score and notch a kill for it
			// This is inside the (enemy hit a player's shot) if statement, so the user only gets credit if they kill the enemy
			// not the enemy exploding on contact
			if(shouldKill() && type == 3){
				LevelManager.addKill(3);
				LevelManager.addToScore(worth);
			}
		}
		if(c instanceof Player && type==3){loseHealth(1);}//If the pink (3) enemy hits the player it loses health which makes it die
	}

	//Method: shouldRemove
	//Description: Returns true when this enemy should be removed from the list of entities.
	//				Enemies should be removed when their death animation is over, which is when
	//				the death animation counter value is less than the internal timer, and also not 0 because that means
	//				the death animation has not started yet.
	//Params: none
	//Returns: boolean: should the enemy be removed from the list?
	@Override
	public boolean shouldRemove() {
		return time_till_death_animation_over < internal_timer && time_till_death_animation_over != 0;
	}
	
	//Method: kill
	//Description: Runs when an enemy is killed. Plays the sound effect, adds their point value to the score,
	//        		Notches another kill of that type in the array, and initializes the death animation counter
	//Params: none
	//Returns: none
	@Override
	public void kill() {
		SoundEffects.EXPLODE.play();
		// Because pink (3) enemies commit suicide, their point values and kill counts are handled in the collide method,
		// where it can be determined if they were killed by the player or by running into the player
		if(type!=3){ 
			LevelManager.addKill(type);
			LevelManager.addToScore(worth);
		}
		time_till_death_animation_over = internal_timer + 10;
	}

	//Method: shouldKill
	//Description: Returns when to kill an enemy. Enemies should be killed when they have no health,
					// And they have not started their death animation. If they have, that means they have already been killed
					// and should not be again because then the sound would play and score increase more than it should
	//Params: none
	//Returns: boolean: should the enemy be killed
	@Override
	public boolean shouldKill() {
		return health<=0 && time_till_death_animation_over == 0;
	}
	
	// Method: initializePosAndAngle
	// Description: Randomly sets the coordinates of an enemy to a location around the border of the screen.
					// Also sets the direction, though this can be overridden, to be either vertical or horizontal, 
					// depending on which edge the enemy is set to,
					// and makes sure the enemy heads towards the visible area of the screen
	// Params: none
	// Returns: void
	private void initializePosAndAngle(){
		//Determine spawn location around edges of screen
		if(Math.random()<0.5){ 	//spawn on the sides
			y_pos = Math.random()*LevelManager.getHeight()*0.9;
			x_pos = LevelManager.getWidth()/2+((int) (2*Math.random())*2-1)*(LevelManager.getWidth()/2+radius);
			angle = Math.signum(x_pos)*90+90; //direction points horizontally into screen
		}else{ 					//spawn on top and bottom
			y_pos = LevelManager.getHeight()/2+((int) (2*Math.random())*2-1)*(LevelManager.getHeight()/2+radius);
			x_pos = Math.random()*LevelManager.getWidth()*0.9;
			angle = -Math.signum(y_pos)*90; //direction points vertically into screen
		}
	}
	
	// Method: initializeAngle
	// Description: Sets the angle of an enemy, which spawn on the edges, to be aiming towards a random point
	//				in a box around the center of the screen
	// Params: double spread_out_ness: when larger, box that enemies can aim towards is larger. Range: 0-2. 
	// Returns: void
	private void initializeAngle(double spread_out_ness){
		//aim towards random point in a box centered around the center of the screen
		angle = 180/Math.PI*Math.atan2(LevelManager.getWidth()/2*(1+spread_out_ness*(Math.random()-0.5))-y_pos, 
				LevelManager.getHeight()/2*(1+spread_out_ness*(Math.random()-0.5))-x_pos);
	}

	// Method: initializeAngularVel
	// Description: Randomly gives the enemy mult or -mult angular vel
	// Params: double mult: the magnitude of the angular velocity.
	// Returns: void
	private void initializeAngularVel(double mult){
		angular_vel = mult*Math.signum(Math.random()-0.5);
	}
	
	// Method: goInDirection
	// Description: Modifies the x_vel and y_vel of the enemy so it travels in the direction
	//				of angle, at a speed of vel
	// Params: none
	// Returns: void
	private void goInDirection(){
		x_vel = vel*Math.cos(Math.toRadians(angle));
		y_vel = vel*Math.sin(Math.toRadians(angle));
	}

	// Method: aimBySettingAngularVel
	// Description: Uses the stored target coordinates and the enemy's current position to
	//				change angular vel to turn towards the target
	// Params: double mult: affects how vigourusly the enemy tries to turn towards the target
	// Returns: void
	private void aimBySettingAngularVel(double mult){
		angular_vel = (180/Math.PI*Math.atan2(target[1]-y_pos,target[0]-x_pos)-angle);
		angular_vel = mult*(((((angular_vel+180) % 360) + 360) % 360) - 180);
	}
	
}