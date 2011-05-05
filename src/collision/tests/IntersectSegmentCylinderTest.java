package collision.tests;

import structures.Vector;
import collision.CollisionData;

/**
 * Test for finding intersection between a line segment and a cylinder. Original
 * test written by Christer Ericson in Real Time Collision Detection, this is an
 * implementation in java.
 * @author Jacob Norlin Andersson.
 *
 */
public class IntersectSegmentCylinderTest {
	
	/**
	 *  Intersect segment S(t)=sa+t(sb-sa), 0<=t<=1 against cylinder specified by p, q and r.
	 * @param sa First point.
	 * @param sb Second point.
	 * @param p First point in cylinder.
	 * @param q Second point in cylinder.
	 * @param r Cylinder radius.
	 * @return CollisionData containing point of impact, time of impact and if there was a collision.
	 */
	public CollisionData IntersectSegmentCylinder(Vector sa, Vector sb, Vector p, Vector q, float r)
	{
		CollisionData rData = new CollisionData();
		
		Vector d=q.subtract(p),
		m=sa.subtract(p),
		n=sb.subtract(sa);
		
		float md = m.dot(d);
		float nd = n.dot(d);
		float dd = d.dot(d);
		
		// Test if segment fully outside either endcap of cylinder
		if (md < 0.0f && md + nd < 0.0f) return rData; // Segment outside ’p’ side of cylinder
		if (md > dd && md + nd > dd) return rData; // Segment outside ’q’ side of cylinder
		
		float nn = n.dot(n);
		float mn = m.dot(n);
		float a=dd*nn-nd*nd;
		float k = m.dot(m)-r*r;
		float c=dd*k-md*md;
		
		if (Math.abs(a) < 0.1) {
		// Segment runs parallel to cylinder axis
			rData.collision = false;
			if (c > 0.0f) return rData; // ’a’ and thus the segment lie outside cylinder
			// Now known that segment intersects cylinder; figure out how it intersects
			if (md < 0.0f) rData.t=-mn/nn; // Intersect segment against ’p’ endcap
			else if (md > dd)rData.t=(nd-mn)/nn; // Intersect segment against ’q’ endcap
			else rData.t = 0.0f; // ’a’ lies inside cylinder
			rData.collision = true;
			return rData;
		}
		float b=dd*mn-nd*md;
		float discr=b*b-a*c;
		if (discr < 0.0f) return rData; // No real roots; no intersection
		
		rData.t = (float)(-b - Math.sqrt(discr)) / a;
		if (rData.t < 0.0f || rData.t > 1.0f) return rData; // Intersection lies outside segm
		if(md+rData.t*nd< 0.0f) {
			// Intersection outside cylinder on ’p’ side
			rData.collision = false;
			if (nd <= 0.0f) return rData; // Segment pointing away from endcap
			rData.t = -md / nd;
			// Keep intersection if Dot(S(t) - p, S(t) - p) <= r2
			rData.collision = k+2*rData.t*(mn+rData.t*nn)<= 0.0f;
			return rData;
		} else if (md+rData.t*nd>dd){
			// Intersection outside cylinder on ’q’ side
			rData.collision = false;
			if (nd >= 0.0f) return rData; // Segment pointing away from endcap
			rData.t = (dd - md) / nd;
			// Keep intersection if Dot(S(t) - q, S(t) - q) <= r2
			rData.collision = k+dd-2*md+rData.t*(2*(mn-nd)+rData.t*nn)<= 0.0f;
			return  rData;
		}
		// Segment intersects cylinder between the endcaps; t is correct
		return rData;
		}

}
