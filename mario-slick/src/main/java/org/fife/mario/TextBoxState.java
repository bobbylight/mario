package org.fife.mario;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * State entered when Mario hits a "text box".
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TextBoxState extends BasicGameState {

	private int id;
	private long totalTime;
	private UnicodeFont font;

	private static final float MAX_SCREEN_PERCENTAGE_W		= 0.5f;
	private static final float MAX_SCREEN_PERCENTAGE_H		= 0.3f;

	public TextBoxState(int id) {
		this.id = id;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		totalTime = 0;
	}

	/**
	 * Called after the user has read the message and presses Enter.
	 *
	 * @param game The game being played.
	 */
	protected void enterNextState(StateBasedGame game) throws SlickException {
		game.enterState(Constants.STATE_PLAYING_GAME);
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		font = new UnicodeFont("fonts/smwtextfontpro.ttf", 16, false, false);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		font.loadGlyphs();

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {

//		Level level = GameInfo.get().getLevel();
//		level.render(container, game, g, null);
		game.getState(Constants.STATE_PLAYING_GAME).render(container, game, g);

		final float maxMillis = 500;
		float millis = Math.min(totalTime, maxMillis);
		float screenPct = millis/maxMillis;
		float screenW = container.getWidth();
		float screenH = container.getHeight();
		float width = screenW * screenPct * MAX_SCREEN_PERCENTAGE_W;
		float height = screenH * screenPct * MAX_SCREEN_PERCENTAGE_H;
		float x = (screenW - width)/2;
		float y = (screenH - height)/3; // A little higher than center

		g.setColor(Color.black);
		//g.fillRect(x, y, width, height);
		g.fillRoundRect(x, y, width, height, 10);

		if (millis==maxMillis) {
			String text = GameInfo.get().getTextMessage();
			if (text!=null) { // Should always be true
				x += 20;
				y += 20;
				g.setFont(font);
				g.setColor(Color.white);
				// NOTE: Newline handline already done by slick.
				g.drawString(text, x,y);
			}
		}

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		totalTime += delta;

		if (totalTime>=1000) {

			Input input = container.getInput();

			if (input.isKeyPressed(Input.KEY_ENTER) ||
					input.isKeyPressed(Input.KEY_Z) ||
					input.isKeyPressed(Input.KEY_X)) {
				input.clearKeyPressedRecord();
				enterNextState(game);
			}

		}

	}

}
