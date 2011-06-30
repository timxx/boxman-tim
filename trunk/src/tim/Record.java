//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.Point;
//-----------------------------------------------------------
public class Record
{
	public Point	oldPos;		// 原来位置
	public Point	newPos;		// 移动到的新位置
	
	public char		oldType;	// 原来类型
	public char		newType;	// 新位置类型
	
	public boolean 	boxMoved;	// 标记是否移动了箱子
	
	Record()
	{
		oldPos = new Point();
		newPos = new Point();
		
		boxMoved = false;
	}
}
//-----------------------------------------------------------
//END OF FILE
//-----------------------------------------------------------