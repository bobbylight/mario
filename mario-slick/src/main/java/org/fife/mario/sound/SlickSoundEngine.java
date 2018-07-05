package org.fife.mario.sound;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.SoundStore;


/**
 * A sound engine that uses Slick's sound API's.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class SlickSoundEngine extends SoundEngine {

	private Music[] musics;
	private Sound[] audio;
	private int curMusic;


	/**
	 * Private constructor to prevent instantiation.
	 */
	SlickSoundEngine() {
		SoundStore.get().init();
		musics = new Music[MUSIC_COUNT];
		audio = new Sound[SOUND_COUNT];
		curMusic = -1;
	}


	private void loadMusic(int music) {

		if (musics[music]!=null) { // Already loaded
			return;
		}

		String resource = MUSIC_FILES[music];
		System.out.println("Loading music " + music + ": " + resource);
		try {
			musics[music] = new Music(resource);
		} catch (SlickException se) {
			se.printStackTrace();
		}
		System.out.println("Done");

	}


	/**
	 * Loads a sound effect.
	 *
	 * @param sound The sound effect to load.
	 */
	private void loadSoundEffect(int sound) {
		String resource = SOUND_FILES[sound];
		try {
			audio[sound] = new Sound(resource);
		} catch (SlickException se) {
			se.printStackTrace();
		}
	}

	@Override
	public void play(int sound) {
		if (isSoundEnabled()) {
			if (audio[sound]==null) {
				loadSoundEffect(sound);
			}
			// Should never be null, unless classpath problem
			if (audio[sound]!=null) {
				audio[sound].play();
			}
		}
	}

	@Override
	public void playMusic(int music, boolean loop) {
		if (isSoundEnabled() && music!=curMusic) {
			if (musics[music]==null) {
				loadMusic(music);
			}
			// Should never be null, unless classpath problem.
			if (musics[music]!=null) {
				curMusic = music;
				if (loop) {
					musics[music].loop();
				}
				else {
					musics[music].play();
				}
			}
		}
	}

	@Override
	public void preloadMusic(int music) {
		if (isSoundEnabled()) {
			loadMusic(music);
		}
	}

	@Override
	public void preloadSoundEffects() {
		if (isSoundEnabled()) {
			for (int i=0; i<SOUND_COUNT; i++) {
				loadSoundEffect(i);
			}
		}
	}

	@Override
	public void setSoundEnabled(boolean enabled) {
		if (enabled!=this.enabled) {
			this.enabled = enabled;
			SoundStore.get().setMusicOn(enabled);
			SoundStore.get().setSoundsOn(enabled);
		}
	}

	@Override
	public void stopMusic() {
		if (curMusic>-1) {
			musics[curMusic].stop();
			curMusic = -1;
		}
	}


}
