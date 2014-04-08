package me.moscrop.JCarrom;

import javax.vecmath.*;

import me.moscrop.Helpers.GameTimer;
import me.moscrop.JCarrom.Renders.FontRender;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.ResourceLoader;

/**
 * This is the main class file used by
 * this game, will contain the main game
 * render and logic update loop
 * @author Moschops
 */
public class Game implements Runnable {
	
	static final float MOVEMENTSPEED = 0.02f;
	
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
		this.gameBoard = new GameBoard();
		this.gameTimer = new GameTimer();
		
		System.out.println("init() - Loading Reasources");
		this.fontRender = new FontRender(ResourceLoader.getResourceAsStream("/res/fonts/8bitoperator_jve.ttf"), 24f);	
		
		
		this.gameBoard.init();
		
		//Setup up OpenGL
		this.initOpenGL();	
	}
	
	/**
	 * Initialise opgenGL
	 * 
	 * Also print out what GPU 
	 * openGL will be using
	 */
	private void initOpenGL() {
		//Debug Info
		System.out.println("initOpenGL() OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("initOpenGL() OpenGL Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		System.out.println("initOpenGL() OpenGL Renderer: " + GL11.glGetString(GL11.GL_RENDERER));
		
		//enable textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_LIGHTING);
				
		//Set the blending behaviour
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		
		GL11.glDepthFunc(GL11.GL_LEQUAL); 
	}
	
	/**
	 * This is the the main render/update
	 * loop will be continually looped will
	 * we request the game to exit
	 */
	@Override
	public void run() {
		
		Mouse.setGrabbed(true);
		float dx = 0.0f;
        float dy = 0.0f; 
        
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
					GL11.glClearColor(0.5f, 0.0f, 0.5f, 1.0f);
					
					//Due to LWJGL bug these must
					//be stored in variables and  cannot
					//be passed directly
					dx = Mouse.getDX();
	                dy = Mouse.getDY();
					
	                /**
	                 * Debug camera movement
	                 */
	                if (Keyboard.isKeyDown(Keyboard.KEY_W)) this.camera.walkForward(MOVEMENTSPEED * deltaTime);
	                if (Keyboard.isKeyDown(Keyboard.KEY_S)) this.camera.walkBackwards(MOVEMENTSPEED * deltaTime);
	                if (Keyboard.isKeyDown(Keyboard.KEY_D)) this.camera.strafeRight(MOVEMENTSPEED * deltaTime);
	                if (Keyboard.isKeyDown(Keyboard.KEY_A)) this.camera.strafeLeft(MOVEMENTSPEED * deltaTime);
	                this.camera.setRotation(dx, dy);     			
	                
	                //Set to 3D perspective
	                //Render 3d objects
	                this.camera.lookThrough();
					this.render3d();
					 
					//2D game objects rendered
					//above 3D objects
					this.init2d();
					this.render2d();
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
	 * Sets OpenGL into a 2D perspective
	 */
	private void init2d() {		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	/**
	 * Updates game logic
	 */
	private void render2d() {		
		this.fontRender.drawString(10, 10, "FPS: " + this.gameTimer.getFPS(), Color.white, true);
		this.fontRender.drawString(10, 30, this.camera.toString(), Color.white, true);
	}
	
	private void render3d() {
		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		this.gameBoard.render();
		
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
}
