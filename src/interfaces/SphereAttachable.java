package interfaces;

import models.Sphere;

public interface SphereAttachable {
	public void sayHello(Sphere parent);
	public void move();
	public void render();
	public boolean markedForRemoval();
	public void setScale(float scale);
}
