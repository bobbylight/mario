package org.fife.mario.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.fife.mario.Position;
import org.fife.mario.WarpInfo;
import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.UIUtil;


/**
 * A dialog for modifying a warp location (a pipe or door).
 *
 * @author Robert Futrell
 * @version 1.0
 */
class WarpLocationDialog extends EscapableDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private WarpInfo info;
	private JComboBox areaCombo;
	private JTextField rowField;
	private JTextField colField;

	/**
	 * Constructor.
	 *
	 * @param parent The parent application.
	 */
	WarpLocationDialog(Main parent, String[] areas) {

		super(parent);

		JPanel cp = new ResizableFrameContentPane(new BorderLayout());
		cp.setBorder(UIUtil.getEmpty5Border());

		Box topPanel = Box.createVerticalBox();
		JLabel label = new JLabel(parent.getString("Dialog.WarpLocation.Area"));
		areaCombo = new JComboBox(areas);
		areaCombo.setEditable(false);
		addLabeled(topPanel, label, areaCombo);
		label = new JLabel(parent.getString("Dialog.WarpLocation.DestRow"));
		rowField = new JTextField(5);
		addLabeled(topPanel, label, rowField);
		label = new JLabel(parent.getString("Dialog.WarpLocation.DestCol"));
		colField = new JTextField(5);
		addLabeled(topPanel, label, colField);
		topPanel.add(Box.createVerticalGlue());
		cp.add(topPanel, BorderLayout.NORTH);

		JButton okButton = new JButton(parent.getString("Button.OK"));
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton(parent.getString("Button.Cancel"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
        Container buttons = UIUtil.createButtonFooter(okButton, cancelButton);
		cp.add(buttons, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(cancelButton);
		setContentPane(cp);
		setModal(true);
		setTitle(parent.getString("Dialog.WarpLocation.Title"));
		pack();
		setLocationRelativeTo(parent);

	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("OK".equals(command)) {
			info = new WarpInfo((String)areaCombo.getSelectedItem());
			int row = Integer.parseInt(rowField.getText());
			int col = Integer.parseInt(colField.getText());
			info.setStartPosition(new Position(row, col));
			setVisible(false);
		}

		else if ("Cancel".equals(command)) {
			escapePressed();
		}

	}

	private void addLabeled(Box container, JLabel label, Component c) {
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(label, BorderLayout.LINE_START);
		temp.add(c);
		container.add(temp);
		container.add(Box.createVerticalGlue());
	}

	public WarpInfo getWarpInfo() {
		return info;
	}

	public void setWarpInfo(WarpInfo info) {
		if (info!=null) {
			Position pos = info.getStartPosition();
			areaCombo.setSelectedItem(info.getDestArea());
			rowField.setText(Integer.toString(pos.getRow()));
			colField.setText(Integer.toString(pos.getCol()));
		}
	}

}
