package org.fife.mario;

/**
 * The result of Mario (possibly) colliding with a wall.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CollisionResult {

	public boolean leftWall;
	public boolean rightWall;
	public boolean below;
	public boolean above;

	public CollisionResult() {
	}

	@Override
	public String toString() {
		return "[CollisionResult: " + above + ", " + leftWall + ", " + below + ", " + rightWall + "]";
	}
}
