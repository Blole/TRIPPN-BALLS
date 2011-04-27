package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luaj.vm2.LuaValue;

import engine.Engine;
import engine.LuaLoader;
import engine.ModelLoader;

public class Entity {
	public static final List<Entity> all = new ArrayList<Entity>();
	public static final List<Entity> hasGravity = new ArrayList<Entity>();
	public HashMap<String, LuaValue> userVars;
	private List<Entity> children = new ArrayList<Entity>();
	
	private boolean hasOwnGravity = false;
	private boolean isFrozen = false;
	private boolean isAnon;
	private boolean isSphere;
	
	public final Vector speed = new Vector(0,0,0);
	public final Vector pos = new Vector(0,0,0);
	
	private float boundingRadius = 1;
	private float mass = 1;
	private VBOinfo vbo;
	private Events events;
	private Entity father;
	
	public Entity (String doodadName, boolean isAnon) {
		if (doodadName.equals("LUA_DEBUG"))
			return;
		this.isAnon = isAnon;
		loadEverything(doodadName);
		if (!isAnon)
			Entity.all.add(this);
	}
	
	@SuppressWarnings("unchecked") //.clone() will give an HashMap
	private void loadEverything(String doodadName) {
		vbo = ModelLoader.loadStatic(doodadName);
		events = LuaLoader.load(doodadName);
		this.userVars = (HashMap<String, LuaValue>) events.userVars.clone();
	}
	
	public void render() {
		Engine.gl.glPushMatrix();
		Engine.gl.glTranslatef(pos.x, pos.y, pos.z);
		Engine.drawVBO(vbo);
		for (Entity child : children)
			child.render();
		Engine.gl.glPopMatrix();
	}
	
	public void move() {
		pos.addSelf(speed);
	}
	public void checkForCollision(Entity other) {
		Vector vectorBetween = pos.vectorTo(other.pos);
		if (vectorBetween.isNull())
			vectorBetween.x=0.00001f;
		float distanceBetween = (vectorBetween.abs() - (boundingRadius+other.getBoundingRadius()))/2;
		if (distanceBetween < -0.0001) {
			Vector toOther = speed.multiply(mass).proj(vectorBetween);
			Vector toMe = other.speed.multiply(mass).proj(vectorBetween);
			pos.addSelf(vectorBetween.setLength(distanceBetween));
			other.pos.addSelf(vectorBetween.setLength(-distanceBetween));
			
			speed.addSelf(toMe);
			other.speed.subtractSelf(toMe);
			other.speed.addSelf(toOther);
			speed.subtractSelf(toOther);
			
			LuaValue e1 = LuaLoader.toUserdata(this);
			LuaValue e2 = LuaLoader.toUserdata(other);
			events.onCollide.call(e1, e2);
			other.events.onCollide.call(e2, e1);
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
	
	public void attach(Entity anonEntity) {
		if (!anonEntity.isAnon())
			throw new RuntimeException(String.format(
					"Entity %s is not anonymous and can therefore not be attached to %s",
					anonEntity, this));
		children.add(anonEntity);
		anonEntity.imYourFather(this);
	}
	
	private void imYourFather(Entity father) {
		this.father = father;
	}
	/*
	 * Set information booleans
	 */
	public Entity setOwnGravity(boolean hasOwnGravity) {
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
		return this;
	}
	public Entity setFreeze(boolean freeze) {
		isFrozen = freeze;
		return this;
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
	public boolean isAnon() {
		return isAnon;
	}
	public boolean hasOwnGravity() {
		return hasOwnGravity;
	}
}