package org.fife.mario;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The "axe" that mario uses to cut the ties for the bridge Bowser is on.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Axe extends Character {

	public static final int WIDTH			= 32;
	private static final int EXTRA_HEIGHT	= 32;
	public static final int HEIGHT			= 32 + EXTRA_HEIGHT; // Cheat so Mario can't jump "over" it.

	private boolean collapsed;

	public Axe(Area area, float x, float y) {
		setArea(area);
		setLocation(x, y - EXTRA_HEIGHT);
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

		if (!collapsed) {
			collapsed = true;
			Main.get().enterState(Constants.STATE_BRIDGE_COLLAPSING);
		}

		return false;

	}

	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_AXE;
	}

	@Override
	public boolean isMoving() {
		return false;
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
		return 0;
	}

	@Override
	public float getWidth() {
		return WIDTH;
	}

	@Override
	protected void renderImage(Image image, GameContainer container,
			StateBasedGame game, Graphics g, Color filter) throws SlickException {
		g.translate(0, EXTRA_HEIGHT);
		super.renderImage(image, container, game, g, filter);
	}

	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		setSSIndex(collapsed ? 1 : 0, 0);
	}

}
