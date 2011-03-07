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
	private Random random = new Random();
	private ArrayList<Sphere> spheres = new ArrayList<Sphere>();

	public Game (GL2 gl, GLUgl2 glu, Input input) {
		this.gl = gl;
		//this.glu = glu;
		this.input = input;
		Settings.loadSettings();
		
		spheres.add(new Sphere(new Vector(0,3,0), 1, 10, 10));
		for (int i=0; i<0; i++)
			spheres.add(new Sphere(new Vector(r(-50,50),0,r(-50,50)), 1, 10, 10));
		for (Sphere sphere : spheres) {
			sphere.setAffectedByGravity(true);
			sphere.enableTrack(new float[]{r(0,1),r(0,1),r(0,1)}, 5000);
		}
		me = new Sphere(new Vector(0,0,0), 1, 20, 20);
		me.setAffectedByGravity(true);
		me.enableTrack(new float[]{1,0,0}, 5000);
		spheres.add(me);
		
		camera = new Camera(new Vector(0,0,0));
		camera.setTarget(me);
		camera.setFOV(90);
	}
	public float r(float min, float max) {
		return random.nextFloat()*(max-min)+min;
	}
	public void tick() {
		parseInput();
		
		for (int i=0; i<spheres.size()-1; i++) {
			Sphere mark = spheres.get(i);
			if (!mark.affectedByGravity)
				continue;
			for (int j=i+1; j<spheres.size(); j++) {
				Sphere runner = spheres.get(j);
				if (!runner.affectedByGravity)
					continue;
				mark.attract(runner);
			}
		}
		for (Sphere sphere : spheres)
			sphere.move();
		for (int i=0; i<spheres.size()-1; i++) {
			Sphere mark = spheres.get(i);
			for (int j=i+1; j<spheres.size(); j++) {
				Sphere runner = spheres.get(j);
				mark.checkForCollision(runner);
			}
		}
		
		camera.move();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		camera.setUpCameraLook(gl);
		
		drawAxes();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		for (Model model : spheres)
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
			if (input.keyPressed(Settings.forward))
				me.speed.addSelf(new Vector(0,0,-change));
			if (input.keyPressed(Settings.backward))
				me.speed.addSelf(new Vector(0,0,change));
			if (input.keyPressed(Settings.strafeR))
				me.speed.addSelf(new Vector(change,0,0));
			if (input.keyPressed(Settings.strafeL))
				me.speed.addSelf(new Vector(-change,0,0));
			if (input.keyPressed(Settings.up))
				me.speed.addSelf(new Vector(0,change,0));
			if (input.keyPressed(Settings.down))
				me.speed.addSelf(new Vector(0,-change,0));
			break;
		case FREELOOK:
			if (input.keyPressed(Settings.forward))
				camera.pos.addSelf(new Vector(0,0,-change));
			if (input.keyPressed(Settings.backward))
				camera.pos.addSelf(new Vector(0,0,change));
			if (input.keyPressed(Settings.strafeR))
				camera.pos.addSelf(new Vector(change,0,0));
			if (input.keyPressed(Settings.strafeL))
				camera.pos.addSelf(new Vector(-change,0,0));
			if (input.keyPressed(Settings.up))
				camera.pos.addSelf(new Vector(0,change,0));
			if (input.keyPressed(Settings.down))
				camera.pos.addSelf(new Vector(0,-change,0));
			break;
		}
		if (input.keyPressed(KeyEvent.VK_CONTROL))
			me.speed = new Vector(0,0,0);
		if (input.keyPressed(KeyEvent.VK_I))
			System.out.println("me.speed:"+me.speed+"    me.pos:"+me.pos+"   camera.pos:"+camera.pos);
		if (input.keyPressed(KeyEvent.VK_B)) {
			for (Sphere sphere : spheres) {
				sphere.speed=new Vector(0,0,0);
				sphere.setAffectedByGravity(false);
			}
		}
		if (input.keyPressed(KeyEvent.VK_C)) {
			for (Sphere sphere : spheres)
				sphere.clearTrack();
		}
		if (input.keyPressed(KeyEvent.VK_N)) {
			Sphere sphere = new Sphere(me.pos.add(new Vector(0,0,me.getRadius()+1)), 1, 10, 10);
			sphere.setAffectedByGravity(false);
			sphere.enableTrack(new float[]{r(0,1),r(0,1),r(0,1)}, 500);
			spheres.add(sphere);
		}
		if (input.keyPressed(KeyEvent.VK_G))
			me.setAffectedByGravity(!me.affectedByGravity);
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
			camera.zoom(clicks*Settings.zoomStep);
		
		if (input.keyPressed(Settings.menu))
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
		input.setMouseCenter(new Point(x+width/2, y+height/2));
	}
}
