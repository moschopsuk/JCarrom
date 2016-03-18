package me.moscrop.Helpers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * Contain some common OpenGL
 * call grouped together for ease
 * of use
 * @author Moschops
 *
 */
public class OpenGLHelper {
	
	//Private float buffer for matrixes
	private static FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Transforms the position of the transform
	 * based on a matrix
	 * @param matrix
	 */
	static public void MultMatrix(float[] matrix) {
		GL11.glPushMatrix();
		floatBuf.clear();
        floatBuf.put(matrix).flip();
        GL11.glMultMatrix(floatBuf);
	}
	
	/**
	 * Initialise opgenGL
	 * 
	 * Also print out what GPU 
	 * openGL will be using
	 */
	static public void initOpenGL() {
		//Debug Info
		System.out.println("initOpenGL() OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("initOpenGL() OpenGL Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		System.out.println("initOpenGL() OpenGL Renderer: " + GL11.glGetString(GL11.GL_RENDERER));
		
		//enable textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
				
		//Set the blending behaviour
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}
	
	/**
	 * Sets OpenGL into a 2D perspective
	 */
	static public void init2D() {	
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	/**
	 * Draws a simple cube
	 * @param extent
	 */
	public static void drawCube(float extent) {
        extent = extent * 0.5f;
        
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, 0); GL11.glVertex3f(+extent,-extent,+extent);
		GL11.glTexCoord2f(0, 1); GL11.glVertex3f(+extent,-extent,-extent);
		GL11.glTexCoord2f(1, 1); GL11.glVertex3f(+extent,+extent,-extent);
		GL11.glTexCoord2f(1, 0); GL11.glVertex3f(+extent,+extent,+extent);
	     
		GL11.glTexCoord2f(0, 0); GL11.glVertex3f(+extent,+extent,+extent);
		GL11.glTexCoord2f(0, 1); GL11.glVertex3f(+extent,+extent,-extent);
		GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-extent,+extent,-extent);
	    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-extent,+extent,+extent);
	    
	    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(+extent,+extent,+extent);
	    GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-extent,+extent,+extent);
	    GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-extent,-extent,+extent);
	    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(+extent,-extent,+extent);
	    
	    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-extent,-extent,+extent);
	    GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-extent,+extent,+extent);
	    GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-extent,+extent,-extent);
	    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-extent,-extent,-extent);
	    
	    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-extent,-extent,+extent);
	    GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-extent,-extent,-extent);
	    GL11.glTexCoord2f(1, 1); GL11.glVertex3f(+extent,-extent,-extent);
	    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(+extent,-extent,+extent);
	    
	    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-extent,-extent,-extent);
	    GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-extent,+extent,-extent);
	    GL11.glTexCoord2f(1, 1); GL11.glVertex3f(+extent,+extent,-extent);
	    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(+extent,-extent,-extent);
	    GL11.glEnd();
	}
}
