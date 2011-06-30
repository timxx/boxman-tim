//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
//-----------------------------------------------------------
public class MapMaker extends JFrame implements ActionListener
{
	//-----------------------------------------------------------
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	MapPanel mapPainter = new MapPanel();
	MapInfoFrame miFrame = null;
	//-----------------------------------------------------------
	MapMaker()
	{
		setTitle("地图管理");
		setSize(480, 410);
		setLocationRelativeTo(null);
		setResizable(false);

		init();	

		validate();
	}
	//-----------------------------------------------------------
	private void init()
	{
	    URL iconURL = getClass().getResource("res/skin/3.png");
	    Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
	    setIconImage(icon);
	    
		setLayout(null);
		setMenu();
		
		add(mapPainter);
		mapPainter.setBounds(0, 0, 480, 360);
	}
	//-----------------------------------------------------------
	private void setMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("文件(F)");
		file.setMnemonic(KeyEvent.VK_M);
		
		JMenuItem open 	= new JMenuItem("打开地图");
		JMenuItem newM	= new JMenuItem("新建地图");
		JMenuItem save	= new JMenuItem("保存地图");
		
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		newM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		
		file.add(open);
		file.add(newM);
		file.add(save);
		
		open.addActionListener(this);
		newM.addActionListener(this);
		save.addActionListener(this);
		
		JMenu option = new JMenu("选项(O)");
		option.setMnemonic(KeyEvent.VK_O);
		
		JMenuItem info = new JMenuItem("地图信息");
		info.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		info.addActionListener(this);
		
		option.add(info);
		
		menuBar.add(file);
		menuBar.add(option);
		
		setJMenuBar(menuBar);
	}
	//-----------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("打开地图")){
			doOpen();
		}
		else if(e.getActionCommand().equals("新建地图")){
			doNew();
		}
		else if(e.getActionCommand().equals("保存地图")){
			doSave();
		}
		else if(e.getActionCommand().equals("地图信息")){
			doMapInfo();
		}
	}
	//-----------------------------------------------------------
	private void doOpen()
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
		
		ArrayList<MapInfo> mapList = loader.getMapList();
		
		if (mapList.size() == 0)
		{
			JOptionPane.showMessageDialog(this, "你所选择的文件不包含有有效地图信息哦~",
					"推箱子", JOptionPane.ERROR_MESSAGE);
			
			return ;
		}
		else
		{
			String names[] = new String[mapList.size()];
			
			for(int i=0; i<mapList.size(); i++)
				names[i] = mapList.get(i).name;
			
			mapPainter.doSaveCurMap();
			/*SelectionList list = */new SelectionList(names, mapList, mapPainter);
		}
	}
	//-----------------------------------------------------------
	private void doNew()
	{
		mapPainter.doNew();
	}
	//-----------------------------------------------------------
	private void doSave()
	{
		if (!mapPainter.isMapValid())
		{
			JOptionPane.showMessageDialog(this, "你编辑的地图不符合基本要求哦~",
					"推箱子", JOptionPane.ERROR_MESSAGE);
			return ;
		}
		
		FileDialog openDlg = new FileDialog(this, "保存地图文件", FileDialog.SAVE);
		openDlg.setVisible(true);

		String dir = openDlg.getDirectory();
		String file = openDlg.getFile();

		if (dir == null || file == null)
			return ;

		String fullPath = dir + file;
		mapPainter.doSave(fullPath);
	}
	//-----------------------------------------------------------
	private void doMapInfo()
	{
		if (miFrame == null)
		{
			miFrame = new MapInfoFrame(mapPainter);
		}
		miFrame.setVisible(true);
		miFrame.setMapInfo(mapPainter.getCurMap());
	}
	//-----------------------------------------------------------

}
//-----------------------------------------------------------
//END OF FILE
//-----------------------------------------------------------