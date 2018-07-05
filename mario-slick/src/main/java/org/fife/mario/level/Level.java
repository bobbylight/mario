package org.fife.mario.level;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A level for Mario to complete.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Level {

	private Map<String, Area> areas;
	private String currentArea;
	private String level;
	private int totalTime;
	private boolean showGrid;

	private Animation startingAnim;
	private float startX;
	private float startY;


	public Level(String res) throws SlickException {
		areas = new HashMap<>();
		totalTime = 300;
		load(res);
	}


	public void addArea(String name, Area area) {
		areas.put(name, area);
	}


	public void checkOtherEntityCollisions(GameContainer container,
			StateBasedGame game, int delta) throws SlickException {
		areas.get(currentArea).checkOtherEntityCollisions(container, game, delta);
	}


	void clearAreas() {
		areas.clear();
		currentArea = "main";
	}


	public Map<String, Area> getAreas() {
		return new HashMap<>(areas);
	}


	public Area getCurrentArea() {
		return areas==null ? null : areas.get(currentArea);
	}


	public String getCurrentAreaName() {
		return currentArea;
	}


	public String getDisplayName() {
		if (level==null) {
			return null;
		}
		int start = level.lastIndexOf('/') + 1;
		int dot = level.indexOf('.', start);
		return level.substring(start, dot).replace("-", " - ");
	}


	public boolean getShowGrid() {
		return showGrid;
	}


	/**
	 * Returns the animation that should play when Mario starts this level
	 * (i.e., warping in through a pipe).
	 *
	 * @return The animation, or <code>null</code> if none.
	 * @see #setStartingAnimation(Animation)
	 */
	public Animation getStartingAnimation() {
		return startingAnim;
	}


	public int getTotalTime() {
		return totalTime;
	}


	public void load(String level) throws SlickException {
		this.level = level;
		//reset(level); // This will be done when entering the game state
	}


	private void loadTerrain(String resource) throws IOException {
		LevelLoader.load(resource, this);
	}


	public void moveToStartingLocation(Character ch) {
		ch.setLocation(startX, startY+32-ch.getHeight());
	}


	public void reset() throws SlickException {
		reset(level);
	}


	private void reset(String level) throws SlickException {
		try {
			loadTerrain(level);
		} catch (IOException ioe) {
			throw new SlickException("Error loading level", ioe);
		}
	}


	public Area setCurrentArea(String name) {
		Area current = getCurrentArea();
		Area next = areas.get(name);
		if (current!=null && current!=next) {
			current.marioLeft();
		}
		currentArea = name;
		return getCurrentArea();
	}


	/**
	 * Sets the animation that should play when Mario starts this level
	 * (i.e., warping in through a pipe).
	 *
	 * @param anim The animation, or <code>null</code> if none.
	 * @see #getStartingAnimation()
	 */
	public void setStartingAnimation(Animation anim) {
		startingAnim = anim;
	}


	public void setStartingLocation(float x, float y) {
		startX = x;
		startY = y;
	}


	/**
	 * Toggles whether the grid is visible.
	 */
	public void toggleShowGrid() {
		showGrid = !showGrid;
	}


}
