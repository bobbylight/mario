package org.fife.mario;

import org.fife.mario.enemy.FlyingFish;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;


public final class SpriteSheetManager {

	public static final int SHEET_AXE				= 0;
	public static final int SHEET_BOWSER			= 1;
	public static final int SHEET_BOWSER_FLAME		= 2;
	public static final int SHEET_FIREBALL			= 3;
	public static final int SHEET_FISH				= 4;
	public static final int SHEET_GOOMBA			= 5;
	public static final int SHEET_KOOPA_TROOPA		= 6;
	public static final int SHEET_MARIO				= 7;
	public static final int SHEET_PIRANHA_PLANT		= 8;
	public static final int SHEET_PLATFORM			= 9;
	public static final int SHEET_POWER_UPS			= 10;
	public static final int SHEET_SHELL				= 11;
	public static final int SHEET_SPRINGBOARD		= 12;
	public static final int SHEET_TOAD				= 13;

	private SpriteSheet[] sheets;

	private static final SpriteSheetManager INSTANCE = new SpriteSheetManager();


	/**
	 * Constructor.
	 */
	private SpriteSheetManager() {

		SpriteSheet ss;
		sheets = new SpriteSheet[14];
		Color grayBG = new Color(192,192,192);

		try {

			ss = loadSheet("img/bowser.png", grayBG, 68,82, 2);
			sheets[SHEET_BOWSER] = ss;

			Image temp = ss.getSubImage(224,0, 32,66);
			SpriteSheet ss2 = new SpriteSheet(temp, 32,32, 2);
			sheets[SHEET_AXE] = ss2;

			temp = ss.getSubImage(156,224,100,32);
			ss2 = new SpriteSheet(temp, Springboard.WIDTH,Springboard.HEIGHT, 2);
			sheets[SHEET_SPRINGBOARD] = ss2;

			temp = ss.getSubImage(0,168, 198,30*2+2);
			ss2 = new SpriteSheet(temp, 48, 30, 2);
			sheets[SHEET_BOWSER_FLAME] = ss2;

			ss = loadSheet("img/goomba.png",
								new Color(0, 255, 255), 66,62, 2);
			sheets[SHEET_GOOMBA] = ss;

			ss = loadSheet("img/koopatroopa.png", grayBG, 64,64, 2);
			sheets[SHEET_KOOPA_TROOPA] = ss;

			ss = loadSheet("img/mario_wip.png", grayBG, Mario.WIDTH,Mario.HEIGHT, 2);
			sheets[SHEET_MARIO] = ss;

			temp = ss.getSubImage(0,384, Mario.WIDTH,Mario.HEIGHT);
			ss2 = new SpriteSheet(temp, Mario.WIDTH,Mario.HEIGHT, 2);
			sheets[SHEET_TOAD] = ss2;

			temp = ss.getSubImage(0, 448, (MovingPlatform.TILE_WIDTH+2)*3,MovingPlatform.HEIGHT);
			ss2 = new SpriteSheet(temp, MovingPlatform.TILE_WIDTH,MovingPlatform.HEIGHT, 2);
			sheets[SHEET_PLATFORM] = ss2;

			ss = loadSheet("img/piranha_plant.png", grayBG, 32,66, 2);
			sheets[SHEET_PIRANHA_PLANT] = ss;

			ss = loadSheet("img/power_ups.png", Color.red, 32,32, 2);
			sheets[SHEET_POWER_UPS] = ss;

			ss = loadSheet("img/shells.png", Color.red, 32,32, 2);
			sheets[SHEET_SHELL] = ss;

			temp = ss.getSubImage(68,0, (FlyingFish.SIZE+2)*6,FlyingFish.SIZE);
			ss2 = new SpriteSheet(temp, FlyingFish.SIZE,FlyingFish.SIZE, 2);
			sheets[SHEET_FISH] = ss;

			ss = loadSheet("img/fireball.png", grayBG, 16,16, 0);
			sheets[SHEET_FIREBALL] = ss;

		} catch (SlickException se) {
			se.printStackTrace();
			System.exit(0);
		}

	}


	public Image getImage(int sheet, int x, int y) {
		return sheets[sheet].getSubImage(x, y);
	}


	public SpriteSheet getSheet(int sheet) {
		return sheets[sheet];
	}


	/**
	 * Returns the singleton manager.
	 *
	 * @return The singleton sprite sheet manager.
	 */
	public static SpriteSheetManager instance() {
		return INSTANCE;
	}


	private SpriteSheet loadSheet(String img, Color trans, int tw, int th,
									int spacing) throws SlickException {
		Image temp = new Image(img, false, Image.FILTER_NEAREST, trans);
		return new SpriteSheet(temp, tw,th, spacing);
	}


}
