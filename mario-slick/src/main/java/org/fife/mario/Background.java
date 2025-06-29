package org.fife.mario;

import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The background of a Mario level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Background {

	private Image[] images;
	private int index;
	private long totalDelta;
	private int[] indexArray;
	private float xOffs;
	private float yOffs;

	private static final int[] THREE_IMAGES_INDEX_ARRAY		= { 0, 1, 2, 1, };
	private static final int[] FOUR_IMAGES_INDEX_ARRAY		= { 0, 1, 2, 3, };

	private static final int TICK_TIME				= 250;


	public int getHeight() {
		return images[0].getHeight();
	}


	public float getXOffs() {
		return xOffs;
	}


	public float getYOffs() {
		return yOffs;
	}


	/**
	 * Renders the scrolling background.
	 *
	 * @param container
	 * @param game
	 * @param g
	 * @param filter
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g,
								Color filter) {

		// Only happens if we had an IO error loading background images
		if (images==null) {
			return;
		}
		Image bg = images.length==1 ? images[0] : images[indexArray[index]];
		float screenW = container.getWidth();
		float screenH = container.getHeight();
		float y = bg.getHeight() - container.getHeight();
		float xOffs = this.xOffs / 2; // Parallax
		if (xOffs==0) {
			g.drawImage(bg, 0,0,screenW,screenH, 0,y,screenW,y+screenH, filter);
		}

		else {

			// Draw "end" of image
			float srcX = xOffs % bg.getWidth();
			float srcY = y;
			float srcX2 = srcX + screenW;
			if (srcX2>bg.getWidth()) {
				srcX2 = bg.getWidth();
			}
			float w = srcX2 - srcX;
			float srcY2 = y + screenH;
			g.drawImage(bg, 0,0,w,screenH, srcX,srcY,srcX2,srcY2, filter);

			// Draw wrap-around start of image, if necessary
			float x = srcX2 - srcX;
			if (x<screenW) {
				srcX = 0;
				srcX2 = screenW-x;
				w = srcX2;
				g.drawImage(bg, x,0,x+w,screenH, srcX,srcY,srcX2,srcY2, filter);
			}
		}

	}


	public void setImage(String name) {

// Destroying the images here seems to be permanent.  Re-creating new Image's
// results in a solid white Image, guess the texture caching is messing things
// up?
//		if (images!=null) {
//			for (int i=0; i<images.length; i++) {
//				try {
//					images[i].destroy();
//				} catch (SlickException se) {
//					se.printStackTrace();
//				}
//			}
//		}
		images = null;
		index = 0;

		String resName = "img/bg_" + name + ".png";
		ClassLoader cl = getClass().getClassLoader();

		InputStream in = cl.getResourceAsStream(resName);
System.err.println(" --- --- " + resName + " => " + in);
		if (in!=null) {
			try {
				images = new Image[1];
				images[0] = new Image(in, resName, false);
			} catch (SlickException se) {
				se.printStackTrace();
				images = null;
			}
		}
		else {
			String base = "img/bg_" + name;
			int count = 0;
			while (cl.getResource(base + "_" + (count+1) + ".png")!=null) {
				count++;
			}
			// Only 3- and 4-frames of animation are supported
			if (count==3 || count==4) {
				try {
					images = new Image[count];
					for (int i=0; i<count; i++) {
						images[i] = new Image(base + "_" + (i+1) + ".png");
					}
					indexArray = count==3 ? THREE_IMAGES_INDEX_ARRAY :
											FOUR_IMAGES_INDEX_ARRAY;
				} catch (SlickException se) {
					se.printStackTrace();
					images = null;
				}
			}
		}

	}


	public void setOffset(float xOffs, float yOffs) {
		this.xOffs = xOffs;
		this.yOffs = yOffs;
	}


	/**
	 * Called each frame, so the entity can update itself.
	 *
	 * @param container
	 * @param game
	 * @param delta
	 * @throws SlickException
	 */
	public void update(GameContainer container, StateBasedGame game,
						int delta) throws SlickException {
		if (images.length>1) {
			totalDelta += delta;
			while (totalDelta>TICK_TIME) {
				totalDelta -= TICK_TIME;
				index = (index+1) % indexArray.length;
			}
		}
	}


}
