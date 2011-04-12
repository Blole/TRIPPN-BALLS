package structures;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.TraceGL2;

import com.jogamp.common.nio.Buffers;
import common.TextureReader;
import engine.Engine;

public class VBO {
    private int vboID;
    private int iboID;
    private float[] vertices = new float[] {
    		 3, 3, 3,
    		 3, 3,-3,
    		 3,-3, 3,
    		 3,-3,-3,
    		-3, 3, 3,
    		-3, 3,-3,
    		-3,-3, 3,
    		-3,-3,-3,
    };
	private Mesh mesh;
    
	public VBO (){
//        Engine.gl.glColorMaterial( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE );
//        Engine.gl.glEnable( GL2.GL_COLOR_MATERIAL );
//        
//        Engine.gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, iboID );
//        Engine.gl.glEnableClientState( GL2.GL_VERTEX_ARRAY );
//        Engine.gl.glEnableClientState( GL2.GL_COLOR_ARRAY );
//        Engine.gl.glVertexPointer( 3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 0 );
//        Engine.gl.glColorPointer( 3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 3 * Buffers.SIZEOF_FLOAT );
//        Engine.gl.glPolygonMode( GL.GL_FRONT, GL2.GL_FILL );
//        Engine.gl.glDrawArrays( GL2.GL_QUADS, 0, vboID );
//
//        // disable arrays once we're done
//        Engine.gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0 );
//        Engine.gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
//        Engine.gl.glDisableClientState( GL2.GL_COLOR_ARRAY );
//        Engine.gl.glDisable( GL2.GL_COLOR_MATERIAL );
//		FloatBuffer data = ByteBuffer.allocateDirect(Buffers.SIZEOF_FLOAT*vertices.length).asFloatBuffer();
//		data.put(vertices);
//		
//        Engine.gl.glGenBuffers(1, vboID, 0);
//		Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID[0]);
//		Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT*data.capacity(), null, GL2.GL_DYNAMIC_DRAW);
//		Engine.gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, Buffers.SIZEOF_FLOAT*data.capacity(), data);
//		
//		ShortBuffer index = ByteBuffer.allocateDirect(Buffers.SIZEOF_SHORT*data.capacity()).asShortBuffer();
//		for (short i=0; i<data.capacity(); i++)
//			index.put(i);
//
//		Engine.gl.glGenBuffers(1, iboID, 0);
//		Engine.gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboID[0]);
//		Engine.gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, Buffers.SIZEOF_SHORT*data.capacity(), null, GL2.GL_DYNAMIC_DRAW);
//		Engine.gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, Buffers.SIZEOF_SHORT*index.capacity(), index);
//		
//		Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
//		Engine.gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Load The Mesh Data
		mesh = new Mesh();										// Instantiate Our Mesh
		try {
		    mesh.loadHeightmap("C:/Documents and Settings/bjorn/Desktop/Nehe/src/demos/data/images/Terrain.bmp", // Load Our Heightmap
		            1, 16);
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
        Engine.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);						// Black Background
        Engine.gl.glClearDepth(1.0f);										// Depth Buffer Setup
        Engine.gl.glDepthFunc(GL.GL_LEQUAL);									// The Type Of Depth Testing (Less Or Equal)
        Engine.gl.glEnable(GL.GL_DEPTH_TEST);									// Enable Depth Testing
        Engine.gl.glShadeModel(GL2.GL_SMOOTH);									// Select Smooth Shading
        Engine.gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);			// Set Perspective Calculations To Most Accurate
        Engine.gl.glEnable(GL.GL_TEXTURE_2D);									// Enable Textures
        Engine.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);						// Set The Color To White
	}
	
	public void render() {
        Engine.gl.glLoadIdentity();											// Reset The Modelview Matrix
		mesh.render();
		
//		Engine.gl.glLoadIdentity();
//		Engine.gl.glBegin(GL2.GL_LINES);
//		Engine.gl.glColor3f(1, 0, 1);
//		Engine.gl.glVertex2f(-5, -5);
//		Engine.gl.glVertex2f(5, 3);
//		Engine.gl.glEnd();
//		
//		Engine.gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
//		
//		//Engine.gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboID[0]);
//		Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID[0]);
//		
//		Engine.gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
//		
//        Engine.gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
//		
//		Engine.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
//		
//		Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
//		Engine.gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		//Engine.gl.glDrawRangeElements(GL2.GL_LINES, 0, arg2, arg3, GL2.GL_UNSIGNED_SHORT, 0);
		
		//Engine.gl.glDrawArrays(GL2.GL_POINTS, 0, data.capacity());
		// disable arrays once we're done
		//Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		//Engine.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
	
	@SuppressWarnings("unused")
	private class Vertex {
		public float x, y, z;        //Vertex
		public float nx, ny, nz;     //Normal
		public float s0, t0;         //Texcoord0
		public float s1, t1;         //Texcoord1
		public float s2, t2;         //Texcoord2
		public float padding, p2, p3, p4;
		
		public Vertex (
				float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Vertex (
				float x, float y, float z,
				float nx, float ny, float nz) {
			this.x = x;
			this.y = y;
			this.z = z;
			
			this.nx = nx;
			this.ny = ny;
			this.nz = nz;
		}
		
		public Vertex (
				float x, float y, float z,
				float nx, float ny, float nz,
				float s0, float t0,
				float s1, float t1,
				float s2, float t2) {
			this.x = x;
			this.y = y;
			this.z = z;
			
			this.nx = nx;
			this.ny = ny;
			this.nz = nz;
			
			this.s0 = s0;
			this.t0 = t0;
			
			this.s1 = s1;
			this.t1 = t1;
			
			this.s2 = s2;
			this.t2 = t2;
		}
	}
	private class Mesh {
        // Mesh Data
        private int vertexCount;								// Vertex Count
        private FloatBuffer vertices;								// Vertex Data
        private FloatBuffer texCoords;								// Texture Coordinates
        private int[] textureId = new int[1];								// Texture ID

        // Vertex Buffer Object Names
        private int[] VBOVertices = new int[1];								// Vertex VBO Name
        private int[] VBOTexCoords = new int[1];							// Texture Coordinate VBO Name

        public boolean loadHeightmap(String szPath, float flHeightScale, float flResolution) throws IOException {
            TextureReader.Texture texture = null;
            texture = TextureReader.readTexture(szPath);
            Engine.out.printf("textureId:%d   VBOVertices:%d   VBOTexCoords:%d\n", textureId[0], VBOVertices[0], VBOTexCoords[0]);

            // Generate Vertex Field
            vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 / (flResolution * flResolution));
            vertices  = ByteBuffer.allocateDirect(vertexCount * 3 * Buffers.SIZEOF_FLOAT).asFloatBuffer();
            texCoords = ByteBuffer.allocateDirect(vertexCount * 2 * Buffers.SIZEOF_FLOAT).asFloatBuffer();
            for (int nZ = 0; nZ < texture.getHeight(); nZ += (int) flResolution) {
                for (int nX = 0; nX < texture.getWidth(); nX += (int) flResolution) {
                    for (int nTri = 0; nTri < 6; nTri++) {
                        // Using This Quick Hack, Figure The X,Z Position Of The Point
                        float flX = (float) nX + ((nTri == 1 || nTri == 2 || nTri == 5) ? flResolution : 0.0f);
                        float flZ = (float) nZ + ((nTri == 2 || nTri == 4 || nTri == 5) ? flResolution : 0.0f);

                        // Set The Data, Using PtHeight To Obtain The Y Value
                        vertices.put(flX - (texture.getWidth() / 2f));
                        vertices.put(pointHeight(texture, (int) flX, (int) flZ) * flHeightScale);
                        vertices.put(flZ - (texture.getHeight() / 2f));

                        // Stretch The Texture Across The Entire Mesh
                        texCoords.put(flX / texture.getWidth());
                        texCoords.put(flZ / texture.getHeight());
                    }
                }
            }
            vertices.flip();
            texCoords.flip();
            System.out.println("lolb");
            while (vertices.hasRemaining())
            	System.out.printf("%8.2f%8.2f%8.2f\n",vertices.get(), vertices.get(), vertices.get());
            vertices.flip();

            // Load The Texture Into OpenGL
            Engine.gl = new TraceGL2(Engine.gl, Engine.err);
            Engine.gl.glGenTextures(1, textureId, 0);							// Get An Open ID
            Engine.gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);				// Bind The Texture
            Engine.gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
            Engine.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            Engine.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            Engine.out.printf("textureId:%d   VBOVertices:%d   VBOTexCoords:%d\n", textureId[0], VBOVertices[0], VBOTexCoords[0]);
            buildVBOs();									// Build The VBOs

            Engine.out.printf("textureId:%d   VBOVertices:%d   VBOTexCoords:%d\n", textureId[0], VBOVertices[0], VBOTexCoords[0]);
            Engine.gl = Engine.gl.getGL2();
            return true;
        }

        public void render() {
            // Enable Pointers
            Engine.gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);						// Enable Vertex Arrays
            Engine.gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);				// Enable Texture Coord Arrays

            // Set Pointers To Our Data
            //if (Engine.usesVBO()) {
                Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOTexCoords[0]);
                Engine.gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);		// Set The TexCoord Pointer To The TexCoord Buffer
                Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);
                Engine.gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);		// Set The Vertex Pointer To The Vertex Buffer
