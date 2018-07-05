package org.fife.mario.blocks;

import org.fife.mario.level.Area;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A '?' block that contains an item.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class QuestionBlock extends LoadedBlock {

	/**
	 * The '?' block animation, shared amongst all instances.
	 */
	private static Animation yellowAnim;

	/**
	 * The red '?' block animation, shared amongst all instances.
	 */
	private static Animation redAnim;

	/**
	 * The animation for this particular block (either {@link #yellowAnim} or
	 * {@link #redAnim}.
	 */
	private Animation anim;


	/**
	 * Creates a new question block.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this question block.
	 */
	public QuestionBlock(Area area, Content content) {
		this(area, content, BlockTypes.BLOCK_QUESTION, false);
	}


	/**
	 * Creates a new question block.
	 *
	 * @param area The area that this block is in.
	 * @param content The content of this question block.
	 * @param type The type of block.  This should be either
	 *        {@link BlockTypes#BLOCK_QUESTION} or
	 *        {@link BlockTypes#BLOCK_QUESTION_RED}.
	 * @param hidden Whether this block is hidden.
	 */
	public QuestionBlock(Area area, Content content, BlockTypes type,
						boolean hidden) {
		super(area, content, hidden);
		setType(type);
	}


	@Override
	public BlockTypes getType() {
		return anim==yellowAnim ? BlockTypes.BLOCK_QUESTION :
									BlockTypes.BLOCK_QUESTION_RED;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
							Graphics g, float x, float y, Color filter) {
		anim.draw(x, y, filter);
	}


	/**
	 * Sets the type of this question block.
	 *
	 * @param type the The type of the block.  This must be either {@link BlockTypes#BLOCK_QUESTION} or
	 *        {@link BlockTypes#BLOCK_QUESTION_RED}.
	 */
	private void setType(BlockTypes type) {
		if (type==BlockTypes.BLOCK_QUESTION) {
			anim = yellowAnim;
		}
		else if (type==BlockTypes.BLOCK_QUESTION_RED) {
			anim = redAnim;
		}
		else {
			throw new IllegalArgumentException("Invalid block type: " + type);
		}
	}


	/**
	 * Updates the {@code "?"} block animation.  This is a shared resource amongst all {@code "?"} blocks.
	 *
	 * @param delta The length of time that has passed since the last update.
	 */
	static void updateAnimation(int delta) {
		yellowAnim.update(delta);
		redAnim.update(delta);
	}


	static {
		yellowAnim = createAnimation(BlockTypes.BLOCK_QUESTION,  200, false);
		redAnim = createAnimation(BlockTypes.BLOCK_QUESTION_RED, 200, false);
	}


}
