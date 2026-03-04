package com.factory.flappybattmde.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.factory.flappybattmde.Constants;
import com.factory.flappybattmde.MainGame;

public class GameOverScreen implements Screen {

    private final MainGame game;
    private final int      finalScore;

    private Stage   stage;
    private Texture background;

    public GameOverScreen(MainGame game, int finalScore) {
        this.game       = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        background = game.assetManager.get(Constants.BG_MENU, Texture.class);

        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);

        Preferences prefs     = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int         bestScore = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title      = new Label("GAME OVER",             game.skin, "title");
        Label scoreLbl   = new Label("SCORE: " + finalScore,  game.skin);
        Label bestLbl    = new Label("BEST: "  + bestScore,   game.skin);
        Label newBestLbl = new Label("NEW BEST!",             game.skin);

        TextButton retryBtn = new TextButton("RETRY",     game.skin);
        TextButton menuBtn  = new TextButton("MAIN MENU", game.skin);

        final float bw = 280f, bh = 62f, pad = 12f;

        table.add(title).padBottom(30f).row();
        table.add(scoreLbl).padBottom(8f).row();
        table.add(bestLbl).padBottom(8f).row();
        if (finalScore >= bestScore && bestScore > 0) {
            table.add(newBestLbl).padBottom(8f).row();
        }
        table.add(retryBtn).size(bw, bh).pad(pad).row();
        table.add(menuBtn).size(bw, bh).pad(pad).row();

        stage.addActor(table);

        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
