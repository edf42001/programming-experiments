import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GameController {
	private static ControllerEnvironment ce;
	private static Controller controller1, controller2, controller3; //First controller is either Controller or Keyboard. Second is either null or mouse
	private static boolean useController;
	public static boolean areControllersWorking; // If having issues with library, default to manual keyboard methods
	private static GraphicsPanel panel;					// Used to get mouse position
	
	//public buttons and such
	public static boolean pauseButton;
	private static boolean currentPauseButton, lastPauseButton;
	public static float xValue;
	public static float yValue;
	public static double angle;
	public static boolean click;
	private static boolean currentClick, lastClick;
	public static boolean shoot;
	public static int mouseX;
	public static int mouseY;

	// Variables for only if controllers library isn't working
	public static boolean mousePressed;
	public static HashMap<java.lang.Character, Integer> keyMap;
	
	public static void init(GraphicsPanel p) throws Exception{
		panel = p;
		angle = 90; //Initial angle

		ce = ControllerEnvironment.getDefaultEnvironment();
		int indexOfController = indexOfController("Wireless Controller");
		if(indexOfController>=0){
			useController = true;
			controller3 = ce.getControllers()[indexOfController];
		}

		// Check if controllers are working at all:
		if (ce.getControllers().length == 0){
			System.out.println("No controllers could be found, defaulting to normal keyboard interface");
			areControllersWorking = false;

			// Create keymap (WASD and Pause)
			keyMap = new HashMap<>();
			keyMap.put('w', 0);
			keyMap.put('a', 0);
			keyMap.put('s', 0);
			keyMap.put('d', 0);
			keyMap.put('p', 0);

			// Setup listeners to respond to mouse/keyboard events
			panel.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {
					mousePressed = true;
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					mousePressed = false;
				}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
			});

			panel.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyPressed(KeyEvent e) {
					if (keyMap.containsKey(e.getKeyChar())) {
						keyMap.put(e.getKeyChar(), 1);
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if (keyMap.containsKey(e.getKeyChar())) {
						keyMap.put(e.getKeyChar(), 0);
					}				}
			});

			// Set focusable so keys work
			panel.setFocusable(true);
		} else {
			areControllersWorking = true;
		}
	}
	
	public static void update(){
		//Update mouse position
		Point mouse = panel.getMousePosition();

		if(mouse!=null){
			mouseX = mouse.x;
			mouseY = mouse.y;
		}
		Component[] components = null;
		if(useController && !controller3.poll()){
			useController = false;
			LevelManager.setState(State.PAUSED);
		}
		if(useController){
			components = controller3.getComponents();
			float[] values = new float[components.length]; 
			for(int i = 0; i<components.length; i++){
				values[i] = components[i].getPollData();
			}
			
			if(Math.abs(values[14])>0.08){
				xValue = values[14]*10;
			}else{
				xValue = 0;
			}
			
			if(Math.abs(values[15])>0.08){
				yValue = values[15]*10;
			}else{
				yValue = 0;
			}
			
			if(Math.abs(values[16])>0.15 || Math.abs(values[17])>0.15){
				angle = (Math.toDegrees(Math.atan2(values[17], values[16])));
			}//else angle stays the same
			
			currentClick = values[1] == 1;
			click = currentClick && ! lastClick;
			lastClick = currentClick;
			
			currentPauseButton = values[0] == 1;
			pauseButton = currentPauseButton && !lastPauseButton;
			lastPauseButton = currentPauseButton;
			
			
			shoot = values[20]>-0.8;
			
		}else if (areControllersWorking){ //keyboard and mouse (with controller library)
			ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
			Controller[] controllers = ce.getControllers();
			controller1 = controllers[0];
			controller2 = controllers[1];
			
			controller1.poll();
			controller2.poll();
			components = controller1.getComponents();
			float[] values = new float[components.length]; 
			for(int i = 0; i<components.length; i++){
				values[i] = components[i].getPollData();
			}
			
			if(values[11]==1){
				xValue = -10;
			}else if (values[14]==1){
				xValue = 10;
			}else{
				xValue = 0;
			}
			
			if(values[33]==1){
				yValue = -10;
			}else if (values[29]==1){
				yValue = 10;
			}else{
				yValue = 0;
			}
			if(LevelManager.getPlayer()!=null){
				angle = -Math.toDegrees(Math.atan2(-(mouseY-LevelManager.getPlayer().getY_pos()),
														mouseX-LevelManager.getPlayer().getX_pos()));
			}
			
			currentClick = controller2.getComponents()[0].getPollData()==1;
			click = currentClick && ! lastClick;
			lastClick = currentClick;
			
			currentPauseButton = values[26]==1;
			pauseButton = currentPauseButton && !lastPauseButton;
			lastPauseButton = currentPauseButton;
			
			shoot = controller2.getComponents()[0].getPollData()==1;
		} else {
			// Controllers don't work, use native keyboard interface
			// Most of this is just directly copied from above. I want it to work on linux, it doesn't have
			// to be too clean right now
			if(keyMap.get('a')==1){
				xValue = -10;
			}else if (keyMap.get('d')==1){
				xValue = 10;
			}else{
				xValue = 0;
			}

			if(keyMap.get('w')==1){
				yValue = -10;
			}else if (keyMap.get('s')==1){
				yValue = 10;
			}else{
				yValue = 0;
			}

			if(LevelManager.getPlayer()!=null) {
				angle = -Math.toDegrees(Math.atan2(-(mouseY - LevelManager.getPlayer().getY_pos()),
						mouseX - LevelManager.getPlayer().getX_pos()));
			}

			currentClick = mousePressed;
			click = currentClick && ! lastClick;
			lastClick = currentClick;

			// I think shoot is when held, but click is only upon first mouse press.
			// This makes sense, but this was like 4 years ago
			shoot = mousePressed;

			currentPauseButton = keyMap.get('p')==1;
			pauseButton = currentPauseButton && !lastPauseButton;
			lastPauseButton = currentPauseButton;
		}
	}
	
	// Method: usingController
	// Description: Returns true if using a controller
	// Params: none
	// Returns: boolean: using a controller
	public static boolean usingController(){
		return useController;
	}
	
	// Method: indexOfController
	// Description: Finds the index of a controller with certain name
	// Params: String controller: name of the controller
	// Returns: int: index of the controller. -1 if not found
	private static int indexOfController(String controller){
		Controller[] controllers = ce.getControllers();
		for(int i = 0; i<controllers.length; i++){
			if(controllers[i].getName().equals(controller)){
				return i;
			}
		}
		return -1;
	}
}
