package com.mygdx.game;

import com.badlogic.gdx.utils.Pool;

public class ObstaclePool extends Pool<Obstacle> {
    @Override
    protected Obstacle newObject() {
        return new Obstacle();
    }
}
