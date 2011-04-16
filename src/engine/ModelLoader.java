package engine;


import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import structures.VBOinfo;


import com.jogamp.common.nio.Buffers;


public final class ModelLoader {
	private static final Map<String,VBOinfo> loaded = new HashMap<String,VBOinfo>();

	public static VBOinfo loadStatic(String name) {
		VBOinfo load = loaded.get(name);
		if (load != null)
			return load;
		
		if (name.equals("sphere"))
			load = sphere();
		else
			throw new RuntimeException(String.format("Error loading model %s, not found", name));
		
		loaded.put(name, load);
		return load;
	}
	
	private static VBOinfo sphere() {
		final int longitude = 20;
		final int latitude = 20;
		
		VBOinfo vbo = new VBOinfo();
		vbo.drawType = GL2.GL_QUADS;
		int vertices = longitude*(latitude-2)+2;
		vbo.chunkSize = (3+3+2)*Buffers.SIZEOF_FLOAT;
		vbo.vbo = Engine.allocateVBO(vertices*vbo.chunkSize, GL2.GL_STATIC_DRAW);
		FloatBuffer vb = Engine.mapVBO(vbo.vbo).asFloatBuffer();
		vb.put(0).put(1).put(0);
		vb.put(0).put(1).put(0);
		vb.put(0).put(0);
		for (float i=1; i<latitude-1; i++) {
			for (float j=0; j<longitude; j++) {
				vb
				.put(cos(2*j/longitude)*sin(i/(latitude-1)))
				.put(cos(i/(latitude-1)))
				.put(sin(2*j/longitude)*sin(i/(latitude-1)));
				vb
				.put(cos(2*j/longitude)*sin(i/(latitude-1)))
				.put(cos(i/(latitude-1)))
				.put(sin(2*j/longitude)*sin(i/(latitude-1)));
				vb
				.put(0)
				.put(0);
			}
		}
		vb.put(0).put(-1).put(0);
		vb.put(0).put(-1).put(0);
		vb.put(0).put(0);
		//Engine.debugBuffer("sphere", vb, 8);
		Engine.unmapVBO(vbo.vbo);
		
		vbo.indices = longitude*(latitude-1)*4;
		vbo.ibo = Engine.allocateVBO(vbo.indices*Buffers.SIZEOF_SHORT, GL2.GL_STATIC_DRAW);
		ShortBuffer ib = Engine.mapVBO(vbo.ibo).asShortBuffer();
		for (int i=1; i<=longitude; i++) {
			int lOffset = (short)((i-1)%longitude+1);
			int rOffset = (short)(i%longitude+1);
			ib
			.put((short)0)
			.put((short)0);
			int row;
			for (row=0; row<latitude-2; row++) {
				ib
				.put((short) (row*longitude+lOffset))
				.put((short) (row*longitude+rOffset))
				.put((short) (row*longitude+rOffset))
				.put((short) (row*longitude+lOffset));
			}
			ib
			.put((short) (vertices-1))
			.put((short) (vertices-1));
		}
		//Engine.debugBuffer("sphere", ib, 4);
		Engine.unmapVBO(vbo.ibo);
		
		return vbo;
	}

	private static float sin(float part) {
		return (float)Math.sin(Math.PI*part);
	}
	private static float cos(float part) {
		return (float)Math.cos(Math.PI*part);
	}
}
