package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Carro {
    public float x, y; // Position
    private final Texture texture;

    public Carro(float x, float y) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("carro.png");
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void mover(float deltaY) {
        this.y += deltaY;
    }
}
