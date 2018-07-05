package org.fife.mario;

import java.util.List;


/**
 * An animation to render.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class Animation extends AbstractEntity {


	public Animation(float x, float y) {
		setLocation(x, y);
		//setLocation(x, y-getHeight());
	}


	/**
	 * Called when this <code>Animation</code> is being disposed of (i.e.,
	 * after the parent <code>Area</code> sees that {@link #isDone()} returns
	 * <code>true</code>).  This gives this animation a chance to perform any
	 * arbitrary actions it wants.
	 *
	 * @return A character to replace this animation with, or <code>null</code>
	 *         for none.
	 */
	public Character dispose(Mario mario) {
		return null;
	}


	/**
	 * Returns <code>0</code>, since animations don't collide with anything.
	 *
	 * @return The margin top y-bounds.
	 * @see #getHitMarginX()
	 */
	@Override
	public float getHitMarginTop() {
		return 0;
	}


	/**
	 * Returns <code>0</code>, since animations don't collide with anything.
	 *
	 * @return The margin x-bounds.
	 * @see #getHitMarginTop()
	 */
	@Override
	public float getHitMarginX() {
		return 0;
	}


	/**
	 * Returns a list of animations to replace this one with.  This is called
	 * right after {@link #isDone()} returns <code>true</code>.  The default
	 * implementation returns <code>null</code>.
	 *
	 * @return Any animations to replace this one with when this one is done.
	 */
	public List<Animation> getReplacementAnimations() {
		return null;
	}


}
