package org.fife.mario.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Toggles the visibility of a hidden block.
 */
public class ToggleBlockHiddenAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private Main app;


	public ToggleBlockHiddenAction(Main app, String text) {
		this.app = app;
		putValue(NAME, text);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)e.getSource();
		JPopupMenu popup = (JPopupMenu)source.getParent();
		BlockInfo info = (BlockInfo)popup.getClientProperty("blockInfo");
		info.toggleHidden();
		app.getCurrentAreaEditor().repaintTile(app.getSelectedTile());
	}


}
