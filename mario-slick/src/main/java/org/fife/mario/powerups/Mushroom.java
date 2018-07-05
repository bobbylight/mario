package org.fife.mario.powerups;

import org.fife.mario.CollisionResult;
import org.fife.mario.Mario;
import org.fife.mario.MarioState;
import org.fife.mario.PowerUp;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A super mushroom.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Mushroom extends AbstractPowerUp {

	private float xSpeed;


	public Mushroom(float x, float y, boolean goRight) {
		super(x, y);
		dir = goRight ? 1 : -1;
		int imageIndex = PowerUp.MUSHROOM.getIndex();
		setSSIndex(imageIndex/4, imageIndex%4);
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		if (mario.getState()==MarioState.SMALL) {
			mario.setState(MarioState.BIG);
		}
		addPointsAnimation();
		return true;
	}

	@Override
	public boolean isMoving() {
		return true;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

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
				airborne = false;
				ySpeed = 0;
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
