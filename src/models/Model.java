package models;

import javax.media.opengl.GL2;

public abstract class Model extends Base {
	public Vector speed = new Vector(0,0,0);
	protected Vector gravity = new Vector (0,0,0);
	protected boolean affectedByGravity = false;
	public Vector roll;
	public float[][][] polygons;
	
	public Model (Vector pos) {
		this(pos, 0, 0, 0);
	}
	public Model (Vector pos, float pitch, float yaw, float roll) {
		super(pos, pitch, yaw, roll);
	}
	
	public Vector getGravity() {
		return gravity ;
	}
	
	public void move() {}

	public void render(GL2 gl, int optimization) {
		System.out.println("lol?");
	}
	public void setAffectedByGravity(boolean value) {
		affectedByGravity = value;
	}
	public boolean affectedByGravity() {
		return affectedByGravity;
	}
}
