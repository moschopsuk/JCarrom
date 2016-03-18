package me.moscrop.JCarrom.Renders;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ObjectPool;

import me.moscrop.JCarrom.BoardPieces.PieceBase;

public class PieceRender {
	
	//Used by bullet physics as temporary storage
	static ObjectPool<Vector3f> vectorsPool = ObjectPool.get(Vector3f.class);
	
	/**
	 * Renders a carrom coin
	 * 
	 * Will collect any the coins rigidbody
	 * physical information and render a cylinder and circle
	 * will also apply a texture and colour
	 * 
	 * @param piece
	 */
	public static void RenderPiece(PieceBase piece) {
		//Grap information about the piece
		RigidBody body = piece.getBody();
		float[] glMat = new float[16];
		FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);
		Transform trans = body.getInterpolationWorldTransform(new Transform());
		CollisionShape shape = body.getCollisionShape();
		CylinderShape cylindershape = (CylinderShape) shape;
		
		//Need to draw where the physics body for
		//the piece is located at
		GL11.glPushMatrix();
        trans.getOpenGLMatrix(glMat);       
        floatBuf.clear();
        floatBuf.put(glMat).flip();       
        GL11.glMultMatrix(floatBuf);
              
        int upAxis = cylindershape.getUpAxis();
        float radius = cylindershape.getRadius();
        Vector3f halfVec = vectorsPool.get();
        float halfHeight = VectorUtil.getCoord(cylindershape.getHalfExtentsWithMargin(halfVec), upAxis);
            
        BuildMesh(piece, radius, halfHeight, upAxis);
        
        vectorsPool.release(halfVec);      
        GL11.glPopMatrix();
	}
	
	/**
	 * Creates a new cylinder and cirlce rendered at 0,0,0
	 * Must be moved by using glTranfrom, to the spot required
	 * 
	 * @param Piece to render
	 * @param radius of the coin
	 * @param halfHeight height of the coin
	 * @param upAxis or rotation of the coin
	 */
	private static void BuildMesh(PieceBase piece, float radius, float halfHeight, int upAxis) {		
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
        
        //set the pieces colour
        GL11.glColor3f(piece.getColor().x, piece.getColor().y, piece.getColor().z);
        
        //Render the pieces walls as a cylinder
        cylinder.setDrawStyle(GLU.GLU_FILL);
        cylinder.setNormals(GLU.GLU_SMOOTH);
        cylinder.draw(radius, radius, 2f * halfHeight, 15, 10);
        
        
        piece.getTexture().bind();
        
        //Render the top of the cylinder
		GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef(0f, 0f, 2f * halfHeight);
        GL11.glRotatef(180f, 0f, 1f, 0f);
        disk.setTextureFlag(true);
        disk.draw(0, radius, 15, 10); 
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        GL11.glPopMatrix();
	}
	
}
