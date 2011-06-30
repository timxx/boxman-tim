//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.io.File;
import java.util.ArrayList;
//-----------------------------------------------------------
public class SoundMgr
{
	//-----------------------------------------------------------
	private ArrayList<String> soundList = new ArrayList<String>();
	//-----------------------------------------------------------
	public ArrayList<String> getSoundList()
	{
		return soundList;
	}
	//-----------------------------------------------------------
	// 获取声音列表
	//-----------------------------------------------------------
	public void load()
	{
		File file = new File("Sound");

		File[] fileList = file.listFiles();
		if (fileList == null)
			return ;

		for(int i=0; i<fileList.length; i++)
		{
			if(fileList[i].isFile())
				soundList.add(fileList[i].getName());
		}
	}
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
//END OF FILE
//-----------------------------------------------------------