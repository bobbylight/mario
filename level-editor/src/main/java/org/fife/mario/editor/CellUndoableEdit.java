package org.fife.mario.editor;

import javax.swing.undo.AbstractUndoableEdit;

import org.fife.mario.Position;
import org.fife.mario.blocks.BlockTypes;


/**
 * An event in the level editor that can be undone.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class CellUndoableEdit extends AbstractUndoableEdit {

	private static final long serialVersionUID = 1L;

	private AreaEditor ec;
	private Position pos;
	private CellState oldState;
	private CellState newState;

	CellUndoableEdit(AreaEditor ec, Position pos,
							CellState oldState, CellState newState) {
		this.ec = ec;
		this.pos = pos;
		this.oldState = oldState;
		this.newState = newState;
	}

	@Override
	public void redo() {
		super.redo();
		setCellValues(newState);
	}

	private void setCellValues(CellState state) {
		ec.setCellInfo(pos.getRow(), pos.getCol(), state.getTerrain(),
			state.getBlockType(), state.getBlockContent(), state.getEnemy());
	}

	@Override
	public void undo() {
		super.undo();
		setCellValues(oldState);
	}

    /**
     * The state of a cell.
     */
	public static class CellState {

		private int terrain;
		private BlockTypes blockType;
		private int blockContent;
		private int enemy;

		CellState(int terrain, BlockTypes blockType, int blockContent,
							int enemy) {
			this.terrain = terrain;
			this.blockType = blockType;
			this.blockContent = blockContent;
			this.enemy = enemy;
		}

		public int getBlockContent() {
			return blockContent;
		}

		public BlockTypes getBlockType() {
			return blockType;
		}

		public int getEnemy() {
			return enemy;
		}

		public int getTerrain() {
			return terrain;
		}

	}

}
