package org.fife.mario.blocks;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A block that displays a single image and cannot be bumped or broken.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ImmovableBlock extends Block {

	/**
	 * The index for the static image we render.
	 */
	private BlockTypes type;


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param type The type of block this is.
	 */
	public ImmovableBlock(Area area, BlockTypes type) {
		super(area);
		this.type = type;
	}


	@Override
	public BlockTypes getType() {
		return type;
	}


	/**
	 * This method always returns <code>false</code>.
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean isActivatable() {
		return false;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
						Graphics g, Color filter) throws SlickException {
		float x = getX()-area.xOffs;
		float y = getY()-area.yOffs;
		getStaticBlockImage(type).draw(x, y, filter);
	}

	/**
	 * This method does nothing, since immovable blocks never change.
	 */
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
	}


}
