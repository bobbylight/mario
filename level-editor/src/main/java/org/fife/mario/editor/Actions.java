package org.fife.mario.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.*;

import org.fife.mario.Position;
import org.fife.mario.WarpInfo;
import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.UIUtil;
import org.fife.ui.app.AppAction;


/**
 * Actions used by the editor.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface Actions {

    String NEW_ACTION_KEY = "newAction";
    String OPEN_ACTION_KEY = "openAction";
    String REDO_ACTION_KEY = "redoAction";
    String RESIZE_LEVEL_ACTION_KEY = "resizeLevelAction";
    String SAVE_ACTION_KEY = "saveAction";
    String SAVE_AS_ACTION_KEY = "saveAsAction";
    String NEW_AREA_ACTION_KEY = "newAreaAction";
    String DELETE_COLUMNS_ACTION = "deleteColumnsAction";
    String DELETE_AREA_ACTION_KEY = "deleteAreaAction";
    String INSERT_COLUMNS_ACTION = "insertColumnsAction";
    String LEVEL_START_INFO_ACTION_KEY = "levelStartInfoAction";
    String MUSIC_ATHLETIC_ACTION = "athleticMusicAction";
    String MUSIC_CASTLE_ACTION = "castleMusicAction";
    String MUSIC_OVERWORLD_ACTION = "overworldMusicAction";
    String MUSIC_TITLE_SCREEN_ACTION = "titleScreenMusicAction";
    String MUSIC_UNDERGROUND_ACTION = "undergroundMusicAction";
    String TILESET_BROWN_ACTION_KEY = "brownTilesetAction";
    String TILESET_BLUE_ACTION_KEY = "blueTilesetAction";
    String TILESET_CASTLE_GRAY_ACTION_KEY = "grayCastleTilesetAction";
    String TILESET_UNDERGROUND_BROWN_ACTION_KEY = "undergroundBrownTilesetAction";
    String TILESET_UNDERWATER_GREEN_ACTION_KEY = "underwaterGreenTilesetAction";
    String TOGGLE_FLYING_FISH_ACTION_KEY = "flyingFishAction";
    String TOGGLE_WATER_PHYSICS_ACTION_KEY = "waterPhysicsAction";
    String UNDO_ACTION_KEY = "undoAction";
    String VIEW_ALL_LAYERS_ACTION = "viewAllLayersAction";
    String VIEW_BACKGROUND_ACTION = "viewBackgroundAction";
    String VIEW_BACK_LAYER_ACTION = "viewBackLayerAction";
    String VIEW_CURRENT_LAYER_ACTION = "viewEmphasizeCurrentLayerAction";
    String VIEW_EMPHASIZE_CURRENT_LAYER_ACTION = "viewCurrentLayerAction";
    String VIEW_FRONT_LAYER_ACTION = "viewFrontLayerAction";
    String VIEW_GRIDLINES_ACTION_KEY = "viewGridlinesAction";
    String VIEW_MIDDLE_LAYER_ACTION = "viewMiddleLayerAction";
    String VIEW_STATUS_BAR_ACTION_KEY = "viewStatusBarAction";
    String VIEW_TOOLBAR_ACTION_KEY = "viewToolBarAction";
    String WARP_LOCATION_ACTION = "warpLocationAction";

    /**
     * Changes the music for the current area.
     */
    class ChangeMusicAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        private int music;

        ChangeMusicAction(Main app, String key, int music) {
            super(app, app.getResourceBundle(), key);
            this.music = music;
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().getCurrentAreaEditor().setMusic(music);
        }

        public int getMusic() {
            return music;
        }

    }

    /**
     * Changes the tileset of this level.
     */
    class ChangeTilesetAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        private String img;

        ChangeTilesetAction(Main app, String key, String img) {
            super(app, app.getResourceBundle(), key);
            this.img = img;
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().setTileset(img);
        }

        public String getImage() {
            return img;
        }

    }

    class DeleteAreaAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        DeleteAreaAction(Main app, String name) {
            super(app, name, "/img/cross.png");
        }

        public void actionPerformed(ActionEvent e) {

            Main main = getApplication();
            String area = main.getSelectedArea();

            if ("main".equals(area)) {
                UIManager.getLookAndFeel().provideErrorFeedback(main);
                return;
            }

            String title = main.getString("Dialog.DeleteAreaConfirm.Title");
            String text = main.getString("Dialog.DeleteAreaConfirm.Text", area);
            int rc = JOptionPane.showConfirmDialog(main, text, title,
                JOptionPane.YES_NO_OPTION);

            if (rc == JOptionPane.YES_OPTION) {
                main.removeSelectedArea();
            }

        }

    }

    class DeleteColumnsAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        DeleteColumnsAction(Main app, String name) {
            super(app, app.getResourceBundle(), name);
            //setIcon(new ImageIcon("/img/cross.png"));
        }

        public void actionPerformed(ActionEvent e) {
            Main app = getApplication();
            JOptionPane.showMessageDialog(app, "Not implemented", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    class EditWarpLocationAction extends AppAction<Main> {

        static final String PROPERTY_WARP_INFO = "warpInfo";
        static final String PROPERTY_WARP_POSITION = "warpPosition";

        private static final long serialVersionUID = 1L;

        EditWarpLocationAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            Main app = getApplication();
            String[] areas = app.getAreaNames();
            WarpLocationDialog wld = new WarpLocationDialog(app, areas);
            Position pos = (Position)getValue(PROPERTY_WARP_POSITION);
            WarpInfo info = (WarpInfo)getValue(PROPERTY_WARP_INFO);
            wld.setWarpInfo(info);
            wld.setVisible(true);
            info = wld.getWarpInfo();
            if (info != null) { // User didn't cancel
                app.getCurrentAreaEditor().setWarpInfo(pos, info);
            }
        }

    }

    class InsertColumnsAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        InsertColumnsAction(Main app, String name) {
            super(app, app.getResourceBundle(), name);
            //setIcon(new ImageIcon("/img/cross.png"));
        }

        public void actionPerformed(ActionEvent e) {
            Main app = getApplication();
            InsertSomethingDialog isd = new InsertSomethingDialog(app, false);
            isd.setVisible(true);
            int toAdd = isd.getToAdd();
            JOptionPane.showMessageDialog(app, "Not implemented, would have added: " + toAdd, "Error",
                JOptionPane.ERROR_MESSAGE);
        }

    }

    class LevelStartInfoAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        LevelStartInfoAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
            //setIcon(new ImageIcon("/img/picture.png"));
        }

        public void actionPerformed(ActionEvent e) {
            Main app = getApplication();
            LevelStartInfoDialog lsid = new LevelStartInfoDialog(app);
            lsid.setVisible(true);
            if (lsid.info != null) {
                app.setLevelStartInfo(lsid.info);
            }
        }

        private static class LevelStartInfoDialog extends EscapableDialog
            implements ActionListener {

            private static final long serialVersionUID = 1L;

            private JComboBox<String> animCombo;
            private JTextField rowField;
            private JTextField colField;
            private LevelStartInfo info;

            LevelStartInfoDialog(Main app) {

                super(app);
                LevelStartInfo lsi = app.getLevelStartInfo();

                JPanel cp = new ResizableFrameContentPane(new BorderLayout());
                cp.setBorder(UIUtil.getEmpty5Border());

                JPanel panel = new JPanel(new SpringLayout());
                JLabel animLabel = new JLabel(app.getString("Dialog.LevelStartInfo.Animation"));
                animCombo = new JComboBox<>(LevelStartInfo.LEVEL_START_ANIMS);
                animCombo.setSelectedItem(lsi.getAnimation());
                animCombo.setEditable(false);
                JLabel rowLabel = new JLabel(app.getString("Dialog.LevelStartInfo.Row"));
                rowField = new JTextField(Integer.toString(lsi.getPosition().getRow()));
                JLabel colLabel = new JLabel(app.getString("Dialog.LevelStartInfo.Column"));
                colField = new JTextField(Integer.toString(lsi.getPosition().getCol()));
                if (getComponentOrientation().isLeftToRight()) {
                    panel.add(animLabel);
                    panel.add(animCombo);
                    panel.add(rowLabel);
                    panel.add(rowField);
                    panel.add(colLabel);
                    panel.add(colField);
                } else {
                    panel.add(animCombo);
                    panel.add(animLabel);
                    panel.add(rowField);
                    panel.add(rowLabel);
                    panel.add(colField);
                    panel.add(colLabel);
                }
                UIUtil.makeSpringCompactGrid(panel, 3, 2, 5, 5, 5, 5);
                cp.add(panel, BorderLayout.NORTH);

                JButton okButton = new JButton(app.getString("Button.OK"));
                okButton.setActionCommand("OK");
                okButton.addActionListener(this);
                JButton cancelButton = new JButton(app.getString("Button.Cancel"));
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(this);
                cp.add(UIUtil.createButtonFooter(okButton, cancelButton), BorderLayout.SOUTH);

                getRootPane().setDefaultButton(okButton);
                setContentPane(cp);
                setTitle(app.getString("Dialog.LevelStartInfo.Title"));
                setModal(true);
                pack();
                setLocationRelativeTo(app);

            }

            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("OK".equals(command)) {
                    try {
                        int row = Integer.parseInt(rowField.getText());
                        int col = Integer.parseInt(colField.getText());
                        info = new LevelStartInfo(
                            (String)animCombo.getSelectedItem(),
                            new Position(row, col));
                    } catch (NumberFormatException nfe) {
                        UIManager.getLookAndFeel().provideErrorFeedback(null);
                    }
                    escapePressed();
                } else { // "Cancel"
                    escapePressed();
                }
            }

        }

    }

    class NewAreaAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        NewAreaAction(Main app, String name) {
            super(app, name, "/img/picture.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().newArea();
        }

    }

    class NewLevelAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        NewLevelAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
            //setIcon(new ImageIcon("/img/picture.png"));
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().newFile();
        }

    }

    class OpenAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        OpenAction(Main app, String name) {
            super(app, name, "/img/folder.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().openFile();
        }

    }

    class RedoAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        RedoAction(Main app, String name) {
            super(app, name, "/img/arrow_redo.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().redo();
        }

    }

    class ResizeLevelAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        ResizeLevelAction(Main app, String name) {
            super(app, name, "/img/arrow_out.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().resizeLevel();
        }

    }

    class SaveAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        SaveAction(Main app, String name) {
            super(app, name, "/img/disk.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().save();
        }
    }

    class SaveAsAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        SaveAsAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().saveAs();
        }

    }

    class ToggleFlyingFishAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        ToggleFlyingFishAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            AreaEditor editor = getApplication().getCurrentAreaEditor();
            boolean flyingFish = !editor.getFlyingFish();
            editor.setFlyingFish(flyingFish);
            putValue(SELECTED_KEY, flyingFish);
        }

    }

    class ToggleWaterPhysicsAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        ToggleWaterPhysicsAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            AreaEditor editor = getApplication().getCurrentAreaEditor();
            boolean water = !editor.isWater();
            editor.setWater(water);
            putValue(SELECTED_KEY, water);
        }

    }

    class UndoAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        UndoAction(Main app, String name) {
            super(app, name, "/img/arrow_undo.png");
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().undo();
        }

    }

    class ViewBackgroundAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        ViewBackgroundAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            Main app = getApplication();
            app.setPaintBackground(!app.getPaintBackground());
        }

    }

    class ViewGridlinesAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        ViewGridlinesAction(Main app, ResourceBundle msg, String name) {
            super(app, msg, name);
        }

        public void actionPerformed(ActionEvent e) {
            AreaEditor ec = getApplication().getCurrentAreaEditor();
            ec.setPaintGridlines(!ec.getPaintGridlines());
        }

    }

    class ViewLayersAction extends AppAction<Main> {

        private static final long serialVersionUID = 1L;

        private int rule;

        ViewLayersAction(Main app, ResourceBundle msg, String name, int rule) {
            super(app, msg, name);
            this.rule = rule;
        }

        public void actionPerformed(ActionEvent e) {
            getApplication().setPaintLayerRule(rule);
        }

    }
}
