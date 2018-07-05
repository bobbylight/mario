package org.fife.mario;

import org.fife.mario.level.Level;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * The game's "HUD".
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Hud {

	private Image hudImage;

	// Cached values
	private int lastRenderedCoinCount;
	private String coinCountStr;
	private int lastRenderedScore;
	private String scoreStr;
	private int lastRenderedTime;
	private String timeStr;

	private static final int DIGIT_WIDTH	= 16;


	public Hud() throws SlickException {
		Color grayBG = new Color(192,192,192);
		hudImage = new Image("img/hud.png", false, Image.FILTER_NEAREST, grayBG);
		lastRenderedCoinCount = -1;
		lastRenderedScore = -1;
		lastRenderedTime = -1;
	}


	public void render(GameContainer container, StateBasedGame game,
			Graphics g, PlayerInfo pi, Level level, int elapsedSeconds)
			throws SlickException {

		updateStrings(pi);

		int marginY = 20;
		int marginX = 30;

		// "MARIO" or "LUIGI"
		int x = marginX;
		int y = marginY;
		int w = 82;
		int h = 19;
		hudImage.draw(x, y, x + w, y + h, 3, 4, 3 + w, 4 + h);

		// The box holding the extra item
		x = (container.getWidth() - w) / 2;
		y = marginY - 10;
		w = 56;
		h = 57;
		hudImage.draw(x, y, x + w, y + h, 100, 2, 100 + w, 2 + h);

		// The player's time
		w = 52;
		h = 17;
		x = 380;
		y = marginY;
		hudImage.draw(x, y, x + w, y + h, 171, 4, 171 + w, 4 + h);
		int secs = level.getTotalTime() - elapsedSeconds;
		if (secs != lastRenderedTime) {
			lastRenderedTime = secs;
			timeStr = Integer.toString(lastRenderedTime);
		}
		x = x + w - timeStr.length() * DIGIT_WIDTH;
		y = y + y + 5;
		renderNumber(g, x, y, timeStr, true);

		// Coin count and score
		int rightX = container.getWidth() - marginX;
		y = marginY;
		x = rightX - 5 * DIGIT_WIDTH;
		//hudImage.draw(x,y, 173,21,173+32-1,21+16-1); // "Coin x" image
		w = 32; h = 16;
		hudImage.draw(x,y,x+w,y+h, 173,21,173+w,21+h);
		x = rightX - coinCountStr.length() * DIGIT_WIDTH;
		renderNumber(g, x, y, coinCountStr, false);
		x = rightX - scoreStr.length() * DIGIT_WIDTH;
		y += 20;
		renderNumber(g, x, y, scoreStr, false);

	}


	private void renderNumber(Graphics g, float x, float y, String num,
			boolean yellow) {

		int srcX = 0;
		int srcY = 66;
		if (yellow) {
			srcY = 86;
		}

		for (int i = 0; i < num.length(); i++) {
			int digit = num.charAt(i) - '0';
			srcX = digit * 18;
            hudImage.draw(x, y, x + DIGIT_WIDTH, y + 14, srcX, srcY, srcX + DIGIT_WIDTH, srcY + 14);
			x += DIGIT_WIDTH;
		}
	}


	private void updateStrings(PlayerInfo info) {

		int score = info.getScore();
		if (score!=lastRenderedScore) {
			scoreStr = Integer.toString(score);
			lastRenderedScore = score;
		}

		int coinCount = info.getCoinCount();
		if (coinCount!=lastRenderedCoinCount) {
			coinCountStr = Integer.toString(coinCount);
			lastRenderedCoinCount = coinCount;
		}

	}


}
