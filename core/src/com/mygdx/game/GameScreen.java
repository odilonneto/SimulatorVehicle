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
import com.badlogic.gdx.assets.AssetManager;

import java.util.Iterator;

public class GameScreen implements Screen {
    private float pistaEsquerda;
    private float pistaDireita;
    private final Main game;
    private SpriteBatch batch;
    private AssetManager assetManager;
    private TextureRegion[] straightFrames;
    private TextureRegion[] leftFrames;
    private TextureRegion[] rightFrames;
    private TextureRegion currentFrame;
    private Rectangle car;
    private Rectangle collisionBox;
    private Array<Obstacle> obstacles;
    private Array<Fuel> fuels;
    private float roadY1, roadY2;
    private BitmapFont font;
    private boolean isGameOver = false;
    private int score = 0;
    private int fuelCollected = 0;
    private float timeSinceLastUpdate = 0f;
    private float obstacleSpawnTime = 0f;
    private float fuelSpawnTime = 0f;
    private float carSpeed = 200f;
    private float obstacleSpeed = 200f;
    private float animationTime = 0f;

    private Rectangle buttonRestartBounds;
    private boolean isRestartHovered = false;

    private ParticleEffect fireEffect;
    private boolean isParticleActive = false;
    private boolean areHeadLightsBlinking = false;
    private float blinkTime = 0f;

    private class Obstacle {
        Rectangle visualBox;
        Rectangle collisionBox;
        float speed;
    }

    private class Fuel {
        Rectangle visualBox;
    }

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();

        // Carregar recursos com AssetManager
        assetManager.load("SportsCar.png", Texture.class);
        assetManager.load("road.jpg", Texture.class);
        assetManager.load("car.png", Texture.class);
        assetManager.load("BrakeLightsOn.png", Texture.class);
        assetManager.load("HeadLightsOn.png", Texture.class);
        assetManager.load("gasolina.png", Texture.class);
        assetManager.load("button_restart.png", Texture.class);
        assetManager.load("button_restart_hover.png", Texture.class);
        assetManager.load("button_restart_clicked.png", Texture.class);
        assetManager.load("background_music.mp3", Music.class);
        assetManager.load("collision_sound.mp3", Sound.class);
        assetManager.load("fuel_sound.mp3", Sound.class);
        assetManager.finishLoading();

