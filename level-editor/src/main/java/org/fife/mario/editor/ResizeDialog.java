package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.UIUtil;


/**
 * A dialog that gathers information for a new level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ResizeDialog extends EscapableDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField rowField;
	private JTextField colField;
	private JButton okButton;
	private ResizeInfo ri;

	private static final int MIN_ROW_COUNT				= 15; // == 480/32

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 */
	ResizeDialog(Main parent) {

		super(parent);

		Listener l = new Listener();
		JPanel cp = new ResizableFrameContentPane(new BorderLayout());

		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.add(new JLabel(parent.getString("Dialog.NewLevelOrArea.Rows")));
		rowField = new JTextField(20);
		rowField.getDocument().addDocumentListener(l);
		mainPanel.add(rowField);
		mainPanel.add(new JLabel(parent.getString("Dialog.NewLevelOrArea.Cols")));
		colField = new JTextField(20);
		colField.getDocument().addDocumentListener(l);
		mainPanel.add(colField);
		UIUtil.makeSpringCompactGrid(mainPanel, 2,2, 5,5, 5,5);
		cp.add(mainPanel, BorderLayout.NORTH);

		okButton = new JButton(parent.getString("Button.OK"));
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton(parent.getString("Button.Cancel"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		cp.add(UIUtil.createButtonFooter(okButton, cancelButton), BorderLayout.SOUTH);

		setContentPane(cp);
		setTitle(parent.getString("Dialog.Resize.Title"));
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
			try {
				int rowCount = Integer.parseInt(rowField.getText());
				int colCount = Integer.parseInt(colField.getText());
				ri = new ResizeInfo(rowCount, colCount);
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
	 * Returns the new size for the level.
	 *
	 * @return The new size for the level, or <code>null</code> if the
	 *         user cancelled this dialog.
	 */
	public ResizeInfo getNewLevelInfo() {
		return ri;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			Main app = (Main)getOwner();
			int rows = app.getCurrentAreaEditor().getRowCount();
			int cols = app.getCurrentAreaEditor().getColumnCount();
			rowField.setText(Integer.toString(rows));
			colField.setText(Integer.toString(cols));
			ri = null;
		}
		super.setVisible(visible);
	}

	/**
	 * Listens for events in this dialog.
	 */
	private class Listener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		private void handleDocumentEvent(DocumentEvent e) {
			boolean valid = true;
			try {
				int rows = Integer.parseInt(rowField.getText());
				if (rows<MIN_ROW_COUNT) {
					throw new NumberFormatException();
				}
				int cols = Integer.parseInt(colField.getText());
				if (cols<=0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException nfe) {
				valid = false;
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
	 * The info entered for the new size of the level.
	 */
	static class ResizeInfo {

		private int rowCount;
		private int colCount;

		ResizeInfo(int rowCount, int colCount) {
			this.rowCount = rowCount;
			this.colCount = colCount;
		}

		public int getColumnCount() {
			return colCount;
		}

		public int getRowCount() {
			return rowCount;
		}

	}

}
