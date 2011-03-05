import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;


public class Input implements KeyListener, MouseListener, MouseWheelListener, FocusListener, WindowListener {
	private boolean[] pressedKeys = new boolean[526];
	private boolean focus = false;
	private Robot robot;
	private boolean keepMouseCenteredAndHidden;
	private boolean focusGained;
	private boolean focusLost;
	private int wheelRotation = 0;
	public Point mouseCenter;
	private Point mousePosBeforeCentering;
	
	public static Cursor noCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new java.awt.Point(0,0), "none");
	public static int forward	= KeyEvent.VK_W;
	public static int backward	= KeyEvent.VK_S;
	public static int strafeL	= KeyEvent.VK_A;
	public static int strafeR	= KeyEvent.VK_D;
	public static int up		= KeyEvent.VK_SPACE;
	public static int down		= KeyEvent.VK_SHIFT;
	public static int menu 		= KeyEvent.VK_ESCAPE;
	
	public Input() {
		Robot r=null;
		try{
		r=new Robot();
		}catch(final AWTException e){System.out.println("Trouble stating Robot");}
		robot=r;
		if(robot==null)System.out.println("Error robot has not been initialized");
	}
	public boolean keyPressed(int keyCode) {
		if (pressedKeys[keyCode]) {
			pressedKeys[keyCode] = false;
			return true;
		}
		return false; 
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent wheel) {
		wheelRotation += wheel.getWheelRotation(); 
	}
	@Override
	public void mouseClicked(MouseEvent mouse) {
		System.out.println(mouse);
	}
	@Override
	public void mouseEntered(MouseEvent mouse) {
		System.out.println(mouse);
	}
	@Override
	public void mouseExited(MouseEvent mouse) {
		System.out.println(mouse);
	}
	@Override
	public void mousePressed(MouseEvent mouse) {
		System.out.println(mouse);
	}
	@Override
	public void mouseReleased(MouseEvent mouse) {
		System.out.println(mouse);
	}
	@Override
	public void keyPressed(KeyEvent key) {
		pressedKeys[key.getKeyCode()] = true;
	}
	@Override
	public void keyReleased(KeyEvent key) {
	}
	@Override
	public void keyTyped(KeyEvent key) {
	}
	@Override
	public void windowActivated(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void windowClosed(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void windowClosing(WindowEvent window) {
		System.out.println(window);
        System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void windowDeiconified(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void windowIconified(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void windowOpened(WindowEvent window) {
		System.out.println(window);
	}
	@Override
	public void focusGained(FocusEvent e) {
		System.out.println("FocusGained");
		focusGained = true;
		focus = true;
	}
	@Override
	public void focusLost(FocusEvent e) {
		System.out.println("FocusLost");
		focusLost = true;
		focus = false;
	}
	
	
	
	
	
	public boolean focusGained() {
		if (focusGained) {
			focusGained = false;
			return true;
		}
		return false;
	}
	public boolean focusLost() {
		if (focusLost) {
			focusLost = false;
			return true;
		}
		return false;
	}
	public boolean inFocus() {
		return focus;
	}
	public int getMouseWheelRotation() {
		int rotation = wheelRotation;
		wheelRotation = 0;
		return rotation;
	}
	public void hideCursor(boolean hide) {
		if (hide)
			Engine.getFrame().setCursor(noCursor);
		else
			Engine.getFrame().setCursor(Cursor.getDefaultCursor());
	}
	public void keepCursorCenteredAndHidden(boolean hide) {
		if (hide) {
			hideCursor(true);
			if (!keepMouseCenteredAndHidden) {
				mousePosBeforeCentering = MouseInfo.getPointerInfo().getLocation();
				robot.mouseMove(mouseCenter.x, mouseCenter.y);
			}
		}
		else {
			hideCursor(false);
			if (keepMouseCenteredAndHidden)
				robot.mouseMove(mousePosBeforeCentering.x, mousePosBeforeCentering.y);
		}
		keepMouseCenteredAndHidden = hide;
	}
	public Point getMouseMovementAndCenter() {
		if (keepMouseCenteredAndHidden) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(mouseCenter.x, mouseCenter.y);
			return new Point(mousePos.x-mouseCenter.x, mousePos.y-mouseCenter.y);
		}
		return null;
	}
}
