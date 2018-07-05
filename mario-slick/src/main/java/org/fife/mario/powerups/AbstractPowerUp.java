package org.fife.mario.powerups;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import org.fife.mario.Character;
import org.fife.mario.Constants;
import org.fife.mario.Mario;
import org.fife.mario.PlayerInfo;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.anim.PointsAnimation;


/**
 * Base class for power-ups.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractPowerUp extends Character {

	protected float ySpeed;


	private static final int WIDTH	= 32;
	private static final int HEIGHT	= 32;


	/**
	 * Constructor.
	 *
	 * @param x The x-location of the power up.
	 * @param y The y-location of the power up.
	 */
	public AbstractPowerUp(float x, float y) {
		setLocation(x, y);
	}


	/**
	 * Most power-up subclasses should call this method from
	 * {@link #collidedWithMario(Mario)} to ensure the points animation is
	 * rendered.  This also actually increments the player's score.
	 */
	protected void addPointsAnimation() {
		int score = 1000;
		area.addTemporaryAnimation(new PointsAnimation(getX(), getY(), "1000"));
		PlayerInfo.get(0).incScore(score);
	}


	@Override
	public boolean bump(Character ch) {
		ySpeed -= 4;
		airborne = true;
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		return false;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	public static Image getImage(int index) {
		int x = index % 3;
		int y = index / 3;
		return SpriteSheetManager.instance().getImage(
				SpriteSheetManager.SHEET_POWER_UPS, x, y);
	}


	/**
	 * Returns <code>0</code>, as power-ups have no hit margin.
	 *
	 * @return The margin top y-bounds.
	 * @see #getHitMarginX()
	 */
	@Override
	public float getHitMarginTop() {
		return 0;
	}


	/**
	 * Returns <code>0</code>, as power-ups have no hit margin.
	 *
	 * @return The margin x-bounds.
	 * @see #getHitMarginTop()
	 */
	@Override
	public float getHitMarginX() {
		return 0;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_POWER_UPS;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	@Override
	public boolean isSolid() {
		return false;
	}


//	@Override
//	public void render(GameContainer container, StateBasedGame game, Graphics g)
//						throws SlickException {
//		float x = getX() - area.xOffs;
//		float y = getY() - area.yOffs;
//		Image image = getImage(imageIndex);
//		image.draw(x, y);
//	}


	/**
	 * Called via {@link #updateImpl(GameContainer, StateBasedGame, int)} when
	 * this power-up is airborne.
	 */
	protected void updateYSpeed() {
		if (ySpeed<8) {
			ySpeed += Constants.GRAVITY;
			ySpeed = Math.min(ySpeed, 8);
		}
		motion.y = ySpeed;
	}


}
