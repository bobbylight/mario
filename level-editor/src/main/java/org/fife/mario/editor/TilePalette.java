package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fife.mario.Mario;
import org.fife.ui.CleanSplitPaneUI;
import org.fife.ui.RListSelectionModel;
import org.fife.ui.TitledPanel;


/**
 * Lets the user select a tile to add to the level.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TilePalette extends JComponent implements
								ChangeListener, ListSelectionListener {

	/**
	 * The parent level editor application.
	 */
	private Main parent;

	private JTabbedPane tabbedPane;
	private BackgroundTileTable bgTable;
	private BlockImageTable blockTable;
	private int tileW;
	private int tileH;
	private int spacing;
	private JList<EnemyInfo> enemyList;
	private DefaultListModel<EnemyInfo> enemyListModel;
	private JList<EntityInfo> otherList;
	private DefaultListModel<EntityInfo> otherListModel;
	private JLabel selectedItemLabel;
//	private PropertySheet newTilePropSheet;
	private JPanel extraInfoPanel;
	private TitledPanel newTileTitledPanel;
	private int lastTileIndex;
	private int lastBlockIndex;
	private int lastEnemyIndex;
	private int lastOtherIndex;

	private static final String TILES_FILE	= "/img/ground_brown.png";

	private static final long serialVersionUID			= 1L;


	/**
	 * Constructor.
	 *
	 * @param creator The parent map creator application.
	 * @param tileWidth
	 * @param tileHeight
	 * @param spacing
	 */
	public TilePalette(Main creator, int tileWidth, int tileHeight,
						int spacing) throws IOException {

		this.parent = creator;
		this.tileW = tileWidth;
		this.tileH = tileHeight;
		this.spacing = spacing;

		BufferedImage image = createTilesImage(TILES_FILE);
		bgTable = new BackgroundTileTable(this, image);
		bgTable.setPreferredScrollableViewportSize(new Dimension(tileWidth*10, tileHeight*4));
		JPanel temp = new JPanel(new BorderLayout());
		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(bgTable);
		sp.setBorder(null);
		sp.setViewportBorder(null); // Needed by MotifLookAndFeel
		temp.add(sp);//, BorderLayout.LINE_START);
		TitledPanel tilePanel = new TitledPanel("Tiles", temp, TitledPanel.LINE_BORDER);

		BufferedImage blockImage = createBlockImage();
		blockTable = new BlockImageTable(this, blockImage);
		blockTable.setPreferredScrollableViewportSize(new Dimension(tileWidth*10, tileHeight*4));
		temp = new JPanel(new BorderLayout());
		sp = new JScrollPane(blockTable);
		sp.setBorder(null);
		sp.setViewportBorder(null);
		temp.add(sp);
		TitledPanel blockPanel = new TitledPanel("Blocks", temp, TitledPanel.LINE_BORDER);

		enemyListModel = createEnemyListModel();
		enemyList = new JList<>(enemyListModel);
		enemyList.setVisibleRowCount(5);
		enemyList.setCellRenderer(new EnemyListCellRenderer());
		enemyList.setSelectionModel(new RListSelectionModel());
		enemyList.setSelectedIndex(0); // Start with something selected.
		enemyList.addListSelectionListener(this);
		sp = new JScrollPane(enemyList);
		TitledPanel enemyPanel = new TitledPanel("Enemies", sp, TitledPanel.LINE_BORDER);

		otherListModel = createOtherListModel();
		otherList = new JList<>(otherListModel);
		otherList.setVisibleRowCount(5);
		otherList.setCellRenderer(new EntityListCellRenderer());
		otherList.setSelectionModel(new RListSelectionModel());
		otherList.setSelectedIndex(0); // Start with something selected.
		otherList.addListSelectionListener(this);
		sp = new JScrollPane(otherList);
		TitledPanel otherPanel = new TitledPanel("Other", sp, TitledPanel.LINE_BORDER);

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.addTab(tilePanel.getTitle(), tilePanel);
		tabbedPane.addTab(blockPanel.getTitle(), blockPanel);
		tabbedPane.addTab(enemyPanel.getTitle(), enemyPanel);
		tabbedPane.addTab(otherPanel.getTitle(), otherPanel);

		tabbedPane.addChangeListener(this);
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(tabbedPane);

		JPanel rightPanel = new JPanel(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
											leftPanel, rightPanel);
		splitPane.setUI(new CleanSplitPaneUI());
		splitPane.setDividerSize(3);
		splitPane.setResizeWeight(1.0);
		add(splitPane);

		extraInfoPanel = new JPanel(new BorderLayout());
		extraInfoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5),
				new DisablableTitledBorder("Selected tile:")
		));
		selectedItemLabel = new JLabel();
		selectedItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
		extraInfoPanel.add(selectedItemLabel, BorderLayout.NORTH);
		JPanel temp2 = new JPanel(new BorderLayout());
		temp2.add(extraInfoPanel, BorderLayout.NORTH);
		leftPanel.add(temp2, BorderLayout.LINE_END);

