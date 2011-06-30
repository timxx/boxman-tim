//-----------------------------------------------------------
package tim;
//-----------------------------------------------------------
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

//-----------------------------------------------------------
public class NumberField extends JTextField implements KeyListener
{
	private static final long serialVersionUID = 1L;
	//-----------------------------------------------------------
	NumberField(int columns)
	{
		setColumns(columns);
		addKeyListener(this);
	}
	//-----------------------------------------------------------
	public void keyPressed(KeyEvent e){
	}
	public void keyReleased(KeyEvent e){
	}
	//-----------------------------------------------------------
	public void keyTyped(KeyEvent e)
	{
		char key = e.getKeyChar();
		
		//只接收输入数字
		if ((key < KeyEvent.VK_0 ||
			key > KeyEvent.VK_9)
			)
		{
			e.consume();
		}
	}
	//-----------------------------------------------------------
}
//-----------------------------------------------------------
// END OF FILE
//-----------------------------------------------------------