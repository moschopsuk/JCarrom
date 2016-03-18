package me.moscrop.JCarrom.BoardPieces;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class PieceQueen extends PieceBase {

	public PieceQueen(Vector3f position) {
		super(position, 0.2f, 0.13f, 1.5f);
		
		super.setTexture("/res/images/Coin.png");
	}
	
	/**
	 * Sets the colour of the piece to be rendered
	 * @return
	 */
	public Color3f getColor() {
		return new Color3f(0.9f, 0.2f, 0.1f);
	}
}
