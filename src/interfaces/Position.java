package interfaces;

import structures.Vector;

public interface Position {
	public float yaw = 2;
	public Vector getPos();
	//public float[] getYawPitchRoll();
	//public void setYawPitchRoll(float yaw, float pitch, float roll);
	public void setPos(Vector pos);
}
