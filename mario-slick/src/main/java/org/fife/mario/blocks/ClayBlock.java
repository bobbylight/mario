package org.fife.mario.blocks;

import org.fife.mario.Mario;
import org.fife.mario.level.Area;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * One of the "clay" (or whatever it is) blocks that come in yellow, blue and
 * gray.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ClayBlock extends Block {

	private Animation anim;
	private BlockTypes type;
	private boolean spinning;
	private int bounceFrame;
	private int spinTime;

	private static final int MAX_SPIN_TIME		= 5000; // 5 seconds


	public ClayBlock(Area area, BlockTypes type) {
		super(area);
		setType(type);
	}


	@Override
	public void activate(Mario mario) {
		checkBump(mario);
		bounceFrame = 1;
	}


	@Override
	public BlockTypes getType() {
		return type;
	}


	@Override
	public boolean isActivatable() {
		return !spinning;
	}


	@Override
	public boolean isHittable() {
		return !spinning;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {

		if (spinning) {
			float x = getX()-area.xOffs;
			float y = getY()-area.yOffs;
			anim.draw(x, y, filter);
		}

		else {
			int yBounce = 0;
			if (bounceFrame>0) {
				if (bounceFrame<8) {
					yBounce = -1*bounceFrame;
				}
				else {
					yBounce = -8 + 2*(bounceFrame-8);
				}
			}
			float x = getX()-area.xOffs;
			float y = getY()+yBounce-area.yOffs;
			renderImage(g, x,y, type.getTypeIndex(),0, filter);
		}

	}


	private void setType(BlockTypes type) {
		if (type!=BlockTypes.BLOCK_YELLOW &&
				type!=BlockTypes.BLOCK_BLUE &&
				type!=BlockTypes.BLOCK_GRAY) {
			throw new IllegalArgumentException("Invalid block type: " + type);
		}
		this.type = type;
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (spinning) {
			spinTime += delta;
			if (spinTime>=MAX_SPIN_TIME) {
				spinning = false;
				anim.stop();
				spinTime = 0;
			}
		}
		if (bounceFrame>0 && bounceFrame<MAX_BOUNCE_FRAME) {
			bounceFrame++;
		}
		else if (bounceFrame>=MAX_BOUNCE_FRAME) {
			bounceFrame = 0;
			if (anim==null) {
				anim = createAnimation(type, 200, true);
			}
			anim.stop();
			//anim.restart(); // Always start from first frame.
			anim.setCurrentFrame(1);
			anim.start();
			spinning = true;
			spinTime = 0;
		}
	}


}
