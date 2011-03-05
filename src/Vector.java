public class Vector {
	public float x;
	public float y;
	public float z;
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector add(Vector p) {
		return new Vector(x+p.x, y+p.y, z+p.z);
	}
	public void addSelf(Vector p) {
		x += p.x;
		y += p.y;
		z += p.z;
	}
	public Vector subtract(Vector p) {
		return new Vector(x-p.x, y-p.y, z-p.z);
	}
	
	public float abs() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public float distanceTo(Vector v) {
		float a = v.x-x;
		float b = v.y-y;
		float c = v.z-z;
		return (float)Math.sqrt(a*a+b*b+c*c);
	}

	public Vector multiply(float f) {
		return new Vector(x*f, y*f, z*f);
	}
	public void multiplySelf(float f) {
		x *= f;
		y *= f;
		z *= f;
	}
	
	public Vector vectorTo(Vector p) {
		return new Vector(p.x-x, p.y-y, p.z-z);
	}
	public void vectorToSelf(Vector p) {
		x = p.x-x;
		y = p.y-y;
		z = p.z-z;
	}

	public float dot(Vector v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vector proj(Vector v) {
		return v.multiply( dot(v) / v.dot(v) );
	}
	
	public Vector turn(float pitch, float yaw) {
		System.out.println(pitch);
		System.out.println(yaw);
		pitch *= (Math.PI/180);
		yaw   *= (Math.PI/180);
		System.out.println(pitch);
		System.out.println(yaw);
		return new Vector(x*(float)Math.cos(pitch)*(float)Math.sin(yaw),y*(float)Math.sin(pitch), z*(float)Math.cos(pitch)*(float)Math.cos(yaw));
	}
	
	public Vector unit() {
		return multiply(1/abs());
	}
	
	public Vector setLength(float length) {
		return multiply(length/abs());
	}
	public void setLengthSelf(float length) {
		float abs = abs();
		x *= length/abs;
		z *= length/abs;
		y *= length/abs;
	}
	public Vector shortenIfLonger(float length) {
		if (abs() > length)
			return setLength(length);
		else
			return this;
	}

	public boolean isMultiple(Vector v) {
		return unit().equals(v.unit());
	}
	
	public String toString() {
		return "("+x+", "+y+", "+z+")";
	}
}
