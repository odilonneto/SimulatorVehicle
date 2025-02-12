package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;

public class GameInputProcessor implements InputProcessor {
    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean restartHovered = false;
    private boolean restartClicked = false;
    private boolean startHovered = false;
    private boolean startClicked = false;
    private boolean menuClicked = false;
    private boolean menuHovered = false;

    private Rectangle buttonRestartBounds;
    private Rectangle buttonStartBounds;
    private Rectangle buttonMenuBounds;

    private Rectangle carDefaultBounds;
    private Rectangle carBlueBounds;
    private Rectangle carPurpleBounds;
    private String selectedCar;

    public GameInputProcessor(Rectangle buttonStartBounds, Rectangle buttonRestartBounds,  Rectangle buttonMenuBounds) {
        this.buttonStartBounds = buttonStartBounds;
        this.buttonRestartBounds = buttonRestartBounds;
        this.buttonMenuBounds = buttonMenuBounds;
    }

    public GameInputProcessor(Rectangle buttonStartBounds, Rectangle buttonRestartBounds, Rectangle buttonMenuBounds,
                              Rectangle carDefaultBounds, Rectangle carBlueBounds, Rectangle carPurpleBounds) {
        this.buttonStartBounds = buttonStartBounds;
        this.buttonRestartBounds = buttonRestartBounds;
        this.buttonMenuBounds = buttonMenuBounds;
        this.carDefaultBounds = carDefaultBounds;
        this.carBlueBounds = carBlueBounds;
        this.carPurpleBounds = carPurpleBounds;
        this.selectedCar = null;
    }

    public boolean isRightPressed() { return rightPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRestartHovered() { return restartHovered; }
    public boolean isRestartClicked() { return restartClicked; }
    public boolean isStartHovered() { return startHovered; }
    public boolean isStartClicked() { return startClicked; }
    public boolean isMenuHovered() { return menuHovered; }
    public boolean isMenuClicked() { return menuClicked; }
    public String getSelectedCar() { return selectedCar; }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int flippedY = Gdx.graphics.getHeight() - screenY;
        if (buttonRestartBounds != null && buttonRestartBounds.contains(screenX, flippedY)) {
            restartClicked = true;
            return true;
        }
        if (buttonStartBounds != null && buttonStartBounds.contains(screenX, flippedY)) {
            startClicked = true;
            return true;
        }
        if (buttonMenuBounds != null && buttonMenuBounds.contains(screenX, flippedY)) {
            menuClicked = true;
            return true;
        }
        if (carDefaultBounds != null && carDefaultBounds.contains(screenX, flippedY)) {
            selectedCar = "SportsCar.png";
            return true;
        } else if (carBlueBounds != null && carBlueBounds.contains(screenX, flippedY)) {
            selectedCar = "SportsCar-Sheet-Blue.png";
            return true;
        } else if (carPurpleBounds != null && carPurpleBounds.contains(screenX, flippedY)) {
            selectedCar = "SportsCar-Sheet-Purple.png";
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        restartClicked = false;
        startClicked = false;
        menuClicked = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int flippedY = Gdx.graphics.getHeight() - screenY;
        if (buttonRestartBounds != null) {
            restartHovered = buttonRestartBounds.contains(screenX, flippedY);
        }
        if (buttonStartBounds != null) {
            startHovered = buttonStartBounds.contains(screenX, flippedY);
        }
        if (buttonMenuBounds != null) {
            menuHovered = buttonMenuBounds.contains(screenX, flippedY);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
