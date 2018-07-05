package org.fife.mario;

import org.fife.mario.anim.MarioDyingAnimation;
import org.fife.mario.anim.PoofAnimation;
import org.fife.mario.anim.WarpingAnimation;
import org.fife.mario.anim.WarpingAnimation.Direction;
import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The Mario sprite.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Mario extends Character {

	public static final int HEIGHT		= 62;
	public static final int WIDTH		= 40;

	private boolean running;
	private boolean ducking;
	private boolean throwingFireball;
	private int fireballFrame;
	private int strokeFrame;
	private int frame;
	private float xSpeed;
	private float ySpeed;
	private MarioState state;
	private float gettingSmallerFrame;
	private float blinkingFrame;
	private boolean completedLevel;

	/**
	 * If Mario is jumping, this boolean is set to whether the B button has
	 * been held down for the length of the entire jump.  When B is held down,
	 * jumps are higher and go farther.  Once B is released, jumps go back to
	 * going "regular" distance.
	 */
	private boolean fastJump;

	private static final float MAX_X_SPEED_WALKING		= 3;
	private static final float MAX_X_SPEED_RUNNING		= 5;

	private static final float X_DELTA_WALKING					= 0.2f;
	private static final float X_DELTA_RUNNING					= 0.25f;
	private static final float X_DELTA_SLOWDOWN_DUCKING			= 0.15f;
	private static final float X_DELTA_SLOWDOWN_WALKING			= 0.12f;
	private static final float INITIAL_RUNNING_JUMP_VELOCITY	= -8;
	private static final float INITIAL_WALKING_JUMP_VELOCITY	= -7;

	private static final int MAX_BLINKING_FRAME					= 180;
	private static final int MAX_GETTING_SMALLER_FRAME			= 60;

	private static Mario mario;


	static {
		try {
			mario = new Mario();
		} catch (SlickException se) {
			se.printStackTrace();
			System.exit(0);
		}
	}


	public Mario() throws SlickException {
		reset(true);
	}


	@Override
	public boolean bump(Character ch) {
		// Never happens
		return false;
	}


	@Override
	public boolean collidedWith(Character ch) {
		return false; // Never happens
	}


	@Override
	public boolean collidedWithMario(Mario mario) {
		return false;
	}


	public Animation createDyingAnimation() throws SlickException {
		return new MarioDyingAnimation(area, getX(), getY(), this);
	}


	private WarpingAnimation createWarpingAnimation(WarpInfo info, Direction dir) {
		int imgX = 0;
		int imgY = getState()==MarioState.FIRE ? 4 : 0;
		if (getState()==MarioState.SMALL) {
			imgY += 2;
		}
		imgY += getDirection();
		float x;
		float y = getY();
		switch (dir) {
			default:
			case DOWN:
				int col = area.viewToCol(getX() + getHitMarginX());
				float pipeMiddle = (col+1) * 32;
				x = pipeMiddle - getWidth()/2;
				y += getHeight();
				break;
			case UP:
				col = area.viewToCol(getX() + getHitMarginX());
				pipeMiddle = (col+1) * 32;
				x = pipeMiddle - getWidth()/2;
				break;
			case LEFT:
				col = area.viewToCol(getX() + getHitMarginX() - 1);
				x = col*32;
				break;
			case RIGHT:
				col = area.viewToCol(getX() + getWidth() - getHitMarginX());
				x = col*32;
				break;
		}
		return new WarpingAnimation(this, x,y, imgX,imgY, info, dir);
	}


	public void die(boolean showAnimation) throws SlickException {
		setDone(true);
		if (showAnimation) {
			Animation anim = createDyingAnimation();
			area.addTemporaryAnimation(anim);
		}
		SoundEngine.get().playMusic(SoundEngine.MUSIC_MARIO_DIES, false);
	}


	public static Mario get() {
		return mario;
	}


	@Override
	public float getHeight() {
		return HEIGHT;
	}


	@Override
	public float getHitMarginTop() {
		if (state==MarioState.SMALL) {
			return isDucking() ? 36 : 26;
		}
		return isDucking() ? 34 : 8;
	}


	@Override
	public float getHitMarginX() {
		return 8;
	}


	/**
	 * Returns the row in Mario's sprite sheet for his current sprite.  This
	 * can be used by animations to use the proper frame for an animation
	 * involving Mario.
	 *
	 * @return The row in the sprite sheet.
	 */
	public int getImageRow() {
		return (state!=null ? state.getIndex()*2 : 2) + dir;
	}


	@Override
	protected int getSpriteSheet() {
		return SpriteSheetManager.SHEET_MARIO;
	}


	public MarioState getState() {
		return state;
	}


	@Override
	public float getWidth() {
		return WIDTH;
	}


	public float getMaxXSpeed() {
		float xSpeed = running ? MAX_X_SPEED_RUNNING :
						(isMoving() ? MAX_X_SPEED_WALKING : 0);
		if (dir==LEFT) {
			xSpeed = -xSpeed;
		}
		return xSpeed;
	}


	public float getXSpeed() {
		return xSpeed;
	}


	public float getYSpeed() {
		return ySpeed;
	}


	/**
	 * Returns whether Mario is "blinking" from getting hit.  If this is
	 * <code>true</code> then Mario cannot die from touching an enemy.
	 *
	 * @return Whether Mario is blinking.
	 * @see #isChangingSize()
	 */
	public boolean isBlinking() {
		return blinkingFrame>0;
	}


	/**
	 * Returns whether Mario is changing size (either growing or shrinking).
	 * If this is happening, the rest of the action is frozen.
	 *
	 * @return Whether Mario is changing size.
	 * @see #isBlinking()
	 */
	public boolean isChangingSize() {
		return gettingSmallerFrame>0;
	}


	@Override
	public boolean isDone() {
		return state==null;
	}


	public boolean isDucking() {
		return ducking;
	}


	@Override
	public boolean isMoving() {
		return isAirborne() || isWalking();
	}


	public boolean isTurning() {
		return !isAirborne() &&
				((dir==LEFT && xSpeed>0) || (dir==RIGHT && xSpeed<0));
	}


	public boolean isWalking() {
		return !isAirborne() && xSpeed!=0;
	}


	public void jump() {
		if (getStandingOn() instanceof Springboard) {
			return;
		}
		jump(1, true);
	}


	public void jump(float factor, boolean playSound) {
		if (area.isWater()) {
			airborne = true;
			ySpeed = INITIAL_WALKING_JUMP_VELOCITY;
			frame = 0; // Force consistent walk frame on landing
			if (playSound) {
				SoundEngine.get().play(SoundEngine.SOUND_SWIM);
			}
			resetEntitiesStandingOnInfo();
			fastJump = false;
			strokeFrame = 15;
		}
		else if (!airborne) {
			airborne = true;
			ySpeed = (running && isWalking()) ?
									INITIAL_RUNNING_JUMP_VELOCITY :
									INITIAL_WALKING_JUMP_VELOCITY;
			ySpeed *= factor;
			frame = 0; // Force consistent walk frame on landing
			if (playSound) {
				SoundEngine.get().play(SoundEngine.SOUND_JUMP);
			}
			resetEntitiesStandingOnInfo();
			fastJump = running;
		}
	}

	private void moveLeft() {
		dir = LEFT;
		boolean fast = isAirborne() ? fastJump : running;
		float delta = fast ? X_DELTA_RUNNING : X_DELTA_WALKING;
		float min = -(fast ? MAX_X_SPEED_RUNNING : MAX_X_SPEED_WALKING);
		if (xSpeed<min) { // Going from running to walking
			xSpeed = Math.min(xSpeed+delta, min);
		}
		else { // xSpeed>=min, going from stopped to walking/walking to running
			xSpeed = Math.max(xSpeed-delta, min);
		}
	}


	private void moveRight() {
		dir = RIGHT;
		if (completedLevel) {
			xSpeed = 0.65f;
		}
		else {
			boolean fast = isAirborne() ? fastJump : running;
			float delta = fast ? X_DELTA_RUNNING : X_DELTA_WALKING;
			float max = fast ? MAX_X_SPEED_RUNNING : MAX_X_SPEED_WALKING;
			if (xSpeed<max) { // Going from stopped to walking/walking to running
				xSpeed = Math.min(xSpeed+delta, max);
			}
			else { // xSpeed>=max
				xSpeed = Math.max(xSpeed-delta, max);
			}
		}
	}


	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
						Graphics g, Color filter) throws SlickException {

		if (isDone()) {
			return;
		}

		if (!isChangingSize() && blinkingFrame>0) {
			if ((blinkingFrame%20)<10) {
				return;
			}
		}

		super.renderImpl(container, game, g, filter);

	}


	public void reset(boolean stateToo) {
		clearEntitiesStandingOn();
		resetEntitiesStandingOnInfo();
		frame = 0;
		running = false;
		ducking = false;
		throwingFireball = false;
		fireballFrame = 0;
		strokeFrame = 0;
		dir = RIGHT;
		xSpeed = ySpeed = 0;
		gettingSmallerFrame = 0;
		blinkingFrame = 0;
		if (stateToo || state==null) {
			// After dying, state==null, but we have to have a state...
			state = MarioState.SMALL;
		}
		setSpriteFrame(0); // After dir and state are set
	}


	public void setCompletedLevel(boolean completed) {
		completedLevel = completed;
	}


	@Override
	public void setDone(boolean done) {
		setState(null);
		clearEntitiesStandingOn();
	}


	public void setDucking(boolean ducking) {
		this.ducking = ducking;
	}


	public void setRunning(boolean running) {
		this.running = running;
	}


	private void setSpriteFrame(int index) {
		int col = index;
		int row = getImageRow();
		setSSIndex(row, col);
	}


	public void setState(MarioState state) {

		if (gettingSmallerFrame>0 || state==this.state) {
			return;
		}

		// TODO: Add the animation for other states.
		if (state!=null) {
			switch (state) {
				case SMALL:
					gettingSmallerFrame = MAX_GETTING_SMALLER_FRAME;
					blinkingFrame = MAX_BLINKING_FRAME;
					SoundEngine.get().play(SoundEngine.SOUND_SHRINK);
					break;
				case BIG:
					int sound = SoundEngine.SOUND_GROW;
					if (this.state==MarioState.SMALL) {
						gettingSmallerFrame = MAX_GETTING_SMALLER_FRAME;
					}
					else {
						blinkingFrame = MAX_BLINKING_FRAME;
						sound = SoundEngine.SOUND_SHRINK;
					}
					SoundEngine.get().play(sound);
					break;
				case FIRE:
					SoundEngine.get().play(SoundEngine.SOUND_GROW);
					break;
				case CAPE:
					// TODO: Implement cape
					break;
			}
		}

		this.state = state;

	}


	/**
	 * Overridden to scroll the level when Mario moves.
	 */
	@Override
	public void setX(float x) {

		if (x==getX()) {
			return;
		}

		//super.setX(x);
		x = Math.max(x, 0);
		super.setX(x);
		if (area!=null) {
			setX(Math.min(getX(), area.getWidth()-getWidth()));
			area.centerAround(this);
		}

	}


	/**
	 * Sets Mario's y-speed.  This is used to bounce him off of enemies or
	 * other objects.
	 *
	 * @param ySpeed The new y-speed.
	 */
	public void setYSpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}


	/**
	 * "Shrinks" Mario as a result of him hitting an enemy, a spike, etc.
	 *
	 * @return Whether Mario should die (i.e, he is already small Mario).
	 */
	public boolean shrink() {
		if (gettingSmallerFrame>0) {
			return false;
		}
		setState(state.getNext());
		return getState()==null; // dead
	}


	/**
	 * Changes Mario's x-speed to be closer to 0.  This is called via
	 * {@link #updateImpl(GameContainer, StateBasedGame, int)} if the user is not
	 * pressing left or right and Mario is moving.
	 */
	private void slowDown() {
		float delta = isDucking() ? X_DELTA_SLOWDOWN_DUCKING :
								X_DELTA_SLOWDOWN_WALKING;
		if (xSpeed>0) {
			xSpeed = Math.max(xSpeed-delta, 0);
		}
		else if (xSpeed<0) {
			xSpeed = Math.min(xSpeed+delta, 0);
		}

	}


	/**
	 * Causes Mario to lose his momentum.  Can also toggle his "airborne"
	 * status.
	 *
	 * @param airborne Whether Mario's airborne status should be set to
	 *        <code>true</code>.
	 */
	public void stopMoving(int imgX, int imgY, boolean airborne) {
		xSpeed = ySpeed = 0;
		this.airborne = airborne;
		if (!airborne) {
			setSSIndex(imgY, imgX);
		}
	}


	/**
	 * Throws a fireball.
	 */
	private void throwFireball() {

		float x = getX();
		if (dir==LEFT) {
			x += getHitMarginX() - Fireball.SIZE;
		}
		else {
			x += getWidth() - getHitMarginX();
		}
		float y = getY() + getHeight() / 2;

		// Create the fireball, and give it a dummy initial location, so we can
		// check if it's hitting something right when thrown.
		Fireball ball = new Fireball(getDirection());
		ball.setLocation(x, y);
		SoundEngine.get().play(SoundEngine.SOUND_FIREBALL);

		int row = area.viewToRow(ball.getY());
		int col = area.viewToCol(dir==LEFT ? ball.getX() : ball.getX() + ball.getWidth());
		if (area.isSolidTerrainOrBlock(row, col)) {
			x = dir==LEFT ? ball.getX() : ball.getX() + ball.getWidth();
			PoofAnimation poofAnim = new PoofAnimation(x, ball.getY());
			area.addTemporaryAnimation(poofAnim);
			SoundEngine.get().play(SoundEngine.SOUND_HIT_HEAD);
		}
		else {
			area.addCharacter(ball);
		}

		throwingFireball = true;
		fireballFrame = 0;
		strokeFrame = 0;

	}

