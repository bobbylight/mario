package org.fife.mario.editor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Changes the content of a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ChangeBlockContentAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private Main app;
	private int content;

	ChangeBlockContentAction(Main app, String text, int content) {
		this.app = app;
		putValue(NAME, text);
		this.content = content;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)e.getSource();
		JPopupMenu popup = (JPopupMenu)source.getParent();
		BlockInfo info = (BlockInfo)popup.getClientProperty("blockInfo");
		info.setContent(content);
		app.getCurrentAreaEditor().repaintTile(app.getSelectedTile());
	}

}
