package org.fife.mario;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Main extends StateBasedGame {

	private Mario mario;

	private static Main instance;

	public Main() {

		super("Test");//Super Mario Demo");

		// NOTE: I know this is "bad," but we need to do it this way so that
		// the static instance points to the game singleton even when run in
		// a Slick applet.
		Main.instance = this;

	}

	public static Main get() {
		return instance;
	}

	public Mario getMario() {
		return mario;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {

		mario = Mario.get();

		addState(new OobgPresentsState(Constants.STATE_FIFESOFT_PRESENTS));
		addState(new TitleScreenState(Constants.STATE_TITLE_SCREEN));
		addState(new CheckScoresState(Constants.STATE_CHECK_SCORES));
		addState(new PreLevelState(Constants.STATE_PRE_LEVEL));
		addState(new GameState(Constants.STATE_PLAYING_GAME));
		addState(new TextBoxState(Constants.STATE_TEXT_BOX));
		addState(new GameOverState(Constants.STATE_GAME_OVER));
		addState(new LevelCompletedState(Constants.STATE_LEVEL_COMPLETED));
		addState(new BridgeCollapsingState(Constants.STATE_BRIDGE_COLLAPSING));
		addState(new CastleCompletedState(Constants.STATE_CASTLE_COMPLETED));

		container.setClearEachFrame(false);
container.setMultiSample(2);

	}

private static AppGameContainer app;
private boolean zoomed = false;
@Override
public void keyPressed(int key, char ch) {
	if (key==org.newdawn.slick.Input.KEY_F1) {
		zoomed = !zoomed;
		try {
			if (zoomed) {
//				app.setDisplayMode(1280, 960, false);
app.setFullscreen(true);
			}
			else {
//				app.setDisplayMode(1280, 960, false);
				app.setDisplayMode(640, 480, false);
			}
		} catch (SlickException se) {
			se.printStackTrace();
		}
	}
}

	public void restart() {

//		mario.reset();
		enterState(Constants.STATE_PRE_LEVEL, new FadeOutTransition(),
									new FadeInTransition());

//		// Don't reset the level until here, to prevent a single frame from
//		// being rendered with the level in the incorrect (restarted) state.
//		try {
//			((GameState)getState(STATE_PLAYING_GAME)).reset();
//		} catch (SlickException se) {
//			se.printStackTrace();
//		}

	}

	public static void main(String[] args) {
		try {

			Main main = new Main();
			ScalableGame sg = new ScalableGame(main, 640,480);
			app = new AppGameContainer(sg);
			app.setDisplayMode(640,480, false);
			app.setTargetFrameRate(60);
//			app.setVSync(true);
			app.start();

		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
