// Class: GraphicsPanel
// Written by: Mr. Swope
// Date: 10/28/15
// Description: This class is the main class for this project.  It extends the Jpanel class and will be drawn on
// 				on the JPanel in the GraphicsMain class.  Your project should have at least one character that moves
//				with the arrow keys and one character that moves with the clock.  Finally, you should detect if the
//				two items intersect and have something happen if they do intersect.
//
// Since you will modify this class you should add comments that describe when and how you modified the class.  

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class GraphicsPanel extends JPanel{
	private Timer t;							 // The time is used to move objects at a consistent time interval.
	final static int width = 800;
	final static int height = 800;
	public static PVector goal = new PVector(50,50);
	Population pop;

	public GraphicsPanel(){
		setPreferredSize(new Dimension(width, height));
		MyRand.initialize(76);
		pop = new Population(1000);
		t = new Timer(1, new ClockListener(this));  
		t.start();
	}
	// method: paintComponent
	// description: This method will paint the items onto the graphics panel.  This method is called when the panel is
	//   			first rendered.  It can also be called by this.repaint()
	// parameters: Graphics g - This object is used to draw your images onto the graphics panel.
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.GREEN);
		g2.fillOval((int) goal.x-5, (int) goal.y-5, 10, 10);
		pop.show(g2);
		
	}
	
	// method:clock
	// description: This method is called by the clocklistener every 5 milliseconds.
	public void clock(){
		pop.update();
		if(pop.allDead()){
			//genetic algorithm here
			pop.calculateFitness();
			pop.naturalSelection();
			pop.mutateBabies();
		}
		repaint();
	}

}
