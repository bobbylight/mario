package org.fife.mario.blocks;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A block that displays a text message when hit.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TextBlock extends LoadedBlock {


	/**
	 * Constructor.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this loaded block.
	 */
	public TextBlock(Area area, Content content) {
		super(area, content);
	}


	@Override
	public BlockTypes getType() {
		return BlockTypes.BLOCK_INFORMATION;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, float x, float y, Color filter) throws SlickException {
		Image img = getStaticBlockImage(BlockTypes.BLOCK_INFORMATION);
		g.drawImage(img, x, y);
	}


}
