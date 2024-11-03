package org.fife.mario.editor;

import org.fife.ui.app.AppContext;

import java.io.File;

public class EditorAppContext extends AppContext<Main, EditorPrefs> {

    @Override
    protected String getPreferencesClassName() {
        return EditorPrefs.class.getName();
    }

    @Override
    public File getPreferencesDir() {
        return new File(System.getProperty("user.home"), ".mario_editor");
    }

    @Override
    public String getPreferencesFileName() {
        return "editor.properties";
    }

    @Override
    protected Main createApplicationImpl(String[] filesToOpen, EditorPrefs preferences) {
        return new Main(this, preferences);
    }
}
