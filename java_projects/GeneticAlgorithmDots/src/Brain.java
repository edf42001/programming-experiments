
public class Brain {
	PVector[] directions;
	int step = 0;
	public double mutatability = 0.02;
	
	Brain(int size){
		directions = new PVector[size];
		randomize();
	}
	
	//-------------------------------------------------------------------------------------
	
	void randomize(){
		for(int i = 0; i<directions.length; i++){
			double angle = MyRand.randFloat()*Math.PI*2;
			directions[i] = new PVector(Math.cos(angle), Math.sin(angle));
		}
	}
	
	//-------------------------------------------------------------------------------------

	public Brain clone(){
		Brain clone = new Brain(directions.length);
		for(int i = 0; i<directions.length; i++){
			clone.directions[i] = directions[i].clone();
		}
		clone.mutatability = mutatability;
		
		return clone;
	}
	
	//-------------------------------------------------------------------------------------

	public int mutate(){
		int mutations = 0;
		for(int i = 0; i<directions.length; i++){
			double rand = MyRand.randFloat();
			if(rand<mutatability){
				mutations++;
				double angle = Math.atan2(directions[i].y, directions[i].x);
				angle+=2*(MyRand.randFloat()-0.5);
				directions[i] = new PVector(Math.cos(angle), Math.sin(angle));
			}
		}
		double mr = 0.1;
		if(MyRand.randFloat()<mr){
			mutatability *= (MyRand.randFloat()*0.7+0.65);
		}
		
		return mutations;
		
	}
}
