import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;


public class Game {
	private GL2 gl;
	//private GLUgl2 glu;
	private Input input;
	private Camera camera;
	private Sphere me;
	private float mouseWheelLengthPerClick = 1;
	private Random random = new Random(16163);
	private GravitySystem system = new GravitySystem();

	public Game (GL2 gl, GLUgl2 glu, Input input) {
		this.gl = gl;
		//this.glu = glu;
		this.input = input;
		for (int i=0; i<2; i++)
			system.add(new Sphere(new Vector(r(-50,50)-50,r(0,50),r(-50,50)), 1, 10, 10));
		for (Sphere sphere : system) {
			sphere.setAffectedByGravity(true);
			sphere.enableTrack(new float[]{r(0,1),r(0,1),r(0,1)}, 5000);
		}
		me = new Sphere(new Vector(0,0,0), 1, 20, 20);
		me.speed = new Vector (0,1,0);
		me.setAffectedByGravity(true);
		me.enableTrack(new float[]{1,0,0}, 5000);
		system.add(me);
		
		camera = new Camera(new Vector(0,0,0));
		camera.setTarget(me);
		camera.setFOV(90);
	}
	public float r(float min, float max) {
		return random.nextFloat()*(max-min)+min;
	}
	public void tick() {
		parseInput();
		
		system.gravitate();
		for (Sphere sphere : system)
			sphere.move();
		system.collide();
		
		camera.move();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		camera.setUpCameraLook(gl);
		
		drawAxes();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		for (Model model : system)
			model.render(gl, 1);
		me.render(gl, 2);
	}

	private void drawAxes() {
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f(-500,    0,    0);
		gl.glVertex3f( 500,    0,    0);
		gl.glVertex3f(    0,-500,    0);
		gl.glVertex3f(    0, 500,    0);
		gl.glVertex3f(    0,    0,-500);
		gl.glVertex3f(    0,    0, 500);
		gl.glEnd();
		gl.glBegin(GL2.GL_POINTS);
		for (int x=-50; x<=50; x++) {
			for (int z=-50; z<=50; z++)
				gl.glVertex3f(x, 0, z);
		}
			
		gl.glEnd();
	}
	private void parseInput() {
		float change = 0.1f;
		if (input.keyPressed(KeyEvent.VK_T)) {
			if (camera.getMode() == Camera.Mode.TARGET)
				camera.setMode(Camera.Mode.FREELOOK);
			else
				camera.setMode(Camera.Mode.TARGET);
		}
		switch (camera.getMode()) {
		case TARGET:
			if (input.keyPressed(Input.forward))
				me.speed.addSelf(new Vector(0,0,-change));
			if (input.keyPressed(Input.backward))
				me.speed.addSelf(new Vector(0,0,change));
			if (input.keyPressed(Input.strafeR))
				me.speed.addSelf(new Vector(change,0,0));
			if (input.keyPressed(Input.strafeL))
				me.speed.addSelf(new Vector(-change,0,0));
			if (input.keyPressed(Input.up))
				me.speed.addSelf(new Vector(0,change,0));
			if (input.keyPressed(Input.down))
				me.speed.addSelf(new Vector(0,-change,0));
			break;
		case FREELOOK:
			if (input.keyPressed(Input.forward))
				camera.pos.addSelf(new Vector(0,0,-change));
			if (input.keyPressed(Input.backward))
				camera.pos.addSelf(new Vector(0,0,change));
			if (input.keyPressed(Input.strafeR))
				camera.pos.addSelf(new Vector(change,0,0));
			if (input.keyPressed(Input.strafeL))
				camera.pos.addSelf(new Vector(-change,0,0));
			if (input.keyPressed(Input.up))
				camera.pos.addSelf(new Vector(0,change,0));
			if (input.keyPressed(Input.down))
				camera.pos.addSelf(new Vector(0,-change,0));
			break;
		}
		if (input.keyPressed(KeyEvent.VK_CONTROL))
			me.speed = new Vector(0,0,0);
		if (input.keyPressed(KeyEvent.VK_I))
			System.out.println("me.speed:"+me.speed+"    me.pos:"+me.pos+"   camera.pos:"+camera.pos);
		if (input.keyPressed(KeyEvent.VK_UP)) {
			camera.setFOV(camera.getFOV()+3);
			System.out.printf("FOV set to: %3f degrees\n", camera.getFOV());
		}
		if (input.keyPressed(KeyEvent.VK_DOWN)) {
			camera.setFOV(camera.getFOV()-3);
			System.out.printf("FOV set to: %3f degrees\n", camera.getFOV());
		}
		
		int clicks = input.getMouseWheelRotation();
		if (clicks != 0)
			camera.zoom(clicks*mouseWheelLengthPerClick);
		
		if (input.keyPressed(Input.menu))
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
		gl.glViewport(0, 0, width, height);

		camera.setAspect(width, height);
		input.mouseCenter = new Point(x+width/2, y+height/2);
	}
}
