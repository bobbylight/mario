package org.fife.mario.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;


/**
 * A dialog that gathers information for a new area.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class NewAreaDialog extends EscapableDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField nameField;
	private JTextField rowField;
	private JTextField colField;
	private JButton okButton;
	private NewAreaInfo nli;

	private static final int MIN_ROW_COUNT				= 15; // == 480/32


	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @param level Whether this dialog is for creating a new level.  If this
	 *        value is <code>false</code>, then this dialog is for creating a
	 *        new area in a level.
	 */
	NewAreaDialog(Main parent, boolean level) {

		super(parent);

		Listener l = new Listener();
		JPanel cp = new ResizableFrameContentPane(new BorderLayout());

		String key = level ? "Dialog.NewLevel.Description" :
							"Dialog.NewArea.Description";
		SelectableLabel descLabel = new SelectableLabel(parent.getString(key));

		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.add(new JLabel(parent.getString("Dialog.NewLevelOrArea.Name")));
		nameField = new JTextField(20);
        nameField.getDocument().addDocumentListener(l);
		if (level) {
			nameField.setText("main");
			nameField.setEnabled(false);
		}
		mainPanel.add(nameField);
		mainPanel.add(new JLabel(parent.getString("Dialog.NewLevelOrArea.Rows")));
		rowField = new JTextField(20);
		rowField.getDocument().addDocumentListener(l);
		mainPanel.add(rowField);
		mainPanel.add(new JLabel(parent.getString("Dialog.NewLevelOrArea.Cols")));
		colField = new JTextField(20);
		colField.getDocument().addDocumentListener(l);
		mainPanel.add(colField);
		UIUtil.makeSpringCompactGrid(mainPanel, 3,2, 5,5, 5,5);

		Box topPanel = Box.createVerticalBox();
		topPanel.setBorder(UIUtil.getEmpty5Border());
		topPanel.add(descLabel);
		topPanel.add(Box.createVerticalStrut(5));
		topPanel.add(mainPanel);
		topPanel.add(Box.createVerticalGlue());
		cp.add(topPanel, BorderLayout.NORTH);

		okButton = new JButton(parent.getString("Button.OK"));
		okButton.setEnabled(false);
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton(parent.getString("Button.Cancel"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
        Container buttons = UIUtil.createButtonFooter(okButton, cancelButton);
        cp.add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
		setContentPane(cp);
		String paramKey = level ? "Dialog.NewLevel" : "Dialog.NewArea";
		String title = parent.getString("Dialog.NewLevelOrArea.Title",
										parent.getString(paramKey));
		setTitle(title);
		setModal(true);
		pack();
		setLocationRelativeTo(parent);

	}

	/**
	 * Called when an action occurs in this dialog.
	 *
	 * @param e The event.
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("OK".equals(command)) {
			String name = nameField.getText();
			try {
				int rowCount = Integer.parseInt(rowField.getText());
				int colCount = Integer.parseInt(colField.getText());
				nli = new NewAreaInfo(name, rowCount, colCount);
				setVisible(false);
			} catch (NumberFormatException nfe) {
				Main app = (Main)getOwner();
				String title = app.getString("Error.DialogTitle");
				app.displayException(this, nfe, title);
			}
		}

		else if ("Cancel".equals(command)) {
			escapePressed();
		}

	}


	/**
	 * Returns the initial info to use for the new area.
	 *
	 * @return The initial info for the level, or <code>null</code> if the
	 *         user cancelled this dialog.
	 */
	public NewAreaInfo getNewAreaInfo() {
		return nli;
	}


	@Override
	public void setVisible(boolean visible) {

		if (visible) {

			Main app = (Main)getOwner();
			int rows = app.getAreaEditor("main").getRowCount();
			int cols = app.getAreaEditor("main").getColumnCount();
			rowField.setText(Integer.toString(rows));
			colField.setText(Integer.toString(cols));

			JTextField focusedField = nameField.isEnabled() ? nameField : rowField;
			focusedField.requestFocusInWindow();

			nli = null;
		}

		super.setVisible(visible);
	}


    /**
     * Listens for events in this dialog.
     */
	private final class Listener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		private void handleDocumentEvent(DocumentEvent e) {

			boolean valid = true;

			// Area name field
            if (nameField.getText().isEmpty()) {
                valid = false;
            }

            if (valid) {
                // Row and column fields
                try {
                    int rows = Integer.parseInt(rowField.getText());
                    if (rows < MIN_ROW_COUNT) {
                        throw new NumberFormatException();
                    }
                    int cols = Integer.parseInt(colField.getText());
                    if (cols <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    valid = false;
                }
            }

			okButton.setEnabled(valid);
		}

		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

	}


	/**
	 * The info entered for the initial settings of the new area.
	 */
	public static class NewAreaInfo {

		private String name;
		private int rowCount;
		private int colCount;

		NewAreaInfo(String name, int rowCount, int colCount) {
			this.name = name;
			this.rowCount = rowCount;
			this.colCount = colCount;
		}

		public int getColumnCount() {
			return colCount;
		}

		public String getName() {
			return name;
		}

		public int getRowCount() {
			return rowCount;
		}

	}


}
