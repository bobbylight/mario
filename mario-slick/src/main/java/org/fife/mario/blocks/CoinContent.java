package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.PlayerInfo;
import org.fife.mario.anim.CoinAnimation;
import org.fife.mario.blocks.LoadedBlock.Content;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;

import org.newdawn.slick.SlickException;


/**
 * A number of coins in a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CoinContent implements Content {

	private int count;


	public CoinContent(int count) {
		this.count = count;
	}


	@Override
	public boolean hasMoreContent() {
		return count>0;
	}


	@Override
	public int makeAvailable(Block fromBlock, Mario mario, Area area,
							boolean goRight) {
		if (count>0) { // Always true
			count--;
		}
		float x = fromBlock.getX();
		float y = fromBlock.getY() - CoinAnimation.HEIGHT;
		try {
			CoinAnimation ca = new CoinAnimation(x, y, true);
			area.addTemporaryAnimation(ca);
		} catch (SlickException se) {
			se.printStackTrace();
		}
		PlayerInfo.get(0).incCoinCount(1);
		return SoundEngine.SOUND_COIN;
	}


}