private Vector2f foo = new Vector2f();
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
						throws SlickException {

		if (isDone()) {
			return;
		}

		if (isChangingSize()) {
			int row = getSSRow();
			if ((--gettingSmallerFrame%10)==0) {
				row = (row+2) % 4; // Oscillate from big to small
				setSSIndex(row, getSSCol());
			}
			return;
		}
		if (blinkingFrame>0) {
			blinkingFrame--;
		}

		Input input = container.getInput();

		if (completedLevel) {
			setRunning(false);
			moveRight(); // TODO: Move direction towards "end" of level.
		}

		else {

			boolean running = input.isKeyDown(Input.KEY_Z);

			setRunning(running);
			if (!isAirborne()) {
				boolean down = input.isKeyDown(Input.KEY_DOWN);
				setDucking(down);
				if (down) {
					WarpInfo info = area.isWarpBelowPoint(getBottomLeft(foo));
					if (info!=null) {
						setActive(false);
						xSpeed = ySpeed = 0;
						WarpingAnimation anim = createWarpingAnimation(info, Direction.DOWN);
						SoundEngine.get().play(SoundEngine.SOUND_WARP);
						area.addTemporaryAnimation(anim);
					}
				}
			}
			if (input.isKeyPressed(Input.KEY_X)) {
				jump();
			}
			if (!throwingFireball && input.isKeyPressed(Input.KEY_Z) &&
					state==MarioState.FIRE) {
				throwFireball();
			}

			// Determine xSpeed.
			boolean movingHorizontally = false;
			boolean left = input.isKeyDown(Input.KEY_LEFT);
			boolean right = input.isKeyDown(Input.KEY_RIGHT);
			if (isAirborne() && !running) {
				fastJump = false;
			}
			if (isAirborne() || !ducking) { // Can move while ducking and jumping
				if (left) {
					moveLeft();
					movingHorizontally = true;
				}
				else if (right) {
					moveRight();
					movingHorizontally = true;
				}
			}
			if (left) {
				if (!isAirborne()) {
					WarpInfo info = area.isWarpLeftOfPoint(getBottomLeft(foo));
					if (info!=null) {
						setActive(false);
						xSpeed = ySpeed = 0;
						WarpingAnimation anim = createWarpingAnimation(info, Direction.LEFT);
						SoundEngine.get().play(SoundEngine.SOUND_WARP);
						area.addTemporaryAnimation(anim);
					}
				}
			}
			else if (right) {
				if (!isAirborne()) {
					WarpInfo info = area.isWarpRightOfPoint(getBottomRight(foo));
					if (info!=null) {
						setActive(false);
						xSpeed = ySpeed = 0;
						WarpingAnimation anim = createWarpingAnimation(info, Direction.RIGHT);
						SoundEngine.get().play(SoundEngine.SOUND_WARP);
						area.addTemporaryAnimation(anim);
					}
				}
			}
			if (isDucking() || !movingHorizontally) {
				slowDown();
			}

		}

		// Determine sprite (and possibly ySpeed)
		if (isAirborne()) {
			if (input.isKeyDown(Input.KEY_UP)) {
				WarpInfo info = area.isWarpAbovePoint(getTopLeft(foo));
				if (info!=null) {
					setActive(false);
					xSpeed = ySpeed = 0;
					WarpingAnimation anim = createWarpingAnimation(info, Direction.UP);
					SoundEngine.get().play(SoundEngine.SOUND_WARP);
					area.addTemporaryAnimation(anim);
				}
			}
			if (isDucking()) {
				setSpriteFrame(7);
			}
			else {
				if (area.isWater()) {
					setSpriteFrame(strokeFrame>0 && --strokeFrame>0 ? 9 : 8);
				}
				else { // Regular jump
					setSpriteFrame(ySpeed<0 ? 4 : 5);
				}
			}
			updateYSpeed(input);
		}
		else if (isDucking()) {
			setSpriteFrame(7);
		}
		else if (isTurning()) {
			setSpriteFrame(getState()==MarioState.SMALL ? 0 : 3);
		}
		else if (isWalking()) {
			if (running) {
				frame = (frame%13)+1;
				if (getState()==MarioState.SMALL) {
					setSpriteFrame(frame<6 ? 0 : 1);
				}
				else {
					if (frame<5) {
						setSpriteFrame(1);
					}
					else if (frame<10) {
						setSpriteFrame(2);
					}
					else {
						setSpriteFrame(0);
					}
				}
			}
			else {
				frame = (frame%26)+1;
				if (getState()==MarioState.SMALL) {
					setSpriteFrame(frame<12 ? 0 : 1);
				}
				else {
					if (frame<10) {
						setSpriteFrame(1);
					}
					else if (frame<20) {
						setSpriteFrame(2);
					}
					else {
						setSpriteFrame(0);
					}
				}
			}
		}
		else { // Not moving
			if (!completedLevel && input.isKeyDown(Input.KEY_UP)) {
				setSpriteFrame(6);
			}
			else if (!completedLevel && isDucking()) { // Needed when landing while ducking
				setSpriteFrame(7);
			}
			else {
				setSpriteFrame(0);
			}
		}

		motion.set(xSpeed, ySpeed);
		//System.out.println("- " + motion);
		Level level = GameInfo.get().getLevel();
		Area area = level.getCurrentArea();
		area.checkHittingWall(this, motion);
		if (airborne) {
			// Check y-speed too to prevent "sticking" inside block when jumping
			if (ySpeed>=0 && area.isTerrainBelow(this)) {
				airborne = false;
				if (isDucking()) {
					setSpriteFrame(7);
				}
				else {
					setSpriteFrame(0);
				}
				ySpeed = 0;
			}
			// Check y-speed too to prevent "sticking" to bottom of block when falling
			else if (ySpeed<0 && area.isTerrainAbove(this)) {
				if (isDucking()) {
					setSpriteFrame(7);
				}
				else {
					setSpriteFrame(5);
				}
				ySpeed = 0;
			}
		}
		else if (!area.isTerrainBelow(this) && getStandingOn()==null) {
			airborne = true;
			ySpeed = 0;
			setSpriteFrame(5);
		}

		// "Throwing fireball" overrides all other appearance.
		if (throwingFireball) {
			setSpriteFrame(airborne ? 8 : 11);
			if (++fireballFrame==6) {
				fireballFrame = 0;
				throwingFireball = false;
			}
		}

		area.checkGettingCoins(this);
		if (area.isTouchingLava(this)) {
			die(true);
		}

	}


	/**
	 * Called via {@link #updateImpl(GameContainer, StateBasedGame, int)} when
	 * Mario is airborne.
	 *
	 * @param input User input to poll.
	 */
	private void updateYSpeed(Input input) {

		final int maxYSpeed = area.isWater() ? 2 : 8;

		if (ySpeed<0) {
			if (input.isKeyDown(Input.KEY_X)) {
				ySpeed -= Constants.GRAVITY/2;
			}
			// TODO: Formalize "water" vs. "regular" physics?
			// These are randomly-guessed values really.
			if (area.isWater()) {
				ySpeed += Constants.GRAVITY;
			}
		}

		if (ySpeed<maxYSpeed) {
			ySpeed += Constants.GRAVITY;
			ySpeed = Math.min(ySpeed, maxYSpeed);
		}

		motion.y = ySpeed;

	}


}
