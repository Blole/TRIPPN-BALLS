/**
 * 
 */
package collision.tests;


import structures.Entity;
import structures.Vector;
import collision.CollisionData;

/**
 * @author Jacob
 *
 */
public class SpherePolygonTest {
	public CollisionData testSpherePolygon(Entity s, Entity p){
		//First do a test against the plane of p.
		CollisionData rData = IntersectMovingSpherePlane(s, p.getPlane());
		Vector center = s.getPos();
		Vector v = s.getSpeed();
		if(rData.collision == false)
			return rData;

		float[][] vertices = p.getVertices();
		//A polygon is constructed of n-3 triangles.
		Vector a = new Vector(vertices[0]);
		Vector b = new Vector(vertices[1]);
		Vector c = new Vector(vertices[2]);
		Vector q = rData.intersectionPoint;
		if(pointInTriangle(q,a,b,c)){//Check if the point is in the triangle.
			rData.collision = true;
			return rData;
		}
		if(true){//Check against all the edges. Every edge is a cylinder with radius r.
			IntersectSegmentCylinderTest eTest = new IntersectSegmentCylinderTest();
			//Check the edges
			CollisionData ab = eTest.IntersectSegmentCylinder(center,v, a, b, s.getRadius());
			CollisionData ac = eTest.IntersectSegmentCylinder(center,v, a, c, s.getRadius());
			CollisionData bc = eTest.IntersectSegmentCylinder(center,v, b, c, s.getRadius());

			if(ab.collision){
				rData.intersectionPoint = ab.intersectionPoint;
				rData.t = ab.t;
			}else if(ac.collision){
				rData.intersectionPoint = ab.intersectionPoint;
				rData.t = ac.t;
			}else if(bc.collision){
				rData.intersectionPoint = ab.intersectionPoint;
				rData.t = bc.t;
			
			}
			if(rData.intersectionPoint != q)return rData;
			
			
		}
		if(true){//Check against the vertices.
			RaySphereIntersectTest vTest = new RaySphereIntersectTest();
			
			//Check against all the vertices
			CollisionData v1 = vTest.IntersectRaySphere(center, v, new Sphere(a,s.getRadius()));
			CollisionData v2 = vTest.IntersectRaySphere(center, v, new Sphere(b,s.getRadius()));
			CollisionData v3 = vTest.IntersectRaySphere(center, v, new Sphere(c,s.getRadius()));
			
			if(v1.collision){
				rData.intersectionPoint = v1.intersectionPoint;
				rData.t = v1.t;
			}else if(v2.collision){
				rData.intersectionPoint = v2.intersectionPoint;
				rData.t = v2.t;
			}else if(v3.collision){
				rData.intersectionPoint = v3.intersectionPoint;
				rData.t = v3.t;
			
			}
			if(rData.intersectionPoint != q)return rData;

		}
		
		
		return rData;
	}
	
	
	public boolean pointInTriangle(Vector q, Vector a, Vector b, Vector c){
		//Compute the vectors
		Vector v0 = c.vectorTo(a);
		Vector v1 = b.vectorTo(a);
		Vector v2 = q.vectorTo(a);
		
		//Compute dot products
		float dot00 = v0.dot(v0);
		float dot01 = v0.dot(v1);
		float dot02 = v0.dot(v2);
		float dot11 = v1.dot(v1);
		float dot12 = v1.dot(v2);
		
		//Barycentric coordinates
		float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		//Check if point is in triangle, per definition of
		//barycentric coordinates.
		return (u > 0) && (v > 0) && (u + v < 1);
	}
	
	/**
	 * Original test by Christer Ericson written in the book
	 * Real time collision detection. Converted from C to java
	 * with some changes.
	 * 
	 * Intersect sphere s with movement vector v with plane p. If intersecting
	 * return time t of collision and point q at which sphere hits plane
	 * @param s
	 * @param p
	 * @return
	 */
	private CollisionData IntersectMovingSpherePlane(Sphere s, Plane p){
		
		Vector normal = p.getNormal();
		float d = p.getD();
		float radius = s.getRadius();
		Vector center = s.getPos();
		Vector v = s.getSpeed();
		CollisionData returnData = new CollisionData();
		
		// Compute distance of sphere center to plane
		float dist = normal.dot(center) - d;
		if (Math.abs(dist) <= radius) {
			// The sphere is already overlapping the plane. Set time of
			// intersection to zero and q to sphere center
			returnData.t = 0.0f;
			returnData.intersectionPoint = center;
			return returnData;
		} 
		else {
			float denom = normal.dot(v);
			if (denom * dist >= 0.0f) {
			// No intersection as sphere moving parallel to or away from plane
				return returnData;
			} 
			else {
				// Sphere is moving towards the plane
				// Use +r in computations if sphere in front of plane, else -r
				float r = dist > 0.0f ? radius : -radius;
				returnData.t = (r - dist) / denom;
				returnData.intersectionPoint=center.scalarAdd(returnData.t).multiplyVec(v).scalarAdd(-r).multiplyVec(normal);
				return returnData;
			}
		}
	}

}
