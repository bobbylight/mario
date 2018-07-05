package org.fife.mario;


/**
 * A high score.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Score {

	private String name;
	private String date;
	private String score;


	public Score(String name, String date, String score) {
		this.name = name;
		this.date = date;
		this.score = score;
	}


	public String getDate() {
		return date;
	}


	public String getName() {
		return name;
	}


	public String getScore() {
		return score;
	}


}
