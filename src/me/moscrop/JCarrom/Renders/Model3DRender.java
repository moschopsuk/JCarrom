package me.moscrop.JCarrom.Renders;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import me.moscrop.Helpers.Model3D;
import me.moscrop.Helpers.ModelFace;

public class Model3DRender {
	
	private Model3D model;
	private int vboID;
	private FloatBuffer vboBuffer;
	
	/**
	 * This class takes in the Model3D object
	 * and creates a vertex buffer object which can store
	 * large amounts of data to the GPU's VRAM
	 * @param model
	 */
	public Model3DRender(Model3D model) {
		this.model = model;
		
		//generate an OpenGL Buffer ID
		this.vboID = GL15.glGenBuffers();
		
		System.out.println("VBO ID:" + this.vboID);
				
    	ArrayList<Float> vertices		= this.model.getVertices();
    	ArrayList<Float> texturesUV		= this.model.getTextureUV();
    	ArrayList<Float> normals	 	= this.model.getNormals();
    	ArrayList<ModelFace> faces		= this.model.getFaces();
    	
    	System.out.println("Faces:" + faces.size());
    	    			
		//Create a buffer array
		//to store all the merged data
		ByteBuffer vbb = ByteBuffer.allocateDirect(faces.size() * 32);
        vbb.order(ByteOrder.nativeOrder());
        this.vboBuffer = vbb.asFloatBuffer();
             
        for (int j = 0; j < faces.size(); j++) {
        	//Store the Vertices
        	vboBuffer.put(vertices.get((int) (faces.get(j).getVI() * 3)));
        	vboBuffer.put(vertices.get((int) (faces.get(j).getVI() * 3 + 1)));
        	vboBuffer.put(vertices.get((int) (faces.get(j).getVI() * 3 + 2)));
        	
        	//Store the Texture Coords
        	vboBuffer.put(texturesUV.get(faces.get(j).getTI() * 2));
        	vboBuffer.put(texturesUV.get((faces.get(j).getTI() * 2) + 1));
        	
        	//Store the Normals
        	vboBuffer.put(normals.get(faces.get(j).getNI() * 3));
        	vboBuffer.put(normals.get((faces.get(j).getNI() * 3) + 1));
        	vboBuffer.put(normals.get((faces.get(j).getNI() * 3) + 2));
        }
        
        //Set the buffer to the start
        this.vboBuffer.rewind();
                
        //Upload the data to the GPU
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vboBuffer, GL15.GL_STATIC_DRAW);
								
		//Tell the GPU what the data means
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);		//3*4
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);	//2*4
		GL11.glNormalPointer(GL11.GL_FLOAT, 32, 20L);		//3*4
		
		//Un bind the buffer		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, GL11.GL_NONE);
	}
	   
	/**
	 * Renders the VBO on screen
	 */
    public void render() {
    	//Rebinds the buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		
	    //Set the types of data for the VBO*/
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
	    	    
	    //Draw what is stored in the VBO
	    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getFaces().size() * 6);
	    
	    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	    
	    //Unbind the buffer
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, GL11.GL_NONE);
    }
}
