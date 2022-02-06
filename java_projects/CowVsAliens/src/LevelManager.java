import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

// Class: LevelManager
// Written by: Ethan Frank
// Date: Nov 17, 2017
// Description: Runs the entire game. Draws everything, handles all the interactions.
public class LevelManager {
	//Instance Fields
	private static ArrayList<Character> characters;		// Every non-text entity in the game
	
	private static int score;							// The player's current score on the level
	
	private static LevelData[] levels;					// An array of LevelDatas, which hold the configurations that make the levels different
														// A LevelData contains spawn rates, the level name, the level code, and kill goals
	
	private static int level;							// The current level that is being run and should be run
	
	private static int timer;							// Level elapsed time
	
	private static Player player;						// The player object the user controls
	
	private static int width;							// The width and height of the screen. Used for determining where to spawn enemies
	private static int height;							// remove entities from the arrayList, and stop the player at the edge of the screen

	private static int startLevelCountdown;				// When running, the 321GO! coutdown is displayed on the screen. Afterwards, the level starts
														// and enemies start spawning
	private static String code;							// Keeps track of the level code the user is entering
	
	private static int time_since_gameovered;  			// Used to fade in gameover text
	private static int time_since_level_completed; 		// Used to fade in score, when reaches a certain value next level starts
	private static int time_since_main_menued;			// Used to animate main menu
	
	private static int[] kills;							// How many of each enemy has been killed
	
	private static State state;							// What is currently going on in the game
	private static State lastState;						// The state before pasuing, used to go back after pausing
	
	private static String name;							// The user's name, used in high score. 
	
	private static double[] mouse;							//Coordinates of the mouse

	
	// Method: initialize
	// Description: Initializes the levels, and the width and height of the screen
	//				Also initializes the characters array, and the level code, to both be empty
	// Params: LevelData[] levels: Array of level configurations
				// int width: width of the screen in pixels
				// int height: height of the screen in pixels
	// Returns: void
	public static void initialize(LevelData[] levels, int width, int height) {
		LevelManager.characters = new ArrayList<Character>();
		LevelManager.levels = levels;
		LevelManager.width = width;
		LevelManager.height = height;
		LevelManager.code = "";
		LevelManager.name = "";
		LevelManager.state = State.MAIN;	
		LevelManager.player = new Player();		
		LevelManager.player.setHealth(3);
		LevelManager.score = 200;							// Initalize score
		mouse = new double[2];
	}
	
	// Method: initializeLevel
	// Description: Sets up all variables for the start of a level
	// Params:	int level: which level to set up, defined by the LevelData at that index in the levels array
	// Returns: void
	public static void initializeLevel(int level){
		LevelManager.level = level;	//Set the current level to the wanted level
		LevelManager.timer = 0;     //how much time has elapsed in level
		LevelManager.characters.clear();				// Delete all entities
		LevelManager.player = new Player();				 
		LevelManager.player.setHealth(3);				// Set initial health of player
		LevelManager.characters.add(LevelManager.player);// Insert the player into the characters array the game manager handles it
		LevelManager.startLevelCountdown = 4*20-1; 		// Set the initital value of the timer for the start of level countdown
														// Level countdown goes 3-2-1-GO!, where each text stays on screen for 0.5 seconds
		LevelManager.state = State.COUNTDOWN; // If it is the first level, show the instructions
		LevelManager.score = 200;							// Reset score for new level
		LevelManager.kills = new int[levels[level].getSpawn_chances().length]; 	// Initialize kill array to length of spawn chances
																				// array of this level because it needed a length and I am afraid of constants
		//If this is the bonus level,make the character superpowered
		LevelManager.getPlayer().setSuper_powered(levels[level].getName()[0].equals("B-B-BONUS")); 
	}
	
	// Method: restartLevel
	// Description: Runs when a player tries again after failing a level
	// Params: none
	// Returns: void
	public static void restartLevel(){
		initializeLevel(level); //Initialize the current level again normally,
		state = State.COUNTDOWN; //But if it is the first level, don't show the instructions again
	}
	
	//Accessors
	public static ArrayList<Character> getCharacters() {
		return characters;
	}

	public static int getScore() {
		return score;
	}
	
	public static LevelData[] getLevels() {
		return levels;
	}
	
	public static int getLevel(){
		return level;
	}
	
	public static int getTimer(){
		return timer;
	}
	
