package org.fife.mario;

import org.fife.mario.blocks.Block;
import org.fife.mario.level.Level;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


/**
 * The title screen.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TitleScreenState extends BasicGameState {

	private Level scrollingLevel;
	private int id;
	private Image titleImg;
	private Image bgImg;
	private int selection;
	private UnicodeFont font;
	private Color translucentGray;

	/**
	 * The scale of the "Super Mario World" text.
	 */
	private float titleScale;

	/**
	 * Whether the menu is visible.
	 */
	private boolean menuVisible;


	public TitleScreenState(int id) {
		this.id = id;
	}


	@Override
	public int getID() {
		return id;
	}


	@Override
	public void enter(GameContainer container, StateBasedGame game)
										throws SlickException {
		SoundEngine.get().playMusic(SoundEngine.MUSIC_TITLE_SCREEN, false);
		selection = 0;
		titleScale = -1f;
		menuVisible = false;
scrollingLevel.reset();
//scrollingLevel.getCurrentArea().xOffs = 0;
// Unfortunately, must do GameInfo's level too, as that's what AbstractEntities
// always think they're in.
GameInfo.get().getLevel().setCurrentArea("main");
GameInfo.get().getLevel().reset();
GameInfo.get().getLevel().getCurrentArea().xOffs = 0;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		scrollingLevel = new Level("levels/1-1.txt");
		scrollingLevel.reset();

		Color grayBG = new Color(192,192,192);
		Image img = new Image("img/hud.png", false, Image.FILTER_NEAREST,
								grayBG);
		titleImg = img.getSubImage(0,373, 417,139);
		//img.destroy();

		try {
			bgImg = new Image("img/title_screen.png"); // Testing
		} catch (SlickException se) {
			se.printStackTrace();
		}

		try {
			font = new UnicodeFont("fonts/smwtextfontpro.ttf", 24, false, false);
			font.getEffects().add(new ColorEffect(java.awt.Color.white));
			font.addAsciiGlyphs();
			font.loadGlyphs();
		} catch (SlickException se) {
			se.printStackTrace();
		}

		translucentGray = new Color(0,0,0, 0.85f);

	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {

		if (bgImg==null) {
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, container.getWidth(), container.getHeight());
		}
		else {
			scrollingLevel.getCurrentArea().render(container, game, g, null);
		}


		if (titleScale>0) {
			float x = -titleImg.getWidth()/2f;
			float y = -titleImg.getHeight()/2f;
			g.translate(container.getWidth()/2, 80);
			g.scale(titleScale, titleScale);
			g.drawImage(titleImg, x,y);
			g.resetTransform();
		}

		if (menuVisible) {
			renderMenu(container, game, g);
		}

		g.setFont(font);
		g.setColor(Color.white);

		String text = "2010 OutOnBail Games";
		int width = font.getWidth(text);
		float x = (container.getWidth()-width)/2f;
		float y = container.getHeight() - font.getLineHeight()*2;
		g.drawString(text, x, y);

		text = "All content is (C) Nintendo!";
		width = font.getWidth(text);
		x = (container.getWidth()-width)/2f;
		y += font.getLineHeight();
		g.drawString(text, x, y);

	}


	private void renderMenu(GameContainer container, StateBasedGame game,
							Graphics g) {

		g.setColor(translucentGray);
		float x = 120;
		float y = 280;
		g.fillRoundRect(x, y,
				container.getWidth()-x*2, container.getHeight()-y-100,
				10);

		g.setColor(Color.white);
		g.setFont(font);
		String choice1 = "Start Game!";
		String choice2 = "View High Scores";
		float textWidth = font.getWidth(choice2);
		x = (container.getWidth() - textWidth)/2;

		y = 300;
		g.drawString(choice1, x, y);
		if (selection==0) {
			g.drawString(">", x-32, y);
		}
		y += 32;
		g.drawString(choice2, x, y);
		if (selection==1) {
			g.drawString(">", x-32, y);
		}

	}


	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		if (titleScale<1f) {
			titleScale = Math.min(titleScale+0.03f, 1);
		}

		Block.updateAnimations(delta);
		scrollingLevel.getCurrentArea().update(container, game, delta);
		scrollingLevel.getCurrentArea().xOffs++;
		scrollingLevel.checkOtherEntityCollisions(container, game, delta);

		Input input = container.getInput();

		if (input.isKeyPressed(Input.KEY_X) ||
				input.isKeyPressed(Input.KEY_ENTER)) {

			SoundEngine.get().play(SoundEngine.SOUND_COIN);
			input.clearKeyPressedRecord();

			if (!menuVisible) {
				menuVisible = true;
			}
			else {
				if (selection==0) {
					PlayerInfo.get(0).reset();
					PlayerInfo.get(1).reset();
					SoundEngine.get().stopMusic();
					game.enterState(Constants.STATE_PRE_LEVEL, new FadeOutTransition(),
												new FadeInTransition());
				}
				else {
					game.enterState(Constants.STATE_CHECK_SCORES, null, null);
				}
			}

		}

		else if (menuVisible) {
			if (input.isKeyPressed(Input.KEY_Z)) {
				if (menuVisible) {
					menuVisible = false;
					selection = 0;
					SoundEngine.get().play(SoundEngine.SOUND_HIT_HEAD);
				}
			}
			else if (input.isKeyPressed(Input.KEY_UP) ||
					input.isKeyPressed(Input.KEY_DOWN)) {
				selection = (selection^1)&1;
				SoundEngine.get().play(SoundEngine.SOUND_FIREBALL);
			}
		}

	}


}
