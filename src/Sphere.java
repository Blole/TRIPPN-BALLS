import javax.media.opengl.GL2;

public class Sphere extends Model {
	private float radius;
	
	public Sphere(Vector pos) {
		this(pos, 1, 20, 20);
	}
	
	public Sphere(Vector pos, float radius, int longitude, int latitude) {
		this(pos, 0, 0, 0, radius, longitude, latitude);
	}
	
	public Sphere(Vector pos, float pitch, float yaw, float roll, float radius, int longitude, int latitude) {
		super(pos, pitch, yaw, roll);
		this.setRadius(radius);
		createPolygons(longitude, latitude);
		speed = new Vector(0,0,.001f);
	}
	
	@Override
	public void move() {
		if (affectedByGravity)
			speed.addSelf(gravity);
		pos.addSelf(speed);
	}

	@Override
	public void render(GL2 gl, int optimization) {
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(pos.x, pos.y, pos.z);
        gl.glBegin(GL2.GL_QUADS);
		for (int i=0; i<polygons.length; i+=optimization) {
			for (int j=0; j<8;) {
				gl.glColor3fv (polygons[i][j++], 0);
				gl.glVertex3fv(polygons[i][j++], 0);
			}
		}
		gl.glEnd();
		gl.glPopMatrix();
	}

	private void createPolygons(int longitude, int latitude) {
		polygons = new float[longitude*latitude*2][8][3];
		
		for (int row=0; row<latitude; row++) {
			double latAngle   = (double) row   /latitude*Math.PI;
			double latAngle2  = (double)(row+1)/latitude*Math.PI;
			for (int corner=0; corner < longitude; corner++) {
				double longAngle  = (double) corner   /longitude*Math.PI*2;
				double longAngle2 = (double)(corner+1)/longitude*Math.PI*2;
				
				int i = row*longitude+corner;
				polygons[i][0] = polygons[i][1] = new float[] {
						(float)(Math.sin(longAngle)*Math.sin(latAngle)*radius),
						(float)(Math.cos(longAngle)*Math.sin(latAngle)*radius),
						(float)Math.cos(latAngle)*radius, };
				polygons[i][2] = polygons[i][3] = new float[] {
						(float)(Math.sin(longAngle2)*Math.sin(latAngle)*radius),
						(float)(Math.cos(longAngle2)*Math.sin(latAngle)*radius),
						(float)Math.cos(latAngle)*radius, };
				polygons[i][4] = polygons[i][5] = new float[] {
						(float)(Math.sin(longAngle2)*Math.sin(latAngle2)*radius),
						(float)(Math.cos(longAngle2)*Math.sin(latAngle2)*radius),
						(float)Math.cos(latAngle2)*radius, };
				polygons[i][6] = polygons[i][7] = new float[] {
						(float)(Math.sin(longAngle)*Math.sin(latAngle2)*radius),
						(float)(Math.cos(longAngle)*Math.sin(latAngle2)*radius),
						(float)Math.cos(latAngle2)*radius, };
			}
		}
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}

	public void attract(Sphere other) {
		float xDiff = other.pos.x-pos.x;
		float yDiff = other.pos.y-pos.y;
		float zDiff = other.pos.z-pos.z;
		float distance = pos.distanceTo(other.pos);
		float attraction = 1f/(distance*distance-1);
		
		speed.x += xDiff*attraction;
		speed.y += yDiff*attraction;
		speed.z += zDiff*attraction;
		other.speed.x -= xDiff*attraction;
		other.speed.y -= yDiff*attraction;
		other.speed.z -= zDiff*attraction;
	}
}
