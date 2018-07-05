package org.fife.mario.level;

import org.fife.mario.Constants;


/**
 * A space in the 2-D map.  It contains what terrain is in the cell, as well
 * as what block or coin (if any).
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MapCell {

	private int terrain;
	private int blockIndex;


	MapCell() {
		this(0, 0);
	}


	MapCell(int terrain, int blockIndex) {
		setTerrain(Constants.MIDDLE, terrain);
		setBlockIndex(blockIndex);
	}


	public int getBlockIndex() {
		return blockIndex;
	}


	public int getTerrain() {
		return getTerrain(1);
	}


	public int getTerrain(int layer) {
		return (terrain>>(8*layer)) & 0xff;
	}


	public int getTerrainAllLayers() {
		return terrain & 0xffffff;
	}


	public void setBlockIndex(int index) {
		this.blockIndex = index;
	}


	public void setTerrain(int layer, int terrain) {
		int shift = 8*layer;
		int mask = 0xff << shift;
		this.terrain &= ~mask;
		this.terrain |= (terrain&0xff) << shift;
	}


	public void setTerrainAllLayers(int value) {
		terrain = value;
	}


}
