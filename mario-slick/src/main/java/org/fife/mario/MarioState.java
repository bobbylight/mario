package org.fife.mario;

public enum MarioState {

	SMALL(null, 1),
	BIG(SMALL, 0),
	FIRE(BIG, 2),
	CAPE(BIG, 3);

	/**
	 * The state Mario goes to when he is hit in the current state.
	 */
	private MarioState next;

	/**
	 * The index into Mario's sprite sheet for this state.
	 */
	private int index;

	MarioState(MarioState next, int index) {
		this.next = next;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public MarioState getNext() {
		return next;
	}

}
