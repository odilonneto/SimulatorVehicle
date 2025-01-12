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
    private Rectangle car; // Retângulo usado para desenhar o carro
    private Rectangle collisionBox; // Retângulo usado para colisões
    private Array<Obstacle> obstacles; // Array de obstáculos
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

    // Classe interna para representar o obstáculo
    private class Obstacle {
        Rectangle visualBox;      // Retângulo visual do obstáculo
        Rectangle collisionBox;   // Retângulo de colisão do obstáculo
        float speed;              // Velocidade do obstáculo
    }

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
        car.width = 140 * 0.8f; // Tamanho visual do carro
        car.height = 150 * 0.8f;
        car.x = (Gdx.graphics.getWidth() - car.width) / 2;
        car.y = 50;

        collisionBox = new Rectangle();
        collisionBox.width = 50;  // Ajuste para corresponder à área de colisão desejada
        collisionBox.height = 100;
        collisionBox.x = car.x + (car.width - collisionBox.width) / 2; // Centraliza no carro
        collisionBox.y = car.y;

        obstacles = new Array<>(); // Inicializa a lista de obstáculos

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        pistaEsquerda = 100;
        pistaDireita = Gdx.graphics.getWidth() - 100;

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
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                car.x += carSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                car.x -= carSpeed * delta;
            }

            // Atualiza a posição da área de colisão
            collisionBox.x = car.x + (car.width - collisionBox.width) / 2;
            collisionBox.y = car.y;

            // Limita a movimentação do carro
            if (car.x < pistaEsquerda) {
                car.x = pistaEsquerda;
                isGameOver = true;
                collisionSound.play();
                triggerGameOver();
            }
            if (car.x + car.width > pistaDireita) {
                car.x = pistaDireita - car.width;
                isGameOver = true;
                collisionSound.play();
                triggerGameOver();
            }

            // Spawning de obstáculos
            if (obstacleSpawnTime >= 2f) {
                spawnObstacle();
                obstacleSpawnTime = 0f;
            }

            // Atualiza e verifica colisões com obstáculos
            Iterator<Obstacle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Obstacle obstacle = iter.next();
                obstacle.visualBox.y -= obstacle.speed * delta;
                obstacle.collisionBox.y -= obstacle.speed * delta;

                if (obstacle.visualBox.y + obstacle.visualBox.height < 0) {
                    iter.remove();
                }
                if (collisionBox.overlaps(obstacle.collisionBox)) { // Verifica com a área de colisão
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
        for (Obstacle obstacle : obstacles) {
            batch.draw(obstacleTexture, obstacle.visualBox.x, obstacle.visualBox.y, obstacle.visualBox.width, obstacle.visualBox.height);
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
        // Cria um novo obstáculo
        Obstacle obstacle = new Obstacle();

        // Configura o retângulo visual
        obstacle.visualBox = new Rectangle();
        obstacle.visualBox.width = car.width;  // Tamanho visual do obstáculo
        obstacle.visualBox.height = car.height;
        obstacle.visualBox.y = Gdx.graphics.getHeight();

        // Configura o retângulo de colisão
        obstacle.collisionBox = new Rectangle();
        obstacle.collisionBox.width = 50;  // Tamanho da área de colisão
        obstacle.collisionBox.height = 100;

        // Posiciona o obstáculo (visual e colisão)
        if (Math.random() < 0.5) {
            obstacle.visualBox.x = pistaEsquerda + (pistaDireita - pistaEsquerda) / 4 - obstacle.visualBox.width / 2;
        } else {
            obstacle.visualBox.x = pistaDireita - (pistaDireita - pistaEsquerda) / 4 - obstacle.visualBox.width / 2;
        }
        obstacle.collisionBox.x = obstacle.visualBox.x + (obstacle.visualBox.width - obstacle.collisionBox.width) / 2;
        obstacle.collisionBox.y = obstacle.visualBox.y + (obstacle.visualBox.height - obstacle.collisionBox.height) / 2;

        // Define a velocidade do obstáculo (diferente da do carro principal)
        obstacle.speed = 100 + (float)(Math.random() * 150); // Velocidade do obstáculo aleatória

        // Adiciona à lista de obstáculos
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
