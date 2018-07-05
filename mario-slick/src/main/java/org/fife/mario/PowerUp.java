package org.fife.mario;

/**
 * The power-ups Mario can get.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public enum PowerUp {

	MUSHROOM(0),
	FIRE_FLOWER(1),
	FEATHER(2),
	ONE_UP(3),
	STAR(6);

	private int index;

	PowerUp(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
