//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.io.File;
import java.util.ArrayList;
//-----------------------------------------------------------
public class SkinMgr
{
	//-------------------------------------------------------
	// 规定图片名称只能是这些
	//-------------------------------------------------------
	public final static String[] IMG_NAMES = new String[]
	{
		"back.png",
		"wall.png",
		"road.png",
		"box1.png",
		"box2.png",
		"target.png",
		"man_up.png",
		"man_down.png",
		"man_left.png",
		"man_right.png",
		"bomb.png"
	};
	//-------------------------------------------------------
	private ArrayList<String> skinList = new ArrayList<String>();
	//-------------------------------------------------------
	public ArrayList<String> getSkinList()
	{
		return skinList;
	}
	//-------------------------------------------------------
	// 获取皮肤列表
	//-------------------------------------------------------
	public void load()
	{
		File file = new File("Skin");

		File[] fileList = file.listFiles();
		if (fileList == null)
			return ;

		for(int i=0; i<fileList.length; i++)
		{
			if(fileList[i].isDirectory())
				if (checkSkinOk(fileList[i].getPath()))
					skinList.add(fileList[i].getName());
		}
	}
	//-------------------------------------------------------
	// 验证皮肤的完整性
	//-------------------------------------------------------
	public static boolean checkSkinOk(String folder)
	{
		for(int i=0; i<IMG_NAMES.length; i++)
			if (!(new File(folder + "/" + IMG_NAMES[i]).exists()))
				return false;
		
		return true;
	}
	//-------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------