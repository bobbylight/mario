package org.fife.mario.powerups;

import org.fife.mario.AbstractEntity;
import org.fife.mario.GameInfo;
import org.fife.mario.Mario;
import org.fife.mario.PlayerInfo;
import org.fife.mario.PowerUp;
import org.fife.mario.anim.PointsAnimation;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;


/**
 * A 1-up.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OneUp extends Mushroom {


	public OneUp(float x, float y, boolean goRight) {
		super(x, y, goRight);
		int imageIndex = PowerUp.ONE_UP.getIndex();
		setSSIndex(imageIndex/3, imageIndex%3);
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		doOneUp(this);
		return true;
	}


	/**
	 * Gives the player a 1-up, and draws the "1UP" animation above the
	 * specified entity.
	 *
	 * @param entity The entity (e.g. the 1-up mushroom, or something else,
	 *        like Mario himself, for debugging with free 1-ups).
	 */
	public static void doOneUp(AbstractEntity entity) {
		PlayerInfo.get(0).incScore(1000);
		PlayerInfo.get(0).incLives(1);
		SoundEngine.get().play(SoundEngine.SOUND_ONE_UP);
		Area area = GameInfo.get().getLevel().getCurrentArea();
		area.addTemporaryAnimation(new PointsAnimation(
										entity.getX(), entity.getY(), "1UP"));
	}


}
