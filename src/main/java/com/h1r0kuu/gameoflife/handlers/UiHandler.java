package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class UiHandler {

    private final GameManager gameManager;

    public UiHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void handlePauseButtonClick(MouseEvent e) {
        gameManager.setPaused(!gameManager.isPaused());
        gameManager.setPausedByButton(gameManager.isPaused());
    }

    public void handleThemeChange(Number newValue, ComboBox<String> themes) {
        String newTheme = themes.getItems().get((Integer) newValue);
//        GameManager.themeManager.changeTheme(newTheme);
        gameManager.changeTheme(newTheme);
    }

    public void handleSearchTextChange(String newValue, ListView<String> patternList, ObservableList<String> patterns) {
        patternList.getItems().clear();
        patternList.getItems().addAll(patterns.filtered(p -> p.toLowerCase().contains(newValue.toLowerCase())));
    }

    public void handleGameSpeedChange(Number new_val, Label gameSpeedLabel, Slider gameSpeed) {
        int newValue = new_val.intValue();
        gameManager.setGameSpeed(newValue);
        gameSpeedLabel.setText(LabelUtility.getText(LabelUtility.GAME_SPEED, newValue));
        int percentage = (int) ((newValue * 100) / gameSpeed.getMax());
        String style = String.format("-track-color: linear-gradient(to right, #0096c9 %d%%, rgb(80,80,80) %d%%);", percentage, percentage);
        gameSpeed.setStyle(style);
    }

    public void handleRandomProbabilityChange(Number new_val, Label randomProbablityLabel, Slider randomProbability) {
        int newValue = new_val.intValue();
        randomProbablityLabel.setText(LabelUtility.getText(LabelUtility.RANDOM_PERCENTAGE, newValue));
        int percentage = (int) ((newValue * 100) / randomProbability.getMax());
        String style = String.format("-track-color: linear-gradient(to right, #0096c9 %d%%, rgb(80,80,80) %d%%);", percentage, percentage);
        randomProbability.setStyle(style);
    }

    public void handleOnPauseOnDrawButtonClick(MouseEvent e, Button pauseOnDraw) {
        boolean isPauseWhenDraw = gameManager.isPauseWhenDraw();
        gameManager.setPauseWhenDraw(!isPauseWhenDraw);
        if(isPauseWhenDraw) {
           pauseOnDraw.setStyle(Constants.IDLE_BUTTON);
        } else {
            pauseOnDraw.setStyle(Constants.ACTIVE_BUTTON);
        }
    }
}
