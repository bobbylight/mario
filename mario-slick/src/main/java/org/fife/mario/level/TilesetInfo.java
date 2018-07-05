package org.fife.mario.level;


/**
 * Information on the various possible tilesets.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class TilesetInfo {

	public static final int EMPTY				= 0;
	public static final int SOLID				= 1;
	public static final int LANDABLE			= 2;
	public static final int DEADLY				= 3;

	private int[] info;

	private static final String TILESET_CASTLE_GRAY			= "castle_gray";
	private static final String TILESET_GROUND_BROWN		= "ground_brown";
	private static final String TILESET_GROUND_BLUE			= "ground_blue";
	private static final String TILESET_UNDERGROUND_BROWN	= "underground_brown";
	private static final String TILESET_UNDERWATER_GREEN	= "underwater_green";

	private static final int[] INFO_GROUND = {
		1, 2, 1,	2, 2,	0, 1, 1, 0,    0, 0, 0, 1, 1, 0,
		1, 0, 1,	0, 0,	1, 1, 1, 1,    0, 0, 0, 1, 0, 1,
		1, 1, 1,	1, 1,	1, 0, 0, 1,    0, 0, 3, 3, 3, 3,

		0, 0, 0,	1, 1,	1, 1, 1, 1, 1, 1, 0, 0, 3, 0,
		0, 0, 1,	1, 1,	1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
		0, 1, 1,	1, 1,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 1,	1, 1,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0,	1, 1,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,

		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,

		0, 0, 0,	0, 0,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0,	0, 0,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0,	0, 0,	0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	};


	private static final int[] INFO_CASTLE = {
		1, 2, 1,	2, 2,	1, 1, 1, 0,    0, 0, 0, 1, 1, 0,
		1, 0, 1,	0, 0,	1, 0, 1, 0,    0, 0, 0, 1, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 0,    0, 0, 3, 3, 3, 3,

		1, 2, 1,	2, 2,	1, 1, 1, 0,    0, 0, 0, 0, 3, 0,
		1, 0, 1,	0, 0,	1, 0, 1, 0,    0, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 0,    0, 0, 0, 0, 0, 0,

		0, 0, 0,	0, 0,	0, 0, 0, 0,    0, 0, 0, 0, 0, 0,
		0, 0, 0,	0, 0,	0, 0, 0, 0,    0, 0, 0, 0, 0, 0,

		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,

		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 1, 1, 0, 0, 0, 0, 0,

		1, 2, 1,	2, 2,	1, 1, 1, 0,    0, 0, 0, 0, 0, 0,
		1, 0, 1,	0, 0,	1, 0, 1, 0,    0, 0, 0, 0, 0, 0,
		1, 1, 1,	1, 1,	1, 1, 1, 0,    0, 0, 0, 0, 0, 0,

	};


	/**
	 * Private constructor to prevent instantiation.
	 *
	 * @param info Information on the tileset.
	 */
	private TilesetInfo(int[] info) {
		this.info = info;
	}


	/**
	 * Returns information on the specified tileset.
	 *
	 * @param tileset The tileset.
	 * @return Information about the tileset.
	 */
	public static TilesetInfo getInfo(String tileset) {

		int[] info = null;

		if (TILESET_GROUND_BROWN.equals(tileset)) {
			info = INFO_GROUND;
		}
		else if (TILESET_GROUND_BLUE.equals(tileset)) {
			info = INFO_GROUND;
		}
		else if (TILESET_UNDERGROUND_BROWN.equals(tileset)) {
			info = INFO_GROUND;
		}
		else if (TILESET_CASTLE_GRAY.equals(tileset)) {
			info = INFO_CASTLE;
		}
		else if (TILESET_UNDERWATER_GREEN.equals(tileset)) {
			info = INFO_GROUND;
		}
		else {
			throw new RuntimeException("Unknown tileset: " + tileset);
		}

		TilesetInfo ti = new TilesetInfo(info);
		return ti;

	}


	public boolean isEmpty(int tile) {
		return info[tile]==EMPTY;
	}


	public boolean isLandable(int tile) {
		return info[tile]==SOLID || info[tile]==LANDABLE;
	}


	public boolean isSolid(int tile) {
		return info[tile]==SOLID;
	}


}
