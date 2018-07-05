package org.fife.mario;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * Base class for Mario and enemies.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class Character extends AbstractEntity {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	private SpriteSheet ss;
	private Position ssIndex;

	protected int dir; // 0==left, 1==right, matches sprite sheets
	protected Vector2f motion;
	protected boolean airborne;
	private boolean enabled;

	private static boolean renderHitBoxes;


	/**
	 * Constructor.
	 */
	public Character() {
		init();
	}


	/**
	 * Constructor.
	 *
	 * @param x The x-location of the character, as specified in the level
	 *        editor.The actual y-coordinate of the character will be
	 *        adjusted appropriately.
	 * @param y The y-location of the character, as specified in the level
	 *        editor.  The actual y-coordinate of the character will be
	 *        adjusted appropriately.
	 */
	public Character(float x, float y) {
		init();
		x -= getHitMarginX();
		y = y - getHeight() + getHitMarginTop();
		setLocation(x, y);
	}


	/**
	 * Called when this characters gets "bumped" from underneath (e.g. when
	 * Mario hits a block this character is standing on).
	 *
	 * @param ch The character that bumped this character.
	 * @return Whether this character dies because of the bump.
	 */
	public abstract boolean bump(Character ch);


	/**
	 * Called when this object and Mario touch.
	 *
	 * @param mario The mario touched.
	 * @return Whether this character should be removed.
	 */
	public abstract boolean collidedWithMario(Mario mario);


	/**
	 * Called when this object and another touch.
	 *
	 * @param ch The other character touched.
	 * @return Whether this character should be removed.
	 */
	public abstract boolean collidedWith(Character ch);


	public int getDirection() {
		return dir;
	}


	protected Image getImage(int col, int row) {
		return ss.getSubImage(col, row);
	}


	protected abstract int getSpriteSheet();


	protected int getSSCol() {
		return ssIndex.getCol();
	}


	protected int getSSRow() {
		return ssIndex.getRow();
	}


	private void init() {
		motion = new Vector2f();
		airborne = false;
		ss = SpriteSheetManager.instance().getSheet(getSpriteSheet());
		ssIndex = new Position();
		setEnabled(true);
	}


	/**
	 * Returns whether this character is airborne (jumping or falling).
	 *
	 * @return Whether this character is airborne.
	 */
	public boolean isAirborne() {
		return airborne;
	}


	/**
	 * Returns whether this character has been enabled (e.g. should be
	 * updated each frame).
	 *
	 * @return Whether this character is enabled.
	 * @see #setEnabled(boolean)
	 */
	public boolean isEnabled() {
		return enabled;
	}


	/**
	 * Returns whether this entity is in motion.
	 *
	 * @return Whether this entity is in motion.
	 */
	public abstract boolean isMoving();


	/**
	 * Returns whether this entity is solid.  Solid entities can be hit by
	 * fireballs, for example.<p>
	 * The default implementation returns <code>true</code>.
	 *
	 * @return  Whether this entity is solid.
	 */
	public boolean isSolid() {
		return true;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
					Graphics g, Color filter) throws SlickException {

		// Rounding to ints prevents occasional sprite issues (rounding errors?)
		int x = (int)(getX() - area.xOffs);
		int y = (int)(getY() - area.yOffs);
		g.translate(x, y);

		if (renderHitBoxes) {
			g.setColor(BOUNDS_COLOR);
			g.fillRect(0,0, getWidth(),getHeight());
			getHitBounds();
			g.setColor(HIT_BOUNDS_COLOR);
			g.fillRect(getHitMarginX(),getHitMarginTop(), bounds.width, bounds.height);
		}

		Image image = getImage(ssIndex.getCol(), ssIndex.getRow());
		renderImage(image, container, game, g, filter);

		g.resetTransform();

	}


	protected void renderImage(Image image, GameContainer container,
		StateBasedGame game, Graphics g, Color filter) throws SlickException {
		if (filter==null) {
			filter = Color.white;
		}
		g.drawImage(image, 0,0, filter);
	}


	/**
	 * Called when Mario leaves the area this <code>Character</code> is in,
	 * such as via a pipe.  This gives it a chance to reset its internal state
	 * to be good/different for when Mario comes back into this area.  This can
	 * be used by Piranha plants, for example, to go back into their pipes, to
	 * ensure Mario doesn't come out of one directly into a plant.<p>
	 *
	 * The default implementation does nothing.
	 */
	public void reset() {
	}


	/**
	 * Sets whether this character is "enabled;" that is, whether it should
	 * be updated each frame.
	 *
	 * @param enabled Whether this character should be activated.
	 * @see #isEnabled()
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	protected void setSSIndex(int row, int col) {
		ssIndex.set(row, col);
	}


	/**
	 * Toggles whether bounding boxes and hit boxes for characters should be
	 * rendered.
	 */
	public static void toggleRenderHitBoxes() {
		renderHitBoxes = !renderHitBoxes;
	}


}
