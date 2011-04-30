package collision.tests;

import structures.Entity;
import structures.Vector;
import collision.CollisionData;

/**
 * Class for testing the intersection of two moving spheres dynamically.
 * Implements Test interface.
 * @author Jacob
 *
 */
public class MovingSphereSphere {


	/**
	 * A test to detect intersection/collision between two moving spheres.
	 * Tunneling, spheres moving through eachother, should not be possible
	 * due to the nature of the test. Returns a CollisionData class.
	 * 
	 * Original test written by Christer Ericson in the book Real Time
	 * Collision detection, converted from C to Java.
	 * @param s0 First sphere
	 * @param s1 Second sphere
	 * @param v0 First sphere velocity vector
	 * @param v1 Second sphere velocity vector
	 * @return CollisionData, contains time of collision on speed vector
	 * 			point of intersection and boolean for collision.
	 */
	public CollisionData testMovingSphereSphere(Entity s0, Entity s1, Vector v0, Vector v1)
	{
		CollisionData rData = new CollisionData(false, -1, null); //The initial data
		Vector s = s1.pos.vectorTo(s0.pos);
		Vector v = v1.vectorTo(v0); // Relative motion of s1 with respect to stationary s0
		float r = s1.getBoundingRadius() + s0.getBoundingRadius(); // Sum of sphere radii
		float c = s.dot(s)-(r*r);
		if (c < 0) {
		// Spheres initially overlapping so exit directly
			rData.t = 0.0f;
//			System.out.println("Intersection");
			rData.collision = true;
			return rData;
		}
		float a = v.dot(v);
		if (a < 0.1f) {
//			System.out.println("Non relative motion");
			return rData; // Spheres not moving relative each other
		}
		
		float b = v.dot(s);
		if (b >= 0.0f) {
//			System.out.println("Spheres not moving towards each other");
			return rData; // Spheres not moving towards each other
		}
		
		float d = ((b*b)-(a*c));
		if (d < 0.0f) {
//			System.out.println("No real valued roots");
			return rData; // No real-valued root, spheres do not intersect
		}
		
		rData.t = (float) ((-b-Math.sqrt(d)) / a);
		rData.collision = true;
//        System.out.println("Intersection");
		return rData;
	}
	
//	public CollisionData testMovingSphereSphere(Sphere s0, Sphere s1, Vector v0, Vector v1)
//	{
//		// Expand sphere s1 by the radius of s0
//		float r = s1.getRadius();
//		s1.setRadius(r+s0.getRadius());
//		// Subtract movement of s1 from both s0 and s1, making s1 stationary
//		Vector v = v0.vectorTo(v1);
//		// Can now test directed segment s = s0.c + tv, v = (v0-v1)/||v0-v1|| against
//		// the expanded sphere for intersection
//		float vlen = (float)Math.sqrt(v.abs());
//		RaySphereIntersectTest rTest = new RaySphereIntersectTest();
//		CollisionData rData = rTest.IntersectRaySphere(s0.getPos(), v.multiply(1/vlen), s1);
//		rData.collision = rData.t <= vlen;
//		s1.setRadius(r);
//		return rData;
//	}
}
