package org.fife.mario.enemy;

import org.fife.mario.Character;
import org.fife.mario.Fireball;
import org.fife.mario.Mario;
import org.fife.mario.Shell;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.anim.PoofAnimation;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A piranha plant.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PiranhaPlant extends Enemy {

	private int height;
	private boolean up;
	private int inc;
	private long time;
	private int topDelay;
	private int bottomDelay;

	private static final int MAX_HEIGHT			= 66;
	private static final int TOP_DELAY			= 850;
	private static final int BOTTOM_DELAY		= 1500;

	public PiranhaPlant(Area area, float x, float y, boolean up)
						throws SlickException {
		super(area, x, y);
		height = 0;
		this.up = up;
		inc = 1;
		time = 0;
	}

	@Override
	public boolean bump(Character ch) {
		return false;
	}

	@Override
	public boolean collidedWith(Character ch) {
		if (ch instanceof Fireball) {
			PoofAnimation anim = new PoofAnimation(ch.getX(), ch.getY());
			area.addTemporaryAnimation(anim);
			SoundEngine.get().play(SoundEngine.SOUND_KICK);
			return true;
		}
		else if ((ch instanceof Shell) && ch.isMoving()) {
			PoofAnimation anim = new PoofAnimation(getCenterX(), getCenterY());
			area.addTemporaryAnimation(anim);
			SoundEngine.get().play(SoundEngine.SOUND_KICK);
			return true;
		}
		return false;
	}


	/**
	 * Overridden to always damage Mario, if he isn't flashing or invincible.
	 *
	 * @return Whether this monster is dead after this collision.
	 */
	@Override
	public boolean collidedWithMario(Mario mario) {
		if (!mario.isBlinking()) {
			mario.shrink();
		}
		return false;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getHitMarginTop() {
		// When his mouth is open, he's just a tad shorter.
		return getSSCol()==0 ? 2 : 0;
	}

	@Override
	public float getHitMarginX() {
		return 1;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_PIRANHA_PLANT;
	}

	@Override
	public float getWidth() {
		return 32;
	}


	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean marioIsTooClose() {
		Mario mario = Mario.get();
		float marioCX = mario.getCenterX();
		float cx = getCenterX();
		// TODO: When dir can be left or right too, distance calculation
		// should be dependent on whether we're vertical or horizontal
		return Math.abs(marioCX-cx) < 32 + mario.getWidth()/2 + 8;//VERTICAL_SHY_RANGE;
	}


	@Override
	protected void renderImage(Image image, GameContainer container,
		StateBasedGame game, Graphics g, Color filter) throws SlickException {

		if (filter==null) {
			filter = Color.white;
		}

		int w = image.getWidth();
		g.drawImage(image, 0,0,w,height, 0,0,w,height, filter);

	}


	/**
	 * Sets this piranha to be back in his pipe.
	 */
	@Override
	public void reset() {
		topDelay = 0;
		bottomDelay = BOTTOM_DELAY;
		inc = 1;
		moveY(height);
		height = 0;
		time = 0;
	}

	@Override
	protected boolean stompedOn(Mario mario) {
		return false;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
							int delta) throws SlickException {

		if (topDelay>=0) {
			topDelay -= delta;
		}
		else if (bottomDelay>=0) {
			if (marioIsTooClose()) {
				// Plants are shy and don't pop out if Mario is beside them.
				bottomDelay = BOTTOM_DELAY;
			}
			else {
				bottomDelay -= delta;
			}
		}
		else {
			moveY(-inc);
			height += inc;
			if (height==MAX_HEIGHT) {
				topDelay = TOP_DELAY;
				inc = -inc;
			}
			else if (height==0) {
				bottomDelay = BOTTOM_DELAY;
				inc = -inc;
			}
		}

		// Animate the mouth
		if (height>=0) {
			time += delta;
			if (time>=1000/3) { // Open/close 3 times per second
				time -= 1000/3;
				setSSIndex(0, getSSCol()^1);
			}
		}

	}
}
