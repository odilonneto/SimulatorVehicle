package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Obstacle {
    private Rectangle visualBox;
    private Rectangle collisionBox;
    private float speed;

    public Obstacle() {
        visualBox = new Rectangle();
        collisionBox = new Rectangle();
    }

    public void init(float pistaEsquerda, float pistaDireita) {
        visualBox.width = 140 * 0.8f;
        visualBox.height = 150 * 0.8f;
        visualBox.y = Gdx.graphics.getHeight();

        collisionBox.width = 50;
        collisionBox.height = 100;

        if (Math.random() < 0.5) {
            visualBox.x = pistaEsquerda + (pistaDireita - pistaEsquerda) / 4 - visualBox.width / 2;
        } else {
            visualBox.x = pistaDireita - (pistaDireita - pistaEsquerda) / 4 - visualBox.width / 2;
        }

        collisionBox.x = visualBox.x + (visualBox.width - collisionBox.width) / 2;
        collisionBox.y = visualBox.y + (visualBox.height - collisionBox.height) / 2;
        speed = 100 + (float)(Math.random() * 150);
    }

    public void update(float delta, Array<Obstacle> obstacles) {
        checkAndAdjustSpeed(obstacles);
        visualBox.y -= speed * delta;
        collisionBox.y -= speed * delta;
    }

    private void checkAndAdjustSpeed(Array<Obstacle> obstacles) {
        for (Obstacle other : obstacles) {
            if (other == this) continue;
            if (Math.abs(other.visualBox.x - this.visualBox.x) < 10) {
                if (this.visualBox.y > other.visualBox.y && this.visualBox.y - other.visualBox.y < (this.visualBox.height + 50)) {
                    this.speed = Math.min(this.speed, other.speed);
                }
            }
        }
    }

    public boolean isOffScreen() {
        return visualBox.y + visualBox.height < 0;
    }

    public Rectangle getVisualBox() {
        return visualBox;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public float getSpeed() {
        return speed;
    }
}
