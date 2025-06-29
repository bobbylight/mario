package org.fife.mario.enemy;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.CollisionResult;
import org.fife.mario.Constants;
import org.fife.mario.Fireball;
import org.fife.mario.Mario;
import org.fife.mario.Shell;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.level.Area;
import org.fife.mario.powerups.AbstractPowerUp;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A Koopa Troopa.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class KoopaTroopa extends Enemy {

    public static final int COLOR_GREEN = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_BLUE = 2;
    public static final int COLOR_YELLOW = 3;

    protected int frame;
    protected float ySpeed;
    private int color;
    protected boolean turning;

    static final int HEIGHT = 64;
    static final int WIDTH = 64;

    /**
     * Constructor.
     */
    public KoopaTroopa() throws SlickException {
        this(0, 0);
    }

    /**
     * Constructor.
     *
     * @param x The x-coordinate of the Koopa Troopa.
     * @param y The y-coordinate of the Koopa Troopa.
     */
    public KoopaTroopa(float x, float y) throws SlickException {
        this(null, x, y, COLOR_GREEN);
    }

    /**
     * Constructor.
     *
     * @param area
     * @param x
     * @param y
     * @param color
     * @throws SlickException
     */
    public KoopaTroopa(Area area, float x, float y, int color)
        throws SlickException {
        super(area, x, y);
        this.color = color;
        dir = LEFT;
    }

    @Override
    public boolean bump(Character ch) {
        try {
            float y = getY() + getHeight() - 32;
            Shell shell = new Shell(getX(), y, color);
            shell.bump(ch);
            area.addCharacter(shell);
        } catch (SlickException se) {
            se.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean collidedWith(Character ch) {

        if (ch instanceof AbstractPowerUp) {
            return false;
        } else if (ch instanceof Fireball) {
            float diff = getCenterX() - ch.getCenterX();
            int dir = diff < 0 ? LEFT : RIGHT;
            Animation anim = Shell.createDyingAnimation(getX(), getY(), dir, color);
            area.addTemporaryAnimation(anim);
            SoundEngine.get().play(SoundEngine.SOUND_KICK);
            return true;
        } else if (ch.isMoving()) {
            if (ch instanceof Shell) {
                float diff = getCenterX() - ch.getCenterX();
                int dir = diff < 0 ? LEFT : RIGHT;
                Animation anim = Shell.createDyingAnimation(getX(), getY(), dir, color);
                area.addTemporaryAnimation(anim);
                SoundEngine.get().play(SoundEngine.SOUND_KICK);
                return true;
            } else if (!isMoving()) {
                return false;
            } else if ((getDirection() == ch.getDirection()) ||
                (getDirection() == LEFT && getX() < ch.getX()) ||
                (getDirection() == RIGHT && getX() > ch.getX())) {
                return false;
            } else if (dir == LEFT) {
                dir = RIGHT;
                turning = true;
                frame = 0;
            } else { // dir==RIGHT
                dir = LEFT;
                turning = true;
                frame = 0;
            }
        } else { // !ch.isMoving()
            if (getDirection() == LEFT && ch.getX() < getX()) {
                dir = RIGHT;
                turning = true;
            } else if (getDirection() == RIGHT && getX() < ch.getX()) {
                dir = LEFT;
                turning = true;
            }
        }

        return false;

    }

    protected Character createStompReplacementCharacter() throws SlickException {
        float y = getY() + getHeight() - Shell.HEIGHT;
        return new Shell(getX(), y, color);
    }


    public int getColor() {
        return color;
    }


    /**
     * If this actor is flying, this method returns the upward acceleration of
     * this flying actor (i.e. this should be a negative value).  If this
     * actor is not flying, this method should return <code>0</code>.
     *
     * @return The y-acceleration of this actor.
     */
    protected float getFlightYAcceleration() {
        return 0;
    }

    @Override
    public float getHeight() {
        return HEIGHT;
    }

    @Override
    public float getHitMarginTop() {
        return 25;
    }

    @Override
    public float getHitMarginX() {
        return 20;
    }


    @Override
    protected int getSpriteSheet() {
        return SpriteSheetManager.SHEET_KOOPA_TROOPA;
    }

    @Override
    public float getWidth() {
        return WIDTH;
    }


    public float getXSpeed() {
        return turning ? 0 : (dir == LEFT ? -1 : 1);
    }

    @Override
    public boolean isMoving() {
        return !turning;
    }


    protected boolean isSmart() {
        return color != COLOR_GREEN;
    }

    @Override
    protected void setSSIndex(int row, int col) {
        super.setSSIndex(row + color * 2, col);
    }

    @Override
    protected boolean stompedOn(Mario mario) {
        mario.setYSpeed(-8);
        SoundEngine.get().play(SoundEngine.SOUND_STOMP);
        try {
            Character ch = createStompReplacementCharacter();
            area.addCharacter(ch);
        } catch (SlickException se) {
            se.printStackTrace();
        }
        return true;
    }


    @Override
    protected void updateImpl(GameContainer container, StateBasedGame game, int delta)
        throws SlickException {

        motion.set(0, 0);

        if (airborne) {
            updateFalling(container, game, delta, motion);
        } else {
            updateWalking(container, game, delta, motion);
        }

        frame++;
        if (turning) {
            if (frame <= 10) {
                setSSIndex(dir, 0);
            } else {
                turning = false;
                frame = 0;
                setSSIndex(dir, 1);
            }
        } else {
            if (frame <= 10) {
                setSSIndex(dir, 1);
            } else if (frame <= 20) {
                setSSIndex(dir, 2);
            } else {
                frame = 0;
                setSSIndex(dir, 1);
            }
        }

        CollisionResult cr = area.checkHittingWall(this, motion);
        if (airborne) {
            if (area.isTerrainBelow(this)) {
                airborne = false;
                setSSIndex(dir, 0);
                ySpeed = 0;
            } else if (area.isTerrainAbove(this)) {
                ySpeed = 0;
            }
        } else if (!area.isTerrainBelow(this)) {
            airborne = true;
            ySpeed = 0;
        }

        if (cr.leftWall) {
            dir = RIGHT;
            turning = true;
            frame = 0;
        } else if (cr.rightWall) {
            dir = LEFT;
            turning = true;
            frame = 0;
        }

    }


    public void updateFalling(GameContainer container, StateBasedGame game,
                              int delta, Vector2f motion) {

        motion.x = getXSpeed();

        if (ySpeed < 7) {
            ySpeed += Constants.GRAVITY + getFlightYAcceleration();
            ySpeed = Math.min(ySpeed, 7);
        }
        motion.y = ySpeed;

    }


    public void updateWalking(GameContainer container, StateBasedGame game,
                              int delta, Vector2f motion) throws SlickException {

        frame++;
        motion.x = getXSpeed();


//		if (turning) {
//			if (frame<=10) {
//				setSSIndex(dir, 0);
//			}
//			else {
//				turning = false;
//				frame = 0;
//				setSSIndex(dir, 1);
//			}
//		}
//		else {
        if (!turning) {

            // Red koopas know to turn around at edges to avoid falling.
            if (isSmart()) {
                moveX(motion.x); // Temporary
                boolean willFall = area.isAtAnEdge(this);
                moveX(-motion.x);
                if (willFall) {
                    motion.x = 0;
                    turning = true;
                    frame = 0;
                    dir = (dir + 1) % 2;
                    setSSIndex(dir, 1);
                    return;
                }
            }

//			if (frame<=10) {
//				setSSIndex(dir, 1);
//			}
//			else if (frame<=20) {
//				setSSIndex(dir, 2);
//			}
//			else {
//				frame = 0;
//				setSSIndex(dir, 1);
//			}

        }

    }


}
