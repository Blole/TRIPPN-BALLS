package engine;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import collision.CollisionData;
import collision.tests.RaySphereIntersectTest;
import structures.Entity;
import structures.Vector;

public class Selection {

	
	public void findTarget(float pitch, float yaw){
		
		pitch += Camera.pitch;
		yaw += Camera.yaw;
		
		Vector lookPos = Vector.newTurnedUnitVector(pitch, yaw);
		lookPos = new Vector(1,-1,-1).turn(pitch, yaw);
		System.out.println(lookPos.abs());
		lookPos.multiplySelf(300);
		Vector offset = new Vector(5,5,5);
		lookPos.addSelf(Camera.pos);
		Engine.gl.glPointSize(10);
		Engine.gl.glLoadIdentity();
		Engine.gl.glBegin(GL2.GL_POINTS);
		Engine.gl.glColor3f(1, 1, 1);
		Engine.gl.glVertex3f(lookPos.x,    lookPos.y,    lookPos.z);
		Engine.gl.glEnd();
		
		Engine.gl.glBegin(GL2.GL_LINES);
		Engine.gl.glColor3f(1, 1, 1);
		Engine.gl.glVertex3f(lookPos.x,     lookPos.y,    	lookPos.z);
		Engine.gl.glColor3f(1, 0, 0);
		Engine.gl.glVertex3f(offset.x,    	offset.y,    	offset.z);
		Engine.gl.glEnd();
		
		
		Engine.gl.glLoadIdentity();
		Engine.gl.glRotatef(pitch, 1, 0, 0);
		Engine.gl.glRotatef(yaw, 0, 1, 0);
		
		Engine.gl.glBegin(GL2.GL_LINES);
		Engine.gl.glColor3f(0, 1, 0);
		Engine.gl.glVertex3f(0,0,0);
		
		Engine.gl.glColor3f(1, 1, 1);
		Engine.gl.glVertex3f(0,0,3);
		
		Engine.gl.glEnd();
		
		System.out.println(lookPos);
		
		RaySphereIntersectTest test = new RaySphereIntersectTest();
		
		for (Entity entity : Entity.all) {
			if(test.TestRaySphere(Camera.pos, lookPos, entity)){
				onOver(entity);
				break;
			}
		}
	}
	
	public void onOver(Entity entity){
		System.out.println("		COOLT GRABBEN");
//		Camera.setTarget(smek.pos);
	}
	
	public void findMouseTarget(){
		Point mousePos = Input.getRelativeMousePos();
		Rectangle bounds = Engine.getAbsoluteCanvasBounds();
		float bw = bounds.width/2f;
		float bh = bounds.height/2f;
		Point2D.Float lol = new Point2D.Float(mousePos.x/bw-1, mousePos.y/bh-1);
		System.out.println("WWEEEEEEEE mouseCoords: "+lol);
		
		float FOV = Camera.getFOV()/2;
		
		float yaw = lol.x*FOV;
		float pitch = lol.y*FOV;
		
//		System.out.println("-----------");
//		System.out.println("mouseX: "+mouseX+" mouseY: "+mouseY+"\n"+
//							"pitch: "+pitch+" yaw: "+yaw+"\n"+
//							"xAspect: "+xAspect+" yAspect: "+yAspect);
		System.out.println("===========");
		findTarget(pitch, yaw);
	}
		
}