        Texture carSpriteSheet = assetManager.get("SportsCar.png", Texture.class);

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
        fuels = new Array<>();

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        Music backgroundMusic = assetManager.get("background_music.mp3", Music.class);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        pistaEsquerda = 130;
        pistaDireita = Gdx.graphics.getWidth() - 130;

        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("fire.p"), Gdx.files.internal(""));
        fireEffect.scaleEffect(0.5f);

        buttonRestartBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 100,
                Gdx.graphics.getHeight() / 2f - 75,
                200,
                50
        );
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            obstacleSpawnTime += delta;
            fuelSpawnTime += delta;
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
                car.x += 100f * delta;
                currentFrame = rightFrames[(int) (animationTime * 10) % rightFrames.length];
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                car.x -= 100f * delta;
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

            if (fuelSpawnTime >= 3f) {
                spawnFuel();
                fuelSpawnTime = 0f;
            }

            Iterator<Obstacle> obstacleIter = obstacles.iterator();
            while (obstacleIter.hasNext()) {
                Obstacle obstacle = obstacleIter.next();
                obstacle.visualBox.y -= obstacle.speed * delta;
                obstacle.collisionBox.y -= obstacle.speed * delta;

                if (obstacle.visualBox.y + obstacle.visualBox.height < 0) {
                    obstacleIter.remove();
                }
                if (collisionBox.overlaps(obstacle.collisionBox)) {
                    triggerGameOver();
                }
            }

            Iterator<Fuel> fuelIter = fuels.iterator();
            while (fuelIter.hasNext()) {
                Fuel fuel = fuelIter.next();
                fuel.visualBox.y -= obstacleSpeed * delta;

                if (fuel.visualBox.y + fuel.visualBox.height < 0) {
                    fuelIter.remove();
                }
                if (collisionBox.overlaps(fuel.visualBox)) {
                    fuelCollected++;
                    assetManager.get("fuel_sound.mp3", Sound.class).play();
                    fuelIter.remove();
                }

                for (Obstacle obstacle : obstacles) {
                    if (obstacle.collisionBox.overlaps(fuel.visualBox)) {
                        fuelIter.remove();
                    }
                }
            }
        } else {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            isRestartHovered = buttonRestartBounds.contains(mouseX, mouseY);

            if (isRestartHovered && Gdx.input.isTouched()) {
                game.setScreen(new GameScreen(game));
            }

            blinkTime += delta;
            if (blinkTime >= 0.5f) {
                areHeadLightsBlinking = !areHeadLightsBlinking;
                blinkTime = 0f;
            }
        }

        batch.begin();
        batch.draw(assetManager.get("road.jpg", Texture.class), 0, roadY1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(assetManager.get("road.jpg", Texture.class), 0, roadY2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(currentFrame, car.x, car.y, car.width, car.height);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            batch.draw(assetManager.get("BrakeLightsOn.png", Texture.class), car.x, car.y, car.width, car.height);
        }

        for (Obstacle obstacle : obstacles) {
            batch.draw(assetManager.get("car.png", Texture.class), obstacle.visualBox.x, obstacle.visualBox.y, obstacle.visualBox.width, obstacle.visualBox.height);
        }

        for (Fuel fuel : fuels) {
            batch.draw(assetManager.get("gasolina.png", Texture.class), fuel.visualBox.x, fuel.visualBox.y, fuel.visualBox.width, fuel.visualBox.height);
        }

        if (isParticleActive) {
            fireEffect.update(delta);
            fireEffect.draw(batch);
        }

        if (isGameOver) {
            font.draw(batch, "Jogo encerrado.", Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 80);
            font.draw(batch, "Pontuação final: " + score, Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 100);

            if (areHeadLightsBlinking) {
                batch.draw(assetManager.get("HeadLightsOn.png", Texture.class), car.x, car.y, car.width, car.height);
            }

            Texture restartTexture = isRestartHovered ?
                    (Gdx.input.isTouched() ? assetManager.get("button_restart_clicked.png", Texture.class)
                            : assetManager.get("button_restart_hover.png", Texture.class))
                    : assetManager.get("button_restart.png", Texture.class);
            batch.draw(restartTexture, buttonRestartBounds.x, buttonRestartBounds.y, buttonRestartBounds.width, buttonRestartBounds.height);
        } else {
            font.draw(batch, "Car Racing!", 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 30);
            font.draw(batch, "Fuel: " + fuelCollected, 10, Gdx.graphics.getHeight() - 50);
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

    private void spawnFuel() {
        Fuel fuel = new Fuel();
        fuel.visualBox = new Rectangle();
        fuel.visualBox.width = 80;
        fuel.visualBox.height = 80;
        fuel.visualBox.y = Gdx.graphics.getHeight();

        if (Math.random() < 0.5) {
            fuel.visualBox.x = pistaEsquerda + (pistaDireita - pistaEsquerda) / 4 - fuel.visualBox.width / 2;
        } else {
            fuel.visualBox.x = pistaDireita - (pistaDireita - pistaEsquerda) / 4 - fuel.visualBox.width / 2;
        }

        fuels.add(fuel);
    }

    private void triggerGameOver() {
        isGameOver = true;
        assetManager.get("collision_sound.mp3", Sound.class).play();
        Music backgroundMusic = assetManager.get("background_music.mp3", Music.class);
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
        font.dispose();
        fireEffect.dispose();
        assetManager.dispose();
    }
}