package org.fife.mario.editor;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Utils} class.
 */
class UtilsTest {

	@Test
	void testCreateImageWithAlpha() {
		BufferedImage orig = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		orig.setRGB(0, 0, 0xFF0000); // Set a pixel to red
		BufferedImage result = Utils.createImageWithAlpha(orig);

		assertNotNull(result);
		assertEquals(BufferedImage.TYPE_INT_ARGB, result.getType());
		assertEquals(0xFFFF0000, result.getRGB(0, 0)); // Should be red with alpha
	}

	@Test
	void testGetVerticallyMirroredImage() {
		BufferedImage orig = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				orig.setRGB(x, y, (x + y) * 0x010101); // Set a gradient
			}
		}
		BufferedImage result = Utils.getVerticallyMirroredImage(orig);

		assertNotNull(result);
		assertEquals(10, result.getWidth());
		assertEquals(10, result.getHeight());

		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				assertEquals(orig.getRGB(x, 9 - y), result.getRGB(x, y));
			}
		}
	}
}
