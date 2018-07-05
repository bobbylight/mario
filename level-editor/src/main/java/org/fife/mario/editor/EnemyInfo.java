package org.fife.mario.editor;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * Information about an enemy.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class EnemyInfo {

	private String name;
	private String shortName;
	private Icon icon;


	EnemyInfo(Image img, String name, String shortName) {
		this.icon = new ImageIcon(img);
		this.name = name;
		this.shortName = shortName;
	}


	public Icon getIcon() {
		return icon;
	}


	public String getShortName() {
		return shortName;
	}


	@Override
	public String toString() {
		return name;
	}
}
