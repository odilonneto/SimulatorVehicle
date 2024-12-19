package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;

import java.util.ArrayList;

public class GameScreen implements Screen {
	private SpriteBatch batch;
	private ArrayList<Veiculo> veiculos;

	public GameScreen() {
		batch = new SpriteBatch();
		veiculos = new ArrayList<>();

		// Adicionando veículos
		veiculos.add(new CarEsportivo(1, 50000, 4));
		veiculos.add(new Bike(2, 800, 2));
	}

	@Override
	public void render(float delta) {
		batch.begin();
		for (Veiculo veiculo : veiculos) {
			veiculo.render(batch);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		for (Veiculo veiculo : veiculos) {
			veiculo.dispose();
		}
	}

	// Métodos não utilizados neste exemplo
	@Override public void show() {}
	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}
}
