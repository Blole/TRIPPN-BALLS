package engine;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import org.luaj.vm2.LuaValue;

import collision.Collision;

import models.Sun;
import models.WorldMesh;

import structures.Doodad;
import structures.Entity;
import structures.Vector;


public final class Game {
	private static final Vector groundGravity = new Vector(0,-0.02f,0);
	private static Entity me;
	private static boolean drawAxes = true;
//	private static VBOdammit vbo;
	private static WorldMesh worldMesh;
	private static Collision collision = new Collision();
	private static Selection selection = new Selection();
	private static Sun sun;
	private static boolean enableGroundGravity = true;
	
	private Game() {}
	
	public static void init() {
		Settings.loadSettings();
		worldMesh = new WorldMesh();
		worldMesh.loadHeightMap(Settings.worldMap);
		sun = new Sun();
		
		me = new Entity("sphere", false);
		Camera.init();
		Camera.setTarget(me.pos);
	}
	public static void tick() {
		parseInput();
		move();
		groundGravity();
		gravity();
		groundCollision();
		collision();
		render();
		Doodad.executeQueue();
	}
	private static void move() {
		for (Entity entity : Entity.all) {
			if (!entity.isFrozen())
				entity.move();
		}
	}
	
	private static void groundGravity() {
		if (enableGroundGravity) {
			for (Entity entity : Entity.all) {
				if (!entity.isFrozen())
					entity.speed.addSelf(groundGravity);
			}
		}
	}
	
	/**
	 * Not perfect, even if gravity source is frozen, it will move
	 * because of attraction.
	 */
	private static void gravity() {
		Engine.phase("gravity",true);
		for (Entity entityWithGravity : Entity.hasGravity) {
			boolean resetIfOriginallyFrozen = entityWithGravity.isFrozen();
			entityWithGravity.setFreeze(true);
			for (Entity other : Entity.all) {
				if (!other.isFrozen()) {
					entityWithGravity.attract(other);
				}
			}
			entityWithGravity.setFreeze(resetIfOriginallyFrozen);
		}
		Engine.phase("gravity",false);
	}
	
	/**
	 * Collision is only between spheres and
	 * does not include rotation. 
	 */
	private static void collision() {
		Engine.phase("collide",true);
		collision.testCollision();
		Engine.phase("collide",false);
	}
	
	/**
	 * Temporary collision against the xz-plane.
	 */
	private static void groundCollision() {
		Engine.phase("groundCollision",true);
		for (Entity entity : Entity.all) {
			if (!entity.isFrozen() && entity.pos.y < 0) {
				entity.pos.y = 0;
				if (Math.abs(entity.speed.y) < 0.1f)
					entity.speed.y = 0;
				else
					entity.speed.y *= -0.5f;
			}
		}
		Engine.phase("groundCollision",false);
	}
	
	private static void render() {
		Engine.phase("render",true);
		Engine.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		Camera.setUpCameraLook();
		Engine.gl.glMatrixMode(GL2.GL_MODELVIEW);
		Engine.gl.glLoadIdentity();
		if (drawAxes)
			drawAxes();
		
		Engine.gl.glEnable(GL2.GL_LIGHTING);
		Engine.gl.glEnable(GL2.GL_LIGHT0);
//		Engine.gl.glEnable(GL2.GL_TEXTURE_2D);
		Engine.gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		Engine.gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		Engine.gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		sun.render();
		Engine.gl.glLoadIdentity();
		for (Entity entity : Entity.all)
			entity.render();
		//worldMesh.render();
		
//		for (int x=-5; x<5; x++) {
//			for (int z=-5; z<5; z++) {
//				drawQuad(x*9,1-Math.max(x*z, -x*z),z*9);
//			}
//		}
		Engine.gl.glDisable(GL2.GL_LIGHTING);
		Engine.gl.glDisable(GL2.GL_LIGHT0);
//		Engine.gl.glDisable(GL2.GL_TEXTURE_2D);
		Engine.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		Engine.gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		Engine.gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		Engine.phase("render",false);
	}
	
