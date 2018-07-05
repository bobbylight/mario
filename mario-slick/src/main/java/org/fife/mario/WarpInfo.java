package org.fife.mario;



/**
 * Information about a warp.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class WarpInfo {

	private String destArea;
	private Position startPos;


	public WarpInfo(String destArea) {
		setDestArea(destArea);
	}


	public String getDestArea() {
		return destArea;
	}


	public Position getStartPosition() {
		return startPos;
	}


	public void setDestArea(String area) {
		this.destArea = area;
	}


	public void setStartPosition(Position pos) {
		startPos = pos.clone();
	}


	@Override
	public String toString() {
		return "[WarpInfo: warpTo=" + getDestArea() + "]";
	}


}
