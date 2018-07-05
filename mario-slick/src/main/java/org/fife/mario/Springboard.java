package org.fife.mario;

import org.fife.mario.level.Area;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The springboard thingie that Mario can jump off.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Springboard extends Character {

	public static final int HEIGHT		= 32;
	public static final int WIDTH		= 32;

	/**
	 * How "compressed" the springboard is, from 0 to 2.
	 */
	private State compressionState;

	/**
	 * Used for animation.
	 */
	private int compressionTick;

	private float origY;


	/**
	 * Constructor.
	 *
	 * @param area
	 * @param x
	 * @param y
	 */
	public Springboard(Area area, float x, float y) {
		super(x, y + HEIGHT);
		origY = y;
		this.area = area;
		compressionState = State.IDLE;
	}


	@Override
	public boolean bump(Character ch) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Springboards do nothing when they collide with other entities.
	 *
	 * @return <code>false</code> always.
	 */
	@Override
	public boolean collidedWith(Character ch) {
		return false;
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		if (mario.isOnTopOf(this) && mario.isAirborne() &&
				mario.getYSpeed()>=0 && mario.getStandingOn()!=this) {

			// Reset back to uncompressed.  Note that bounces are too fast
			// for Mario to really jump onto a springboard when it is not
			// already re-idled.
			compressionState = State.IDLE;
			compressionTick = compressionState.getInitialTickCount();
			setY(origY);

			mario.setY(getY()+getHitMarginTop()-mario.getHeight());
			mario.setYSpeed(0);
			addEntityStandingOn(mario);

		}
		return false;
	}


	/**
	 * Returns the state of this spring board.
	 *
	 * @return How compressed this spring board is.
	 */
	public State getCompressionState() {
		return compressionState;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_SPRINGBOARD;
	}


	@Override
	public float getHeight() {
		// Unfortunately, in our super constructor, getHeight() is called
		return compressionState==null ? HEIGHT : compressionState.getHeight();
	}


	@Override
	public float getHitMarginTop() {
		// Spring board always moves itself so its top is at the right position
		return 0;
	}


	@Override
	public float getHitMarginX() {
		return 4;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	@Override
	public boolean isMoving() {
		return false;
	}


	@Override
	protected void renderImage(Image image, GameContainer container,
			StateBasedGame game, Graphics g, Color filter) throws SlickException {
		int col = getSSCol();
		if (col<2) {
			int y = 16 - 8*col;
			image = image.getSubImage(0, y, image.getWidth(), image.getHeight()-y);
		}
		super.renderImage(image, container, game, g, filter);
	}


	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		if (compressionTick>0) {
			compressionTick--;
			if (compressionTick==0) {
				float oldH = getHeight();
				compressionState = compressionState.getNextState();
				float h = getHeight();
				moveY(oldH - h); // Moves Mario, if he's on top
				if (compressionState!=State.IDLE) {
					compressionTick = compressionState.getInitialTickCount();
				}
				else {
					Mario mario = Mario.get();
					if (mario.isOnTopOf(this)) {
						float factor = 1;
						if (container.getInput().isKeyDown(Input.KEY_X)) {
							factor = 1.5f;
						}
						mario.jump(factor, false);
						SoundEngine.get().play(SoundEngine.SOUND_SPRING);
					}
				}
			}
		}

		setSSIndex(0, compressionState.getCol());

	}


	/**
	 * The state of a spring board.
	 */
	public enum State {

		IDLE(32, 2, 1),
		COMPRESSING(24, 1, 4),
		COMPRESSED(16, 0, 4),
		EXPANDING(24, 1, 2),
		EXPANDED(32, 2, 2);

		private int height;
		private int col;
		private int initialTickCount;

		State(int height, int col, int initialTickCount) {
			this.height = height;
			this.col = col;
			this.initialTickCount = initialTickCount;
		}

		private int getCol() {
			return col;
		}

		public int getHeight() {
			return height;
		}

		private int getInitialTickCount() {
			return initialTickCount;
		}

		public State getNextState() {
			State[] values = values();
			return values[(ordinal()+1)%values.length];
		}

	}


}
