package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Fuel {
    private Rectangle visualBox;
    private Rectangle collisionBox;

    public Fuel() {
        visualBox = new Rectangle();
        collisionBox = new Rectangle();
    }

    public void init(float pistaEsquerda, float pistaDireita) {
        visualBox.width = 80;
        visualBox.height = 80;
        visualBox.y = Gdx.graphics.getHeight();
        if (Math.random() < 0.5) {
            visualBox.x = pistaEsquerda + (pistaDireita - pistaEsquerda) / 4 - visualBox.width / 2;
        } else {
            visualBox.x = pistaDireita - (pistaDireita - pistaEsquerda) / 4 - visualBox.width / 2;
        }

        float collisionWidth = visualBox.width * 0.7f;
        float collisionHeight = visualBox.height * 0.7f;
        float collisionX = visualBox.x + (visualBox.width - collisionWidth) / 2f;
        float collisionY = visualBox.y + (visualBox.height - collisionHeight) / 2f;
        collisionBox.set(collisionX, collisionY, collisionWidth, collisionHeight);
    }

    public void update(float delta, float speed) {
        visualBox.y -= speed * delta;
        collisionBox.x = visualBox.x + (visualBox.width - collisionBox.width) / 2f;
        collisionBox.y = visualBox.y + (visualBox.height - collisionBox.height) / 2f;
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
}
