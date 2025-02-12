package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Fuel {
    private Rectangle visualBox;

    public Fuel() {
        visualBox = new Rectangle();
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
    }

    public void update(float delta, float speed) {
        visualBox.y -= speed * delta;
    }

    public boolean isOffScreen() {
        return visualBox.y + visualBox.height < 0;
    }

    public Rectangle getVisualBox() {
        return visualBox;
    }
}
