package attachables;

import interfaces.SphereAttachable;

import javax.media.opengl.GL2;

import engine.Engine;


import models.Sphere;

public class Pyramid implements SphereAttachable {
	private Sphere parent;
	private float scale = 1;
	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;
	private float rollIncrement;
	
	public Pyramid (float yaw, float pitch, float rollIncrement, float scale) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.rollIncrement = rollIncrement;
		this.scale = scale;
	}

	@Override
	public void move() {
		roll += rollIncrement;
	}

	@Override
	public void render() {
		Engine.gl.glPushMatrix();
		Engine.gl.glRotatef(yaw,   0, 1, 0);
		Engine.gl.glRotatef(pitch, 1, 0, 0);
		Engine.gl.glRotatef(roll,  0, 1, 0);
		Engine.gl.glTranslatef(0, parent.getRadius(), 0);
		Engine.gl.glScalef(scale, scale, scale);
		
		Engine.gl.glBegin(GL2.GL_TRIANGLE_FAN);
		Engine.gl.glColor3f(1, 1, 1);	Engine.gl.glVertex3f( 0, 1, 0);
		Engine.gl.glColor3f(0, 0, 1);	Engine.gl.glVertex3f( 1, 0, 1);
		Engine.gl.glColor3f(0, 1, 0);	Engine.gl.glVertex3f(-1, 0, 1);
		Engine.gl.glColor3f(1, 0, 0);	Engine.gl.glVertex3f(-1, 0,-1);
		Engine.gl.glColor3f(0, 0, 0);	Engine.gl.glVertex3f( 1, 0,-1);
		Engine.gl.glColor3f(0, 0, 1);	Engine.gl.glVertex3f( 1, 0, 1);
		Engine.gl.glEnd();
		
		Engine.gl.glPopMatrix();
	}

	@Override
	public boolean markedForRemoval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setScale(float scale) {
		this.scale  = scale;
	}

	@Override
	public void sayHello(Sphere parent) {
		this.parent = parent;
	}
}
