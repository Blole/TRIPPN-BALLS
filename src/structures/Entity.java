package structures;

import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.ModelLoader;

public class Entity {
	public static final List<Entity> all = new ArrayList<Entity>();
	public static final List<Entity> hasGravity = new ArrayList<Entity>();
	private boolean hasOwnGravity = false;
	private boolean isFrozen = false;
	private boolean isSphere;
	
	public final Vector speed = new Vector(0,0,0);
	public final Vector pos = new Vector(0,0,0);
	
	private float boundingRadius = 1;
	private float mass = 1;
	private VBOinfo vbo;
	
	public Entity (String entityName) {
		vbo = ModelLoader.loadStatic(entityName);
		Entity.all.add(this);
	}
	
	public void render() {
		Engine.gl.glLoadIdentity();
		Engine.gl.glTranslatef(pos.x, pos.y, pos.z);
		Engine.drawVBO(vbo);
	}
	
	public void move() {
		pos.addSelf(speed);
	}
	public void checkForCollision(Entity other) {
		Vector vectorBetween = pos.vectorTo(other.pos);
		if (vectorBetween.isNull())
			vectorBetween.x=0.00001f;
		float distanceBetween = (vectorBetween.abs() - (boundingRadius+other.getBoundingRadius()))/2;
		if (distanceBetween < 0) {
			Vector toOther = speed.multiply(mass).proj(vectorBetween);
			Vector toMe = other.speed.multiply(mass).proj(vectorBetween);
			pos.addSelf(vectorBetween.setLength(distanceBetween));
			other.pos.addSelf(vectorBetween.setLength(-distanceBetween));
			
			speed.addSelf(toMe);
			other.speed.subtractSelf(toMe);
			other.speed.addSelf(toOther);
			speed.subtractSelf(toOther);
		}
	}
	public void attract(Entity other) {
		Vector vectorBetween = pos.vectorTo(other.pos);
		if (vectorBetween.abs() >= (boundingRadius+other.getBoundingRadius())) {
			float xDiff = other.pos.x-pos.x;
			float yDiff = other.pos.y-pos.y;
			float zDiff = other.pos.z-pos.z;
			float distance = vectorBetween.abs();
			float attraction = 1f/(distance*distance);
			
			speed.x += xDiff*attraction;
			speed.y += yDiff*attraction;
			speed.z += zDiff*attraction;
			other.speed.x -= xDiff*attraction;
			other.speed.y -= yDiff*attraction;
			other.speed.z -= zDiff*attraction;
		}
	}
	/*
	 * Set information booleans
	 */
	public void setHasOwnGravity(boolean hasOwnGravity) {
		if (hasOwnGravity) {
			if (!Entity.hasGravity.contains(this)) {
				Entity.hasGravity.add(this);
				this.hasOwnGravity = true;
			}
		}
		else {
			Entity.hasGravity.remove(this);
			this.hasOwnGravity = false;
		}
	}
	public void freeze(boolean freeze) {
		isFrozen = freeze;
	}
	
	
	/*
	 * Gets
	 */
	public float getMass() {
		return mass;
	}
	public float getBoundingRadius() {
		return boundingRadius;
	}
	
	/*
	 * Information booleans
	 */
	public boolean isFrozen() {
		return isFrozen;
	}
	public boolean hasOwnGravity() {
		return hasOwnGravity;
	}
}