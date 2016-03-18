package me.moscrop.JCarrom.Renders;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 * Encapsulates the LWJGL and SLICK2D
 * TTF handling into one class
 * 
 * @author Moschops
 */
public class FontRender {
	
	//Stores the TTF font
	private TrueTypeFont activeFont;
	
	/**
	 * Creates a new TTF object from a .ttf
	 * at a set font size
	 * 
	 * @param TTF file in an data stream
	 * @param Size of the font
	 */
	public FontRender(InputStream inputStream, float fontSize) {	
		try {			
			//convert data stream into a TTF
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			//Set font size to 24pt
			awtFont = awtFont.deriveFont(fontSize);
			//use slick library to convert the awt.font 
			activeFont = new TrueTypeFont(awtFont, true);					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Renders a string in a 2D perspective 
	 * 
	 * @param x position of the text
	 * @param y position of the text
	 * @param string to render
	 * @param color of the string
	 * @param Should a black shadow be rendered beneath the text
	 */
	public void drawString(float x, float y, String string, Color color, Boolean shadow) {	
		if(shadow) {
			activeFont.drawString((x + 2), (y + 2), string, Color.black);
		}
		activeFont.drawString(x, y, string, color);
	}
}
