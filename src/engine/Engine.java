package engine;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import structures.Entity;
import structures.VBOinfo;


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;

public final class Engine implements GLEventListener {
	private static final long serialVersionUID = 1L;
	private static boolean fullscreen=false;
	private static Frame frame;
	public static FPSAnimator animator;
	private static String title = "";
	private static float fps;
	
	public static Random random = new Random();
	private static GLCanvas canvas;
	public static GL2 gl;
	public static final PrintStream out = System.out;
	public static final PrintStream err = System.err;
	
	public static Map<String, TimeThing> times = new HashMap<String, TimeThing>();
	public static Stopwatch renderingTime = new Stopwatch();
	private static float angle;

    public static void main(String[] args) {
    	LuaLoader.init();
		Input.init();
    	new Engine();
	}
    
    public Engine () {
		canvas = new GLCanvas();
        frame = new Frame("TRIPPIN' BALLS");
		frame.add(canvas);
		frame.setSize(600, 600);
		if (fullscreen)
			frame.setUndecorated(true);
		frame.setVisible(true);
        
        if(fullscreen) {
        	GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        	ge.getDefaultScreenDevice().setFullScreenWindow(frame);
        }
		
		frame.addWindowListener(Input.listener);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(Input.listener);
		canvas.addMouseListener(Input.listener);
		canvas.addMouseWheelListener(Input.listener);
		canvas.addFocusListener(Input.listener);
		animator = new FPSAnimator(canvas, 60);
		animator.add(canvas);
		animator.start();
    }
    
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
    	
		if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
			throw new RuntimeException("Graphics card does not support Vertex Buffer Objects, TERMINATED");
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		float mat_specular[] = { 1, 1, 1, 1 };
	    float mat_shininess[] = { 30 };
	    float light_position[] = { 20, 20, 20, 0 };
	    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
	    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
//	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
//	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse, 0)
	    
    	Game.init();
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		Game.tick();
		updateFPS();
		gl.glFlush();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int topY, int topX, int width, int height) {
		Game.reshape(topX, topY, width, height);
	}
	
	public static void setTitle(String title) {
		Engine.title = title;
	}
	
	private static void updateTitle() {
		long rend = times.get("render").getTimeAndReset();
		long coll = times.get("collide").getTimeAndReset();
		frame.setTitle(String.format("TRIPPN' BALLS %2.1f fps   %s   rend:%5.0f%% coll:%5.0f%% entities: %d",
				fps, title, 100f*rend/(rend+coll), 100f*coll/(rend+coll), Entity.all.size()));
	}
	
	private static void updateFPS() {
		if (animator.getDuration() > 400) {
			fps = 1000.0f*animator.getTotalFrames()/animator.getDuration();
			animator.resetCounter();
			updateTitle();
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("disposed");
	}
	
	/**
	 * @param size in _bytes_ to be allocated.
	 * @param glDrawMode GL_STATIC_DRAW, or the like 
	 * @return id of the newly allocated VBO.
	 */
	public static int allocateVBO(int size, int glDrawMode) {
		int[] vboID = new int[1];
        gl.glGenBuffers(1, vboID, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID[0]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, size, null, glDrawMode);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		return vboID[0];
	}
	public static ByteBuffer mapVBO(int id) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		ByteBuffer buffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		return buffer;
	}
	public static void unmapVBO(int id) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}
	public static void drawVBO(VBOinfo vbo) {
//		gl.glLoadIdentity();
		//gl.glRotatef(angle+=150f,0,1,0);
		gl.glColor3f(0,1,0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vbo.ibo);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.vbo);
		gl.glVertexPointer(3,	GL2.GL_FLOAT, vbo.chunkSize, 0);
		gl.glNormalPointer(		GL2.GL_FLOAT, vbo.chunkSize, 3*3*Buffers.SIZEOF_FLOAT);
		gl.glTexCoordPointer(2,	GL2.GL_FLOAT, vbo.chunkSize, 3*6*Buffers.SIZEOF_FLOAT);
		gl.glDrawElements(vbo.drawType, vbo.indices, GL2.GL_UNSIGNED_SHORT, 0);
//		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
//		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	public static void drawNormals(VBOinfo vbo, int length) {
		enableOldColoredDrawing(true);
		gl.glLineWidth(2);
		FloatBuffer b = mapVBO(vbo.vbo).asFloatBuffer();
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0.1f,1,0.1f);
		while (b.hasRemaining()) {
			float vertex[] = new float[]{b.get(), b.get(), b.get()};
			float normal[] = new float[]{b.get(), b.get(), b.get()};
			gl.glVertex3fv(vertex, 0);
			gl.glVertex3f(vertex[0]+normal[0]*length, vertex[1]+normal[1]*length, vertex[2]+normal[2]*length);
			b.get();b.get();
		}
		gl.glEnd();
		unmapVBO(vbo.vbo);
		enableOldColoredDrawing(false);
	}
	
	public static void enableOldColoredDrawing(boolean enable) {
		if (enable) {
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glDisable(GL2.GL_LIGHT0);
		}
		else {
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
		}
	}
	
	public static void deleteVBO(int id) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		gl.glDeleteBuffers(1, new int[]{id}, 1);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}
	
	public static Frame getFrame() {
		return frame;
	}
	
	public static void phase(String key, boolean start) {
		TimeThing timeThing = times.get(key);
		if (timeThing == null) {
			timeThing = new TimeThing();
			times.put(key, timeThing);
		}
		if (start)
			timeThing.start();
		else
			timeThing.stop();
			
	}
	private static final class TimeThing {
		private long startTime;
		private long time;
		public void start() {
			startTime = System.nanoTime();
		}
		public void stop() {
			time += System.nanoTime()-startTime;
		}
		public long getTimeAndReset() {
			long temp = time;
			time = 0;
			return temp;
		}
	}
	public static void debugBuffer(String start, FloatBuffer vb, int gets) {
		vb.rewind();
		start += " vb: ";
		System.out.println(start+vb);
		while (vb.hasRemaining()) {
			System.out.print(start);
			for (int i=0; i<gets; i++)
				System.out.printf("%7.1f",vb.get());
			System.out.println();
		}
	}
	public static void debugBuffer(String start, ShortBuffer ib, int gets) {
		ib.rewind();
		
		start += " ib: ";
		System.out.println(start+ib);
		while (ib.hasRemaining()) {
			System.out.print(start);
			for (int i=0; i<gets; i++)
				System.out.printf("%5d",ib.get());
			System.out.println();
		}
	}

	public static Rectangle getAbsoluteCanvasBounds() {
		Rectangle bounds = canvas.getBounds();
		bounds.x += frame.getX();
		bounds.y += frame.getY();
		return bounds;
	}
}