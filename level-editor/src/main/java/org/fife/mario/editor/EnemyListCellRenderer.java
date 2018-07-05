package org.fife.mario.editor;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 * The renderer for the enemy list.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class EnemyListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;


	@Override
	public Component getListCellRendererComponent(JList list, Object value,
							int index, boolean selected, boolean focused) {
		super.getListCellRendererComponent(list, value, index, selected, focused);
		EnemyInfo ei = (EnemyInfo)value;
		setIcon(ei.getIcon());
		return this;
	}


}
