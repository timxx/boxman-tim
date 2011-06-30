//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
//-----------------------------------------------------------
public class GameCanvas extends JPanel implements KeyListener
{
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	private MapInfo curMap = new MapInfo();	// 当前地图
	private MapInfo oldMap = new MapInfo();	// 未移动前的地图
	private Image[] img = new Image[11];	// 背景图片
	
	private BoxManUI ui = null;
	private int steps = 0;			// 移动次数
	
	private Stack<Record> sHistory = new Stack<Record>();	// 记录推箱子历史

	private boolean usingBomb = false;	// 是否在用炸弹
	private Point bombPos = new Point(-1, -1);	// 记录炸弹位置
	private char prevBombType = MapInfo.TYPE_WALL;	// 炸弹取代位置原来的类型
	
	private Timer bombTimer = null;
	//-----------------------------------------------------------
	GameCanvas(BoxManUI ui)
	{
		this.ui = ui;
		addKeyListener(this);
		setBackground(Color.white);
		repaint();
	}
	//-----------------------------------------------------------
	public void setMap(MapInfo map)	// 设置地图
	{
		curMap.setMapInfo(map);
		oldMap.setMapInfo(map);
		
		replay();
	}
	//-----------------------------------------------------------
	public void loadImgs()	//加载默认内置图片
	{
		for (int i=0; i<11; i++)
		{
			URL url = getClass().getResource("res/skin/" + i + ".png");
			img[i] 	= Toolkit.getDefaultToolkit().getImage(url);
		}
		
		repaint();
	}
	//-----------------------------------------------------------
	public void loadImgs(String path)	//加载外部图片
	{
		for (int i=0; i<11; i++)
			img[i] = Toolkit.getDefaultToolkit().getImage(path + SkinMgr.IMG_NAMES[i]);
		
		repaint();
	}
	//-----------------------------------------------------------
	private void replay()	// 重玩
	{
		sHistory.clear();
		ui.setStep((steps = 0));
		ui.enableUndo(false);
		ui.stopTimer();
		
		ui.setBomb(curMap.bomb);
		usingBomb = false;	
		prevBombType = MapInfo.TYPE_WALL;
		bombPos.setLocation(-1, -1);
		stopTimer();

		repaint();
	}
	//-----------------------------------------------------------
	public void paint(Graphics g)	// 绘制
	{
		for (int i=0; i<MapInfo.ROW_NUM; i++)
		{
			for(int j=0; j<MapInfo.COL_NUM; j++)
				g.drawImage(img[curMap.data[i][j]], j * MapInfo.IMG_WIDTH, i * MapInfo.IMG_HEIGHT, this);
		}
	}
	//-----------------------------------------------------------
	private void updateMap(int row, int col)
	{
		repaint(col * MapInfo.IMG_WIDTH, row * MapInfo.IMG_HEIGHT, MapInfo.IMG_WIDTH, MapInfo.IMG_HEIGHT);
	}
	//-----------------------------------------------------------
	private void updateMap(Point pos)
	{
		updateMap(pos.x, pos.y);
	}
	//-----------------------------------------------------------
	public void keyPressed(KeyEvent e)	// 按键处理
	{
		doKeyDown(e);
	}
	//-----------------------------------------------------------
	private void doKeyDown(KeyEvent key)
	{
		// 启用、隐藏炸弹
		if (key.getKeyCode() == KeyEvent.VK_B)
		{
			doBombKey();
			return ;
		}
		else if(key.getKeyCode() == KeyEvent.VK_ENTER)	// 引爆炸弹
		{
			doEnterKey();
			return ;
		}

		Point oldPos = curMap.getManPos();
	
		if (usingBomb)
			oldPos.setLocation(bombPos);
		
		Point newPos = new Point(oldPos);
		char manType = MapInfo.TYPE_MAN_D;
		
		switch(key.getKeyCode())
		{
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			newPos.x--;
			manType = MapInfo.TYPE_MAN_U;
			break;
		
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			newPos.x++;
			manType = MapInfo.TYPE_MAN_D;
			break;
		
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			newPos.y--;
			manType = MapInfo.TYPE_MAN_L;
			break;
		
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			newPos.y++;
			manType = MapInfo.TYPE_MAN_R;
			break;
		}
		
		if (usingBomb)	// 移动炸弹
		{
			moveBomb(bombPos, newPos);
			return ;
		}
		
		char type = curMap.data[newPos.x][newPos.y];

		if(type == MapInfo.TYPE_BOX_NRM ||	// 前方有箱子挡住
			type == MapInfo.TYPE_BOX_FIS)
		{
			if (moveBox(newPos, key))	// 箱子可以移动
			{
				moveMan(oldPos, newPos, manType);

				if (gameCompleted())
				{
					ui.stopTimer();
					if (ui.hasNextLevel())
					{
						int sel = JOptionPane.showConfirmDialog(this, "恭喜！当前关卡已完成！是否继续挑战下一关卡？",
								"推箱子", JOptionPane.YES_NO_OPTION);
						
						if (sel == JOptionPane.YES_OPTION)
							ui.doNext();
					}
					else
					{
						JOptionPane.showMessageDialog(this, "恭喜！你太利害啦，已无下一关卡啦~",
								"推箱子", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
		else if (type == MapInfo.TYPE_ROAD ||
				type == MapInfo.TYPE_DEST)
		{
			moveMan(oldPos, newPos, manType);
		}
	}
	//-----------------------------------------------------------
	public void keyReleased(KeyEvent e){}
	//-----------------------------------------------------------
	public void keyTyped(KeyEvent e){}
	//-----------------------------------------------------------
	private void doBombKey()	// 处理'B'键
	{
		if (curMap.bomb < 1)	// 无炸弹可用
			return ;
		
		usingBomb = !usingBomb;
		
		if (usingBomb)
		{
			// 首次使用炸弹
			if (bombPos.x == -1 && bombPos.y == -1)
			{
				Point pos = curMap.getTypePos(MapInfo.TYPE_WALL);
				if (pos == null)	// 没墙？
					return ;
				
				bombPos.setLocation(pos);
			}
			
			curMap.data[bombPos.x][bombPos.y] = MapInfo.TYPE_BOMB;
			updateMap(bombPos);
			
			startTimer();	// 让炸弹“动”起来
		}
		else
		{
			curMap.data[bombPos.x][bombPos.y] = prevBombType;
			updateMap(bombPos);
			
			if (prevBombType != MapInfo.TYPE_WALL)
			{
				bombPos.setLocation(-1, -1);
				prevBombType = MapInfo.TYPE_WALL;
			}
			
			stopTimer();
		}
	}
	//-----------------------------------------------------------
	private void doEnterKey()	// 处理Enter键
	{
		// 判断是否在用炸弹、是否可炸
		if (!usingBomb || prevBombType != MapInfo.TYPE_WALL)
			return ;
		
		stopTimer();
		
		curMap.data[bombPos.x][bombPos.y] = MapInfo.TYPE_ROAD;
		updateMap(bombPos);
		
		bombPos.setLocation(-1, -1);
		curMap.bomb--;	// 炸弹数减1
		prevBombType = MapInfo.TYPE_WALL;

		usingBomb = false;
		
		ui.setBomb(curMap.bomb);
	}
	//-----------------------------------------------------------
	private boolean moveBox(Point oldPos, KeyEvent key)	// 移动箱子
	{
		Point newPos = new Point(oldPos);
		
		switch(key.getKeyCode())
		{
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:	newPos.x-- ;	break;
		
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:	newPos.x++ ;	break;
		
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:	newPos.y-- ;	break;
		
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:	newPos.y++ ;	break;
		}
		
		char type = curMap.data[newPos.x][newPos.y];

		if (type == MapInfo.TYPE_BKGND 	 ||
			type == MapInfo.TYPE_WALL 	 ||
			type == MapInfo.TYPE_BOX_NRM ||
			type == MapInfo.TYPE_BOX_FIS)
		{
			return false;
		}
		
		// 移动保存
		makeRecord(oldPos, newPos, true);

		if (type == MapInfo.TYPE_DEST)	// 箱子刚好推到目的地
			curMap.data[newPos.x][newPos.y] = MapInfo.TYPE_BOX_FIS;
		else
			curMap.data[newPos.x][newPos.y] = MapInfo.TYPE_BOX_NRM;
		
		type = oldMap.data[oldPos.x][oldPos.y];
		
		// 恢复现场
		if (type == MapInfo.TYPE_DEST || type == MapInfo.TYPE_BOX_FIS)
			curMap.data[oldPos.x][oldPos.y] = MapInfo.TYPE_DEST;
		else
			curMap.data[oldPos.x][oldPos.y] = MapInfo.TYPE_ROAD;
		
		updateMap(oldPos);
		updateMap(newPos);
		
		return true;
	}
	//-----------------------------------------------------------
	// 移动人物
	//-----------------------------------------------------------
	private void moveMan(Point oldPos, Point newPos, char manType)
	{
		char type = oldMap.data[oldPos.x][oldPos.y];
		
		// 移动保存	
		makeRecord(oldPos, newPos, false);
	
		// 恢复原有现场
		if (type == MapInfo.TYPE_DEST ||
			type == MapInfo.TYPE_BOX_FIS)
			curMap.data[oldPos.x][oldPos.y] = MapInfo.TYPE_DEST;
		else
			curMap.data[oldPos.x][oldPos.y] = MapInfo.TYPE_ROAD;
		
		curMap.data[newPos.x][newPos.y] = manType;
		
		updateMap(oldPos);
		updateMap(newPos);
		
		ui.setStep(++steps);
		if (steps == 1)		// 第一次移动，开始计时
			ui.startTimer();
		
		if (steps > 0)
			ui.enableUndo(true);
	}
	//-----------------------------------------------------------
	// 移动炸弹
	//-----------------------------------------------------------
	private void moveBomb(Point oldPos, Point newPos)
	{
		char type = curMap.data[newPos.x][newPos.y];
		
		// 先判断是否能够移动	
		if (type == MapInfo.TYPE_BOX_NRM ||
			type == MapInfo.TYPE_BOX_FIS ||
			type == MapInfo.TYPE_BKGND	 ||
			(type >= MapInfo.TYPE_MAN_U && type <= MapInfo.TYPE_MAN_R))
		{
			return ;
		}
		
		// 还原炸弹覆盖的类型
		curMap.data[oldPos.x][oldPos.y] = prevBombType;
		
		prevBombType = curMap.data[newPos.x][newPos.y];
		curMap.data[newPos.x][newPos.y] = MapInfo.TYPE_BOMB;
		
		updateMap(oldPos);
		updateMap(newPos);
		
		bombPos.setLocation(newPos);
	}
	//-----------------------------------------------------------
	private boolean gameCompleted()	// 是否已完成任务
	{
		for(int i=0; i<MapInfo.ROW_NUM; i++)
		{
			for(int j=0; j<MapInfo.COL_NUM; j++)
			{
				if (curMap.data[i][j] == MapInfo.TYPE_BOX_NRM)
					return false;
				
				if (curMap.data[i][j] == MapInfo.TYPE_DEST)
					return false;
			}
		}
		
		return true;
	}
	//-----------------------------------------------------------
	// 记录一个移动过程
	//-----------------------------------------------------------
	private void makeRecord(Point oldPos, Point newPos, boolean isBox)
	{
		Record record = new Record();
		
		record.oldPos.setLocation(oldPos);
		record.newPos.setLocation(newPos);
		
		record.oldType = curMap.data[oldPos.x][oldPos.y];
		record.newType = curMap.data[newPos.x][newPos.y];
		
		record.boxMoved = isBox;
		
		sHistory.push(record);
	}
	//-----------------------------------------------------------
	public void undo()	// 撤消一次移动
	{
		Record record = sHistory.pop();
	
		curMap.data[record.oldPos.x][record.oldPos.y] = record.oldType;
		curMap.data[record.newPos.x][record.newPos.y] = record.newType;
		
		updateMap(record.oldPos);
		updateMap(record.newPos);
		
		if (sHistory.size() > 0)
		{
			record = sHistory.pop();
			if (record.boxMoved)		// 如果同时移动了箱子则再回退一步
			{
				curMap.data[record.oldPos.x][record.oldPos.y] = record.oldType;
				curMap.data[record.newPos.x][record.newPos.y] = record.newType;
				
				updateMap(record.oldPos);
				updateMap(record.newPos);
			}
			else
			{
				sHistory.push(record);
			}
		}
		
		ui.setStep(--steps);
		
		if (sHistory.size() == 0)
			ui.enableUndo(false);
	}
	//-----------------------------------------------------------
	private void startTimer()	// 启用炸弹定时器
	{
		bombTimer = new Timer();
		bombTimer.schedule(new TimerTask() {
			public void run()
			{
				if (prevBombType == MapInfo.TYPE_WALL ||
					prevBombType == MapInfo.TYPE_DEST)
				{
					if (curMap.data[bombPos.x][bombPos.y] == MapInfo.TYPE_BOMB)
					{
						curMap.data[bombPos.x][bombPos.y] = prevBombType;
					}
					else
					{
						curMap.data[bombPos.x][bombPos.y] = MapInfo.TYPE_BOMB;
					}
					
					updateMap(bombPos);
				}
			}
			},
			0, 500);
	}
	//-----------------------------------------------------------
	private void stopTimer()
	{
		if (bombTimer != null)
		{
			bombTimer.cancel();
			bombTimer.purge();
			bombTimer = null;
		}
	}
	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------