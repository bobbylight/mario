package org.fife.mario.anim;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.Mario;
import org.fife.mario.PowerUp;
import org.fife.mario.powerups.AbstractPowerUp;
import org.fife.mario.powerups.FireFlower;
import org.fife.mario.powerups.Mushroom;
import org.fife.mario.powerups.OneUp;
import org.fife.mario.powerups.Star;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;



/**
 * An animation of a power-up (mushroom, fire flower, etc.) coming out of a
 * question block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PowerUpAnimation extends Animation {

	private	PowerUp powerUp;
	private boolean goRight;
	private int totalDelta;
	private int pixelsExposed;

	private static final int MAX_DELTA			= 1000;
	private static final int DELTA_INC			= 66; // ~ 15 updates


	public PowerUpAnimation(float x, float y, PowerUp powerUp, boolean goRight){
		super(x, y);
		this.powerUp = powerUp;
		this.goRight = goRight;
		totalDelta = 0;
	}


	@Override
	public Character dispose(Mario mario) {

		AbstractPowerUp entity = null;

		switch (powerUp) {
			default:
			case MUSHROOM:
				entity = new Mushroom(getX(), getY() - getHeight(), goRight);
				break;
			case FIRE_FLOWER:
				entity = new FireFlower(getX(), getY()-getHeight());
				break;
			case STAR:
				entity = new Star(getX(), getY()-getHeight());
				break;
			case ONE_UP:
				entity = new OneUp(getX(), getY()-getHeight(), goRight);
				break;
		}

		entity.setArea(area);
		return entity;

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

		Image img = AbstractPowerUp.getImage(powerUp.getIndex());

		float x = getX() - area.xOffs;
		float y = getY() - pixelsExposed - area.yOffs;
		img.draw(x,y,x+getWidth(),y+pixelsExposed,
				0,0,getWidth(),pixelsExposed, filter);

	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
						throws SlickException {
		totalDelta += delta;
		pixelsExposed = (totalDelta/DELTA_INC) * 2;
		if (totalDelta>=MAX_DELTA) {
			setDone(true);
		}
	}


}
