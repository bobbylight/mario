package org.fife.mario.anim;

import java.util.ArrayList;
import java.util.List;

import org.fife.mario.Constants;
import org.fife.mario.blocks.Block;
import org.fife.mario.blocks.BlockTypes;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The animation of a coin hit out of a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CoinAnimation extends org.fife.mario.Animation {

	public static final float HEIGHT		= 32;

	private Animation animation;
	private float ySpeed;
	private boolean fromBlock;

	private static final float INITIAL_Y_SPEED		= -4;


	public CoinAnimation(float x, float y, boolean fromBlock)
							throws SlickException {
		super(x, y);
		this.fromBlock = fromBlock;
		animation = Block.createAnimation(BlockTypes.BLOCK_YELLOW_COIN, 60, true);
		ySpeed = INITIAL_Y_SPEED;
	}


	@Override
	public List<org.fife.mario.Animation> getReplacementAnimations() {
		List<org.fife.mario.Animation> l = new ArrayList<>();
		l.add(new CoinGrabbedAnimation(getX(), getY(), fromBlock));
		return l;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getWidth() {
		return 32;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
					Graphics g, Color filter) throws SlickException {
		animation.draw(getX()-area.xOffs, getY()-area.yOffs, filter);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
//		float max1 = 600f;
//		if (totalDelta<max1) {
//			totalDelta = Math.min(totalDelta+delta, max1);
//			y -= 0.65f;
//		}
//		else {
//			float max2 = 900f;
//			if (totalDelta<max2) {
//				totalDelta = Math.min(totalDelta+delta, max2);
//				y += 0.45f;
//				if (totalDelta>=max2) {
//					setDone(true);
//				}
//			}
//		}
moveY(ySpeed);
ySpeed += Constants.GRAVITY/2;
if (ySpeed>=2) {
	setDone(true);
}
	}


}
