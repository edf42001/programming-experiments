import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Class: State
// Written by: Ethan Frank
// Date: May 21, 2018
// Description: Describes a state of the game. Some states have buttons
public enum State {
	PLAY, COUNTDOWN, 
	MAIN(new Button[] {
			new Button(30, 630, 320, 80, "INSTRUCTIONS", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.INSTRUCTIONS);
					if(MAIN.buttons[2].getText().equals("CANCEL")){MAIN.buttons[2].onClick();} //Turn off options to clear/cancel when leaving MAIN
				}
				
			}),

			new Button(400, 630, 320, 80, "LEVEL SELECT", new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.LEVELSELECT);
					if(MAIN.buttons[2].getText().equals("CANCEL")){MAIN.buttons[2].onClick();} //Turn off options to clear/cancel when leaving MAIN
				}
			}),
			
			new Button(215, 520, 320, 80, "CLEAR DATA", new ActionListener(){
				boolean areYouSuring = false;
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!areYouSuring){
						areYouSuring = true;
						MAIN.buttons[2].setText("CANCEL");
						MAIN.buttons[3] = new Button(563, 520, 160, 80, "CLEAR", new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								MAIN.buttons[2].setText("CLEAR DATA");
								MAIN.buttons[3] = new Button(0,0,0,0,"", null);
								areYouSuring = false;
								try {
									Files.write(Paths.get("cookies.txt"), new byte[]{0});
									LevelManager.completeLevels(0);
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}	
						});
					}else{
						MAIN.buttons[2].setText("CLEAR DATA");
						MAIN.buttons[3] = new Button(0,0,0,0,"", null);
						areYouSuring = false;
					}
				}
			}),
			new Button(0,0,0,0,"", null),
			new Button(0,0,0,0,"", null),
		}
	), 
	
	LEVELSELECT(new Button[] {
			new Button(30, 630, 320, 80, "MAIN MENU", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.MAIN);
				}
			})
		}, "LEVELSELECT"),
	
	INSTRUCTIONS(new Button[] {
			new Button(30, 630, 320, 80, "MAIN MENU", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.MAIN);
				}
			})
		}),
	
	PAUSED(new Button[] {
			new Button(30, 630, 320, 80, "MAIN MENU", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.MAIN);
				}
			}),
			new Button(400, 630, 320, 80, "LEVEL SELECT", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.LEVELSELECT);
				}
			}),
			new Button(215, 520, 320, 80, "RESTART", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.restartLevel();
				}
			})
		}
	), 
	
	LEVELCOMPLETE(new Button[] {
			new Button(30, 630, 320, 80, "MAIN MENU", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.MAIN);
				}
			}),
			new Button(400, 630, 320, 80, "NEXT", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int level = LevelManager.getLevel()+1;
					if(level>=LevelManager.getLevels().length){
						LevelManager.setState(State.LEVELSELECT);
					}else{
						LevelManager.initializeLevel(level);
					}
				}
			}),
			new Button(215, 520, 320, 84, "LEVEL SELECT", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.LEVELSELECT);
				}
			})
		}
	),
	
	GAMEOVER(new Button[] {
			new Button(30, 630, 320, 80, "MAIN MENU", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.MAIN);
				}
			}),
			new Button(400, 630, 320, 80, "TRY AGAIN", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.restartLevel();
				}
			}),
			new Button(215, 520, 320, 84, "LEVEL SELECT", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					LevelManager.setState(State.LEVELSELECT);
				}
			})
		}
	);
	
	
	//Instance variables
	private Button[] buttons;
	
	//Constructors
	private State(Button[] buttons, String modifier){
		this.buttons = buttons;
		
		if(modifier.equals("LEVELSELECT")){ // Add a button for each level in a grid shape
			int numLevels = LevelManager.getLevels().length;
			Button[] allButtons = new Button[numLevels+buttons.length];
			for(int i = 0; i<numLevels; i++){
				final int level = i;
				allButtons[i] = new Button(90+120*(i%5), 100+120*(i/5), 90, 90, ""+(i+1), new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						LevelManager.initializeLevel(level);
					}
				}, "LEVELSELECT");
			}
			
			//copy buttons into array with currently only level buttons
			for(int i = numLevels; i<allButtons.length; i++){
				allButtons[i] = buttons[i-numLevels];
			}
			
			this.buttons = allButtons;
		}
	
	}
	
	private State(Button[] buttons){
		this(buttons, "");
	}
	
	private State(){
		this(new Button[0]);
	}
	
	//Methods
	
	// Method: updateButtons
	// Description: Checks all buttons and updates them
	// Params: int x: x position of mouse, int y: y position of mouse, boolean clicked: if the mouse clicks
	// Returns: void
	public void updateButtons(int x, int y, boolean clicked){
		for(Button b : buttons){
			b.update(x, y, clicked);
		}
	}
	
	// Method: drawButtons
	// Description:  Draws all the buttons
	// Params: Graphics2D g2: The graphics
	// Returns: void
	public void drawButtons(Graphics2D g2){
		for(Button b : buttons){
			b.draw(g2);
		}
	}
	
	// Method: shouldAllowControllerToMoveMouse
	// Description: Says whether in a certain state the controller should be allowed to control the mouse
	// Params: none
	// Returns: boolean: if the controller should be allowed to move the mouse
	public boolean shouldAllowControllerToMoveMouse(){
		return buttons.length > 0; //If there are buttons to push then yes can move. 
	}
	
	
}
