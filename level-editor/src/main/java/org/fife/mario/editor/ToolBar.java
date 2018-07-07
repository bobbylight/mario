package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.fife.mario.Constants;
import org.fife.ui.CustomizableToolBar;
import org.fife.ui.app.AppAction;


/**
 * The toolbar used by the level editor.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ToolBar extends CustomizableToolBar implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Main app;
	private JLabel layeredComboLabel;
	private JComboBox<String> layerCombo;

	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	ToolBar(Main app) {

		this.app = app;

		add(createToolBarButton(app.getAction(Main.OPEN_ACTION_KEY)));
		add(createToolBarButton(app.getAction(Main.SAVE_ACTION_KEY)));
		addBigSeparator();

		add(createToolBarButton(app.getAction(Main.NEW_AREA_ACTION_KEY)));
		add(createToolBarButton(app.getAction(Main.DELETE_AREA_ACTION_KEY)));
		addBigSeparator();

		add(createToolBarButton(app.getAction(Main.UNDO_ACTION_KEY)));
		add(createToolBarButton(app.getAction(Main.REDO_ACTION_KEY)));
		addBigSeparator();

		ButtonGroup bg = new ButtonGroup();
		JToggleButton tb = new JToggleButton(new SetModeAction(EditMode.PAINT, "PaintMode"));
        tb.setIcon(createIcon("/img/paintbrush.png"));
		tb.setSelected(true);
		bg.add(tb);
		add(tb);

		tb = new JToggleButton(new SetModeAction(EditMode.FILL, "FillMode"));
		tb.setIcon(createIcon("/img/paintcan.png"));
		bg.add(tb);
		add(tb);

		tb = new JToggleButton(new SetModeAction(EditMode.DELETE, "DeleteMode"));
		tb.setIcon(createIcon("/img/pencil_delete.png"));
		bg.add(tb);
		add(tb);

		tb = new JToggleButton(new SetModeAction(EditMode.EDIT, "EditMode"));
		tb.setIcon(createIcon("/img/image_edit.png"));
		bg.add(tb);
		add(tb);

		addBigSeparator();

		layeredComboLabel = new JLabel(app.getString("LayerPrompt"));
		String[] choices = new String[] {
				app.getString("Layer.Foreground"),
				app.getString("Layer.Middle"),
				app.getString("Layer.Background"),
		};
		layerCombo = new JComboBox<>(choices);
		layerCombo.addActionListener(this);
		layerCombo.setSelectedIndex(Constants.MIDDLE);
		layeredComboLabel.setLabelFor(layerCombo);
		add(layeredComboLabel);
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(layerCombo, BorderLayout.LINE_START);
		add(temp);

		add(Box.createHorizontalGlue());
	}

	/**
	 * Called when the active layer changes.
	 * @param e The action performed.
	 */
	public void actionPerformed(ActionEvent e) {
		if (app.getPaintLayerRule()!=Main.LAYER_ALL) {
			AreaEditor ec = app.getCurrentAreaEditor();
			ec.repaint();
		}
	}

	private void addBigSeparator() {
		add(Box.createHorizontalStrut(8));
		addSeparator();
		add(Box.createHorizontalStrut(8));
	}

    private Icon createIcon(String resource) {
        return new ImageIcon(getClass().getResource(resource));
    }

	private JButton createToolBarButton(Action a) {
		JButton b = new JButton(a);
		b.setText(null);
		return b;
	}

	public int getActiveTileLayer() {
		return layerCombo.getSelectedIndex();
	}

	public void setLayerComboEnabled(boolean enabled) {
		layeredComboLabel.setEnabled(enabled);
		layerCombo.setEnabled(enabled);
	}

	/**
	 * Sets the edit mode of the application.
	 */
	private class SetModeAction extends AppAction<Main> {

		private static final long serialVersionUID = 1L;

		private EditMode mode;

		SetModeAction(EditMode mode, String key) {
			super(app, app.getResourceBundle(), key);
			this.mode = mode;
		}

		public void actionPerformed(ActionEvent e) {
			app.setEditMode(mode);
		}

	}

}
