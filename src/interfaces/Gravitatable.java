package interfaces;

public interface Gravitatable extends Moveable {
	public void attract(Gravitatable other);
	public void setAffectedByGravity(boolean affectedByGravity);
	public boolean isAffectedByGravity();
	public float getRadius();
}