//		newTilePropSheet = createNewTilePropertySheet();
//		newTilePropSheet.addPropertySheetListener(this);
//		newTileTitledPanel = new TitledPanel("New Tile Properties",
//									newTilePropSheet, TitledPanel.LINE_BORDER);
//		rightPanel.add(newTileTitledPanel);

		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setSelectedTile(0);

	}


	private BufferedImage createBlockImage() throws IOException {

		// Arrange in 4 rows of 4

		int w = (tileW+spacing)*4;
		int h = (tileH+spacing)*4;
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
		BufferedImage fromImg = ImageIO.read(getClass().getResource("/img/blocks.png"));
		fromImg = Utils.createImageWithAlpha(fromImg, -1, 0xc0c0c0);

		Graphics g = img.createGraphics();
		int xInc = tileW + spacing;
		int sy = 0;
		try {

			// The first block is "no block".
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, tileW-1, tileH-1);
			for (int col=0; col<3; col++) {
				int dx = (tileW+spacing) * (col+1); // Skip "empty" block
				g.drawImage(fromImg, dx,0,dx+tileW,tileH, 0,sy,tileW,sy+tileH, null);
				sy += xInc;
			}

			int dy = tileH + spacing;
			for (int col=0; col<4; col++) {
				int dx = (tileW+spacing) * col;
				g.drawImage(fromImg, dx,dy,dx+tileW,dy+tileH, 0,sy,tileW,sy+tileH, null);
				sy += xInc;
			}

			dy += tileH + spacing;
			for (int col=0; col<2; col++) {
				int dx = (tileW+spacing) * col;
				g.drawImage(fromImg, dx,dy,dx+tileW,dy+tileH, 0,sy,tileW,sy+tileH, null);
				sy += xInc;
			}

			sy -= xInc;
			int sx = (tileW+spacing) * 1;
			int dx = (tileW+spacing)*2;
			g.drawImage(fromImg, dx,dy,dx+tileW,dy+tileH, sx,sy,sx+tileW,sy+tileH, null);

			dx += tileW + spacing;
			sx += tileW + spacing;
			g.drawImage(fromImg, dx,dy,dx+tileW,dy+tileH, sx,sy,sx+tileW,sy+tileH, null);

			dy += tileH + spacing;
			dx = 0;
			sx += tileW + spacing;
			g.drawImage(fromImg, dx,dy,dx+tileW,dy+tileH, sx,sy,sx+tileW,sy+tileH, null);

		} finally {
			g.dispose();
		}

		return img;

	}


	private DefaultListModel<EnemyInfo> createEnemyListModel() throws IOException {

		DefaultListModel<EnemyInfo> model = new DefaultListModel<>();

		// Images from which several enemies are created.
		BufferedImage marioImg = ImageIO.read(getClass().getResource("/img/mario_wip.png"));
		BufferedImage goombaImg = ImageIO.read(getClass().getResource("/img/goomba.png"));
		BufferedImage ktImg = ImageIO.read(getClass().getResource("/img/koopatroopa.png"));
		BufferedImage ppImg = ImageIO.read(getClass().getResource("/img/piranha_plant.png"));
		BufferedImage bowserImg = ImageIO.read(getClass().getResource("/img/bowser.png"));
		BufferedImage platformImg = ImageIO.read(getClass().getResource("/img/moving_platform.png"));

		BufferedImage img = goombaImg.getSubimage(17,30, 32,32);
		img = Utils.createImageWithAlpha(img);
		EnemyInfo info = new EnemyInfo(img, "Goomba", "goomba");
		model.addElement(info);

		img = ktImg.getSubimage(147, 12, 32, 52);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Koopa Troopa (Green)", "koopatroopa green");
		model.addElement(info);

		img = ktImg.getSubimage(148, 144, 32, 52);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Koopa Troopa (Red)", "koopatroopa red");
		model.addElement(info);

		img = ktImg.getSubimage(342, 8, 386-342, 64-8);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Flying Koopa Troopa (Green)", "flying_koopatroopa green");
		model.addElement(info);

		img = ktImg.getSubimage(342, 140, 386-342, 196-140);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Flying Koopa Troopa (Red)", "flying_koopatroopa red");
		model.addElement(info);

		img = ppImg.getSubimage(0, 0, 32, 66);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Piranha Plant (Up)", "piranha_plant up");
		model.addElement(info);

		img = Utils.getVerticallyMirroredImage(img);
		info = new EnemyInfo(img, "Piranha Plant (Down)", "piranha_plant down");
		model.addElement(info);

		img = bowserImg.getSubimage(0, 0, 68,82);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Bowser", "bowser");
		model.addElement(info);

		img = ImageIO.read(getClass().getResource("/img/axe.png"));
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Axe", "axe");
		model.addElement(info);

		img = marioImg.getSubimage(0,384, Mario.WIDTH,Mario.HEIGHT);
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Toad", "toad");
		model.addElement(info);

		img = platformImg;
		img = Utils.createImageWithAlpha(img);
		info = new EnemyInfo(img, "Moving Platform", "moving_platform");
		model.addElement(info);

		return model;

	}


	private DefaultListModel<EntityInfo> createOtherListModel() throws IOException {

		DefaultListModel<EntityInfo> model = new DefaultListModel<>();

		BufferedImage img = ImageIO.read(getClass().getResource("/img/goal_small.png"));
		img = Utils.createImageWithAlpha(img);
		EntityInfo ei = new EntityInfo(img, "Big Goal Post", "goal");
		model.addElement(ei);

		img = ImageIO.read(getClass().getResource("/img/firestick.png"));
		img = Utils.createImageWithAlpha(img);
		ei = new EntityInfo(img, "Fire Stick", "firestick");
		model.addElement(ei);

		img = ImageIO.read(getClass().getResource("/img/springboard.png"));
		img = Utils.createImageWithAlpha(img, -1, 0xc0c0c0);
		ei = new EntityInfo(img, "Spring Board", "springboard");
		model.addElement(ei);

		return model;

	}


	private BufferedImage createTilesImage(String imgFile) throws IOException {

		BufferedImage image = ImageIO.read(getClass().getResource(imgFile));
		image = Utils.createImageWithAlpha(image, -1, 0xff0000);

		return image;

	}


