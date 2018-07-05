package org.fife.mario.enemy;

import org.fife.mario.Character;
import org.fife.mario.Mario;
import org.fife.mario.PlayerInfo;
import org.fife.mario.anim.PointsAnimation;
import org.fife.mario.level.Area;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;


/**
 * A monster.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class Enemy extends Character {

    private Vector2f tempPoint = new Vector2f();


    /**
     * Constructor.
     *
     * @param area
     * @param x
     * @param y
     * @throws SlickException
     */
    public Enemy(Area area, float x, float y) throws SlickException {
        super(x, y);
        setArea(area);
    }


    /**
     * Overridden to do nothing as Mario/enemy collisions are handled
     * in GameState.
     *
     * @return Whether this monster is dead after this collision.
     */
    @Override
    public boolean collidedWithMario(Mario mario) {

//		getHitBounds();

        // Determine whether Mario hit the "top" of the monster, or on
        // its side or bottom.
        float ySpeed = mario.getYSpeed();
        if (ySpeed > 0) { // Mario is going down
            if (mario.isOnTopOf(this)) {
                if (stompedOn(mario)) {
                    return true;
                }
                else if (!isLandable()) {
                    // Only get points if enemy doesn't die
                    area.addTemporaryAnimation(
                        new PointsAnimation(getX(), getY(), "200"));
                    PlayerInfo.get(0).incScore(200);
                    mario.translate(0, bounds.y - mario.getBottomCenter(tempPoint).y - 1); // "-1" important!
                }
            }
            return false;
        }

        if (isKickable()) {
            return kickedBy(mario);
        }

        if (!(mario.isBlinking() || mario.getStandingOn() == this)) {
            mario.shrink();
        }
        return false;

    }


    /**
     * Called when Mario stomps on an enemy.
     *
     * @param mario The guy who stomped on you.
     * @return Whether this enemy is now dead.
     */
    protected abstract boolean stompedOn(Mario mario);


}
