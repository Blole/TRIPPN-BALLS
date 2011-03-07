import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.opengl.util.FPSAnimator;

public class Engine implements GLEventListener {
	private static final long serialVersionUID = 1L;
	private static boolean fullscreen=false;//can be toggled
	private static Frame frame;
	private Input input;
	private Game game;
	public FPSAnimator animator;

    public static void main(String[] args) {
		GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(new Engine(canvas));
	}
    
    public Engine (GLCanvas canvas) {
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
		
		input = new Input();
		frame.addWindowListener(input);
        canvas.addKeyListener(input);
        canvas.addMouseListener(input);
        canvas.addMouseWheelListener(input);
        canvas.addFocusListener(input);
        
        animator = new FPSAnimator(canvas, 60);
        animator.add(canvas);
        animator.start();
        animator.getTotalFrames();
    }
    
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = (GL2) drawable.getGL();
		GLUgl2 glu = new GLUgl2();
		
		if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
			System.out.println("Error: VBO support is missing");
		
		
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		game=new Game(gl, glu, input);
	}
	
	public static Frame getFrame() {
		return frame;
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		game.tick();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int topY, int topX, int width, int height) {
		game.reshape(topX, topY, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("disposed");
	}
}