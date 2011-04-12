package interfaces;

import structures.Vector;

public interface Moveable extends ChangesOverTime, Position {
	public float getMass();
	
	public Vector getMomentum();
	
	public Vector getSpeed();
	public void setSpeed(Vector pos);
}
