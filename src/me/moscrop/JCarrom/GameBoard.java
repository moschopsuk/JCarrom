package me.moscrop.JCarrom;

import java.io.InputStream;
import java.util.*;

import javax.vecmath.*;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.ResourceLoader;

import com.bulletphysics.dynamics.RigidBody;

import me.moscrop.Helpers.*;
import me.moscrop.JCarrom.BoardPieces.*;

public class GameBoard implements IGameComponent {
	
	//Handles the physics
	private BulletPhysics physics;
	
	//Contain all NPC board pieces
	private List<PieceBase> boardPieces;
	//The players piece
	private PieceStriker playerPiece;
		
	//stores the 
	private VBO boardVBO;
	private Texture boardtexture;
			
	public GameBoard() {
		this.boardPieces = new ArrayList<PieceBase>();
		this.physics = new BulletPhysics();
		this.boardVBO = new VBO();
		
		this.boardPieces.add(new PieceQueen(new Vector3f(0.0f, 100.0f, 0.0f)));
	}
	
	@Override
	public void init() throws Exception {
		System.out.println("GameBoard- Loading Models");
		
		this.boardtexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/res/images/CarromBoard.png"));		
		InputStream stream = ResourceLoader.getResourceAsStream("/res/models/CarromBoard.obj");
		Model model = ModelImporter.ProcessOBJ(stream);			
		this.boardVBO.create(model);
		
		this.physics.init();
		
		for(PieceBase piece : this.boardPieces) {	
			this.physics.addPiece(piece);
		}
	}
	
	@Override
	public void update(float deltaTime) {	
        this.physics.update(deltaTime);
        
		for(PieceBase piece : this.boardPieces) {	
			RigidBody body = piece.getBody();			
			Vector3f t = body.getCenterOfMassPosition(new Vector3f());
		
			System.out.println(t.toString());
		}
	}

	@Override
	public void render() {
		this.boardtexture.bind();
		this.boardVBO.render();		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		for(PieceBase piece : this.boardPieces) {
			this.physics.renderBody(piece.getBody());
		}
		
		this.physics.render();
	}

}
