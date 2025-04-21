package org.example.quanlybanhang.utils;

import javafx.scene.Scene;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final String DARK_MODE_KEY = "darkMode";
    private static final String LIGHT_THEME = "/org/example/quanlybanhang/views/css/light_Mode.css";
    private static final String DARK_THEME = "/org/example/quanlybanhang/views/css/dark_Mode.css";

    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    public static boolean isDarkMode() {
        return prefs.getBoolean(DARK_MODE_KEY, false);
    }

    public static void setDarkMode(boolean darkMode) {
        prefs.putBoolean(DARK_MODE_KEY, darkMode);
    }

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode()) {
            scene.getStylesheets().add(ThemeManager.class.getResource(DARK_THEME).toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource(LIGHT_THEME).toExternalForm());
        }
    }

    public static void toggleTheme(Scene scene) {
        setDarkMode(!isDarkMode());
        applyTheme(scene);
    }
}