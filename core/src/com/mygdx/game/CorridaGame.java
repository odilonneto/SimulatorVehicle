package com.mygdx.game;


import com.badlogic.gdx.Game;

public class CorridaGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}