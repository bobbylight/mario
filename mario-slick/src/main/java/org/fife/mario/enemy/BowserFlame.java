package org.fife.mario.enemy;

import org.fife.mario.Character;
import org.fife.mario.Mario;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A flame from Bowser's mouth.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class BowserFlame extends Enemy {

	public static final float WIDTH		= 48;
	public static final float HEIGHT	= 30;

	private int frame;
	private int frameTime;

	private static final int MAX_FRAME_TIME		= 120;


	public BowserFlame(Area area, float x, float y) throws SlickException {
		super(area, x, y);
		frameTime = MAX_FRAME_TIME;
	}


	/**
	 * Never called due to overridden {@link #collidedWithMario(Mario)}.
	 */
	@Override
	protected boolean stompedOn(Mario mario) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Does nothing since flames cannot be bumped.
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean bump(Character ch) {
		return false;
	}


	/**
	 * Returns <code>false</code> always as flames can never be extinguished.
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean collidedWith(Character ch) {
		return false;
	}


	/**
	 * Overridden to always hurt Mario.
	 *
	 * @return <code>true</code> always.
	 */
	@Override
	public boolean collidedWithMario(Mario mario) {
		if (!mario.isBlinking()) { // If he's flickering from a previous hit
			mario.shrink();
		}
		return false;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_BOWSER_FLAME;
	}


	@Override
	public boolean isMoving() {
		return true;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		return 1;
	}


	@Override
	public float getHitMarginX() {
		return 1;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		// TODO: Implement me
		moveX(-2);

		// If we've flown off the screen, don't bother remembering me
		if (getX()+getWidth() < area.xOffs) {
			setDone(true);
			return;
		}

		frameTime -= delta;
		if (frameTime<=0) {
			frameTime += MAX_FRAME_TIME;
			frame = (frame+1) % 6;
		}
		setSSIndex(frame/4, frame%4);

	}

}
