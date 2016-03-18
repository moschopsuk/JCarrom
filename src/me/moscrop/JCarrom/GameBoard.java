package me.moscrop.JCarrom;

import java.util.*;

import javax.vecmath.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.ResourceLoader;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBody;

import me.moscrop.Helpers.*;
import me.moscrop.JCarrom.BoardPieces.*;
import me.moscrop.JCarrom.Renders.*;

public class GameBoard {
	
	//Handles the game parent
	private Game gameBase;
	
	//Handles the physics
	private BulletPhysics physics;
	
	//Contain all NPC board pieces
	private List<PieceBase> boardPieces;
	//
	private PieceQueen queen;
	//The players piece
	private PieceStriker player1;
	private PieceStriker player2;
	
	//for the carrom base
	private Model3DRender carromBase;
	private Texture boardtexture;
	
	//for the carrom border
	private CompoundShape staticWalls;
	private Texture woodtexture;
	
	//Handles game state
	private enum GameSates { PLAYER1AIM, PLAYER2AIM, PLAYER1WAIT, PLAYER2WAIT }
	private GameSates currentState;
	
	/**
	 * Loads all of the other classes
	 * and set ups lists
	 */
	public GameBoard(Game base) {
		this.boardPieces = new ArrayList<PieceBase>();
		this.physics = new BulletPhysics();
		this.gameBase = base;
		
		//set the pieces up
		this.setPositions();
		
		this.queen = new PieceQueen(new Vector3f(0.0f, 0.5f, 0.0f));
		
		this.player1 = new PieceStriker(new Vector3f(-3.25f, 0.25f, 0.0f), new Vector2f(2.7f, -2.7f));
		this.player1.setAngles(new Vector2f(-3.0f, -6.4f), -4.7f);
		
		this.player2 = new PieceStriker(new Vector3f(3.25f, 0.25f, 0.0f), new Vector2f(2.7f, -2.7f));
		this.player2.setAngles(new Vector2f(6.4f, 3.0f), 4.7f);
		this.player2.setInverse(true);
	}



	/**
	 * Sets up the physics and
	 * other resources for the board
	 * @throws Exception
	 */
	public void init() throws Exception {
		
		this.physics.init();
		
		//load the board textures
		this.boardtexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/res/images/CarromBoard.png"));			
		Model3D floorModel = new Model3D("/res/models/CarromBoard.obj");			
		this.carromBase = new Model3DRender(floorModel);
		
		this.woodtexture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/res/images/wood.jpg"));					
		staticWalls = new CompoundShape();
		
		/**
		 * Do a little hack random
		 * to decide who goes first
		 * 
		 * at this point the first player to go must plot white
		 */
		if(Math.random() < 0.5)
		{
			this.currentState = GameSates.PLAYER1AIM;
			this.player1.setTargetColor("WHITE");
			this.player2.setTargetColor("BLACK");
		} else {
			this.currentState = GameSates.PLAYER2AIM;
			this.player1.setTargetColor("BLACK");
			this.player2.setTargetColor("WHITE");
		}
			
		/**
		 * Add out objects to the physics world
		 */
		this.physics.addBoard(floorModel, staticWalls);
		this.physics.addPiece(queen);
		this.physics.addPiece(player1);
		this.physics.addPiece(player2);
		for(PieceBase piece : this.boardPieces) {
			this.physics.addPiece(piece);
		}
	}
	
