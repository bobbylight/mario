package org.fife.mario.anim;

import java.util.ArrayList;
import java.util.List;

import org.fife.mario.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;


/**
 * Animation played when Mario grabs a coin.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CoinGrabbedAnimation extends Animation {

	private org.newdawn.slick.Animation anim;
	private static SpriteSheet ss;
	private boolean showScore;

	private static final int COIN_SCORE			= 10;


	/**
	 * Constructor.
	 *
	 * @param x
	 * @param y
	 * @param showScore
	 */
	public CoinGrabbedAnimation(float x, float y, boolean showScore) {
		super(x, y);
		anim = new org.newdawn.slick.Animation();
		anim.setLooping(false);
		for (int col=0; col<8; col++) {
			anim.addFrame(ss.getSubImage(col, 9), 400);
		}
	}


	@Override
	public float getHeight() {
		return 32;
	}


	@Override
	public List<org.fife.mario.Animation> getReplacementAnimations() {
		List<Animation> l = null;
		if (showScore) {
			l = new ArrayList<>();
			l.add(new PointsAnimation(getX(), getY(), ""+COIN_SCORE));
		}
		return l;
	}


	@Override
	public float getWidth() {
		return 32;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {
		anim.draw(getX()-area.xOffs, getY()-area.yOffs, filter);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		anim.update(delta);
		if (anim.isStopped()) {
			setDone(true);
		}
	}


	static {
		try {
			Image img = new Image("img/blocks.png", new Color(192,192,192));
			ss = new SpriteSheet(img, 32,32, 2);
		} catch (SlickException se) {
			throw new InternalError(se.toString());
		}
	}


}
