package org.fife.mario.powerups;

import org.fife.mario.CollisionResult;
import org.fife.mario.Mario;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A star that makes Mario temporarily invincible.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Star extends AbstractPowerUp {

	private float xSpeed;
	private int time;
	private int imgCol;

	private static final float INITIAL_Y_SPEED			= -8;
	private static final int COLOR_FLIP_TIME			= 250;


	public Star(float x, float y) {
		super(x, y);
		airborne = true;
		time = COLOR_FLIP_TIME;
		imgCol = 0;
		setSSIndex(2, imgCol); // Set initial image.
		dir = 1;
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		// TODO
		addPointsAnimation();
		return true;
	}

	@Override
	public boolean isMoving() {
		return true;
	}

	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		// Toggle the color of the star.
		time -= delta;
		if (time<=0) {
			time += COLOR_FLIP_TIME;
			imgCol = (imgCol+1) & 1;
			setSSIndex(2, imgCol);
		}

		xSpeed = dir * 2;
		if (airborne) {
			updateYSpeed();
		}

		motion.set(xSpeed, ySpeed);

		CollisionResult res = area.checkHittingWall(this, motion);
		if (res.leftWall) {
			dir = 1;
		}
		else if (res.rightWall) {
			dir = -1;
		}

		if (airborne) {
			// Check y-speed too to prevent "sticking" inside block when jumping
			if (ySpeed>=0 && area.isTerrainBelow(this)) {
				//airborne = true;
				ySpeed = INITIAL_Y_SPEED;
			}
			// Check y-speed too to prevent "sticking" to bottom of block when falling
			else if (ySpeed<0 && area.isTerrainAbove(this)) {
				ySpeed = 0;
			}
		}
		else if (!area.isTerrainBelow(this)) {
			airborne = true;
			ySpeed = 0;
		}

	}


}