	/**
	 * On update tick
	 * @param deltaTime
	 */
	public void update(float deltaTime) {	
        this.physics.update(deltaTime);
        
        //Grab the camera from parent class
        Camera cam = this.gameBase.getCamera();
        ArrayList<PieceBase> removed = this.removeFallen();
        
        switch (this.currentState) {
        	case PLAYER1AIM:
        		cam.setPosition(new Vector3f(6.0f, -7.0f, 0.0f));
        		cam.setRotation(90.0f, 50.0f);
        		   		
        		this.player1.reset();
        		this.player1.movePiece();
        		//Once fired we move out of this state
        		if(this.player1.getFired()) this.currentState = GameSates.PLAYER1WAIT;
        		break;
        		
        	case PLAYER2AIM:
        		cam.setPosition(new Vector3f(-6.0f, -7.0f, 0.0f));
        		cam.setRotation(-90.0f, 50.0f);
        		
        		this.player2.reset();
        		this.player2.movePiece();
        		//Once fired we move out of this state
        		if(this.player2.getFired()) this.currentState = GameSates.PLAYER2WAIT;
        		break;
        		
        	case PLAYER1WAIT:	
        		
        		if(this.updateScores(removed, this.player1, this.player2)) {
        			this.currentState = GameSates.PLAYER1AIM;
        		}
        		
        		//Nothing else is moving so we end
        		if(this.isClear()) {
        			this.currentState = GameSates.PLAYER2AIM;
        			this.player1.breakCombo();
        		}
        		break;
        		
        	case PLAYER2WAIT:
        		
        		if(this.updateScores(removed, this.player2, this.player1)) {
        			this.currentState = GameSates.PLAYER2AIM;
				}
        		
        		//Nothing else is moving so we end
        		if(this.isClear()){
        			this.currentState = GameSates.PLAYER1AIM;
        			this.player2.breakCombo();
        		}
        		break;
        }
	}
	
	/**
	 * 
	 * @param removed
	 * @param currentPlayer
	 * @param otherPlayer
	 */
	private boolean updateScores(ArrayList<PieceBase> removed, PieceStriker currentPlayer, PieceStriker otherPlayer) {
		
		boolean continuePlayer = false;
		
		for(PieceBase piece : removed) {
			if(piece instanceof PieceWhite) {
				if(currentPlayer.getTargetColor() == "WHITE") {
					currentPlayer.increaseScore();
					otherPlayer.breakCombo();
					continuePlayer = true;
				}
			} else if(piece instanceof PieceBlack) {
				if(currentPlayer.getTargetColor() == "BLACK") {
					currentPlayer.increaseScore();
					otherPlayer.breakCombo();
					continuePlayer = true;
				}
			} else if(piece instanceof PieceQueen) {
				if(currentPlayer.getCombo() < 1) {
					this.physics.addPiece(queen);
					queen.setPosition(new Vector3f(0.0f, 0.5f, 0.0f));
				} else {
					currentPlayer.increaseScore(5);
				}
			}
		}
		
		return continuePlayer;
	}
	
	/**
	 * Will loop over the pieces if all their
	 * velocities are 0 then we can assume
	 * that everything has stopped moving
	 * @return
	 */
	public boolean isClear() {
		Vector3f vec = new Vector3f();
		boolean isClear = true;
		RigidBody body;
		
		/**
		 * Check the strikers must be done
		 * Independently NOT together
		 */
		if (this.currentState == GameSates.PLAYER1WAIT) {
			body = this.player1.getBody();
			
			if(body.isActive()) {
				isClear = false;
			}
			
		} else if(this.currentState == GameSates.PLAYER2WAIT) {
			body = this.player2.getBody();
			
			if(body.isActive()) {
				isClear = false;
			}
		}
		
		if(this.queen.getBody().isActive()) {
			isClear = false;
		}
		
		/**
		 * we now perform a check over the pieces
		 */
		for(PieceBase piece : this.boardPieces) {
			body = piece.getBody();             
            vec = body.getLinearVelocity(vec);        
            
            if(body.isActive()) {
            	isClear = false;
			} 
		}
				
		return isClear;
	}
	
	
	/**
	 * Will iterate over all pieces that have a
	 * y axis greater than -5, incating they have
	 * fallen and must be removed
	 * @return
	 */
	private ArrayList<PieceBase> removeFallen() {
		
		ArrayList<PieceBase> removedPieces = new ArrayList<PieceBase>();
		Iterator<PieceBase> iterator = this.boardPieces.iterator();
		
		/**
		 * Since we are removing from a "live" array list
		 * we must iterate over rather than using a for loop
		 */
		while(iterator.hasNext()) {
			PieceBase piece = iterator.next();
			
			if(piece.getPosition().y < -5) {
				
				//Remove it's physics body and piece from
				//the collective lists
				this.physics.removePiece(piece.getBody());
				iterator.remove();
				
				//returns the piece so it can be
				//handled by the games logic
				removedPieces.add(piece);
			}
		}
		
		if(this.queen.getPosition().y < -5) {
			this.physics.removePiece(queen.getBody());
			removedPieces.add(queen);
		}
		
		if(this.player1.getPosition().y < -5) {
			this.player1.reset();
		}
		
		if(this.player2.getPosition().y < -5) {
			this.player2.reset();
		}
		
		return removedPieces;
	}

