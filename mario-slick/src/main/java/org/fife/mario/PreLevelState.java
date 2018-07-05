package org.fife.mario;

import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


/**
 * The screen that shows the player how many lives they have left.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PreLevelState extends BasicGameState {

	private int id;
	private long totalTime;
	private String lives;
	private Thread loadAreaSongsThread;
	private UnicodeFont font;

	private static final int MAX_SCREEN_TIME		= 3000;


	/**
	 * Constructor.
	 *
	 * @param id The ID for this state.
	 */
	public PreLevelState(int id) {
		this.id = id;
	}


	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {

		container.getInput().clearKeyPressedRecord();
		totalTime = 0;

		Main main = (Main)game;
		Mario mario = main.getMario();
		mario.reset(false);
		final Level level = GameInfo.get().getLevel();
		level.reset();
		Area area = level.getCurrentArea();
// Unfortunately, we must do this stuff here instead of GameState.enter(),
// as State.enter() isn't called until AFTER the "in" transition is done, and
// it calls State.render() repeatedly.
mario.setArea(area);
if (level.getStartingAnimation()!=null) {
	mario.setActive(false);
}
else {
	mario.setActive(true);
	level.moveToStartingLocation(mario);
}

		lives = Integer.toString(PlayerInfo.get(0).getLives());

		loadAreaSongsThread = new Thread(() -> {
            for (Area area1 : level.getAreas().values()) {
                SoundEngine.get().preloadMusic(area1.getMusic());
            }
        });
		loadAreaSongsThread.start();

	}


	@Override
	public int getID() {
		return id;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

		font = new UnicodeFont("fonts/smwtextfontpro.ttf", 24, false, false);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		font.loadGlyphs();
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		// null first time through, since Slick renders once before entering.
		if (lives!=null) {

			SpriteSheetManager ssm = SpriteSheetManager.instance();
			Image img = ssm.getImage(SpriteSheetManager.SHEET_MARIO, 0, 3);

			g.setColor(Color.white);
			g.setFont(font);
			int fontH = font.getHeight("x0123456789-");
			final int spacer = 15;
			int totalH = img.getHeight() + spacer + fontH;

			// Draw the name of the level we're on.
			String level = GameInfo.get().getLevel().getDisplayName();
			float w = font.getWidth(level);
			float x = (container.getWidth() - w) / 2;
			float y = (container.getHeight() - totalH) / 2 + font.getAscent();
			g.drawString(level, x,y);

			// Draw Mario, allowing for his life count beside him.
			String text = "  x  " + lives;
			w = img.getWidth() + font.getWidth(text);
			x = (container.getWidth() - w) / 2;
			y = y += font.getDescent() + spacer;
			g.drawImage(img, x, y);

			// Draw " x 3" beside Mario.
			x += img.getWidth();
			y = y + img.getHeight() - (img.getHeight()-fontH)/2 - font.getDescent();
			g.drawString(text, x,y);

		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {

		totalTime += delta;

		boolean enterPressed = container.getInput().isKeyPressed(Input.KEY_ENTER);

		if (totalTime>MAX_SCREEN_TIME || enterPressed) {
			try {
				System.out.println("Waiting for song loading thread!");
				loadAreaSongsThread.join(10000);
				System.out.println("Songs loaded!");
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			game.enterState(Constants.STATE_PLAYING_GAME,
					new FadeOutTransition(), new FadeInTransition());
		}
	}
}
