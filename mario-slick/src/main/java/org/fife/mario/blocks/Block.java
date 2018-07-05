package org.fife.mario.blocks;

import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import org.fife.mario.AbstractEntity;
import org.fife.mario.Character;
import org.fife.mario.Mario;
import org.fife.mario.level.Area;


/**
 * Base class for blocks ('?' blocks, bricks, etc.).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class Block extends AbstractEntity {

	protected static final int MAX_BOUNCE_FRAME	= 12;

	private int row;
	private int col;

	private static SpriteSheet ss;


	public Block(Area area) {
		this.area = area;
	}


	/**
	 * Activates this block, if it can be activated.
	 *
	 * @param mario The character that activated the block.
	 */
	public void activate(Mario mario) {
	}


	protected void checkBump(Mario mario) {
		getHitBounds();
		int row = getRow();
		int col = getCol();
		List<Character> chars = area.getOtherCharacters();
		for (int i=0; i<chars.size(); i++) {
			Character ch = chars.get(i);
			if (area.isInUpdateBounds(ch) && ch.isOnTopOf(row, col)) {
				if (ch.bump(mario)) {
					chars.remove(i);
					i--;
				}
			}
		}
	}


	public static Animation createAnimation(BlockTypes type, int duration,
											boolean autoUpdate) {
		Animation anim = new Animation(autoUpdate);
		for (int i=0; i<4; i++) {
			anim.addFrame(ss.getSubImage(i, type.getTypeIndex()), duration);
		}
		return anim;
	}


	public int getCol() {
		return col;
	}


	@Override
	public float getHeight() {
		return 32;
	}


	@Override
	public float getHitMarginTop() {
		return 0;
	}


	@Override
	public float getHitMarginX() {
		return 0;
	}


	public int getRow() {
		return row;
	}


	/**
	 * Returns a static block image, such as
	 * {@link BlockTypes#BLOCK_SOLID_BROWN}.
	 *
	 * @param type The type of block whose image to retrieve.
	 * @return The image.
	 */
	public static Image getStaticBlockImage(BlockTypes type) {
		// The first 7 block types are animated, everything afterwards is
		// just an image
		int img = type.getTypeIndex();
		if (img<8) {
			return ss.getSubImage(0, img);
		}
		return ss.getSubImage(img-8, 8);
	}


	public abstract BlockTypes getType();


	@Override
	public float getWidth() {
		return 32;
	}


	@Override
	public float getX() {
		return col*32; // "x" field is ignored.
	}


	@Override
	public float getY() {
		return row*32; // "y" field is ignored.
	}


	public abstract boolean isActivatable();


	/**
	 * Returns whether this block is hidden.  This will always be
	 * <code>false</code> for the default implementation.  Subclasses that
	 * can be hidden can override.
	 *
	 * @return Whether this block is hidden.
	 */
	public boolean isHidden() {
		return false;
	}


	public boolean isHittable() {
		return true;
	}


	public boolean isAt(int row, int col) {
		return this.row==row && this.col==col;
	}


	/**
	 * Renders a single frame of a block.
	 *
	 * @param g The graphics context.
	 * @param x The x-coordinate on screen.
	 * @param y The y-coordinate on screen.
	 * @param row The row in the sprite sheet of the image.
	 * @param col The column in the sprite sheet of the image.
	 */
	protected void renderImage(Graphics g, float x, float y, int row, int col,
								Color filter) {
		g.drawImage(ss.getSubImage(col, row), x, y, filter);
	}


	public void setCol(int col) {
		this.col = col;
	}


	public void setRow(int row) {
		this.row = row;
	}


	public void setRowAndColumn(int row, int col) {
		setRow(row);
		setCol(col);
	}


	public static void updateAnimations(int delta) {
		QuestionBlock.updateAnimation(delta);
	}


	static {
		try {
			Image img = new Image("img/blocks.png", new Color(192,192,192));
			ss = new SpriteSheet(img, 32,32, 2);
		} catch (SlickException se) {
			throw new InternalError(se.toString());
		}
	}

}
