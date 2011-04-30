package engine;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
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
	/*
	 * Delay, in frames (for now), between the first
	 * and second time keyTyped will return true if
	 * a key is held down. And the delay between
	 * each time after that.
	 * the delay must be > 0:
	 * 1 = every frame
	 * 2 = every other frame and so on
	 */
	private static final byte firstDelay = 10;
	private static final byte delay = 1;
	
	private static byte[]   keyTyped = new byte[526];
	private static boolean[] keyDown = new boolean[526];
	private static boolean[] keyPressed = new boolean[526];
	private static boolean[] keyReleased = new boolean[526];
	private static boolean mouseClicked = false;
	private static boolean focus = false;
	private static Robot robot;
	private static boolean keepMouseCenteredAndHidden;
	private static boolean focusGained;
	private static boolean focusLost;
	private static int wheelRotation = 0;
	private static Point mouseCenter;
	private static Point mousePosBeforeCentering;
	private static Cursor noCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new java.awt.Point(0,0), "none");
	public static Input listener;
	
	private Input() {}
	
	public static void init() {
		try {
			robot=new Robot();
		}
		catch (final AWTException e) {
			System.out.println("Trouble stating Robot");
		}
		if (robot==null)
			System.out.println("Error robot for centering mouse has not been initialized");
		listener = new Input();
	}
	
	public static boolean keyTyped(int keyCode) {
		if (keyDown[keyCode]) {
			keyTyped[keyCode]--;
			if (keyTyped[keyCode] <= 0) {
				if (keyTyped[keyCode] == -1) //only if it was set to 0 will it reach this.
					keyTyped[keyCode] = firstDelay;
				else
					keyTyped[keyCode] = delay;
				return true;
			}
		}
		return false;
	}

	public static boolean keyPressed(int keyCode) {
		boolean ret = keyPressed[keyCode];
		keyPressed[keyCode] = false;
		return ret;
	}

	public static boolean keyReleased(int keyCode) {
		boolean ret = keyReleased[keyCode];
		keyReleased[keyCode] = false;
		return ret;
	}
	public static boolean keyDown(int keyCode) {
		return keyDown[keyCode];
	}
	public static boolean mouseClick(){
		return mouseClicked;
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
		mouseClicked = true;
	}
	@Override
	public void mouseReleased(MouseEvent mouse) {
		mouseClicked = false;
	}
	@Override
	public void keyPressed(KeyEvent key) {
		if (!keyDown[key.getKeyCode()]) {
			keyTyped[key.getKeyCode()] = 0;
			keyDown[key.getKeyCode()] = true;
			keyPressed[key.getKeyCode()] = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent key) {
		keyDown[key.getKeyCode()] = false;
		keyReleased[key.getKeyCode()] = true;
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
	
	
	
	
	
	public static boolean focusGained() {
		if (focusGained) {
			focusGained = false;
			return true;
		}
		return false;
	}
	public static boolean focusLost() {
		if (focusLost) {
			focusLost = false;
			return true;
		}
		return false;
	}
	public static boolean inFocus() {
		return focus;
	}
	public static int getMouseWheelRotation() {
		int rotation = wheelRotation;
		wheelRotation = 0;
		return rotation;
	}
	public static void setMouseCenter(Point mouseCenter) {
		Input.mouseCenter = mouseCenter;
	}
	public static void hideCursor(boolean hide) {
		if (hide)
			Engine.getFrame().setCursor(noCursor);
		else
			Engine.getFrame().setCursor(Cursor.getDefaultCursor());
	}
	public static void keepCursorCenteredAndHidden(boolean hide) {
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
	public static Point getMouseMovementAndCenter() {
		if (keepMouseCenteredAndHidden) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(mouseCenter.x, mouseCenter.y);
			return new Point(mousePos.x-mouseCenter.x, mousePos.y-mouseCenter.y);
		}
		return null;
	}
	
	public static Point getRelativeMousePos(){
		Rectangle bounds = Engine.getAbsoluteCanvasBounds();
		Point realMousePos = MouseInfo.getPointerInfo().getLocation();
		realMousePos.x -= bounds.x;
		realMousePos.y -= bounds.y;
		return realMousePos;
	}
}
