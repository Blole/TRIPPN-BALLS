package models;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import structures.VBOinfo;

import com.jogamp.common.nio.Buffers;
import common.TextureReader;

import engine.Engine;

public final class WorldMesh {
    private static final float MESH_RESOLUTION = 1.0f;
    private static final float MESH_HEIGHTSCALE = 0.05f;
    private VBOinfo vbo;
    
	public WorldMesh () {
		
	}
	public void render() {
		Engine.drawVBO(vbo);
	}
	
	/**
	 * Tries to load given map from the /maps/ directory.
	 * @param heightMapPath
	 */
	public void loadHeightMap(String heightMapPath) {
        TextureReader.Texture map;
        try {
            map = TextureReader.readTexture("maps/"+heightMapPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        vbo = new VBOinfo();
        vbo.drawType = GL2.GL_TRIANGLES;
        vbo.chunkSize = (3+3+2)*Buffers.SIZEOF_FLOAT;
        int vertices = (int) (map.getWidth() * map.getHeight() / (MESH_RESOLUTION * MESH_HEIGHTSCALE));
        
        //(3+2)*vbo.indices*Buffers.SIZEOF_FLOAT
        vbo.vbo = Engine.allocateVBO(vertices*vbo.chunkSize, GL2.GL_STATIC_DRAW);
        FloatBuffer vb = Engine.mapVBO(vbo.vbo).asFloatBuffer();
        for (int z = 0; z < map.getHeight(); z += (int) MESH_RESOLUTION) {
            for (int x = 0; x < map.getWidth(); x += (int) MESH_RESOLUTION) {
                vb.put(x - map.getWidth()/2);
                vb.put(pointHeight(map, x, z) * MESH_HEIGHTSCALE);
                vb.put(z - map.getHeight()/2);

                vb.put(0);
                vb.put(pointHeight(map, x, z) * MESH_HEIGHTSCALE);
                vb.put(0);
                // Stretch The Texture Across The Entire Mesh
                vb.put(x / map.getWidth());
                vb.put(z / map.getHeight());
            }
        }
//        ver.rewind();
//        for (int i=0; i < 96; i++)
//    	System.out.printf("vertices:  %8.1f%8.1f%8.1f\n",
//    			ver.get(), ver.get(), ver.get());
        Engine.unmapVBO(vbo.vbo);
        vbo.indices = (map.getHeight()-1)*(map.getWidth()-1)*6;
        vbo.ibo = Engine.allocateVBO(vbo.indices*Buffers.SIZEOF_SHORT, GL2.GL_STATIC_DRAW);
        ShortBuffer ib = Engine.mapVBO(vbo.ibo).asShortBuffer();
        for (int z = 0; z < map.getHeight()-1; z += (int) MESH_RESOLUTION) {
            for (int x = 0; x < map.getWidth()-1; x += (int) MESH_RESOLUTION) {
            	int a = z*map.getHeight()+x;
            	int b = a+map.getHeight();
                ib
                .put((short) (a+1))
                .put((short) (a))
                .put((short) (b))
                .put((short) (a+1))
                .put((short) (b))
                .put((short) (b+1));
            }
        }
        Engine.unmapVBO(vbo.ibo);
        
        int textureId[] = new int[1];
        // Load The Texture Into OpenGL
        Engine.gl.glGenTextures(1, textureId, 0);
        Engine.gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId[0]);
        Engine.gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, map.getWidth(), map.getHeight(),
        		0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, map.getPixels());
        Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        
        Engine.setTitle(String.format("%d Points:", vbo.indices));
	}

    private float pointHeight(TextureReader.Texture texture, int x, int z) {
        // Calculate The Position In The Texture, Careful Not To Overflow
        int bytePos = ((x % texture.getWidth()) + ((z % texture.getHeight()) * texture.getWidth())) * 3;
        float red   = unsignedByteToInt(texture.getPixels().get(bytePos));		// Red Component
        float green = unsignedByteToInt(texture.getPixels().get(bytePos + 1));	// Green Component
        float blue  = unsignedByteToInt(texture.getPixels().get(bytePos + 2));	// Blue Component
        return (0.299f * red + 0.587f * green + 0.114f * blue);		// Calculate The Height Using The Luminance Algorithm
    }
    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
}
