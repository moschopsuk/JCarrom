package me.moscrop.JCarrom.BoardPieces;

import java.io.IOException;
import java.io.InputStream;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public abstract class PieceBase {
	
	//Physics shape and body
	private RigidBody body;
	private CylinderShape baseShape;
	
	//Used for rendering the piece
	private float radius;
	private float height;
	private Texture texture;
	
	/**
	 * Base class for each of the moving parts on the board
	 * This class should not be called directly
	 * 
	 * @param Initial position on the board
	 * @param The size of the cylinder
	 * @param How much the piece weighs
	 */
	public PieceBase(Vector3f position, float radius, float height, float mass) {
    	this.baseShape = new CylinderShape(new Vector3f(radius, height, 0.5f));  	
    	this.body = new RigidBody(mass, null, this.baseShape, new Vector3f());
    	
    	this.body.setFriction(0.7f);
    	
    	//We transform the position of the 
    	//body to the specific position
    	Transform transform = new Transform();
    	transform.setIdentity();	
    	transform.origin.set(position);
   	
    	this.body.setWorldTransform(transform);
	}
	
	/**
	 * Gets the position of a Carrom piece based
	 * on its centre of gravity
	 * @return
	 */
	public Vector3f getPosition() {
		Vector3f position;
		position = this.body.getCenterOfMassPosition(new Vector3f());
		return position;
	}
	
	/**
	 * Sets the piece to a new position
	 * @param New position for the piece
	 */
	public void setPosition(Vector3f position) {
    	//We transform the position of the 
    	//body to the specific position
    	Transform transform = new Transform();
    	transform.setIdentity();	
    	transform.origin.set(position);
   	
    	this.body.setWorldTransform(transform);
    	
    	this.body.setLinearVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
    	
		/*Stops object going to sleep and being left
		  behind after mouse */
    	this.body.activate();
    	this.body.setDeactivationTime(0);
	}
	
	/**
	 * Set the object to not be effected by the
	 * physics of the world.
	 * @param condiation
	 */
	public void setStatic(boolean condiation) {
		if(condiation) {
			this.body.setCollisionFlags(this.body.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		}else{
			this.body.setCollisionFlags(this.body.getCollisionFlags() & ~CollisionFlags.STATIC_OBJECT);
		}
	}
	
	/**
	 * Returns the base rigid-body object
	 * @return
	 */
	public RigidBody getBody() {
		return this.body;
	}
	
	/**
	 * Returns the radius of the piece
	 * @return
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/**
	 * Return the height of the piece
	 * @return
	 */
	public float getHeight() {
		return this.height;
	}
	
	/**
	 * Sets the colour of the piece to be rendered
	 * @return
	 */
	public Color3f getColor() {
		return new Color3f(1.0f, 1.0f, 1.0f);
	}
	
	public void setTexture(String texFile) {
		InputStream in = ResourceLoader.getResourceAsStream(texFile);
		
		try {
			this.texture = TextureLoader.getTexture("PNG", in);
		} 
		catch (IOException e) {
			System.out.println("Texture " + texFile + " Missing");
		}
	}
	
	/**
	 * Returns the texture name so we
	 * know what texture to bind
	 * @return
	 */
	public Texture getTexture() {
		return this.texture;
	}
}
