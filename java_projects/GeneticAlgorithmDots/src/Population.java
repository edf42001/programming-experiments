import java.awt.Color;
import java.awt.Graphics2D;

public class Population {
	Dot[] dots;
	double fitnessSum; 
	int gen;
	Dot bestDot;
	int bestSteps = 400;
	Obstacle[] obstacles;
	
	Population(int size){
		dots = new Dot[size];
		for(int i = 0; i<dots.length; i++){
			dots[i] = new Dot();
		}
		obstacles = new Obstacle[] {new Obstacle(0, 450, 380, 10),
									new Obstacle(400,450,400,10)};
	}
	
	//-------------------------------------------------------------------------------------
	
	void show(Graphics2D g2){
		for(Obstacle o : obstacles){
			o.show(g2);
		}
		for(Dot d : dots){
			d.show(g2);
		}
	}
	
	//-------------------------------------------------------------------------------------

	void update(){
		for(Dot d : dots){
			if(d.brain.step>bestSteps){
				d.dead = true;
			}else{
				for(Obstacle o : obstacles){
					if(o.contains(d)){
						d.dead = true;
					}
				}
				d.update();
			}

		}
	}
	
	//-------------------------------------------------------------------------------------
	
	void calculateFitness(){
		for(Dot d : dots){
			d.calculateFitness();
		}
	}
	
	//-------------------------------------------------------------------------------------

	public boolean allDead(){
		for(Dot d : dots){
			if(!d.dead){
				return false;
			}
		}
		return true;
	}
	
	//-------------------------------------------------------------------------------------
	public void naturalSelection(){
		Dot[] newDots = new Dot[dots.length];
		setBestDot();
		calculateFitnessSum();
		
		for(int i = 0; i<newDots.length-1; i++){
			//get parent and generate baby
			Dot parent = selectParent();
			newDots[i] = parent.getBaby();
		}
		newDots[newDots.length-1] = bestDot.getBaby();
		newDots[newDots.length-1].isBest = true;
		dots = newDots;
		gen++;
	}
	
	//-------------------------------------------------------------------------------------

	
	void calculateFitnessSum(){
		fitnessSum = 0;
		for(Dot d : dots){
			fitnessSum+=d.fitness;
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	public Dot selectParent(){
		double rand = MyRand.randFloat()*fitnessSum;
		double runningSum = 0;
		for(Dot d : dots){
			runningSum+=d.fitness;
			if(runningSum>=rand){
				return d;
			}
		}
		//Should never get to this point
		return null;
	}
	
	//-------------------------------------------------------------------------------------
	
	public void mutateBabies(){
		for(int i = 0; i<dots.length-1; i++){//Don't mutate last because last is best dot
			dots[i].mutate();
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	public void setBestDot(){
		Dot max = dots[0];
		
		for(int i = 1; i<dots.length; i++){
			if(dots[i].fitness>max.fitness){
				max = dots[i];
			}
		}
		bestDot = max;
		if(bestDot.reachedGoal){
			bestSteps = bestDot.brain.step;
		}
		
		System.out.println("Steps: " + bestSteps);
	}
}
