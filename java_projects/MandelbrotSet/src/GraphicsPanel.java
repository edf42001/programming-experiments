// Graphics Panel
// Ethan Frank
// Date: February 6th, 2017
// This project extends the Jpanel class. In order to draw items on this panel you need use the Graphics2D's methods.

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class GraphicsPanel extends JPanel implements KeyListener, MouseListener {
	int width = 1430; //panel width, pixels
	int height = 800; //panel height, pixels
	
	double x = -0.5, y = 0.0;
	double zoom = 1; //amount to zoom in
	
	// method: GraphicsPanel Constructor
	// description: This 'method' runs when a new instance of this class in instantiated.  It sets default values  
	// that are necessary to run this project.  You do not need to edit this method.
	public GraphicsPanel(){
		setPreferredSize(new Dimension(width, height)); //Set window dimensions
		this.addKeyListener(this); 
		this.addMouseListener(this);
	}
	
	// method: paintComponent
	// description: This method is called when the Panel is painted.  It contains code that draws shapes onto the panel.
	// parameters: Graphics g - this object is used to draw shapes onto the JPanel.
	// return: void
	public void paintComponent(Graphics g){
		
		double[] c ={0,0}; //The constant c, depends on where point is
		double[] z = {0,0}; //z is the number that gets iterated z = z^2+c
		int maxIterations = (int) Math.min(Math.max(750+750*Math.log(zoom), 750),10000);
		int iteration = 0; //iterations of the pixel
		double p = 0; //for seeing if a point is in the cardioid
		
		Color color = new Color(0,0,0); //The point's color

		Graphics2D g2 = (Graphics2D) g;

			for(int i = 0; i < height; i++){ //y
				for(int j = 0; j<width; j++){//x
					
					//reset z
					z[0] = 0; 
					z[1] = 0;
					
					//Set c to the current point
					c[0] = (j-width/2)/(400.0*zoom) + x;
					c[1] = (i-height/2)/(400.0*zoom) - y;
					
					iteration = 0;
					//find iteration when pixel escapes
					p=Math.sqrt(Math.pow(c[0]-0.25, 2)+c[1]*c[1]);
					if(Math.pow(c[0]+1, 2) + c[1]*c[1] < 0.0625 ||
						c[0]<(p-2*p*p+0.25)){
						
						color=Color.BLACK;
					}else{
						while(magnitude(z) < 2 && iteration < maxIterations){
							z = addImaginary(multiplyImaginary(z,z), c);
							iteration++;
						}
					
						if(magnitude(z)<2){
							color = Color.BLACK;
						}else{
							color=interpolateColor(500-Math.abs(500-iteration/(maxIterations*0.001)));
							
							//color = interpolateColor(Math.abs((iteration-500)-Math.floor((iteration-500)/1000.0)*1000-500));
						}	
					}	
					
			        g2.setColor(color); //set the color
				    g2.drawRect(j, i, 1, 1); //draw the pixel
					
				}//end y for
			}//end x for
			g2.setColor(Color.GREEN.darker());
			g2.drawString(String.format("Zoom: + %.2E",zoom), 1300, 20);
	}
	//Method: multiplyImaginary
	//Takes two "imaginary" numbers (array of {r,i}) and multiplies them.
	public static double[] multiplyImaginary(double[] num1, double[] num2){
		double[] answer = {0,0};
		answer[0] = num1[0] * num2[0] - (num1[1] * num2[1]);
		answer[1] = num1[0] * num2[1] + num1[1] * num2[0];
		return answer;
	}
	
	//Method: addImaginary
	//Adds two imaginary numbers
	public static double[] addImaginary(double[] num1, double[] num2){
		double[] answer = {0,0};
		answer[0] = num1[0] + num2[0];
		answer[1] = num1[1] + num2[1];
		return answer;
	}
	
	//Method: magnitude
	//Finds the magnitude of the imaginary number
	public static double magnitude(double[] num){
		return Math.sqrt(Math.pow(num[0], 2) + Math.pow(num[1], 2));
	}
	
	//Method: interpolateColor
	//Takes in the value to interpolate to. 
	//The colors are stored in a 2D array of {{index,r,g,b},{index2,r,g,b},{index3,r,g,b}}
	public static Color interpolateColor(double value){
		double[][] colors = {{0,0,0,95},{8,0,0,95},{25,204,255,255},{50,255,170,51},{80,0,0,95},{250,175,180,255},{500,200,140,40}};
		
		double prop = 0;
		double[] color = {0,0,0};
		
		if(value<=colors[0][0]){
			color[0] = colors[0][1];
			color[1] = colors[0][2];
			color[2] = colors[0][3];
		}else if(value>=colors[colors.length-1][0]){
			color[0] = colors[colors.length-1][1];
			color[1] = colors[colors.length-1][2];
			color[2] = colors[colors.length-1][3];
		}
		else{
			for(int i = 0; i<=colors.length-1; i++){
				if(value >= colors[i][0] && value <=colors[i+1][0]){
					prop = (value-colors[i][0])/(colors[i+1][0]-colors[i][0]);
					for(int j = 1; j<=3; j++){
						color[j-1] = prop*(colors[i+1][j]-colors[i][j])+colors[i][j];
					}
					
				}
			}
		}
		
		return new Color((int) (color[0]), (int) (color[1]),(int) (color[2]));
	}

	@Override
	//Move center to where mouse is clicked
	public void mouseClicked(MouseEvent e) {
		x = (e.getX()-width/2)/(400.0*zoom) + x;
		y = -1*((e.getY()-height/2)/(400.0*zoom) - y);
		this.requestFocusInWindow(); //make keyboard work
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
	
	}

	@Override
	public void keyTyped(KeyEvent e) {
	
	}

	@Override
	//Zoom in and out, reset
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();

		if(key == '1'){
			zoom*=0.03125;
		}else if(key == '2'){
			zoom*=0.0625;
		}else if(key == '3'){
			zoom*=0.125;
		}else if(key == '4'){
			zoom*=0.25;
		}else if(key == '5'){
			zoom*=0.5;
		}else if(key == '6'){
			zoom*=2;
		}else if(key == '7'){
			zoom*=4;
		}else if(key == '8'){
			zoom*=8;
		}else if(key == '9'){
			zoom*=16;
		}else if(key == '0'){
			zoom*=32;
		}else if(key == '\\'){ //reset with "\" key
			zoom = 1;
			x= -0.5;
			y= 0;
		}else{
			
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

}






