// Class: GraphicsPanel
// Written by: Mr. Swope
// Date: 10/28/15
// Description: This class is the main class for this project.  It extends the Jpanel class and will be drawn on
// 				on the JPanel in the GraphicsMain class.  Your project should have at least one character that moves
//				with the arrow keys and one character that moves with the clock.  Finally, you should detect if the
//				two items intersect and have something happen if they do intersect.
//
// Since you will modify this class you should add comments that describe when and how you modified the class.  
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;

public class GraphicsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	static final int width = 750;					// The width of the game screen
	static final int height = 750;					// The height of the game screen
	
	private Timer t;								// The timer is used to move objects at a consistent time interval.
	
	double background_angle;						// The angle of the background image, in degrees
	int start_level = 0;							// Which level to start the game on, for tessting purposes only
	Thread thread;
	Runnable r;
	// This array holds the different level configurations for each level. 
	LevelData[] levels;
	
	public GraphicsPanel() throws Exception{

		setPreferredSize(new Dimension(width,height));  // Set these dimensions to the width of your background picture
		
		levels = new LevelData[]{ // Initialize levels
					new LevelData(new double[] {0.5, 0, 0, 0, 0, 0, 0}, new String[] {"EASY", "START"}, new int[] {15, 0, 0, 0, 0, 0, 0}),
					new LevelData(new double[] {0.15, 0, 0, 0, 0, 0, 0}, new String[] {"A BIT HARDER"},  new int[] {40, 0, 0, 0, 0, 0, 0}),
					new LevelData(new double[] {0.65, 0.75, 0, 0, 0, 0, 0},new String[] {"THERE'S", "MORE?!"}, new int[] {10, 10, 0, 0, 0, 0, 0}),
					new LevelData(new double[] {0.75, 0.85, 3, 0, 0, 0, 0}, new String[] {"THE BIGGER", "THEY ARE"}, new int[] {10, 7, 3, 0, 0, 0, 0}),
					new LevelData(new double[] {1.1, 1.2, 3.5, 4.5, 0, 0, 0}, new String[] {"AAAHHHHHHH!"},  new int[] {15, 10, 3, 6, 0, 0, 0}),
					new LevelData(new double[] {1, 2, 4, 0, 1.7, 0, 0}, new String[] {"KILL THEM", "QUICK"},  new int[] {10, 5, 3, 0, 10, 0, 0}),
					new LevelData(new double[] {2, 1.6, 3, 0, 0, 0, 4}, new String[] {"OH", "MY", "GAWD"}, new int[] {8, 5, 3, 0, 0, 0, 7}),
					new LevelData(new double[] {2, 2.5, 5, 0, 5, 1.4, 0},new String[] {"WHY MUST I", "DO THIS"}, new int[] {8, 5, 3, 0, 7, 20, 0}),
					new LevelData(new double[] {0, 0, 0.19, 0, 0, 0, 0}, new String[] {"THE MAZE"}, new int[] {0, 0, 15, 0, 0, 0, 0}),
					new LevelData(new double[] {3, 3, 4, 1.1, 0, 0, 0}, new String[] {"I CAN HAZ", "DODGE?"},  new int[] {9, 5, 3, 30, 0, 0, 0}),
					new LevelData(new double[] {0, 0.6, 0, 0, 1.2, 0, 0},new String[] {"COMPLEMENTARY", "COLORS"},  new int[] {0, 10, 0, 0, 25, 0, 0}),
					new LevelData(new double[] {0, 0, 0, 0, 0.5, 0, 0}, new String[] {"CROWD", "CONTROL"}, new int[] {0, 0, 0, 0, 45, 0, 0}),
					new LevelData(new double[] {0, 0, 0, 0, 0, 0, 1.4}, new String[] {"I CAN HAZ", "DODGE?2"}, new int[] {0, 0, 0, 0, 0, 0, 20}),
					new LevelData(new double[] {0.046, 0, 0, 0, 0, 0, 0}, new String[] {"THE MAZE 2"}, new int[] {110, 0, 0, 0, 0, 0, 0}),
					new LevelData(new double[] {0, 0, 0, 15, 0, 0.75, 5}, new String[] {"I CAN HAZ", "DODGE?3"}, new int[] {0, 0, 0, 2, 0, 50, 3}),
					new LevelData(new double[] {0, 0, 0, 0, 0, 0.25, 0}, new String[] {"WHYYYYYYYYYY"},  new int[] {0, 0, 0, 0, 0, 45, 0}),
					new LevelData(new double[] {1.9, 2, 7, 12, 3.2, 3, 6}, new String[] {"ROY G. BIV"},new int[] {10, 7, 3, 3, 8, 12, 4}),
					new LevelData(new double[] {0, 0, 0, 0.27, 0, 0, 0}, new String[] {"I CAN HAZ", "DODGE?4"}, new int[] {0, 0, 0, 100, 0, 0, 0}),
					new LevelData(new double[] {0.01, 0.01, 0.01, 0, 0.01, 0.0, 0.}, new String[] {"B-B-BONUS", "LEVEL!"}, new int[] {550, 550, 550, 0, 550, 0, 0})
		};
		
		levels[0].setUnlocked(true); //Unlock the first level
		
	
		GameController.init(this);						// Load GameController
		
        t = new Timer(25, new ClockListener(this));  	// t is a timer.  This object will call the ClockListener's
        											 	// action performed method every 25 milliseconds once the timer is started.
        
        SoundEffects.values();							 //Pre-initialize all sound effects
		LevelManager.initialize(levels, width, height);	 // Configure the level manager with the levels to play and the width and height of the screen

		background_angle = 0;							// Inital angle of background picture
        
        t.start();									

		new Thread(new Runnable(){
			javazoom.jl.player.Player player;
			@Override
			public void run() {
				while(true){
					try{
						player = new javazoom.jl.player.Player(getClass().getResource("sounds/background_music.mp3").openStream());
						player.play();
					} catch (JavaLayerException | IOException e) {
						e.printStackTrace();
					}
				}
			}	
		}).start();
		
		new Thread(new Runnable(){
			Robot r = new Robot();
			@Override
			public void run() {
				while(true){
					r.mouseMove(Math.min(MouseInfo.getPointerInfo().getLocation().x+1,1438), 
							Math.min(MouseInfo.getPointerInfo().getLocation().y+1, 898));
					try {
						Thread.sleep(50000);//50 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}	
		}).start();	
		
		//Create or read the cookies file with the game data
		if(!(new File("cookies.txt").exists())){//If the cookies file doesn't exist create it
			Files.write(Paths.get("cookies.txt"), new byte[]{0});
		}else{
			byte[] data = Files.readAllBytes(Paths.get("cookies.txt"));
			LevelManager.completeLevels(data[0]);
		}
	}
	
	// method: paintComponent
	// description: This method will paint the items onto the graphics panel.  This method is called when the panel is
	//   			first rendered.  It can also be called by this.repaint()
	// parameters: Graphics g - This object is used to draw your images onto the graphics panel.
	public void paintComponent(Graphics g){	
		Graphics2D g2 = (Graphics2D) g;
		
		ClassLoader cldr = this.getClass().getClassLoader();	// These four lines of code load the background picture.
		String imagePath = "images/background.png";			
		URL imageURL = cldr.getResource(imagePath);	
		ImageIcon image = new ImageIcon(imageURL); 

		//These lines of code rotate and draw the background image
		g2.translate(width/2, height/2);
		g2.rotate(background_angle);
		image.paintIcon(this, g2, -1920/2, -1080/2);
		g2.rotate(-background_angle);
		g2.translate(-width/2, -height/2);
		
		LevelManager.draw(this, g2);		//Draw every other single thing in the game except the background
	}
	
	// method:clock
	// description: This method is called by the clocklistener every 25 milliseconds.  You should update the coordinates
	//				of one of your characters in this method so that it moves as time changes.  After you update the
	//				coordinates you should repaint the panel.
	public void clock(){
		background_angle+=0.001;// Rotate the background
		
		GameController.update();
		
		//X direction movement
		LevelManager.getPlayer().setX_vel(GameController.xValue);
		
		//Y direction movement
		LevelManager.getPlayer().setY_vel(GameController.yValue);

		//Aiming
		LevelManager.getPlayer().setTargetAngle(GameController.angle);

		
		//Pausing
		if((LevelManager.getState() == State.PLAY  || LevelManager.getState() == State.PAUSED || LevelManager.getState() == State.COUNTDOWN) 
				&& GameController.pauseButton){
			LevelManager.flipPaused();
		}
		
		//Shooting
		LevelManager.getPlayer().setTry_shoot(GameController.shoot);

		LevelManager.tick(); // Run one iteration of the game
		this.repaint();		
	}
}
