package com.factory.flappybattmde.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.factory.flappybattmde.Constants;
import com.factory.flappybattmde.MainGame;

public class GameScreen implements Screen {

    // ── inner class for a pipe pair ──────────────────────────────────────────
    static class PipePair {
        float x;
        float gapBottomY;  // Y coordinate of bottom of the gap
        boolean scored;

        PipePair(float x, float gapBottomY) {
            this.x = x;
            this.gapBottomY = gapBottomY;
            this.scored = false;
        }
    }

    // ── fields ───────────────────────────────────────────────────────────────
    private final MainGame game;

    private FitViewport   viewport;
    private Stage         stage;
    private ShapeRenderer shapeRenderer;
    private Texture       background;

    // bat state
    private float batY;
    private float batVelocity;

    // pipes
    private final Array<PipePair> pipes = new Array<>();
    private float nextPipeX;
    private float pipeSpeed;

    // game state
    private int     score;
    private boolean paused;
    private boolean gameOver;
    private boolean gameOverPending;
    private int     pipesPassed;

    // HUD
    private Label scoreLabel;

    // reusable rectangles for collision
    private final Rectangle batRect    = new Rectangle();
    private final Rectangle pipeRect   = new Rectangle();

    private boolean initialized = false;

    // ── constructor ──────────────────────────────────────────────────────────
    public GameScreen(MainGame game) {
        this.game = game;
    }

    // ── Screen lifecycle ─────────────────────────────────────────────────────
    @Override
    public void show() {
        if (!initialized) {
            initialized = true;

            viewport      = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            stage         = new Stage(viewport, game.batch);
            shapeRenderer = new ShapeRenderer();
            background    = game.assetManager.get(Constants.BG_GAME, Texture.class);

            resetGameState();
            buildHud();
        }

        paused = false;
        Gdx.input.setInputProcessor(stage);
    }

    private void resetGameState() {
        batY        = Constants.BAT_START_Y;
        batVelocity = 0f;
        score       = 0;
        pipesPassed = 0;
        pipeSpeed   = Constants.PIPE_SPEED;
        paused      = false;
        gameOver    = false;
        gameOverPending = false;
        pipes.clear();
        nextPipeX = Constants.WORLD_WIDTH + 100f;
    }

