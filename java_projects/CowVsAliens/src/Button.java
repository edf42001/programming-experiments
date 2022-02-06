import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Class: Button
// Written by: Ethan Frank
// Date: May 19, 2018
// Description: This is a button you click on it and it does things. 

public class Button{
	private int x, y, width, height;
	private String text;
	private boolean hovered;
	private ActionListener listener;
	private String modifier;
	
	
	/**
	 * @param x of top left
	 * @param y of top left
	 * @param width the width
	 * @param height the height
	 * @param text text to display
	 */
	public Button(int x, int y, int width, int height, String text, ActionListener listener){
		this(x, y, width, height, text, listener, "");
	}
	
	public Button(int x, int y, int width, int height, String text, ActionListener listener, String modifier){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.listener = listener;
		this.modifier = modifier;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	// Method: draw
	// Description: Draws an orange button with rounded corners. Gets brighter when hovered over
	// Params: Graphcis2D g2: The graphics
	// Returns: void
	public void draw(Graphics2D g2){
		//Color determining logic
		Color c = new Color(255, 153, 0);
		int level = -1;
		if(modifier.equals("LEVELSELECT")){
			level = Integer.parseInt(text)-1;
			if(!LevelManager.getLevels()[level].unlocked()){
				c = c.darker().darker();
			}else if(LevelManager.getLevels()[level].completed()){
				c = new Color(114, 205, 60);
			}
			
			if(hovered && LevelManager.getLevels()[level].unlocked()){
				c = c.brighter();
			}
		}else{
			if(hovered){
				c = c.brighter();
			}
		}
		
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 15, 15);
		g2.setColor(c.darker());
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(x+6, y+6, width-13, height-13, 13, 13);
		LevelManager.drawCenteredText(g2, text, y+height/2+11, 40, x+width/2, Color.BLACK);	
	}
	
	// Method: update
	// Description: Lightens the button if mouse is hovering over it and calls the action listener if it is clicked
	// Params: int x: x coord to check. int y: y coord to check, boolean clicked: did the mouse click
	// Returns: void
	public void update(int x, int y, boolean clicked){
		if(inButton(x,y)){
			hovered = true;
			if(clicked && (modifier.equals("LEVELSELECT") && LevelManager.getLevels()[Integer.parseInt(text)-1].unlocked())
					|| (clicked && !modifier.equals("LEVELSELECT"))){
				onClick();
			}
		}else{
			hovered = false;
		}
	}
	
	// Method: inButton
	// Description: Returns true when the coordinates are inside the button
	// Params: int x: x coord to check. int y: y coord to check
	// Returns: boolean: coords are in the button
	private boolean inButton(int x, int y){
		return x>this.x && x<this.x+this.width && y>this.y && y<this.y+this.height;
	}

	// Method: onClick
	// Description: Runs the code that should happen when the button is clicked
	// Params: none
	// Returns: void
	public void onClick() {
		listener.actionPerformed(null);
	}
	
}