	public static Player getPlayer(){
		return player;
	}
	
	// Method: playerDead
	// Description: Returns if the player is dead 
	// Params: none
	// Returns: boolean - is the player dead?
	public static boolean playerDead(){
		return player.getHealth() <= 0;
	}
	
	// Method: levelCompleted
	// Description: Checks to see if the level has been completed, which means the kill goals have been met
	// Params: none
	// Returns: boolean: Has the level been completed?
	public static boolean levelCompleted(){
		boolean good = true;//used to keep track of if all kill goals met
		
		//check if all kill goals met
		for(int i = 0; i < kills.length; i++){
			good&=kills[i]>=levels[level].getKillGoals()[i];
		}
		
		return good;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static State getState() {
		return state;
	}
	
	public static boolean gameover(){
		return state==State.GAMEOVER;
	}
	
	public static boolean isPaused(){
		return state==State.PAUSED;
	}
	
	// Method: getCurrentLevel
	// Description: Returns the level data associated with the current level
	// Params: none
	// Returns: LevelData - the level data associated with the current level
	public static LevelData getCurrentLevel(){
		return levels[level];	
	}
	
	public static boolean isShowingInstructions(){
		return state==State.INSTRUCTIONS;
	}

	//Modifiers
	public static void setCharacters(ArrayList<Character> characters) {
		LevelManager.characters = characters;
	}
	
	// Method: addCharacters(ArrayList<Character> characters)
	// Description: Adds multiple entities to the character array
	// Params: ArrayList<Character> characters: an arraylist of characters to add to the main character list
	// Returns: void
	public static void addCharacters(ArrayList<Character> characters) {
		LevelManager.characters.addAll(characters);
	}
	
	// Method: addCharacters(Character character)
	// Description: Adds a character to the main character list
	// Params: Character character: the character to add to the list
	// Returns: void
	public static void addCharacters(Character character) {
		LevelManager.characters.add(character);
	}

	public static void setScore(int score) {
		LevelManager.score = score;
	}
	
	// Method: addToScore
	// Description: If the level isn't over, adds an amount to the current score
	// Params: int points: the points to add to the current score
	// Returns: void
	public static void addToScore(int points){
		if(!(state==State.LEVELCOMPLETE)){//don't add the score gained when all the enemies die in the "win level animation"
			score=Math.max(1,score+points); //min score of 1
		}
	}
	
	public static void setLevels(LevelData[] levels) {
		LevelManager.levels = levels;
	}
	
	public static void setLevel(int level) {
		LevelManager.level = level;
	}
	
	public static void setWidth(int width) {
		LevelManager.width = width;
	}

	public static void setHeight(int height) {
		LevelManager.height = height;
	}

	public static void setGameover(boolean gameover){
		if(gameover){
			state=State.GAMEOVER;
		}
	}
	
	public static void setState(State state){
		//Switch state most of the time but if switching to paused only switch if current state = play or countdown
		if(state!= State.PAUSED || (state == State.PAUSED && (LevelManager.state == State.COUNTDOWN || LevelManager.state == State.PLAY))){
			LevelManager.state = state;
		}
		
		if(state == State.MAIN){
			time_since_main_menued = 0;
		}
		
		if(state == State.MAIN || state == State.INSTRUCTIONS || state == State.LEVELSELECT){
			//remove all in states that aren't playing
			characters.clear();
		}
		
		LevelManager.state.updateButtons(GameController.mouseX, GameController.mouseY, false); //Make sure buttons know about state change so they don't look like they did the last time they were on screen
	}
	
	// Method: flipPaused
	// Description: If paused, unpause. If unpaused, pause. Also reset level code guess
	// Params: none
	// Returns: void
	public static void flipPaused(){
		if(state==State.PAUSED){
			state=lastState;
		}else{
			lastState=state;
			state=State.PAUSED;
		}
		code = "";
	}
	
	// Method: addKill
	// Description: Increments the counter of how many of a certain enemy have been killed
	// Params: int type: the type of enemy to increment how many have been killed for
	// Returns: void
	public static void addKill(int type){
		kills[type]++;
	}
	
	// Method: tick
	// Description: Runs one iteration of the game. Checks collision, updates movement, spawns new entities, etc. 
	//            	This method is run at 40Hz, so to convert from seconds to iterations of the tick method do s*40.
	// Params: none
	// Returns: void
	public static void tick(){
		state.updateButtons(GameController.mouseX, GameController.mouseY, GameController.click); //Handle all buttons, (will not break even if there are none)

		if(state.shouldAllowControllerToMoveMouse() && GameController.usingController()//Game controller controls mouse sometimes
				&& (Math.abs(GameController.xValue)>0.5  || Math.abs(GameController.yValue)>0.5)){ 
			
			Robot r = null;
			try {
				 r = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
			mouse[0] = (int) Math.max(Math.min(1439, mouse[0]+Math.signum(GameController.xValue)
														*GameController.xValue*GameController.xValue/10*2), 1);
			mouse[1] = (int) Math.max(Math.min(899, mouse[1]+Math.signum(GameController.yValue)
														*GameController.yValue*GameController.yValue/10*2), 1);
			r.mouseMove((int) mouse[0], (int) mouse[1]);
		}else{
			mouse[0] = MouseInfo.getPointerInfo().getLocation().x; //Store mouse so it transitions seamlessly 
			mouse[1] = MouseInfo.getPointerInfo().getLocation().y; //when the controller starts controlling again
		}
	
		switch(state){
			case PAUSED:
				break;
			case LEVELCOMPLETE:
				//When the level ended, every enemy and shot was killed. When they are done their death animation, remove them from the list
				for(int i = characters.size()-1; i>=0; i--){
					if(characters.get(i) instanceof Player){
						characters.remove(i); //Remove the player so it doesn't move around
					}else if(characters.get(i).shouldRemove()){
						characters.remove(i);	
					}
				}
				//The player still gets to move around, and the enemies need to shrink their radii for their death aniation
				for(int i = 0; i<characters.size(); i++){
					characters.get(i).update();
				}
				//Increment the counter
				time_since_level_completed++;
				break;
			case GAMEOVER:
				time_since_gameovered++; //Increment the counter so the cool fade in animation of the gameover text works.
				break;
			case PLAY:
				timer++; //increment timer
				if(timer%10==0){
					addToScore(-1);
				}
				
				// Go through the spawn change of each enemy type, if a random number is less than it, spawn a new enemy of that type
				// Types with smaller spawn chances, therefore, spawn less often.
				for(int i = 0; i < levels[level].getSpawn_chances().length; i++){
					if(Math.random() < levels[level].getSpawn_chances()[i]){
						characters.add(new Enemy(i));
					}
				}
				
				//Run the update method on all the entities
				for(int i = 0; i<characters.size(); i++){
					characters.get(i).update();
				}
				
				// Check collisions
				// Because if A collides B, B collides A, when it finds A collides B it runs both entities's collides method on the other
				for(int i = characters.size()-1; i>=0; i--){
					for(int j = i-1; j>=0; j--){
						if(characters.get(i).intersects(characters.get(j))){
							characters.get(j).collide(characters.get(i));
							characters.get(i).collide(characters.get(j));	
						}
					}
				}	
		
				//If an entity should be killed, kill it. Killing is when a death animation would run
				//and because of this, it is not removed form the list just yet
				for(int i = characters.size()-1; i>=0; i--){
					if(characters.get(i).shouldKill()){
						characters.get(i).kill();
					}
				}
				
				// If the character should be removed from the list, remove it
				for(int i = characters.size()-1; i>=0; i--){
					if(characters.get(i).shouldRemove()){
						characters.remove((characters.get(i)));
					}
				}
				
				// If the entity is way out of bounds, with the high leiniacy
				// to allow for spawning out of bounds, remove them form the list
				for(int i = characters.size()-1; i>=0; i--){
					if(characters.get(i).outOfBounds(0.9)){characters.remove(i);}
				}
				
				//If the player is dead set gameover to true and initialize the time_since_gameovered counter
				if(playerDead()){
					state=State.GAMEOVER;//gameover
					time_since_gameovered = 0;
				}
				
				// If the player wins the level
				if(levelCompleted()){
					SoundEffects.WIN.play(); //Play the winning level noise
					state=State.LEVELCOMPLETE; // Say that the level has been won
					time_since_level_completed = 0; //Initalize the time_since_level_completed counter
					levels[level].setCompleted(true);
					if(level<levels.length-1){
						levels[level+1].setUnlocked(true);
					}
					
					try {
						Files.write(Paths.get("cookies.txt"), new byte[]{(byte) (level+1)});
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//Kill off every entity that is not a player for the cool death animations
					for(int i = characters.size()-1; i>=0; i--){
						if(!(characters.get(i) instanceof Player)){
							characters.get(i).kill();
						}
					}	
				}
				break;
			case COUNTDOWN:
				startLevelCountdown--; //Decrement the levelCountdown countdown timer

				if(startLevelCountdown <= 0){
					state=State.PLAY;
				}
				break;
			case INSTRUCTIONS:
				break;
		case MAIN:
			time_since_main_menued++; //Increment timer
			break;
		case LEVELSELECT:
			break;
		default:
			break;	
		}//end switch
	}
	
	// Method: draw
	// Description: Draws entities and text for the game
	// Params: Component c: the component to draw to, Graphics2D g2: the graphics to use to draw
	// Returns: void
	public static void draw(Component c, Graphics2D g2){
	
		//Draw all the entities, all the time
		for(Character ch : characters){
			ch.draw(c,  g2);
		}

		switch(state){
		case PAUSED:
			//Display the word paused
			drawCenteredText(g2, "PAUSED", 350, 100);	
			drawKillGoals(g2);
			break;
		case LEVELCOMPLETE:
				Color color = new Color(255, 153, 0 , Math.min(time_since_level_completed*10, 255)); //The color changes to make the text fade in
				drawCenteredText(g2, "YOUR SCORE", 270, 60, color);
				drawCenteredText(g2, ""+score, 320, 60, color);
			break;
		case GAMEOVER:
			//Fade in gameover text
			color = new Color(255, 153, 0 , Math.min(time_since_gameovered*3, 255));
			drawCenteredText(g2, "GAME OVER", 285, 90, color);
			drawKillGoals(g2);
			break;
		case PLAY:
			drawKillGoals(g2);
			break;
		case COUNTDOWN:
			// If on last section of countdown, draw "GO!"
			if((int) Math.ceil(startLevelCountdown/20)==0){
				drawCenteredText(g2, "GO!", 330, 100);
			}else{ //otherwise, draw the 3-2-1, where the current number is determined by the timer value
				drawCenteredText(g2, String.format("%d", (int) Math.ceil(startLevelCountdown/20)), 330, 100);
			}
			//Draw the level name, which can be on multiple lines
			for(int i = 0; i<levels[level].getName().length; i++){
				drawCenteredText(g2, levels[level].getName()[i], 310-levels[level].getName().length*70+70*i, 100);
			}
			drawKillGoals(g2);
			break;
		case INSTRUCTIONS:
			//Array of instructions, each element is on new line
			String[] instructions = {"MOVE: L or WASD", "AIM: R or MOUSE", "SHOOT: R2 or MOUSE", "PAUSE: ☐ or P", 
					"SELECT: X or MOUSE", "YOU HAVE THREE LIVES", "KILL ENOUGH ALIENS TO PROCEDE", "GOALS IN BOTTOM RIGHT"};

			//Draw the instructions
			for(int i = 0; i<instructions.length; i++){
				drawCenteredText(g2, instructions[i], (80+70*i), 37);
			}
			break;
		case MAIN:
			//Animate the menu text
			if(time_since_main_menued<10){
				drawCenteredText(g2, "COW", 140, 150 + (10-time_since_main_menued)*90);
			}else if(time_since_main_menued<25 && time_since_main_menued >15){
				drawCenteredText(g2, "VS", 300, 150+(25-time_since_main_menued)*90);
			}else if(time_since_main_menued<40 && time_since_main_menued >30){
				drawCenteredText(g2, "ALIENS", 460, 150+(40-time_since_main_menued)*90);
			}
			
			if(time_since_main_menued>=10){
				drawCenteredText(g2, "COW", 140, 150);
			}
			if(time_since_main_menued>=25){
				drawCenteredText(g2, "VS", 300, 150);
			}
			if (time_since_main_menued>=40){
				drawCenteredText(g2, "ALIENS", 460, 150);
			}
		
			break;
		case LEVELSELECT:
			break;
		default:
			break;
		}	
		
		// Draw the lives and score if playing
		if(state == State.PLAY || state == State.PAUSED || state == State.LEVELCOMPLETE || state == State.COUNTDOWN){
			g2.setColor(Color.getHSBColor(0.1f, 1f, 1f));	
			g2.setFont(new Font("Courier", 1, 25));
			for(int i = 0; i<LevelManager.getPlayer().getHealth(); i++)
			g2.drawString("X", 730-25*i, 20);
			//draw the score
			g2.drawString(String.format("Score: %05d", LevelManager.getScore()), 5, 20); 
		}
		
		state.drawButtons(g2); //Handle all buttons, (will not break even if there are none)
	}
	
	// Method: drawCenteredText
	// Description: Draws orange text centered in the screen
	// Params: @see drawCenteredText(Graphics2D g2, String text, int y, int size, int x, Color color) for description of params
	// Returns: void
	public static void drawCenteredText(Graphics2D g2, String text, int y, int size){
		drawCenteredText(g2, text, y, size, Color.getHSBColor(0.1f, 1f, 1f));
	}
	
	// Method: drawCenteredText
	// Description: Draws orange text centered at an x value
	// Params: @see drawCenteredText(Graphics2D g2, String text, int y, int size, int x, Color color) for description of params
	// Returns: void
	public static void drawCenteredText(Graphics2D g2, String text, int y, int size, int x){
		drawCenteredText(g2, text, y, size, x, Color.getHSBColor(0.1f, 1f, 1f));
	}
	
	// Method: drawCenteredText
	// Description: Draws text centered in screen at certain color
	// Params: @see drawCenteredText(Graphics2D g2, String text, int y, int size, int x, Color color) for description of params
	// Returns: void
	public static void drawCenteredText(Graphics2D g2, String text, int y, int size, Color color){
		drawCenteredText(g2, text, y, size, LevelManager.getWidth()/2, color);
	}
	// Method: drawCenteredText
	// Description: Draws text centered at x position
	// Params: Graphics2D g2: the graphics to use to draw
				// String text: the text to draw
				// int y: the y coordinate to draw the text at
				// int size: the size of the text
				// int x: x coordinate to center on
				// Color c: color of text
	// Returns: void
	public static void drawCenteredText(Graphics2D g2, String text, int y, int size, int x, Color color){
		Font font = new Font("Courier", 1, size); //Create new font of the size
		FontMetrics metrics = g2.getFontMetrics(font); // Get the font metrics
		g2.setColor(color);
		g2.setFont(font);
		g2.drawString(text, x-metrics.stringWidth(text)/2, y); // Draw the centered text
	}
	
	// Method: drawKillGoals
	// Description: Draws the kill goals and the current amount killed for each type of enemy in the bottom right of the screen
	//				The color of the text to express the kill goal of an enemy is the color of that enemy
	// Params: Graphics2D g2: the graphics to draw with
	// Returns: void
	private static void drawKillGoals(Graphics2D g2){
		float[] hues = {0.0f, 0.6f, 0.4f, 0.85f, 0.1f, 0.95f, 0.75f}; 	//The color of each type of enemy
																		// and therefore the color of the text showing that enemy's kill goals
		int[] killGoals = levels[level].getKillGoals();					// Get the kill goals for the current level
		int x = 750;											// The x coordinate to draw the kill goal at
		
		//Loop through every enemy type
		for(int i = killGoals.length-1; i>=0; i--){
			//Draw if you need to kill at least one of that enemy, and you have not already met that goal
			if(killGoals[i]!=0 && kills[i]<levels[level].getKillGoals()[i]){ 
				Color textColor = Color.getHSBColor(hues[i], 0.8f, 1); //Set the text color to that enemies color
				g2.setColor(textColor);
				
				Font font = new Font("Courier", 1, 25); //Set the font
				String str = kills[i] + "/" + killGoals[i]; // Construct the display string
				x-=g2.getFontMetrics(font).stringWidth(str)+10; //Subtract from x the width of this display string so the next one doesn't overlap.
				g2.setFont(font);
				
				g2.drawString(str, x, 740); //Draw the string
			}
		}
	}
	
	// Method: completeLevels
	// Description: completes all levels up to a certain level, and unlocks the next. Used when initializing the game and there is saved data
	// Params: int upTo: The farthest unlocked level. All before it are completed
	// Returns: void
	public static void completeLevels(int upTo){
		for(int i = 0; i<levels.length; i++){ //Set others to false, in case data is being cleared
			levels[i].setUnlocked(false);
			levels[i].setCompleted(false);
		}
		
		for(int i = 0; i<upTo; i++){
			levels[i].setUnlocked(true);
			levels[i].setCompleted(true);
		}
		if(upTo>=0 && upTo<levels.length){
			levels[upTo].setUnlocked(true);
		}
	}
}