//            } else {
//                Engine.gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices); // Set The Vertex Pointer To Our Vertex Data
//                Engine.gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texCoords); // Set The Vertex Pointer To Our TexCoord Data
//            }

            // Render
            Engine.gl.glPointSize(20);
            Engine.gl.glDrawArrays(GL2.GL_POINTS, 0, vertexCount);	// Draw All Of The Triangles At Once

            // Disable Pointers
            Engine.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);					// Disable Vertex Arrays
            Engine.gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);				// Disable Texture Coord Arrays
        }

        private float pointHeight(TextureReader.Texture texture, int nX, int nY) {
            // Calculate The Position In The Texture, Careful Not To Overflow
            int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) * texture.getWidth())) * 3;
            float flR = unsignedByteToInt(texture.getPixels().get(nPos));			// Get The Red Component
            float flG = unsignedByteToInt(texture.getPixels().get(nPos + 1));		// Get The Green Component
            float flB = unsignedByteToInt(texture.getPixels().get(nPos + 2));		// Get The Blue Component
            return (0.299f * flR + 0.587f * flG + 0.114f * flB);		// Calculate The Height Using The Luminance Algorithm
        }

        private int unsignedByteToInt(byte b) {
            return (int) b & 0xFF;
        }

        private void buildVBOs() {
            // Generate And Bind The Vertex Buffer
            Engine.gl.glGenBuffers(1, VBOVertices, 0);							// Get A Valid Name
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);			// Bind The Buffer
            // Load The Data
            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);

            // Generate And Bind The Texture Coordinate Buffer
            Engine.gl.glGenBuffers(1, VBOTexCoords, 0);							// Get A Valid Name
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOTexCoords[0]);		// Bind The Buffer
            // Load The Data
            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 2 * Buffers.SIZEOF_FLOAT, texCoords, GL2.GL_STATIC_DRAW);

            // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
            vertices = null;
            texCoords = null;
        }
    }
}