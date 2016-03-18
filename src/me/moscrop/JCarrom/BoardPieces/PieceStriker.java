package me.moscrop.JCarrom.BoardPieces;

import javax.vecmath.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class PieceStriker extends PieceBase {
	
	//handles the score of the piece
	private int score;
	private int combo;
	
	//Handlers the pieces movement
	float playerInput;
	private Vector3f defualtPosition;
		
	//handles how the mouse interacts wit it
	private float mouseXDelta;
	private Vector2f range;
	private boolean inversed;
	float defualtAngle;
	float angle;
	private Vector2f angleRange;
	
	//handles if the striker has been fired
	float power;
	boolean fired;
	
	//The target the piece should be hitting
	String targetColor;

	/**
	 * Creates a new striker piece
	 * @param position oif the piece
	 * @param range of movement the striker can go
	 */
	public PieceStriker(Vector3f position, Vector2f range) {
		super(position, 0.27f, 0.039f, 1.2f);
		
		this.range = range;
		this.defualtPosition = position;
		this.playerInput = position.z;
		this.fired = false;
		this.inversed = false;
		this.power = 7.0f;
		this.score = 0;
		
		super.setTexture("/res/images/CoinStriker.png");
	}
	
	/**
	 * sets the colour that this striker
	 * should pocket
	 * @return
	 */
	public void setTargetColor(String color) {
		this.targetColor = color;
	}
	
	/**
	 * gets the colour that this striker
	 * should pocket
	 * @return
	 */
	public String getTargetColor() {
		return this.targetColor;
	}
	
	/**
	 * Sets the aim angles and the limits
	 * so the user can't fire behind
	 * @param range
	 * @param defualt
	 */
	public void setAngles(Vector2f range, float defualt) {
		this.angleRange = range;
		this.angle = defualt;
		this.defualtAngle = defualt;
	}
	
	/**
	 * Should the mouse control be inversed
	 * @param inverse
	 */
	public void setInverse(boolean inverse) {
		this.inversed = inverse;
	}
	
	/**
	 * Increases the score by +1
	 * also increases the combo by 1
	 */
	public void increaseScore(int ammount) {
		this.score += ammount;
		this.combo++;
	}
	
	/**
	 * Alternative method will
	 * increase score by one
	 */
	public void increaseScore() {
		this.increaseScore(1);
	}
	
	/**
	 * Sets the combo to 0
	 */
	public void breakCombo() {
		this.combo = 0;
	}
	
	/**
	 * Returns the score of the piece/player
	 * @return
	 */
	public int getScore() {
		return this.score;
	}
	
	/**
	 * Returns the combo of the piece/player
	 * @return
	 */
	public int getCombo() {
		return this.combo;
	}
	
	/**
	 * Handles the users input
	 * and handles the force added to the piece
	 */
	public void movePiece() {
		
		if(!Mouse.isButtonDown(0) && !this.fired) {
			this.mouseXDelta = Mouse.getDX();
			if (this.mouseXDelta < 0) {			
				if(inversed) {
					playerInput += 0.05f;
				} else {
					playerInput -= 0.05f;
				}		
			} else if (this.mouseXDelta > 0){
				if(inversed) {
					playerInput -= 0.05f;
				} else {
					playerInput += 0.05f;
				}
			}
			
			if (playerInput >= this.range.x) playerInput = this.range.x;
			if (playerInput <= this.range.y) playerInput = this.range.y;
			
			Vector3f p = super.getPosition();		
			super.setPosition(new Vector3f(p.x, p.y, playerInput));
		} else if(Mouse.isButtonDown(0) && !this.fired) {
			
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				angle += 0.01f;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)){
				angle -= 0.01f;
			}
				
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				power += 0.2f;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
				power -= 0.2f;
			}
			
			if (power <= 3) power = 3;
			if (power >= 15) power = 15;
			
            if (angle > this.angleRange.x) angle = (float) this.angleRange.x;
            if (angle < this.angleRange.y) angle = (float) this.angleRange.y;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !this.fired) {
			
			float dx = (float) (Math.sin(angle) * power);
			float dz = (float) (Math.cos(angle) * power);
			this.fired = true;
			super.getBody().applyImpulse(new Vector3f( dx, 0.0f, dz ), new Vector3f(0.0f, 0.0f, 0.0f));			
		}
	}
	
	/**
	 * if the piece has been fired
	 * we reset it
	 */
	public void reset() {
		if (this.fired) {
			super.setPosition(defualtPosition);
			this.angle = this.defualtAngle;
			this.fired = false;
		}
	}
	
	/**
	 * returns if the piece has been
	 * fired
	 * @return
	 */
	public boolean getFired() {
		return this.fired;
	}
	
	/**
	 * Draws the line to show the user where
	 * they will be firing
	 */
	public void drawAim() {			
		if(Mouse.isButtonDown(0) && !this.fired) {
											
			Vector3f orgin = this.getPosition();
			           
            float dx = (float) (Math.sin(angle) * power);
			float dz = (float) (Math.cos(angle) * power);
                     
			GL11.glLineWidth(2.5f); 
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glBegin(GL11.GL_LINES);
			
			GL11.glVertex3f(orgin.x, orgin.y, orgin.z);
			GL11.glVertex3f(dx, orgin.y, dz);
			
			GL11.glEnd();
			GL11.glLineWidth(1.0f);
		}
	}
	
	/**
	 * Sets the colour of the piece to be rendered
	 * @return
	 */
	public Color3f getColor() {
		return new Color3f(0.3f, 0.5f, 1.0f);
	}
}
