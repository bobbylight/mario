package org.fife.mario;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;


/**
 * State where Mario is talking either to Toad or Princess Toadstool.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CastleCompletedState extends TextBoxState {


	public CastleCompletedState(int id) {
		super(id);
	}


	@Override
	protected void enterNextState(StateBasedGame game) throws SlickException {
		if (GameInfo.get().loadNextLevel()) {
			GameInfo.get().setTextMessage(null); // Hack to keep things working
			// The next level was successfully loaded.
			game.enterState(Constants.STATE_PRE_LEVEL, new FadeOutTransition(), null);
		}
		else {
			// There were no more levels, so the "You win" state was entered.
			GameInfo.get().reset();
		}
	}


}
