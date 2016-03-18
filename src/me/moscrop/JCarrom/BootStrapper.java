package me.moscrop.JCarrom;

public class BootStrapper {

	/**
	 * @param args
	 * This class will be used to just
	 * load the game class.
	 */
	public static void main(String[] args) {
		Game g = new Game();
		
		try {			
			g.init();
			g.run();
			
			new Thread(g).start();	
			
		} catch(Exception ex) {			
			ex.printStackTrace();			
		}
	}

}
