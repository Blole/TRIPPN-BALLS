package models;

import interfaces.BoundingSphereCollidable;
import interfaces.Gravitatable;
import interfaces.Moveable;
import interfaces.Render;
import interfaces.SphereAttachable;

import java.util.ArrayList;

import engine.Engine;
import engine.ModelLoader;

import structures.VBOinfo;
import structures.Vector;



public class Sphere implements Render, Moveable, BoundingSphereCollidable, Gravitatable {
//	private float[][] track;
//	private boolean enableTrack = false;
//	private float[] trackColor;
//	private int trackIndex;
	
	private final Vector pos = new Vector(0,0,0);
	private final Vector speed = new Vector(0,0,0);
	private float mass;
	
	private boolean affectedByGravity = false;
	
	private float radius;
	
	public boolean markedForRemoval = false;
	public ArrayList<SphereAttachable> attachables;
//	public float[][][] polygons;
	public float yaw;
	public float pitch;
	public float roll;
	private VBOinfo vbo;
	
	public Sphere(Vector pos) {
		this(pos, 1, 20, 20);
	}
	public Sphere(Vector pos, float radius, int longitude, int latitude) {
		this(pos, 0, 0, 0, radius, longitude, latitude);
	}
	public Sphere(Vector pos, float pitch, float yaw, float roll, float radius, int longitude, int latitude) {
		setPos(pos);
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.setRadius(radius);
		this.vbo = ModelLoader.loadStatic("sphere");
		mass = radius;
		attachables = new ArrayList<SphereAttachable>();
	}
	
	public void attach(SphereAttachable attachable) {
		attachables.add(attachable);
		attachable.sayHello(this);
	}
	
	public void enableTrack(float[] color, int length) {
//		if (color != null) {
//			trackColor = color;
//			track = new float[length][];
//			for (int i=0; i<track.length; i++)
//				track[i] = new float[]{pos.x, pos.y, pos.z};
//			enableTrack = true;
//		}
//		else {
//			enableTrack = false;
//			track = null;
//		}
	}
	public void clearTrack() {
//		if (enableTrack)
//			for (int i=track.length-1; i>=0; i--)
//				track[i] = new float[]{pos.x, pos.y, pos.z};
	}
	@Override
	public void move() {
		pos.addSelf(getSpeed());
		for (SphereAttachable attachable : attachables)
			attachable.move();
	}

	@Override
	public void render() {
		Engine.gl.glLoadIdentity();
		Engine.gl.glTranslatef(pos.x, pos.y, pos.z);
		
		Engine.drawVBO(vbo);
//		Engine.gl.glBegin(GL2.GL_QUADS);
//		for (int i=0; i<polygons.length; i+=1) {
//			for (int j=0; j<8;) {
//				Engine.gl.glColor3fv (polygons[i][j++], 0);
//				Engine.gl.glVertex3fv(polygons[i][j++], 0);
//			}
//		}
//		Engine.gl.glEnd();
		for (SphereAttachable attachable : attachables)
			;//attachable.render();
		
//		if (enableTrack) {
//			Engine.gl.glLoadIdentity();
//			Engine.gl.glColor3f(trackColor[0], trackColor[1], trackColor[2]);
//			Engine.gl.glBegin(GL2.GL_LINE_STRIP);
//			for (int i=trackIndex+1; i<track.length; i++)
//				Engine.gl.glVertex3fv(track[i], 0);
//			for (int i=0; i<trackIndex; i++)
//				Engine.gl.glVertex3fv(track[i], 0);
//			Engine.gl.glEnd();
//
//			Engine.gl.glFlush();
//			
//			if (getSpeed().abs()!=0) {
//				track[trackIndex][0] = pos.x;
//				track[trackIndex][1] = pos.y;
//				track[trackIndex][2] = pos.z;
//				trackIndex++;
//				trackIndex %= track.length;
//			}
//		}
	}
	
	@Override
	public void attract(Gravitatable other) {
		Vector vectorBetween = pos.vectorTo(other.getPos());
		if (vectorBetween.abs() >= (radius+other.getRadius())) {
			float xDiff = other.getPos().x-pos.x;
			float yDiff = other.getPos().y-pos.y;
			float zDiff = other.getPos().z-pos.z;
			float distance = vectorBetween.abs();
			float attraction = 1f/(distance*distance);
			
			getSpeed().x += xDiff*attraction;
			getSpeed().y += yDiff*attraction;
			getSpeed().z += zDiff*attraction;
			other.getSpeed().x -= xDiff*attraction;
			other.getSpeed().y -= yDiff*attraction;
			other.getSpeed().z -= zDiff*attraction;
		}
	}
	
	@Override
	public void checkForCollision(BoundingSphereCollidable other) {
		Vector vectorBetween = pos.vectorTo(other.getPos());
		if (vectorBetween.isNull())
			vectorBetween.x=0.00001f;
		float distanceBetween = (vectorBetween.abs() - (radius+other.getRadius()))/2;
		if (distanceBetween < 0) {
			Vector toOther = getMomentum().proj(vectorBetween);
			Vector toMe = other.getMomentum().proj(vectorBetween);
			pos.addSelf(vectorBetween.setLength(distanceBetween));
			other.getPos().addSelf(vectorBetween.setLength(-distanceBetween));
			
			getSpeed().addSelf(toMe);
			other.getSpeed().subtractSelf(toMe);
			other.getSpeed().addSelf(toOther);
			getSpeed().subtractSelf(toOther);
		}
	}
	
	public String toString() {
		return "Sphere pos:"+pos+" speed:"+getSpeed();
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	@Override
	public float getRadius() {
		return radius;
	}
	@Override
	public Vector getPos() {
		return pos;
	}
	@Override
	public void setPos(Vector pos) {
		this.pos.x = pos.x;
		this.pos.y = pos.y;
		this.pos.z = pos.z;
	}
	@Override
	public Vector getMomentum() {
		return getSpeed().multiply(mass);
	}
	@Override
	public Vector getSpeed() {
		return speed;
	}
	@Override
	public float getMass() {
		return mass;
	}
	@Override
	public void setAffectedByGravity(boolean affectedByGravity) {
		this.affectedByGravity = affectedByGravity;
	}
	@Override
	public boolean isAffectedByGravity() {
		return affectedByGravity;
	}
	public void setSpeed(Vector speed) {
		this.speed.x = speed.x;
		this.speed.y = speed.y;
		this.speed.z = speed.z;
	}
}