	private static void drawQuad(int x, int y, int z) {
		Engine.gl.glLoadIdentity();
		Engine.gl.glTranslatef(x,y,z);
		//Engine.gl.glRotatef(me.getPos().y, 1, 0, 0);
		Engine.gl.glRotatef(-x, 0, 0, 1);
		Engine.gl.glRotatef(z, 1, 0, 0);
		Engine.gl.glBegin(GL2.GL_QUADS);
		Engine.gl.glNormal3f(0, 1, 0);
		Engine.gl.glVertex3f(0, 0, 0);
		Engine.gl.glVertex3f(0, 0, 9);
		Engine.gl.glVertex3f(9, 0, 9);
		Engine.gl.glVertex3f(9, 0, 0);
		Engine.gl.glEnd();
	}
	private static void drawAxes() {
		Engine.gl.glPointSize(1);
		Engine.gl.glBegin(GL2.GL_LINES);
		Engine.gl.glColor3f(1, 1, 1);
		Engine.gl.glVertex3f(-500,    0,    0);
		Engine.gl.glVertex3f( 500,    0,    0);
		Engine.gl.glVertex3f(    0,-500,    0);
		Engine.gl.glVertex3f(    0, 500,    0);
		Engine.gl.glVertex3f(    0,    0,-500);
		Engine.gl.glVertex3f(    0,    0, 500);
		Engine.gl.glEnd();
		Engine.gl.glBegin(GL2.GL_POINTS);
		for (int x=-50; x<=50; x++) {
			for (int z=-50; z<=50; z++)
				Engine.gl.glVertex3f(x, 0, z);
		}
			
		Engine.gl.glEnd();
	}
	private static void parseInput() {
		float change = 0.1f;
		if (Input.keyTyped(KeyEvent.VK_T)) {
			if (Camera.getMode() == Camera.Mode.TARGET)
				Camera.setMode(Camera.Mode.FREELOOK);
			else
				Camera.setMode(Camera.Mode.TARGET);
		}
		switch (Camera.getMode()) {
		case TARGET:
			if (Input.keyDown(Settings.forward))
				me.speed.addSelf(new Vector(0,0,-change));
			if (Input.keyDown(Settings.backward))
				me.speed.addSelf(new Vector(0,0,change));
			if (Input.keyDown(Settings.strafeR))
				me.speed.addSelf(new Vector(change,0,0));
			if (Input.keyDown(Settings.strafeL))
				me.speed.addSelf(new Vector(-change,0,0));
			if (Input.keyDown(Settings.up))
				me.speed.addSelf(new Vector(0,change,0));
			if (Input.keyDown(Settings.down))
				me.speed.addSelf(new Vector(0,-change,0));
			break;
		case FREELOOK:
			if (Input.keyDown(Settings.forward))
				Camera.moveForward(change);

			if (Input.keyDown(Settings.backward))
				Camera.moveForward(-change);

			if (Input.keyDown(Settings.strafeR))
				Camera.strafe(-change);

			if (Input.keyDown(Settings.strafeL))
				Camera.strafe(change);

			if (Input.keyDown(Settings.up))
				Camera.pos.addSelf(new Vector(0,change,0));
			if (Input.keyDown(Settings.down))
				Camera.pos.addSelf(new Vector(0,-change,0));
			if (Input.mouseClick()){
//				System.out.println("Pitch: "+Camera.pitch+" Yaw: "+Camera.yaw);
				selection.findMouseTarget();
			}
			
			break;
		}
		if (Input.keyDown(KeyEvent.VK_CONTROL))
			me.speed.set(0,0,0);
		if (Input.keyTyped(KeyEvent.VK_I))
			System.out.println("me.speed:"+me.speed+"    me.pos:"+me.pos+"   camera.pos:"+Camera.pos);
		if (Input.keyPressed(KeyEvent.VK_B))
			pauseGame(true);
		if (Input.keyReleased(KeyEvent.VK_B))
			pauseGame(false);
		if (Input.keyTyped(KeyEvent.VK_C)) {
			for (int i=0; i<20; i++)
				System.out.println();
		}
		if (Input.keyTyped(KeyEvent.VK_H)) {
			drawAxes = !drawAxes;
		}
		if (Input.keyTyped(KeyEvent.VK_N)) {
			new Entity("sphere", false).pos.set(me.pos.x, me.pos.y, me.pos.z-2.5f);
		}
		if (Input.keyTyped(KeyEvent.VK_M)) {
			new Entity("sphere", false).setOwnGravity(true).pos.set(me.pos.x, me.pos.y, me.pos.z-10);
		}
		if (Input.keyTyped(KeyEvent.VK_R)) {
			Entity.hasGravity.clear();
			Entity.all.clear();
			Entity.all.add(me);
			me.speed.set(0,0,0);
			me.pos.set(0,0,0);
		}
		if (Input.keyTyped(KeyEvent.VK_F))
			me.setFreeze(!me.isFrozen());
		if (Input.keyTyped(KeyEvent.VK_UP)) {
			Camera.setFOV(Camera.getFOV()+3);
			System.out.printf("FOV set to: %3.0f degrees\n", Camera.getFOV());
		}
		if (Input.keyTyped(KeyEvent.VK_DOWN)) {
			Camera.setFOV(Camera.getFOV()-3);
			System.out.printf("FOV set to: %3.0f degrees\n", Camera.getFOV());
		}
		
		int clicks = Input.getMouseWheelRotation();
		if (clicks != 0)
			Camera.zoom(clicks*Settings.zoomStep);
		if (Input.keyDown(KeyEvent.VK_LEFT))
			Camera.zoom(-Settings.zoomStep);
		if (Input.keyDown(KeyEvent.VK_RIGHT))
			Camera.zoom(Settings.zoomStep);
		
		if (Input.keyTyped(Settings.menu))
			Input.keepCursorCenteredAndHidden(false);
		if (Input.focusGained())
			Input.keepCursorCenteredAndHidden(true);
		if (Input.focusLost())
			Input.keepCursorCenteredAndHidden(false);
		Point mouseMovement = Input.getMouseMovementAndCenter();
		if (mouseMovement != null) {
			//System.out.println(mouseMovement);
			Camera.updateTurn(mouseMovement);
		}
		
		if (Input.keyTyped(KeyEvent.VK_P))
			me.speed.z -= 100; ///yeah
	}
	private static void pauseGame(boolean pause) {
		if (pause) {
			for (Entity entity : Entity.all)
				entity.setFreeze(true);
		}
		else {
			for (Entity entity : Entity.all)
				entity.setFreeze(false);
		}
	}
	public static void reshape(int x, int y, int width, int height) {
		Engine.gl.glViewport(0, 0, width, height);

		Camera.setAspect(width, height);
		Input.setMouseCenter(new Point(x+width/2, y+height/2));
	}
}
