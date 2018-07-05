package org.fife.mario;

import org.fife.mario.level.Area;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The character Mario meets in castle that tells him "The princess is in another castle!".
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Toad extends Character {


	/**
	 * Constructor.
	 *
	 * @param area The area this toad is in.
	 * @param x The x-location of this Toad.
	 * @param y The y-location of this Toad.
	 */
	public Toad(Area area, float x, float y) {
		setArea(area);
		setLocation(x, y - getHeight() + 32);
	}


	@Override
	public boolean bump(Character ch) {
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		return false;
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		Main main = Main.get();
		GameInfo.get().setTextMessage("Thanks Mario!\nBut our princess is in\nanother castle!!");
		main.enterState(Constants.STATE_CASTLE_COMPLETED);
		return false;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_TOAD;
	}


	@Override
	public boolean isMoving() {
		return false;
	}


	@Override
	public float getHeight() {
		return Mario.HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		return 0;
	}


	@Override
	public float getHitMarginX() {
		return 0;
	}


	@Override
	public float getWidth() {
		return Mario.WIDTH;
	}


	/**
	 * Overridden to let fireballs pass through Toad. They shouldn't hit him!
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean isSolid() {
		return false;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
	}


}
