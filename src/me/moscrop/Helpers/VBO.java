package me.moscrop.Helpers;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VBO {
	
	private Model model;
	private FloatBuffer vboData;
	private int vboVertexHandle;
	
	public void create(Model model) {
		
		this.model = model;	
		this.vboData = ModelImporter.convertModel(model);
		this.vboVertexHandle = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vboData, GL15.GL_STATIC_DRAW);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 20, 0L);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 20, 8L);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		//Bind the buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboVertexHandle);
		
	    //Set the types of data for the VBO*/
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	    
	    //Draw what is stored in the VBO
	    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getFaces().size() * 6);
	    
	    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	    
	    //Unbind the buffer
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
}
