package org.fife.mario;

import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A KoopaTroopa's shell.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Shell extends Character {

    public static final int HEIGHT = 32;

    private int color;
    private float xSpeed;
    private float ySpeed;
    private int frame;
    private Vector2f tempPoint;


    /**
     * Constructor.
     *
     * @param x The x-coordinate of the shell.
     * @param y The y-coordinate of the shell.
     * @param color The color of the shell.
     */
    public Shell(float x, float y, int color) throws SlickException {
        setLocation(x, y);
        this.color = color;
        tempPoint = new Vector2f();
    }

    @Override
    public boolean bump(Character ch) {
        ySpeed -= 4;
        airborne = true;
        return false;
    }


    /**
     * Called when this object and another touch.
     *
     * @param ch The other character touched.
     * @return Whether this character should be removed.
     */
    @Override
    public boolean collidedWith(Character ch) {
        if (ch instanceof Shell || ch instanceof Fireball) {
            float diff = getCenterX() - ch.getCenterX();
            int dir = diff < 0 ? LEFT : RIGHT;
            Animation anim = Shell.createDyingAnimation(getX(), getY(), dir, color);
            area.addTemporaryAnimation(anim);
            return true;
        }
        return false;
    }


    @Override
    public boolean collidedWithMario(Mario mario) {

        if (mario.isBlinking()) { // If he's flickering from a previous hit
            return false;
        }

        if (xSpeed == 0) {
            float oldX = getX();
            kickedBy(mario);
            if (oldX != getX()) { // was kicked
                SoundEngine.get().play(SoundEngine.SOUND_KICK);
                if (mario.isAirborne()) {
                    mario.getBottomCenter(tempPoint);
                    mario.setYSpeed(-8);
                    getHitBounds();
                    mario.translate(0, bounds.y - tempPoint.y - 1); // "-1" important!
                }
            }
        } else { // Shell is in motion
            getHitBounds();
            // Determine whether Mario hit the "top" of the monster, or on
            // its side or bottom.
            float ySpeed = mario.getYSpeed();
            if (ySpeed > 0) { // Going down
                // Get "bottom center" point of Mario
                mario.getBottomCenter(tempPoint);
                if (bounds.contains(tempPoint.x, tempPoint.y)) {
                    xSpeed = 0;
                    mario.setYSpeed(-8);
                    SoundEngine.get().play(SoundEngine.SOUND_STOMP);
                    mario.translate(0, bounds.y - tempPoint.y - 1); // "-1" important!
                    return false;
                }
            }

//			System.out.println("Mario hit!");
            mario.shrink();

        }

        return false;

    }


    public static Animation createDyingAnimation(float x, float y, int dir,
                                                 int color) {
        SoundEngine.get().play(SoundEngine.SOUND_KICK);
        Image img = SpriteSheetManager.instance().getImage(
            SpriteSheetManager.SHEET_SHELL, 2, color);
        img = img.getFlippedCopy(false, true);
        Animation anim = new Shell.DyingAnimation(x, y, img);
        return anim;
    }


    @Override
    public float getHeight() {
        return HEIGHT;
    }


    @Override
    public float getHitMarginTop() {
        return 3;
    }


    @Override
    public float getHitMarginX() {
        return 2;
    }


    @Override
    protected int getSpriteSheet() {
        return SpriteSheetManager.SHEET_SHELL;
    }


    @Override
    public float getWidth() {
        return 32;
    }


    @Override
    public boolean isKickable() {
        return true;
    }

    @Override
    public boolean isMoving() {
        return xSpeed != 0 || ySpeed > 0;
    }


    @Override
    public boolean kickedBy(Character ch) {
        switch (ch.getDirection()) {
            case Character.LEFT:
                xSpeed = -5;
                moveX(-10);
                break;
            default: // RIGHT:
                xSpeed = 5;
                moveX(10);
                break;
        }
//		xSpeed = ch.getDirection()==Character.LEFT ? -5 : 5;
        SoundEngine.get().play(SoundEngine.SOUND_KICK);
        return false;
    }


    @Override
    protected void updateImpl(GameContainer container, StateBasedGame game, int delta) throws SlickException {

        motion.set(0, 0);

        if (airborne) {
            updateFalling(container, game, delta, motion);
        } else {
            updateWalking(container, game, delta, motion);
        }

        CollisionResult cr = area.checkHittingWall(this, motion);
        if (airborne) {
            if (area.isTerrainBelow(this)) {
                airborne = false;
                ySpeed = 0;
            } else if (area.isTerrainAbove(this)) {
                ySpeed = 0;
            }
        } else if (!area.isTerrainBelow(this)) {
            airborne = true;
            ySpeed = 0;
        }

        if (cr.leftWall || cr.rightWall) {
            xSpeed = -xSpeed;
        }

        int frameCol = xSpeed != 0 ? frame / 10 : 0;
        setSSIndex(color, frameCol);

    }


    public void updateFalling(GameContainer container, StateBasedGame game,
                              int delta, Vector2f motion) {

        motion.x = xSpeed;

        if (ySpeed < 7) {
            ySpeed += Constants.GRAVITY;
            ySpeed = Math.min(ySpeed, 7);
        }
        motion.y = ySpeed;

    }


    public void updateWalking(GameContainer container, StateBasedGame game,
                              int delta, Vector2f motion) throws SlickException {

        frame++;
        motion.x = xSpeed;

        if (frame >= 40) {
            frame = 0;
        }

    }


    public static class DyingAnimation extends Animation {

        private Image img;
        private float dy;

        public DyingAnimation(float x, float y, Image img) {
            super(x, y);
            this.img = img;
            dy = -4;
        }

        @Override
        public float getHeight() {
            return img.getHeight();
        }

        @Override
        public float getWidth() {
            // Gets called via super() in constructor, before img is set
            return img == null ? 0 : img.getWidth();
        }

        @Override
        protected void renderImpl(GameContainer container, StateBasedGame game,
                                  Graphics g, Color filter) throws SlickException {
            float x = getX() - area.xOffs;
            float y = getY() - area.yOffs;
            g.drawImage(img, x, y, filter);
        }

        @Override
        protected void updateImpl(GameContainer container, StateBasedGame game,
                                  int delta) throws SlickException {
            if (dy < 8) {
                dy += 0.2f;
                dy = Math.min(dy, 8);
            }
            moveY(dy);
            if (getY() > area.getHeight()) {
                setDone(true);
            }
        }

    }
}
