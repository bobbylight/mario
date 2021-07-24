package org.fife.mario.enemy;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.CollisionResult;
import org.fife.mario.Constants;
import org.fife.mario.Fireball;
import org.fife.mario.Mario;
import org.fife.mario.Shell;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;
import org.fife.mario.powerups.AbstractPowerUp;
import org.fife.mario.sound.SoundEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The "Goomba" enemy.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Goomba extends Enemy {

	private int frame;
	private float ySpeed;
	private boolean upsideDown;

	private static final int HEIGHT		= 62;
	private static final int WIDTH		= 66;


	/**
	 * Constructor.
	 */
	public Goomba() throws SlickException {
		this(0, 0);
	}


	/**
	 * Constructor.
	 */
	public Goomba(int x, int y) throws SlickException {
		this(null, x, y);
	}


	/**
	 * Constructor.
	 *
	 * @param area The area the enemy is in.
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public Goomba(Area area, float x, float y) throws SlickException {
		super(area, x, y);
		dir = LEFT;
		upsideDown = false;

	}


	@Override
	public boolean bump(Character ch) {
		return kickedBy(ch);
	}


	@Override
	public boolean collidedWith(Character ch) {
		if (ch instanceof Fireball) {
			return kickedBy(ch); // Create dying animation
		}
		else if ((ch instanceof Shell) && ch.isMoving()) {
			return kickedBy(ch); // Create dying animation
		}
		else if (ch instanceof AbstractPowerUp) {
			return false;
		}
		dir = dir==LEFT ? RIGHT : LEFT;
		return false;
	}


	private Animation createDyingAnimation(int dir) {

		org.newdawn.slick.Animation a = new org.newdawn.slick.Animation();
		Image image = getImage(0, dir);
		image = image.getFlippedCopy(true, true);
		a.addFrame(image, 200);
		image = getImage(1, dir);
		image = image.getFlippedCopy(true, true);
		a.addFrame(image, 200);

		float x = getX();
		float y = getY();
		DyingAnimation anim = new DyingAnimation(x, y, a, dir);
		return anim;

	}


	@Override
	public float getHitMarginTop() {
		return 30; // Only bottom 32 pixels.
	}


	@Override
	public float getHitMarginX() {
		return 17; // Only middle  32 pixels
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_GOOMBA;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	public float getXSpeed() {
		return upsideDown ? 0 : (dir==LEFT ? -1 : 1);
	}


	@Override
	public boolean isKickable() {
		return upsideDown;
	}


	@Override
	public boolean isLandable() {
		return true;
	}


	@Override
	public boolean isMoving() {
		return !upsideDown;
	}


	@Override
	public boolean kickedBy(Character ch) {
		float diff = getCenterX() - ch.getCenterX();
		int dir = diff<0 ? LEFT : RIGHT;
		Animation anim = createDyingAnimation(dir);
		area.addTemporaryAnimation(anim);
		SoundEngine.get().play(SoundEngine.SOUND_KICK);
		return true;
	}


	@Override
	protected void renderImage(Image image, GameContainer container,
			StateBasedGame game, Graphics g, Color filter) throws SlickException {
		if (upsideDown) {
			g.pushTransform();
			float y = getHitMarginTop() + (getHeight()-getHitMarginTop())/2;
			g.rotate(getWidth()/2, y, 180);
		}
		g.drawImage(image, 0,0, filter);
		if (upsideDown) {
			g.popTransform();
		}
	}


	@Override
	protected boolean stompedOn(Mario mario) {
/*
 * Code to allow Mario to be carried.
mario.setY(getY()+getHitMarginTop()-mario.getHeight());
mario.setYSpeed(0);
addEntityStandingOn(mario);
return false;
*/
		if (!upsideDown) {
			mario.setYSpeed(-8);
			upsideDown = true;
			SoundEngine.get().play(SoundEngine.SOUND_STOMP);
			return false;
		}
		mario.setYSpeed(-8/2);
		return kickedBy(mario); // Creates dying animation and returns true
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
							throws SlickException {

		motion.set(0, 0);

		if (airborne) {
			updateFalling(container, game, delta, motion);
		}
		else {
			updateWalking(container, game, delta, motion);
		}

		CollisionResult cr = area.checkHittingWall(this, motion);
		if (airborne) {
			if (area.isTerrainBelow(this)) {
				airborne = false;
				setSSIndex(dir, 0);
				ySpeed = 0;
			}
			else if (area.isTerrainAbove(this)) {
				ySpeed = 0;
			}
		}
		else if (!area.isTerrainBelow(this)) {
			airborne = true;
			ySpeed = 0;
		}

		if (cr.leftWall) {
			dir = RIGHT;
		}
		else if (cr.rightWall) {
			dir = LEFT;
		}

	}


	public void updateFalling(GameContainer container, StateBasedGame game,
			int delta, Vector2f motion) {

		motion.x = getXSpeed();

		if (ySpeed<7) {
			ySpeed += Constants.GRAVITY;
			ySpeed = Math.min(ySpeed, 7);
		}
		motion.y = ySpeed;
	}


	public void updateWalking(GameContainer container, StateBasedGame game,
			int delta, Vector2f motion) throws SlickException {

		motion.x = getXSpeed();

		frame++;
		if (frame<=10) {
			setSSIndex(dir, 0);
		}
		else if (frame<=20) {
			setSSIndex(dir, 1);
		}
		else {
			frame = 0;
			setSSIndex(dir, 0);
		}

	}


	private static class DyingAnimation extends Animation {

		private org.newdawn.slick.Animation anim;
		private float dx;
		private float dy;

		DyingAnimation(float x, float y,
						org.newdawn.slick.Animation anim, int dir) {
			super(x, y);
			this.anim = anim;
			dx = dir==LEFT ? -2 : 2;
			dy = -4;
		}

		@Override
		public float getHeight() {
			return WIDTH;
		}

		@Override
		public float getWidth() {
			return WIDTH;
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
