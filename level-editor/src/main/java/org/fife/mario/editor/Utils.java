package org.fife.mario.editor;

import java.awt.image.BufferedImage;


/**
 * Obligatory static utility methods.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class Utils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() {
        // Do nothing (comment for Sonar)
    }

	/**
	 * Takes an image with no alpha channel and creates a new one from it,
	 * replacing all pixels with the color of pixel (0,0) with a zero-alpha
	 * pixel.
	 *
	 * @param orig The original image.
	 * @return The new image.
	 */
	public static BufferedImage createImageWithAlpha(BufferedImage orig) {
		return createImageWithAlpha(orig, -1, orig.getRGB(0,0));
	}



	/**
	 * Takes an image with no alpha channel and creates a new one from it,
	 * replacing a specific color with a zero-alpha pixel.
	 *
	 * @param orig The original image.
	 * @param h The size of the original image you want to grab vertically, or
	 *        <code>-1</code> for the entire original's height.
	 * @param c The color to replace.
	 * @return The new image.
	 */
	public static BufferedImage createImageWithAlpha(BufferedImage orig, int h,
													int c) {

		c = 0xff000000 | c; // Just make sure alpha is set.

		int width = orig.getWidth();
		int height = h==-1 ? orig.getHeight() : h;
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] data = new int[width];

		for (int y=0; y<height; y++) {
			// fetch a line of data from the image
			orig.getRGB(0, y, width, 1, data, 0, 1);
			for (int x=0; x<width; x++) {
				if (data[x]==c) {
					data[x] = 0x00000000;
				}
			}
			newImg.setRGB(0, y, width, 1, data, 0, 1);
		}

		return newImg;

	}



	/**
	 * Returns a vertically flipped version of an image.
	 *
	 * @param orig The original image.
	 * @return A vertically flipped version of the image.
	 */
	public static BufferedImage getVerticallyMirroredImage(BufferedImage orig) {
		int w = orig.getWidth();
		int h = orig.getHeight();
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
		int[] data = new int[w];
		for (int y=0; y<h; y++) {
			orig.getRGB(0,y, w,1, data, 0, 1);
			img.setRGB(0,h-1-y, w,1, data, 0, 1);
		}
		return img;
	}


}
