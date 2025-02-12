package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private AssetManager assetManager;
    private PlayerCar playerCar;
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
    private float obstacleSpeed = 200f;
    private float animationTime = 0f;
    private Rectangle buttonRestartBounds;
    private Rectangle buttonMenuBounds;
    private GameInputProcessor inputProcessor;
    private ParticleEffect fireEffect;
    private boolean isParticleActive = false;
    private boolean areHeadLightsBlinking = false;
    private float blinkTime = 0f;
    private boolean showMessage = false;
    private float messageTimer = 0f;
    private NinePatch patch;
    private float messageWidth = 0f;
    private float messageHeight = 0f;
    private final float maxMessageWidth = 150f;
    private final float maxMessageHeight = 150f;
    private final float messageGrowSpeed = 300f;
    private float pistaEsquerda;
    private float pistaDireita;
    private String carTextureFile;

    // NOVOS CAMPOS: username recebido da tela de início e flag para evitar múltiplas atualizações do ranking
    private String username;
    private boolean rankingUpdated = false;

    public GameScreen(Main game) {
        this(game, "SportsCar.png", "Player");
    }

    public GameScreen(Main game, String carTextureFile, String username) {
        this.game = game;
        this.carTextureFile = carTextureFile;
        this.username = username;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();

        obstaclePool = new ObstaclePool();
        fuelPool = new FuelPool();
        obstacles = new Array<>();
        fuels = new Array<>();

        assetManager.load(carTextureFile, Texture.class);
        assetManager.load("road.jpg", Texture.class);
        assetManager.load("car.png", Texture.class);
        assetManager.load("BrakeLightsOn.png", Texture.class);
        assetManager.load("HeadLightsOn.png", Texture.class);
        assetManager.load("gasolina.png", Texture.class);
        assetManager.load("button_restart.png", Texture.class);
        assetManager.load("button_restart_hover.png", Texture.class);
        assetManager.load("button_restart_clicked.png", Texture.class);
        assetManager.load("button_menu.png", Texture.class);
        assetManager.load("button_menu_hover.png", Texture.class);
        assetManager.load("button_menu_clicked.png", Texture.class);
        assetManager.load("background_music.mp3", Music.class);
        assetManager.load("collision_sound.mp3", Sound.class);
        assetManager.load("fuel_sound.mp3", Sound.class);
        assetManager.finishLoading();

        Texture carSpriteSheet = assetManager.get(carTextureFile, Texture.class);
        TextureRegion[][] tmpFrames = TextureRegion.split(carSpriteSheet, carSpriteSheet.getWidth() / 7, carSpriteSheet.getHeight());
        TextureRegion[] straightFrames = new TextureRegion[] { tmpFrames[0][0] };
        TextureRegion[] leftFrames = new TextureRegion[] { tmpFrames[0][1], tmpFrames[0][2], tmpFrames[0][3] };
        TextureRegion[] rightFrames = new TextureRegion[] { tmpFrames[0][4], tmpFrames[0][5], tmpFrames[0][6] };
        playerCar = new PlayerCar(straightFrames, leftFrames, rightFrames);

        pistaEsquerda = 130;
        pistaDireita = Gdx.graphics.getWidth() - 130;

        roadY1 = 0;
        roadY2 = Gdx.graphics.getHeight();

        font = new BitmapFont();

        Music backgroundMusic = assetManager.get("background_music.mp3", Music.class);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("fire.p"), Gdx.files.internal(""));
        fireEffect.scaleEffect(0.5f);

        buttonRestartBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 80,
                Gdx.graphics.getHeight() / 2f - 75,
                150,
                150
        );

        buttonMenuBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 80,
                Gdx.graphics.getHeight() / 2f,
                150,
                150
        );

        patch = new NinePatch(new Texture(Gdx.files.internal("knob.png")), 12, 12, 12, 12);

        inputProcessor = new GameInputProcessor(null, buttonRestartBounds, buttonMenuBounds);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);

        if (!isGameOver) {
            timeSinceLastUpdate += delta;
            messageTimer += delta;
            obstacleSpawnTime += delta;
            fuelSpawnTime += delta;
            animationTime += delta;

            if (messageTimer >= 5f && !showMessage) {
                showMessage = true;
            }
            if (showMessage && messageTimer >= 10f) {
                showMessage = false;
            }
            if (timeSinceLastUpdate >= 1f) {
                score += 10;
                timeSinceLastUpdate = 0f;
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

            playerCar.update(inputProcessor.isLeftPressed(), inputProcessor.isRightPressed(), delta, animationTime);

            if (playerCar.getX() < pistaEsquerda) {
                playerCar.setX(pistaEsquerda);
                triggerGameOver();
            }
            if (playerCar.getX() + playerCar.getWidth() > pistaDireita) {
                playerCar.setX(pistaDireita - playerCar.getWidth());
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

            for (int i = 0; i < obstacles.size; i++) {
                Obstacle obstacle = obstacles.get(i);
                obstacle.update(delta, obstacles);

                if (obstacle.isOffScreen()) {
                    obstacles.removeIndex(i);
                    obstaclePool.free(obstacle);
                    i--;
                }

                if (obstacle.getCollisionBox().overlaps(playerCar.getCollisionBox())) {
                    triggerGameOver();
                }
            }

            for (Iterator<Fuel> fuelIter = fuels.iterator(); fuelIter.hasNext();) {
                Fuel fuel = fuelIter.next();
                fuel.update(delta, obstacleSpeed);
                if (fuel.isOffScreen()) {
                    fuelIter.remove();
                    fuelPool.free(fuel);
                } else if (fuel.getVisualBox().overlaps(playerCar.getCollisionBox())) {
                    fuelCollected++;
                    assetManager.get("fuel_sound.mp3", Sound.class).play();
                    fuelIter.remove();
                    fuelPool.free(fuel);
                } else {
                    for (Obstacle obstacle : obstacles) {
                        if (obstacle.getCollisionBox().overlaps(fuel.getVisualBox())) {
                            fuelIter.remove();
                            fuelPool.free(fuel);
                            break;
                        }
                    }
                }
            }

        } else {
            if (inputProcessor.isRestartClicked()) {
                game.setScreen(new GameScreen(game, this.carTextureFile, username));
            }

            if (inputProcessor.isMenuClicked()) {
                game.setScreen(new StartScreen(game));
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

        playerCar.draw(batch);

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
            float messageY = 0;
            patch.draw(batch, messageX, messageY, messageWidth, messageHeight);
            font.draw(batch, "Hello!", messageX + 95, messageY + messageHeight - 30);
        } else {
            messageWidth = 0;
            messageHeight = 0;
        }

        if (inputProcessor.isRightPressed() || inputProcessor.isLeftPressed()) {
            batch.draw(assetManager.get("BrakeLightsOn.png", Texture.class),
                    playerCar.getX(), playerCar.getY(), playerCar.getWidth(), playerCar.getHeight());
        }

        for (Obstacle obstacle : obstacles) {
            batch.draw(assetManager.get("car.png", Texture.class),
                    obstacle.getVisualBox().x,
                    obstacle.getVisualBox().y,
                    obstacle.getVisualBox().width,
                    obstacle.getVisualBox().height);
        }

        for (Fuel fuel : fuels) {
            batch.draw(assetManager.get("gasolina.png", Texture.class),
                    fuel.getVisualBox().x,
                    fuel.getVisualBox().y,
                    fuel.getVisualBox().width,
                    fuel.getVisualBox().height);
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
                batch.draw(assetManager.get("HeadLightsOn.png", Texture.class),
                        playerCar.getX(), playerCar.getY(), playerCar.getWidth(), playerCar.getHeight());
            }
            Texture restartTexture = inputProcessor.isRestartClicked()
                    ? assetManager.get("button_restart_clicked.png", Texture.class)
                    : (inputProcessor.isRestartHovered()
                    ? assetManager.get("button_restart_hover.png", Texture.class)
                    : assetManager.get("button_restart.png", Texture.class));
            batch.draw(restartTexture, buttonRestartBounds.x, buttonRestartBounds.y,
                    buttonRestartBounds.width, buttonRestartBounds.height);

            Texture menuTexture = inputProcessor.isMenuClicked()
                    ? assetManager.get("button_menu_clicked.png", Texture.class)
                    : (inputProcessor.isMenuHovered()
                    ? assetManager.get("button_menu_hover.png", Texture.class)
                    : assetManager.get("button_menu.png", Texture.class));
            batch.draw(menuTexture, buttonMenuBounds.x, buttonMenuBounds.y,
                    buttonMenuBounds.width, buttonMenuBounds.height);
        } else {
            font.draw(batch, "Car Racing!", 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 30);
            font.draw(batch, "Fuel: " + fuelCollected, 10, Gdx.graphics.getHeight() - 50);
        }
        batch.end();
    }

    private void spawnObstacle() {
        Obstacle obstacle = obstaclePool.obtain();
        obstacle.init(pistaEsquerda, pistaDireita);
        obstacles.add(obstacle);
    }

    private void spawnFuel() {
        Fuel fuel = fuelPool.obtain();
        fuel.init(pistaEsquerda, pistaDireita);
        fuels.add(fuel);
    }

    // Ao disparar o Game Over, atualiza o ranking (apenas uma vez)
    private void triggerGameOver() {
        if (!isGameOver) {
            isGameOver = true;
            assetManager.get("collision_sound.mp3", Sound.class).play();
            Music backgroundMusic = assetManager.get("background_music.mp3", Music.class);
            backgroundMusic.stop();
            isParticleActive = true;
            fireEffect.setPosition(playerCar.getX() + playerCar.getWidth() / 2,
                    playerCar.getY() + playerCar.getHeight() / 2);
            fireEffect.start();
            if (!rankingUpdated) {
                RankingManager.updateRanking(username, score, fuelCollected);
                rankingUpdated = true;
            }
        }
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
        playerCar.dispose();
    }
}
