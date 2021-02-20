import javax.swing.JFrame;
import javax.swing.JPanel;

public class Connect4Main extends JFrame{
	
	public static void main(String[] args) {
		Connect4Main window = new Connect4Main();
	    JPanel p = new JPanel();
	    p.add(new ConnectFourPanel());  //add a class that extends JPanel

	    window.setTitle("Connect 4");
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    window.setContentPane(p);
	    
	    window.pack();
	    window.setLocationRelativeTo(null);
	    window.setVisible(true);
	}
}