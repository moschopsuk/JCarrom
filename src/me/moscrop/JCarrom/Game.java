package me.moscrop.JCarrom;

import javax.vecmath.*;

import me.moscrop.Helpers.GameTimer;
import me.moscrop.Helpers.OpenGLHelper;
import me.moscrop.JCarrom.Renders.FontRender;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import org.newdawn.slick.util.ResourceLoader;

/**
 * This is the main class file used by
 * this game, will contain the main game
 * render and logic update loop
 * @author Moschops
 */
public class Game implements Runnable {
	
	static final float MOVEMENTSPEED = 0.002f;
	
	private GameBoard gameBoard;
	private FontRender fontRender;
	private GameTimer gameTimer;
	private Camera camera;
				
	/**Attempt to create a display
	 * Will throw exception on error
	*/
	public void init() throws Exception {			
		System.out.println("init() - Initsalising LWJGL Display window");	
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.setResizable(true);
		Display.create(new PixelFormat(0, 8, 8, 8));
		Display.setInitialBackground(0.5f, 0.5f, 1.0f);
		
		this.camera = new Camera(new Vector3f(6.0f, -7.0f, 0.0f), 90.0f, 50.0f);
		this.gameBoard = new GameBoard(this);
		this.gameTimer = new GameTimer();
		
		System.out.println("init() - Loading Reasources");
		this.fontRender = new FontRender(ResourceLoader.getResourceAsStream("/res/fonts/8bitoperator_jve.ttf"), 24f);	
			
		this.gameBoard.init();
		
		//Setup up OpenGL
		OpenGLHelper.initOpenGL();
	}
		
	/**
	 * This is the the main render/update
	 * loop will be continually looped will
	 * we request the game to exit
	 */
	@Override
	public void run() {
		//Have the application grab the mouse
		//the user will not be-able see the mouse
		Mouse.setGrabbed(true);
        
		try {
			while (!Display.isCloseRequested()) {
				//Update render Time
				float deltaTime = this.gameTimer.getDelta();
						
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					break;
				}
				
				/**
				 * If the game is not on screen
				 * we slow the this while loop
				 * to reduce the strain on the machine
				 */
				if (!Display.isVisible()) {
	                  Thread.sleep(500);
				} else {
					this.update(deltaTime);
					
					// Clear The Screen And The Depth Buffer
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
					GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	                //Set to 3D perspective
	                //Render 3d objects
	                this.camera.lookThrough();
					this.render3d();
					 
					//2D game objects rendered
					//above 3D objects
					OpenGLHelper.init2D();
					
					this.render2d();
					
					Display.setTitle("Carroms 3D (FPS:" + this.gameTimer.getFPS() + ")");
				}
				
				//Swap the buffers
				Display.update();
			}		
		} catch (Throwable t) {
            t.printStackTrace();
		} finally {
            Display.destroy();
            
            //Fixes odd exception error when
            //exiting
            System.exit(0);
    	}
	}
		
	/**
	 * Updates Game logic
	 */
	private void update(float deltaTime) {	
		this.gameTimer.update();
		this.gameBoard.update(deltaTime);
	}
		
	/**
	 * Updates game logic
	 */
	private void render2d() {				
		this.gameBoard.render2D();
	}
	
	private void render3d() {
		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		this.gameBoard.render3D();
		
		GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
	}
	
	/**
	 * Gets the FontRender object that
	 * should be used to render for child classes
	 * 
	 * @return True type Font 
	 */
	public FontRender getRenderFont() {
		return this.fontRender;
	}
	
	/**
	 * returns the camera should
	 * it need to be moved
	 * @return
	 */
	public Camera getCamera() {
		return this.camera;
	}
}
