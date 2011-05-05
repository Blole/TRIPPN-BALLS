package collision.tests;

import structures.Entity;
import structures.Vector;
import collision.CollisionData;

/**
 * Test for finding intersection between a ray, basiaclly a vector, and a sphere.
 * Oringinal test written by Christer Ericson in Real time collision detection,
 * this is an implementation in java.
 * @author Jacob Norlin Andersson.
 *
 */
public class RaySphereIntersectTest {
	/**
	 * Intersects a ray with a sphere and returns a CollisionData with the
	 * results.
	 * @param p First point on the ray.
	 * @param d Second point on the ray.
	 * @param s The sphere.
	 * @return CollisionData containing point of impact time and if there was a collision.
	 */
	public CollisionData IntersectRaySphere(Vector p, Vector k, Entity s)
	{
		CollisionData rData = new CollisionData();
		Vector d = p.vectorTo(k).unit();
		System.out.println(d);
		Vector m = p.subtract(s.pos);
//		System.out.println(m);
		float b = m.dot(d);
//		System.out.println("b: "+b);
		float c = m.dot(m) - (s.getBoundingRadius() * s.getBoundingRadius());
		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (c > 0.0f && b > 0.0f)
			return rData;
		float discr = (b*b) - c;
		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f)
			return rData;
		
		// Ray now found to intersect sphere, compute smallest t value of intersection
		rData.t = (float) (-b - Math.sqrt(discr));
		// If t is negative, ray started inside sphere so clamp t to zero
		if (rData.t < 0.0f) 
			rData.t = 0.0f;
		rData.intersectionPoint = (p.add(d.multiply(rData.t)));
		rData.collision = true;
		System.out.println("Intersection");
		return rData;
	}
	
	/**
	 * Faster version of finding if a ray intersects a sphere, only
	 * finds if it is intersecting and does not calculate any data
	 * that might not be needed.
	 * @param p First point on the ray.
	 * @param d Second point on the ray.
	 * @param s The sphere.
	 * @return True or false.
	 */
	public boolean TestRaySphere(Vector p, Vector k, Entity s){
		
		Vector d = p.vectorTo(k).unit();
		Vector m= p.subtract(s.pos);
		float c = m.dot(m) - s.getBoundingRadius() * s.getBoundingRadius();
		// If there is definitely at least one real root, there must be an intersection
		if (c <= 0.0f) return true;;
		float b = m.dot(d);
		// Early exit if ray origin outside sphere and ray pointing away from sphere
		if (b > 0.0f) return false;
		float disc = b*b - c;
		// A negative discriminant corresponds to ray missing sphere
		if (disc < 0.0f) return false;
		// Now ray must hit sphere
		return true;
		
	}

}
