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

        Theme ocean = new Theme(
                "Ocean",
                Color.rgb(1, 56, 95),
                Color.rgb(66, 66, 128),
                Color.rgb(3, 190, 238),
                CellShade.B,
                CellShadeDirection.MIN,
                Color.rgb(3, 255, 238),
                Color.rgb(1, 56, 180),
                CellShade.B,
                CellShadeDirection.MAX,
                Color.rgb(1, 56, 110)
        );
        themes.add(ocean);

        Theme sunset = new Theme(
                "Sunset",
                Color.rgb(255, 128, 0),
                Color.rgb(255, 192, 128),
                Color.rgb(255, 255, 255),
                CellShade.R,
                CellShadeDirection.MIN,
                Color.rgb(255, 255, 0),
                Color.rgb(192, 96, 0),
                CellShade.B,
                CellShadeDirection.MAX,
                Color.rgb(64, 0, 0)
        );
        themes.add(sunset);

        Theme neon = new Theme(
                "Neon",
                Color.rgb(0, 0, 0),
                Color.rgb(64, 64, 64),
                Color.rgb(255, 255, 255),
                CellShade.G,
                CellShadeDirection.MAX,
                Color.rgb(255, 0, 255),
                Color.rgb(64, 192, 64),
                CellShade.R,
                CellShadeDirection.MIN,
                Color.rgb(0, 0, 255)
        );
        themes.add(neon);

        Theme desert = new Theme(
                "Desert",
                Color.rgb(255, 255, 191),
                Color.rgb(255, 255, 128),
                Color.rgb(128, 64, 0),
                CellShade.G,
                CellShadeDirection.MIN,
                Color.rgb(255, 255, 0),
                Color.rgb(191, 128, 64),
                CellShade.R,
                CellShadeDirection.MAX,
                Color.rgb(255, 128, 0)
        );
        themes.add(desert);

        Theme winter = new Theme(
                "Winter",
                Color.rgb(192, 220, 255),
                Color.rgb(128, 160, 192),
                Color.rgb(0, 0, 0),
                CellShade.B,
                CellShadeDirection.MAX,
                Color.rgb(255, 255, 255),
                Color.rgb(96, 128, 160),
                CellShade.G,
                CellShadeDirection.MIN,
                Color.rgb(0, 32, 64)
        );
        themes.add(winter);

        Theme occupied = new Theme(
                "Occupied",
                Color.rgb(0, 0, 0),
                Color.rgb(80, 80, 80),
                Color.rgb(240, 240, 240),
                CellShade.B,
                CellShadeDirection.MAX,
                Color.rgb(240, 240, 240),
                Color.rgb(240, 240, 240),
                CellShade.G,
                CellShadeDirection.MIN,
                Color.rgb(240, 240, 240)
        );
        themes.add(occupied);

        this.currentTheme = classic;
        logger.info("ThemeManager init");
    }

    public String nextTheme() {
        int currentThemeIndex = themes.indexOf(currentTheme);
        int nextThemeIndex = currentThemeIndex + 1;
        if(nextThemeIndex >= themes.size()) {
            nextThemeIndex = 0;
        }
        return themes.get(nextThemeIndex).getTHEME_NAME();
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
