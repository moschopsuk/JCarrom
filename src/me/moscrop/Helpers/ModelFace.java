package me.moscrop.Helpers;

public class ModelFace {
	private long vertexIndex;
	private int textureIndex;
	private int normalIndex;
	
	/**
	 * This acts more like a data structure
	 * rather than a class, as it groups the indexed data
	 * 
	 * @param Vertex Index
	 * @param TectureUV Index
	 * @param Normal Index
	 */
	public ModelFace(long v, int t, int n) {
		this.vertexIndex = v;
		this.textureIndex = t;
		this.normalIndex = n;
	}
	
	/**
	 * 
	 * @return Vertex Index
	 */
	public long getVI() {
		return this.vertexIndex;
	}
	
	/**
	 * 
	 * @return TextureUV Index
	 */
	public int getTI() {
		return this.textureIndex;
	}
	
	/**
	 * 
	 * @return Normal Index
	 */
	public int getNI() {
		return this.normalIndex;
	}
}
