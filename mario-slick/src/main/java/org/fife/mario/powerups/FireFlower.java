package org.fife.mario.powerups;

import org.fife.mario.Mario;
import org.fife.mario.MarioState;
import org.fife.mario.PowerUp;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The fire flower power up.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FireFlower extends AbstractPowerUp {


	public FireFlower(float x, float y) {
		super(x, y);
		int imageIndex = PowerUp.FIRE_FLOWER.getIndex();
		setSSIndex(imageIndex/4, imageIndex%4);
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		if (mario.getState()==MarioState.SMALL) {
			mario.setState(MarioState.BIG);
		}
		else {
			mario.setState(MarioState.FIRE);
		}
		addPointsAnimation();
		return true;
	}


	@Override
	public boolean isMoving() {
		return ySpeed!=0;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		if (airborne) {
			updateYSpeed();
		}

		motion.set(0, ySpeed);

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
