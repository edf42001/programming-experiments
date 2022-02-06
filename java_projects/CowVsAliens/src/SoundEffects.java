import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

import src.main.java.com.adonax.audiocue.*;

// Enum: SoundEffects
// Written by: Ethan Frank
// Date: Nov 16, 2017
// Description: Encapsulates the different sound effects in the game and methods for playing them.
public enum SoundEffects{ 
	SHOT("sounds/shot.wav", 0.1),
	HURT("sounds/ooh.wav", 0.5),
	EXPLODE("sounds/explosion.wav", 0.2),
	DEATH("sounds/death.wav", 0.5),
	WIN("sounds/win.wav", 0.5);
	
	
	private AudioCue myAudioCue;	// Each sound effect gets it's own clip
	private int handle;
	
	//Constructor
	SoundEffects(String soundFileName, double volume){
		URL url = this.getClass().getResource(soundFileName);
		try {
			myAudioCue = AudioCue.makeStereoCue(url, 1);
			myAudioCue.open();  // see API for parameters to override "sound thread" configuration default
			handle = myAudioCue.obtainInstance();
			myAudioCue.setVolume(handle, volume);
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //allows 4 concurrent
 catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Method: play
	// Description: Plays a sound effect. Restarts if sound effect already playing
	// Params: none
	// Returns: void
	public void play(){
		
		if(myAudioCue.getIsPlaying(handle)){
			myAudioCue.stop(handle);
		}
			myAudioCue.setFramePosition(handle, 0);
			myAudioCue.start(handle);
	}
}
