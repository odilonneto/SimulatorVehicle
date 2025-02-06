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

    private Rectangle buttonRestartBounds;

    public GameInputProcessor(Rectangle buttonRestartBounds) {
        this.buttonRestartBounds = buttonRestartBounds;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRestartHovered() {
        return restartHovered;
    }

    public boolean isRestartClicked() {
        return restartClicked;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

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
        if (buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
            restartClicked = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
        }
        restartClicked = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        restartHovered = buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