	/**
	 * Render the 3D objects
	 */
	public void render3D() {
		
		/**
		 * Renders the carrom table and the
		 * wall that surround it
		 */
		this.boardtexture.bind();
		this.carromBase.render();
		this.woodtexture.bind();
		WallsRender.render(staticWalls);		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		for(PieceBase piece : this.boardPieces) {
			PieceRender.RenderPiece(piece);
		}
		
		PieceRender.RenderPiece(this.queen);
		
	    switch (this.currentState) {
	       	case PLAYER1AIM:
	       	case PLAYER1WAIT:
	       		PieceRender.RenderPiece(this.player1);
	    		this.player1.drawAim();
	       		break;
	       		
	       	case PLAYER2AIM:
	       	case PLAYER2WAIT:
	       		PieceRender.RenderPiece(this.player2);
	    		this.player2.drawAim();
	       		break;
        }
	}
	
	/**
	 * Render the 2D objects
	 */
	public void render2D() {
		FontRender font = this.gameBase.getRenderFont();
		
		Color player1Color = Color.white;
		Color player2Color =Color.white;
		
		switch (this.currentState) {
	       	case PLAYER1AIM:
	       	case PLAYER1WAIT:
	       		player1Color = Color.yellow;
	       		break;
	       		
	       	case PLAYER2AIM:
	       	case PLAYER2WAIT:
	       		player2Color = Color.yellow;
	       		break;
		}
		
		font.drawString(10, 10, "Player 1: " + this.player1.getScore() + " (" + this.player1.getCombo() +  ")", player1Color, true);
		font.drawString(10, 30, "Potting: " + this.player1.getTargetColor(), player1Color, true);
		
		font.drawString(Display.getWidth() - 150, 10, "Player 2: " + this.player2.getScore() + " (" + this.player2.getCombo() +  ")", player2Color, true);
		font.drawString(Display.getWidth() - 150, 30, "Potting: " + this.player2.getTargetColor(), player2Color, true);
		
		//Bug fix the color set by the string
		//may bled to the other renders and change there
		//color
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
	}
	
	
	/**
	 * Sets the carrom coins up
	 * to their default positions
	 */
	private void setPositions() {
		this.boardPieces.add(new PieceWhite(new Vector3f(0.5f, 0.25f, -1.0f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(0.0f, 0.25f, -1.0f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(-0.5f, 0.25f, -1.0f)));
		
		this.boardPieces.add(new PieceBlack(new Vector3f(0.75f, 0.25f, -0.5f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(0.25f, 0.25f, -0.5f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(-0.75f, 0.25f, -0.5f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(-0.25f, 0.25f, -0.5f)));
		
		this.boardPieces.add(new PieceWhite(new Vector3f(0.5f, 0.25f, 0.0f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(1.0f, 0.25f, 0.0f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(-0.5f, 0.25f, 0.0f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(-1.0f, 0.25f, 0.0f)));
		
		this.boardPieces.add(new PieceBlack(new Vector3f(0.75f, 0.25f, 0.5f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(0.25f, 0.25f, 0.5f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(-0.75f, 0.25f, 0.5f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(-0.25f, 0.25f, 0.5f)));
		
		this.boardPieces.add(new PieceWhite(new Vector3f(0.5f, 0.25f, 1.0f)));
		this.boardPieces.add(new PieceBlack(new Vector3f(0.0f, 0.25f, 1.0f)));
		this.boardPieces.add(new PieceWhite(new Vector3f(-0.5f, 0.25f, 1.0f)));
	}

}
