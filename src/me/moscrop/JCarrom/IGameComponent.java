package me.moscrop.JCarrom;

public interface IGameComponent {
	
	public void init() throws Exception;
	
	public void update(float deltaTime);
	
	public void render();

}
