import java.awt.Color;
import java.awt.Graphics2D;

public class Dot {
	public PVector pos, vel, accel;
	public Brain brain;
	
	public boolean dead = false;
	public boolean reachedGoal = false;
	public boolean isBest = false;
	
	public double fitness = 0;
	
	public float color;
	
	Dot(){
		pos = new PVector(GraphicsPanel.width-200, GraphicsPanel.height-20);
		vel = new PVector(0,0);
		accel = new PVector(0,0);
		brain = new Brain(400);
		color = MyRand.randFloat();
	}

//-------------------------------------------------------------------------------------

	public void show(Graphics2D g2){
		if(isBest){
			g2.setColor(Color.BLACK);
			g2.fillOval((int) pos.x-4, (int) pos.y-4, 8, 8);
		}else{
			g2.setColor(Color.getHSBColor(color, 1f, 0.8f));
			g2.fillOval((int) pos.x-2, (int) pos.y-2, 4, 4);
		}
		
	
	}

//-------------------------------------------------------------------------------------

	public void move(){
		if(brain.step < brain.directions.length){
			accel = brain.directions[brain.step];
			brain.step++;
		}else{
			dead = true;
		}
		
		vel.add(accel);
		vel.x = Math.max(-5, Math.min(5, vel.x));
		vel.y = Math.max(-5, Math.min(5, vel.y));
		pos.add(vel);
	}
	
//-------------------------------------------------------------------------------------

	public void update(){
		if(!dead && !reachedGoal){
			move();
			if(PVector.dist(pos, GraphicsPanel.goal)<5){
				reachedGoal = true;
				dead = true;
			}
		}
	
		if(pos.x < 4 || pos.x > GraphicsPanel.width-4 || pos.y<4 || pos.y>GraphicsPanel.height-4){
			dead = true;
		}
	}
	
//-------------------------------------------------------------------------------------

	void calculateFitness(){
		if(reachedGoal){
			fitness = 1/16.0 + 1000/((double) brain.step*brain.step);
		}else{
			double distanceToGoal = PVector.dist(pos, GraphicsPanel.goal);
			fitness = 1/(distanceToGoal*distanceToGoal);
		}
		
	}
	
	//-------------------------------------------------------------------------------------

	public Dot getBaby(){
		Dot baby = new Dot();
		baby.brain = brain.clone();
		baby.color = color;
		return baby;
	}
	
	//-------------------------------------------------------------------------------------
	
	public void mutate(){
		int mutations = brain.mutate();
		color += Math.signum(MyRand.randFloat()-0.5)*mutations/300.0;
	}
	
	
}



