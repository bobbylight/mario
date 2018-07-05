package org.fife.mario.blocks;

import org.fife.mario.level.Area;


/**
 * A bouncy music block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
/*
 * TODO: Change from an immovable block to actually have proper behavior.
 */
public class MusicBlock extends ImmovableBlock {


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 */
	public MusicBlock(Area area) {
		super(area, BlockTypes.BLOCK_MUSIC_NOTE);
	}


}
