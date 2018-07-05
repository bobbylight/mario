package org.fife.mario.editor;

import java.awt.image.BufferedImage;
import javax.swing.event.ListSelectionEvent;


/**
 * A table that allows the user to select 1 or more background tiles.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class BackgroundTileTable extends ImageTable {

	private static final long serialVersionUID = 1L;


	/**
	 * Constructor.
	 *
	 * @param parent The parent tile palette component.
	 * @param img The image to use for background tiles.
	 */
	BackgroundTileTable(TilePalette parent, BufferedImage img) {
		super(parent, img);
	}


	private int getTileFor(int row, int col) {
		int tile = row*getModel().getColumnCount() + col;
		if (tile>=getColumnCount()*getRowCount()) {
			tile = -1;
		}
		return tile;
	}


	/**
	 * Called when the selection in this table changes.
	 *
	 * @param e The event.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e); // JTable defines this as well!
		int row = Math.max(getSelectedRow(), 0);
		int col = Math.max(getSelectedColumn(), 0);
		getTilePalette().setSelectedTile(getTileFor(row, col));
	}


}
