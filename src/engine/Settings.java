package engine;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Settings {
	public static final String doodadDir = "doodads/";
	public static final String luaEventFile = "/events.lua";
	public static final String luaInfoFile = "/info.lua";
	
	private static final String settingsFileName = "settings.ini";
	private static final String constantsFileName = "constants.ini";
	private static final String keyBindSection = "KeyBinds";
	private static final String mouseSection = "Mouse";
	private static final String cameraSection = "Camera";
	private static final String worldSection = "World";
	
	private static Wini constantsIni;
	private static Wini settingsIni;
	
	public static int forward	= KeyEvent.VK_W;
	public static int backward	= KeyEvent.VK_S;
	public static int strafeL	= KeyEvent.VK_A;
	public static int strafeR	= KeyEvent.VK_D;
	public static int up		= KeyEvent.VK_SPACE;
	public static int down		= KeyEvent.VK_SHIFT;
	public static int menu 		= KeyEvent.VK_ESCAPE;
	public static float mouseSense = 0.2f;
	public static float zoomStep = 5;
	public static float zoomInit = 100;
	public static float zoomMax = 1000;
	public static float zoomMin = 2.1f;
	public static String worldMap = "world.bmp";
	public static float FOV;
	public static int invertTargetX;
	public static int invertTargetY;
	public static int invertFreelookX;
	public static int invertFreelookY;
	public static float gravitationalConstant;
	
	public static void loadSettings() {
		try {
			constantsIni = new Wini(new File(constantsFileName));
			settingsIni = new Wini(new File(settingsFileName));
			
			forward		= loadKeyBind("forward");
			backward	= loadKeyBind("backward");
			strafeL		= loadKeyBind("strafeL");
			strafeR		= loadKeyBind("strafeR");
			up			= loadKeyBind("up");
			down		= loadKeyBind("down");
			menu		= loadKeyBind("menu");
			mouseSense	= settingsIni.get(mouseSection, "mouseSense", float.class);
			System.out.println(settingsIni.get(cameraSection, "mouseSense", float.class));
			zoomStep	= settingsIni.get(cameraSection, "zoomStep", float.class);
			zoomInit	= settingsIni.get(cameraSection, "zoomInit", float.class);
			zoomMin		= settingsIni.get(cameraSection, "zoomMin", float.class);
			zoomMax		= settingsIni.get(cameraSection, "zoomMax", float.class);
			worldMap	= settingsIni.get(worldSection,  "map", String.class);
			invertTargetX	= settingsIni.get(cameraSection, "invertTargetX", boolean.class)?-1:1;
			invertTargetY	= settingsIni.get(cameraSection, "invertTargetY", boolean.class)?1:-1;
			invertFreelookX	= settingsIni.get(cameraSection, "invertFreelookX", boolean.class)?-1:1;
			invertFreelookY	= settingsIni.get(cameraSection, "invertFreelookY", boolean.class)?-1:1;
			FOV				= settingsIni.get(cameraSection, "FOV", float.class);
			gravitationalConstant = settingsIni.get(worldSection, "gravitationalConstant", float.class);
			
		} catch (InvalidFileFormatException e) {
			System.out.printf("Settings file '%s' not found.. sort of. Default key binds loaded.", settingsFileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.printf("Settings file '%s' not found. Default key binds loaded.", settingsFileName);
			e.printStackTrace();
		}
	}

	private static int loadKeyBind(String key) {
		return constantsIni.get("KeyEvent", "VK_"+settingsIni.get(keyBindSection, key , String.class), int.class);
	}
}
