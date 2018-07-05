package org.fife.mario.anim;

import java.util.ArrayList;
import java.util.List;

import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.GameInfo;
import org.fife.mario.Mario;
import org.fife.mario.Position;
import org.fife.mario.SpriteSheetManager;
import org.fife.mario.WarpInfo;
import org.fife.mario.level.Area;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;


/**
 * An animation played when Mario warps through a pipe.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class WarpingAnimation extends Animation {

    private Character character;
    private Image img;
    private Direction dir;
    private WarpInfo info;
    private int totalDelta;
    private int pixelsExposed;
    private boolean goingOut;
    private int imgX;
    private int imgY;

    private static final float MAX_DELTA = 1000f;


    public WarpingAnimation(Character ch, float x, float y, int imgX, int imgY,
                            WarpInfo info, Direction dir) {
        this(ch, x, y, imgX, imgY, info, dir, false);
    }


    public WarpingAnimation(Character ch, float x, float y, int imgX, int imgY,
                            WarpInfo info, Direction dir, boolean goingOut) {
        super(x, y);
        character = ch;
        SpriteSheet ss = SpriteSheetManager.instance().
            getSheet(SpriteSheetManager.SHEET_MARIO);
        img = ss.getSubImage(imgX, imgY);

        // HACK: When going "up" into a pipe, (ch.y+ch.hitMarginTop) is the
        // pixel that hit the top of the pipe.  We need to "slide" this
        // sub-image into the pipe, not the whole thing...
        if (dir == Direction.UP && !goingOut) {
            int hitMarginTop = (int)ch.getHitMarginTop();
            img = img.getSubImage(0, hitMarginTop,
                img.getWidth(), img.getHeight() - hitMarginTop);
            moveY(hitMarginTop);
        }

        this.info = info;
        this.imgX = imgX;
        this.imgY = imgY;
        this.dir = dir;
        this.goingOut = goingOut;

        if (goingOut && character instanceof Mario) { // HACK!!
            boolean airborne = false;
            ((Mario)character).stopMoving(imgX, imgY, airborne);
        }

    }


    @Override
    public Character dispose(Mario mario) {

        if (goingOut) {
            character.setActive(true);
            if (dir == Direction.UP) {
                moveY(-character.getHeight());
            } else if (dir == Direction.LEFT) {
                moveX(-character.getWidth());
            }
            character.setLocation(getX(), getY());
        } else {
            Level level = GameInfo.get().getLevel();
            // Don't reset the area (and the music!) if the area is the same.
            if (!info.getDestArea().equals(level.getCurrentAreaName())) {
                Area area = level.setCurrentArea(info.getDestArea());
                character.setArea(area);
                SoundEngine.get().playMusic(area.getMusic(), true);
            }
            SoundEngine.get().play(SoundEngine.SOUND_WARP);
            // getReplacementAnimations() will return new warp animation
        }

        return null;

    }


    @Override
    public float getHeight() {
        return img.getHeight();
    }


    @Override
    public List<Animation> getReplacementAnimations() {

        List<Animation> anims = null;

        if (!goingOut) {
            Position pos = info.getStartPosition();
            float x = pos.getCol() * 32;
            float y = pos.getRow() * 32;
            WarpingAnimation anim = new WarpingAnimation(character, x, y,
                imgX, imgY, info, null, true);
            anims = new ArrayList<>(1);
            anims.add(anim);
// Temporarily set character's location "close enough" to where it will be,
// so that a piranha plant isn't coming out as Mario is too.
            character.setLocation(x, y);
        }

        return anims;

    }


    @Override
    public float getWidth() {
        // this gets called through super() in constructor (via setX()), before
        // img is set.
        return img == null ? 0 : img.getWidth();
    }


    @Override
    protected void renderImpl(GameContainer container, StateBasedGame game,
                              Graphics g, Color filter) throws SlickException {

        if (goingOut) {
            switch (dir) {
                case DOWN:
                    pixelsExposed = (int)(img.getHeight() * (totalDelta / MAX_DELTA));
                    float x = getX() - area.xOffs;
                    float y = getY() - area.yOffs;
                    img.draw(x, y, x + img.getWidth(), y + pixelsExposed,
                        0, img.getHeight() - pixelsExposed, img.getWidth(), img.getHeight(), filter);
                    break;
                case UP:
                    pixelsExposed = (int)(img.getHeight() * (totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs;
                    y = getY() - area.yOffs - pixelsExposed;
                    img.draw(x, y, x + img.getWidth(), y + pixelsExposed,
                        0, 0, img.getWidth(), pixelsExposed, filter);
                    break;
                case LEFT:
                    pixelsExposed = (int)(img.getWidth() * (totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs - pixelsExposed;
                    y = getY() - area.yOffs;
                    img.draw(x, y, x + pixelsExposed, y + img.getHeight(),
                        0, 0, pixelsExposed, img.getHeight(), filter);
                    break;
                case RIGHT:
                    pixelsExposed = (int)(img.getWidth() * (totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs;
                    y = getY() - area.yOffs;
                    img.draw(x, y, x + pixelsExposed, y + img.getHeight(),
                        img.getWidth() - pixelsExposed, 0, img.getWidth(), img.getHeight(), filter);
                    break;
            }
        } else {
            switch (dir) {
                case DOWN:
                    pixelsExposed = img.getHeight() - (int)(img.getHeight() * (totalDelta / MAX_DELTA));
                    float x = getX() - area.xOffs;
                    float y = getY() - area.yOffs - pixelsExposed;
                    img.draw(x, y, x + getWidth(), y + pixelsExposed,
                        0, 0, getWidth(), pixelsExposed, filter);
                    break;
                case UP:
                    pixelsExposed = img.getHeight() - (int)(img.getHeight() * (totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs;
                    y = getY() - area.yOffs;
                    img.draw(x, y, x + img.getWidth(), y + pixelsExposed,
                        0, img.getHeight() - pixelsExposed, img.getWidth(), img.getHeight(), filter);
                    break;
                case LEFT:
                    // Shave 1 "hit margin" off since his hit bounds are touching the pipe
                    float temp = character.getWidth() - character.getHitMarginX();
                    pixelsExposed = (int)(temp * (1 - totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs;
                    y = getY() - area.yOffs;
                    img.draw(x, y, x + pixelsExposed, y + getHeight(),
                        img.getWidth() - pixelsExposed, 0, img.getWidth(), getHeight(), filter);
                    break;
                case RIGHT:
                    // Shave 1 "hit margin" off since his hit bounds are touching the pipe
                    temp = character.getWidth() - character.getHitMarginX();
                    pixelsExposed = (int)(temp * (1 - totalDelta / MAX_DELTA));
                    x = getX() - area.xOffs - pixelsExposed;
                    y = getY() - area.yOffs;
                    img.draw(x, y, x + pixelsExposed, y + getHeight(),
                        0, 0, pixelsExposed, getHeight(), filter);
                    break;
            }
        }

    }


    @Override
    public void setArea(Area area) {

        // NOTE: For "goingOut" animations, this will be called twice, once
        // when it's added to the "current" area, and then again when it's
        // re-parented to the "new" area.  This is really bad design...

        super.setArea(area);

        if (!goingOut) {
//            if (dir == Direction.UP) {
//				moveY(32); // "bottom" of pipe
//            }
            /*else*/
            if (dir == Direction.LEFT) {
                moveX(32); // "right" side of pipe
            }
        } else { // e.g. goingOut - must determine direction

            int row = area.viewToRow(getY());
            int col = area.viewToCol(getX());

            if (area.isPipeSide(row + 1, col)) {
                dir = Direction.UP;
                moveX(32 - character.getWidth() / 2);// Center coming out of pipe
            } else if (area.isPipeSide(row - 1, col)) {
                dir = Direction.DOWN;
                moveY(32);
                moveX(32 - character.getWidth() / 2);// Center coming out of pipe
            } else if (area.isPipeSide(row, col - 1)) {
                dir = Direction.RIGHT;
                moveY(-32);
                moveX(32);
            } else { // col+1
                dir = Direction.LEFT;
                moveY(-32);
            }

        }

        area.centerAround(this);

    }


    public void setGoingOut(boolean goingOut) {
        this.goingOut = goingOut;
    }


    @Override
    protected void updateImpl(GameContainer container, StateBasedGame game,
                              int delta) throws SlickException {
        totalDelta += delta;
        if (totalDelta >= MAX_DELTA) {
// TODO: This is the better conditional, make it work for all cases
//if ((pixelsExposed>=character.getHeight()-character.getHitMarginTop()) {
            setDone(true);
        }
    }


    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }


}
