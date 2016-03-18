package me.moscrop.JCarrom.BoardPieces;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class PieceBlack extends PieceBase {
	
	/**
	 * Creates a new black carrom coin
	 * almost identical to the white coin
	 * except visual differences
	 * @param position
	 */
	public PieceBlack(Vector3f position) {
		super(position, 0.2f, 0.07f, 1f);
		
		super.setTexture("/res/images/Coin.png");
	}
	
	@Override
	public Color3f getColor() {
		return new Color3f(0.2f, 0.2f, 0.2f);
	}
}
