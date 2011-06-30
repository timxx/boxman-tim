//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
//-----------------------------------------------------------
public class SelectionList extends JDialog implements ListSelectionListener, ActionListener
{
	//-----------------------------------------------------------
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	private JList jList = null;
	private JButton jButton = null;
	//-----------------------------------------------------------
	ArrayList<MapInfo> mapList = null;
	MapPanel mapPanel = null;
	//-----------------------------------------------------------
	SelectionList(String[] list, ArrayList<MapInfo> mapList, MapPanel mapPanel)
	{
	    URL iconURL = getClass().getResource("res/skin/1.png");
	    Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
	    setIconImage(icon);
	    
		setSize(150, 200);
		setLocationRelativeTo(null);
		
		setVisible(true);
		setResizable(false);
		setTitle("地图列表");
		
		setLayout(null);
		
		this.mapList = mapList;
		this.mapPanel = mapPanel;

		jList = new JList(list);
		jList.addListSelectionListener(this);
		
		JScrollPane sp = new JScrollPane(jList);
		jButton = new JButton("确定");
		
		sp.setBounds(0, 0, 145, 140);
		jButton.setBounds(30, 145, 70, 20);
		
		add(sp);	
		add(jButton);
		
		jButton.addActionListener(this);
			
		validate();
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent arg0)
			{
				doExit();
			}
		});
	}
	//-----------------------------------------------------------
	public void valueChanged(ListSelectionEvent e)
	{
		if (mapPanel != null)
		{
			int index = e.getFirstIndex();
			if (index >= 0 && index <= mapList.size())
				mapPanel.doPreviewMap(mapList.get(index));
		}
	}
	//-----------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == jButton)
		{
			setVisible(false);
		}
	}
	//-----------------------------------------------------------
	private void doExit()
	{
		mapPanel.doCancelOpenMap();
	}
}
//-----------------------------------------------------------
//END OF FILE
//-----------------------------------------------------------