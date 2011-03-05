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
	private ArrayList<Sphere> spheres = new ArrayList<Sphere>();
	private Camera camera;
	private Sphere me;
	private int index =0;
	private float[][] track = new float[5000][];
	private float mouseWheelLengthPerClick = 1;
	private Random random = new Random(16163);

	public Game (GL2 gl, GLUgl2 glu, Input input) {
		this.gl = gl;
		//this.glu = glu;
		this.input = input;
		System.out.println(r(-10,10));
		spheres.add(new Sphere(new Vector(0,0,-5), 1, 21, 20));
		spheres.add(new Sphere(new Vector(0,2,0), 1, 11, 10));
		spheres.add(new Sphere(new Vector(4,5,-4), 1, 31, 30));
		for (int i=0; i<2; i++)
			spheres.add(new Sphere(new Vector(r(-50,50)-50,r(0,50),r(-50,50)), 2, 10, 10));
		spheres.get(2).speed = new Vector(0,1,0-.01f);
		spheres.add(new Sphere(new Vector(-2,0,0), 1, 31, 30));
		for (Sphere sphere : spheres)
			sphere.setAffectedByGravity(true);
		
		me = new Sphere(new Vector(0,0,0), 1, 20, 20);
		me.speed = new Vector (0,0,0);
		
		camera = new Camera(new Vector(0,0,0));
		camera.setTarget(me);
		camera.setFOV(90);
		
		for (int i=0; i<track.length; i++)
			track[i] = new float[3];
	}
	public float r(float min, float max) {
		return random.nextFloat()*(max-min)+min;
	}
	public void tick() {
		parseInput();
		
		//if (input.inFocus())
		//	robot.mouseMove(mouseCenter.x, mouseCenter.y);
		
		int totalSpheres = spheres.size();
		for (int i=0; i<totalSpheres; i++){
			Sphere sphere = spheres.get(i);
			//for (int j=i; j<totalSpheres; j++)
				sphere.attract(me);//spheres.get(j));
			sphere.move();
			if (sphere.pos.y-sphere.getRadius() < 0) {
				sphere.pos.y = sphere.getRadius();
				sphere.speed.y = -sphere.speed.y;
			}
		}
		me.move();
		camera.move();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		camera.setUpCameraLook(gl);
		
		drawAxes();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		for (Model model : spheres)
			model.render(gl, 1);
		me.render(gl, 2);
		
		gl.glLoadIdentity();
		gl.glColor3f(1.0f, 0, 0);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i=index+1; i<track.length; i++)
			gl.glVertex3fv(track[i], 0);
		for (int i=0; i<index; i++)
			gl.glVertex3fv(track[i], 0);
		gl.glEnd();

		gl.glFlush();
		
		track[index][0] = me.pos.x;
		track[index][1] = me.pos.y;
		track[index++][2] = me.pos.z;
		index %= track.length;
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
		float change = 0.01f;
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
