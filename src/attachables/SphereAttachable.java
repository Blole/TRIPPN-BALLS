package attachables;
import javax.media.opengl.GL2;

import models.Sphere;

public interface SphereAttachable {
	public void sayHello(Sphere parent);
	public void move();
	public void render(GL2 gl);
	public boolean markedForRemoval();
	public float initialRotation();
	public void setScale(float scale);
}
