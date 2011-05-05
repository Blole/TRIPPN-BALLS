package structures;

/**
 * A representation of the mathematical concept of a Vector as well as a representation
 * of a point in 3-space. Contains most of the necessary operations available to Vectors
 * and points in 3-space. It contains 3 floats, x,y,z, which represent its coordinates 
 * in 3-space.
 * 
 * Every method with a self in its signature will change the 
 * values of the vector its called from and not return a new vector of the result of the
 * method call.
 * @author Jacob Norlin Andersson And Björn Holm.
 *
 */
public class Vector {
	
	/**
	 * x-coordinate.
	 */
	public float x;
	
	/**
	 * y-coordinate.
	 */
	public float y;
	
	/**
	 * z-coordinate.
	 */
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
	
	/**
	 * Adds a vector v to this vector and returns a
	 * new vector which is the sum of the vectors.
	 * @param v Vector to be added.
	 * @return Old vector plus new vector.
	 */
	public Vector add(Vector v) {
		return new Vector(x+v.x, y+v.y, z+v.z);
	}
	
	/**
	 * Adds a vector v to the own vector.
	 * @param v Vector to be added.
	 */
	public void addSelf(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	/**
	 * Subtracts a vector v from this vector and
	 * returns a new vector with the result.
	 * @param v Vector that is subtracted.
	 * @return The difference between the vectors.
	 */
	public Vector subtract(Vector v) {
		return new Vector(x-v.x, y-v.y, z-v.z);
	}
	
	/**
	 * Subtracts a vector v from this vector.
	 * @param v Vector that is subtracted.
	 */
	public void subtractSelf(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	/**
	 * Returns the absolute value of this vector, uses square root.
	 * @return Absolute value of the vector.
	 */
	public float abs() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	/**
	 * Adds a scalar to this vector and returns a new
	 * vector with the result.
	 * @param f The scalar added.
	 * @return New vector that is the result of the addition.
	 */
	public Vector scalarAdd(float f){
		x+=f;
		y+=f;
		z+=f;
		return this;
	}
	
	/**
	 * Calculates and returns the distance from this vector to,
	 * to the given vector v. Uses square root.
	 * @param v The vector/point to check distance to.
	 * @return Distance to v.
	 */
	public float distanceTo(Vector v) {
		float a = v.x-x;
		float b = v.y-y;
		float c = v.z-z;
		return (float)Math.sqrt(a*a+b*b+c*c);
	}

	/**
	 * Multiplies this vector with a scalar f and returns
	 * the result.
	 * @param f Float multiplier.
	 * @return New vector of the result.
	 */
	public Vector multiply(float f) {
		return new Vector(x*f, y*f, z*f);
	}
	
	/**
	 * Multiplies this vector with a vector v.
	 * @param v Vector to be multiplied with.
	 * @return The resulting vector.
	 */
	public Vector multiply(Vector v){
		return new Vector(x*v.x, y*v.y, z*v.z);
	}
	
	/**
	 * Multiplies this vector with a float f.
	 * @param f Float multiplier.
	 */
	public void multiplySelf(float f) {
		x *= f;
		y *= f;
		z *= f;
	}
	
	/**
	 * Multiplies this vector with a vector v.
	 * @param v Vector multiplier.
	 */
	public void multiplySelf(Vector v){
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}
	
	/**
	 * Divides this vector with a vector v.
	 * @param v Vector divider.
	 * @return The resulting vector.
	 */
	public Vector divide(Vector v){
		return new Vector(x/v.x,	y/v.y,	z/v.z);
	}
	
	/**
	 * Calculates and returns the Vector between the this vector
	 * and p.
	 * @param p Second point.
	 * @return Vector between this vector and p.
	 */
	public Vector vectorTo(Vector p) {
		return new Vector(p.x-x, p.y-y, p.z-z);
	}
	
	/**
	 * Calculates the vector to point p from this vector.
	 * Changes the value of the vector called to to the result.
	 * @param p Second point.
	 */
	public void vectorToSelf(Vector p) {
		x = p.x-x;
		y = p.y-y;
		z = p.z-z;
	}

	
	/**
	 * Calculates and returns the dot product between this vector 
	 * and the vector v.
	 * @param v Second vector.
	 * @return Dot product,float, between this vector and v.
	 */
	public float dot(Vector v) {
		return (x*v.x) + (y*v.y) + (z*v.z);
	}
	
	/**
	 * Calculates the and returns the cross product between this vector
	 * and the vector v.
	 * @param v Second vector.
	 * @return The resulting cross product vector.
	 */
	public Vector cross(Vector v) {
		return new Vector(y*v.z-z*v.y,z*v.x-x*v.z,x*v.y-y*v.x);
	}
	
	/**
	 * Calculates and returns the projection of this vector on the 
	 * the vector v.
	 * @param v Vector to be projected on.
	 * @return The resulting projection vector.
	 */
	public Vector proj(Vector v) {
		return v.multiply( dot(v) / v.dot(v) );
	}
	
	/**
	 * Turns this vector by a pitch and yaw angle and returns the resulting
	 * vector.
	 * @param pitch Pitch angle.
	 * @param yaw Yaw Angle
	 * @return Turned vector.
	 */
	public Vector turn(float pitch, float yaw) {
		System.out.println(pitch);
		System.out.println(yaw);
		pitch *= (Math.PI/180);
		yaw   *= (Math.PI/180);
		System.out.println(pitch);
		System.out.println(yaw);
		return new Vector(x*(float)Math.cos(pitch)*(float)Math.sin(yaw),y*(float)Math.sin(pitch), z*(float)Math.cos(pitch)*(float)Math.cos(yaw));
	}
	
	/**
	 * Turns this vector by the pitch and yaw angles and returns a new
	 * vector of unit length.
	 * @param pitch Pitch angle.
	 * @param yaw Yaw Angle
	 * @return Turned vector of unit length.
	 */
	public static Vector newTurnedUnitVector(float pitch, float yaw) {
		pitch *= (Math.PI/180);
		yaw   *= (Math.PI/180);
		return new Vector(
				(float)Math.cos(pitch)*(float)Math.cos(yaw),
				(float)Math.sin(pitch),
				(float)Math.cos(pitch)*(float)Math.sin(yaw)
			);
	}
	
	/**
	 * Calculated and returns this vector of unit length.
	 * @return This vector with unit length.
	 */
	public Vector unit() {
		return multiply(1/abs());
	}
	
	/**
	 * Sets this vectors length and returns the resulting vector.
	 * @param length New length of the vector
	 * @return New vector of length, length.
	 */
	public Vector setLength(float length) {
		return multiply(length/abs());
	}
	
	/**
	 * Sets this vectors length to, length.
	 * @param length The new length of the vector.
	 */
	public void setLengthSelf(float length) {
		length /= abs();
		x *= length;
		z *= length;
		y *= length;
	}
	
	/**
	 * Sets this vectors x, y, and z coordinate.
	 * @param x x-coordinate.
	 * @param y y-coordinate.
	 * @param z z-coordinate.
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Sets this vectors x,y, and z coordinate by a vector v.
	 * @param v New vector coordinates.
	 */
	public void set(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	/**
	 * Sets the length of the returning vector if the given length is
	 * smaller than the length of the current vector.
	 * @param length New length of the vector.
	 * @return Vector with the new length.
	 */
	public Vector shortenIfLonger(float length) {
		if (abs() > length)
			return setLength(length);
		else
			return this;
	}

	/**
	 * Checks if this vector is a multiple of another vector v.
	 * @param v The vector checked for linear dependancy.
	 * @return The resulting boolean.
	 */
	public boolean isMultiple(Vector v) {
		return unit().equals(v.unit());
	}
	
	/**
	 * Checks if this vector is zero.
	 * @return Returns true if this vector zero
	 */
	public boolean isZero() {
		return x==0 && y==0 && z==0;
	}
	
	/**
	 * Calculates the squared distance to the given vector v.
	 * @param v Vector distance is calculated to.
	 * @return The squared distance between this vector and v.
	 */
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
