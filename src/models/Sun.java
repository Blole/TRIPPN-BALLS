package models;

import javax.media.opengl.GL2;

import engine.Engine;
import engine.ModelLoader;

import structures.VBOinfo;


public class Sun {
	private VBOinfo vbo;
	private float pitch;
	private float yaw;
	public Sun() {
		vbo = ModelLoader.loadStatic("sphere");
	}
	public void render() {
		pitch += 0.1f;
		yaw += 	1f;
		Engine.gl.glLoadIdentity();
		Engine.gl.glRotatef(pitch, 1, 0, 0);
		Engine.gl.glRotatef(yaw, 0, 1, 0);
		Engine.gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{100,0,0,1}, 0);
//		Engine.gl.glLoadIdentity();
//		Engine.gl.glRotatef(-pitch, 1, 0, 0);
//		Engine.gl.glRotatef(-yaw, 0, 1, 0);
		Engine.gl.glTranslatef(100,0,0);
		Engine.gl.glScalef(20, 20, 20);
		Engine.gl.glColor3f(1,1,0);
		Engine.enableOldColoredDrawing(true);
		Engine.drawVBO(vbo);
		//Engine.drawNormals(vbo, 200);
		Engine.enableOldColoredDrawing(false);
	}
}
