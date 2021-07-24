package org.fife.mario;

/**
 * A row-column pair, representing a position on the tile map of a level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Position implements Comparable<Position>, Cloneable {

	private int row;
	private int col;

	public Position() {
		this(0, 0);
	}

	public Position(int row, int col) {
		setRow(row);
		setCol(col);
	}

	@Override
	public Position clone() {
		Position pos;
		try {
			pos = (Position)super.clone();
		} catch (CloneNotSupportedException cnse) { // Never happens
			cnse.printStackTrace();
			return null;
		}
		pos.set(row, col);
		return pos;
	}

	@Override
	public int compareTo(Position p2) {
		int diff = getCol() - p2.getCol();
		if (diff==0) {
			diff = getRow() - p2.getRow();
		}
		return diff;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Position) && compareTo((Position)obj)==0;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	@Override
	public int hashCode() {
		return (getRow()&0xffff)<<16 | getCol();
	}

	public void incRow(int amt) {
		setRow(getRow() + amt);
	}

	public void set(int row, int col) {
		setRow(row);
		setCol(col);
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "[Position: (" + row + ", " + col + ")]";
	}

}
