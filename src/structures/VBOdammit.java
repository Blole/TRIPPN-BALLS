package structures;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import common.TextureReader;
import engine.Engine;

public class VBOdammit {
    // Mesh Generation Paramaters
    private static final float MESH_RESOLUTION = 1.0f;									// Pixels Per Vertex
    private static final float MESH_HEIGHTSCALE = 0.05f;									// Mesh Height Scale
    private GL2 gl;

    private Mesh mesh = null;										// Mesh Data
    private float yRotation = 0.0f;									// Rotation
    private long previousTime = System.currentTimeMillis();

    public VBOdammit() {
        mesh = new Mesh();										// Instantiate Our Mesh
        try {
            mesh.loadHeightmap(gl,
            		"C:/Documents and Settings/bjorn/Desktop/Nehe/src/demos/data/images/tim.png",
                    MESH_HEIGHTSCALE, MESH_RESOLUTION);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void update(long milliseconds) {
        yRotation += (float) (milliseconds) / 10000.0f * 25.0f;
    }

    public void render() {
        long time = System.currentTimeMillis();
        update(time - previousTime);
        previousTime = time;

        Engine.gl.glPushMatrix();
        Engine.gl.glLoadIdentity();

        Engine.gl.glColor4f(.7f,.7f,1,0);
        Engine.gl.glTranslatef(0, 2, 0);
        Engine.gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);

        // Render the mesh
        mesh.render(gl);
        Engine.gl.glPopMatrix();
    }

    private class Mesh {
        // Mesh Data
        private int vertexCount;								// Vertex Count
        private int[] textureId = new int[1];								// Texture ID

        // Vertex Buffer Object Names
        private int verID;
        private int texID;

        public int getVertexCount() {
            return vertexCount;
        }

        public boolean loadHeightmap(GL2 gl, String szPath, float flHeightScale, float flResolution) throws IOException {
            TextureReader.Texture texture = null;
            texture = TextureReader.readTexture(szPath);

            // Generate Vertex Field
            vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 / (flResolution * flResolution));
            verID = Engine.allocateVBO(vertexCount*3*Buffers.SIZEOF_FLOAT, GL2.GL_STATIC_DRAW);
            texID = Engine.allocateVBO(vertexCount*2*Buffers.SIZEOF_FLOAT, GL2.GL_STATIC_DRAW);
            FloatBuffer ver = Engine.mapVBO(verID).asFloatBuffer();
            FloatBuffer tex = Engine.mapVBO(texID).asFloatBuffer();
            for (int nZ = 0; nZ < texture.getHeight(); nZ += (int) flResolution) {
                for (int nX = 0; nX < texture.getWidth(); nX += (int) flResolution) {
                    for (int nTri = 0; nTri < 6; nTri++) {
                        // Using This Quick Hack, Figure The X,Z Position Of The Point
                        float flX = (float) nX + ((nTri == 1 || nTri == 2 || nTri == 5) ? flResolution : 0.0f);
                        float flZ = (float) nZ + ((nTri == 2 || nTri == 4 || nTri == 5) ? flResolution : 0.0f);

                        // Set The Data, Using PtHeight To Obtain The Y Value
                        ver.put(flX - (texture.getWidth() / 2f));
                        ver.put(pointHeight(texture, (int) flX, (int) flZ) * flHeightScale);
                        ver.put(flZ - (texture.getHeight() / 2f));

                        // Stretch The Texture Across The Entire Mesh
                        tex.put(flX / texture.getWidth());
                        tex.put(flZ / texture.getHeight());
                    }
                }
            }
//            ver.rewind();
//            for (int i=0; i < 96; i++)
//        	System.out.printf("vertices:  %8.1f%8.1f%8.1f\n",
//        			ver.get(), ver.get(), ver.get());
            Engine.unmapVBO(verID);
            Engine.unmapVBO(texID);

            // Load The Texture Into OpenGL
            Engine.gl.glGenTextures(1, textureId, 0);
            Engine.gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId[0]);
            Engine.gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(),
            		0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, texture.getPixels());
            Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            Engine.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

            //buildVBOs(gl);
            
            Engine.setTitle(String.format("%d Tris", mesh.getVertexCount()));

            return true;
        }

        public void render(GL2 gl) {
            // Enable Pointers
            Engine.gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            Engine.gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

            // Set Pointers To Our Data
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.texID);
            Engine.gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.verID);
            Engine.gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);

            // Render
            Engine.gl.glDrawArrays(GL2.GL_LINES, 0, mesh.vertexCount);

            // Disable Pointers
            Engine.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
            Engine.gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
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
        /*
        private void buildVBOs(GL2 glass) {
            // Generate And Bind The Vertex Buffer
            Engine.gl.glGenBuffers(1, verID, 0);							// Get A Valid Name
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, verID);			// Bind The Buffer
            // Load The Data
            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * Buffers.SIZEOF_FLOAT, null, GL2.GL_STATIC_DRAW);
            FloatBuffer loadInto = Engine.gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY).asFloatBuffer();
            loadInto.put(vertices);
            Engine.gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
            // Generate And Bind The Texture Coordinate Buffer
            Engine.gl.glGenBuffers(1, texID, 0);							// Get A Valid Name
            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, texID);		// Bind The Buffer
            // Load The Data
            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 2 * Buffers.SIZEOF_FLOAT, null, GL2.GL_STATIC_DRAW);
            loadInto = Engine.gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY).asFloatBuffer();
            loadInto.put(texCoords);
            Engine.gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);

            // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
            loadInto = null;
            vertices = null;
            texCoords = null;
            fUseVBO = true;
//            GL2 gl = new TraceGL2(glass, System.err);
//        	// Generate And Bind The Texture Coordinate Buffer
//            Engine.gl.glGenBuffers(1, VBOTexCoords, 0);							// Get A Valid Name
//            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOTexCoords[0]);		// Bind The Buffer
//            // Load The Data
//            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 2 * Buffers.SIZEOF_FLOAT, texCoords, GL2.GL_STATIC_DRAW);
//            
//            
//            
//            // Generate And Bind The Vertex Buffer
//            Engine.gl.glGenBuffers(1, VBOVertices, 0);							// Get A Valid Name
//            Engine.gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);			// Bind The Buffer
//            // Load The Data
//            int replaceSize=16;
//            Engine.gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_READ);
//            ByteBuffer replace = ByteBuffer.allocateDirect(replaceSize);
//            for (int i=0; i<replaceSize; i++)
//            	replace.put((byte)6);
//            replace.flip();
//            replace.rewind();
//            System.out.println("replace: "+replace);
//            Engine.gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, replaceSize, replace);
//            replace.rewind();
//            FloatBuffer loaded = Engine.gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_ONLY).asFloatBuffer();
//            
//            loaded.put(5);
//            loaded.put(5);
//            loaded.put(5);
//            loaded.put(5);
//            loaded.rewind();
//            System.out.print("before: "+vertices);
//            System.out.println("   after: "+loaded);
//            while (loaded.hasRemaining())
//            	System.out.printf("original->loaded:  %8.1f->%4.1f%8.1f->%4.1f%8.1f->%4.1f\n",
//            			vertices.get(), loaded.get(),
//            			vertices.get(), loaded.get(),
//            			vertices.get(), loaded.get());
//            Engine.gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
//
//            // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
//            vertices = null;
//            texCoords = null;
//            fUseVBO = true;
        }*/
    }
}
