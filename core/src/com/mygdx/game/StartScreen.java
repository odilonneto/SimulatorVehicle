package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputMultiplexer;

public class StartScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Texture defaultCarTexture;
    private Texture blueCarTexture;
    private Texture purpleCarTexture;
    private Texture headLightsTexture;

    private Animation<TextureRegion> defaultCarAnimation;
    private Animation<TextureRegion> blueCarAnimation;
    private Animation<TextureRegion> purpleCarAnimation;

    private Rectangle defaultCarBounds;
    private Rectangle blueCarBounds;
    private Rectangle purpleCarBounds;

    private BitmapFont font;
    private float stateTime;
    private String selectedCar = null;

    private static final int FRAME_COLS = 7;
    private static final int FRAME_ROWS = 1;
    private static final float BLINK_FREQUENCY = 4.0f;

    private GameInputProcessor inputProcessor;
    private AssetManager assetManager;
    private Rectangle buttonStartBounds;

    public StartScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        assetManager = new AssetManager();

        assetManager.load("HeadLightsOn.png", Texture.class);
        assetManager.load("SportsCar.png", Texture.class);
        assetManager.load("SportsCar-Sheet-Blue.png", Texture.class);
        assetManager.load("SportsCar-Sheet-Purple.png", Texture.class);
        assetManager.load("button_start.png", Texture.class);
        assetManager.load("button_start_hover.png", Texture.class);
        assetManager.load("button_start_clicked.png", Texture.class);
        assetManager.finishLoading();

        headLightsTexture = assetManager.get("HeadLightsOn.png", Texture.class);
        defaultCarTexture = assetManager.get("SportsCar.png", Texture.class);
        blueCarTexture = assetManager.get("SportsCar-Sheet-Blue.png", Texture.class);
        purpleCarTexture = assetManager.get("SportsCar-Sheet-Purple.png", Texture.class);
        defaultCarAnimation = createAnimationFromSpriteSheet(defaultCarTexture);
        blueCarAnimation = createAnimationFromSpriteSheet(blueCarTexture);
        purpleCarAnimation = createAnimationFromSpriteSheet(purpleCarTexture);

        float frameWidth = defaultCarTexture.getWidth() / FRAME_COLS;
        float frameHeight = defaultCarTexture.getHeight();
        float displayWidth = frameWidth;
        float displayHeight = frameHeight;

        defaultCarBounds = new Rectangle(80, 500, displayWidth, displayHeight);
        blueCarBounds = new Rectangle(210, 500, displayWidth, displayHeight);
        purpleCarBounds = new Rectangle(340, 500, displayWidth, displayHeight);

        buttonStartBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 80,
                Gdx.graphics.getHeight() / 2f - 350,
                150,
                150
        );

        stateTime = 0f;

        InputAdapter carSelectionAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int flippedY = Gdx.graphics.getHeight() - screenY;
                if (defaultCarBounds.contains(screenX, flippedY)) {
                    selectedCar = "SportsCar.png";
                } else if (blueCarBounds.contains(screenX, flippedY)) {
                    selectedCar = "SportsCar-Sheet-Blue.png";
                } else if (purpleCarBounds.contains(screenX, flippedY)) {
                    selectedCar = "SportsCar-Sheet-Purple.png";
                }
                return false;
            }
        };

        inputProcessor = new GameInputProcessor(
                buttonStartBounds,
                null, null,
                defaultCarBounds,
                blueCarBounds,
                purpleCarBounds
        );

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputProcessor);
        multiplexer.addProcessor(carSelectionAdapter);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private Animation<TextureRegion> createAnimationFromSpriteSheet(Texture spriteSheet) {
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / FRAME_COLS,
                spriteSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] frames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            frames[i] = tmp[0][i];
        }
        return new Animation<TextureRegion>(0.1f, frames);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        boolean headlightsVisible = Math.sin(stateTime * BLINK_FREQUENCY) > 0;

        stateTime += delta;

        TextureRegion currentFrameDefault = defaultCarAnimation.getKeyFrame(stateTime, true);
        TextureRegion currentFrameBlue = blueCarAnimation.getKeyFrame(stateTime, true);
        TextureRegion currentFramePurple = purpleCarAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Car Racing", 160, Gdx.graphics.getHeight() - 50);

        font.getData().setScale(1.5f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Choose your vehicle:", 140, Gdx.graphics.getHeight() - 100);

        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Ranking", 210, Gdx.graphics.getHeight() - 400);

        batch.draw(currentFrameDefault, defaultCarBounds.x, defaultCarBounds.y, defaultCarBounds.width, defaultCarBounds.height);
        batch.draw(currentFrameBlue, blueCarBounds.x, blueCarBounds.y, blueCarBounds.width, blueCarBounds.height);
        batch.draw(currentFramePurple, purpleCarBounds.x, purpleCarBounds.y, purpleCarBounds.width, purpleCarBounds.height);

        if (headlightsVisible) {
            batch.draw(headLightsTexture, defaultCarBounds.x, defaultCarBounds.y, defaultCarBounds.width, defaultCarBounds.height);
            batch.draw(headLightsTexture, blueCarBounds.x, blueCarBounds.y, blueCarBounds.width, blueCarBounds.height);
            batch.draw(headLightsTexture, purpleCarBounds.x, purpleCarBounds.y, purpleCarBounds.width, purpleCarBounds.height);
        }
        batch.end();

        String selectedCar = inputProcessor.getSelectedCar();
        float padding = 10;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(selectedCar != null && selectedCar.equals("SportsCar.png") ? Color.GREEN : Color.WHITE);
        shapeRenderer.rect(defaultCarBounds.x - padding, defaultCarBounds.y - padding,
                defaultCarBounds.width + 2 * padding, defaultCarBounds.height + 2 * padding);
        shapeRenderer.setColor(selectedCar != null && selectedCar.equals("SportsCar-Sheet-Blue.png") ? Color.GREEN : Color.WHITE);
        shapeRenderer.rect(blueCarBounds.x - padding, blueCarBounds.y - padding,
                blueCarBounds.width + 2 * padding, blueCarBounds.height + 2 * padding);
        shapeRenderer.setColor(selectedCar != null && selectedCar.equals("SportsCar-Sheet-Purple.png") ? Color.GREEN : Color.WHITE);
        shapeRenderer.rect(purpleCarBounds.x - padding, purpleCarBounds.y - padding,
                purpleCarBounds.width + 2 * padding, purpleCarBounds.height + 2 * padding);
        shapeRenderer.end();

        batch.begin();
        Texture restartTexture = inputProcessor.isStartClicked()
                ? assetManager.get("button_start_clicked.png", Texture.class)
                : (inputProcessor.isStartHovered()
                ? assetManager.get("button_start_hover.png", Texture.class)
                : assetManager.get("button_start.png", Texture.class));
        batch.draw(restartTexture, buttonStartBounds.x, buttonStartBounds.y,
                buttonStartBounds.width, buttonStartBounds.height);
        batch.end();

        if (inputProcessor.isStartClicked() && inputProcessor.getSelectedCar() != null) {
            game.setScreen(new GameScreen(game, inputProcessor.getSelectedCar()));
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
        shapeRenderer.dispose();
        defaultCarTexture.dispose();
        blueCarTexture.dispose();
        purpleCarTexture.dispose();
        font.dispose();
        assetManager.dispose();
    }
}
