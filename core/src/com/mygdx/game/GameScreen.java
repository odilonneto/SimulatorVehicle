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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Iterator;

public class GameScreen implements Screen {
    private float pistaEsquerda;
    private float pistaDireita;
    private final Main game;
    private SpriteBatch batch;
    private Texture carTexture;
    private Texture roadTexture;
    private Texture obstacleTexture;
    private Rectangle car;
    private Array<Rectangle> obstacles;
    private float roadY1, roadY2;
    private BitmapFont font;
    private boolean isGameOver = false;
    private int score = 0;
    private float timeSinceLastUpdate = 0f;
    private float obstacleSpawnTime = 0f;
    private Music backgroundMusic;
    private Sound collisionSound;
    private float carSpeed = 200f;
    private float obstacleSpeed = 200f;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        carTexture = new Texture("car.png");
        roadTexture = new Texture("road.jpg");
        obstacleTexture = new Texture("car.png");

        car = new Rectangle();
        car.width = 140 * 0.8f; // Reduzindo a largura da zona de colisão do carro
        car.height = 150 * 0.8f; // Reduzindo a altura da zona de colisão do carro
        car.x = (Gdx.graphics.getWidth() - car.width) / 2;
        car.y = 50;

        obstacles = new Array<>();

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        pistaEsquerda = 125;
        pistaDireita = Gdx.graphics.getWidth() - 125;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            obstacleSpawnTime += delta;

            // Aumenta a pontuação a cada segundo
            if (timeSinceLastUpdate >= 1f) {
                score += 10;
                timeSinceLastUpdate = 0f;
                carSpeed += 5;  // Aumenta a velocidade com o tempo
                obstacleSpeed += 5;
            }

            // Movimento da estrada
            roadY1 -= obstacleSpeed * delta;
            roadY2 -= obstacleSpeed * delta;
            if (roadY1 + Gdx.graphics.getHeight() < 0) {
                roadY1 = roadY2 + Gdx.graphics.getHeight();
            }
            if (roadY2 + Gdx.graphics.getHeight() < 0) {
                roadY2 = roadY1 + Gdx.graphics.getHeight();
            }

            // Controle do carro
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                car.x = pistaDireita - car.width;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                car.x = pistaEsquerda;
            }

            // Limita a movimentação do carro
            if (car.x < pistaEsquerda) {
                car.x = pistaEsquerda;
                triggerGameOver();
            }
            if (car.x + car.width > pistaDireita) {
                car.x = pistaDireita - car.width;
                triggerGameOver();
            }

            // Spawning de obstáculos
            if (obstacleSpawnTime >= 2f) {
                spawnObstacle();
                obstacleSpawnTime = 0f;
            }

            // Atualiza e verifica colisões com obstáculos
            Iterator<Rectangle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Rectangle obstacle = iter.next();
                obstacle.y -= obstacleSpeed * delta;
                if (obstacle.y + obstacle.height < 0) {
                    iter.remove();
                }
                if (obstacle.overlaps(car)) {
                    triggerGameOver();
                }
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                game.setScreen(new GameScreen(game));
            }
        }

        // Renderização
        batch.begin();
        batch.draw(roadTexture, 0, roadY1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(roadTexture, 0, roadY2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(carTexture, car.x, car.y, car.width, car.height);
        for (Rectangle obstacle : obstacles) {
            batch.draw(obstacleTexture, obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }
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

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();
        obstacle.width = car.width; // Reduzindo a largura da zona de colisão do obstáculo
        obstacle.height = car.height; // Reduzindo a altura da zona de colisão do obstáculo

        // Escolhe aleatoriamente entre a pista esquerda ou direita
        if (Math.random() < 0.5) {
            obstacle.x = pistaEsquerda + (car.width - obstacle.width) / 2; // Centralizando o obstáculo na pista esquerda
        } else {
            obstacle.x = pistaDireita - car.width + (car.width - obstacle.width) / 2; // Centralizando o obstáculo na pista direita
        }

        obstacle.y = Gdx.graphics.getHeight();
        obstacles.add(obstacle);
    }


    private void triggerGameOver() {
        isGameOver = true;
        collisionSound.play();
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
        obstacleTexture.dispose();
        font.dispose();
        backgroundMusic.dispose();
        collisionSound.dispose();
    }
}