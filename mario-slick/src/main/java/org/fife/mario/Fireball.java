package org.fife.mario;

import org.fife.mario.anim.PointsAnimation;
import org.fife.mario.anim.PoofAnimation;
import org.fife.mario.enemy.Enemy;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A fireball thrown by Mario.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Fireball extends Character {

	private float xSpeed;
	private float ySpeed;

	/**
	 * The width and height of a fireball.
	 */
	public static final int SIZE		= 16;


	public Fireball(int dir) {
		this.dir = dir;
		airborne = true;
	}


	private void addPoofAnimation() {
		float x = dir==LEFT ? getX() : getX() + getWidth();
		PoofAnimation poofAnim = new PoofAnimation(x, getY());
		area.addTemporaryAnimation(poofAnim);
	}


	/**
	 * Called when the fireball hits an object beneath it and should "bounce"
	 * back up.
	 */
	private void bounce() {
		airborne = true;
		ySpeed = -5.5f;
	}


	@Override
	public boolean bump(Character ch) {
		// TODO
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		// Explicitly check for Enemy since fireballs go into enemies
		// quite frequently.
		if (!(ch instanceof Enemy) &&
				ch.isLandable() && isOnTopOf(ch)) {
			bounce();
			return false;
		}
		else if (ch.isSolid()) {
			addPoofAnimation();
			area.addTemporaryAnimation(new PointsAnimation(getX(), getY(), "10"));
			PlayerInfo.get(0).incScore(10);
			return true;
		}
		return false;
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public float getHeight() {
		return SIZE;
	}


	@Override
	public float getHitMarginTop() {
		return 2;
	}


	@Override
	public float getHitMarginX() {
		return 2;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_FIREBALL;
	}


	@Override
	public float getWidth() {
		return SIZE;
	}


	public float getXSpeed() {
		return dir==LEFT ? -6 : 6;
	}


	@Override
	public boolean isMoving() {
		return true;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		xSpeed = getXSpeed();
		if (airborne) {
			updateYSpeed();
		}

		motion.set(xSpeed, ySpeed);

		CollisionResult res = area.checkHittingWall(this, motion);
		if (res.leftWall) {
			SoundEngine.get().play(SoundEngine.SOUND_HIT_HEAD);
			addPoofAnimation();
			setDone(true);
			return;
		}
		else if (res.rightWall) {
			SoundEngine.get().play(SoundEngine.SOUND_HIT_HEAD);
			addPoofAnimation();
			setDone(true);
			return;
		}
		else if (!area.isInUpdateBounds(this)) {
			// Don't remember me if I've flown off the screen
			setDone(true);
		}

		if (airborne) {
			// Check y-speed too to prevent "sticking" inside block when jumping
			if (ySpeed>=0 && area.isTerrainBelow(this)) {
				bounce();
			}
//			// Check y-speed too to prevent "sticking" to bottom of block when falling
//			else if (ySpeed<0 && level.isTerrainAbove(this)) {
//				ySpeed = 0;
//			}
		}
		else if (!area.isTerrainBelow(this)) {
			airborne = true;
		}

		imgIndex++;
		int temp = (imgIndex/2)%4;
		int row = temp/2;
		int col = temp%2;
		setSSIndex(row, col);

	}
private int imgIndex;

	/**
	 * Called via {@link #updateImpl(GameContainer, StateBasedGame, int)} when
	 * this fireball is airborne.
	 */
	protected void updateYSpeed() {
		if (ySpeed<8) {
			ySpeed += Constants.GRAVITY;
			ySpeed = Math.min(ySpeed, 8);
		}
		motion.y = ySpeed;
	}


}
