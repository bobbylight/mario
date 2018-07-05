package org.fife.mario.editor;

import java.awt.image.BufferedImage;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;


/**
 * Table that allows the user to select a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class BlockImageTable extends ImageTable {


	private static final long serialVersionUID = 1L;


	/**
	 * Constructor.
	 *
	 * @param parent The parent tile palette component.
	 * @param img The image to use for block images.
	 */
	BlockImageTable(TilePalette parent, BufferedImage img) {
		super(parent, img);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}


	private int getBlockFor(int row, int col) {
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
		getTilePalette().setSelectedBlockType(getBlockFor(row, col));
	}


}
