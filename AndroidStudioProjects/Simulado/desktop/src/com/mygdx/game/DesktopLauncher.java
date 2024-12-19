package com.mygdx.game;


import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.CorridaGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Simulador de Corrida");
		config.setWindowedMode(800, 600);
		config.setResizable(false);
		new Lwjgl3Application(new CorridaGame(), config);
	}
}