//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
//-----------------------------------------------------------
public class Settings  implements Serializable
{
	private static final long serialVersionUID = 1L;
	//-------------------------------------------------------
	public int	 	level 		= -1;			// 游戏关卡
	public String 	skinName 	= new String();	// 皮肤
	public String 	soundName 	= new String();	// 背景音乐
	//-------------------------------------------------------
	public void load()	// 加载设置
	{
		try
		{
			FileInputStream fis = new FileInputStream("settings.pbs");
			ObjectInputStream ois = new ObjectInputStream(fis);

			try
			{
				Settings sett = (Settings)ois.readObject();
				
				this.level = sett.level;
				this.skinName = sett.skinName;
				this.soundName = sett.soundName;
			}
			catch(ClassNotFoundException e)
			{}
			ois.close();
		}
		catch(FileNotFoundException e){}
		catch(IOException e){}
	}
	//-------------------------------------------------------
	public void save()	// 保存设置
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("settings.pbs"));
			oos.writeObject(this);
			
			oos.flush();
			oos.close();
		}
		catch(IOException e)
		{
		}
	}
	//-------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------