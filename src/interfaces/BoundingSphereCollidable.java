package interfaces;


public interface BoundingSphereCollidable extends Moveable {
	public void checkForCollision(BoundingSphereCollidable other);
	public float getRadius();
}
