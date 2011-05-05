package models;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import structures.VBOinfo;
import structures.Vector;

import com.jogamp.common.nio.Buffers;
import common.TextureReader;
import common.TextureReader.Texture;

import engine.Engine;

public final class WorldMesh {
	private static final int MESH_RESOLUTION = 8;
	private static final float MESH_HEIGHTSCALE = 0.05f;
	private VBOinfo vbo;
	private Texture map;
	private int[] textureId = new int[1];
	
	public WorldMesh () {
		
	}
	public void render() {
		Engine.gl.glLoadIdentity();
		Engine.drawVBO(vbo);
		Engine.drawNormals(vbo, 10);
	}
	
	/**
	 * Tries to load given map from the /maps/ directory.
	 * @param heightMapPath
	 */
	public void loadHeightMap(String heightMapPath) {
		try {
		    map = TextureReader.readTexture("maps/"+heightMapPath);
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
		int height = (int) (map.getHeight()/MESH_RESOLUTION)+1;
		int width  =  (int) (map.getWidth()/MESH_RESOLUTION)+1;
		
		
		vbo = new VBOinfo();
		vbo.drawType = GL2.GL_TRIANGLES;
		vbo.chunkSize = (3+3+2)*Buffers.SIZEOF_FLOAT;
		int vertices = (int) width*height;
		
		//(3+2)*vbo.indices*Buffers.SIZEOF_FLOAT
		vbo.vbo = Engine.allocateVBO(vertices*vbo.chunkSize, GL2.GL_STATIC_DRAW);
		FloatBuffer vb = Engine.mapVBO(vbo.vbo).asFloatBuffer();
		for (int z = 0; z <= map.getHeight(); z += (int) MESH_RESOLUTION) {
		    for (int x = 0; x <= map.getWidth(); x += (int) MESH_RESOLUTION) {
		        vb
		        .put(x - map.getWidth()/2)
//		        .put(pointHeight(x, z))
		        .put(10)
		        .put(z - map.getHeight()/2);
		        
		        int left = Math.max(x-MESH_RESOLUTION, 0);
		        int right = Math.min(x+MESH_RESOLUTION, map.getWidth());
		        int up = Math.max(z-MESH_RESOLUTION, 0);
		        int down = Math.min(z+MESH_RESOLUTION, map.getHeight());
		        Vector v = new Vector(0,pointHeight(x, z),0);
		        Vector u = new Vector(0,pointHeight(x, up)  -pointHeight(x, down) ,2*MESH_RESOLUTION);
		        Vector normal = v.unit();
		        vb
		        .put(0)
		        .put(1)
		        .put(0);
//		        .put(normal.x)
//		        .put(normal.y)
//		        .put(normal.z);
		        vb.put(1f*x / map.getWidth());
		        vb.put(1f*z / map.getHeight());
		    }
		}
		//Engine.debugBuffer("world", vb, 8);
		Engine.unmapVBO(vbo.vbo);
		vbo.indices = (height-1)*(width-1)*6;
		vbo.ibo = Engine.allocateVBO(vbo.indices*Buffers.SIZEOF_SHORT, GL2.GL_STATIC_DRAW);
		ShortBuffer ib = Engine.mapVBO(vbo.ibo).asShortBuffer();
		for (int z = 0; z < height-1; z += 1) {
			for (int x = 0; x < width-1; x += 1) {
				int a = z*height+x;
				int b = a+height;
				ib
				.put((short) (a+1))
				.put((short) (a))
				.put((short) (b))
				.put((short) (a+1))
				.put((short) (b))
				.put((short) (b+1));
			}
		}
		//Engine.debugBuffer("world", ib, 3);
		Engine.unmapVBO(vbo.ibo);
		
		// Load The Texture Into OpenGL
		Engine.gl.glGenTextures(1, textureId , 0);
		Engine.gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId[0]);
		Engine.gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, map.getWidth(), map.getHeight(),
				0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, map.getPixels());
		Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	}

    private float pointHeight(int x, int z) {
        // Calculate The Position In The Texture, Careful Not To Overflow
        int bytePos = ((x % map.getWidth()) + ((z % map.getHeight()) * map.getWidth())) * 3;
        float red   = unsignedByteToInt(map.getPixels().get(bytePos));		// Red Component
        float green = unsignedByteToInt(map.getPixels().get(bytePos + 1));	// Green Component
        float blue  = unsignedByteToInt(map.getPixels().get(bytePos + 2));	// Blue Component
        return MESH_HEIGHTSCALE*(0.299f * red + 0.587f * green + 0.114f * blue);		// Calculate The Height Using The Luminance Algorithm
    }
    private static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
}
