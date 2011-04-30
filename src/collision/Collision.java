package collision;

import collision.tests.*;

import structures.Entity;

/**
 * Collision system.
 * @author Jacob
 *
 */
public class Collision {
	
//	/**
//	 * 
//	 * @author Jacob
//	 *
//	 */
//	private final class TestCase {
//		public final int ID;
//		
//		public TestCase(Entity k, Entity p){
//			if(k.isSphere == true && p.isSphere == true)
//				ID = 1;
//			else if(k.isSphere == true && p.isSphere == false || k.isSphere == false && p.isSphere == true)
//				ID = 2;
//			else
//				ID = 3;
//		}
//		@Override
//		public int hashCode(){
//			return ID;
//		}
//	
//	}
	//A map of all the tests for the different collisions.
//	private HashMap<Integer,Test> TestCases = new HashMap<Integer,Test>();
	public Collision(){
//		TestCase c = new TestCase(new Entity("sphere"), new Entity("sphere"));
//		float[][] k = {{1},{1},{1}};
////		TestCase b = new TestCase(new Sphere(new Vector(0,0,0)), new Polygon(new Vector(0,0,0),k));
//		TestCases.put(c.hashCode(), new MovingSphereSphere());
////		TestCases.put(b.hashCode(), new SpherePolygonTest());
	}
	
	

	/**
	 * Loops through every collidable object in Objects.
	 * For every object encountered it checks if it is
	 * possible to collide with another object and if it
	 * is, it will run the appropriate test for the
	 * collision. If it finds a sphere can collide with
	 * another sphere it will run the SphereSphere test.
	 * Time complexity : O(n^2)
	 * @param Objects
	 */
	public void testCollision(){
		for(int i = 0; i < Entity.all.size(); i ++){
			Entity e1 = Entity.all.get(i);
//			System.out.println(k);
			if(e1.isFrozen() == true)
				continue;
			for(int j = i+1; j < Entity.all.size(); j++){
				Entity e2 = Entity.all.get(j);
				
				//Find the radius of the bounding sphere
				float e1Radius = e1.getBoundingRadius();
				float e2Radius = e2.getBoundingRadius();
				
				//Calculate the distance between the bounds of the spheres.
				float distanceToP = (float) (e1.pos.squaredDistanceTo(e2.pos)-Math.pow(e1Radius,2)-Math.pow(e2Radius,2));

				/*
				 * Calculated the sphere of the speed vector and radius of the sphere
				 * added together. This helps avoid unnecessary checks.
				 */
				float checkSphereRadius = (float) (Math.pow(e1Radius,2)+(e1.speed.dot(e1.speed)))/4;
				
				
				if(distanceToP < checkSphereRadius){
					CollisionData collData = new CollisionData();
//					TestCase c = new TestCase(k,p);
//					System.out.println(k.getSpeed());
//					CollisionData d = TestCases.get(c.hashCode()).run(k,p); //Find the test.
					if(e1.isSphere() && e2.isSphere()){
						MovingSphereSphere test = new MovingSphereSphere();
						collData = test.testMovingSphereSphere(e1, e2, e1.speed,e2.speed);
					}
					
//					System.out.println((d));
					if(collData.collision == true){
						collData.other = e2;
						e1.collide(collData);
						break;
					}
				}
			}
		}
	}
	
}
