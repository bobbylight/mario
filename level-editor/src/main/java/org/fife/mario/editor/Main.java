package org.fife.mario.editor;

import org.fife.help.HelpDialog;
import org.fife.mario.Constants;
import org.fife.mario.Position;
import org.fife.mario.WarpInfo;
import org.fife.mario.blocks.BlockTypes;
import org.fife.mario.level.LevelFileReader;
import org.fife.mario.sound.SoundEngine;
import org.fife.ui.*;
import org.fife.ui.SplashScreen;
import org.fife.ui.app.AbstractGUIApplication;
import org.fife.ui.app.GUIApplication;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.util.MacOSUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ResourceBundle;

/**
 * A level editor for the Mario game.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Main extends AbstractGUIApplication<EditorPrefs> implements Actions {

	public static final int LAYER_FRONT						= Constants.FOREGROUND;
	public static final int LAYER_MIDDLE					= Constants.MIDDLE;
	public static final int LAYER_BACK						= Constants.BACKGROUND;
	public static final int LAYER_CURRENT					= Constants.BACKGROUND+1;
	public static final int LAYER_EMPHASIZE_CURRENT			= Constants.BACKGROUND+2;
	public static final int LAYER_ALL						= Constants.BACKGROUND+3;

	private JSplitPane splitPane;
	private EditorTabbedPane tabPane;
	private TilePalette tilePalette;
	private boolean dirty;
	private boolean paintBackground;
	private int paintLayerRule;
	private ObjectType type;
	private int selectedTileData;
	private RTextFileChooser chooser;
	private JLabel armedCellLabel;
	private JLabel selectedCellLabel;
	private Position selectedTile;
	private LevelStartInfo levelStartInfo;

	/**
	 * The current level file being edited.
	 */
	private File file;

	private static final long serialVersionUID = 1;

	public Main(EditorAppContext context, EditorPrefs prefs) {
		super(context, "Mario Level Editor", prefs);
		dirty = false;
		paintBackground = true;
		paintLayerRule = LAYER_ALL;
		selectedTile = new Position();
	}

	void armedTileChanged(int row, int col) {
		armedCellLabel.setText(getString("Status.ArmedTile", row, col));
	}

	void selectedTileChanged(int row, int col) {
		selectedTile.set(row, col);
		selectedCellLabel.setText(getString("Status.SelectedTile", row, col));
	}

	@Override
	protected AboutDialog createAboutDialog() {

		AboutDialog ad = (AboutDialog)super.createAboutDialog();

        JPanel appPanel = UIUtil.newTabbedPanePanel(new BorderLayout());
        appPanel.setBorder(UIUtil.getEmpty5Border());
        JLabel label = new JLabel(new ImageIcon(getClass().getResource("/img/AboutImage.png")));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        appPanel.add(label, BorderLayout.NORTH);
        String html = getString("Dialog.About.AboutBlurb");
        SelectableLabel blurb = new SelectableLabel(html);
        appPanel.add(blurb, BorderLayout.SOUTH);
        ad.setApplicationPanel(appPanel);
        ad.pack();

		return ad;
	}

	@Override
	protected void createActions(EditorPrefs prefs) {

		ResourceBundle msg = getResourceBundle();

		StandardAction a = new NewLevelAction(this, msg, "NewLevel");
		addAction(NEW_ACTION_KEY, a);

		addAction(OPEN_ACTION_KEY, new OpenAction(this, "OpenLevel"));

		addAction(SAVE_ACTION_KEY, new SaveAction(this, "Save"));

		addAction(SAVE_AS_ACTION_KEY, new SaveAsAction(this, msg, "SaveAs"));

		addAction(NEW_AREA_ACTION_KEY, new NewAreaAction(this, "NewArea"));

		addAction(DELETE_AREA_ACTION_KEY, new DeleteAreaAction(this, "DeleteArea"));

		addAction(EXIT_ACTION_KEY, new ExitAction<>(this, msg, "Exit"));

		addAction(UNDO_ACTION_KEY, new UndoAction(this, "Undo"));

		addAction(REDO_ACTION_KEY, new RedoAction(this, "Redo"));

		addAction(LEVEL_START_INFO_ACTION_KEY, new LevelStartInfoAction(this, msg, "LevelStartInfo"));

		a = new ChangeTilesetAction(this, "GroundBrown", "ground_brown");
		addAction(TILESET_BROWN_ACTION_KEY, a);

		a = new ChangeTilesetAction(this, "GroundBlue", "ground_blue");
		addAction(TILESET_BLUE_ACTION_KEY, a);

		a = new ChangeTilesetAction(this, "UndergroundBrown", "underground_brown");
		addAction(TILESET_UNDERGROUND_BROWN_ACTION_KEY, a);

		a = new ChangeTilesetAction(this, "CastleGray", "castle_gray");
		addAction(TILESET_CASTLE_GRAY_ACTION_KEY, a);

		a = new ChangeTilesetAction(this, "UnderwaterGreen", "underwater_green");
		addAction(TILESET_UNDERWATER_GREEN_ACTION_KEY, a);

		a = new ResizeLevelAction(this,"ResizeLevel");
		addAction(RESIZE_LEVEL_ACTION_KEY, a);

		a = new ViewLayersAction(this, msg, "AllLayers", LAYER_ALL);
		addAction(VIEW_ALL_LAYERS_ACTION, a);

		a = new ViewLayersAction(this, msg, "CurrentLayer", LAYER_CURRENT);
		addAction(VIEW_CURRENT_LAYER_ACTION, a);

		a = new ViewLayersAction(this, msg, "EmphasizeCurrentLayer", LAYER_EMPHASIZE_CURRENT);
		addAction(VIEW_EMPHASIZE_CURRENT_LAYER_ACTION, a);

		a = new ViewLayersAction(this, msg, "BackLayer", LAYER_BACK);
		addAction(VIEW_BACK_LAYER_ACTION, a);

		a = new ViewLayersAction(this, msg, "MiddleLayer", LAYER_MIDDLE);
		addAction(VIEW_MIDDLE_LAYER_ACTION, a);

		a = new ViewLayersAction(this, msg, "FrontLayer", LAYER_FRONT);
		addAction(VIEW_FRONT_LAYER_ACTION, a);

		a = new ViewBackgroundAction(this, msg, "Background");
		addAction(VIEW_BACKGROUND_ACTION, a);

		a = new ViewGridlinesAction(this, msg, "GridLines");
		addAction(VIEW_GRIDLINES_ACTION_KEY, a);

		a = new ToggleFlyingFishAction(this, msg, "FlyingFish");
		addAction(TOGGLE_FLYING_FISH_ACTION_KEY, a);

		a = new ToggleWaterPhysicsAction(this, msg, "WaterPhysics");
		addAction(TOGGLE_WATER_PHYSICS_ACTION_KEY, a);

		a = new ToggleToolBarAction<>(this, msg, "ToolBar");
		addAction(VIEW_TOOLBAR_ACTION_KEY, a);

		a = new ToggleStatusBarAction<>(this, msg, "StatusBar");
		addAction(VIEW_STATUS_BAR_ACTION_KEY, a);

		a = new GUIApplication.HelpAction<>(this, msg, "HelpTopics");
		a.setIcon(new ImageIcon("img/help.png"));
		addAction(HELP_ACTION_KEY, a);

		a = new GUIApplication.AboutAction<>(this, msg, "About");
		addAction(ABOUT_ACTION_KEY, a);

		a = new EditWarpLocationAction(this, msg, "EditWarpLocation");
		addAction(WARP_LOCATION_ACTION, a);

		a = new ChangeMusicAction(this, "Music.TitleScreen", SoundEngine.MUSIC_TITLE_SCREEN);
		addAction(MUSIC_TITLE_SCREEN_ACTION, a);

		a = new ChangeMusicAction(this, "Music.Overworld", SoundEngine.MUSIC_OVERWORLD);
		addAction(MUSIC_OVERWORLD_ACTION, a);

		a = new ChangeMusicAction(this, "Music.Underground", SoundEngine.MUSIC_UNDERGROUND);
		addAction(MUSIC_UNDERGROUND_ACTION, a);

		a = new ChangeMusicAction(this, "Music.Athletic", SoundEngine.MUSIC_ATHLETIC);
		addAction(MUSIC_ATHLETIC_ACTION, a);

		a = new ChangeMusicAction(this, "Music.Castle", SoundEngine.MUSIC_CASTLE);
		addAction(MUSIC_CASTLE_ACTION, a);

		addAction(DELETE_COLUMNS_ACTION, new DeleteColumnsAction(this, "Columns.Delete"));

		addAction(INSERT_COLUMNS_ACTION, new InsertColumnsAction(this, "Columns.Insert"));
	}

	/**
	 * Creates the file chooser to use for this application.
	 *
	 * @return The file chooser.
	 */
	private RTextFileChooser createFileChooser() {
		return new RTextFileChooser(false);
	}

	@Override
	protected JMenuBar createMenuBar(EditorPrefs prefs) {
		return new MenuBar(this);
	}

	@Override
	protected SplashScreen createSplashScreen() {
		return null;
	}

	@Override
	protected StatusBar createStatusBar(EditorPrefs prefs) {

		StatusBar sb = new StatusBar();

		StatusBarPanel sbp = new StatusBarPanel();
		armedCellLabel = new JLabel("-");
		sbp.add(armedCellLabel);
		GridBagConstraints gbc = new GridBagConstraints();
		sb.addStatusBarComponent(sbp, gbc);

		sbp = new StatusBarPanel();
		selectedCellLabel = new JLabel("-");
		sbp.add(selectedCellLabel);
		gbc = new GridBagConstraints();
		sb.addStatusBarComponent(sbp, gbc);

		return sb;
	}

	@Override
	protected CustomizableToolBar createToolBar(EditorPrefs prefs) {
		return new ToolBar(this);
	}

    @Override
    public void doExit() {
        savePreferences();
        super.doExit();
    }

	/**
	 * Verifies that the specified layer is valid.
	 *
	 * @param layer The layer to check.
	 * @throws IllegalArgumentException If <code>layer</code> is invalid.
	 */
	private void ensureValidLayer(int layer) {
		if (layer!=Constants.BACKGROUND && layer!=Constants.MIDDLE &&
				layer!=Constants.FOREGROUND) {
			throw new IllegalArgumentException("Invalid layer: " + layer);
		}
	}

	public int getActiveTileLayer() {
		return ((ToolBar)getToolBar()).getActiveTileLayer();
	}

	public String[] getAreaNames() {
		int count = tabPane.getTabCount();
		String[] names = new String[count];
		for (int i=0; i<count; i++) {
			names[i] = tabPane.getTitleAt(i);
		}
		return names;
	}

	private int getContentForString(String str) {

		int content = BlockInfo.CONTENT_NONE;

		if (str.startsWith("coin")) {
			try {
				int count = Integer.parseInt(str.substring(4));
				// We only support 1 and 10 coins for now.
				switch (count) {
					default:
					case 1:
						content = BlockInfo.CONTENT_COINS_1;
						break;
					case 10:
						content = BlockInfo.CONTENT_COINS_10;
				}
			} catch (NumberFormatException nfe) {
				displayException(nfe);
			}
		}

		else if ("fireflower".equals(str)) {
			content = BlockInfo.CONTENT_FIRE_FLOWER;
		}

		else if ("oneup".equals(str)) {
			content = BlockInfo.CONTENT_ONE_UP;
		}

		else if ("star".equals(str)) {
			content = BlockInfo.CONTENT_STAR;
		}

		else {
			throw new IllegalArgumentException("Unknown content type: " + str);
		}

		return content;
	}

	public AreaEditor getCurrentAreaEditor() {
		return tabPane.getAreaEditor();
	}

	public AreaEditor getAreaEditor(String area) {
		return tabPane.getAreaEditor(area);
	}

	@Override
	public HelpDialog getHelpDialog() {
		return null;
	}

	/**
	 * Gets information about how Mario starts the level.
	 *
	 * @return The start info.
	 * @see #setLevelStartInfo(LevelStartInfo)
	 */
	public LevelStartInfo getLevelStartInfo() {
		return levelStartInfo;
	}

	/**
	 * Returns whether the background is being painted in the editor component.
	 *
	 * @return Whether the background is being painted.
	 * @see #setPaintBackground(boolean)
	 */
	public boolean getPaintBackground() {
		return paintBackground;
	}

	/**
	 * Returns whether the specified layer should be painted in the editor
	 * component (either focused or not).
	 *
	 * @param layer The layer, such as {@link Constants#MIDDLE}.
	 * @return Whether the layer should be painted.
	 * @see #isFocusedLayer(int)
	 */
	public boolean getPaintLayer(int layer) {
		ensureValidLayer(layer);
		return paintLayerRule==LAYER_ALL || paintLayerRule==layer ||
				paintLayerRule==LAYER_EMPHASIZE_CURRENT ||
				(paintLayerRule==LAYER_CURRENT && getActiveTileLayer()==layer);
	}

	/**
	 * Returns the rule for determining whether a layer should be painted.
	 *
	 * @return The rule.
	 */
	int getPaintLayerRule() {
		return paintLayerRule;
	}

	@Override
	public String getResourceBundleClassName() {
		return "org.fife.mario.editor.LevelEditor";
	}

	String getSelectedArea() {
		return tabPane.getTitleAt(tabPane.getSelectedIndex());
	}

	public Position getSelectedTile() {
		return selectedTile.clone();
	}

	public int getSelectedTileData() {
		return selectedTileData;
	}

	public ObjectType getSelectedTileType() {
		return type;
	}

	public TilePalette getTilePalette() {
		return tilePalette;
	}

	public int getTileWidth() {
		return 32;
	}

	public int getTileHeight() {
		return 32;
	}

	@Override
	public String getVersionString() {
		return "1.0.00";
	}

	/**
	 * Returns whether the level is dirty.
	 *
	 * @return Whether the level is dirty.
	 * @see #setDirty(boolean)
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Returns whether the specified layer is focused in the editor component.
	 *
	 * @param layer The layer to check.
	 * @return Whether that layer is focused.
	 * @see #getPaintLayer(int)
	 */
	public boolean isFocusedLayer(int layer) {
		ensureValidLayer(layer);
		return paintLayerRule==LAYER_ALL || paintLayerRule==layer ||
			(getActiveTileLayer()==layer &&
					(paintLayerRule==LAYER_CURRENT ||
						paintLayerRule==LAYER_EMPHASIZE_CURRENT));
	}

	/**
	 * Called when the user wants to start creating a new area.
	 *
	 * @return Whether a new area was created.
	 */
	public boolean newArea() {

		NewAreaDialog nld = new NewAreaDialog(this, false);
		nld.setVisible(true);
		NewAreaDialog.NewAreaInfo nli = nld.getNewAreaInfo();

		if (nli!=null) {
			String name = nli.getName();
			int rowCount = nli.getRowCount();
			int colCount = nli.getColumnCount();
			AreaEditor ec = new AreaEditor(this, rowCount, colCount);
			tabPane.addEditorComponent(name, ec);
			String msg = getString("Status.NewArea",
					Integer.toString(rowCount), Integer.toString(colCount));
			levelStartInfo = new LevelStartInfo(
					LevelStartInfo.LEVEL_START_ANIMS[0], new Position(10,10));
			getStatusBar().setStatusMessage(msg);
			dirty = false;
			return true;
		}

		return false;
	}

	/**
	 * Called when the user wants to start creating a new level.
	 *
	 * @return Whether a new level was created.
	 */
	public boolean newFile() {

		if (isDirty()) {
			String message = getString("Prompt.SaveDirtyFile");
			String title = getString("Prompt.Title");
			int rc = JOptionPane.showConfirmDialog(this, message, title,
										JOptionPane.YES_NO_CANCEL_OPTION);
			if (rc==JOptionPane.YES_OPTION) {
				if (!save()) {
					// If the save operation fails, don't start a new level
					return false;
				}
			} else if (rc==JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		NewAreaDialog nld = new NewAreaDialog(this, true);
		nld.setVisible(true);
		NewAreaDialog.NewAreaInfo nli = nld.getNewAreaInfo();
		if (nli!=null) {
			String name = nli.getName(); // Should be "main"
			int rowCount = nli.getRowCount();
			int colCount = nli.getColumnCount();
			tabPane.removeAll();
			AreaEditor ec = new AreaEditor(this, rowCount, colCount);
			tabPane.addEditorComponent(name, ec);
			String msg = getString("Status.NewLevel",
					Integer.toString(rowCount), Integer.toString(colCount));
			getStatusBar().setStatusMessage(msg);
			dirty = false;
			file = null;
			return true;
		}

		return false;
	}

	/**
	 * Gives the user a JFileChooser to open a file.
	 *
	 * @see #openFile(File)
	 */
	public void openFile() {
		if (chooser==null) {
			chooser = createFileChooser();
		}
		int rc = chooser.showOpenDialog(this);
		if (rc==RTextFileChooser.APPROVE_OPTION) {
			openFile(chooser.getSelectedFile());
		}
	}

	@Override
	public void openFile(File file) {

		try {

			EditorTabbedPane tabPane = new EditorTabbedPane(this);
			LevelFileReader r = new LevelFileReader(file);

			String areaCountStr = r.readKeyValueLine("AreaCount");
			int areaCount = Integer.parseInt(areaCountStr);

			String[] temp = r.readKeyValueLine("MarioStart").split(",");
			String animName = temp[0];
			Position pos = new Position(Integer.parseInt(temp[1]),
										Integer.parseInt(temp[2]));
			levelStartInfo = new LevelStartInfo(animName, pos);

			for (int i=0; i<areaCount; i++) {
				String name = r.readKeyValueLine("Area");
				AreaEditor ec = readArea(r);
				tabPane.addEditorComponent(name, ec);
			}

			// Create and use a whole new tabbed pane, in case the load
			// fails in the middle.
			this.file = file;
			splitPane.setTopComponent(tabPane);
			this.tabPane = tabPane;
			pack();

		} catch (IOException ioe) {
			displayException(this, ioe, getString("Error.Loading"));
		}
	}

	@Override
	protected void preDisplayInit(EditorPrefs prefs, SplashScreen splash) {

	    setTitle(getString("Application.Name"));
	}

	@Override
	protected void preMenuBarInit(EditorPrefs prefs, SplashScreen splash) {

		tabPane = new EditorTabbedPane(this);
		AreaEditor ec = new AreaEditor(this, 20, 40);
		tabPane.addEditorComponent("main", ec);

		try {
			tilePalette = new TilePalette(this, getTileWidth(),
											getTileHeight(), 2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		setTileset("ground_brown");

		levelStartInfo = new LevelStartInfo(
				LevelStartInfo.LEVEL_START_ANIMS[0], new Position(10,10));

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
									tabPane, tilePalette);
		splitPane.setUI(new CleanSplitPaneUI());
		splitPane.setDividerSize(3);
		splitPane.setResizeWeight(1.0); // Give top area more area when resizing.
		getContentPane().add(splitPane);
	}

	@Override
	protected void preStatusBarInit(EditorPrefs prefs, SplashScreen splash) {
	}

	@Override
	protected void preToolBarInit(EditorPrefs prefs, SplashScreen splash) {
	}

	@Override
	public void preferences() {
	}

	public AreaEditor readArea(LevelFileReader r) throws IOException {

		// Flags describing the behavior of the area
		boolean flyingFish = false;
		boolean water = false;
		String tempStr = r.readKeyValueLine("Flags");
		if (tempStr.length()>0) {
			String[] flags = tempStr.split(",");
            for (String flag : flags) {
                if ("flying_fish".equals(flag)) {
                    flyingFish = true;
                }
                else if ("water".equals(flag)) {
                    water = true;
                }
            }
		}

		String[] dims = r.readLine().split("\\s+");
		int rowCount = Integer.parseInt(dims[0]);
		int colCount = Integer.parseInt(dims[1]);
		AreaEditor ec = new AreaEditor(this, rowCount, colCount);

		// Set any flags for the area.
		ec.setFlyingFish(flyingFish);
		ec.setWater(water);

		String bgImageName = r.readLine().trim();
		ec.setBackgroundImage(bgImageName);
		String tileset = r.readLine().trim();
		ec.setTilesetImageName(tileset);
		int music = Integer.parseInt(r.readLine().trim());
		ec.setMusic(music);

		// Read in any blocks and coins
		int blockCount = Integer.parseInt(r.readLine());
		int blockIndex = 0;
		for (int i=0; i<blockCount; i++) {
			String line = r.readLine();
			String prefix = "block " + (i+1);
			if (!line.startsWith(prefix)) {
				throw new IOException("Invalid block line: '" + line + "'");
			}
			String[] temp = line.substring(prefix.length()).trim().split("\\s+", 3);
			if (temp.length<2) {
				throw new IOException("Invalid block line: '" + line + "'");
			}
			String[] loc = temp[0].split(",");
			int row = Integer.parseInt(loc[0]);
			int col = Integer.parseInt(loc[1]);
			String typeName = temp[1];
			BlockTypes type = Enum.valueOf(BlockTypes.class, typeName);
			int content = Integer.MAX_VALUE;
			String text = null;
			boolean hidden = false;
			if (type==BlockTypes.BLOCK_INFORMATION) {
				if (temp.length==3) { // Always true
					text = org.fife.mario.Utils.getInfoBlockText(temp[2]);
				}
				System.out.println("******** " + text);
			}
			else if (temp.length>2) {
				String[] parms = temp[2].split("\\s+");
				if (parms.length<1) {
					throw new IOException("Invalid block line: '" + line + "'");
				}
				String contentStr = parms[0];
				int comma = contentStr.indexOf(',');
				if (comma>-1) {
					tempStr = contentStr.substring(0, comma);
					content = getContentForString(tempStr);
					hidden = "hidden".equals(contentStr.substring(comma+1));
				}
				else {
					content = getContentForString(contentStr);
				}
			}
			ec.addBlock(row, col, type, content, text, hidden);
			blockIndex++;
		}
		// Sanity check
		if (blockIndex!=blockCount) {
			throw new IOException("Expected " + blockCount +
								" blocks, found " + blockIndex);
		}

		// Read in enemies
		int enemyCount = Integer.parseInt(r.readLine());
		for (int i=0; i<enemyCount; i++) {
			String line = r.readLine();
			String loc = line.substring(0, line.indexOf(' '));
			String[] temp = loc.split(",");
			int row = Integer.parseInt(temp[0]);
			int col = Integer.parseInt(temp[1]);
			String name = line.substring(line.indexOf(' ')).trim();
			int type = tilePalette.getEnemyType(name);
			ec.addEnemy(new Position(row, col), type);
		}

		// Read warps
		int warpCount = Integer.parseInt(r.readLine());
		for (int i=0; i<warpCount; i++) {
			String line = r.readLine();
			String loc = line.substring(0, line.indexOf(' '));
			String[] temp = loc.split(",");
			int row = Integer.parseInt(temp[0]);
			int col = Integer.parseInt(temp[1]);
			temp = line.substring(line.indexOf(' ')+1).split(",");
			String destArea = temp[0];
			int destRow = Integer.parseInt(temp[1]);
			int destCol = Integer.parseInt(temp[2]);
			WarpInfo info = new WarpInfo(destArea);
			info.setStartPosition(new Position(destRow, destCol));
			ec.setWarpInfo(new Position(row, col), info);
		}

		// Read in "other" (goal posts, moving platforms, etc.)
		int otherCount = Integer.parseInt(r.readLine());
		for (int i=0; i<otherCount; i++) {
			String line = r.readLine();
			String loc = line.substring(0, line.indexOf(' '));
			String[] temp = loc.split(",");
			int row = Integer.parseInt(temp[0]);
			int col = Integer.parseInt(temp[1]);
			String name = line.substring(line.indexOf(' ')).trim();
			int type = tilePalette.getOtherType(name);
			ec.addOther(new Position(row, col), type);
		}

		for (int row=0; row<rowCount; row++) {
			String line = r.readLine();
			String[] cols = line.split("\\s+");
			if (cols.length!=colCount) {
                throw new IOException("Invalid terrain file (for row " + row + ", " + cols.length + "!=" +
                    colCount + ")");
			}
			for (int col=0; col<cols.length; col++) {
				int terrain = Integer.parseInt(cols[col]);
				if (terrain<0) {
					throw new IOException("Invalid terrain in level file: " + terrain);
				}
				ec.setTerrainAllLayers(row, col, terrain);
			}
		}

		ec.scrollToBottomLeft();
		return ec;
	}

	/**
	 * Re-does the last action.
	 *
	 * @see #undo()
	 */
	public void redo() {
		getCurrentAreaEditor().redo();
	}

	void removeSelectedArea() {
		tabPane.removeTabAt(tabPane.getSelectedIndex());
	}

	/**
	 * Resizes the level.
	 */
	public void resizeLevel() {
		ResizeDialog rd = new ResizeDialog(this);
		rd.setVisible(true);
		ResizeDialog.ResizeInfo ri = rd.getNewLevelInfo();
		if (ri!=null) {
			int rowCount = ri.getRowCount();
			int colCount = ri.getColumnCount();
			getCurrentAreaEditor().reset(rowCount, colCount, false);
			String msg = getString("Status.ResizedLevel",
					Integer.toString(rowCount), Integer.toString(colCount));
			getStatusBar().setStatusMessage(msg);
		}
	}

	/**
	 * Saves the current level.
	 *
	 * @return Whether the file was saved (versus the user cancelling).
	 * @see #saveAs()
	 * @see #openFile()
	 */
	public boolean save() {

		if (file==null) {
			return saveAs();
		}

		try {

			PrintWriter w = new PrintWriter(new BufferedWriter(
											new FileWriter(file)));

			w.println("### General level information ###");
			w.println("AreaCount=" + tabPane.getTabCount());
			w.println("MarioStart=" + levelStartInfo.toString());

			for (int i=0; i<tabPane.getTabCount(); i++) {
				String area = tabPane.getTitleAt(i);
				AreaEditor ec = tabPane.getAreaEditor(area);
				w.println();
				w.println("### Area " + (i+1) + ": " + area + " ###");
				w.println("Area=" + area);
				saveArea(w, ec);
			}

			w.close();
			return true;

		} catch (IOException ioe) {
			displayException(this, ioe, getString("Error.Saving"));
			return false;
		}

	}

	private void saveArea(PrintWriter w, AreaEditor ec) throws IOException {

		// Flags
		StringBuilder flags = new StringBuilder();
		if (ec.getFlyingFish()) {
			flags.append("flying_fish");
		}
		if (ec.isWater()) {
			if (flags.length()>0) {
				flags.append(",");
			}
			flags.append("water");
		}
		w.println("Flags=" + flags);

		// Header
		w.println(ec.getRowCount() + " " + ec.getColumnCount());
		w.println(ec.getBackgroundImageName());
		w.println(ec.getTilesetImageName());
		w.println(ec.getMusic());
		w.println();

		// Blocks and coins, enemies, warps and "other"
		ec.printBlockInfo(w);
		w.println();
		ec.printEnemyInfo(w);
		w.println();
		ec.printWarps(w);
		w.println();
		ec.printOtherInfo(w);
		w.println();

		// Terrain
		int colCount = ec.getColumnCount();
		for (int row=0; row<ec.getRowCount(); row++) {
			for (int col=0; col<colCount; col++) {
				w.print(ec.getTerrainAllLayers(row, col));
				if (col<colCount-1) {
					w.print(' ');
				}
			}
			w.println();
		}

	}

	/**
	 * Saves the current level with a new file name.
	 *
	 * @return Whether the file was saved (versus the user cancelling).
	 * @see #save()
	 */
	public boolean saveAs() {

		if (chooser==null) {
			chooser = createFileChooser();
		}

		int rc = chooser.showSaveDialog(this);
		if (rc==RTextFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			return save();
		}

		return false;

	}

	public void setBackgroundImageName(String img) {
		getCurrentAreaEditor().setBackgroundImage(img);
	}

	/**
	 * Sets whether the level has had any changes made.
	 *
	 * @param dirty Whether the level is dirty.
	 * @see #isDirty()
	 */
	public void setDirty(boolean dirty) {
		if (this.dirty!=dirty) {
			this.dirty = dirty;
		}
	}

	public void setEditMode(EditMode mode) {
		for (int i=0; i<tabPane.getTabCount(); i++) {
			tabPane.getEditorComponent(i).setEditMode(mode);
		}
	}

	/**
	 * Sets information about how Mario starts the level.
	 *
	 * @param lsi The new start info.
	 * @see #getLevelStartInfo()
	 */
	public void setLevelStartInfo(LevelStartInfo lsi) {
		levelStartInfo = lsi;
		setDirty(true);
	}

	/**
	 * Toggles whether the background should be painted in the editor component.
	 *
	 * @param paint Whether the background should be painted.
	 * @see #getPaintBackground()
	 */
	public void setPaintBackground(boolean paint) {
		paintBackground = paint;
		getCurrentAreaEditor().repaint();
	}

	public void setPaintLayerRule(int rule) {
		paintLayerRule = rule;
		getCurrentAreaEditor().repaint();
	}

	public void setSelectedTileData(ObjectType type, int tileData) {
		this.type = type;
		selectedTileData = tileData;
		((ToolBar)getToolBar()).setLayerComboEnabled(type==ObjectType.TILE);
	}

	public void setTileset(String tileset) {
		getCurrentAreaEditor().setTilesetImageName(tileset);
		String imgResource = "/img/" + tileset + ".png";
		tilePalette.setTileset(imgResource);
		getCurrentAreaEditor().repaint();
		String key = TILESET_BROWN_ACTION_KEY;
		if ("ground_blue".equals(tileset)) {
			key = TILESET_BLUE_ACTION_KEY;
		}
		else if ("underground_brown".equals(tileset)) {
			key = TILESET_UNDERGROUND_BROWN_ACTION_KEY;
		}
		getAction(key).putValue(Action.SELECTED_KEY, Boolean.TRUE);
	}

	/**
	 * Undoes the last action.
	 *
	 * @see #redo()
	 */
	public void undo() {
		getCurrentAreaEditor().undo();
	}



    /**
     * Program entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

        // Properties that must be set before amy AWT classes are loaded.
        // Note that some of these are also configured for our installable
        // package via jpackage, but are also set here for testing before
        // releases
        MacOSUtil.setApplicationName("Mario Level Editor");
        MacOSUtil.setApplicationAppearance(MacOSUtil.AppAppearance.SYSTEM);

        EditorAppContext context = new EditorAppContext();
        SwingUtilities.invokeLater(() -> context.createApplication(args).setVisible(true));
    }
}
