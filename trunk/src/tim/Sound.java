//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;
//-----------------------------------------------------------
public class Sound
{
	//-------------------------------------------------------
	ContinuousAudioDataStream adStream = null;
	//-------------------------------------------------------
	// 循环播放
	//-------------------------------------------------------
	public boolean playL(String fileName)
	{
		stop();
		InputStream in = null;
		AudioStream as = null;
		try{
			in = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			return false;
		}
		
		try {
			as = new AudioStream(in);
		} catch (IOException e) {
			return false;
		}
		
		AudioData data;
		try {
			data = as.getData();
		} catch (IOException e) {
			return false;
		}
		adStream = new ContinuousAudioDataStream(data);
		AudioPlayer.player.start(adStream);

		return true;
	}
	//-----------------------------------------------------------
	// 单次播放
	//-----------------------------------------------------------
	public boolean play(String fileName)
	{
		InputStream in = null;
		AudioStream as = null;
		
		try {
			in = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			return false;
		}
		
		try {
			as = new AudioStream(in);
		} catch (IOException e) {
			return false;
		}

		AudioPlayer.player.start(as);
		
		return true;
	}
	//-----------------------------------------------------------
	public void stop()
	{
		if (adStream != null)
			AudioPlayer.player.stop(adStream);
	}
	//-----------------------------------------------------------
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
//END OF FILE
//-----------------------------------------------------------