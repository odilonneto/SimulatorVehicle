import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private Carro carro;
    private ArrayList<Obstacle> obstaculos;
    private Texture fundo;
    private float tempo;
    private Sound somColisao;
    private Music musicaFundo;
    private boolean gameOver;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        carro = new Carro(100, 200);
        obstaculos = new ArrayList<>();
        fundo = new Texture("pista.jpg");

        musicaFundo.setLooping(true);
        musicaFundo.play();
        gameOver = false;
    }

    @Override
    public void render(float delta) {
        if (!gameOver) {
            // Movimento do carro
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) carro.mover(200 * delta);
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) carro.mover(-200 * delta);

            // Criar obstáculos
            tempo += delta;
            if (tempo > 1.5) {
                obstaculos.add(new Obstacle(800, (float) (Math.random() * 400)));
                tempo = 0;
            }

            // Verificar colisão
            for (Obstacle obs : obstaculos) {
                obs.mover(delta);
                if (obs.x < carro.x + 64 && obs.x > carro.x && obs.y < carro.y + 64 && obs.y > carro.y - 64) {
                    somColisao.play();
                    gameOver = true;
                }
            }
        }

        // Renderização
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fundo, 0, 0);
        carro.render(batch);
        for (Obstacle obs : obstaculos) obs.render(batch);
        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        fundo.dispose();
        musicaFundo.dispose();
        somColisao.dispose();
    }
}
