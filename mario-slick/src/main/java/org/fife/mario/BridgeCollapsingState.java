package org.fife.mario;

import org.fife.mario.blocks.Block;
import org.fife.mario.blocks.BlockTypes;
import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * State when Bowser's bridge is collapsing.
 */
public class BridgeCollapsingState extends BasicGameState {

	private int id;
	private int time;
	private int row;
	private int currentCol;
	private int count;

	private static final int BRIDGE_PIECE_COLLAPSE_TIME		= 225;


	/**
	 * Constructor.
	 *
	 * @param id The ID for this state.
	 */
	public BridgeCollapsingState(int id) {
		this.id = id;
	}


	@Override
	public void enter(GameContainer container, StateBasedGame game)
										throws SlickException {
		currentCol = -1;
		time = BRIDGE_PIECE_COLLAPSE_TIME;
		count = 0;
	}


	@Override
	public int getID() {
		return id;
	}


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		// TODO Auto-generated method stub

	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {

		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();
		area.render(container, game, g, null);

		g.setColor(Color.white);
		g.drawString("You win!!!", 200, 200);

	}


	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		time -= delta;
		if (time>0) {
			return;
		}
		time += BRIDGE_PIECE_COLLAPSE_TIME;

		Area area = GameInfo.get().getLevel().getCurrentArea();

		if (currentCol==-1) {
OUTER:
			for (currentCol=area.getColumnCount()-1; currentCol>=0; currentCol--) {
				for (row=0; row<area.getRowCount(); row++) {
					Block block = area.getBlockAt(row, currentCol);
					if (block!=null && block.getType()==BlockTypes.BLOCK_SOLID_BROWN) {
						area.removeBlockAt(row, currentCol);
						SoundEngine.get().play(SoundEngine.SOUND_EXPLODE);
						count++;
						break OUTER;
					}
				}
			}
		}
		else {
			Block block = area.getBlockAt(row, --currentCol);
			if (block==null) {
System.out.println("No more blocks!");
				// TODO: Create "real" way to have GameState not reset level
				GameInfo.get().setTextMessage("Hack text");
				game.enterState(Constants.STATE_PLAYING_GAME);
				return;
			}
			else {
				area.removeBlockAt(row, currentCol);
				if ((++count&1)==0) {
					SoundEngine.get().play(SoundEngine.SOUND_EXPLODE);
				}
			}
		}

	}


}
