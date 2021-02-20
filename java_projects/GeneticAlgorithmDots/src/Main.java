import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame{

	public static void main(String[] args) {
		Main window = new Main();
	    JPanel p = new JPanel();
	    p.add(new GraphicsPanel()); 
	    window.setTitle("Genetic Algorithm With Dots");
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setContentPane(p);
	    window.pack();
	    window.setLocationRelativeTo(null);
	    window.setVisible(true);
	}

}
