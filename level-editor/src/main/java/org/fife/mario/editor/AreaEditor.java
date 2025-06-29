package org.fife.mario.editor;

import org.fife.mario.Constants;
import org.fife.mario.Position;
import org.fife.mario.WarpInfo;
import org.fife.mario.blocks.BlockTypes;
import org.fife.mario.editor.Actions.EditWarpLocationAction;
import org.fife.mario.level.MapData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * The component that actually lets you edit a Mario area.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class AreaEditor extends JComponent implements Scrollable {

    public static final String PROPERTY_SCALE        = "scale";

    private Main parent;
    private JPopupMenu textBlockPopup;
    private JCheckBoxMenuItem hiddenBlockItem;
    private JPopupMenu blockPopup;
    private JPopupMenu warpPopup;
    private EditMode editMode;
    private int tileWidth;
    private int tileHeight;
    private Image bgImage;
    private String bgImageName;
    private String tilesetImageName;
    private MapData tileInfo;
    private Map<Position, BlockInfo> blocks;
    private Map<Position, WarpInfo> warps;
    private SortedMap<Position, Integer> enemies;
    private SortedMap<Position, Integer> others;
    private int music;
    private boolean flyingFish;
    private boolean water;
    private Position armedTile;
    private Position selectedTile;
    private boolean paintGridlines;
    private int scalePercent;
    private int scaledTileWidth;
    private int scaledTileHeight;
    private Image[] contentImages;
    private Image goalsImage;
    private Image fireStickImage;
    private Image springBoardImage;
    private UndoManager undoManager;

    private static final Composite ALPHA_COMPOSITE =
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private static final Stroke SELECTED_STROKE =
        new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param parent The parent application.
     * @param rowCount The number of rows.
     * @param colCount The number of columns.
     */
    public AreaEditor(Main parent, int rowCount, int colCount) {

        this.parent = parent;
        tileWidth = parent.getTileWidth();
        tileHeight = parent.getTileHeight();
        setPaintGridlines(true);
        setScale(100);
        editMode = EditMode.PAINT;
        undoManager = new UndoManager();
        ToolTipManager.sharedInstance().registerComponent(this);

        if (!setBackgroundImage("hills")) {
            System.exit(1);
        }
        setTilesetImageName("ground_brown");

        enableEvents(AWTEvent.MOUSE_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK);

        armedTile = new Position(-1, -1);
        selectedTile = new Position(-1, -1);
        reset(rowCount, colCount, true);

        setPaintGridlines(true);

        try {
            BufferedImage fromImg = ImageIO.read(getClass().getResource("/img/blocks.png"));
            fromImg = Utils.createImageWithAlpha(fromImg, -1, 0xff0000);
            BufferedImage fromImg2 = ImageIO.read(getClass().getResource("/img/power_ups.png"));
            fromImg2 = Utils.createImageWithAlpha(fromImg2, -1, 0xff0000);
            contentImages = new Image[5];
            contentImages[BlockInfo.CONTENT_COINS_1] = fromImg.getSubimage(0,34*6, 32,32)
                .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            BufferedImage temp = new BufferedImage(24,24, BufferedImage.TYPE_INT_ARGB);
            Graphics g = temp.getGraphics();
            try {
                Image coinImg = contentImages[BlockInfo.CONTENT_COINS_1];
                g.drawImage(coinImg, 0,0, null);
                g.drawImage(coinImg, 4,4, null);
                g.drawImage(coinImg, 8,8, null);
            } finally {
                g.dispose();
            }
            contentImages[BlockInfo.CONTENT_COINS_10] = temp;
            contentImages[BlockInfo.CONTENT_FIRE_FLOWER] = fromImg2.getSubimage(34,0, 32,32)
                .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            contentImages[BlockInfo.CONTENT_ONE_UP] = fromImg2.getSubimage(0,34, 32,32)
                .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            contentImages[BlockInfo.CONTENT_STAR] = fromImg2.getSubimage(0,68, 32,32)
                .getScaledInstance(16, 16, Image.SCALE_SMOOTH);

            BufferedImage img = ImageIO.read(getClass().getResource("/img/goals.png"));
            img = Utils.createImageWithAlpha(img, -1, 0xc0c0c0);
            goalsImage = img;

            img = ImageIO.read(getClass().getResource("/img/firestick.png"));
            img = Utils.createImageWithAlpha(img);
            fireStickImage = img;

            img = ImageIO.read(getClass().getResource("/img/springboard.png"));
            img = Utils.createImageWithAlpha(img, -1, 0xc0c0c0);
            springBoardImage = img;

        } catch (IOException ioe) {
            parent.displayException(ioe);
        }

    }

    public void addBlock(int row, int col, BlockTypes blockType) {
        BlockInfo bi = new BlockInfo(blockType);
        blocks.put(new Position(row, col), bi);
    }

    public void addBlock(int row, int col, BlockTypes blockType, int content,
                            String text, boolean hidden) {
        BlockInfo bi = new BlockInfo(blockType, content);
        if (text!=null) {
            bi.setText(text);
        }
        bi.setHidden(hidden);
        blocks.put(new Position(row, col), bi);
    }

    public boolean addEnemy(Position pos, Integer type) {
        if (type>-1) {
            if (type!=enemies.get(pos)) {
                enemies.put(pos, type);
                return true;
            }
        }
        else {
            enemies.remove(pos);
        }
        return false;
    }

    public boolean addOther(Position pos, Integer type) {
        if (type>-1) {
            if (type!=others.get(pos)) {
                others.put(pos, type);
                return true;
            }
        }
        else {
            others.remove(pos);
        }
        return false;
    }

    private BlockTypes blockPaletteIndexToBlockType(int paletteIndex) {
        // Better way to do this?
        paletteIndex--;
        for (BlockTypes type : BlockTypes.values()) {
            if (type.getTypeIndex()==paletteIndex) {
                return type;
            }
        }
        return null;
    }

    private Integer blockTypeToBlockPaletteIndex(BlockTypes type) {
        return type.getTypeIndex() + 1;
    }

    private int colToX(int col) {
        return col * scaledTileWidth;
    }

    private JPopupMenu createBlockPopup() {

        JPopupMenu popup = new JPopupMenu();
        ButtonGroup bg = new ButtonGroup();

        popup.add(createChangeBlockContentAction(parent, "BlockContent.None", BlockInfo.CONTENT_NONE, bg));
        popup.add(createChangeBlockContentAction(parent, "BlockContent.1Coin", BlockInfo.CONTENT_COINS_1, bg));
        popup.add(createChangeBlockContentAction(parent, "BlockContent.10Coins", BlockInfo.CONTENT_COINS_10, bg));
        popup.add(createChangeBlockContentAction(parent, "BlockContent.FireFlower",
            BlockInfo.CONTENT_FIRE_FLOWER, bg));
        popup.add(createChangeBlockContentAction(parent, "BlockContent.OneUp", BlockInfo.CONTENT_ONE_UP, bg));
        popup.add(createChangeBlockContentAction(parent, "BlockContent.Star", BlockInfo.CONTENT_STAR, bg));

        popup.addSeparator();

        String text = parent.getString("Block.Hidden");
        hiddenBlockItem = new JCheckBoxMenuItem(new ToggleBlockHiddenAction(parent, text));
        popup.add(hiddenBlockItem);

        return popup;
    }

    private JMenuItem createChangeBlockContentAction(Main parent, String textKey, int blockInfo, ButtonGroup bg) {
        String text = parent.getString(textKey);
        Action a = new ChangeBlockContentAction(parent, text, blockInfo);
        JMenuItem menuItem = new JRadioButtonMenuItem(a);
        bg.add(menuItem);
        return menuItem;
    }

    private JPopupMenu createTextBlockPopup() {

        JPopupMenu popup = new JPopupMenu();

        String name = parent.getString("ChangeText");
        ChangeBlockTextAction cbta = new ChangeBlockTextAction(parent, name);
        popup.add(new JMenuItem(cbta));

        return popup;

    }

    private JPopupMenu createWarpPopup() {

        JPopupMenu popup = new JPopupMenu();

        Action a = parent.getAction(Actions.WARP_LOCATION_ACTION);
        popup.add(new JMenuItem(a));

        return popup;

    }

    private void deleteCells(Position pos) {

        if (isCellEmpty(pos)) {
            return;
        }
        CellUndoableEdit.CellState oldState = getCellState(pos);

        int row = pos.getRow();
        int col = pos.getCol();
        tileInfo.setTerrainAllLayers(row, col, 0);
        // block info isn't used in tileInfo (too difficult!)
        //tileInfo.removeBlock(row, col);
        blocks.remove(pos);
        boolean bigThingRemoved = enemies.remove(pos)!=null;
        bigThingRemoved |= others.remove(pos)!=null;
        if (bigThingRemoved) { // Enemies and Others can take up > 1 tile
            repaint();
        }
        else {
            repaintTile(row, col);
        }

        CellUndoableEdit.CellState newState = getCellState(pos);
        undoManager.addEdit(new CellUndoableEdit(this, pos, oldState, newState));

    }

    /**
     * Similar to a paint application's "fill" or "paint can" operation, this
     * method fills the specified cell, and all adjacent cells of the same
     * tile type, with a new tile type.
     *
     * @param layer The layer to fill in.
     * @param row The row of the first cell to change.
     * @param col The column of the first cell to change.
     * @param terrainToChange The terrain being changed.
     * @param changeTo What to change the terrain to.
     */
    void doFillImpl(int layer, int row, int col, int terrainToChange,
                            int changeTo) {

        // NOTE: Do not use recursion to prevent StackOverflowErrors!
        // We use a custom dynamic array of longs here for a couple of reasons:
        //    1. Avoid object creation for each element added, which we'd do
        //       using a standard Java collection such as ArrayList.
        //    2. Data store should be unsynchronized for performance (e.g.
        //       don't use java.util.Stack or java.util.Vector).

        long start = System.nanoTime();

        DynamicLongArray toCheck = new DynamicLongArray();
        toCheck.add(((long)col<<32)|row);
        int colMax= getColumnCount() - 1;
        int rowMax = getRowCount() - 1;

        int iterCount = 0;
        while (!toCheck.isEmpty()) {
            iterCount++;
            long rowCol = toCheck.removeLastUnsafe();
            col = (int)((rowCol>>32) & 0xffffffff);
            row = (int)((rowCol) & 0xffffffff);
            int terrain = tileInfo.getTerrain(layer, row, col);
            if (terrain==terrainToChange) {
                tileInfo.setTerrain(layer, row, col, changeTo);
                if (col>0) {
                    toCheck.add(rowCol-(1L<<32)); // (tileX-1, tileY)
                }
                if (row>0) {
                    toCheck.add(rowCol-1); // (tileX, tileY-1)
                }
                if (col<colMax) {
                    toCheck.add(rowCol+(1L<<32)); // (tileX+1, tileY)
                }
                if (row<rowMax) {
                    toCheck.add(rowCol+1); // (tileX, tileY+1)
                }
            }
        }

        long time = System.nanoTime() - start;
        System.err.println("doFillImpl: Fill time (" + iterCount + "): " + (time/1000000000.0) + " seconds");

    }

    private void fillCells(Position pos) {

        ObjectType type = parent.getSelectedTileType();
        if (type!=ObjectType.TILE) { // Only handle tiles (for now, until undo)
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        int layer = parent.getActiveTileLayer();
        if (layer!=Constants.MIDDLE) { // Only allow fills in middle layer.
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        int row = pos.getRow();
        int col = pos.getCol();
        int terrainToChange = tileInfo.getTerrain(layer, row, col);
        if (terrainToChange==parent.getSelectedTileData()) {
            return; // Nothing to change.
        }

        int changeTo = parent.getSelectedTileData();
        if (changeTo!=terrainToChange) {
            doFillImpl(layer, row, col, terrainToChange, changeTo);
            FillUndoableEdit fue = new FillUndoableEdit(this, pos, layer,
                                            terrainToChange, changeTo);
            undoManager.addEdit(fue);
            repaint();
            parent.setDirty(true);
        }

    }

    /**
     * Returns the name of the background image for this level (as it should
     * be in a level file).
     *
     * @return The background image name.
     */
    public String getBackgroundImageName() {
        return bgImageName;
    }

    private BlockInfo getBlockAt(int row, int col) {
        if (row<0 || row>=getRowCount()) {
            throw new IllegalArgumentException("Invalid row: " + row);
        }
        if (col<0 || col>=getColumnCount()) {
            throw new IllegalArgumentException("Invalid col: " + col);
        }
        tempPoint.set(row, col);
        return blocks.get(tempPoint);
    }

    /**
     * Returns the number of blocks in this level.
     *
     * @return The number of blocks.
     * @see #getBlockAt(int, int)
     */
    public int getBlockCount() {
        return blocks.size();
    }

    private CellUndoableEdit.CellState getCellState(Position pos) {
        int terrain = getTerrainAllLayers(pos.getRow(), pos.getCol());
        BlockTypes bt = null;
        int blockContent = -1;
        BlockInfo bi = getBlockAt(pos.getRow(), pos.getCol());
        if (bi!=null) {
            bt = bi.getType();
            blockContent = bi.getContent();
        }
        Integer temp = enemies.get(pos);
        int enemy = temp==null ? -1 : temp;
        return new CellUndoableEdit.CellState(terrain, bt, blockContent, enemy);
    }

    /**
     * Returns the number of tiles in a "row" in the map.
     *
     * @return The length of the map, in tiles.
     * @see #getRowCount()
     * @see #getTileCount()
     */
    public int getColumnCount() {
        return tileInfo.getColumnCount();
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {

        int row = armedTile.getRow();
        int col = armedTile.getCol();

        BlockInfo bi = getBlockAt(row, col);
        if (bi!=null) {
            BlockTypes type = bi.getType();
            if (type==BlockTypes.BLOCK_INFORMATION) {
                if (textBlockPopup==null) {
                    textBlockPopup = createTextBlockPopup();
                }
                textBlockPopup.putClientProperty("blockInfo", bi);
                return textBlockPopup;
            }
            else if (type.getCanHaveContent()) {
                if (blockPopup==null) {
                    blockPopup = createBlockPopup();
                }
                blockPopup.putClientProperty("blockInfo", bi);
                int content = bi.getContent();
                ((JRadioButtonMenuItem)blockPopup.getComponent(content+1)).setSelected(true);
                hiddenBlockItem.setSelected(bi.isHidden());
                hiddenBlockItem.getAction().setEnabled(content!=BlockInfo.CONTENT_NONE);
                return blockPopup;
            }
        }

        else {
            int terrain = tileInfo.getTerrain(Constants.MIDDLE, row, col);
            terrain--;
            if (isPipeTop(terrain)) {
                if (warpPopup==null) {
                    warpPopup = createWarpPopup();
                }
                Action a = parent.getAction(Actions.WARP_LOCATION_ACTION);
                WarpInfo info = warps.get(selectedTile);
                a.putValue(EditWarpLocationAction.PROPERTY_WARP_INFO, info);
                a.putValue(EditWarpLocationAction.PROPERTY_WARP_POSITION, selectedTile.clone());
                return warpPopup;
            }
        }

        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(parent.getAction(Actions.INSERT_COLUMNS_ACTION)));
        popup.add(new JMenuItem(parent.getAction(Actions.DELETE_COLUMNS_ACTION)));
        return popup;

    }

    /**
     * Returns whether this area has flying fish.
     *
     * @return Whether this area has flying fish.
     * @see #setFlyingFish(boolean)
     */
    public boolean getFlyingFish() {
        return flyingFish;
    }

    /**
     * Returns the color to use when painting grid lines.
     *
     * @return The color to use when painting grid lines.
     * @see #getPaintGridlines()
     */
    public Color getGridlineColor() {
        return Color.WHITE;
    }

    /**
     * Returns the music for this area.
     *
     * @return The music for this area.
     * @see #setMusic(int)
     */
    public int getMusic() {
        return music;
    }

    /**
     * Returns whether grid lines should be painted.
     *
     * @return Whether grid lines should be painted.
     * @see #setPaintGridlines(boolean)
     */
    public boolean getPaintGridlines() {
        return paintGridlines;
    }

    /**
     * Returns the amount of this component to display inside a scroll
     * pane.
     *
     * @return The preferred size of the parent scroll pane's viewport.
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        // Don't use scaled size as it can get quite large.
        //return new Dimension(20*scaledTileWidth, 8*scaledTileHeight);
        return new Dimension(20*tileWidth, 8*tileHeight);
    }

    /**
     * Returns the vertical size of the map being edited, in tiles.
     *
     * @return The vertical size of the map being edited, in tiles.
     * @see #getColumnCount()
     * @see #getTileCount()
     */
    public int getRowCount() {
        return tileInfo.getRowCount();
    }

    /**
     * Returns the number of pixels to scroll when the user scrolls
     * "one block" in the parent scroll pane.
     *
     * @return The amount to scroll.
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

        int increment;

        if (orientation==SwingConstants.VERTICAL) {
            increment = visibleRect.height;
        }
        else { // SwingConstants.HORIZONTAL
            increment = visibleRect.width;
        }

        return increment;

    }

    /**
     * Returns <code>false</code> always so this component isn't resized
     * to fit in the scroll pane vertically.
     *
     * @return <code>false</code> always.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Returns <code>false</code> always so this component isn't resized
     * to fit in the scroll pane horizontally.
     *
     * @return <code>false</code> always.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Returns the amount to scroll when the user scrolls "one unit" via
     * the parent scroll pane.
     *
     * @return The amount to scroll, in pixels.
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                        int orientation, int direction) {

        int increment;

        if (orientation==SwingConstants.VERTICAL) {
            increment = scaledTileWidth;
        }
        else { // SwingConstants.HORIZONTAL
            increment = scaledTileHeight;
        }

        return increment;

    }

    /**
     * Returns the preferred size of this panel.  Since this component does
     * custom painting, we must be explicit about the size we want to be.
     *
     * @return The preferred size of this component, to display all of its
     *         contents.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getColumnCount()*scaledTileWidth,
                            getRowCount()*scaledTileHeight);
    }

    /**
     * Returns the scale being applied to the map being edited, in
     * percent.
     *
     * @return The scale of the map.
     * @see #setScale(int)
     */
    public int getScale() {
        return scalePercent;
    }

    /**
     * Returns the scaled height of a tile.
     *
     * @return The scaled height of a tile, in pixels.
     * @see #getScaledTileWidth()
     */
    public int getScaledTileHeight() {
        return scaledTileHeight;
    }

    /**
     * Returns the scaled width of a tile.
     *
     * @return The scaled width of a tile, in pixels.
     * @see #getScaledTileHeight()
     */
    public int getScaledTileWidth() {
        return scaledTileWidth;
    }

    public int getTerrainAllLayers(int row, int col) {
        return tileInfo.getTerrainAllLayers(row, col);
    }

    public int getTerrainAt(int layer, int row, int col) {
        if (row<0 || row>=getRowCount()) {
            throw new IllegalArgumentException("Invalid row: " + row);
        }
        if (col<0 || col>=getColumnCount()) {
            throw new IllegalArgumentException("Invalid col: " + col);
        }
        return tileInfo.getTerrain(layer, row, col);
    }

    /**
     * Returns the number of tiles in the map being edited.
     *
     * @return The number of tiles.
     * @see #getColumnCount()
     * @see #getRowCount()
     */
    public int getTileCount() {
        return getColumnCount() * getRowCount();
    }

    /**
     * Returns the name of the tileset for this area (as it should be in a
     * level file).
     *
     * @return The tileset image name.
     */
    public String getTilesetImageName() {
        return tilesetImageName;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Position pos = xyToCell(e.getX(), e.getY());
        BlockInfo block = getBlockAt(pos.getRow(), pos.getCol());
        if (block!=null && block.getType()==BlockTypes.BLOCK_INFORMATION) {
            return block.getText();
        }
        WarpInfo info = warps.get(pos);
        if (info!=null) {
            String text = parent.getString("ToolTip.WarpTo");
            Position toPos = info.getStartPosition();
            text = MessageFormat.format(text, info.getDestArea(),
                                        toPos.getRow(), toPos.getCol());
            return text;
        }
        return super.getToolTipText(e);
    }

public void setCellInfo(int row, int col, int terrain, BlockTypes blockType, int blockContent, int enemy) {
    tileInfo.setTerrainAllLayers(row, col, terrain);
    Position pos = new Position(row, col);
    if (blockType==null) {
        blocks.remove(pos);
    }
    else {
        addBlock(row, col, blockType, blockContent, null, false);
    }
    addEnemy(pos, enemy);
    repaint();
}

    /**
     * Called when the mouse is pressed in (or dragged through) a tile.
     *
     * @param e The mouse event.
     */
    private void handleMousePressed(MouseEvent e) {

        int mapTile = xyToTileIndex(e.getX(), e.getY());
        if (mapTile>-1) { // In case they drag the mouse off the component

            Position p = tileIndexToCell(mapTile);

            // Check button mask as getButton() only works for presses, not drags.
            if ((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)>0) {
                if (editMode==EditMode.PAINT) {
                    paintCell(p.getRow(), p.getCol());
                }
                else if (editMode==EditMode.FILL) {
                    fillCells(p);
                }
                else if (editMode==EditMode.DELETE) {
                    deleteCells(p);
                }
            }

        }

    }

    private void paintCell(int row, int col) {

        Position pos = new Position(row, col);
        CellUndoableEdit.CellState oldState = getCellState(pos);
        CellUndoableEdit.CellState newState = null;

        ObjectType type = parent.getSelectedTileType();
        int newValue = parent.getSelectedTileData();
        switch (type) {
            case TILE:
                int layer = parent.getActiveTileLayer();
                int curTile = tileInfo.getTerrain(layer, row, col);
                if (curTile!=newValue) {
                    tileInfo.setTerrain(layer, row, col, newValue);
                    parent.setDirty(true);
                    repaintTile(row, col);
                    newState = getCellState(pos);
                }
                break;
            case BLOCK: // A block or coin
                if (possiblyUpdateBlock(row, col, newValue)) {
                    newState = getCellState(pos);
                }
                break;
            case ENEMY:
                if (addEnemy(pos, newValue)) {
                    // Enemies might be > 32x32
                    repaint(); // TODO: Optimize me
                    newState = getCellState(pos);
                }
                break;
            case OTHER: // TODO: Improve me!
                if (newValue>=0 || newValue<=1) { // Big goal post, fire stick, axe
                    addOther(pos, newValue);
                }
                break;
        }

        if (newState!=null) {
            undoManager.addEdit(new CellUndoableEdit(this, pos, oldState, newState));
        }

        setArmedTile(row, col);

    }

    private boolean isCellEmpty(Position pos) {

        int row = pos.getRow();
        int col = pos.getCol();
        if (row<0 || row>=getRowCount() || col<0 || col>=getColumnCount()) {
            return true;
        }

        return getTerrainAllLayers(row, col)==0 &&
                getBlockAt(row, col)==null &&
                enemies.get(pos)==null &&
                others.get(pos)==null;

    }

    private boolean isPipeTop(int terrain) {
        // TODO: Remove magic numbers
        return (terrain>=8*15 && terrain<=8*15+9) ||
                (terrain>=10*15 && terrain<=10*15+9 && (terrain&1)==0) ||
                (terrain>=11*15 && terrain<=11*15+9 && (terrain&1)==1);
    }

    public boolean isWater() {
        return water;
    }

private Position tempPoint = new Position();
    /**
     * Paints this component.
     *
     * @param g The graphics context to paint with.
     */
    @Override
    protected void paintComponent(Graphics g) {

//        long start = System.nanoTime();
        Graphics2D g2d = (Graphics2D)g;

        super.paintComponent(g2d);

        // Paint the background image.
        // TODO: Optimize me!
        if (parent.getPaintBackground()) {
            int y = getHeight() - bgImage.getHeight(null);
            int x = 0;
            while (x<getWidth()) {
                g.drawImage(bgImage, x,y, null);
                x += bgImage.getWidth(null);
            }
        }

        // Paint only those tiles in clip bounds (usually only 2: the old
        // and the new armedTile).
        Rectangle bounds = g.getClipBounds();
        Position topLeft = xyToCell(bounds.x, bounds.y);
        Position botRight = xyToCell(bounds.x+bounds.width, bounds.y+bounds.height);
        botRight.setCol(Math.min(botRight.getCol(), getColumnCount()-1));
        botRight.setRow(Math.min(botRight.getRow(), getRowCount()-1));
        //System.err.println("... " + g.getClipBounds());
        //System.err.println("... ... Painting cells " + topLeft + " to " + bottomRight);

        Composite old = null;
        for (int layer=Constants.BACKGROUND; layer>=Constants.FOREGROUND; layer--) {

            if (!parent.getPaintLayer(layer)) {
                continue;
            }
            else if (!parent.isFocusedLayer(layer)) {
                old = g2d.getComposite();
                g2d.setComposite(ALPHA_COMPOSITE);
            }

            // Paint the 3 tiles layers and blocks
            paintTilesAndBlocks(g2d, layer, topLeft, botRight);

            if (!parent.isFocusedLayer(layer)) {
                g2d.setComposite(old);
            }

            // Paint all "other" stuff.
            if (layer==Constants.MIDDLE) {
                for (Map.Entry<Position, Integer> entry : others.entrySet()) {
                    Position pos = entry.getKey();
                    int i = entry.getValue();
                    switch (i) {
                        case 0: // Big goal post
                            int col = pos.getCol();
                            // "-2" since a goal post is 3 columns wide
                            if (col>=topLeft.getCol()-2 && col<=botRight.getCol()) {
                                paintGoalPost(g, pos);
                            }
                            break;
                        case 1: // Fire stick
                            col = pos.getCol();
                            // Fire sticks are 3 columns wide (?)
                            if (col>=topLeft.getCol()-3 && col<=botRight.getCol()) {
                                paintFireStick(g, pos);
                            }
                            break;
                        case 2: // Spring boards
                            col = pos.getCol();
                            paintSpringBoard(g, pos);
                            break;
                    }
                }
            }

        }

        paintEnemies(g2d, topLeft, botRight);

        paintWarpLocations(g2d, topLeft, botRight);

        // Paint lines dividing the tiles if desired.
        if (getPaintGridlines()) {
            paintGridlines(g2d, topLeft, botRight);
        }

        g.setColor(Color.YELLOW);
        int rx = selectedTile.getCol()*scaledTileWidth;
        int ry = selectedTile.getRow()*scaledTileHeight;
        Stroke oldStroke = g2d.getStroke();
        int w = 3;
        g2d.setStroke(SELECTED_STROKE);
        g.drawRect(rx+1,ry+1, scaledTileWidth-w,scaledTileHeight-w);
        g2d.setStroke(oldStroke);

        g.setColor(Color.RED);
        rx = armedTile.getCol()*scaledTileWidth;
        ry = armedTile.getRow()*scaledTileHeight;
        g.drawRect(rx,ry, scaledTileWidth-1,scaledTileHeight-1);

//        long time = System.nanoTime() - start;
//        System.err.println("Painting time (" + getTileCount() + "): " + (time/1000000000.0) + " seconds");

    }

    private void paintContent(Graphics2D g2d, int x, int y, int content) {
        Image img = contentImages[content];
        int imgW = img.getWidth(null)/2;
        int imgH = img.getHeight(null)/2;
        int cx = x + scaledTileWidth/2;
        int cy = y + scaledTileHeight/2;
        g2d.drawImage(img, cx-imgW,cy-imgH, null);
    }

    private void paintEnemies(Graphics2D g2d, Position topLeft, Position botRight) {
        for (int row=topLeft.getRow(); row<=botRight.getRow(); row++) {
            for (int col=topLeft.getCol(); col<=botRight.getCol(); col++) {
                int dx = col*scaledTileWidth;
                int dy = row*scaledTileHeight;
                tempPoint.set(row, col);
                Integer enemy = enemies.get(tempPoint);
                if (enemy!=null) {
                    parent.getTilePalette().paintEnemy(enemy, g2d, dx,dy, scalePercent/100f);
                }
            }
        }
    }

    private void paintSpringBoard(Graphics g, Position pos) {
        int x = colToX(pos.getCol());
        int y = rowToY(pos.getRow());
        g.drawImage(springBoardImage, x,y, null);
    }

    private void paintFireStick(Graphics g, Position pos) {

        // Center of tile we're spinning around
        int x = colToX(pos.getCol()) + 16;
        int y = rowToY(pos.getRow()) + 16;

        y -= fireStickImage.getHeight(null);
        g.drawImage(fireStickImage, x,y, null);

    }

    private void paintGoalPost(Graphics g, Position pos) {
        int col = pos.getCol();
        int x = colToX(col);
        int y = rowToY(pos.getRow());
        int w = scaledTileWidth;
        int h = scaledTileHeight;
        int endRow = Math.max(0, pos.getRow()-7);
        for (int row=pos.getRow(); row>=endRow; row--) {
            g.drawImage(goalsImage, x,     y, x+w,   y+h,  0, 34, 32, 66, null);
            g.drawImage(goalsImage, x+2*w, y, x+3*w, y+h, 34, 34, 68, 66, null);
//            if (row==endRow-2) {
//                g.drawImage(goalsImage, )
//            }
            y -= h;
        }
        if (y>=0) {
            g.drawImage(goalsImage, x,     y, x+w,   y+h,  0, 0, 32, 32, null);
            g.drawImage(goalsImage, x+2*w, y, x+3*w, y+h, 34, 0, 68, 32, null);
        }
    }

    private void paintGridlines(Graphics g, Position topLeft, Position botRight) {

        Rectangle bounds = g.getClipBounds();
        g.setColor(getGridlineColor());

        //for (int i=0; i<getHorizontalTileCount(); i++) {
        for (int col=topLeft.getCol(); col<=botRight.getCol(); col++) {
            int x = col*scaledTileWidth;
            g.drawLine(x,bounds.y, x,bounds.y+bounds.height);
        }

        //for (int i=0; i<getVerticalTileCount(); i++) {
        for (int row=topLeft.getRow(); row<=botRight.getRow(); row++) {
            int y = row*scaledTileHeight;
            g.drawLine(bounds.x,y, bounds.x+bounds.width,y);
        }

    }

    private void paintTilesAndBlocks(Graphics2D g2d, int layer,
                                    Position topLeft, Position botRight) {

        for (int row=topLeft.getRow(); row<=botRight.getRow(); row++) {
            for (int col=topLeft.getCol(); col<=botRight.getCol(); col++) {

                int dx = col*scaledTileWidth;
                int dy = row*scaledTileHeight;
                int info = tileInfo.getTerrain(layer, row, col);

                if (info>0) {
                    info--;
                    parent.getTilePalette().paintTile(info,
                                g2d, dx,dy,
                                scaledTileWidth,scaledTileHeight);
                }

                if (layer==Constants.MIDDLE) {
                    tempPoint.set(row, col);
                    BlockInfo bi = blocks.get(tempPoint);
                    if (bi!=null) {
                        int content = bi.getContent();
                        Composite oldComposite = g2d.getComposite();
                        if (bi.getType().getCanHaveContent() && content!=BlockInfo.CONTENT_NONE) {
                            g2d.setComposite(ALPHA_COMPOSITE);
                        }
                        int paletteIndex = blockTypeToBlockPaletteIndex(bi.getType());
                        parent.getTilePalette().paintBlock(paletteIndex, g2d, dx,dy,
                                dx+scaledTileWidth, dy+scaledTileHeight);
                        if (bi.getType().getCanHaveContent() && content!=BlockInfo.CONTENT_NONE) {
                            g2d.setComposite(oldComposite);
                            paintContent(g2d, dx,dy, content);
                        }
                    }
                }

            }
        }

    }

    private void paintWarpLocations(Graphics2D g2d, Position topLeft, Position botRight) {
        g2d.setColor(Color.GRAY);
        for (Map.Entry<Position, WarpInfo> entry : warps.entrySet()) {
            Position pos = entry.getKey();
            int row = pos.getRow();
            int col = pos.getCol();
            if (col>=topLeft.getCol() && col<=botRight.getCol()) {
                if (row>=topLeft.getRow() && row<=botRight.getRow()) {
                    int x = col*scaledTileWidth;
                    int y = row*scaledTileHeight;
                    for (int x0=x; x0<x+32; x0+=2) {
                        g2d.drawLine(x0,y, x+31,y+(x+32-x0));
                    }
                }
            }

        }
    }

    private boolean possiblyUpdateBlock(int row, int col, int newBlockPaletteIndex) {

        Position p = new Position(row, col);
        BlockInfo old = blocks.get(p);

        if (old==null && newBlockPaletteIndex!=0) { // No previous block for this tile
            BlockTypes type = blockPaletteIndexToBlockType(newBlockPaletteIndex);
            BlockInfo bi = new BlockInfo(type);
            blocks.put(p, bi);
            parent.setDirty(true);
            repaintTile(row, col);
            return true;
        }

        if (newBlockPaletteIndex==0) { // "Erase" a block
            System.out.println("DEBUG: Removing block from:" + row + ", " + col);
            blocks.remove(p);
            parent.setDirty(true);
            repaintTile(row, col);
            return true;
        }

        BlockTypes curType = old.getType();
        BlockTypes newType = blockPaletteIndexToBlockType(newBlockPaletteIndex);
        if (newType!=curType) {
            BlockInfo bi = new BlockInfo(newType, old.getContent());
            blocks.put(p, bi);
            parent.setDirty(true);
            repaintTile(row, col);
            return true;
        }

        return false;

    }

    public void printBlockInfo(PrintWriter w) {

        w.println("# Blocks and Coins");
        w.println(getBlockCount());

        int index = 1;
        for (Map.Entry<Position, BlockInfo> e : blocks.entrySet()) {

            Position loc = e.getKey();
            BlockInfo bi = e.getValue();
            BlockTypes type = bi.getType();

            StringBuilder sb = new StringBuilder("block ");
            sb.append(index).append(" ");
            sb.append(loc.getRow()).append(",").append(loc.getCol()).append(' ');
            sb.append(type.name());

            if (type==BlockTypes.BLOCK_INFORMATION) {
                sb.append(" \"").append(
                    org.fife.mario.Utils.createInfoBlockText(bi.getText())).append('"');
            }
            else {
                int content = bi.getContent();
                switch (content) {
                    case BlockInfo.CONTENT_COINS_1:
                        sb.append(" coin1");
                        break;
                    case BlockInfo.CONTENT_COINS_10:
                        sb.append(" coin10");
                        break;
                    case BlockInfo.CONTENT_FIRE_FLOWER:
                        sb.append(" fireflower");
                        break;
                    case BlockInfo.CONTENT_ONE_UP:
                        sb.append(" oneup");
                        break;
                    case BlockInfo.CONTENT_STAR:
                        sb.append(" star");
                }
                if (content!=BlockInfo.CONTENT_NONE){
                    sb.append(bi.isHidden() ? ",hidden" : ",visible");
                }
            }

            w.println(sb);
            index++;

        }

    }

    public void printEnemyInfo(PrintWriter w) {

        w.println("# Enemies");
        w.println(enemies.size());

        //int index = 1;
        for (Map.Entry<Position, Integer> e : enemies.entrySet()) {
            Position loc = e.getKey();
            int enemy = e.getValue();
            String s = loc.getRow() + "," + loc.getCol() + " " +
                    parent.getTilePalette().getEnemyName(enemy);
            w.println(s);
        }

    }

    public void printOtherInfo(PrintWriter w) {

        w.println("# Other");
        w.println(others.size());

        for (Map.Entry<Position, Integer> e : others.entrySet()) {
            Position loc = e.getKey();
            int other = e.getValue();
            String s = loc.getRow() + "," + loc.getCol() + " " +
                parent.getTilePalette().getOtherName(other);
            w.println(s);
        }

    }

    public void printWarps(PrintWriter w) {

        w.println("# Warps");
        w.println(warps.size());

        for (Map.Entry<Position, WarpInfo> e : warps.entrySet()) {
            Position loc = e.getKey();
            WarpInfo info = e.getValue();
            Position destPos = info.getStartPosition();
            String s = loc.getRow() + "," + loc.getCol() + " " +
                info.getDestArea() + "," + destPos.getRow() + "," + destPos.getCol();
            w.println(s);
        }

    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        switch (e.getID()) {
            case MouseEvent.MOUSE_EXITED:
                setArmedTile(-1, -1); // To "erase" the old one.
                break;
            case MouseEvent.MOUSE_PRESSED:
                handleMousePressed(e);
                int mapTile = xyToTileIndex(e.getX(), e.getY());
                if (mapTile>-1) { // Not dragged past edges.
                    Position p = tileIndexToCell(mapTile);
                    setSelectedTile(p.getRow(), p.getCol());
                }
                break;
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        switch (e.getID()) {
            case MouseEvent.MOUSE_DRAGGED:
                handleMousePressed(e);
                break;
            case MouseEvent.MOUSE_MOVED:
                int mapTile = xyToTileIndex(e.getX(), e.getY());
                if (mapTile>-1) { // Not dragged past edges.
                    Position p = tileIndexToCell(mapTile);
                    setArmedTile(p.getRow(), p.getCol());
                }
                break;
        }
        super.processMouseMotionEvent(e);
    }

    /**
     * Re-does the last action.
     *
     * @see #undo()
     */
    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
        else {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }

    /**
     * Repaints a single tile.
     *
     * @param cell The cell of the tile to paint.
     * @see #repaintTile(int, int)
     */
    public void repaintTile(Position cell) {
        if (cell.getRow()>-1) {
            repaintTile(cell.getRow(), cell.getCol());
        }
    }

    /**
     * Repaints a single tile.
     *
     * @param row The row of the file.
     * @param col The column of the tile.
     * @see #repaintTile(Position)
     */
    public void repaintTile(int row, int col) {
        repaint(col*scaledTileWidth,row*scaledTileHeight,
                scaledTileWidth,scaledTileHeight);
    }

    /**
     * Resets this editor component.
     *
     * @param rowCount
     * @param colCount
     * @param startAnew Whether to remove previous blocks and coins.
     */
    public void reset(int rowCount, int colCount, boolean startAnew) {
        if (startAnew) {
            tileInfo = new MapData(rowCount, colCount);
            blocks = new HashMap<>();
            warps = new HashMap<>();
            enemies = new TreeMap<>();
            others = new TreeMap<>();
        }
        else {
            int oldRowCount = 0;
            int oldColCount = 0;
            if (tileInfo==null) {
                tileInfo = new MapData(rowCount, colCount);
            }
            else {
                oldRowCount = tileInfo.getRowCount();
                oldColCount = tileInfo.getColumnCount();
                tileInfo.resize(rowCount, colCount);
            }
            // if rowCount<oldRowCount or colCount<oldColCount, we must remove
            // any blocks in the removed parts.  If rowCount>oldRowCount,
            // we must "move" blocks down rows (since we add rows to the "top").
            if (rowCount!=oldRowCount || colCount<oldColCount) {
                int rowDiff = rowCount-oldRowCount;
System.out.println("rowDiff==" + rowDiff);
                Map<Position, BlockInfo> blocks2 = new HashMap<>();
                for (Iterator<Map.Entry<Position, BlockInfo>> i=blocks.entrySet().iterator(); i.hasNext();) {
                    Map.Entry<Position, BlockInfo> entry = i.next();
                    Position p = entry.getKey();
                    p.incRow(rowDiff);
                    if (p.getRow()<rowCount && p.getCol()<colCount) {
System.out.println("... Keeping: " + p);
                        blocks2.put(p, entry.getValue());
                    }
                    i.remove();
                }
                blocks = blocks2;
                SortedMap<Position, Integer> enemies2 = new TreeMap<>();
                for (Iterator<Map.Entry<Position, Integer>> i=enemies.entrySet().iterator(); i.hasNext();) {
                    Map.Entry<Position, Integer> entry = i.next();
                    Position p = entry.getKey();
                    p.incRow(rowDiff);
                    if (p.getRow()<rowCount && p.getCol()<colCount) {
                        enemies2.put(p, entry.getValue());
                    }
                    i.remove();
                }
                enemies = enemies2;
            }
        }
        revalidate();
        repaint(); // revalidate() does not always cause repainting.
        SwingUtilities.invokeLater(this::scrollToBottomLeft);
    }

    private int rowToY(int row) {
        return row * scaledTileHeight;
    }

    /**
     * Scrolls this component so its bottom left-hand corner is visible.
     */
    public void scrollToBottomLeft() {
        scrollRectToVisible(new Rectangle(0,getHeight(), 0,getHeight()));
    }

    /**
     * Sets the "armed" cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    private void setArmedTile(int row, int col) {
        if (col!=armedTile.getCol() || row!=armedTile.getRow()) {
            repaintTile(armedTile);
            armedTile.set(row, col);
            repaintTile(armedTile);
            parent.armedTileChanged(row, col);
        }
    }

    public boolean setBackgroundImage(String img) {

        URL resource = getClass().getResource("/img/bg_" + img + ".png");
        if (resource == null) { // An "animated" background
            resource = getClass().getResource("/img/bg_" + img + "_1.png");
        }

        try {
            bgImage = ImageIO.read(resource);
            bgImageName = img;
System.out.println("DEBUG: bgImageName==" + bgImageName);
            repaint();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            String desc = parent.getString("Error.LoadingBG");
            parent.displayException(ioe, desc);
        }

        return false;

    }

    /**
     * Sets the current editing mode.
     *
     * @param mode The current mode.
     */
    public void setEditMode(EditMode mode) {
        this.editMode = mode;
    }

    /**
     * Toggles whether this area has flying fish.
     *
     * @param flyingFish Whether this area has flying fish.
     * @see #getFlyingFish()
     */
    public void setFlyingFish(boolean flyingFish) {
        this.flyingFish = flyingFish;
    }

    /**
     * Sets the music for this area.
     *
     * @param music The music for this area.
     * @see #getMusic()
     */
    public void setMusic(int music) {
        this.music = music;
    }

    /**
     * Sets whether grid lines should be painted.
     *
     * @param paint Whether to paint grid lines.
     * @see #getPaintGridlines()
     */
    public void setPaintGridlines(boolean paint) {
        if (paint!=this.paintGridlines) {
            this.paintGridlines = paint;
            repaint();
        }
    }

    /**
     * Sets the scale the map editor is drawn at.<p>
     *
     * This method fires a property change event of type
     * {@link #PROPERTY_SCALE}.
     *
     * @param percent The scale to draw the map editor at.  For example, "<code>100</code>" means "100%", so the map is
     *        drawn with tiles the same size as in the tile palette.  "<code>200</code>" means "200%", so tiles are
     *        drawn twice as large as in the tile palette.  You'll usually want this value to be a multiple of 2, to
     *        ensure the scaled tiles appear undistorted.
     * @throws IllegalArgumentException if <code>percent</code> is less than {@code 1} or greater than {@code 800}.
     * @see #getScale()
     */
    public void setScale(int percent) {
        if (percent<1 || percent>800) {
            throw new IllegalArgumentException("Invalid scale percentage: " +
                                                percent);
        }
        if (percent!=this.scalePercent) {
            int old = this.scalePercent;
            this.scalePercent = percent;
            float scale = (percent/100.0f);
            scaledTileWidth = (int)(tileWidth*scale);
            scaledTileHeight = (int)(tileHeight*scale);
            revalidate();    // Notify parent JScrollPane of new size.
            repaint();        // revalidate() does not cause repainting.
            firePropertyChange(PROPERTY_SCALE, old, this.scalePercent);
        }
    }

    /**
     * Sets the selected cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    private void setSelectedTile(int row, int col) {
        if (col!=selectedTile.getCol() || row!=selectedTile.getRow()) {
            repaintTile(selectedTile);
            selectedTile.set(row, col);
            repaintTile(selectedTile);
            parent.selectedTileChanged(row, col);
        }
    }

    public void setTerrainAllLayers(int row, int col, int data) {
        tileInfo.setTerrainAllLayers(row, col, data);
    }

    /**
     * Sets the name of the tileset for this area (as it should be in a
     * level file).
     *
     * @param name The tileset image name.
     * @see #getTilesetImageName()
     */
    public void setTilesetImageName(String name) {
        tilesetImageName = name;
    }

    public void setWarpInfo(Position pos, WarpInfo info) {
        if (info!=null) {
            warps.put(pos, info);
        }
        else {
            warps.remove(pos);
        }
        repaint(); // TODO: Optimize me
    }

    public void setWater(boolean water) {
        this.water = water;
    }

    private Position tileIndexToCell(int tileIndex) {
        int row = tileIndex/getColumnCount();
        int col = tileIndex%getColumnCount();
        return new Position(row, col);
    }

    /**
     * Undoes the last action.
     *
     * @see #redo()
     */
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
        else {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }

    private Position xyToCell(int x, int y) {
        return new Position(y/scaledTileHeight, x/scaledTileWidth);
    }

    private int xyToTileIndex(int x, int y) {
        int row = y/scaledTileHeight;
        if (row>=getRowCount()) {
            return -1;
        }
        int col = x/scaledTileWidth;
        if (col>=getColumnCount()) {
            return -1;
        }
        return (row*getColumnCount()) + col;
    }

}
