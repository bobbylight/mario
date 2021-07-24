package org.fife.mario.sound;


/**
 * Manages all sound in the Mario game.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class SoundEngine {

	public static final int SOUND_COIN				= 0;
	public static final int SOUND_EXPLODE			= 1;
	public static final int SOUND_FIREBALL			= 2;
	public static final int SOUND_FLAME				= 3;
	public static final int SOUND_GROW				= 4;
	public static final int SOUND_HIT_HEAD			= 5;
	public static final int SOUND_ITEM				= 6;
	public static final int SOUND_JUMP				= 7;
	public static final int SOUND_KICK				= 8;
	public static final int SOUND_ONE_UP			= 9;
	public static final int SOUND_SHRINK			= 10;
	public static final int SOUND_SPRING			= 11;
	public static final int SOUND_STOMP				= 12;
	public static final int SOUND_SWIM				= 13;
	public static final int SOUND_TEXT_BOX			= 14;
	public static final int SOUND_WARP				= 15;
	protected static final int SOUND_COUNT			= 16;

	public static final int MUSIC_TITLE_SCREEN		= 0;
	public static final int MUSIC_OVERWORLD			= 1;
	public static final int MUSIC_UNDERGROUND		= 2;
	public static final int MUSIC_ATHLETIC			= 3;
	public static final int MUSIC_CASTLE			= 4;
	public static final int MUSIC_MARIO_DIES		= 5;
	public static final int MUSIC_COURSE_CLEAR		= 6;
	public static final int MUSIC_GAME_OVER			= 7;
	protected static final int MUSIC_COUNT			= 8;

	public static final String PROPERTY_SOUND_DISABLED = "sound.disabled";

	private static final SoundEngine INSTANCE = new SlickSoundEngine();

	protected static final String[] SOUND_FILES;

	protected static final String[] MUSIC_FILES;

	protected boolean enabled;


	static {

		String prefix;
		ClassLoader cl = SoundEngine.class.getClassLoader();
		if (cl.getResource("sounds/high-def")!=null) {
			prefix = "sounds/high-def/";
		}
		else {
			prefix = "sounds/low-def/";
		}
		System.out.println("Loading sounds from: " + prefix);

		SOUND_FILES = new String[] {
			prefix + "coin.aif",
			prefix + "explode.ogg",
			prefix + "fireball.ogg",
			prefix + "flame.ogg",
			prefix + "grow.ogg",
			prefix + "bump_head.ogg",
			prefix + "powerup_sprout.ogg",
			prefix + "jump.wav",
			prefix + "kick.ogg",
			prefix + "one_up.ogg",
			prefix + "shrink.ogg",
			prefix + "spring.ogg",
			prefix + "kick2.wav",
			prefix + "swim.ogg",
			prefix + "text_box.ogg",
			prefix + "shrink.ogg", // Warping and shrinking are the same sound!
		};

		MUSIC_FILES = new String[] {
			prefix + "title_screen.ogg",
			prefix + "overworld1.ogg",
			prefix + "underground.ogg",
			prefix + "athletic.ogg",
			prefix + "castle.ogg",
			prefix + "die.ogg",
			prefix + "course_clear.ogg",
			prefix + "game_over.ogg",
		};

	}


	/**
	 * Private constructor to prevent instantiation.
	 */
	SoundEngine() {
		enabled = System.getProperty(PROPERTY_SOUND_DISABLED)==null;
	}


	/**
	 * Returns the singleton instance of this sound engine.
	 *
	 * @return The sound engine.
	 */
	public static SoundEngine get() {
		return INSTANCE;
	}


	/**
	 * Returns whether sound is enabled.
	 *
	 * @return Whether sound is enabled.
	 * @see #setSoundEnabled(boolean)
	 */
	public boolean isSoundEnabled() {
		return enabled;
	}


	public abstract void play(int sound);


	public abstract void playMusic(int music, boolean loop);


	/**
	 * Pre-loads a specific song, if it isn't already loaded.
	 *
	 * @param music The song to load.
	 * @see #preloadSoundEffects()
	 */
	public abstract void preloadMusic(int music);


	/**
	 * Pre-loads all sound effects.  If this is not called, then there may
	 * be a slight delay the first time an effect is played as it is loaded.
	 */
	public abstract void preloadSoundEffects();


	/**
	 * Toggles whether sound is enabled.
	 *
	 * @param enabled Whether sound is enabled.
	 * @see #isSoundEnabled()
	 */
	public abstract void setSoundEnabled(boolean enabled);


	/**
	 * Stops playing the current music.
	 */
	public abstract void stopMusic();


}
