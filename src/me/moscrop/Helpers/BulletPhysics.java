package me.moscrop.Helpers;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;

import me.moscrop.JCarrom.IGameComponent;
import me.moscrop.JCarrom.BoardPieces.PieceBase;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ObjectPool;

public class BulletPhysics implements IGameComponent {
	
	//Global Gravity constant
	static final Vector3f GRAVITY = new Vector3f(0f, -2f, 0f);
	
	//Setup BulletTime physics objects
	private DynamicsWorld dynamicsWorld;
	private AxisSweep3 overlappingPairCache;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	
	ObjectPool<Vector3f> vectorsPool = ObjectPool.get(Vector3f.class);
	
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
		// the default constraint solver.
		this.solver = new SequentialImpulseConstraintSolver();
		
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
        this.dynamicsWorld = new DiscreteDynamicsWorld(this.dispatcher, this.overlappingPairCache, this.solver, this.collisionConfiguration);
        this.dynamicsWorld.setGravity(GRAVITY);
        
        //For debugging
        this.dynamicsWorld.setDebugDrawer(new GLDebugDrawer());
        this.dynamicsWorld.getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_WIREFRAME);
        this.dynamicsWorld.getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_AABB);
	}
	
	/**
	 * Adds the static board parts
	 */
	public void addBoard(RigidBody floor, RigidBody sides) {	
		//Tell Bullet physics that the ground should not be effected by
		//Gravity and is at a fixed point
		floor.setCollisionFlags(floor.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		
		//Set the friction of the the floor, or
		//how pieces should slide across the floor.
		floor.setFriction(0.5f);
		
		//Add it to the world
		this.dynamicsWorld.addRigidBody(floor);
	}
	
	/**
	 * Adds all of the pieces to the board.
	 * 
	 * @param All Carrom pieces
	 */
	public void addPiece(PieceBase piece) {
		RigidBody body = piece.getBody();
		this.dynamicsWorld.addRigidBody(body);
	}
	
	/***
	 * Removes a RigidBody from the
	 * bullet physics world
	 * @param body
	 */
	public void removePiece(RigidBody body) {
		this.dynamicsWorld.removeRigidBody(body);
	}
	
	public void update(float deltaTime) {
        // step the simulation
        if (this.dynamicsWorld != null) {
        	this.dynamicsWorld.stepSimulation(1f / 60f, 0);
            //numObjects = dynamicsWorld.getNumCollisionObjects();
        }
	}
	
	public void render() {
		 // optional but useful: debug drawing
        if (dynamicsWorld != null) {
                dynamicsWorld.debugDrawWorld();
        }
	}
	
	public void renderBody(RigidBody body) {
		float[] glMat = new float[16];
		FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);
		Transform trans = body.getInterpolationWorldTransform(new Transform());
		CollisionShape shape = body.getCollisionShape();
				
		GL11.glPushMatrix();
        trans.getOpenGLMatrix(glMat);       
        floatBuf.clear();
        floatBuf.put(glMat).flip();       
        GL11.glMultMatrix(floatBuf);
        
        CylinderShape cylindershape = (CylinderShape) shape;
        
        int upAxis = cylindershape.getUpAxis();
        float radius = cylindershape.getRadius();
        Vector3f halfVec = vectorsPool.get();
        float halfHeight = VectorUtil.getCoord(cylindershape.getHalfExtentsWithMargin(halfVec), upAxis);
        
        //radius, halfHeight, upAxis
        
        Cylinder cylinder = new Cylinder();
        Disk disk = new Disk();
        
        GL11.glPushMatrix();
        
        switch (upAxis) {
                case 0:
                		GL11.glRotatef(-90f, 0.0f, 1.0f, 0.0f);
                		GL11.glTranslatef(0.0f, 0.0f, -halfHeight);
                        break;
                case 1:
                		GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                		GL11.glTranslatef(0.0f, 0.0f, -halfHeight);
                        break;
                case 2:
                	GL11.glTranslatef(0.0f, 0.0f, -halfHeight);
                        break;
                default: {
                        assert (false);
                }
        }
        
        cylinder.setDrawStyle(GLU.GLU_FILL);
        cylinder.setNormals(GLU.GLU_SMOOTH);
        cylinder.draw(radius, radius, 2f * halfHeight, 15, 10);
        
        GL11.glTranslatef(0f, 0f, 2f * halfHeight);
        GL11.glRotatef(-180f, 0f, 1f, 0f);
        disk.draw(0, radius, 15, 10);
        
        GL11.glPopMatrix();
        
        vectorsPool.release(halfVec);
        
        GL11.glPopMatrix();
	}
	
}
