// GraphicsMain.java
// Written by: Mr. Swope
// Date: February 10th, 2015
// This is the main class for a simple java program that uses graphics.  You do not need to edit this class.

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GraphicsMain extends JFrame{
	
		public static void main(String[] args)
		{	
			GraphicsMain window = new GraphicsMain();
	        JPanel p = new JPanel();
	        p.add(new GraphicsPanel());  //  add a class that extends JPanel
	       
	        window.setTitle("Ethan's Graphics Program");
	        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        window.setContentPane(p);
	        
	        window.pack();
	        window.setLocationRelativeTo(null);
	        window.setVisible(true);
	        
		}

}

