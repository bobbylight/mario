package org.fife.mario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fife.mario.level.Level;
import org.newdawn.slick.SlickException;


/**
 * Information about the game being played.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class GameInfo {

	private List<String> levels;
	private int curLevel;
	private Level level;
	private String text;

	/**
	 * The game that is loaded if none is specified as a system property.
	 */
	private static final String DEFAULT_GAME_FILE	= "/game.json";

	/**
	 * The singleton instance of this class.
	 */
	private static final GameInfo INSTANCE = new GameInfo();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private GameInfo() {

		String gameFile = DEFAULT_GAME_FILE;

		GameData gameData;
		try {
		    gameData = new ObjectMapper().readValue(getClass().getResource(gameFile), GameData.class);
        } catch (IOException ioe) {
		    throw new RuntimeException("Failed to load game data in " + gameFile, ioe);
        }

		levels = gameData.getLevels();

		if (levels.size()==0) {
			throw new IllegalArgumentException("No levels defined in " + gameFile);
		}

		reset();
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return The singleton game info instance.
	 */
	public static GameInfo get() {
		return INSTANCE;
	}

	public Level getLevel() {
		return level;
	}

	public String getTextMessage() {
		return text;
	}

	/**
	 * Loads the next level, or enters the "you win" state if all levels have
	 * been completed.
	 *
	 * @return Whether the next level has been loaded (as opposed to the
	 *         "you win" screen being entered).
	 * @throws SlickException
	 */
	public boolean loadNextLevel() throws SlickException {
		curLevel++;
		System.out.println("DEBUG: Loading level " + curLevel + " of " + levels.size());
		if (curLevel==levels.size()) {
			// TODO: Game over screen!
			Main.get().enterState(Constants.STATE_TITLE_SCREEN);
			return false;
		}
		level.load(levels.get(curLevel));
		return true;
	}

	/**
	 * Resets the game back to level 1-1.
	 */
	public void reset() {
		curLevel = 0;
		try {
			level = new Level(levels.get(curLevel));
		} catch (SlickException se) {
			// TODO: Better error handling; perhaps an "error message" state
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void setTextMessage(String text) {
		this.text = text;
	}

    private static final class GameData {

	    private List<String> levels;

	    public List<String> getLevels() {
	        return new ArrayList<>(levels);
        }

        public void setLevels(List<String> levels) {
	        this.levels = levels == null ? Collections.emptyList() : new ArrayList<>(levels);
        }
    }
}
