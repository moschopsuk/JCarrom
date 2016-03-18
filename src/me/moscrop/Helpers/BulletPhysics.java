package me.moscrop.Helpers;

import javax.vecmath.Vector3f;

import me.moscrop.JCarrom.BoardPieces.PieceBase;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class BulletPhysics {
	
	//Global Gravity constant
	static final Vector3f GRAVITY = new Vector3f(0f, -9f, 0f);
	
	//Setup BulletTime physics objects
	private DiscreteDynamicsWorld world;
	private AxisSweep3 overlappingPairCache;
	private CollisionDispatcher dispatcher;
	private DefaultCollisionConfiguration collisionConfiguration;
	
	/**
	 * Encapsulate the JBULLET
	 * physics Library into one object
	 */
	public BulletPhysics() {
		System.out.println("BulletPhysics()");
		// collision configuration contains default setup for memory
		this.collisionConfiguration = new DefaultCollisionConfiguration();
		
		// use the default collision dispatcher.
		this.dispatcher = new CollisionDispatcher(this.collisionConfiguration);
		
        //Limit the size of the world
        Vector3f worldAabbMin = new Vector3f(-1000.0f, -1000.0f, -1000.0f);
		Vector3f worldAabbMax = new Vector3f(1000.0f, 1000.0f, 1000.0f);
		int maxProxies = 1024;	
		this.overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
	}
	
	public void init() {
		System.out.println("BulletPhysics()-init()");
		
	    //Setup a basic dynamic world with a
        //simple gravity constant
        this.world = new DiscreteDynamicsWorld(this.dispatcher,
        										this.overlappingPairCache,
        										new SequentialImpulseConstraintSolver(),
        										this.collisionConfiguration);
        
        this.world.setGravity(GRAVITY);
        
        //For debugging
        //this.world.setDebugDrawer(new GLDebugDrawer());                
        //this.world.getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_AABB);
	}
	
	/**
	 * Adds the static board parts
	 */
	public void addBoard(Model3D floor, CompoundShape walls) {	
		
		RigidBody floorBody = this.modelToShape(floor, 0.01f);
		
		//Create 4 walls
		CollisionShape staticboxShape1 = new BoxShape(new Vector3f(1f, 0.5f, 6.5f)); // left wall
        CollisionShape staticboxShape2 = new BoxShape(new Vector3f(0.5f, 0.5f, 6.5f)); // right wall
        CollisionShape staticboxShape3 = new BoxShape(new Vector3f(6.5f, 0.5f, 0.5f)); // front wall
        CollisionShape staticboxShape4 = new BoxShape(new Vector3f(6.5f, 0.54f, 0.5f)); // back wall
        
        //Merge these 4 walls into one larger shape
        Transform trans = new Transform();
        trans.setIdentity();
        trans.origin.set(-5.84f, 0f, 0f);
        walls.addChildShape(trans, staticboxShape1);
        trans.origin.set(5.45f, 0f, 0f);
        walls.addChildShape(trans, staticboxShape2);
        trans.origin.set(0f, 0f, 5.85f);
        walls.addChildShape(trans, staticboxShape3);
        trans.origin.set(0f, 0f, -5.85f);
        walls.addChildShape(trans, staticboxShape4);
        trans.origin.set(0f, 0f, 0f);
        
		RigidBody borderBody = new RigidBody(0.0f, new DefaultMotionState(trans), walls, new Vector3f());
		
		//Tell Bullet physics that the ground should not be effected by
		//Gravity and is at a fixed point
		floorBody.setCollisionFlags(floorBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		floorBody.setCollisionFlags(floorBody.getCollisionFlags() & ~CollisionFlags.KINEMATIC_OBJECT);
		floorBody.forceActivationState(CollisionObject.ACTIVE_TAG);
		
		//Set the friction of the the floor, or
		//how pieces should slide across the floor.
		floorBody.setFriction(0.2f);	
		
		//Tell Bullet physics that the ground should not be effected by
		//Gravity and is at a fixed point
		borderBody.setCollisionFlags(borderBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		borderBody.setCollisionFlags(borderBody.getCollisionFlags() & ~CollisionFlags.KINEMATIC_OBJECT);
		borderBody.forceActivationState(CollisionObject.ACTIVE_TAG);
			
		//Add it to the world
		this.world.addRigidBody(floorBody);
		this.world.addRigidBody(borderBody);
	}
	
	/**
	 * Converts a 3D model object to a rigidbody
	 * that the bullet physics library can understand
	 * 
	 * @param model
	 * @param margin
	 * @return
	 */
	private RigidBody modelToShape(Model3D model, float margin) {
		
		//used to temporary hold the mesh
		TriangleIndexVertexArray mesh;
		
		//convert the model to a mesh
		mesh = new TriangleIndexVertexArray(
				model.getFaces().size(),
				model.getIndexBuffer(),
				12,
				model.getVertices().size(),
				model.getVertexBuffer(),
				12);
		
		//convert the mesh to a physics collision shape
		BvhTriangleMeshShape meshShape = new BvhTriangleMeshShape(mesh, true);	
		meshShape.setMargin(margin);
		
		//convert the shape to a rigid body
		return new RigidBody(0.0f, null, meshShape, new Vector3f());	
	}

	/**
	 * Adds all of the pieces to the board.
	 * 
	 * @param All Carrom pieces
	 */
	public void addPiece(PieceBase piece) {
		RigidBody body = piece.getBody();
		this.world.addRigidBody(body);
	}
	
	/***
	 * Removes a RigidBody from the
	 * bullet physics world
	 * @param body
	 */
	public void removePiece(RigidBody body) {
		this.world.removeRigidBody(body);
	}
	
	/**
	 * Runs a simulation step for the
	 * physics world
	 */
	public void update(float deltaTime) {
        // step the simulation
        if (this.world != null) {
        	this.world.stepSimulation(deltaTime / 1000f);
        }
	}
	
	/**
	 * Used mainly for drawing debug information
	 * about the shapes and AABB
	 */
	public void render() {
		 // optional but useful: debug drawing
        if (world != null) {
        	world.debugDrawWorld();
        }
	}	
}
