import java.util.function.ToDoubleFunction;

// Class: LevelData
// Written by: Ethan Frank
// Date: Nov 17, 2017
// Description: This class holds the configuration data for a level

public class LevelData {
	//Instance Fields
	private double[] spawn_chances;			// The spawn chance for each type of enemy, in avaerage seconds until spawn
	private String[] name;					// The name of the level, to be displayed at start. Each element is displayed on a new line
	private int[] killGoals;				// Array of how many enemies need to be killed to pass level.
	private boolean unlocked, completed; 	// Booleans describing the state of the level
	
	/**
	 * Creates a new {@code LevelData} object. Converts spawn_chances from seconds to probability of spawning per iteration
	 * @param spawn_chances - Array of spawn chances, in seconds, for each enemy. If and element is 0, no enemies of that kind will spawn 
	 * @param name - Level name, displayed during start of level. Each element of this array is displayed on a new line
	 * @param killGoals - Array of how many enemies need to be killed to pass this level
	 */
	public LevelData(double[] spawn_chances, String[] name, int[] killGoals) {
		this.spawn_chances = spawn_chances;
		this.name = name;
		this.killGoals = killGoals;
		
		//Convert spawn chances from average seconds between spawns to a probability of spawning per iteration of the game.
		// If spawn chance is 0 seconds, there is no chance of spawning
		for(int i = 0; i<spawn_chances.length; i++){
			if(spawn_chances[i]<=0){
				spawn_chances[i] = 0; //no chance at spawning
			}else{
				spawn_chances[i]=1.0/(40*spawn_chances[i]); //convert
			}
		}
	}
	
	//Accessors
	public double[] getSpawn_chances() {
		return spawn_chances;
	}
	
	public String[] getName(){
		return name;
	}
	
	public int[] getKillGoals(){
		return killGoals;
	}
	
	public boolean unlocked(){
		return unlocked;
	}
	
	public boolean completed(){
		return completed;
	}

	//Modifiers
	public void setSpawn_chances(double[] spawn_chances) {
		this.spawn_chances = spawn_chances;
	}
	
	public void setUnlocked(boolean unlocked){
		this.unlocked = unlocked;
	}
	
	public void setCompleted(boolean completed){
		this.completed = completed;
	}
	
	

}
