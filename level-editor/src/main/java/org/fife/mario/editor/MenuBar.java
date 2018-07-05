package org.fife.mario.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.fife.mario.editor.Actions.ChangeMusicAction;
import org.fife.mario.editor.Actions.ChangeTilesetAction;
import org.fife.ui.app.AppAction;


/**
 * The menu bar for the level editor.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MenuBar extends org.fife.ui.app.MenuBar {

	private static final long serialVersionUID = 1L;

	private Main app;
	private JMenu bgMenu;
	private JMenu tilesetMenu;
	private JMenu musicMenu;

	MenuBar(Main app) {

		this.app = app;

		ResourceBundle msg = app.getResourceBundle();

		JMenu menu = createMenu(msg, "File");
		menu.add(new JMenuItem(app.getAction(Main.NEW_ACTION_KEY)));
		menu.add(new JMenuItem(app.getAction(Main.OPEN_ACTION_KEY)));
		menu.add(new JMenuItem(app.getAction(Main.SAVE_ACTION_KEY)));
		menu.add(new JMenuItem(app.getAction(Main.SAVE_AS_ACTION_KEY)));
		menu.addSeparator();
		menu.add(new JMenuItem(app.getAction(Main.NEW_AREA_ACTION_KEY)));
		menu.add(new JMenuItem(app.getAction(Main.DELETE_AREA_ACTION_KEY)));
		menu.addSeparator();
		JMenuItem item = createMenuItem(msg, "Exit");
		item.setAction(app.getAction(Main.EXIT_ACTION_KEY));
		menu.add(item);
		add(menu);

		menu = createMenu(msg, "Edit");
		menu.add(createMenuItem(app.getAction(Main.UNDO_ACTION_KEY)));
		menu.add(createMenuItem(app.getAction(Main.REDO_ACTION_KEY)));
		menu.addSeparator();
		bgMenu = createMenu(msg, "Background");
		ButtonGroup bg = new ButtonGroup();
		Action a = new ChangeBackgroundAction(app, "Hills", "hills");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "GreenStuff", "green_stuff");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "GrayMountains", "gray_mountains");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "Cave", "cave");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "Fortress", "fortress");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "Castle", "castle");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "GhostHouse", "ghost_house");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "Night", "night");
		addRBItem(a, bg, bgMenu);
		a = new ChangeBackgroundAction(app, "Underwater", "underwater");
		addRBItem(a, bg, bgMenu);
		menu.add(bgMenu);
		tilesetMenu = createMenu(msg, "Tileset");
		bg = new ButtonGroup();
		addRBItem(app.getAction(Main.TILESET_BROWN_ACTION_KEY), bg, tilesetMenu);
		addRBItem(app.getAction(Main.TILESET_BLUE_ACTION_KEY), bg, tilesetMenu);
		addRBItem(app.getAction(Main.TILESET_UNDERGROUND_BROWN_ACTION_KEY), bg, tilesetMenu);
		addRBItem(app.getAction(Main.TILESET_CASTLE_GRAY_ACTION_KEY), bg, tilesetMenu);
		addRBItem(app.getAction(Main.TILESET_UNDERWATER_GREEN_ACTION_KEY), bg, tilesetMenu);
		menu.add(tilesetMenu);
		musicMenu = createMenu(msg, "Music");
		bg = new ButtonGroup();
		addRBItem(app.getAction(Main.MUSIC_TITLE_SCREEN_ACTION), bg, musicMenu);
		addRBItem(app.getAction(Main.MUSIC_OVERWORLD_ACTION), bg, musicMenu);
		addRBItem(app.getAction(Main.MUSIC_UNDERGROUND_ACTION), bg, musicMenu);
		addRBItem(app.getAction(Main.MUSIC_ATHLETIC_ACTION), bg, musicMenu);
		addRBItem(app.getAction(Main.MUSIC_CASTLE_ACTION), bg, musicMenu);
		menu.add(musicMenu);
		menu.addSeparator();
		addCBItem(app.getAction(Main.TOGGLE_FLYING_FISH_ACTION_KEY), menu);
		addCBItem(app.getAction(Main.TOGGLE_WATER_PHYSICS_ACTION_KEY), menu);
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(Main.LEVEL_START_INFO_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(Main.RESIZE_LEVEL_ACTION_KEY)));
		add(menu);

		menu = createMenu(msg, "View");
		JMenu menu2 = createMenu(msg, "Layers");
		bg = new ButtonGroup();
		a = app.getAction(Main.VIEW_ALL_LAYERS_ACTION);
		addRBItem(a, bg, menu2).setSelected(true);
		a = app.getAction(Main.VIEW_CURRENT_LAYER_ACTION);
		addRBItem(a, bg, menu2);
		a = app.getAction(Main.VIEW_EMPHASIZE_CURRENT_LAYER_ACTION);
		addRBItem(a, bg, menu2);
		menu2.addSeparator();
		a = app.getAction(Main.VIEW_FRONT_LAYER_ACTION);
		addRBItem(a, bg, menu2);
		a = app.getAction(Main.VIEW_MIDDLE_LAYER_ACTION);
		addRBItem(a, bg, menu2);
		a = app.getAction(Main.VIEW_BACK_LAYER_ACTION);
		addRBItem(a, bg, menu2);
		menu.add(menu2);
		menu.addSeparator();
		JCheckBoxMenuItem cbItem = createCBItem(Main.VIEW_BACKGROUND_ACTION);
		menu.add(cbItem);
		cbItem = createCBItem(Main.VIEW_TOOLBAR_ACTION_KEY);
		menu.add(cbItem);
		cbItem = createCBItem(Main.VIEW_STATUS_BAR_ACTION_KEY);
		menu.add(cbItem);
		cbItem = createCBItem(Main.VIEW_GRIDLINES_ACTION_KEY);
		menu.add(cbItem);
		add(menu);

		menu = createMenu(msg, "Help");
		a = app.getAction(Main.HELP_ACTION_KEY);
		menu.add(createMenuItem(a));
		a = app.getAction(Main.ABOUT_ACTION_KEY);
		menu.add(createMenuItem(a));
		add(menu);

	}


	private JCheckBoxMenuItem addCBItem(Action a, JMenu menu) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(a);
		menu.add(item);
		return item;
	}


	private JRadioButtonMenuItem addRBItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
		return item;
	}


	public void currentEditorComponentChanged(AreaEditor ec) {

		String bgImage = ec.getBackgroundImageName();
		for (int i=0; i<bgMenu.getMenuComponentCount(); i++) {
			Component c = bgMenu.getMenuComponent(i);
			if (c instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)c;
				ChangeBackgroundAction cba = (ChangeBackgroundAction)item.getAction();
				String img = cba.getImage();
				if (img.equals(bgImage)) {
					item.setSelected(true);
					cba.actionPerformed(null);
					break;
				}
			}
		}

		String tilesetImage = ec.getTilesetImageName();
		for (int i=0; i<tilesetMenu.getMenuComponentCount(); i++) {
			Component c = tilesetMenu.getMenuComponent(i);
			if (c instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)c;
				ChangeTilesetAction cta = (ChangeTilesetAction)item.getAction();
				String img = cta.getImage();
				if (img.equals(tilesetImage)) {
					item.setSelected(true);
					cta.actionPerformed(null);
					break;
				}
			}
		}

		int music = ec.getMusic();
		for (int i=0; i<musicMenu.getMenuComponentCount(); i++) {
			Component c = musicMenu.getMenuComponent(i);
			if (c instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)c;
				ChangeMusicAction cma = (ChangeMusicAction)item.getAction();
				if (cma.getMusic()==music) {
					item.setSelected(true);
					//cma.actionPerformed(null);
					break;
				}
			}
		}

		// Set checked state for all flag-related actions.
		app.getAction(Main.TOGGLE_FLYING_FISH_ACTION_KEY).putValue(
									Action.SELECTED_KEY, ec.getFlyingFish());
		app.getAction(Main.TOGGLE_WATER_PHYSICS_ACTION_KEY).putValue(
									Action.SELECTED_KEY, ec.isWater());

	}


	private JCheckBoxMenuItem createCBItem(String actionKey) {
		JCheckBoxMenuItem item= new JCheckBoxMenuItem(app.getAction(actionKey));
		item.setSelected(true);
		return item;
	}


	/**
	 * Changes the background of this level.
	 */
	private class ChangeBackgroundAction extends AppAction<Main> {

		private static final long serialVersionUID = 1L;

		private String img;

		ChangeBackgroundAction(Main app, String key, String img) {
			super(app, app.getResourceBundle(), key);
			this.img = img;
		}

		public void actionPerformed(ActionEvent e) {
			app.setBackgroundImageName(img);
		}

		public String getImage() {
			return img;
		}

	}


}
