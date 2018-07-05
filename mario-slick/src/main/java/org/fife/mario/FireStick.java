package org.fife.mario;

import java.awt.geom.Rectangle2D;

import org.fife.mario.level.Area;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A stick of flames spinning around a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FireStick extends AbstractEntity {

	private float angle;
	private int frame;
	private Rectangle2D.Float[] fireballs;
	private float fireballSize;

	private static final float ANGLE_INC		= (float)Math.PI/90;

	public FireStick(float x, float y, Area area) {

		setLocation(x, y);
		this.area = area;
		angle = 0;

		SpriteSheetManager ssm = SpriteSheetManager.instance();
		Image img = ssm.getImage(SpriteSheetManager.SHEET_FIREBALL, 0,0);
		fireballSize = img.getWidth();

		fireballs = new Rectangle2D.Float[6];
		for (int i=0; i<fireballs.length; i++) {
			fireballs[i] = new Rectangle2D.Float();
			fireballs[i].width = fireballSize;
			fireballs[i].height = fireballSize;
		}

	}

	@Override
	public Rectangle2D.Float getCoreBounds() {
		bounds.x = getX() - fireballSize*fireballs.length;
		bounds.y = getY() - fireballSize*fireballs.length;
		bounds.y = getX() + getWidth() /*block*/ + fireballSize*fireballs.length;
		bounds.y = getY() + getHeight() /*block*/ + fireballSize*fireballs.length;
		return bounds;
	}

	@Override
	public float getHeight() {
		return 32;
	}

	@Override
	public float getHitMarginTop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHitMarginX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getWidth() {
		return 32;
	}

	@Override
	public boolean intersects(AbstractEntity entity) {
		Rectangle2D.Float eBounds = entity.getHitBounds();
		// Micro-optimization - mario is more likely (?) to hit a fireball
		// "farther out".
		for (int i=fireballs.length-1; i>=0; i--) {
			if (fireballs[i].intersects(eBounds)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game,
			Graphics g, Color filter) throws SlickException {

		// The fireballs in the stick rotate every 32 frames.
		SpriteSheetManager ssm = SpriteSheetManager.instance();
		int o = frame/8;
		Image img = ssm.getImage(SpriteSheetManager.SHEET_FIREBALL, o/2, o%2);

        for (Rectangle2D.Float fireball : fireballs) {
            float x = fireball.x - area.xOffs;
            float y = fireball.y - area.yOffs;
            g.drawImage(img, x, y);
        }

	}

	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		frame++;
		if (frame==32) {
			frame = 0;
		}

		// Every other frame, increment the angle of the fire stick.
		if ((frame&1)==0) {

			angle += ANGLE_INC;
			if (angle>=2*Math.PI) {
				angle -= 2*Math.PI;
			}

			float x0 = fireballSize/2; // center
			float cos = (float)Math.cos(angle);
			float sin = (float)Math.sin(angle);
			for (int i=0; i<fireballs.length; i++) {
				float cx = x0 + i*fireballSize;
				float cy = 0;
				float newCX = cx*cos - cy*sin;
				float newCY = cx*sin + cy*cos;
				fireballs[i].x = (getX()+16) + newCX - fireballSize/2;
				fireballs[i].y = (getY()+16) + newCY - fireballSize/2;
			}

		}

	}

}
