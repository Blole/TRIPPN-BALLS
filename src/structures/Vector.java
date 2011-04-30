package structures;
public class Vector {
	public float x;
	public float y;
	public float z;
	
	/**
	 * Creates a vector, takes 3 coordinates as floats.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate
	 */
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a vector from a float vector containing 3 values.
	 * Throws IllegalArgumentException at faulty input.
	 * @param f Vector of 3 floats
	 */
	public Vector(float[] f){
		
		if(f.length > 3 || f.length < 3)throw new IllegalArgumentException();
		
		this.x = f[0];
		this.y = f[1];
		this.z = f[2];
	}
	
	public Vector add(Vector v) {
		return new Vector(x+v.x, y+v.y, z+v.z);
	}
	public void addSelf(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	public Vector subtract(Vector v) {
		return new Vector(x-v.x, y-v.y, z-v.z);
	}
	public void subtractSelf(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	public float abs() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	
	public Vector scalarAdd(float f){
		x+=f;
		y+=f;
		z+=f;
		return this;
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
	public Vector multiply(Vector v){
		return new Vector(x*v.x, y*v.y, z*v.z);
	}
	
	public void multiplySelf(float f) {
		x *= f;
		y *= f;
		z *= f;
	}
	public void multiplySelf(Vector v){
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}
	
	public Vector divide(Vector v){
		return new Vector(x/v.x,	y/v.y,	z/v.z);
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
	
	public Vector cross(Vector v) {
		return new Vector(y*v.z-z*v.y,z*v.x-x*v.z,x*v.y-y*v.x);
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
	
	public static Vector newTurnedUnitVector(float pitch, float yaw) {
		pitch *= (Math.PI/180);
		yaw   *= (Math.PI/180);
		return new Vector(
				(float)Math.cos(pitch)*(float)Math.cos(yaw),
				(float)Math.sin(pitch),
				(float)Math.cos(pitch)*(float)Math.sin(yaw)
			);
	}
	public Vector unit() {
		return multiply(1/abs());
	}
	
	public Vector setLength(float length) {
		return multiply(length/abs());
	}
	public void setLengthSelf(float length) {
		length /= abs();
		x *= length;
		z *= length;
		y *= length;
	}
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
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
	
	public boolean isZero() {
		return x==0 && y==0 && z==0;
	}
	
	public float squaredDistanceTo(Vector v){
		float a = v.x-x;
		float b = v.y-y;
		float c = v.z-z;
		return (a*a+b*b+c*c);
	}
	
	public String toString() {
		return "("+x+", "+y+", "+z+")";
	}
}
