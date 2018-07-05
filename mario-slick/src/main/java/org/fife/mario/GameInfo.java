package org.fife.mario;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.fife.mario.level.Level;
import org.newdawn.slick.SlickException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


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

	private static final String LEVEL				= "level";
	private static final String RESOURCE			= "resource";

	/**
	 * The game that is loaded if none is specified as a system property.
	 */
	private static final String DEFAULT_GAME_FILE	= "/game.xml";

	/**
	 * The singleton instance of this class.
	 */
	private static final GameInfo INSTANCE = new GameInfo();


	/**
	 * Private constructor to prevent instantiation.
	 */
	private GameInfo() {

		levels = new ArrayList<>();

		String gameFile = DEFAULT_GAME_FILE;

		InputStream in = getClass().getResourceAsStream(gameFile);
		BufferedInputStream bin = new BufferedInputStream(in);

		try {
			XMLReader xr = createReader();
			Handler handler = new Handler();
			xr.setContentHandler(handler);
			InputSource is = new InputSource(bin);
			is.setEncoding("UTF-8");
			xr.parse(is);
			bin.close();
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

		if (levels.size()==0) {
			throw new IllegalArgumentException("No levels defined in " +
												gameFile);
		}

		reset();

	}


	/**
	 * Creates the XML reader to use.  Note that in 1.4 JRE's, the reader
	 * class wasn't defined by default, but in 1.5+ it is.
	 *
	 * @return The XML reader to use.
	 */
	private XMLReader createReader() {
		XMLReader reader = null;
		try {
			reader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return reader;
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


	/**
	 * Parses an XML game file.
	 */
	private class Handler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName,
									Attributes attrs) {

			if (LEVEL.equals(qName)) {
				String resource = attrs.getValue(RESOURCE);
				levels.add(resource);
			}

		}

	}


}
