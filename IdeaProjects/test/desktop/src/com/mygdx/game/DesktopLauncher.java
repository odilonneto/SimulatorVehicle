package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {
	public static void main(String[] arg) {
		// Configuração da aplicação
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Jogo de Corrida de Obstáculos"); // Título da janela
		config.setWindowedMode(800, 600);                // Dimensão inicial da janela
		config.setResizable(false);                      // Define se a janela pode ser redimensionada
		config.useVsync(true);                           // Habilita VSync para evitar tearing
		config.setForegroundFPS(60);                     // Define o FPS máximo da aplicação

		// Inicia o jogo
		new Lwjgl3Application(new Main(), config);
	}
}
