package org.fife.mario.blocks;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A clay block that actually contains an item.  These blocks can also be
 * "hidden" (that is, not rendered until they are hit).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LoadedClayBlock extends LoadedBlock {

	private BlockTypes type;


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this block.
	 */
	public LoadedClayBlock(Area area, Content content) {
		super(area, content);
	}


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this block.
	 * @param type The type of this block.
	 */
	public LoadedClayBlock(Area area, Content content, BlockTypes type) {
		super(area, content);
		setType(type);
	}


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this block.
	 * @param type The type of the block.
	 * @param hidden Whether this block is hidden.
	 */
	public LoadedClayBlock(Area area, Content content, BlockTypes type,
							boolean hidden) {
		super(area, content, hidden);
		setType(type);
	}


	@Override
	public BlockTypes getType() {
		return type;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, float x, float y, Color filter) throws SlickException {
		renderImage(g, x,y, type.getTypeIndex(),0, filter);
	}


	private void setType(BlockTypes type) {
		if (type!=BlockTypes.BLOCK_YELLOW &&
				type!=BlockTypes.BLOCK_BLUE &&
				type!=BlockTypes.BLOCK_GRAY) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		this.type = type;
	}


}
