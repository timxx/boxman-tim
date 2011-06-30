//---------------------------------------------------------------
package tim;
//---------------------------------------------------------------
import java.io.Serializable;
//---------------------------------------------------------------
public class MapSerializable  implements Serializable
{
	//-----------------------------------------------------------
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	public String	ver;		//版本
	public String	author;		//作者
	public String	name;		//地图名称
	public char		data[][];	//数据
	public short	bomb;		//炸弹数
	//-----------------------------------------------------------
	MapSerializable()
	{
		ver 	= "1.0";
		author 	= "Unknown";
		name	= "";
		bomb 	= 0;
		data 	= new char[MapInfo.ROW_NUM][MapInfo.COL_NUM];
	}
	//-----------------------------------------------------------
	MapSerializable(MapInfo mi)
	{
		data = new char[MapInfo.ROW_NUM][MapInfo.COL_NUM];
		
		setMapInfo(mi);
	}
	//-----------------------------------------------------------
	public MapInfo getMapInfo()
	{
		MapInfo mi = new MapInfo();
		
		mi.ver = ver;
		mi.author = author;
		mi.name = name;
		mi.bomb = bomb;
		mi.data = data;
		
		return mi;
	}
	//-----------------------------------------------------------
	public void setMapInfo(MapInfo mi)
	{
		data = mi.data;
		ver = mi.ver;
		author = mi.author;
		name = mi.name;
		bomb = mi.bomb;
	}
	//-----------------------------------------------------------
}
//---------------------------------------------------------------
//	END OF FILE
//---------------------------------------------------------------