package org.fife.mario.editor;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 * The renderer for entities (goal post, etc.).
 *
 * @author Robert Futrell
 * @version 1.0
 */
class EntityListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;


	@Override
	public Component getListCellRendererComponent(JList list, Object value,
							int index, boolean selected, boolean focused) {
		super.getListCellRendererComponent(list, value, index, selected, focused);
		EntityInfo ei = (EntityInfo)value;
		setIcon(ei.getIcon());
		return this;
	}


}
