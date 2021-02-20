import java.util.Random;

public class MyRand {
	private static Random myRand;

	public static void initialize(int seed){
		myRand = new Random(seed);
	}
	
	public static int randInt(int bound){
		return myRand.nextInt(bound);
	}
	
	public static float randFloat(){
		return myRand.nextFloat();
	}
	
	
}
