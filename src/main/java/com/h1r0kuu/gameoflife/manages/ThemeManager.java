package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.enums.CellShade;
import com.h1r0kuu.gameoflife.enums.CellShadeDirection;
import com.h1r0kuu.gameoflife.models.Theme;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThemeManager {
    private static final Logger logger = LogManager.getLogger(ThemeManager.class);

    private final List<Theme> themes = new ArrayList<>();
    private Theme currentTheme;

    public ThemeManager() {
        Theme classic = new Theme(
                "Classic",
                Color.rgb(255, 255, 255),
                Color.rgb(192, 192, 192),
                Color.rgb(0, 0, 0),
                CellShade.G,
                CellShadeDirection.MIN,
                Color.rgb(0, 0, 0),
                Color.rgb(255, 255, 255),
                CellShade.R,
                CellShadeDirection.MAX,
                Color.rgb(255, 255, 255)
        );
        themes.add(classic);

        Theme fire = new Theme(
                "Fire",
                Color.rgb(0, 0, 0),
                Color.rgb(80, 80, 80),
                Color.rgb(255, 144, 0),
                CellShade.G,
                CellShadeDirection.MIN,
                Color.rgb(255, 255, 0),
                Color.rgb(160, 0, 0),
                CellShade.R,
                CellShadeDirection.MAX,
                Color.rgb(32, 0, 0)
        );
        themes.add(fire);

        Theme poison = new Theme(
                "Poison",
                Color.rgb(0, 0, 0),
                Color.rgb(80, 80, 80),
                Color.rgb(0, 255, 255),
                CellShade.R,
                CellShadeDirection.MIN,
                Color.rgb(255, 255, 255),
                Color.rgb(0, 128, 0),
                CellShade.G,
                CellShadeDirection.MAX,
                Color.rgb(0, 24, 0)
        );
        themes.add(poison);

        Theme book = new Theme(
                "Book",
                Color.rgb(255, 255, 255),
                Color.rgb(192, 192, 192),
                Color.rgb(0, 0, 0),
                CellShade.R,
                CellShadeDirection.MIN,
                Color.rgb(0, 0, 0),
                Color.rgb(192, 220, 255),
                CellShade.B,
                CellShadeDirection.MAX,
                Color.rgb(255, 220, 192)
        );
        themes.add(book);

        this.currentTheme = classic;
        logger.info("ThemeManager init");
    }

    public void nextTheme() {
        int currentThemeIndex = themes.indexOf(currentTheme);
        int nextThemeIndex = currentThemeIndex + 1;
        if(nextThemeIndex >= themes.size()) {
            nextThemeIndex = 0;
        }
        this.currentTheme = themes.get(nextThemeIndex);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void changeTheme(String themeName) {
        Optional<Theme> theme = themes.stream().filter(th -> th.getTHEME_NAME().equals(themeName)).findFirst();
        theme.ifPresent(value -> this.currentTheme = value);
    }

    public List<Theme> getThemes() {
        return themes;
    }
}
