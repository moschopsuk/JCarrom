package me.moscrop.JCarrom;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * Provides a moment for the camera
 * using position with pitch and yaw
 * 
 * @author Moschops
 *
 */
public class Camera {
	
	static final float MOUSESENSITIVTY = 0.05f;
	static final float FOV = 65.0f;
	static final float NEAR = 0.1f;
	static final float FAR = 1000.0f;
	
	private Vector3f position;
    private float yaw;
    private float pitch;
    
    /**
     * Initialise the class with an initial
     * position for the camera.
     * 
     * @param Set the Starting X position of the camera
     * @param Set the Starting Y position of the camera
     * @param Set the Starting Z position of the camera
     */
    public Camera(Vector3f position, float yaw, float pitch) {
    	this.position = position;
    	this.yaw = yaw;
    	this.pitch = pitch;
    }
    
	public void setRotation(float yaw, float pitch)
    {
        //increment the yaw and pitch
        this.yaw += (yaw * MOUSESENSITIVTY);
        this.pitch -= (pitch * MOUSESENSITIVTY);
    }
     
    /**
     * moves the camera forward relative 
     * to its current rotation (yaw)
     */
    public void walkForward(float distance)
    {
        position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
        position.y += distance * (float)Math.tan(Math.toRadians(pitch));
        position.z += distance * (float)Math.cos(Math.toRadians(yaw));
    }
     
    /**
     * moves the camera backward relative
     * to its current rotation (yaw)
     */
    public void walkBackwards(float distance)
    {
        position.x += distance * (float)Math.sin(Math.toRadians(yaw));
        position.y -= distance * (float)Math.tan(Math.toRadians(pitch));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
    }
     
    /**
     * strafes the camera left relitive
     * to its current rotation (yaw)
     */
    public void strafeLeft(float distance)
    {
        position.x -= distance * (float)Math.sin(Math.toRadians(yaw-90));
        position.z += distance * (float)Math.cos(Math.toRadians(yaw-90));
    }
     
    /**
     * strafes the camera right relitive 
     * to its current rotation (yaw)
     */
    public void strafeRight(float distance)
    {
        position.x -= distance * (float)Math.sin(Math.toRadians(yaw+90));
        position.z += distance * (float)Math.cos(Math.toRadians(yaw+90));
    }
        
    /**
     * translates and rotate the matrix
     * so that it looks through the camera
     */
    public void lookThrough()
    {
    	GL11.glMatrixMode(GL11.GL_PROJECTION);
    	GL11.glLoadIdentity();
    	
    	float widthHeightRatio = (float) Display.getWidth() / (float) Display.getHeight();
    	
    	GLU.gluPerspective(FOV, widthHeightRatio, NEAR, FAR);
    	
    	GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
    	
    	/**
    	 * Clamp pitch and yaw as they can
    	 * cause error if there values get too
    	 * high
    	 */
    	this.pitch = Math.max(-90.0f, Math.min(90.0f, this.pitch));   	
    	
    	if (this.yaw > 360 || this.yaw < -360)
    		this.yaw = 0;
    	    	
    	//roatate the pitch around the X axis
        GL11.glRotatef(this.pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        GL11.glRotatef(this.yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
    }
    
    @Override
    public String toString() {
    	return String.format("X:%s Y:%s Z:%s Yaw:%.2f Pitch:%.2f", this.position.x, this.position.y, this.position.z, this.yaw, this.pitch);
    }
}
