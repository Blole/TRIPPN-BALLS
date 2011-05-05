package engine;
import java.awt.Point;

import javax.media.opengl.GL2;

import structures.Vector;

/**
 * 
 * The camera class represents the in game camera.
 * 
 * @author Björn Holm and Jacob Norlin Andersson.
 *
 */
public class Camera {
	
	/**
	 * The cameras pitch.
	 */
	public static float pitch;
	
	/**
	 * The cameras yaw.
	 */
	public static float yaw;
	
	/**
	 * The cameras roll.
	 */
	public static float roll;
	
	/**
	 * The cameras current focused target.
	 */
	public static Vector target;
	
	/**
	 * The cameras zoom.
	 */
	private static float zoom;
	
	/**
	 * The cameras viewing mode, i.e FREELOOK or TARGET.
	 */
	private static Mode mode;
	
	/**
	 * The cameras aspect ratio.
	 */
	private static float aspect;
	
	/**
	 * The cameras field of view.
	 */
	private static float fovy;
	private static float realFOV;
	
	/**
	 * The cameras current position.
	 */
	public static final Vector pos = new Vector(0,0,0);
	
	public static enum Mode {
		FREELOOK, TARGET
	}
	
	/**
	 * Contructs a Camera.
	 */
	private Camera() {}
	
	/**
	 * Initializes the camera with settings given in Settings.ini .
	 */
	public static void init() {
		zoom = Settings.zoomInit;
		setFOV(Settings.FOV);
	}
	
	/**
	 * Sets the pitch of the camera.
	 * @param pitch New pitch;
	 */
	public static void setPitch(float pitch) {
		Camera.pitch = pitch;
	}
	
	/**
	 * Sets the target which the camera focuses.
	 * @param target New target.
	 */
	public static void setTarget(Vector target) {
	    Camera.target = target;
	    mode = Mode.TARGET;
	}
	
	/**
	 * Sets the viewing mode of the camera.
	 * @param mode New mode, FREELOOK or TARGET.
	 */
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
	
	/**
	 * Updates the cameras view in relation to the movement of the mouse.
	 * @param mouseMovement How the mouse has moved.
	 */
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
	
	/**
	 * Changes the zoom of the camera.
	 * @param rotation New zoom.
	 */
	public static void zoom(float rotation) {
		zoom += rotation;
		if (zoom < Settings.zoomMin)
			zoom = Settings.zoomMin;
		else if (zoom > Settings.zoomMax)
			zoom = Settings.zoomMax;
	}
	
	/**
	 * Sets up the camera in openGL creating the frustum matrix and allows
	 * for the user to actually see.
	 */
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
	
	/**
	 * @return The current aspect.
	 */
	public static float getAspect() {
		return aspect;
	}
	
	/**
	 * @return The current viewing mode.
	 */
	public static Mode getMode() {
		return mode;
	}
	

	/**
	 * Calculates and moves the camera in relation to its own position and viewing
	 * point, so that it can be moved in a first person perspective.
	 * @param magnitude Distance moved.
	 */
    public static void moveForward(float magnitude)
    {	
        // Spherical coordinates maths
    	
    	Vector movement = new Vector(magnitude,-magnitude,-magnitude).turn(pitch, yaw);
        pos.addSelf(movement);

    }

    /**
     * Moves the camera sideways.
     * @param magnitude Distance moved.
     */
    public static void strafe(float magnitude)
    {
    	Vector movement = new Vector(magnitude,-magnitude,-magnitude).turn(0,( yaw-90));
    	pos.addSelf(movement);
    }
}