//	protected PropertySheet createNewTilePropertySheet() {
//		PropertySheet ps = CreatorUtil.createTilePropertySheet();
//		return ps;
//	}


//	/**
//	 * Called when the user changes a value in the "new tile" property sheet.
//	 *
//	 * @param e The event.
//	 */
//	@Override
//	public void displayedPropertyChanged(DisplayedPropertyChangeEvent e) {
//		setSelectedTile(mapCreator.getSelectedTileData()&0x3ff);
//	}


	public int getBlockTypeAt(Point p) {
		int col = p.x / (tileW + spacing);
		return (p.y/(tileH+spacing)) + col*4;
	}


	public String getEnemyName(int index) {
		return ((EnemyInfo)enemyListModel.get(index)).getShortName();
	}


	public int getEnemyType(String shortName) {
		for (int i=0; i<enemyListModel.getSize(); i++) {
			EnemyInfo ei = (EnemyInfo)enemyListModel.get(i);
			if (ei.getShortName().equals(shortName)) {
				return i;
			}
		}
		return 0; // Unknown monster - to Goomba
	}


	private int getHorizontalBlockCount() {
		return blockTable.getColumnCount();
	}

	private int getHorizontalTileCount() {
		return bgTable.getColumnCount();
	}


	public String getOtherName(int index) {
		return ((EntityInfo)otherListModel.get(index)).getShortName();
	}


	public int getOtherType(String shortName) {
		for (int i=0; i<otherListModel.getSize(); i++) {
			EntityInfo ei = (EntityInfo)otherListModel.get(i);
			if (ei.getShortName().equals(shortName)) {
				return i;
			}
		}
		return 0; // Unknown object - ???
	}


	public void paintBlock(int tile, Graphics2D g, int dx1, int dy1, int dx2, int dy2) {
		int index = tile;
		int col = index % getHorizontalBlockCount();
		int row = index / getHorizontalBlockCount();
		g.drawImage(blockTable.getImage(row, col), dx1,dy1, dx2-dx1,dy2-dy1, null);
	}


	public void paintEnemy(int enemy, Graphics2D g, int x, int y, float scale) {
		EnemyInfo ei = (EnemyInfo)enemyListModel.get(enemy);
		Image img = ((ImageIcon)ei.getIcon()).getImage();
		x = x + 32 - img.getWidth(null);
		y = y + 32 - img.getHeight(null);
		// TODO: Properly handle scale
		g.drawImage(img, x,y, null);
	}


	public void paintTile(int tile, Graphics2D g, int dx, int dy, int w, int h) {
		int col = (tile%getHorizontalTileCount());
		int row = (tile/getHorizontalTileCount());
		g.drawImage(bgTable.getImage(row, col), dx,dy, w,h, null);
	}


	public void setPropsPropertySheetEnabled(boolean enabled) {
		newTileTitledPanel.setEnabled(enabled);
//		newTilePropSheet.setEnabled(enabled);
	}


	void setSelectedBlockType(int blockType) {

		if (blockType>12) {
			return;
		}

		int x = (blockType%getHorizontalBlockCount());
		int y = (blockType/getHorizontalBlockCount());
		setSelectedItemImage(blockTable.getImage(y, x));

		parent.setSelectedTileData(ObjectType.BLOCK, blockType);
		lastBlockIndex = blockType;

	}


	private void setSelectedEnemy(int enemy) {

		Icon icon = ((EnemyInfo)enemyListModel.get(enemy)).getIcon();
		setSelectedItemImage(((ImageIcon)icon).getImage());

		parent.setSelectedTileData(ObjectType.ENEMY, enemy);
		lastEnemyIndex = enemy;

	}


	private void setSelectedItemImage(Image image) {
		selectedItemLabel.setIcon(new ImageIcon(image));
		// Make width a little larger so titled border displays all of its
		// text.  We could actually compute the titled border's preferred
		// width, but that's too much work.
		Icon icon = selectedItemLabel.getIcon();
		int preferredWidth = icon.getIconWidth() + 50;
		selectedItemLabel.setPreferredSize(
				new Dimension(preferredWidth, icon.getIconHeight()));
		selectedItemLabel.revalidate();
	}


	private void setSelectedOther(int other) {
		EntityInfo ei = (EntityInfo)otherListModel.get(other);
		setSelectedItemImage(((ImageIcon)ei.getIcon()).getImage());
		parent.setSelectedTileData(ObjectType.OTHER, other);
		lastOtherIndex = other;
	}


	void setSelectedTile(int tileIndex) {

		int x = (tileIndex%getHorizontalTileCount());
		int y = (tileIndex/getHorizontalTileCount());
		setSelectedItemImage(bgTable.getImage(y, x));

//		TileImpl ti = new TileImpl(null, 0);
//		ti.setIndexInTileImage(tileIndex);
//		PropertyInfo pi = newTilePropSheet.getPropertyInfo("General", "Type");
//		ti.setTerrainType(CreatorUtil.terrainTypeFromString((String)pi.getValue()));
//		pi = newTilePropSheet.getPropertyInfo("General", "Motion");
//		ti.setMotionType(CreatorUtil.motionTypeFromString((String)pi.getValue()));
//		mapCreator.setSelectedTileData(ti.getData());
		parent.setSelectedTileData(ObjectType.TILE, tileIndex+1);
		lastTileIndex = tileIndex;

	}


	public void setTileset(String imgFile) {
		try {
			BufferedImage image = createTilesImage(imgFile);
			bgTable.setImage(image);
		} catch (IOException ioe) {
			String desc = parent.getString("Error.LoadingTileset");
			parent.displayException(ioe, desc);
		}
	}


	/**
	 * Called when the user changes tabs in the contained tabbed pane.
	 *
	 * @param e The event.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		switch (tabbedPane.getSelectedIndex()) {
			case 0:
				setSelectedTile(lastTileIndex);
				break;
			case 1:
				setSelectedBlockType(lastBlockIndex);
				break;
			case 2:
				setSelectedEnemy(lastEnemyIndex);
				break;
			case 3:
				setSelectedOther(lastOtherIndex);
				break;
		}
	}


	/**
	 * Called when a value in a list is selected.
	 *
	 * @param e The event.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource()==enemyList) {
			EnemyInfo ei = (EnemyInfo)enemyList.getSelectedValue();
			if (ei!=null) {
				setSelectedEnemy(enemyList.getSelectedIndex());
			}
		}
		else if (e.getSource()==otherList) {
			EntityInfo ei = (EntityInfo)otherList.getSelectedValue();
			if (ei!=null) {
				setSelectedOther(otherList.getSelectedIndex());
			}
		}
		else {
			System.out.println("... " + e.getSource());
		}
	}


	/**
	 * A border that is enabled or disabled depending on the state of its
	 * parent component.
	 *
	 * @author Robert Futrell
	 * @version 1.0
	 */
	private static class DisablableTitledBorder extends TitledBorder {

		private static final long serialVersionUID = 1L;

		DisablableTitledBorder(String title) {
			super(title);
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
								int width, int height) {
			if (c.isEnabled()) {
				setTitleColor(UIManager.getColor("TitledBorder.titleColor"));
			}
			else {
				setTitleColor(SystemColor.textInactiveText);
			}
			super.paintBorder(c, g, x,y, width,height);
		}

	}


}
