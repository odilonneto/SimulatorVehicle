package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture carTexture;
    private Texture roadTexture;
    private Rectangle car;
    private float carSpeed = 200f;
    private float roadY1, roadY2;
    private BitmapFont font;
    private boolean isGameOver = false;
    private int score = 0;
    private float timeSinceLastUpdate = 0f;
    private Music backgroundMusic;
    private Sound collisionSound;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        carTexture = new Texture("car.png");
        roadTexture = new Texture("road.jpg");

        car = new Rectangle();

        car.width = 150;
        car.height = 160;
        car.x = (Gdx.graphics.getWidth() - car.width) / 2;
        car.y = 100;

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
        float pistaEsquerda = 90;
        float pistaDireita = Gdx.graphics.getWidth() - 90;

        ScreenUtils.clear(0, 0.5f, 0, 1);

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            if (timeSinceLastUpdate >= 1f) {
                score += 10;
                timeSinceLastUpdate = 0f;
            }

            roadY1 -= 200 * delta;
            roadY2 -= 200 * delta;
            if (roadY1 + Gdx.graphics.getHeight() < 0) {
                roadY1 = roadY2 + Gdx.graphics.getHeight();
            }
            if (roadY2 + Gdx.graphics.getHeight() < 0) {
                roadY2 = roadY1 + Gdx.graphics.getHeight();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                car.x += carSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                car.x -= carSpeed * delta;
            }

            if (car.x < pistaEsquerda) {
                car.x = pistaEsquerda;
                isGameOver = true;
                collisionSound.play();
            }
            if (car.x + car.width > pistaDireita) {
                car.x = pistaDireita - car.width;
                isGameOver = true;
                collisionSound.play();
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                game.setScreen(new GameScreen(game));
            }
        }

        batch.begin();
        batch.draw(roadTexture, 0, roadY1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(roadTexture, 0, roadY2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(carTexture, car.x, car.y, car.width, car.height);
        if (isGameOver) {
            font.draw(batch, "Jogo encerrado.", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
            font.draw(batch, "Pressione 'R' para reiniciar.", Gdx.graphics.getWidth() / 2f - 110, Gdx.graphics.getHeight() / 2f - 50);
            font.draw(batch, "Pontuação final: " + score, Gdx.graphics.getWidth() / 2f - 80, Gdx.graphics.getHeight() / 2f - 100);
        } else {
            font.draw(batch, "Car Racing!", 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 30);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        carTexture.dispose();
        roadTexture.dispose();
        font.dispose();
        backgroundMusic.dispose();
        collisionSound.dispose();
    }
}