    private void buildHud() {
        Table hud = new Table();
        hud.setFillParent(true);
        hud.top();

        scoreLabel = new Label("SCORE: 0", game.skin);

        TextButton pauseBtn = new TextButton("PAUSE", game.skin);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (!gameOver && !gameOverPending) {
                    paused = true;
                    game.setScreen(new PauseScreen(game, GameScreen.this));
                }
            }
        });

        hud.add(scoreLabel).expandX().left().pad(10f);
        hud.add(pauseBtn).right().pad(10f).width(130f).height(55f);

        stage.addActor(hud);

        // Tap/click anywhere to flap
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!paused && !gameOver && !gameOverPending) {
                    batVelocity = Constants.FLAP_VELOCITY;
                }
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                    return true;
                }
                if (keycode == Input.Keys.SPACE && !paused && !gameOver && !gameOverPending) {
                    batVelocity = Constants.FLAP_VELOCITY;
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        if (!paused && !gameOver && !gameOverPending) {
            update(delta);
        }

        drawObjects();

        stage.act(delta);
        stage.draw();

        if (gameOverPending) {
            gameOverPending = false;
            Screen next = new GameOverScreen(game, score);
            game.setScreen(next);
            dispose();
        }
    }

    private void update(float delta) {
        // Physics
        batVelocity += Constants.GRAVITY * delta;
        batY        += batVelocity * delta;

        // Floor / ceiling collision
        if (batY < 0f || batY + Constants.BAT_HEIGHT > Constants.WORLD_HEIGHT) {
            triggerGameOver();
            return;
        }

        // Spawn pipes
        if (pipes.size == 0 || pipes.peek().x < Constants.WORLD_WIDTH - Constants.PIPE_SPAWN_DIST) {
            float gapBottom = MathUtils.random(Constants.PIPE_GAP_MIN_Y, Constants.PIPE_GAP_MAX_Y);
            pipes.add(new PipePair(Constants.WORLD_WIDTH + Constants.PIPE_WIDTH, gapBottom));
        }

        // Move pipes and check collisions
        batRect.set(Constants.BAT_START_X, batY, Constants.BAT_WIDTH, Constants.BAT_HEIGHT);
        Array<PipePair> toRemove = new Array<>();

        for (PipePair p : pipes) {
            p.x -= pipeSpeed * delta;

            // Bottom pipe rect
            pipeRect.set(p.x, 0f, Constants.PIPE_WIDTH, p.gapBottomY);
            if (batRect.overlaps(pipeRect)) {
                triggerGameOver();
                return;
            }

            // Top pipe rect
            float topPipeBottom = p.gapBottomY + Constants.PIPE_GAP;
            pipeRect.set(p.x, topPipeBottom, Constants.PIPE_WIDTH, Constants.WORLD_HEIGHT - topPipeBottom);
            if (batRect.overlaps(pipeRect)) {
                triggerGameOver();
                return;
            }

            // Score when bat passes pipe
            if (!p.scored && p.x + Constants.PIPE_WIDTH < Constants.BAT_START_X) {
                p.scored = true;
                score += Constants.SCORE_PER_PIPE;
                pipesPassed++;
                scoreLabel.setText("SCORE: " + score);
                checkDifficulty();
            }

            if (p.x + Constants.PIPE_WIDTH < 0f) {
                toRemove.add(p);
            }
        }

        pipes.removeAll(toRemove, true);
    }

    private void checkDifficulty() {
        if (pipesPassed % Constants.DIFF_PIPES_THRESHOLD == 0) {
            pipeSpeed = Math.min(pipeSpeed + Constants.PIPE_SPEED_STEP, Constants.PIPE_SPEED_MAX);
        }
    }

    private void triggerGameOver() {
        gameOver        = true;
        gameOverPending = true;
        saveScore(score);
    }

    private void drawObjects() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw pipes (green)
        shapeRenderer.setColor(0.1f, 0.7f, 0.1f, 1f);
        for (PipePair p : pipes) {
            // Bottom pipe
            shapeRenderer.rect(p.x, 0f, Constants.PIPE_WIDTH, p.gapBottomY);
            // Top pipe
            float topPipeBottom = p.gapBottomY + Constants.PIPE_GAP;
            shapeRenderer.rect(p.x, topPipeBottom, Constants.PIPE_WIDTH,
                               Constants.WORLD_HEIGHT - topPipeBottom);
            // Pipe caps
            shapeRenderer.setColor(0.05f, 0.55f, 0.05f, 1f);
            shapeRenderer.rect(p.x - 5f, p.gapBottomY - 20f, Constants.PIPE_WIDTH + 10f, 20f);
            shapeRenderer.rect(p.x - 5f, topPipeBottom, Constants.PIPE_WIDTH + 10f, 20f);
            shapeRenderer.setColor(0.1f, 0.7f, 0.1f, 1f);
        }

        // Draw bat (dark purple body + wings)
        float bx = Constants.BAT_START_X;
        float by = batY;
        float bw = Constants.BAT_WIDTH;
        float bh = Constants.BAT_HEIGHT;

        // Wings
        shapeRenderer.setColor(0.4f, 0.1f, 0.6f, 1f);
        shapeRenderer.triangle(bx, by + bh * 0.5f,
                               bx - 20f, by + bh,
                               bx + bw * 0.3f, by + bh * 0.7f);
        shapeRenderer.triangle(bx, by + bh * 0.5f,
                               bx - 20f, by,
                               bx + bw * 0.3f, by + bh * 0.3f);
        // Body
        shapeRenderer.setColor(0.55f, 0.15f, 0.75f, 1f);
        shapeRenderer.ellipse(bx, by + bh * 0.2f, bw * 0.7f, bh * 0.6f);
        // Eye
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(bx + bw * 0.5f, by + bh * 0.55f, 5f, 12);

        shapeRenderer.end();
    }

    // ── persistence ──────────────────────────────────────────────────────────
    private void saveScore(int s) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        int hi = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (s > hi) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, s);
        }

        int[] scores = new int[Constants.LEADERBOARD_SIZE];
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            scores[i] = prefs.getInteger(Constants.PREF_LB_PREFIX + i, 0);
        }
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            if (s > scores[i]) {
                for (int j = Constants.LEADERBOARD_SIZE - 1; j > i; j--) {
                    scores[j] = scores[j - 1];
                }
                scores[i] = s;
                break;
            }
        }
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            prefs.putInteger(Constants.PREF_LB_PREFIX + i, scores[i]);
        }
        prefs.flush();
    }

    // ── called by PauseScreen ────────────────────────────────────────────────
    public void resumeGame() {
        paused = false;
        Gdx.input.setInputProcessor(stage);
    }

    // ── other Screen methods ──────────────────────────────────────────────────
    @Override
    public void resize(int width, int height) {
        if (viewport != null) viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        initialized = false;
    }
}
