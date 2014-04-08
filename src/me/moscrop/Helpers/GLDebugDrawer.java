package me.moscrop.Helpers;

import javax.vecmath.*;
import org.lwjgl.opengl.GL11;
import com.bulletphysics.linearmath.*;

public class GLDebugDrawer extends IDebugDraw {
    
    // JAVA NOTE: added
    private static final boolean DEBUG_NORMALS = false;    
    private int debugMode;  
    private final Vector3f tmpVec = new Vector3f();

    @Override
    public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
            if (debugMode > 0) {
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glColor3f(color.x, color.y, color.z);
                    GL11.glVertex3f(from.x, from.y, from.z);
                    GL11.glVertex3f(to.x, to.y, to.z);
                    GL11.glEnd();
            }
    }

    @Override
    public void setDebugMode(int debugMode) {
            this.debugMode = debugMode;
    }

    @Override
    public void draw3dText(Vector3f location, String textString) {
            //glRasterPos3f(location.x,  location.y,  location.z);
            // TODO: BMF_DrawString(BMF_GetFont(BMF_kHelvetica10),textString);
    }

    @Override
    public void reportErrorWarning(String warningString) {
            System.err.println(warningString);
    }

    @Override
    public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
            if ((debugMode & DebugDrawModes.DRAW_CONTACT_POINTS) != 0) {
                    Vector3f to = tmpVec;
                    to.scaleAdd(distance*100f, normalOnB, pointOnB);
                    Vector3f from = pointOnB;

                    // JAVA NOTE: added
                    if (DEBUG_NORMALS) {
                            to.normalize(normalOnB);
                            to.scale(10f);
                            to.add(pointOnB);
                            GL11.glLineWidth(3f);
                            GL11.glPointSize(6f);
                            GL11.glBegin(GL11.GL_POINTS);
                            GL11.glColor3f(color.x, color.y, color.z);
                            GL11.glVertex3f(from.x, from.y, from.z);
                            GL11.glEnd();
                    }

                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glColor3f(color.x, color.y, color.z);
                    GL11.glVertex3f(from.x, from.y, from.z);
                    GL11.glVertex3f(to.x, to.y, to.z);
                    GL11.glEnd();

                    // JAVA NOTE: added
                    if (DEBUG_NORMALS) {
                    	GL11.glLineWidth(1f);
                    	GL11.glPointSize(1f);
                    }
            }
    }

    @Override
    public int getDebugMode() {
            return debugMode;
    }

}
