package me.moscrop.JCarrom.BoardPieces;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public abstract class PieceBase {

	//General properties
	private float mass;	
	private float size;
	
	//Physics shape and body
	private RigidBody body;
	private CylinderShape baseShape;
	
	/**
	 * Base class for each of the moving parts on the board
	 * This class should not be called directly
	 * 
	 * @param Initial position on the board
	 * @param The size of the cylinder
	 * @param How much the piece weighs
	 */
	public PieceBase(Vector3f position, float size, float mass) {
    	this.size = size;
    	this.mass = mass;
    	    	
    	this.baseShape = new CylinderShape(new Vector3f(size, 1.0f, 1.0f));  	
    	this.body = new RigidBody(mass, null, this.baseShape, new Vector3f());
    	
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
		position = this.body.getCenterOfMassPosition(null);
		return position;
	}
	
	/**
	 * Returns the base rigid-body object
	 * @return
	 */
	public RigidBody getBody() {
		return this.body;
	}
}
