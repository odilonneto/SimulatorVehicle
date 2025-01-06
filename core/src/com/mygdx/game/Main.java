package com.mygdx.game;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        this.setScreen(new GameScreen(this));
    }
}
