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
 * The goal at the end of a level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Goal extends AbstractEntity {

	private static final int HEIGHT_IN_TILES	= 9;
	public static final float HEIGHT			= 32*HEIGHT_IN_TILES;

	private static final int TARGET_HEIGHT		= 32/2;
	private static final float MAX_TARGET_Y		= HEIGHT - TARGET_HEIGHT - 32/2;

	private SpriteSheet ss;
	private float targetY;
	private float targetDelta;


	public Goal(Area area, float x, float y) {
		this.area = area;
		y = y - getHeight() + 32;
		setLocation(x, y);
		try {
			Image img = new Image("img/goals.png", false, Image.FILTER_NEAREST,
									new Color(192,192,192));
			ss = new SpriteSheet(img, 32,32, 2);
		} catch (SlickException se) {
			se.printStackTrace();
			System.exit(0);
		}
		targetY = 0;
		targetDelta = 1;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		return 24;
	}


	@Override
	public float getHitMarginX() {
		return 24;
	}


	@Override
	public float getWidth() {
		return 32*3;
	}


	/**
	 * Renders the entire goal.  Note that this is not usually called from
	 * the game because the "left" post is "behind" the player, and the
	 * "right" post is "in front of" the player, to give an illusion of Mario
	 * going "through" the goal.
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
							Graphics g, Color filter) throws SlickException {
		renderLeft(container, game, g, filter);
		renderRight(container, game, g, filter);
	}


	public void renderLeft(GameContainer container, StateBasedGame game,
						Graphics g, Color filter) throws SlickException {

		float x = getX() - area.xOffs;
		float y = getY() - area.yOffs;

		if (filter==null) {
			filter = Color.white;
		}

		Image topLeft = ss.getSubImage(0, 0);
		g.drawImage(topLeft, x, y, filter);

		for (int i=1; i<HEIGHT_IN_TILES; i++) {
			y += 32;
			Image temp = ss.getSubImage(0, 1);
			g.drawImage(temp, x,y, filter);
		}

	}


	public void renderRight(GameContainer container, StateBasedGame game,
							Graphics g, Color filter) throws SlickException {

		float x = getX() - area.xOffs;
		float y = getY() - area.yOffs;

		if (filter==null) {
			filter = Color.white;
		}

		Image img = ss.getSubImage(1, 0);
		g.drawImage(img, x+32*2, y, filter);

		img = ss.getSubImage(1, 1);
		for (int i=1; i<HEIGHT_IN_TILES; i++) {
			y += 32;
			g.drawImage(img, x+32*2, y, filter);
		}

	}


	public void renderTarget(GameContainer container, StateBasedGame game,
							Graphics g, Color filter) throws SlickException {
		float x = getX() - area.xOffs;
		float y = getY() - area.yOffs;
		if (filter==null) {
			filter = Color.white;
		}
		Image img = ss.getSubImage(68,0, 48,TARGET_HEIGHT);
		g.drawImage(img, x+32/2, y+targetY+32 - 32/2, filter);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		targetY += targetDelta;
		if (targetY<=0) {
			targetY = 0;
			targetDelta = -targetDelta;
		}
		else if (targetY>MAX_TARGET_Y) {
			targetY = MAX_TARGET_Y;
			targetDelta = -targetDelta;
		}
	}


}
