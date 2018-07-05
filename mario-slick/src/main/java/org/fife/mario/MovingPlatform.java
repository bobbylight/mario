package org.fife.mario;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A moving platform.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class MovingPlatform extends Character {

	public static final int TILE_WIDTH	= 18;

	public static final int WIDTH	= TILE_WIDTH * 7;
	public static final int HEIGHT	= 22;

	private Area area;
	private SpriteSheet ss;
	private int inc;


	/**
	 * Constructor.
	 *
	 * @param area
	 * @param x
	 * @param y
	 * @param up
	 */
	public MovingPlatform(Area area, float x, float y, boolean up) {
		super(x - WIDTH + TILE_WIDTH, y);
		this.area = area;
		ss = SpriteSheetManager.instance().getSheet(SpriteSheetManager.SHEET_PLATFORM);
		inc = up ? -1 : 1;
	}


	/**
	 * Never occurs.
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean bump(Character ch) {
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		// TODO
		return false;
	}


	@Override
	public boolean collidedWithMario(Mario mario) {

		// Determine whether Mario hit the "top" of the this platform, or on
		// its side or bottom.
		float ySpeed = mario.getYSpeed();
		if (ySpeed>0) { // Going down
			if (mario.isOnTopOf(this)) {
				mario.setY(getY()+getHitMarginTop()-mario.getHeight());
				mario.setYSpeed(0);
				addEntityStandingOn(mario);
				return false;
			}
		}

		return false;

	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		return 0;
	}


	@Override
	public float getHitMarginX() {
		return 0;
	}


	/**
	 * Not really used by this class.
	 */
	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_PLATFORM;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	/**
	 * Returns <code>true</code> since entities can land on this platform.
	 *
	 * @return <code>true</code> always.
	 */
	@Override
	public boolean isLandable() {
		return true;
	}


	@Override
	public boolean isMoving() {
		return true;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {

		int x = (int)(getX() - area.xOffs);
		int y = (int)(getY() - area.yOffs);
		g.translate(x, y);
//System.out.println(y);
		x = 0;
		Image img = ss.getSubImage(0, 0);
		g.drawImage(img, x,0, Color.white);

		img = ss.getSubImage(1, 0);
		for (int i=0; i<7-2; i++) {
			x += TILE_WIDTH;
			g.drawImage(img, x,0, Color.white);
		}

		img = ss.getSubImage(2, 0);
		g.drawImage(img, x,0, Color.white);

		g.resetTransform();

	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		moveY(inc);

		if (getY()>=area.getHeight()) {
			clearEntitiesStandingOn();
			setY(-getHeight());
		}

		else if (getY()<=-getHeight()) {
			clearEntitiesStandingOn();
			setY(area.getHeight()-1);
		}

	}


}
