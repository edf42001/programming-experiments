import java.awt.Component;
import java.awt.Graphics2D;

// Class: Character (You may also refer to it as an "Entity"
// Written by: Mr. Swope
// Date: 10/28/15
// Description: This class implements a Character.

// Modified 11/14/17
// Author: Ethan Frank
// Description: Change x_coordinate and y_coordinate names to x_pos and y_pos. Change values to doubles for more accurate movements.
// Add instance fields: x_vel, y_vel, x_accel, y_accel, radius
// Descriptions of these are next to their decelerations.
// Characters are now circular
// Intersect method is based off of distance between centers and sum of radii. 

// Modified 11/15/17
// Add internal_timer, health
// Create abstract method collide(Character c)

// Modified 11/15/17 20:12
// Losing sight of cohesive structure
// No clue what I am doing

// Modified 11/17/17
// Add a static ArrayList that holds the characters and Integer score to the class
// This means the characters themselves get to add more characters to the game and modify the score.
// Don't know how good of a practice this is. Probably a bad one. But whatever, it's just a game for a high school class. 
// Someday, maybe someone should tell me the correct way to do things instead of me always having to figure it out. 

// Modified somtime in the past
// Removed that static arraylist to my new LevelManger class
// Made everything the way it is now

public abstract class Character {	
	//Instance fields
	protected double x_pos;			// These doubles store the position of the character.
	protected double y_pos;			// They are converted to ints when drawing the character to the panel
	
	protected double x_vel;			// These doubles store the current velocity of the character
	protected double y_vel;
	
	protected double x_accel;		// These double hold the acceleration of the character. 
	protected double y_accel;
	
	protected double radius;		// This is the approximate radius of the character
	
	protected double angle;   		// Current angle of character in degrees. 0 degrees is rightward. +angles are CW. 
	
	protected int health; 			// Health of the character. When it reaches 0, the character dies. 
	
	protected int internal_timer;  	// All characters have an internal timer. Why not?

	
	// Constructors
	// Default constructor. See packed constructor for description of parameters
	// Initializes the most useless of characters. Oh wait, abstract classes can't be initialized
	// Sets the most useless of instance field values for some class that extends Character.
	public Character(){
		this(0,0,0,0,0,0,0,0,0,0);
	}
	
	// Character's packed constructor
	// description: Initialize a new Character object.
	// parameters:	x_pos - Initial x position of the character (measured to center of character)
	//				y_pos - Initial y position of the character (measured to center of character)
	//				x_vel - Initial x velocity of character, in pixels/iteration
	//				y_vel - Initial y velocity of character, in pixels/iteration
	//				x_accel - Initial x acceleration of character, in pixels/iteration/iteration
	//				y_accel - Initial y acceleration of character, in pixels/iteration/iteration
	//				radius - Radius of character, assuming character to be circular
	//				angle - Initial angle of the character, in degrees. postitive angles are clockwise
	//				health - Initial health of the character
	// 				internal_timer - Can be used for whatever purpose. Increases at 1 per iteration.
	
	public Character(double x_pos, double y_pos, double x_vel, double y_vel, double x_accel, double y_accel,
			double radius, double angle, int health, int internal_timer) {
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.x_vel = x_vel;
		this.y_vel = y_vel;
		this.x_accel = x_accel;
		this.y_accel = y_accel;
		this.radius = radius;
		this.angle = angle;
		this.health = health;
		this.internal_timer = internal_timer;
	}

	//Accessors
	public double getX_pos() {
		return x_pos;
	}

	public double getY_pos() {
		return y_pos;
	}

	public double getX_vel() {
		return x_vel;
	}

	public double getY_vel() {
		return y_vel;
	}

	public double getX_accel() {
		return x_accel;
	}

	public double getY_accel() {
		return y_accel;
	}

	public double getRadius() {
		return radius;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public int getHealth() {
		return health;
	}

	public int getInternal_timer() {
		return internal_timer;
	}
	
	// method: intersects
	// description: This method tells if one character is intersecting another. Because all characters are circles, this is easy
	// Parameters: Character c: the character to check if the current Character intersects with
	// Returns: boolean: do they intersect?
	public boolean intersects(Character c){
		return Math.sqrt(Math.pow(x_pos-c.getX_pos(),2) + Math.pow(y_pos-c.getY_pos(),2)) < (radius + c.getRadius());
	}
	
	//Modifiers
	public void setX_pos(double x_pos) {
		this.x_pos = x_pos;
	}

	public void setY_pos(double y_pos) {
		this.y_pos = y_pos;
	}

	public void setX_vel(double x_vel) {
		this.x_vel = x_vel;
	}

	public void setY_vel(double y_vel) {
		this.y_vel = y_vel;
	}

	public void setX_accel(double x_accel) {
		this.x_accel = x_accel;
	}

	public void setY_accel(double y_accel) {
		this.y_accel = y_accel;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}

	public void setInternal_timer(int internal_timer) {
		this.internal_timer = internal_timer;
	}
	
	// Method: loseHealth
	// Description: Removes a certain amount of health from the character's total health 
	// Params: int health: the amount of health to remove
	// Returns: void
	public void loseHealth(int health){
		this.health-=health;
	}
	
	// Method: outOfBounds
	// Description: Tells when a chracter is fully outside a certain box 
	// Params: double leeway: determines how far out of bounds the character needs to be to be determiend "out of bounds"
	//  If 0, character only has to leave screen. If 1, character has to leave an area twice 3 times the width of the screen.
	// Returns: boolean: is the character out of bounds?
	public boolean outOfBounds(double leeway){
		//LEEWAY: Yaknow, because they spawn out of bounds so you want them to be really out of bounds before they die. 
		return x_pos < leeway*-LevelManager.getWidth()-radius || x_pos > (1+leeway)*LevelManager.getWidth() + radius ||
				y_pos < leeway*-LevelManager.getWidth()-radius  || y_pos > (1+leeway)*LevelManager.getHeight() + radius;
	}
	
	// Method: collide
	// Description: Runs when the character collides with another character
	// Params: Character c - the character this object has collided with
	// Returns: void
	abstract void collide(Character c);
	
	// Method: shouldRemove
	// Description: Returns true if a character should be removed from the character list in the LevelManager class
	// Params: none
	// Returns: boolean: Should the character be culled?
	abstract boolean shouldRemove();
	
	// Method: shouldKill
	// Description: Returns true if a character should start the dying process, such as cool death animations
	// Params: none
	// Returns: boolean: Should the character start dying because it was murdered?
	abstract boolean shouldKill();
	
	// Method: kill
	// Description: Kills the character, so the character can do whatever it does when it is killed
	// Params: none
	// Returns: void
	abstract void kill();
	
	// Method: update
	// Description: Should be called once per iteration of the game.
	// 				Updates the character's position, velocity, and other internal variables
	// Params: none
	// Returns: void
	abstract void update();
	
	// Method: draw
	// Description: Guess what? This draws the Character
	// Params: Component c: Component to draw on, Graphics2D g2: Graphics to draw the charcter with
	// Returns: void
	abstract void draw(Component c, Graphics2D g2);
}
