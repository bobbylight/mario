package org.fife.mario;

import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


/**
 * The title screen.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OobgPresentsState extends BasicGameState {

	private int totalTime;
	private Thread titleSongLoader;
	private int id;
	private UnicodeFont font;
	private UnicodeFont font2;

	private static final String TEXT		= "- OutOnBail Games Presents -";
	private static final String TEXT2		= "- (All content originally (C) Nintendo!) -";


	public OobgPresentsState(int id) {
		this.id = id;
	}


	@Override
	public int getID() {
		return id;
	}


	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SoundEngine.get().play(SoundEngine.SOUND_COIN);
	}


	@Override
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame game) {

		titleSongLoader = new Thread(() -> {
            SoundEngine.get().preloadMusic(SoundEngine.MUSIC_TITLE_SCREEN);
            System.out.println("Done loading music!");
        });
		titleSongLoader.start();

		SoundEngine.get().preloadSoundEffects();
		try {
			font = new UnicodeFont("fonts/smwtextfontpro.ttf", 24, false, false);
			font.getEffects().add(new ColorEffect());
			font.addAsciiGlyphs();
			font.loadGlyphs();
			font2 = new UnicodeFont("fonts/smwtextfontpro.ttf", 24, false, false);
			font2.getEffects().add(new ColorEffect());
			font2.addAsciiGlyphs();
			font2.loadGlyphs();
		} catch (SlickException se) {
			se.printStackTrace();
		}

	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		int width = font.getWidth(TEXT);
		float x = (container.getWidth()-width)/2f;
		float y = (container.getHeight() - font.getHeight(TEXT)) / 2f;

		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(TEXT, x, y);
/*
		g.setAntiAlias(true);
		width = font.getWidth(TEXT2);
		x = (container.getWidth()-width)/2f;
		y = (container.getHeight() - font.getHeight(TEXT2)) / 2f;

		g.setColor(Color.yellow);
		g.setFont(font);
		g.drawString(TEXT2, x, y + 30);
*/

	}


	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {

		totalTime += delta;
		if (totalTime<2000) {
			return;
		}

		Input input = container.getInput();

		if (input.isKeyPressed(Input.KEY_ENTER) ||
				input.isKeyPressed(Input.KEY_X)) {
			System.out.println("Enter pressed!");
			try {
				titleSongLoader.join(10000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			game.enterState(Constants.STATE_TITLE_SCREEN, new FadeOutTransition(),
											new FadeInTransition());
		}

	}


}
