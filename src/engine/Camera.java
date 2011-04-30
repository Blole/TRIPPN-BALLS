package engine;
import java.awt.Point;

import javax.media.opengl.GL2;

import structures.Vector;


public class Camera {
	public static float pitch;
	public static float yaw;
	public static float roll;
	public static Vector target;
	private static float zoom;
	private static Mode mode;
	private static float aspect;
	private static float fovy;
	private static float realFOV;
	public static final Vector pos = new Vector(0,0,0);
	
	public static enum Mode {
		FREELOOK, TARGET
	}
	
	private Camera() {}
	
	public static void init() {
		zoom = Settings.zoomInit;
		setFOV(Settings.FOV);
	}
	
	public static void setPitch(float pitch) {
		Camera.pitch = pitch;
	}
	
	public static void move() {
		switch (mode) {
		case FREELOOK:
			break;
		case TARGET:
			break;
		}
	}
	public static void setTarget(Vector target) {
	    Camera.target = target;
	    mode = Mode.TARGET;
	}
	public static void setMode(Mode mode) {
		Camera.mode = mode;
	}
	/**
	 * Set FOV (Field Of View) in degrees.
	 * @param fov
	 */
	public static void setFOV(float fov) {
		Camera.fovy = fov;
		realFOV = (float) Math.tan(fovy/360*Math.PI);
	}
	/**
	 * @return The same FOV as set, in degrees.
	 */
	public static float getFOV() {
		return fovy;
	}
	public static void updateTurn(Point mouseMovement) {
		switch(mode){
		case TARGET:
			yaw	  -= (float)mouseMovement.x*Settings.invertTargetX*Settings.mouseSense;
			pitch += (float)mouseMovement.y*Settings.invertTargetY*Settings.mouseSense;
			break;
		case FREELOOK:
			yaw	  += (float)mouseMovement.x*Settings.invertFreelookX*Settings.mouseSense;
			pitch += (float)mouseMovement.y*Settings.invertFreelookY*Settings.mouseSense;
			break;
		}
		if (pitch > 90)
			pitch = 90;
		else if (pitch < -90)
			pitch = -90;
	}
	public static void zoom(float rotation) {
		zoom += rotation;
		if (zoom < Settings.zoomMin)
			zoom = Settings.zoomMin;
		else if (zoom > Settings.zoomMax)
			zoom = Settings.zoomMax;
	}
	public static void setUpCameraLook() {
		Engine.gl.glMatrixMode(GL2.GL_PROJECTION);
		Engine.gl.glLoadIdentity();
		Engine.gl.glFrustumf(-realFOV, realFOV, -realFOV*aspect, realFOV*aspect, 1, 10000);
		switch (mode) {
		case FREELOOK:
			Engine.gl.glRotatef(pitch, 1, 0, 0);
			Engine.gl.glRotatef(yaw, 0, 1, 0);
			Engine.gl.glTranslatef(-pos.x, -pos.y, -pos.z);
			break;
		case TARGET:
			Engine.gl.glTranslatef(0, 0, -zoom);
			Engine.gl.glRotatef(pitch, 1, 0, 0);
			Engine.gl.glRotatef(yaw, 0, 1, 0);
			Engine.gl.glTranslatef(-target.x, -target.y, -target.z);
			break;
		}
	}
	/**
	 * Set aspect of screen or window. The aspect itself is calculated
	 * from the given width and height.
	 * @param width
	 * @param height 
	 */
	public static void setAspect(int width, int height) {
		Camera.aspect = (float)height/(float)width;
	}
	public static float getAspect() {
		return aspect;
	}
	public static Mode getMode() {
		return mode;
	}
	

    
    public static void moveForward(float magnitude)
    {	
        // Spherical coordinates maths
    	
    	Vector movement = new Vector(magnitude,-magnitude,-magnitude).turn(pitch, yaw);
        pos.addSelf(movement);

    }

    public static void strafe(float magnitude)
    {
    	Vector movement = new Vector(magnitude,-magnitude,-magnitude).turn(0,( yaw-90));
    	pos.addSelf(movement);
    }
}


