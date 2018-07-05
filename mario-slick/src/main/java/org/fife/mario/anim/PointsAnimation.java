package org.fife.mario.anim;

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
public class PointsAnimation extends org.fife.mario.Animation {

	//private Animation animation;
	private String points;
	private float totalDelta;


	public PointsAnimation(float x, float y, String points) {
		super(x, y);
		this.points = points;
	}


	@Override
	public float getHeight() {
		return 32;
	}


	@Override
	public float getWidth() {
		return 32;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {

		//animation.draw(getX()-area.xOffs, getY()-area.yOffs);

		float x = getX() - area.xOffs;
		float y = getY() - area.yOffs;
		g.setColor(Color.black);
		g.drawString(points, x+1, y+1);
		g.setColor(Color.white);
		g.drawString(points, x, y);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		float max = 1000f;
		if (totalDelta<max) {
			totalDelta += delta;
			moveY(-(max-totalDelta)/max*3);
		}
		else {
			setDone(true);
		}
	}


}
