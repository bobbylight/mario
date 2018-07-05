package org.fife.mario;

import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
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
public class GameOverState extends BasicGameState {

	private int id;
	private long elapsedMillis;

	private static final long MINIMUM_GAME_OVER_TIME		= 5500;

	/**
	 * Constructor.
	 *
	 * @param id The ID for this state.
	 */
	public GameOverState(int id) {
		this.id = id;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SoundEngine.get().playMusic(SoundEngine.MUSIC_GAME_OVER, false);
		elapsedMillis = 0;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		g.setColor(Color.white);
		g.drawString("Game Over!", 100, 100);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {

		elapsedMillis += delta;

		if (elapsedMillis>=MINIMUM_GAME_OVER_TIME) {

			// Enter skips the rest of this screen's delay.
			if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
				game.enterState(Constants.STATE_TITLE_SCREEN,
						new FadeOutTransition(), new FadeInTransition());
			}

		}

	}

}
