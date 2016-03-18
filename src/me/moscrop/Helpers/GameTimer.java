package me.moscrop.Helpers;

public class GameTimer {
	//how often the counter should update
	static final double INTERVAL = 1e9;	
	//current frame number
	private int frames;
	//last FPS time
	private long lastFPS;
	//Current FPS
	private int fps;
	//last stored time in nanoseconds
	private long lastTime;
		
	/**
	 * Class constructor set as
	 */
	public GameTimer() {
		lastTime = lastFPS = System.nanoTime();
	}

	/**
	 * When the interval passes a second
	 * of frames is the frames per second.
	 */
	public void update() {		
		frames++;
        if(System.nanoTime() - lastFPS >= INTERVAL) {
                this.fps = frames;    
                lastFPS += 1e9;
                frames = 0;
        }
	}
	
	/**
	 * @return Frames Per Second
	 */
	public int getFPS() {
		return this.fps;
	}
	
	/**
	* Calculate how many nanoseconds have passed
	* since last frame.
	*
	* @return nanoseconds converted to milliseconds
	*/
	public float getDelta() {
		long deltaTime = System.nanoTime() - lastTime;
		lastTime += deltaTime;

		return (deltaTime / 1000000f);
	}
}
