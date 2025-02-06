package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PlayerCar {
    private Rectangle bounds;
    private Rectangle collisionBox;
    private TextureRegion currentFrame;
    private TextureRegion[] straightFrames;
    private TextureRegion[] leftFrames;
    private TextureRegion[] rightFrames;
    private float carSpeed = 100f;

    public PlayerCar(TextureRegion[] straightFrames, TextureRegion[] leftFrames, TextureRegion[] rightFrames) {
        this.straightFrames = straightFrames;
        this.leftFrames = leftFrames;
        this.rightFrames = rightFrames;

        bounds = new Rectangle();
        bounds.width = 75 * 0.8f;
        bounds.height = 150 * 0.8f;
        bounds.x = (Gdx.graphics.getWidth() - bounds.width) / 2;
        bounds.y = 50;

        collisionBox = new Rectangle();
        collisionBox.width = 50;
        collisionBox.height = 100;
        updateCollisionBox();

        currentFrame = straightFrames[0];
    }

    public void update(float delta, float speed, float animationTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bounds.x += carSpeed * delta;
            currentFrame = rightFrames[(int)(animationTime * 10) % rightFrames.length];
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bounds.x -= carSpeed * delta;
            currentFrame = leftFrames[(int)(animationTime * 10) % leftFrames.length];
        } else {
            currentFrame = straightFrames[0];
        }
        updateCollisionBox();
    }

    private void updateCollisionBox() {
        collisionBox.x = bounds.x + (bounds.width - collisionBox.width) / 2;
        collisionBox.y = bounds.y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public float getX() {
        return bounds.x;
    }

    public void setX(float x) {
        bounds.x = x;
        updateCollisionBox();
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public void dispose() {
        // Caso seja necessário liberar recursos do carro
    }
}
