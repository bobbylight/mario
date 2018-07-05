package org.fife.mario.editor;

import javax.swing.undo.AbstractUndoableEdit;

import org.fife.mario.Position;


/**
 * An undoable edit representing a "fill" operation.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class FillUndoableEdit extends AbstractUndoableEdit {

	private AreaEditor ec;
	private Position pos;
	private int layer;
	private int oldData;
	private int newData;

	private static final long serialVersionUID = 1L;

	FillUndoableEdit(AreaEditor ec, Position pos, int layer,
							int oldData, int newData) {
		this.ec = ec;
		this.pos = pos;
		this.layer = layer;
		this.oldData = oldData;
		this.newData = newData;
	}

	@Override
	public void redo() {
		super.redo();
		ec.doFillImpl(layer, pos.getRow(), pos.getCol(), oldData, newData);
		ec.repaint();
	}

	@Override
	public void undo() {
		super.undo();
		ec.doFillImpl(layer, pos.getRow(), pos.getCol(), newData, oldData);
		ec.repaint();
	}

}
