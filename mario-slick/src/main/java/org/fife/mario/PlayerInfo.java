package org.fife.mario;

import org.fife.mario.sound.SoundEngine;


/**
 * Information about the players.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class PlayerInfo {

	private int lives;
	private int coinCount;
	private int score;

	private static PlayerInfo[] infos;

	private static final int COIN_SCORE					= 10;
	private static final int DEFAULT_LIFE_COUNT			= 3;


	private PlayerInfo() {
		reset();
	}


	/**
	 * Removes a life from this player.
	 *
	 * @return Whether this player has lost all of their lives.
	 */
	public boolean die() {
		return lives--<=0;
	}


	public static PlayerInfo get(int player) {
		return infos[player];
	}


	public int getCoinCount() {
		return coinCount;
	}


	public int getLives() {
		return lives;
	}


	public int getScore() {
		return score;
	}


	public void incCoinCount(int amt) {
		score += COIN_SCORE*amt;
		coinCount += amt;
		if (coinCount>=100) {
			coinCount -= 100;
			incLives(1);
			SoundEngine.get().play(SoundEngine.SOUND_ONE_UP);
		}
	}


	public void incLives(int amt) {
		lives += amt;
	}


	public void incScore(int amt) {
		score += amt;
	}


	public void reset() {
		score = coinCount = 0;
		lives = DEFAULT_LIFE_COUNT;
	}


	static {
		infos = new PlayerInfo[2];
		for (int i=0; i<infos.length; i++) {
			infos[i] = new PlayerInfo();
		}
	}


}
