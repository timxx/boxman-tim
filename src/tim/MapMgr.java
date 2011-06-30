//---------------------------------------------------------------
package tim;
//---------------------------------------------------------------
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;
//---------------------------------------------------------------
public class MapMgr
{
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	private ArrayList<MapInfo> mapList = new ArrayList<MapInfo>();
	//-----------------------------------------------------------
	public ArrayList<MapInfo> getMapList()
	{
		return mapList;
	}
	//-----------------------------------------------------------
	// 加载Map目录下所有有效地图（不包括子目录）
	//-----------------------------------------------------------
	public void load()
	{
		File file = new File("Map");

		File[] fileList = file.listFiles();
		if (fileList == null)	// 防止不存在Map目录时出错
			return ;

		for(int i=0; i<fileList.length; i++)
		{
			if(fileList[i].isFile())
				load(fileList[i].getPath(), false);
		}
	}
	//-----------------------------------------------------------
	// 加载地图文件
	//-----------------------------------------------------------
	public void load(String mapFile, boolean showErr)
	{
		try
		{
			FileInputStream fis = new FileInputStream(mapFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			try
			{
				while(true)
				{
					MapSerializable ms = (MapSerializable)ois.readObject();
					if (ms == null)
						break;

					mapList.add(ms.getMapInfo());
				}
			}
			catch(ClassNotFoundException e)
			{
				if (showErr)
					JOptionPane.showMessageDialog(null, "不是有效的地图文件，加载地图失败！",
							"不能加载地图", JOptionPane.ERROR_MESSAGE);
			}
			catch(IOException e)
			{

			}

			ois.close();
		}
		catch(FileNotFoundException e)
		{
			if (showErr)
				JOptionPane.showMessageDialog(null, "未找到指定文件，请检查输入是否有误！",
						"不能加载地图", JOptionPane.ERROR_MESSAGE);
		}
		catch(IOException e)
		{
			if (showErr)
				JOptionPane.showMessageDialog(null, "无法打开文件，加载地图失败！",
						"不能加载地图", JOptionPane.ERROR_MESSAGE);
		}
	}
	//-----------------------------------------------------------
	public void save(MapSerializable ms, String filePath)	// 保存地图到文件
	{
		ObjectOutputStream oos = null;

		try
		{
			oos = new ObjectOutputStream(new FileOutputStream(filePath));
			oos.writeObject(ms);
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "无法保存地图到文件！",
					"出错了", JOptionPane.ERROR_MESSAGE);
		}

		try
		{
			if (oos != null)
			{
				oos.flush();
				oos.close();
			}
		}
		catch(IOException e)
		{
		}
	}
	//-----------------------------------------------------------
}
//---------------------------------------------------------------
//END OF FILE
//---------------------------------------------------------------