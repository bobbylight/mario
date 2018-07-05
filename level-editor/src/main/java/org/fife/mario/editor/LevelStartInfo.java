package org.fife.mario.editor;

import java.util.Arrays;

import org.fife.mario.Position;


/**
 * Information about how Mario should enter a level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LevelStartInfo {

	public static final String[] LEVEL_START_ANIMS = {
		"None",
		"WarpingAnimation",
	};

	private String anim;
	private Position pos;

	public LevelStartInfo(String anim, Position pos) {
		setAnimation(anim);
		setPosition(pos);
	}

	public String getAnimation() {
		return anim;
	}

	public Position getPosition() {
		return pos;
	}

	public void setAnimation(String anim) {
		if (Arrays.binarySearch(LEVEL_START_ANIMS, anim)>-1) {
			this.anim = anim;
		}
	}

	public void setPosition(Position pos) {
		this.pos = pos;
	}

	/**
	 * Returns this info how it should look in a level file.
	 *
	 * @return A string representation of this level start info.
	 */
	@Override
	public String toString() {
		return getAnimation() + "," + pos.getRow() + "," + pos.getCol();
	}

}
