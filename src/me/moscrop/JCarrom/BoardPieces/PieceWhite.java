package me.moscrop.JCarrom.BoardPieces;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class PieceWhite extends PieceBase {

	/**
	 * Creates a new white carrom coin
	 * almost identical to the black coin
	 * except visual differences
	 * @param position
	 */
	public PieceWhite(Vector3f position) {
		super(position, 0.2f, 0.07f, 1f);
		
		super.setTexture("/res/images/Coin.png");
	}
	
	/**
	 * Sets the colour of the piece to be rendered
	 * @return
	 */
	public Color3f getColor() {
		return new Color3f(0.9f, 0.9f, 0.9f);
	}
}
