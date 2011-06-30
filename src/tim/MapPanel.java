//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import javax.swing.JPanel;
//-----------------------------------------------------------
public class MapPanel extends JPanel implements MouseMotionListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	private MapInfo newMap = new MapInfo();
	private MapInfo tmpMap = new MapInfo();
	
	private Image[] img = new Image[10];	// 背景图片

	private Cursor[] cursor = new Cursor[10];
	
	private int dragIndex = -1;
	private Rectangle[] icoRect = new Rectangle[10];
	//-----------------------------------------------------------
	MapPanel()
	{
		init();
	}
	//-----------------------------------------------------------
	private void init()
	{
		loadImgs();
		
		addMouseMotionListener(this);
		addMouseListener(this);
		
		for(int i=0; i<10; i++)
			icoRect[i] = new Rectangle(440, i * MapInfo.IMG_HEIGHT + 30, MapInfo.IMG_WIDTH, MapInfo.IMG_HEIGHT);
	}
	//-----------------------------------------------------------
	private void loadImgs()	// 加载图片
	{
		for (int i=0; i<10; i++)
		{
			URL url = getClass().getResource("res/skin/" + i + ".png");
			img[i] 	= Toolkit.getDefaultToolkit().getImage(url);
			cursor[i] = Toolkit.getDefaultToolkit().createCustomCursor(img[i], new Point(0,0), "myc"+i);
		}
	}
	//-----------------------------------------------------------
	public void paint(Graphics g)
	{
		for (int i=0; i<MapInfo.ROW_NUM; i++)
		{
			for(int j=0; j<MapInfo.COL_NUM; j++)
				g.drawImage(img[newMap.data[i][j]], j * MapInfo.IMG_WIDTH, i * MapInfo.IMG_HEIGHT, this);
		}
		
		URL url = getClass().getResource("res/side.png");
		Image sideimg = Toolkit.getDefaultToolkit().getImage(url);
		
		g.drawImage(sideimg, MapInfo.COL_NUM*MapInfo.IMG_WIDTH + 1, 0, this);
		
		for(int i=0; i<10; i++)
		{
			g.drawImage(img[i], 440, i * MapInfo.IMG_HEIGHT + 30, this);
		}
	}
	//-----------------------------------------------------------
	public void mouseDragged(MouseEvent e){}
	//-----------------------------------------------------------
	public void mouseMoved(MouseEvent e)
	{
		int index = getDragIndex(e.getPoint());
		if (index != -1)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	//-----------------------------------------------------------
	private int getDragIndex(Point pt)	// 获取图片类型索引
	{
		for (int i=0; i<10; i++)
		{
			if (icoRect[i].contains(pt))
				return i;
		}
		
		return -1;
	}
	//-----------------------------------------------------------
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	//-----------------------------------------------------------
	public void mousePressed(MouseEvent e)
	{
		dragIndex = getDragIndex(e.getPoint());
		if (dragIndex != -1)
		{
			setCursor(cursor[dragIndex]);
		}
	}
	//-----------------------------------------------------------
	public void mouseReleased(MouseEvent e)
	{
		if (dragIndex != -1)
		{
			int row = e.getY()/MapInfo.IMG_HEIGHT;
			int col = e.getX()/MapInfo.IMG_WIDTH;
			
			if (row >=0 && row < MapInfo.ROW_NUM &&
				col >= 0 && col < MapInfo.COL_NUM)
			{
				newMap.data[row][col] = (char)dragIndex;
				
				repaint(col * MapInfo.IMG_WIDTH, row * MapInfo.IMG_HEIGHT, MapInfo.IMG_WIDTH, MapInfo.IMG_HEIGHT);
			}
			
			dragIndex = -1;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	//-----------------------------------------------------------
	public boolean isMapValid()	// 简单判断地图是否有有效
	{
		int nMan = 0,
			nDest = 0,
			nBox = 0,
			nWall = 0;

		for (int row = 0; row<MapInfo.ROW_NUM; row++)
		{
			for (int col=0; col<MapInfo.COL_NUM; col++)
			{
				if (newMap.data[row][col] >= MapInfo.TYPE_MAN_U && newMap.data[row][col] <= MapInfo.TYPE_MAN_R)
					nMan++;
				else if(newMap.data[row][col] == MapInfo.TYPE_DEST)
					nDest++;
				else if(newMap.data[row][col] == MapInfo.TYPE_BOX_NRM)
					nBox++;
				else if(newMap.data[row][col] == MapInfo.TYPE_WALL)
					nWall++;
				else if(newMap.data[row][col] == MapInfo.TYPE_BOX_FIS)
				{
					nBox++;
					nDest++;
				}
			}
		}

		//人有且只能有一个
		if (nMan != 1)
			return false;

		//箱子跟目的数目要一致, 且不能为0
		if (nBox != nDest || nBox < 1)
			return false;

		//要有墙
		if (nWall < 1)
			return false;

		return true;
	}
	//-----------------------------------------------------------
	public void doSave(String filePath)
	{
		MapMgr saver = new MapMgr();
		saver.save(new MapSerializable(newMap), filePath);
	}
	//-----------------------------------------------------------
	public void doNew()
	{
		newMap = new MapInfo();
		
		repaint();
	}
	//-----------------------------------------------------------
	public void doPreviewMap(MapInfo mi)
	{
		newMap.setMapInfo(mi);
		
		repaint();
	}
	//-----------------------------------------------------------
	public void doCancelOpenMap()
	{
		newMap.setMapInfo(tmpMap);
		
		repaint();
	}
	//-----------------------------------------------------------
	public void doSaveCurMap()
	{
		tmpMap.setMapInfo(newMap);
	}
	//-----------------------------------------------------------
	public MapInfo getCurMap()
	{
		return newMap;
	}
	//-----------------------------------------------------------
	public void setMapInfo(String ver, short bomb, String name, String author)
	{
		newMap.ver = ver;
		newMap.bomb = bomb;
		newMap.name = name;
		newMap.author = author;
	}
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------