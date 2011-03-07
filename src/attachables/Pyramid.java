package attachables;

import javax.media.opengl.GL2;

import models.Sphere;

public class Pyramid implements SphereAttachable {
	private Sphere parent;
	private float scale = 1;
	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;
	private float angleChange;
	
	public Pyramid (float yaw, float pitch, float rollIncrement) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.angleChange = rollIncrement;
	}

	@Override
	public void move() {
		roll += angleChange;
	}

	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		gl.glRotatef(yaw,   0, 1, 0);
		gl.glRotatef(pitch, 1, 0, 0);
		gl.glRotatef(roll,  0, 1, 0);
		gl.glTranslatef(0, parent.getRadius(), 0);
		
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glColor3f(1, 1, 1);	gl.glVertex3f(     0, scale,     0);
		gl.glColor3f(0, 0, 1);	gl.glVertex3f( scale,     0, scale);
		gl.glColor3f(0, 1, 0);	gl.glVertex3f(-scale,     0, scale);
		gl.glColor3f(1, 0, 0);	gl.glVertex3f(-scale,     0,-scale);
		gl.glColor3f(0, 0, 0);	gl.glVertex3f( scale,     0,-scale);
		gl.glColor3f(0, 0, 1);	gl.glVertex3f( scale,     0, scale);
		gl.glEnd();
		
		gl.glPopMatrix();
	}

	@Override
	public boolean markedForRemoval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float initialRotation() {
		// TODO Auto-generated method stub
		return 0;
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
