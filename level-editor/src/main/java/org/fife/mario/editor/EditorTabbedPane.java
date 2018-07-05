package org.fife.mario.editor;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.ui.RScrollPane;


/**
 * Tabbed pane for {@link AreaEditor}s.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class EditorTabbedPane extends JTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private Main app;


	EditorTabbedPane(Main app) {
		this.app = app;
		addChangeListener(this);
	}


	public boolean addEditorComponent(String name, final AreaEditor ec) {
		for (int i=0; i<getTabCount(); i++) {
			String title = getTitleAt(i);
			if (title.equals(name)) {
				UIManager.getLookAndFeel().provideErrorFeedback(this);
				return false;
			}
		}
		RScrollPane sp = new RScrollPane(ec);
		SwingUtilities.invokeLater(ec::scrollToBottomLeft);
		addTab(name, sp);
		return true;
	}


	public AreaEditor getAreaEditor() {
		return getEditorComponent(getSelectedIndex());
	}


	public AreaEditor getEditorComponent(int index) {
		RScrollPane sp = (RScrollPane)getComponentAt(index);
		return (AreaEditor)sp.getViewport().getView();
	}


	public AreaEditor getAreaEditor(String area) {
		for (int i=0; i<getTabCount(); i++) {
			String title = getTitleAt(i);
			if (title.equals(area)) {
				RScrollPane sp = (RScrollPane)getComponentAt(i);
				return (AreaEditor)sp.getViewport().getView();
			}
		}
		return null;
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (getTabCount()>0 && getSelectedIndex()>-1) {
			MenuBar menuBar = (MenuBar)app.getJMenuBar();
			if (menuBar!=null) {
				menuBar.currentEditorComponentChanged(getAreaEditor());
			}
		}
	}


}
