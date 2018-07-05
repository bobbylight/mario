package org.fife.mario.blocks;

import org.fife.mario.*;
import org.fife.mario.blocks.LoadedBlock.Content;
import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;


/**
 * Text content for a text block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TextContent implements Content {

	private String text;


	public TextContent(String text) {
		this.text = text;
	}

	public boolean hasMoreContent() {
		return true;
	}

	@Override
	public int makeAvailable(Block fromBlock, Mario mario, Area area,
			boolean goRight) {
		area.addFutureTask(new TextBoxTask(text));
		return -1; // No sound yet
	}


	private static class TextBoxTask extends FutureTask {

		TextBoxTask(String text) {
			super(300);
			GameInfo.get().setTextMessage(text);
		}

		@Override
		public void run() {
			SoundEngine.get().play(SoundEngine.SOUND_TEXT_BOX);
			Main.get().enterState(Constants.STATE_TEXT_BOX);
		}

	}


}
