package org.fife.mario.blocks;

public enum BlockTypes {

	BLOCK_MUSIC_NOTE(0),
	BLOCK_YELLOW(1),
	BLOCK_BLUE(2),
	BLOCK_GRAY(3),
	BLOCK_QUESTION(4),
	BLOCK_QUESTION_RED(5),
	BLOCK_YELLOW_COIN(6),
	BLOCK_BLUE_COIN(7),

	BLOCK_SOLID_BROWN(8),
	BLOCK_INFORMATION(9),
	BLOCK_ROCK(10),
	BLOCK_ICE(11);

	private int type;

	private static final String[] NAMES = {
		"music_note",
		"clay",
		"blue",
		"gray",
		"question",
		"question_red",
		"coin_yellow",
		"coin_blue",
		"brown",
		"info",
		"rock",
		"ice",
	};


	BlockTypes(int type) {
		this.type = type;
	}


	public boolean getCanHaveContent() {
		return this==BlockTypes.BLOCK_BLUE ||
				this==BlockTypes.BLOCK_GRAY ||
				this==BlockTypes.BLOCK_MUSIC_NOTE ||
				this==BlockTypes.BLOCK_QUESTION ||
				this==BlockTypes.BLOCK_QUESTION_RED ||
				this==BlockTypes.BLOCK_YELLOW;
	}


	public String getName() {
		return NAMES[type];
	}


	public int getTypeIndex() {
		return type;
	}


}
