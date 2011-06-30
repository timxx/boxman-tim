//-----------------------------------------------------------
package tim;

import java.awt.Point;

//-----------------------------------------------------------
public class MapInfo
{
	//-----------------------------------------------------------
	public String	ver;		//版本
	public String	author;		//作者
	public String	name;		//地图名称
	public char		data[][];	//数据
	public short	bomb;		//炸弹数
	//-----------------------------------------------------------
	public final static short ROW_NUM = 12;		//地图宽、高
	public final static short COL_NUM = 14;
	
	public final static short IMG_WIDTH 	= 30;	//图片宽、高
	public final static short IMG_HEIGHT	= 30;
	
	public final static char TYPE_BKGND 	= 0;	//背景
	public final static char TYPE_WALL 		= 1;	//墙
	public final static char TYPE_ROAD 		= 2;	//道路
	public final static char TYPE_BOX_NRM 	= 3;	//正常状态的箱子
	public final static char TYPE_BOX_FIS 	= 4;	//已推入目的的箱子
	public final static char TYPE_DEST 		= 5;	//目的地
	public final static char TYPE_MAN_U 	= 6;	//人物-四个方向
	public final static char TYPE_MAN_D 	= 7;
	public final static char TYPE_MAN_L 	= 8;
	public final static char TYPE_MAN_R 	= 9;
	public final static char TYPE_BOMB 		= 10;	//炸弹
	//-----------------------------------------------------------
	MapInfo()
	{
		ver 	= "1.0";
		author 	= "Unknown";
		name	= "";
		data	= new char[ROW_NUM][COL_NUM];
		
		for(int i=0; i<ROW_NUM; i++)
			for(int j=0; j<COL_NUM; j++)
				data[i][j] = TYPE_BKGND;
	}
	//-----------------------------------------------------------
	MapInfo(MapInfo mi)
	{
		setMapInfo(mi);
	}
	//-----------------------------------------------------------
	public void setMapInfo(MapInfo mi)
	{
		ver = mi.ver;
		author = mi.author;
		name = mi.name;
		bomb = mi.bomb;
		
		for(int i=0; i<ROW_NUM; i++)
			for(int j=0; j<COL_NUM; j++)
				data[i][j] = mi.data[i][j];
	}
	//-----------------------------------------------------------
	public Point getManPos()	// 获取人物的位置
	{
		Point pos = new Point(0, 0);
		
		for(int i=0; i<ROW_NUM; i++)
		{
			for(int j=0; j<COL_NUM; j++)
			{
				if (data[i][j] >=  TYPE_MAN_U && data[i][j] <= TYPE_MAN_R)
				{
					pos.x = i;
					pos.y = j;
					
					return pos;
				}
			}
		}
		
		return pos;
	}
	//-----------------------------------------------------------
	public Point getTypePos(char type)	// 获取某类型的位置
	{
		Point pos = new Point(-1, -1);
		
		for(int i=0; i<ROW_NUM; i++)
		{
			for(int j=0; j<COL_NUM; j++)
			{
				if (data[i][j] == type)
				{
					pos.x = i;
					pos.y = j;
					
					return pos;
				}
			}
		}
		
		return null;
	}
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------