package me.moscrop.JCarrom.Renders;

import javax.vecmath.Vector3f;

import me.moscrop.Helpers.OpenGLHelper;

import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectPool;

public class WallsRender {
	//Store temporary bullet physics info
	static ObjectPool<Transform> transformsPool = ObjectPool.get(Transform.class);
	static ObjectPool<Vector3f> vectorsPool = ObjectPool.get(Vector3f.class);
	
	/**
	 * Will splits the collection of shapes, into
	 * single shapes that can be rendered 
	 * @param walls
	 */
	public static void render(CompoundShape walls) {	
		Transform childTrans = transformsPool.get();
        
		//loop through each child shape and render
		for (int i = walls.getNumChildShapes() - 1; i >= 0; i--) {
        	walls.getChildTransform(i, childTrans);
            CollisionShape colShape = walls.getChildShape(i);
            drawShape(childTrans, colShape);
        }
        transformsPool.release(childTrans);
	}
	
	
	/**
	 * Performs the render for the 4 walls
	 * @param where in the world we will render to
	 * @param The shape to be rendered
	 */
	private static void drawShape(Transform trans, CollisionShape shape) {
		//Convert the Jbullet matrix to opengl
		float[] glMat = new float[16]; 
        trans.getOpenGLMatrix(glMat);
        OpenGLHelper.MultMatrix(glMat);
        
        //We know it will be a box shape
        BoxShape boxShape = (BoxShape) shape;
        Vector3f halfExtent = boxShape.getHalfExtentsWithMargin(vectorsPool.get());
        GL11.glScalef(2f * halfExtent.x, 2f * halfExtent.y, 2f * halfExtent.z);
        OpenGLHelper.drawCube(1f);
        vectorsPool.release(halfExtent);
        
        GL11.glPopMatrix();
	}
}
