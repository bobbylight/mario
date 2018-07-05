package org.fife.mario.level;

import java.util.ArrayList;
import java.util.List;

import org.fife.mario.Constants;
import org.fife.mario.blocks.Block;


/**
 * The 2D grid that contains what terrain, blocks and coins are in a level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class MapData {

	public static final int TERRAIN_COIN		= -1;

	private MapCell[][] data;
	private List<Block> blocks;


	public MapData(int rowCount, int colCount) {
		blocks = new ArrayList<>();
		reset(rowCount, colCount);
	}


	public void addBlock(int row, int col, Block b) {
		blocks.add(b);
		setBlockIndex(row, col, blocks.size());
	}


	public void addCoin(int row, int col) {
		data[row][col].setBlockIndex(TERRAIN_COIN);
	}


	public Block getBlock(int index) {
		return blocks.get(index);
	}


	public Block getBlockAt(int row, int col) {
		int index = getBlockIndex(row, col);
		return index>0 ? getBlock(index-1) : null;
	}


	private int getBlockIndex(int row, int col) {
		if (row<0 || row>=data.length) {
			System.out.println("Invalid row: " + row + "(0-" + (data.length-1) + ")");
		}
		if (col<0 || col>=data[0].length) {
			System.out.println("Invalid col: " + col + "(0-" + (data[0].length-1) + ")");
		}
		return data[row][col].getBlockIndex();
	}


	public int getColumnCount() {
		return data[0].length;
	}


	public int getRowCount() {
		return data.length;
	}


	public int getTerrain(int layer, int row, int col) {
		return data[row][col].getTerrain(layer);
	}


	public int getTerrainAllLayers(int row, int col) {
		return data[row][col].getTerrainAllLayers();
	}


	public boolean isCoinAt(int row, int col, boolean remove) {
		boolean coin = getBlockIndex(row, col)==TERRAIN_COIN;
		if (coin && remove) {
			setBlockIndex(row, col, 0);
		}
		return coin;
	}


	public boolean isLava(int row, int col) {
		// Allow safety here for "bottomless pits."
		if (row>=getRowCount() || col>=getColumnCount()) {
			return false;
		}
		int terrain = getTerrain(Constants.MIDDLE, row, col) - 1;
		return (terrain>=41 && terrain<=44) || terrain==58;
	}


	public boolean isPipeSide(int row, int col) {
		boolean pipeSide = false;
		int terrain = getTerrain(Constants.MIDDLE, row, col);
		if (terrain>0) {
			terrain--;
			// TODO: Remove magic numbers
			if ((terrain>=9*15 && terrain<=9*15+9) ||
					(terrain>=10*15 && terrain<=10*15+9 && (terrain&1)==1) ||
					(terrain>=11*15 && terrain<=11*15+9 && (terrain&1)==0)) {
				pipeSide = true;
			}

		}
		return pipeSide;
	}


	/**
	 * Removes the block at the specified location.
	 *
	 * @param row The row of the block.
	 * @param col The column of the block.
	 * @return Whether a block was at that position to remove.
	 */
	public boolean removeBlockAt(int row, int col) {
		int index = data[row][col].getBlockIndex();
		if (index>0) {
			data[row][col].setBlockIndex(0);
			blocks.set(index-1, null); // Keep list order the same
			//return blocks.remove(index-1)!=null; // Should always be true
		}
		return false;
	}


	public void removeAllBlocks() {
		blocks.clear();
		for (int row=0; row<getRowCount(); row++) {
			for (int col=0; col<getColumnCount(); col++) {
				if (data[row][col].getBlockIndex()>0) {
					data[row][col].setBlockIndex(0);
				}
			}
		}
	}


	public void removeAllCoins() {
		for (int row=0; row<getRowCount(); row++) {
			for (int col=0; col<getColumnCount(); col++) {
				if (data[row][col].getBlockIndex()==TERRAIN_COIN) {
					data[row][col].setBlockIndex(0);
				}
			}
		}
	}


	public void reset(int rowCount, int colCount) {
		data = new MapCell[rowCount][colCount];
		for (int row=0; row<rowCount; row++) {
			for (int col=0; col<colCount; col++) {
				data[row][col] = new MapCell();
			}
		}
		blocks.clear();
	}


	/**
	 * Resizes the map grid.  Keeps any data (tiles, blocks, coins) that aren't
	 * "cut out" by the new bounds.  This method may be useful for level
	 * editors.
	 *
	 * @param rowCount The new row count.
	 * @param colCount The new column count.
	 */
	public void resize(int rowCount, int colCount) {

		MapCell[][] data2 = new MapCell[rowCount][colCount];

		// Keep old map data for part kept around.
		int oldRowCount = getRowCount();
		int oldColCount = getColumnCount();
		int row = rowCount - 1;
		int oldRow = oldRowCount - 1;
		while (row>=0 && oldRow>=0) {
			for (int col=0; col<colCount; col++) {
				if (col<oldColCount) {
					data2[row][col] = data[oldRow][col];
				}
				else {
					data2[row][col] = new MapCell();
				}
			}
			row--;
			oldRow--;
		}
		while (row>=0) {
			for (int col=0; col<colCount; col++) {
				data2[row][col] = new MapCell();
			}
			row--;
		}
		data = data2;

		// Figure out what blocks to keep
		List<Block> newBlocks = new ArrayList<>();
		int maxRow = Math.min(oldRowCount, rowCount);
		int maxCol = Math.min(oldColCount, colCount);
		for (row=0; row<maxRow; row++) {
			for (int col=0; col<maxCol; col++) {
				int oldIndex = data2[row][col].getBlockIndex();
				if (oldIndex>0) {
					newBlocks.add(blocks.get(oldIndex-1));
					// effectively "index+1".
					data2[row][col].setBlockIndex(newBlocks.size());
				}
			}
		}
		blocks = newBlocks;

	}


	private void setBlockIndex(int row, int col, int index) {
		data[row][col].setBlockIndex(index);
	}


	public void setTerrainAllLayers(int row, int col, int value) {
		data[row][col].setTerrainAllLayers(value);
	}


	public void setTerrain(int layer, int row, int col, int terrain) {
		data[row][col].setTerrain(layer, terrain);
	}


}
