public class Base {
	public Vector pos;
	public float pitch;
	public float yaw;
	public float roll;
	
	public Base (Vector pos) {
		this(pos, 0, 0, 0);
	}
	public Base (Vector pos, float pitch, float yaw, float roll) {
		this.pos = pos;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
}
