package org.fife.mario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

/**
 * Screen that shows the game's high scores.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CheckScoresState extends BasicGameState {

    private int id;
    private Image bgImg;
    private long time;
    private boolean drawPressStart;
    private UnicodeFont font;
    private List<Score> scores;
    private Color translucentGray;

    private static final String URL_SPEC =
        "http://gamesatlunch.com/scores.php?action=list&access_code=51112&sort_var=score";

    public CheckScoresState(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        new Thread(new CheckScoresRunnable()).start();
    }

    public synchronized List<Score> getScores() {
        return scores;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(GameContainer container, StateBasedGame game) {

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

        translucentGray = new Color(0, 0, 0, 0.85f);

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
        throws SlickException {

        if (bgImg == null) {
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
        } else {
            g.drawImage(bgImg, 0, 0);
        }

        float x = 30;
        float y = 30;
        g.setColor(translucentGray);
        g.fillRoundRect(x, y,
            container.getWidth() - 2 * x, container.getHeight() - 2 * y,
            10);

        g.setFont(font);
        x = 75;
        y = 60;
        List<Score> scores = getScores();

        if (scores == null) {
            g.setColor(Color.white);
            g.drawString("Getting high scores...", x, y);
        } else {

            g.setColor(Color.cyan);
            String str = "Player:";
            g.drawString(str, x, y);
            float strW = g.getFont().getWidth(str);
            float y2 = y + g.getFont().getLineHeight();
            g.drawLine(x, y2, x + strW, y2);
            g.drawLine(x, y2 + 1, x + strW, y2 + 1);
            str = "Score:";
            strW = g.getFont().getWidth(str);
            float x2 = container.getWidth() - x - strW;
            g.drawString(str, x2, y);
            g.drawLine(x2, y2, x2 + strW, y2);
            g.drawLine(x2, y2 + 1, x2 + strW, y2 + 1);

            // TODO: Make scores script only return 8, no matter what
            y += 40;
            g.setColor(Color.white);
            int count = Math.min(8, scores.size());
            for (int i = 0; i < count; i++) {
                Score score = scores.get(i);
                g.drawString(score.getName(), x, y);
                if (score.getScore() != null) {
                    // An IO exception would cause an "error" score where
                    // these values are null
                    //g.drawString(score.getDate(), x+200, y);
                    str = score.getScore();
                    x2 = container.getWidth() - x - g.getFont().getWidth(str);
                    g.drawString(score.getScore(), x2, y);
                }
                y += g.getFont().getLineHeight();
            }

        }

        if (drawPressStart) {
            g.setColor(Color.yellow);
            String text = "Press Enter";
            x = (container.getWidth() - font.getWidth(text)) / 2;
            y = 400;
            g.drawString(text, x, y);
        }

    }

    public synchronized void setScores(List<Score> scores) {
        this.scores = scores;
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
        throws SlickException {

        Input input = container.getInput();

        if (input.isKeyPressed(Input.KEY_ENTER) ||
            input.isKeyPressed(Input.KEY_X) ||
            input.isKeyPressed(Input.KEY_Z)) {
            game.enterState(Constants.STATE_TITLE_SCREEN, null, null);
        }

        time += delta;
        if (time >= 1000) {
            time -= 1000;
            drawPressStart = !drawPressStart;
        }

    }

    private final class CheckScoresRunnable implements Runnable {

        public void run() {
            System.out.println("Checking scores...");
            List<Score> scores = new ArrayList<>();

            try {
                URL url = new URI(URL_SPEC).toURL();
                URLConnection con = url.openConnection();
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(" +");
                    Score score = new Score(tokens[0], tokens[2], tokens[1]);
                    scores.add(score);
                }
                in.close();
            } catch (URISyntaxException | IOException ioe) {
                // TODO: Percolate message to user.
                Score score = new Score(ioe.getMessage(), null, null);
                scores.add(score);
                ioe.printStackTrace();
            }

            System.out.println("Done");
            setScores(scores);

        }

    }
}
