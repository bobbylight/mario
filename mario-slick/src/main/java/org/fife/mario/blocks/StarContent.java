package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.PowerUp;


/**
 * Content that releases a star.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class StarContent extends FireFlowerContent {


	@Override
	protected PowerUp getPowerUp(Mario mario) {
		return  PowerUp.STAR;
	}


}
