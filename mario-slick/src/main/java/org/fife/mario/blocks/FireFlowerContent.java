package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.MarioState;
import org.fife.mario.PowerUp;
import org.fife.mario.anim.PowerUpAnimation;
import org.fife.mario.blocks.LoadedBlock.Content;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;


/**
 * Content for a block containing a fire flower (or mushroom if Mario is small).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FireFlowerContent implements Content {

	private boolean empty;


	protected PowerUp getPowerUp(Mario mario) {
		return  mario.getState()==MarioState.SMALL ?
				PowerUp.MUSHROOM : PowerUp.FIRE_FLOWER;
	}


	@Override
	public boolean hasMoreContent() {
		return !empty;
	}


	@Override
	public int makeAvailable(Block fromBlock, Mario mario, Area area,
								boolean goRight) {

		float x = fromBlock.getX();
		float y = fromBlock.getY();

		PowerUp powerUp = getPowerUp(mario);
		PowerUpAnimation a = new PowerUpAnimation(x, y, powerUp, goRight);
		area.addTemporaryAnimation(a);

		empty = true;
		return SoundEngine.SOUND_ITEM;

	}


}
