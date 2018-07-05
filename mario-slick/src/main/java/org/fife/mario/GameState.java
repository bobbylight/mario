package org.fife.mario;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.fife.mario.blocks.Block;
import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.powerups.OneUp;
import org.fife.mario.sound.SoundEngine;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * The main game's state.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GameState extends BasicGameState {

	private int id;
	private Hud hud;
	private Mario mario;

	private long elapsedMillis;

	public GameState(int id) throws SlickException {
		this.id = id;
		hud = new Hud();
	}

	public void checkMarioOtherEntityCollisions(GameContainer container,
						StateBasedGame game, int delta) throws SlickException {

		if (mario.isDone()) {
			return;
		}

		Rectangle2D.Float mBounds = mario.getHitBounds();
		Area area = GameInfo.get().getLevel().getCurrentArea();
		List<Character> chars = area.getOtherCharacters();

		for (Iterator<Character> i=chars.iterator(); i.hasNext();) {

			Character c = i.next();
			Rectangle2D.Float eBounds = c.getHitBounds();

			if (mBounds.intersects(eBounds)) {
				if (c.collidedWithMario(mario)) {
					i.remove();
				}
				else if (mario.isDone()) {
					mario.die(true); // Calls setDone()
					break;
				}
			}

		}

		if (!mario.isBlinking()) {
			List<FireStick> fireSticks = area.fireSticks;
			for (FireStick stick : fireSticks) {
				if (stick.intersects(mario)) {
					if (mario.shrink()) {
						mario.die(true); // Calls done
						break;
					}
				}
			}
		}

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
										throws SlickException {

		boolean fromTextState = GameInfo.get().getTextMessage()!=null;

		if (fromTextState) {
			GameInfo.get().setTextMessage(null);
		}
		else {
			// Mario and the level have already been reset.
			Level level = GameInfo.get().getLevel();
			Area area = level.getCurrentArea();
			SoundEngine.get().playMusic(area.getMusic(), true);
if (level.getStartingAnimation()!=null) {
	Animation anim = level.getStartingAnimation();
	mario.setActive(false);
	area.addTemporaryAnimation(anim);
	SoundEngine.get().play(SoundEngine.SOUND_WARP);
}
System.out.println(mario.getDirection());
			elapsedMillis = 0;
		}

	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		Main main = (Main)game;
		this.mario = main.getMario();
	}

	public void marioDied(StateBasedGame game, Mario mario) {
		boolean dead = PlayerInfo.get(0).die();
		int state = dead ? Constants.STATE_GAME_OVER : Constants.STATE_PRE_LEVEL;
		game.enterState(state, new FadeOutTransition(),
								new FadeInTransition());
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();
		if (area==null) { // First frame, between init() and enter()
			return;
		}
		area.render(container, game, g, null);
		mario.render(container, game, g, null);
		area.renderForeground(container, game, g, null);
		hud.render(container, game, g, PlayerInfo.get(0), level,
						(int)(elapsedMillis/1000f));
	}

	public void reset() throws SlickException {
		GameInfo.get().getLevel().reset();
	}

	@Override
	public void update(GameContainer container, final StateBasedGame game, int delta)
			throws SlickException {

		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();

		if (!mario.isDone()) {

			elapsedMillis += delta;
			if (elapsedMillis/1000.0f >= level.getTotalTime()) {
				((Main)game).restart();
			}

			Input input = container.getInput();
			if (input.isKeyPressed(Input.KEY_F)) {
				mario.setState(MarioState.FIRE);
			}
			else if (input.isKeyPressed(Input.KEY_B)) {
				mario.setState(MarioState.BIG);
			}
			else if (input.isKeyPressed(Input.KEY_S)) {
				mario.setState(MarioState.SMALL);
			}
			else if (input.isKeyPressed(Input.KEY_O)) {
				OneUp.doOneUp(mario);
			}
			if (input.isKeyPressed(Input.KEY_G)) {
				level.toggleShowGrid();
			}
			if (input.isKeyPressed(Input.KEY_R)) {
				Character.toggleRenderHitBoxes();
			}

		}

		Block.updateAnimations(delta);
		mario.update(container, game, delta);
		area.update(container, game, delta);
		if (!mario.isDone()) {
			checkMarioOtherEntityCollisions(container, game, delta);
		}
		level.checkOtherEntityCollisions(container, game, delta);

		if (!mario.isDone()) {

			// If Mario went through a goal post
			if (area.isGoalReached(mario)) {
				game.enterState(Constants.STATE_LEVEL_COMPLETED);
				return;
			}

		}

		// If Mario fell off the map
		else if (mario.getY()>area.getHeight()) {
//			Animation anim = mario.createDyingAnimation();
//			area.addTemporaryAnimation(anim);
			mario.setY(0); // HACK to make this only happen once
			SoundEngine.get().playMusic(SoundEngine.MUSIC_MARIO_DIES, false);
			FutureTask task = new FutureTask(3000) {
				@Override
				public void run() {
					GameState gs = (GameState)game.getCurrentState();
					gs.marioDied(game, mario);
				}
			};
			area.addFutureTask(task);
		}

	}

}
