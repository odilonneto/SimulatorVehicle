package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private float pistaEsquerda;
    private float pistaDireita;
    private final Main game;
    private SpriteBatch batch;
    private Texture carSpriteSheet;
    private TextureRegion[] straightFrames;
    private TextureRegion[] leftFrames;
    private TextureRegion[] rightFrames;
    private TextureRegion currentFrame;
    private Texture roadTexture;
    private Texture obstacleTexture;
    private Texture brakeLightsTexture; // Textura dos faróis ligados
    private Rectangle car;
    private Rectangle collisionBox;
    private Array<Obstacle> obstacles;
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
    private float animationTime = 0f;

    // Botão reiniciar
    private Texture buttonRestartTexture;
    private Texture buttonRestartHoverTexture;
    private Texture buttonRestartClickedTexture;
    private Rectangle buttonRestartBounds;
    private boolean isRestartHovered = false;

    // Efeito de partículas
    private ParticleEffect fireEffect;
    private boolean isParticleActive = false;

    private class Obstacle {
        Rectangle visualBox;
        Rectangle collisionBox;
        float speed;
    }

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        carSpriteSheet = new Texture("SportsCar.png");
        roadTexture = new Texture("road.jpg");
        obstacleTexture = new Texture("car.png");
        brakeLightsTexture = new Texture("BrakeLightsOn.png"); // Carregar a textura dos faróis

        // Botões
        buttonRestartTexture = new Texture("button_restart.png");
        buttonRestartHoverTexture = new Texture("button_restart_hover.png");
        buttonRestartClickedTexture = new Texture("button_restart_clicked.png");

        buttonRestartBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 100,
                Gdx.graphics.getHeight() / 2f - 75,
                200,
                50
        );

        TextureRegion[][] tmpFrames = TextureRegion.split(carSpriteSheet,
                carSpriteSheet.getWidth() / 7, carSpriteSheet.getHeight());

        straightFrames = new TextureRegion[]{tmpFrames[0][0]};
        leftFrames = new TextureRegion[]{tmpFrames[0][1], tmpFrames[0][2], tmpFrames[0][3]};
        rightFrames = new TextureRegion[]{tmpFrames[0][4], tmpFrames[0][5], tmpFrames[0][6]};

        currentFrame = straightFrames[0];

        car = new Rectangle();
        car.width = 75 * 0.8f;
        car.height = 150 * 0.8f;
        car.x = (Gdx.graphics.getWidth() - car.width) / 2;
        car.y = 50;

        collisionBox = new Rectangle();
        collisionBox.width = 50;
        collisionBox.height = 100;
        collisionBox.x = car.x + (car.width - collisionBox.width) / 2;
        collisionBox.y = car.y;

        obstacles = new Array<>();

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        pistaEsquerda = 130;
        pistaDireita = Gdx.graphics.getWidth() - 130;

        // Inicializar efeito de partículas
        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("fire.p"), Gdx.files.internal(""));
        fireEffect.scaleEffect(0.5f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            obstacleSpawnTime += delta;
            animationTime += delta;

            if (timeSinceLastUpdate >= 1f) {
                score += 10;
                timeSinceLastUpdate = 0f;
                carSpeed += 5;
                obstacleSpeed += 5;
            }

            roadY1 -= obstacleSpeed * delta;
            roadY2 -= obstacleSpeed * delta;
            if (roadY1 + Gdx.graphics.getHeight() < 0) {
                roadY1 = roadY2 + Gdx.graphics.getHeight();
            }
            if (roadY2 + Gdx.graphics.getHeight() < 0) {
                roadY2 = roadY1 + Gdx.graphics.getHeight();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                car.x += 70f * delta;
                currentFrame = rightFrames[(int) (animationTime * 10) % rightFrames.length];
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                car.x -= 70f * delta;
                currentFrame = leftFrames[(int) (animationTime * 10) % leftFrames.length];
            } else {
                currentFrame = straightFrames[0];
            }

            collisionBox.x = car.x + (car.width - collisionBox.width) / 2;
            collisionBox.y = car.y;

            if (car.x < pistaEsquerda) {
                car.x = pistaEsquerda;
                triggerGameOver();
            }
            if (car.x + car.width > pistaDireita) {
                car.x = pistaDireita - car.width;
                triggerGameOver();
            }

            if (obstacleSpawnTime >= 2f) {
                spawnObstacle();
                obstacleSpawnTime = 0f;
            }

            Iterator<Obstacle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Obstacle obstacle = iter.next();
                obstacle.visualBox.y -= obstacle.speed * delta;
                obstacle.collisionBox.y -= obstacle.speed * delta;

                if (obstacle.visualBox.y + obstacle.visualBox.height < 0) {
                    iter.remove();
                }
                if (collisionBox.overlaps(obstacle.collisionBox)) {
                    triggerGameOver();
                }
            }
        } else {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            isRestartHovered = buttonRestartBounds.contains(mouseX, mouseY);

            if (isRestartHovered && Gdx.input.isTouched()) {
                game.setScreen(new GameScreen(game));
            }
        }

        batch.begin();
        batch.draw(roadTexture, 0, roadY1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(roadTexture, 0, roadY2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(currentFrame, car.x, car.y, car.width, car.height);

        // Desenhar faróis quando o carro estiver se movendo
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            batch.draw(brakeLightsTexture, car.x, car.y, car.width, car.height);
        }

        for (Obstacle obstacle : obstacles) {
            batch.draw(obstacleTexture, obstacle.visualBox.x, obstacle.visualBox.y, obstacle.visualBox.width, obstacle.visualBox.height);
        }
        if (isParticleActive) {
            fireEffect.update(delta);
            fireEffect.draw(batch);
        }
        if (isGameOver) {
            font.draw(batch, "Jogo encerrado.", Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 80);
            font.draw(batch, "Pontuação final: " + score, Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 100);

            if (isRestartHovered) {
                if (Gdx.input.isTouched()) {
                    batch.draw(buttonRestartClickedTexture, buttonRestartBounds.x, buttonRestartBounds.y, buttonRestartBounds.width, buttonRestartBounds.height);
                } else {
                    batch.draw(buttonRestartHoverTexture, buttonRestartBounds.x, buttonRestartBounds.y, buttonRestartBounds.width, buttonRestartBounds.height);
                }
            } else {
                batch.draw(buttonRestartTexture, buttonRestartBounds.x, buttonRestartBounds.y, buttonRestartBounds.width, buttonRestartBounds.height);
            }
        } else {
            font.draw(batch, "Car Racing!", 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 30);
        }
        batch.end();
    }

    private void spawnObstacle() {
        Obstacle obstacle = new Obstacle();
        obstacle.visualBox = new Rectangle();
        obstacle.visualBox.width = 140 * 0.8f;
        obstacle.visualBox.height = 150 * 0.8f;
        obstacle.visualBox.y = Gdx.graphics.getHeight();
        obstacle.collisionBox = new Rectangle();
        obstacle.collisionBox.width = 50;
        obstacle.collisionBox.height = 100;
        if (Math.random() < 0.5) {
            obstacle.visualBox.x = pistaEsquerda + (pistaDireita - pistaEsquerda) / 4 - obstacle.visualBox.width / 2;
        } else {
            obstacle.visualBox.x = pistaDireita - (pistaDireita - pistaEsquerda) / 4 - obstacle.visualBox.width / 2;
        }
        obstacle.collisionBox.x = obstacle.visualBox.x + (obstacle.visualBox.width - obstacle.collisionBox.width) / 2;
        obstacle.collisionBox.y = obstacle.visualBox.y + (obstacle.visualBox.height - obstacle.collisionBox.height) / 2;
        obstacle.speed = 100 + (float) (Math.random() * 150);
        obstacles.add(obstacle);
    }

    private void triggerGameOver() {
        isGameOver = true;
        collisionSound.play();
        backgroundMusic.stop();
        isParticleActive = true;
        fireEffect.setPosition(car.x + car.width / 2, car.y + car.height / 2);
        fireEffect.start();
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
        carSpriteSheet.dispose();
        roadTexture.dispose();
        obstacleTexture.dispose();
        brakeLightsTexture.dispose(); // Dispose da textura dos faróis
        font.dispose();
        backgroundMusic.dispose();
        collisionSound.dispose();
        buttonRestartTexture.dispose();
        buttonRestartHoverTexture.dispose();
        buttonRestartClickedTexture.dispose();
        fireEffect.dispose();
    }
}
