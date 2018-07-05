package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.PowerUp;


/**
 * A 1-up.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OneUpContent extends FireFlowerContent {


	@Override
	protected PowerUp getPowerUp(Mario mario) {
		return  PowerUp.ONE_UP;
	}


}
