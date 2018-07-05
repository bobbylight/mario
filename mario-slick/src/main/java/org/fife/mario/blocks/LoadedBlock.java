package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A block that contains an item (a '?' block, a hidden block, etc.).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class LoadedBlock extends Block {

	private Content content;
	private int sound;
	private boolean hidden;

	/**
	 * When hit from below, this is the frame of the "bounce" we are on.
	 */
	private int bounceFrame;


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this loaded block.
	 */
	public LoadedBlock(Area area, Content content) {
		this(area, content, false);
	}


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this loaded block.
	 * @param hidden Whether this block is hidden.
	 */
	public LoadedBlock(Area area, Content content, boolean hidden) {
		super(area);
		setContent(content);
		sound = -1;
		this.hidden = hidden;
	}


	@Override
	public void activate(Mario mario) {

		if (content!=null) {
			hidden = false;
			bounceFrame = 1;
			boolean goRight = mario.getCenterX()<=getCenterX();
			sound = content.makeAvailable(this, mario, area, goRight);
			if (!content.hasMoreContent()) {
				content = null;
			}
		}

		checkBump(mario);
	}


	/**
	 * Returns the content of this block, or <code>null</code> if there
	 * is none (e.g. it has already been knocked out).
	 *
	 * @return The content.
	 * @see #setContent(Content)
	 */
	public Content getContent() {
		return content;
	}


	/**
	 * Returns whether this block is hidden (until it is activated).
	 *
	 * @return Whether this block is hidden.
	 */
	@Override
	public boolean isHidden() {
		return hidden;
	}


	@Override
	public boolean isActivatable() {
		return bounceFrame==0 && content!=null;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
						Graphics g, Color filter) throws SlickException {

		if (hidden) {
			return;
		}

		int yBounce = 0;
		if (bounceFrame>0) {
			if (bounceFrame<8) {
				yBounce = -1*bounceFrame;
			}
			else {
				yBounce = -8 + 2*(bounceFrame-8);
			}
		}

		float x = getX()-area.xOffs;
		float y = getY()+yBounce-area.yOffs;
		if (getContent()==null) {
			getStaticBlockImage(BlockTypes.BLOCK_SOLID_BROWN).draw(x, y, filter);
		}
		else {
			renderImpl(container, game, g, x, y, filter);
		}

	}


	/**
	 * Renders this block at the specified coordinates.  The block is
	 * guaranteed to have content and not be hidden.
	 *
	 * @param container The game container.
	 * @param game The game.
	 * @param g The graphics context.
	 * @param x The x-offset at which to paint.
	 * @param y The y-offset at which to paint.
	 * @throws SlickException
	 */
	protected abstract void renderImpl(GameContainer container,
			StateBasedGame game, Graphics g, float x, float y, Color filter)
								throws SlickException;


	/**
	 * Sets the content of this block.
	 *
	 * @param content The new content.
	 * @see #getContent()
	 */
	public void setContent(Content content) {
		this.content = content;
	}


	/**
	 * Updates this loaded block.  Subclasses that override this method
	 * should call the super implementation.
	 */
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (bounceFrame>0 && bounceFrame<MAX_BOUNCE_FRAME) {
			bounceFrame++;
			if (sound>-1) {
				SoundEngine.get().play(sound);
				sound = -1;
			}
		}
		else {
			bounceFrame = 0;
		}
	}


	/**
	 * Content in a loaded block.
	 *
	 * @author Robert Futrell
	 * @version 1.0
	 */
	public interface Content {

		boolean hasMoreContent();

		/**
		 * Makes content available (e.g. releases a coin, mushroom, etc.).
		 * This usually entails starting an animation, returning a sound, and
		 * possibly updating the player's score.
		 *
		 * @param fromBlock
		 * @param mario The character that hit the block.
		 * @param area
		 * @param goRight Whether the content should go right or left (if it
		 *        moves in either direction).
		 * @return The sound to play, or <code>-1</code> if none.
		 */
		int makeAvailable(Block fromBlock, Mario mario, Area area, boolean goRight);

	}


}
