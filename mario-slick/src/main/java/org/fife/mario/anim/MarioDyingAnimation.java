package org.fife.mario.anim;

import org.fife.mario.Constants;
import org.fife.mario.GameState;
import org.fife.mario.Mario;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * An animation of Mario dying.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class MarioDyingAnimation extends org.fife.mario.Animation {

	private Mario mario;
	private Animation anim;
	private float ySpeed;

	private static final float HEIGHT		= 62;
	private static final float WIDTH		= 40;


	public MarioDyingAnimation(Area area, float x, float y,
								Mario mario) throws SlickException {

		super(x, y);
		setArea(area);
		ySpeed = -6;
		this.mario = mario;

		int sheet = SpriteSheetManager.SHEET_MARIO;
		Image img1 = SpriteSheetManager.instance().getImage(sheet, 11, 2);
		Image img2 = SpriteSheetManager.instance().getImage(sheet, 11, 3);
		anim = new Animation(true);
		anim.addFrame(img1, 200);
		anim.addFrame(img2, 200);
	}

	@Override
	public float getHeight() {
		return HEIGHT;
	}

	@Override
	public float getWidth() {
		return WIDTH;
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {
		g.drawAnimation(anim, getX()-area.xOffs, getY()-area.yOffs, filter);
		//anim.draw(getX(), getY());
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		ySpeed += Constants.GRAVITY/4;
		moveY(ySpeed);
//		y += delta/100000f;
//		if (y>=30000) {
//			setDone(true);
//		}

if (getY()>=area.yOffs+container.getHeight()) {
	System.out.println("Restarting at: " + getY());
	GameState gs = (GameState)game.getCurrentState();
	gs.marioDied(game, mario);
}
	}


}
