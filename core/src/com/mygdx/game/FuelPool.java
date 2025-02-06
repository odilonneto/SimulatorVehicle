package com.mygdx.game;

import com.badlogic.gdx.utils.Pool;

public class FuelPool extends Pool<Fuel> {
    @Override
    protected Fuel newObject() {
        return new Fuel();
    }
}
