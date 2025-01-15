package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
    private ObstaclePool obstaclePool;
    private FuelPool fuelPool;
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

    private boolean showMessage = false;
    private float messageTimer = 0f;
    private TextButton messageButton;
    private NinePatch patch;

    private float messageWidth = 0f;
    private float messageHeight = 0f;
    private final float maxMessageWidth = 150f; // Tamanho final do balão de fala
    private final float maxMessageHeight = 150f;
    private final float messageGrowSpeed = 300f;

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

    public class ObstaclePool extends Pool<Obstacle> {
        @Override
        protected Obstacle newObject() {
            return new Obstacle();
        }
    }

    public class FuelPool extends Pool<Fuel> {
        @Override
        protected Fuel newObject() {
            return new Fuel();
        }
    }

    private class GameInputProcessor implements InputProcessor {
        private boolean isRightPressed = false;
        private boolean isLeftPressed = false;
        private boolean isRestartHovered = false;
        private boolean isRestartClicked = false;

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.RIGHT) {
                isRightPressed = true; // Marca a tecla como pressionada
            } else if (keycode == Input.Keys.LEFT) {
                isLeftPressed = true; // Marca a tecla como pressionada
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Input.Keys.RIGHT) {
                isRightPressed = false; // Marca a tecla como não pressionada
            } else if (keycode == Input.Keys.LEFT) {
                isLeftPressed = false; // Marca a tecla como não pressionada
            }
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            // Lógica para teclas digitadas
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                isRestartClicked = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                if (isRestartClicked) {
                    isGameOver = false;
                    game.setScreen(new GameScreen(game));
                }
            }
            isRestartClicked = false;
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            // Lógica para quando o toque é arrastado
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            isRestartHovered = buttonRestartBounds.contains(screenX, Gdx.graphics.getHeight() - screenY);
            return true;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            // Lógica para rolagem
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            // Lógica para quando um toque é cancelado
            return false;
        }
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        obstaclePool = new ObstaclePool();
        fuelPool = new FuelPool();

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
        patch = new NinePatch(new Texture(Gdx.files.internal("knob.png")), 12, 12, 12, 12);

        TextureRegion[][] tmpFrames = TextureRegion.split(carSpriteSheet, carSpriteSheet.getWidth() / 7, carSpriteSheet.getHeight());

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

        Gdx.input.setInputProcessor(new GameInputProcessor());

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);
        GameInputProcessor inputProcessor = (GameInputProcessor) Gdx.input.getInputProcessor();

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            messageTimer += delta;
            obstacleSpawnTime += delta;
            fuelSpawnTime += delta;
            animationTime += delta;

            if (messageTimer >= 5f && !showMessage) {
                showMessage = true; // Exibe o balão após 10 segundos
            }

            if (showMessage && messageTimer >= 10f) { // Oculta após 5 segundos
                showMessage = false;
            }

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

            if (((GameInputProcessor) Gdx.input.getInputProcessor()).isRightPressed) {
                car.x += carSpeed * delta;
                currentFrame = rightFrames[(int) (animationTime * 10) % rightFrames.length];
            } else if (((GameInputProcessor) Gdx.input.getInputProcessor()).isLeftPressed) {
                car.x -= carSpeed * delta;
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
                    obstaclePool.free(obstacle);
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
                    fuelPool.free(fuel);
                }
                if (collisionBox.overlaps(fuel.visualBox)) {
                    fuelCollected++;
                    assetManager.get("fuel_sound.mp3", Sound.class).play();
                    fuelIter.remove();
                    fuelPool.free(fuel);
                }
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.collisionBox.overlaps(fuel.visualBox)) {
                        fuelIter.remove();
                        fuelPool.free(fuel);
                    }
                }
            }

        } else {
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

        if (showMessage) {
            if (messageWidth < maxMessageWidth) {
                messageWidth += messageGrowSpeed * delta;
            }
            if (messageHeight < maxMessageHeight) {
                messageHeight += messageGrowSpeed * delta;
            }

            messageWidth = Math.min(messageWidth, maxMessageWidth);
            messageHeight = Math.min(messageHeight, maxMessageHeight);

            float messageX = 0;
            float messageY = 0; // Margem do topo
            patch.draw(batch, 0, 0, messageWidth, messageHeight);
            font.draw(batch, "Hello!", messageX + 95, messageY + messageHeight - 30);
        } else {
            messageWidth = 0;
            messageHeight = 0;
        }

        if ( (((GameInputProcessor) Gdx.input.getInputProcessor()).isRightPressed) || (((GameInputProcessor) Gdx.input.getInputProcessor()).isLeftPressed) ) {
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
            font.draw(batch, "Game Over.", Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 80);
            font.draw(batch, "Final score: " + score, Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 100);
            font.draw(batch, "Fuel collected: " + fuelCollected, Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f - 120);

            if (areHeadLightsBlinking) {
                batch.draw(assetManager.get("HeadLightsOn.png", Texture.class), car.x, car.y, car.width, car.height);
            }

            Texture restartTexture = inputProcessor.isRestartClicked
                    ? assetManager.get("button_restart_clicked.png", Texture.class)
                    : (inputProcessor.isRestartHovered
                    ? assetManager.get("button_restart_hover.png", Texture.class)
                    : assetManager.get("button_restart.png", Texture.class));
            batch.draw(restartTexture, buttonRestartBounds.x, buttonRestartBounds.y, buttonRestartBounds.width, buttonRestartBounds.height);
        } else {
            font.draw(batch, "Car Racing!", 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 30);
            font.draw(batch, "Fuel: " + fuelCollected, 10, Gdx.graphics.getHeight() - 50);
        }

        batch.end();
    }

    private void spawnObstacle() {
        Obstacle obstacle = obstaclePool.obtain();

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
        Fuel fuel = fuelPool.obtain();

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
        obstaclePool.clear();
        fuelPool.clear();
    }
}