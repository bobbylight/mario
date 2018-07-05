package org.fife.mario;

import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * Played after Mario reaches a level's goal.  Fades the background and
 * foreground to black, and Mario flashes a peace sign.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LevelCompletedState extends BasicGameState {

	private Image buf;
	private int id;
	private float time;
	private int state; // 0=>fadeout, 1=>peace, 2=>fadein
	private Color fadeOverlay;
	private Color alpha;

	private static final float TOTAL_FADEOUT_TIME		= 7350; // 7.35 seconds
	private static final float TOTAL_PEACE_TIME			= 1000; // 1 seconds
	private static final float TOTAL_FADEIN_TIME		= 4000; // 4 seconds


	/**
	 * Constructor.
	 *
	 * @param id The ID for this state.
	 */
	public LevelCompletedState(int id) {
		this.id = id;
		fadeOverlay = new Color(0,0,0, 0);
		alpha = new Color(1f,1f,1f, 1f);
	}


	@Override
	public int getID() {
		return id;
	}


	@Override
	public void enter(GameContainer container, StateBasedGame game)
							throws SlickException {
		Mario mario = Mario.get();
		mario.setCompletedLevel(true);
		time = TOTAL_FADEOUT_TIME;
		state = 0;
		SoundEngine.get().playMusic(SoundEngine.MUSIC_COURSE_CLEAR, false);
	}


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		buf = new Image(container.getWidth(), container.getHeight());
	}


	@Override
	public void leave(GameContainer container, StateBasedGame game)
							throws SlickException {
		Mario mario = ((Main)game).getMario();
		mario.setCompletedLevel(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {

		// Paint the area behind Mario to an off-screen image
		Graphics origG = g;
		g = buf.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();
		area.render(container, game, g, Color.white);

		// Fade everything "behind" Mario by painting over it with an overlay.
		g.setColor(fadeOverlay);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		// Paint Mario normally; he never fades.
		Mario mario = ((Main)game).getMario();
		if (state!=1) {
			mario.render(container, game, g, Color.white);
		}
		else { // Flashing the peace sign
			int dir = mario.getDirection();
			MarioState state = mario.getState();
			int col = 10;
			int row = state.getIndex()*2 + dir;
			SpriteSheetManager ssm = SpriteSheetManager.instance();
			Image img = ssm.getImage(SpriteSheetManager.SHEET_MARIO, col, row);
			g.drawImage(img, mario.getX()-area.xOffs, mario.getY()-area.yOffs);
		}

		// Paint the "foreground" stuff as fading.
		area.renderForeground(container, game, g, alpha);

		// Paint our off-screen buffer on the screen.
		g.flush();
		origG.drawImage(buf, 0, 0);

	}


	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		time -= delta;

		if (state==0) { // Fade out
			if (time<0) {
				state = 1; // Peace
				time = TOTAL_PEACE_TIME;
			}
			else {
				alpha.r = alpha.g = alpha.b = alpha.a = time/TOTAL_FADEOUT_TIME;
				fadeOverlay.a = 1 - alpha.a;
				((Main)game).getMario().updateImpl(container, game, delta);
			}
		}

		else if (state==1) { // Peace
			if (time<0) {
				state = 2; // Fade in
				time = TOTAL_FADEIN_TIME;
			}
		}

		else { // Fade in
			if (time<0) {
				GameInfo.get().loadNextLevel();
				game.enterState(Constants.STATE_PRE_LEVEL, null, null);
			}
			else {
				((Main)game).getMario().updateImpl(container, game, delta);
				alpha.r = alpha.g = alpha.b = alpha.a = 1 - time/TOTAL_FADEIN_TIME;
				fadeOverlay.a = 1 - alpha.a;
			}
		}

	}


}
