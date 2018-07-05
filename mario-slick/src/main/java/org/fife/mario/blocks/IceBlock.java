package org.fife.mario.blocks;

import org.fife.mario.level.Area;


/**
 * A slippery ice block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
/*
 * TODO: Change from an ImmovableBlock to a custom one with proper behavior.
 */
public class IceBlock extends ImmovableBlock {


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 */
	public IceBlock(Area area) {
		super(area, BlockTypes.BLOCK_ICE);
	}


}
