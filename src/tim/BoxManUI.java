//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
//-----------------------------------------------------------
public class BoxManUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	//-----------------------------------------------------------
	// 成员变量
	//-----------------------------------------------------------
	JMenuItem miReplay 	= new JMenuItem("重新开始", new ImageIcon(getClass().getResource("res/replay.png")));
    JMenuItem miBack	= new JMenuItem("上一关卡", new ImageIcon(getClass().getResource("res/back.png")));
	JMenuItem miNext	= new JMenuItem("下一关卡", new ImageIcon(getClass().getResource("res/next.png")));
	JMenuItem miUndo	= new JMenuItem("后退一步");
	JMenuItem miJump	= new JMenuItem("转到关卡");
	
	JMenu mnSound 		= new JMenu("背景音乐");
	JMenu mnSkin 		= new JMenu("皮肤");
	
	ButtonGroup gpSound = new ButtonGroup();
	ButtonGroup gpSkin 	= new ButtonGroup();
	
	GameCanvas canvas 	= new GameCanvas(this);
	
	JLabel lbTimer	= new JLabel("00:00:00", SwingConstants.CENTER);
	JLabel lbBomb	= new JLabel("炸弹数：0", SwingConstants.CENTER);
	JLabel lbStep	= new JLabel("移动次数：0", SwingConstants.CENTER);
	JLabel lbInfo	= new JLabel("", SwingConstants.CENTER);
	
	ArrayList<MapInfo> mapList = null;	// 地图列表
	
	Timer gameTimer = null;		// 游戏计时器
	private long gameTime = 0;
	
	ArrayList<String> skinList = null;	// 皮肤列表
	ArrayList<JRadioButtonMenuItem> skinItem = new ArrayList<JRadioButtonMenuItem>();
	
	ArrayList<String> soundList = null;	// 音乐文件列表
	ArrayList<JRadioButtonMenuItem> soundItem = new ArrayList<JRadioButtonMenuItem>();
	
	Settings sett = new Settings();		// 设置
	Sound bkMusic = new Sound();		// 背景音乐
	
	MapMaker mapMaker = null;	// 地图绘制窗口
	//-----------------------------------------------------------
	// 成员函数
	//-----------------------------------------------------------
	BoxManUI()
	{
		setIcon();
		setTitle("推箱子");
		setSize(425, 440);
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent arg0)
			{
				sett.save();
				System.exit(0);
			}
		});
		
		setLocationRelativeTo(null);
		setVisible(true);
		
		init();
	}
	//-----------------------------------------------------------
	//	加载地图
	//-----------------------------------------------------------
	private void loadMap()
	{		
		MapMgr mgr = new MapMgr();
		mgr.load();
		mapList = mgr.getMapList();
		
		if (mapList.size() > 0 && sett.level >= 0)
		{
			if (sett.level < mapList.size())
				canvas.setMap(mapList.get(sett.level));
		}
		else
			sett.level = - 1;
		
		initMenuState();
	}
	//-----------------------------------------------------------
	// 加载皮肤
	//-----------------------------------------------------------
	private void loadSkin()
	{
		SkinMgr mgr = new SkinMgr();
		mgr.load();
		
		skinList = mgr.getSkinList();
		String skin = new String();
		
		for(int i=0; i<skinList.size(); i++)
		{
			 JRadioButtonMenuItem mi = new JRadioButtonMenuItem(skinList.get(i));
			 gpSkin.add(mi);
			 mnSkin.add(mi);
			 mi.addActionListener(this);
			 
			 skinItem.add(mi);
			 
			 if (!sett.skinName.isEmpty() && sett.skinName.equals(skinList.get(i)))
			 {
				 skin = sett.skinName;
				 mi.setSelected(true);
			 }
		}
		
		if (skinList.size() == 0 || skin.isEmpty())
			canvas.loadImgs();
		else
			canvas.loadImgs("Skin/" + skin + "/");
	}
	//-----------------------------------------------------------
	private void loadSound()	// 加载音乐列表
	{
		SoundMgr loader  = new SoundMgr();
		loader.load();
		
		soundList = loader.getSoundList();
		
		String sound = new String();
		
		for(int i=0; i<soundList.size(); i++)
		{
			 JRadioButtonMenuItem mi = new JRadioButtonMenuItem(soundList.get(i));
			 gpSound.add(mi);
			 mnSound.add(mi);
			 mi.addActionListener(this);
			 
			 soundItem.add(mi);
			 
			 if (!sett.soundName.isEmpty() && sett.soundName.equals(soundList.get(i)))
			 {
				 sound = sett.soundName;
				 mi.setSelected(true);
			 }
		}
		
		if (soundList.size() > 0 && !sound.isEmpty())
			bkMusic.playL("Sound/" + sound);
	}
	//-----------------------------------------------------------
	private void init()	// 初始化
	{
		setLayout(null);
		
		setMenu();

		add(canvas);
		add(lbTimer);
		add(lbBomb);
		add(lbStep);
		add(lbInfo);
		
		canvas.setBounds(0, 0, 420, 360);
		lbTimer.setBounds(5, 365, 50, 20);
		lbBomb.setBounds(60, 365, 100, 20);
		lbStep.setBounds(150, 365, 100, 20);
		lbInfo.setBounds(260, 365, 190, 20);
		
		sett.load();
		loadMap();
		loadSkin();
		loadSound();
		
		canvas.requestFocus();
		validate();
		
		enableUndo(false);
	}
	//-----------------------------------------------------------
	private void initMenuState()	// 初始化一些菜单状态
	{
		if (mapList.size() > 0)
		{
			if (sett.level == -1 || sett.level >= mapList.size())
			{
				sett.level = 0;
				canvas.setMap(mapList.get(0));
			}
			miBack.setEnabled(false);
			if (mapList.size() == 1)
				miNext.setEnabled(false);
			
			if (mapList.size() > 1)
			{
				miJump.setEnabled(true);
				miNext.setEnabled(true);
			}
			
			miReplay.setEnabled(true);
			
			showMapInfo();
		}
		else
		{
			miBack.setEnabled(false);
			miNext.setEnabled(false);
			miUndo.setEnabled(false);
			miReplay.setEnabled(false);
			miJump.setEnabled(false);
		}
	}
	//-----------------------------------------------------------
	private void setIcon()	//设置程序标题图标
	{
	    URL iconURL = getClass().getResource("res/BoxMan.png");
	    Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
	    setIconImage(icon);
	}
	//-----------------------------------------------------------
	private void setMenu()	//设置菜单
	{
		JMenuBar menuBar = new JMenuBar();

	    menuBar.add(getMenuGame());
	    menuBar.add(getMenuMap());
	    menuBar.add(getMenuOptions());
	    menuBar.add(getMenuHelp());

	    setJMenuBar(menuBar);
	}
	//-----------------------------------------------------------
	private JMenu getMenuGame()
	{
	    JMenu menuGame = new JMenu("游戏(G)");
	    
	    menuGame.setMnemonic(KeyEvent.VK_G);

	    miReplay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
	    miBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
	    miNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	    miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
	    miJump.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
	    
	    menuGame.add(miReplay);
	    menuGame.add(miBack);
	    menuGame.add(miNext);
	    menuGame.addSeparator();  
	    
	    menuGame.add(miJump);
	    menuGame.addSeparator();
	    
	    menuGame.add(miUndo);
	    menuGame.addSeparator();
	    
	    JMenuItem miExit = new JMenuItem("退出");  
	    menuGame.add(miExit);
	    
	    miReplay.addActionListener(this);
	    miBack.addActionListener(this);
	    miNext.addActionListener(this);
	    miUndo.addActionListener(this);    
	    miJump.addActionListener(this);
	    miExit.addActionListener(this);

	    return menuGame;
	}
	//-----------------------------------------------------------
	private JMenu getMenuMap()
	{	    
	    JMenu menuMap = new JMenu("地图(M)");
	    
	    menuMap.setMnemonic(KeyEvent.VK_M);
	    
	    JMenuItem load = new JMenuItem("加载地图");
	    JMenuItem mgr = new JMenuItem("管理地图");
	    
	    load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	    mgr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
	    
	    menuMap.add(load);
	    menuMap.add(mgr);
	    
	    load.addActionListener(this);
	    mgr.addActionListener(this);
	    
	    return menuMap;
	}
	//-----------------------------------------------------------
	private JMenu getMenuOptions()
	{
		 JMenu menu = new JMenu("选项(O)");
		 menu.setMnemonic(KeyEvent.VK_O);
		 
		 JRadioButtonMenuItem miOff = new JRadioButtonMenuItem("关闭");
		 mnSound.add(miOff);
		 miOff.setSelected(true);
		 
		 gpSound.add(miOff);
		 
		 JRadioButtonMenuItem miDef = new JRadioButtonMenuItem("默认");
		 mnSkin.add(miDef);
		 miDef.setSelected(true);
		 
		 gpSkin.add(miDef);
		 
		 menu.add(mnSound);
		 menu.add(mnSkin);
		 
		 miOff.addActionListener(this);
		 miDef.addActionListener(this);
		 
		 return menu;
	}
	//-----------------------------------------------------------
	private JMenu getMenuHelp()
	{
	    JMenu menuHelp = new JMenu("帮助(H)");
	    
	    menuHelp.setMnemonic(KeyEvent.VK_H);
	    
	    JMenuItem about = new JMenuItem("关于");
	    JMenuItem help = new JMenuItem("帮助");
	    
	    menuHelp.add(about);
	    menuHelp.add(help);
	    
	    about.addActionListener(this);
	    help.addActionListener(this);
	    
	    return menuHelp;
	}
	//-----------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		canvas.requestFocus();
		if (e.getActionCommand().equals("退出")){
			System.exit(0);
		}
		else if(e.getActionCommand().equals("关于")){
			doAbout();
		}
		else if(e.getActionCommand().equals("帮助")){
			doHelp();
		}
		else if(e.getActionCommand().equals("重新开始")){
			doReplay();
		}
		else if(e.getActionCommand().equals("上一关卡")){
			doBack();
		}
		else if(e.getActionCommand().equals("下一关卡")){
			doNext();
		}
		else if(e.getActionCommand().equals("后退一步")){
			doUndo();
		}
		else if(e.getActionCommand().equals("转到关卡")){
			doJump();
		}
		else if(e.getActionCommand().equals("加载地图")){
			doLoad();
		}
		else if(e.getActionCommand().equals("管理地图")){
			doEdit();
		}
		else if(e.getActionCommand().equals("默认")){
			canvas.loadImgs();
			sett.skinName = "";
		}
		else if(e.getActionCommand().equals("关闭")){
			sett.soundName = "";
			bkMusic.stop();
		}
		else
		{
			for(int i=0; i<skinItem.size(); i++)
			{
				if (e.getSource() == skinItem.get(i))
				{
					if (!(new File("Skin/" + skinList.get(i)).exists()))
					{
						JOptionPane.showMessageDialog(this, "此皮肤已不存在！",
								"推箱子",
								JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						sett.skinName = skinList.get(i);
						canvas.loadImgs("Skin/" + sett.skinName + "/");
					}
					
					return ;
				}
			}
			
			for(int i=0; i<soundItem.size(); i++)
			{
				if (e.getSource() == soundItem.get(i))
				{
					if (!(new File("Sound/" + soundList.get(i)).exists()))
					{
						JOptionPane.showMessageDialog(this, "此声音文件已不存在！",
								"推箱子",
								JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						sett.soundName = soundList.get(i);
						bkMusic.playL("Sound/" + sett.soundName);
					}
					
					return ;
				}
			}
		}
	}
	//-----------------------------------------------------------
	private void doAbout()	// 关于
	{
		JOptionPane.showMessageDialog(this, "推箱子 v1.0\n" + 
				"制作者：____梁维添____\n" +
				"EMAIL： Just_Fancy@live.com\n" +
				"___________Jun. 2011_____",
				"关于",
				JOptionPane.INFORMATION_MESSAGE);
	}
	//-----------------------------------------------------------
	private void doHelp()	// 帮助
	{
		JOptionPane.showMessageDialog(this, "移动人物：用方向键或W, A, S, D\n" + 
				"使用炸弹：先按B键显示炸弹，移动到目的地后按Enter引爆。s\r\n" + 
				"再次按B可隐藏炸弹\r\n" +
				"音乐播放：格式只能是wav和au，请放置于程序根目录下的Sound文件夹，\n" +
				"不支持子目录的哦~\n" + 
				"皮肤：请在程序根目录下的Skin目录下新建一个文件夹（相当于皮肤名称），\n" + 
				"在新建的文件夹里面放置所需图片，名字是固定的，不能改成其它的哦~"
				,
				"推箱子－帮助",
				JOptionPane.INFORMATION_MESSAGE);
	}
	//-----------------------------------------------------------
	private void doReplay()	// 重新开始
	{
		int sel = JOptionPane.showConfirmDialog(this, "确定重玩当局游戏吗？",
				"推箱子",  JOptionPane.YES_NO_OPTION);
		
		if (sel == JOptionPane.YES_OPTION)
		{
			showGameTime((gameTime = 0));
			canvas.setMap(mapList.get(sett.level));
		}
	}
	//-----------------------------------------------------------
	private void doBack()	// 上一关卡
	{
		canvas.setMap(mapList.get(--sett.level));
		if (sett.level == 0)	// 已到首关卡
			miBack.setEnabled(false);
		
		miNext.setEnabled(true);
		showMapInfo();
		stopTimer();
		showGameTime((gameTime = 0));
	}
	//-----------------------------------------------------------
	public void doNext()	// 下一关卡
	{
		canvas.setMap(mapList.get(++sett.level));
		if(sett.level == mapList.size() - 1)	// 末关卡
			miNext.setEnabled(false);
		
		miBack.setEnabled(true);
		showMapInfo();
		stopTimer();
		showGameTime((gameTime = 0));
	}
	//-----------------------------------------------------------
	private void doUndo()	// 后退一步
	{
		canvas.undo();
	}
	//-----------------------------------------------------------
	private void doJump()	// 转到关卡
	{
		String level = JOptionPane.showInputDialog(this, "请输入您要转到的关卡号：(1~" + mapList.size() + ")");
		if (level == null)
			return ;
		
		int l = 0;
		level = level.trim();	// 去除前后空格
		try
		{
			l = Integer.parseInt(level);
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "您输入的是无效的数字哦~",
					"推箱子", JOptionPane.ERROR_MESSAGE);
			return ;
		}
		
		if (l <= 0 || l > mapList.size())
		{
			JOptionPane.showMessageDialog(this, "您输入的关卡无效哦~",
					"推箱子", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			sett.level = l - 1;
			
			canvas.setMap(mapList.get(sett.level));
			
			if (sett.level == 0)
				miBack.setEnabled(false);
			else
				miBack.setEnabled(true);
			
			if (sett.level == mapList.size() - 1)
				miNext.setEnabled(false);
			else
				miNext.setEnabled(true);
			
			showMapInfo();
			stopTimer();
			showGameTime((gameTime = 0));
		}
	}
	//-----------------------------------------------------------
	private void doLoad()	// 加载地图
	{
		FileDialog openDlg = new FileDialog(this, "选择地图文件", FileDialog.LOAD);
		openDlg.setVisible(true);
		
		String dir = openDlg.getDirectory();
		String file = openDlg.getFile();
		
		if (dir == null || file == null)
			return ;
		
		String fullPath = dir + file;
		
		MapMgr loader = new MapMgr();
		loader.load(fullPath, true);
		
		ArrayList<MapInfo> list = loader.getMapList();
		
		for(int i=0; i<list.size(); i++)
			mapList.add(list.get(i));
		
		initMenuState();
	}
	//-----------------------------------------------------------
	private void doEdit()	// 地图管理
	{
		if (mapMaker == null)
			mapMaker = new MapMaker();
		
		mapMaker.setVisible(true);
	}
	//-----------------------------------------------------------
	public void setStep(int step)	// 显示移动次数
	{
		lbStep.setText("移动次数：" + step);
	}
	//-----------------------------------------------------------
	public void setBomb(int count)	// 显示炸弹数量
	{
		lbBomb.setText("炸弹数：" + count);
	}
	//-----------------------------------------------------------
	private void showMapInfo()	// 显示地图信息
	{
		if (sett.level >= 0 && sett.level < mapList.size())
		{
			MapInfo mi = mapList.get(sett.level);
			lbBomb.setText("炸弹数：" + mi.bomb);
			lbInfo.setText(mi.ver.trim() + " by " + mi.author.trim());

			setTitle("推箱子－关卡" + (sett.level + 1) + "－" + mi.name.trim());
		}
	}
	//-----------------------------------------------------------
	public void startTimer()	// 游戏计时
	{
		gameTimer = new Timer();
		gameTimer.schedule(new TimerTask() {
			public void run()
			{
				showGameTime(++gameTime);
			}
			},
			0, 1000);
	}
	//-----------------------------------------------------------
	public void stopTimer()
	{
		if (gameTimer != null)
		{
			gameTimer.cancel();
			gameTimer.purge();
			gameTimer = null;
		}
	}
	//-----------------------------------------------------------
	private void showGameTime(long time)	// 显示游戏时间
	{
		long sec = time % 60;
		long min = time/60;
		long hor = time/60/60;
		
		String str = String.format("%02d:%02d:%02d", hor, min, sec);
		
		lbTimer.setText(str);
	}
	//-----------------------------------------------------------
	public void enableUndo(boolean fEnable)	// 启用或禁用“后退一步”菜单
	{
		miUndo.setEnabled(fEnable);
	}
	//-----------------------------------------------------------
	public boolean hasNextLevel()	// 是否可以进行下一关卡
	{
		return sett.level < mapList.size() - 1;
	}
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------