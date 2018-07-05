package org.fife.mario;


/**
 * The obligatory interface containing some constants.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class Constants {

	public static final int STATE_FIFESOFT_PRESENTS	= 1;

	public static final int STATE_TITLE_SCREEN		= 2;

	public static final int STATE_CHECK_SCORES		= 3;

	public static final int STATE_PRE_LEVEL			= 4;

	public static final int STATE_PLAYING_GAME		= 5;

	public static final int STATE_TEXT_BOX			= 6;

	public static final int STATE_LEVEL_COMPLETED	= 7;

	public static final int STATE_BRIDGE_COLLAPSING	= 8;

	public static final int STATE_CASTLE_COMPLETED	= 9;

	public static final int STATE_GAME_OVER			= 10;

	/**
	 * Represents the foreground tile layer.
	 */
	public static final int FOREGROUND		= 0;

	/**
	 * Represents the middle tile layer.
	 */
	public static final int MIDDLE			= 1;

	/**
	 * Represents the background tile layer.
	 */
	public static final int BACKGROUND		= 2;

	public static final float GRAVITY					= 0.35f;

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        // Do nothing (message for Sonar)
    }
}
