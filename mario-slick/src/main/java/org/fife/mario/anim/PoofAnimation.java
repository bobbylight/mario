package org.fife.mario.anim;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;


/**
 * An animation of a fireball or enemy disappearing in a "poof".
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PoofAnimation extends org.fife.mario.Animation {

	private Animation anim;
	private static SpriteSheet ss;

	private static final int SIZE = 36;


	public PoofAnimation(float x, float y) {
		super(x-SIZE/2, y);
		try {
			anim = createAnimation();
		} catch (SlickException se) {
			se.printStackTrace();
		}
	}


	private Animation createAnimation() throws SlickException {
		if (ss==null) {
			String img = "img/fireball_explode.png";
			Color trans = new Color(192,192,192);
			Image temp = new Image(img, false, Image.FILTER_NEAREST, trans);
			ss = new SpriteSheet(temp, 36,36, 2);
		}
		Animation a = new Animation(true);
		a.setLooping(false);
		int duration = 50;
		a.addFrame(ss.getSubImage(0, 0), duration);
		a.addFrame(ss.getSubImage(1, 0), duration);
		a.addFrame(ss.getSubImage(2, 0), duration);
		a.addFrame(ss.getSubImage(0, 1), duration);
		a.addFrame(ss.getSubImage(1, 1), duration);
		a.addFrame(ss.getSubImage(2, 1), duration);
		a.addFrame(ss.getSubImage(0, 2), duration);
		a.addFrame(ss.getSubImage(1, 2), duration);
		a.addFrame(ss.getSubImage(2, 2), duration);
		a.addFrame(ss.getSubImage(3, 2), duration);
		return a;
	}


	@Override
	public float getHeight() {
		return SIZE;
	}


	@Override
	public float getWidth() {
		return SIZE;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {
		anim.draw(getX()-area.xOffs, getY()-area.yOffs, filter);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		if (!isDone()) {
			anim.update(delta);
			if (anim.isStopped()) {
				setDone(true);
			}
		}
	}


}
