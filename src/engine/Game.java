package engine;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import models.Sun;
import models.WorldMesh;

import structures.Entity;
import structures.Vector;


public final class Game {
	private static final Vector groundGravity = new Vector(0,-0.02f,0);
	private Input input;
	private Camera camera;
	private Entity me;
	private boolean drawAxes = true;
//	private VBOdammit vbo;
	private WorldMesh worldMesh;
	private Sun sun;
	private boolean enableGroundGravity = true;

	public Game (Input input) {
		this.input = input;
		Settings.loadSettings();
		worldMesh = new WorldMesh();
		worldMesh.loadHeightMap(Settings.worldMap);
		sun = new Sun();
		
		me = new Entity("sphere", false);
//		me.enableTrack(new float[]{1,0,0}, 5000);
//		me.attach(new Pyramid(  0,  0, 0, 0.5f));
//		me.attach(new Pyramid(  0, 90, 0, 0.3f));
//		me.attach(new Pyramid( 90, 90, 0, 0.3f));
//		me.attach(new Pyramid(180, 90, 0, 0.3f));
//		me.attach(new Pyramid(270, 90, 0, 0.3f));
//		me.attach(new Pyramid(  0,180, 0, 0.1f));
		
		camera = new Camera();
		camera.setTarget(me.pos);
		camera.setFOV(90);
//		vbo = new VBOdammit();
	}
	public void tick() {
		parseInput();
		move();
		groundGravity();
		gravity();
		groundCollision();
		collision();
		render();
	}
	private void move() {
		for (Entity entity : Entity.all) {
			if (!entity.isFrozen())
				entity.move();
		}
	}
	
	private void groundGravity() {
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
	private void gravity() {
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
	private void collision() {
		Engine.phase("collide",true);
		for (int i=0; i<Entity.all.size()-1; i++) {
			Entity mark = Entity.all.get(i);
			for (int j=i+1; j<Entity.all.size(); j++) {
				Entity runner = Entity.all.get(j);
				mark.checkForCollision(runner);
			}
		}
		Engine.phase("collide",false);
	}
	
	/**
	 * Temporary collision against the xz-plane.
	 */
	private void groundCollision() {
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
	
	private void render() {
		Engine.phase("render",true);
		Engine.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		camera.setUpCameraLook();
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
	
	private void drawQuad(int x, int y, int z) {
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
	private void drawAxes() {
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
	private void parseInput() {
		float change = 0.1f;
		if (input.keyTyped(KeyEvent.VK_T)) {
			if (camera.getMode() == Camera.Mode.TARGET)
				camera.setMode(Camera.Mode.FREELOOK);
			else
				camera.setMode(Camera.Mode.TARGET);
		}
		switch (camera.getMode()) {
		case TARGET:
			if (input.keyDown(Settings.forward))
				me.speed.addSelf(new Vector(0,0,-change));
			if (input.keyDown(Settings.backward))
				me.speed.addSelf(new Vector(0,0,change));
			if (input.keyDown(Settings.strafeR))
				me.speed.addSelf(new Vector(change,0,0));
			if (input.keyDown(Settings.strafeL))
				me.speed.addSelf(new Vector(-change,0,0));
			if (input.keyDown(Settings.up))
				me.speed.addSelf(new Vector(0,change,0));
			if (input.keyDown(Settings.down))
				me.speed.addSelf(new Vector(0,-change,0));
			break;
		case FREELOOK:
			if (input.keyDown(Settings.forward))
				camera.pos.addSelf(new Vector(0,0,-change));
			if (input.keyDown(Settings.backward))
				camera.pos.addSelf(new Vector(0,0,change));
			if (input.keyDown(Settings.strafeR))
				camera.pos.addSelf(new Vector(change,0,0));
			if (input.keyDown(Settings.strafeL))
				camera.pos.addSelf(new Vector(-change,0,0));
			if (input.keyDown(Settings.up))
				camera.pos.addSelf(new Vector(0,change,0));
			if (input.keyDown(Settings.down))
				camera.pos.addSelf(new Vector(0,-change,0));
			break;
		}
		if (input.keyDown(KeyEvent.VK_CONTROL))
			me.speed.set(0,0,0);
		if (input.keyTyped(KeyEvent.VK_I))
			System.out.println("me.speed:"+me.speed+"    me.pos:"+me.pos+"   camera.pos:"+camera.pos);
		if (input.keyDown(KeyEvent.VK_B)) {
			for (Entity entity : Entity.all)
				entity.setOwnGravity(false).speed.set(0,0,0);
		}
		if (input.keyTyped(KeyEvent.VK_C)) {
			for (Entity entity : Entity.all)
				;//entity.clearTrack();
		}
		if (input.keyTyped(KeyEvent.VK_H)) {
			drawAxes = !drawAxes;
		}
		if (input.keyTyped(KeyEvent.VK_N)) {
			new Entity("sphere", false).pos.set(me.pos.x, me.pos.y, me.pos.z-2.5f);
		}
		if (input.keyTyped(KeyEvent.VK_M)) {
			new Entity("sphere", false).setOwnGravity(true).pos.set(me.pos.x, me.pos.y, me.pos.z-10);
		}
		if (input.keyTyped(KeyEvent.VK_R)) {
			Entity.hasGravity.clear();
			Entity.all.clear();
			Entity.all.add(me);
			me.speed.set(0,0,0);
			me.pos.set(0,0,0);
		}
		if (input.keyTyped(KeyEvent.VK_G))
			enableGroundGravity = !enableGroundGravity ;
		if (input.keyTyped(KeyEvent.VK_UP)) {
			camera.setFOV(camera.getFOV()+3);
			System.out.printf("FOV set to: %3.0f degrees\n", camera.getFOV());
		}
		if (input.keyTyped(KeyEvent.VK_DOWN)) {
			camera.setFOV(camera.getFOV()-3);
			System.out.printf("FOV set to: %3.0f degrees\n", camera.getFOV());
		}
		
		int clicks = input.getMouseWheelRotation();
		if (clicks != 0)
			camera.zoom(clicks*Settings.zoomStep);
		if (input.keyDown(KeyEvent.VK_LEFT))
			camera.zoom(-Settings.zoomStep);
		if (input.keyDown(KeyEvent.VK_RIGHT))
			camera.zoom(Settings.zoomStep);
		
		if (input.keyTyped(Settings.menu))
			input.keepCursorCenteredAndHidden(false);
		if (input.focusGained())
			input.keepCursorCenteredAndHidden(true);
		if (input.focusLost())
			input.keepCursorCenteredAndHidden(false);
		Point mouseMovement = input.getMouseMovementAndCenter();
		if (mouseMovement != null) {
			//System.out.println(mouseMovement);
			camera.updateTurn(mouseMovement);
		}
	}
	public void reshape(int x, int y, int width, int height) {
		System.out.println("reshaped");
		Engine.gl.glViewport(0, 0, width, height);

		camera.setAspect(width, height);
		input.setMouseCenter(new Point(x+width/2, y+height/2));
	}
}
