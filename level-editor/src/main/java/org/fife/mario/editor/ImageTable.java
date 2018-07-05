package org.fife.mario.editor;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;


/**
 * A table that allows the user to select a 32x32 tile from a larger image.
 *
 * @author Robert Futrell
 * @version 1.0
 */
abstract class ImageTable extends JTable implements ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private TilePalette parent;

	/**
	 * The width and height for table cells.  This must be a little bigger than
	 * 32x32 to actually render entire 32x32 images.  Not sure why, but it
	 * probably has something to do with JTable's cell insets.
	 */
	private static final int CELL_SIZE = 35;


	/**
	 * Constructor.
	 *
	 * @param parent The parent tile palette component.
	 * @param img The image to use for the tiles.
	 */
	ImageTable(TilePalette parent, BufferedImage img) {
		this.parent = parent;
		setImage(img);
		getSelectionModel().addListSelectionListener(this);
		getColumnModel().getSelectionModel().addListSelectionListener(this);
		setShowGrid(false);
		setTableHeader(null);
		setRowHeight(CELL_SIZE);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setColumnSelectionAllowed(true);
		setIntercellSpacing(new java.awt.Dimension(2,2));
		setDefaultRenderer(Object.class, new CellRenderer());
	}


	public Image getImage(int row, int col) {
		return (Image)getValueAt(row, col);
	}


	private int getTileFor(int row, int col) {
		int tile = row*getModel().getColumnCount() + col;
		if (tile>=getColumnCount()*getRowCount()) {
			tile = -1;
		}
		return tile;
	}


	protected TilePalette getTilePalette() {
		return parent;
	}


	public void setImage(BufferedImage img) {

		setModel(new Model(img));

		// Ensure all columns stay at 32 pixels wide
		TableColumnModel tcm = getColumnModel();
		for (int i=0; i<getColumnCount(); i++) {
			tcm.getColumn(i).setMinWidth(CELL_SIZE);
			tcm.getColumn(i).setPreferredWidth(CELL_SIZE);
			tcm.getColumn(i).setMaxWidth(CELL_SIZE);
		}

	}


	/**
	 * Called when the selection in this table changes.
	 *
	 * @param e The event.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e); // JTable defines this as well!
		int row = Math.max(getSelectedRow(), 0);
		int col = Math.max(getSelectedColumn(), 0);
		parent.setSelectedTile(getTileFor(row, col));
	}


	/**
	 * Renderer for this table.
	 */
	private static class CellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		private ImageIcon icon;
		private boolean selected;
		private AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);

		CellRenderer() {
			icon = new ImageIcon();
		}

		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
	            boolean isSelected, boolean hasFocus, int row, int column) {
	    	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    	this.selected = isSelected;
	    	if (value!=null) {
	    		icon.setImage((Image)value);
	    		setIcon(icon);
	    	}
	    	else {
	    		setIcon(null);
	    	}
	    	return this;
	    }

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			Composite old = null;
			if (selected) {
				old = g2d.getComposite();
				g2d.setComposite(c);
			}
			super.paintComponent(g2d);
			if (selected) {
				g2d.setComposite(old);
			}
		}

	}

	/**
	 * The model for this table.
	 */
	private class Model extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		Model(BufferedImage img) {
			int rowCount = img.getHeight() / (32+2);
			int colCount = img.getWidth() / (32+2);
			setRowCount(rowCount);
			setColumnCount(colCount);
			for (int row=0; row<rowCount; row++) {
				for (int col=0; col<colCount; col++) {
					Image val = img.getSubimage(col*34, row*34, 32, 32);
					setValueAt(val, row, col);
				}
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

	}


}
