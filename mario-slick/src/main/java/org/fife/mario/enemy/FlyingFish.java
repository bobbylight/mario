package org.fife.mario.enemy;

import java.util.Random;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.Constants;
import org.fife.mario.Fireball;
import org.fife.mario.Mario;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A fish that flies out to attack Mario when he's on land.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FlyingFish extends Enemy {

	/**
	 * The width and height of a flying fish.
	 */
	public static final int SIZE		= 32;

	/**
	 * The fish's x-velocity.
	 */
	private float vx;

	/**
	 * The fish's current y velocity.
	 */
	private float vy;


	/**
	 * Constructor.
	 *
	 * @param area
	 * @param x
	 * @param y
	 */
	public FlyingFish(Area area, float x, float y) throws SlickException {
		super(area, x, y);
		Random r = new Random();
		if (r.nextInt(10)<8) {
			vx = -(r.nextInt(5) + 5f);
		}
		else {
			vx = -(r.nextInt(3) + 2f);
		}
		vy = -12 - r.nextInt(4);
	}


	/**
	 * Does nothing; flying fish cannot be bumped.
	 */
	@Override
	public boolean bump(Character ch) {
		return false;
	}


	/**
	 * Does nothing; flying fish cannot hit anything.
	 */
	@Override
	public boolean collidedWith(Character ch) {
		if (ch instanceof Fireball) {
			dieFrom(ch); // Create dying animation
			return true;
		}
		return false;
	}


	private Animation createDyingAnimation(int dir) {

		org.newdawn.slick.Animation a = new org.newdawn.slick.Animation();
		Image image = getImage(3, 2);
		image = image.getFlippedCopy(true, true);
		a.addFrame(image, 100);
		image = getImage(5, 2);
		image = image.getFlippedCopy(true, true);
		a.addFrame(image, 100);

		float x = getX();
		float y = getY();
		DyingAnimation anim = new DyingAnimation(x, y, a, dir);
		return anim;

	}


	private void dieFrom(Character ch) {
		float diff = getCenterX() - ch.getCenterX();
		int dir = diff<0 ? LEFT : RIGHT;
		Animation anim = createDyingAnimation(dir);
		area.addTemporaryAnimation(anim);
		SoundEngine.get().play(SoundEngine.SOUND_KICK);
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_FISH;
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
	public float getWidth() {
		return SIZE;
	}


	/**
	 * Always returns <code>true</code>, as flying fish are always moving.
	 *
	 * @return <code>true</code>, always.
	 */
	@Override
	public boolean isMoving() {
		return true;
	}


	@Override
	protected boolean stompedOn(Mario mario) {
		mario.setYSpeed(-8/2);
		dieFrom(mario);
		return true;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		int col = vy<0 ? 3 : 5;
		setSSIndex(2, col);

		moveX(vx);

		if (vy<7) {
			vy += Constants.GRAVITY * 0.75f;
			vy = Math.min(vy, 7);
		}
		moveY(vy);

	}


	/**
	 * Animation of this fish dying.
	 */
	private class DyingAnimation extends Animation {

		private org.newdawn.slick.Animation anim;
		private float dx;
		private float dy;

		DyingAnimation(float x, float y, org.newdawn.slick.Animation anim, int dir) {
			super(x, y);
			this.anim = anim;
			dx = dir==LEFT ? -2 : 2;
			dy = -4;
		}

		@Override
		public float getHeight() {
			return SIZE;
		}

		@Override
		public float getWidth() {
			return SIZE;
		}

		@Override
		protected void renderImpl(GameContainer container, StateBasedGame game,
				Graphics g, Color filter) throws SlickException {
			float x = getX() - area.xOffs;
			float y = getY() - area.yOffs;
			anim.draw(x, y, filter);
		}

		@Override
		protected void updateImpl(GameContainer container, StateBasedGame game,
				int delta) throws SlickException {
			moveX(dx);
			if (dy<8) {
				dy += 0.2f;
				dy = Math.min(dy, 8);
			}
			moveY(dy);
			if (getY()>area.getHeight()) {
				setDone(true);
			}
		}

	}


}
