package org.fife.mario.enemy;

import org.fife.mario.Character;
import org.fife.mario.level.Area;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A flying Koopa Troopa.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FlyingKoopaTroopa extends KoopaTroopa {

	private static final float STEP = 2*(float)Math.PI/90;

	/**
	 * Used by "smart" flying koopas, input to sin().
	 */
	private float yAccelFrame;

	private float distanceTraveled;

	/**
	 * The distance that the koopa will fly before turning around.
	 * This is ignored for "dumb" (i.e. green) koopas.
	 */
	private float distanceToTravel;

	private static final float DEFAULT_DISTANCE_TO_TRAVEL			= 160f;


	/**
	 * Constructor.
	 */
	public FlyingKoopaTroopa() throws SlickException {
		distanceToTravel = DEFAULT_DISTANCE_TO_TRAVEL;
	}


	/**
	 * Constructor.
	 */
	public FlyingKoopaTroopa(float x, float y) throws SlickException {
		super(x, y);
		distanceToTravel = DEFAULT_DISTANCE_TO_TRAVEL;
	}


	/**
	 * Constructor.
	 *
	 * @param area
	 * @param x
	 * @param y
	 * @param color
	 * @throws SlickException
	 */
	public FlyingKoopaTroopa(Area area, float x, float y, int color)
						throws SlickException {
		super(area, x, y, color);
		distanceToTravel = DEFAULT_DISTANCE_TO_TRAVEL;
	}


	@Override
	protected Character createStompReplacementCharacter() throws SlickException{
		float y = getY() + getHeight() - KoopaTroopa.HEIGHT;
		return new KoopaTroopa(area, getX(),y, getColor());
	}


	@Override
	public float getXSpeed() {
		float speed = super.getXSpeed();
		if (isSmart()) {
			speed *= 0.8f;
		}
		return speed;
	}


	@Override
	protected float getFlightYAcceleration() {
		if (isSmart()) {
			yAccelFrame += STEP;
			if (yAccelFrame>=2*Math.PI) {
				yAccelFrame = 0;
			}
			float amt = (float)Math.sin(yAccelFrame);
			amt *= 0.3f;
			return amt;
		}
		else {
			return -0.1f;
		}
	}


	@Override
	protected void setSSIndex(int row, int col) {
		super.setSSIndex(row, col+3);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		super.updateImpl(container, game, delta);
		if (!turning && isSmart()) {
			distanceTraveled += Math.abs(getXSpeed());//motion.x);
			if (distanceTraveled>distanceToTravel) {
				if (motion.x<0) {
					motion.x += distanceTraveled - distanceToTravel;
				}
				else {
					motion.x -= distanceTraveled - distanceToTravel;
				}
				dir = (dir==RIGHT) ? LEFT : RIGHT;
				turning = true;
				frame = 0;
				distanceTraveled = 0;
			}
		}

	}


	@Override
	public void updateFalling(GameContainer container, StateBasedGame game,
			int delta, Vector2f motion) {
		if (isSmart()) {
			motion.x = getXSpeed();
motion.y = getFlightYAcceleration();
		}
		else {
			super.updateFalling(container, game, delta, motion);
		}
	}


	/*
	 * (non-Javadoc)
	 * Only called when a green flying koopa "lands."
	 * @see org.fife.mario.KoopaTroopa#updateWalking
	 */
	@Override
	public void updateWalking(GameContainer container, StateBasedGame game,
			int delta, Vector2f motion) throws SlickException {
		motion.x = getXSpeed();
		ySpeed = motion.y = -6;
		airborne = true;
	}


}
