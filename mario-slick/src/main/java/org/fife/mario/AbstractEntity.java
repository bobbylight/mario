package org.fife.mario;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * Base class for stuff in the world that Mario can interact with.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractEntity {

	protected Rectangle2D.Float bounds;
	protected Area area;
	private float x;
	private float y;
	private boolean active;
	private boolean done;
	private Vector2f tempVec;

	/**
	 * A list of entities registered as standing "on top of" this entity.
	 */
	private List<AbstractEntity> stuffOnTop;

	/**
	 * The entity that this entity is registered as standing "on top of".
	 */
	private AbstractEntity standingOn;

	protected static final Color BOUNDS_COLOR = new Color(255,224,224, 128);
	protected static final Color HIT_BOUNDS_COLOR = new Color(0, 0, 255, 128);


	public AbstractEntity() {
		bounds = new Rectangle2D.Float();
		tempVec = new Vector2f();
		stuffOnTop = new ArrayList<>(1); // Usually very small
		active = true;
	}


	/**
	 * Adds an entity to the "top" of this one.  If the entity was previously
	 * on another entity, it is removed from the top of that entity.  This
	 * method updates both <code>entity</code> and this entity.
	 *
	 * @param entity The entity to add.
	 * @see #removeEntityStandingOn(AbstractEntity)
	 */
	public void addEntityStandingOn(AbstractEntity entity) {

		if (entity.standingOn!=null) { // Shouldn't ever be true?
			entity.standingOn.removeEntityStandingOn(entity);
		}

		stuffOnTop.add(entity);
		entity.standingOn = this;

	}


	/**
	 * Removes all entities "on top of" this one.
	 *
	 * @see #resetEntitiesStandingOnInfo()
	 */
	public void clearEntitiesStandingOn() {
        for (AbstractEntity aStuffOnTop : stuffOnTop) {
            aStuffOnTop.standingOn = null;
        }
		stuffOnTop.clear();
	}


	public boolean contains(Point p) {
		return getHitBounds().contains(p);
	}


	public Vector2f getBottomCenter(Vector2f p) {
		p.set(x+getWidth()/2, y+getHeight()-1);
		return p;
	}


	public Vector2f getBottomLeft(Vector2f p) {
		p.set(x+getHitMarginX(), y+getHeight()-1);
		return p;
	}


	public Vector2f getBottomRight(Vector2f p) {
		p.set(x+getWidth()-getHitMarginX()-1, y+getHeight()-1);
		return p;
	}



	public float getCenterX() {
		return getX() + getWidth()/2;
	}


	public float getCenterY() {
		return getY() + getHeight()/2;
	}


	/**
	 * Returns the "core" bounds of this entity.  This is the area that is
	 * taken into account when determining if an entity should be updated in
	 * any given frame.<p>
	 *
	 * The default implementation returns {@link #getFullBounds()}.  Subclasses
	 * can override as appropriate.
	 *
	 * @return The core bounds of this entity.
	 */
	public Rectangle2D.Float getCoreBounds() {
		return getFullBounds();
	}


	/**
	 * Returns the "full" bounds of this entity.
	 *
	 * @return The full bounding box of this entity.
	 * @see #getHitBounds()
	 */
	public Rectangle2D.Float getFullBounds() {
		bounds.x = getX();
		bounds.y = getY();
		bounds.width = getWidth();
		bounds.height = getHeight();
		return bounds;
	}


	/**
	 * Returns the bounding rectangle to use when checking for collisions with
	 * other entities.  Note that this can be smaller than the entity's sprite
	 * sprite bounds.
	 *
	 * @return The hit box for this entity.
	 */
	public Rectangle2D.Float getHitBounds() {
		// A little smaller for "overlap."
		bounds.x = getX() + getHitMarginX();
		bounds.y = getY() + getHitMarginTop();
		bounds.width = getHitBoundsWidth();
		bounds.height = getHeight() - getHitMarginTop();
		return bounds;
	}


	public abstract float getHeight();


	public float getHitBoundsHeight() {
		return getHeight() - getHitMarginTop();
	}


	public float getHitBoundsWidth() {
		return getWidth() - 2*getHitMarginX();
	}


	/**
	 * Returns the margin at the top of the image before the top of the hit
	 * box.
	 *
	 * @return The margin top y-bounds.
	 * @see #getHitMarginX()
	 */
	public abstract float getHitMarginTop();


	/**
	 * Returns the margin on the left and right sides between the edge of the
	 * sprite and the edge of the hit box.
	 *
	 * @return The margin x-bounds.
	 * @see #getHitMarginTop()
	 */
	public abstract float getHitMarginX();


	/**
	 * Returns the entity that this entity is standing on.
	 *
	 * @return The entity, or <code>null</code> for none.
	 */
	public AbstractEntity getStandingOn() {
		return standingOn;
	}


	public Vector2f getTopLeft(Vector2f p) {
		p.set(x+getHitMarginX(), y+getHitMarginTop());
		return p;
	}


	public Vector2f getTopRight(Vector2f p) {
		p.set(x+getWidth()-getHitMarginX()-1, y+getHitMarginTop());
		return p;
	}


	public abstract float getWidth();


	/**
	 * Returns the x-coordinate of this entity, in world coordiantes.
	 *
	 * @return The x-coordinate.
	 * @see #getY()
	 */
	public float getX() {
		return x;
	}


	/**
	 * Returns the y-coordinate of this entity, in world coordinates.
	 *
	 * @return The y-coordinate.
	 * @see #getX()
	 */
	public float getY() {
		return y;
	}


	/**
	 * Returns whether this entity's hit bounds collides with another's.
	 *
	 * @param e2 The other entity.  This cannot be <code>null</code>.
	 * @return Whether the two entities intersect.
	 */
	public boolean intersects(AbstractEntity e2) {
		getHitBounds();
		Rectangle2D.Float b2 = e2.getHitBounds();
		return bounds.intersects(b2);
	}


	/**
	 * Returns whether this entity is active.  If an entity is not active, it
	 * is neither updated nor rendered.
	 *
	 * @return Whether this entity is active.
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * Returns whether this entity is done (i.e., "dead," and can be removed).
	 *
	 * @return Whether this entity is done.
	 */
	public boolean isDone() {
		return done;
	}


	/**
	 * Returns whether this entity can be kicked by Mario.  The default
	 * implementation always returns <code>false</code>.
	 *
	 * @return Whether this entity is kickable.
	 * @see #kickedBy(Character)
	 */
	public boolean isKickable() {
		return false;
	}


	/**
	 * Returns whether other entities can land on this one.  The default value
	 * is <code>false</code>; subclasses can override if they can be landed
	 * on.
	 *
	 * @return Whether other entities can land on this one.
	 */
	public boolean isLandable() {
		return false;
	}


	/**
	 * Returns whether this entity is on top of another entity.  This
	 * decision is made when the method is called, and is thus different
	 * than <code>{@link #getStandingOn()}==other</code>.  This method could
	 * be used to determine whether one entity should be on top of another.
	 *
	 * @param other the other entity.
	 * @return Whether this entity is on top of the other one.
	 */
	public boolean isOnTopOf(AbstractEntity other) {

		Rectangle2D.Float otherBounds = other.getHitBounds();

		getBottomLeft(tempVec);
		tempVec.y += 1; // Since we're "just on top of" it
		boolean onTopOf = otherBounds.contains(tempVec.x, tempVec.y);
		if (!onTopOf) {
			getBottomRight(tempVec);
			tempVec.y += 1; // Since we're "just on top of" it
			onTopOf = otherBounds.contains(tempVec.x, tempVec.y);
		}

		return onTopOf;

	}


	public boolean isOnTopOf(int row, int col) {

		getBottomLeft(tempVec);
		tempVec.y++;
		int testRow = area.viewToRow(tempVec.y);
		int testCol = area.viewToCol(tempVec.x);
		if (testRow==row && testCol==col) {
			return true;
		}

		getBottomRight(tempVec);
		tempVec.y++;
		testRow = area.viewToRow(tempVec.y);
		testCol = area.viewToCol(tempVec.x);
		if (testRow==row && testCol==col) {
			return true;
		}

		return false;

	}


	/**
	 * Kicks this entity.  This is only called if {@link #isKickable()}
	 * returns <code>true</code>.<p>
	 *
	 * The default implementation does nothing (to coincide with the default
	 * implementation of {@link #isKickable()} returning <code>false</code>).
	 *
	 * @param ch The character (usually Mario, but can be other things such
	 *        as a {@link Shell}) doing the kicking.
	 * @return Whether this entity should be removed from the world (e.g.
	 *         an enemy that has died).
	 * @see #isKickable()
	 */
	public boolean kickedBy(Character ch) {
		return false;
	}


	/**
	 * Moves all entities resting on top of this one.
	 *
	 * @param dx The change in the x coordinate.
	 * @param dy The change in the y coordinate.
	 */
	protected void moveStuffOnTop(float dx, float dy) {
		for (AbstractEntity entity : stuffOnTop) {
			entity.translate(dx, dy);
		}
	}


	public void moveX(float delta) {
		setX(x+delta);
	}


	public void moveY(float delta) {
		setY(y+delta);
	}


	public final void render(GameContainer container, StateBasedGame game,
							Graphics g, Color filter) throws SlickException {
		if (isActive()) {
			renderImpl(container, game, g, filter);
		}
	}


	/**
	 * @param entity The entity to remove.
	 * @see #addEntityStandingOn(AbstractEntity)
	 */
	public void removeEntityStandingOn(AbstractEntity entity) {
		stuffOnTop.remove(entity);
	}


	protected abstract void renderImpl(GameContainer container,
		StateBasedGame game, Graphics g, Color filter) throws SlickException;


	/**
	 * Removes this entity from being "on top of" another one.  Both this and
	 * the other entity are updated.
	 *
	 * @see #clearEntitiesStandingOn()
	 */
	protected void resetEntitiesStandingOnInfo() {
		if (standingOn!=null) {
			standingOn.removeEntityStandingOn(this);
			standingOn = null;
		}
	}


	public void setArea(Area area) {
		this.area = area;
	}


	/**
	 * Sets whether this entity is active.
	 *
	 * @param active Whether this entity is active.
	 * @see #isActive()
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * Sets whether this entity is done (i.e., "dead," and can be removed).
	 *
	 * @param done Whether the entity is done.
	 * @see #isDone()
	 */
	protected void setDone(boolean done) {
		this.done = done;
		clearEntitiesStandingOn();
	}


	public void setLocation(float x, float y) {
		setX(x);
		setY(y);
	}


	/**
	 * Moves this entity to a new location.  If the new location is outside
	 * of the current {@link Area} complely, {@link #setDone(boolean)} is
	 * called.<p>
	 *
	 * All horizontal motion should boil down to a call to this method.
	 *
	 * @param x The new x-coordinate.
	 * @see #setY(float)
	 */
	public void setX(float x) {
		if (x!=this.x) {

			float diff = x - this.x;
			this.x = x;
			if (x<=-getWidth()) {
				setDone(true);
				clearEntitiesStandingOn();
				return;
			}

			moveStuffOnTop(diff, 0);

			AbstractEntity beneath = getStandingOn();
			if (beneath!=null) {
				if (!isOnTopOf(beneath)) {
					beneath.removeEntityStandingOn(this);
					this.standingOn = null;
				}
			}

		}
	}


	/**
	 * Moves this entity to a new location.<p>
	 *
	 * All vertical motion should boil down to a call into this method.
	 *
	 * @param y The new y-coordinate.
	 * @see #setX(float)
	 */
	public void setY(float y) {
		if (y!=this.y) {
			float diff = y - this.y;
			this.y = y;
			moveStuffOnTop(0, diff);
		}
	}


	public void translate(float x, float y) {
		setX(this.x+x);
		setY(this.y+y);
	}


	/**
	 * Called each frame, so the entity can update itself.
	 *
	 * @param container
	 * @param game
	 * @param delta
	 * @throws SlickException
	 */
	public void update(GameContainer container, StateBasedGame game, int delta)
						throws SlickException {
		if (isActive()) {

			updateImpl(container, game, delta);

			// Automatically remove if the entity fell down a bottomless pit
			Area area = GameInfo.get().getLevel().getCurrentArea();
			if (getY()>=area.yOffs + container.getHeight()) {
				setDone(true);
			}

		}
	}


	/**
	 * Called each frame, so the entity can update itself.
	 *
	 * @param container
	 * @param game
	 * @param delta The time change.
	 * @throws SlickException
	 */
	protected abstract void updateImpl(GameContainer container,
			StateBasedGame game, int delta) throws SlickException;


}
