import javax.swing.JFrame;
import javax.swing.JPanel;


public class GraphicsMain extends JFrame{ 
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws Exception{
		GraphicsMain window = new GraphicsMain();
	    JPanel p = new JPanel();
	    p.add(new GraphicsPanel()); 
	    window.setTitle("Cow Vs. Aliens");
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setContentPane(p);
	    window.pack();
	    window.setLocationRelativeTo(null);
	    window.setVisible(true);
	}
}
