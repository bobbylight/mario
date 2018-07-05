package org.fife.mario.enemy;

import org.fife.mario.Character;
import org.fife.mario.Constants;
import org.fife.mario.GameInfo;
import org.fife.mario.Mario;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The big bad boss of a level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Bowser extends Enemy {

	private float dx;
	private float vy;
	private int dirTime;
	private int stepTime;
	private boolean throwingFireball;
	private int fireballTime;
	private boolean fireballThrown;

	private static final int MAX_DIR_TIME		= 2500;
	private static final float WIDTH			= 68;
	private static final float HEIGHT			= 82;


	public Bowser(Area area, float x, float y) throws SlickException {
		super(area, x, y);
		dx = -0.75f;
		vy = 0;
		dirTime = MAX_DIR_TIME;
	}


	@Override
	public boolean bump(Character ch) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		// TODO Auto-generated method stub
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
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_BOWSER;
	}


	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getHitMarginX() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	/**
	 * Never called (only ever called from super.collidedWithMario()).
	 */
	@Override
	protected boolean stompedOn(Mario mario) {
		return false;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		dirTime -= delta;
		if (dirTime<=0) {
			dirTime += MAX_DIR_TIME;
			dx = -dx;
		}

		if (throwingFireball) {
			fireballTime += delta;
			if (fireballTime<400) {
				setSSIndex(0, 2);
			}
			else if (fireballTime<800) {
				setSSIndex(1, 0);
				if (!fireballThrown) {
					float x = getX() - BowserFlame.WIDTH + 12;
					float y = getY() + BowserFlame.HEIGHT + 5;
					BowserFlame flame = new BowserFlame(area, x, y);
					area.addCharacter(flame);
					SoundEngine.get().play(SoundEngine.SOUND_FLAME);
					fireballThrown = true;
				}
			}
			else {
				throwingFireball = false;
			}
			return;
		}
		else if (area.isTerrainBelow(this)) {
			if (Math.random()>0.994) {
				vy = -6;
			}
			else if (Math.random()>0.996) {
				fireballTime = 0;
				throwingFireball = true;
				fireballThrown = false;
			}
		}
		else {
			vy += Constants.GRAVITY/3;
		}

		motion.set(dx,vy);

		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();
		area.checkHittingWall(this, motion);
		if (airborne) {
			// Check y-speed too to prevent "sticking" inside block when jumping
			if (vy>=0 && area.isTerrainBelow(this)) {
				airborne = false;
				// TODO: Change frame?
				vy = 0;
			}
			// Check y-speed too to prevent "sticking" to bottom of block when falling
			else if (vy<0 && area.isTerrainAbove(this)) {
				// TODO: Change frame?
				vy = 0;
			}
		}
		else if (!area.isTerrainBelow(this)) {
			airborne = true;
			// TODO: Change frame?
		}

		if (vy>0) {
			setSSIndex(0, 0);
		}
		else if (vy<0) {
			setSSIndex(0, 2);
		}
		else {
			stepTime += delta;
			if ((stepTime%1000)<500) {
				setSSIndex(0, 0);
			}
			else {
				setSSIndex(0, 1);
			}
		}

	}


}
