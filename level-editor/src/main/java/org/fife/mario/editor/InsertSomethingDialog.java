package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;


/**
 * A dialog allowing the user to insert either rows or columns.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class InsertSomethingDialog extends EscapableDialog
									implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JSpinner amtField;
	private int toAdd;


	public InsertSomethingDialog(Main app, boolean rows) {

		JPanel cp = new ResizableFrameContentPane(new BorderLayout());
		cp.setBorder(UIUtil.getEmpty5Border());

		String title;
		String prompt;
		if (rows) {
			title = app.getString("Dialog.InsertRows.Title");
			prompt = app.getString("Dialog.InsertRows.Prompt");
		}
		else {
			title = app.getString("Dialog.InsertCols.Title");
			prompt = app.getString("Dialog.InsertCols.Prompt");
		}

		SelectableLabel promptLabel = new SelectableLabel(prompt);
		amtField = new JSpinner(new SpinnerNumberModel(1, 0,100, 1));
		Box topPanel = Box.createVerticalBox();
		topPanel.add(promptLabel);
		topPanel.add(amtField);
		topPanel.add(Box.createVerticalGlue());
		cp.add(topPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridLayout(1,2, 5,5));
		JButton okButton = new JButton(app.getString("Button.OK"));
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		JButton cancelButton = new JButton(app.getString("Button.Cancel"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		JPanel temp = new JPanel();
		temp.setBorder(UIUtil.getEmpty5Border());
		temp.add(buttonPanel);
		cp.add(temp, BorderLayout.SOUTH);

		setTitle(title);
		setContentPane(cp);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(app);

		toAdd = -1;

	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("OK".equals(command)) {
			toAdd = ((Number)amtField.getValue()).intValue();
			escapePressed();
		}

		else if ("Cancel".equals(command)) {
			escapePressed();
		}

	}

	/**
	 * Returns the number of rows or columns to add.
	 *
	 * @return The number of rows or columns to add, or <code>-1</code> if
	 *         the user cancelled the dialog.
	 */
	public int getToAdd() {
		return toAdd;
	}

}
