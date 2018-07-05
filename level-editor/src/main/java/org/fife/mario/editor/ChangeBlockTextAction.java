package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.fife.ui.EscapableDialog;
import org.fife.ui.UIUtil;


/**
 * Changes the content of a block.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ChangeBlockTextAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private Main app;


	ChangeBlockTextAction(Main app, String name) {
		this.app = app;
		putValue(NAME, name);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JMenuItem source = (JMenuItem)e.getSource();
		JPopupMenu popup = (JPopupMenu)source.getParent();
		BlockInfo info = (BlockInfo)popup.getClientProperty("blockInfo");
		String text = info.getText();

		ChangeTextDialog ctd = new ChangeTextDialog(app, text);
		ctd.setVisible(true);

		text = ctd.getText();
		if (text!=null) {
			info.setText(text);
		}

	}

    /**
     * A dialog used by this action.
     */
	private static class ChangeTextDialog extends EscapableDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		private JTextArea textArea;
		private String text;

		ChangeTextDialog(Main app, String origText) {

			super(app);

			JPanel cp = new JPanel(new BorderLayout());

			JPanel temp = new JPanel(new BorderLayout());
			temp.setBorder(UIUtil.getEmpty5Border());
			textArea = new JTextArea(25, 40);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setText(origText);
			temp.add(new JScrollPane(textArea));
			cp.add(temp);

			JPanel buttonPanel = new JPanel(new GridLayout(1,2, 5,5));
			JButton okButton = new JButton(app.getString("Button.OK"));
			okButton.setActionCommand("OK");
			okButton.addActionListener(this);
			buttonPanel.add(okButton);
			JButton cancelButton = new JButton(app.getString("Button.Cancel"));
			cancelButton.setActionCommand("Cancel");
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
			temp = new JPanel();
			temp.setBorder(UIUtil.getEmpty5Border());
			temp.add(buttonPanel);
			cp.add(temp, BorderLayout.SOUTH);

			getRootPane().setDefaultButton(okButton);
			setContentPane(cp);
			setTitle(app.getString("ChangeTextDialog.Title"));
			setModal(true);
			pack();
			setLocationRelativeTo(app);

		}

		public void actionPerformed(ActionEvent e) {

			String command = e.getActionCommand();

			if ("OK".equals(command)) {
				text = textArea.getText();
				setVisible(false);
			}

			else if ("Cancel".equals(command)) {
				text = null;
				escapePressed();
			}

		}

		public String getText() {
			return text;
		}

	}

}
