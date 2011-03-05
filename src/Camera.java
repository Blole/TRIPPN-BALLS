import java.awt.Point;

import javax.media.opengl.GL2;


public class Camera extends Base {
	private float mouseSensitivity = 5.0f;
	public Model target;
	public Vector offset;
	private float zoom = 5;
	private float zoomMin = 1.0f;
	private float zoomMax = 1000.0f;
	private Mode mode;
	private float aspect;
	private float fovy;
	private float realFOV;
	public static enum Mode {
		FREELOOK, LOCKED_FOLLOW, TARGET
	}
	
	public Camera(Vector pos) {
		this(pos, 20, 5, 0);
	}
	public Camera(Vector pos, float pitch, float yaw, float roll) {
		super(pos, pitch, yaw, roll);
		mode = Mode.FREELOOK;
		offset = new Vector(-3,2,6);
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void move() {
		switch (mode) {
		case FREELOOK:
			break;
		case LOCKED_FOLLOW:
			Vector up;
			if (target.getAffectedByGravity())
				up = target.getGravity().setLength(-2);
			else
				up = new Vector(0,1,0);
			Vector current = target.pos.vectorTo(pos);
			pos = target.pos.add(up).add(current.subtract(current.proj(up)).shortenIfLonger(5));
			break;
		case TARGET:
			break;
		}
	}
	public void setTarget(Model target) {
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
		yaw	  -= mouseMovement.x/mouseSensitivity;
		pitch -= mouseMovement.y/mouseSensitivity;
		if (pitch > 90)
			pitch = 90;
		else if (pitch < -90)
			pitch = -90;
	}
	public void zoom(float rotation) {
		zoom += rotation;
		if (zoom < zoomMin)
			zoom = zoomMin;
		else if (zoom > zoomMax)
			zoom = zoomMax;
	}
	public void setUpCameraLook(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-realFOV, realFOV, -realFOV*aspect, realFOV*aspect, 1, 10000);
		switch (mode) {
		case FREELOOK:
			gl.glRotatef(pitch, 1, 0, 0);
			gl.glRotatef(yaw, 0, 1, 0);
			gl.glTranslatef(-pos.x, -pos.y, -pos.z);
			break;
		case TARGET:
			gl.glTranslatef(0, 0, -zoom);
			gl.glRotatef(pitch, 1, 0, 0);
			gl.glRotatef(yaw, 0, 1, 0);
			gl.glTranslatef(-target.pos.x, -target.pos.y, -target.pos.z);
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
