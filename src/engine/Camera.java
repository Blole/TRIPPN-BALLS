package engine;
import java.awt.Point;

import javax.media.opengl.GL2;

import structures.Vector;


public class Camera {
	public float pitch;
	public float yaw;
	public float roll;
	public Vector target;
	public Vector offset;
	private float zoom;
	private Mode mode;
	private float aspect;
	private float fovy;
	private float realFOV;
	public final Vector pos = new Vector(0,0,0);
	
	public static enum Mode {
		FREELOOK, LOCKED_FOLLOW, TARGET
	}
	
	public Camera() {
		this(20, 5, 0);
	}
	public Camera(float pitch, float yaw, float roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		mode = Mode.FREELOOK;
		offset = new Vector(-3,2,6);
		zoom = Settings.zoomInit;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void move() {
		switch (mode) {
		case FREELOOK:
			break;
		case LOCKED_FOLLOW:
			/*Vector up;
			if (target.isAffectedByGravity())
				up = target.getGravity().setLength(-2);
			else
				up = new Vector(0,1,0);
			Vector current = target.pos.vectorTo(pos);
			pos = target.pos.add(up).add(current.subtract(current.proj(up)).shortenIfLonger(5));*/
			break;
		case TARGET:
			break;
		}
	}
	public void setTarget(Vector target) {
	    this.target = target;
	    mode = Mode.TARGET;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	/**
	 * Set FOV (Field Of View) in degrees.
	 * @param fov
	 */
	public void setFOV(float fov) {
		this.fovy = fov;
		realFOV = (float) Math.tan(fovy/360*Math.PI);
	}
	/**
	 * @return The same FOV as set, in degrees.
	 */
	public float getFOV() {
		return fovy;
	}
	public void updateTurn(Point mouseMovement) {
		yaw	  -= (float)mouseMovement.x*Settings.mouseSense;
		pitch -= (float)mouseMovement.y*Settings.mouseSense;
		if (pitch > 90)
			pitch = 90;
		else if (pitch < -90)
			pitch = -90;
	}
	public void zoom(float rotation) {
		zoom += rotation;
		if (zoom < Settings.zoomMin)
			zoom = Settings.zoomMin;
		else if (zoom > Settings.zoomMax)
			zoom = Settings.zoomMax;
	}
	public void setUpCameraLook() {
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
	public void setAspect(int width, int height) {
		this.aspect = (float)height/(float)width;
	}
	public float getAspect() {
		return aspect;
	}
	public Mode getMode() {
		return mode;
	}
}
