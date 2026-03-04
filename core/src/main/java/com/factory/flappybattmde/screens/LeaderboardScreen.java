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

public class LeaderboardScreen implements Screen {

    private final MainGame game;
    private Stage   stage;
    private Texture background;

    public LeaderboardScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = game.assetManager.get(Constants.BG_MENU, Texture.class);

        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("LEADERBOARD", game.skin, "title");
        table.add(title).padBottom(40f).row();

        boolean hasScores = false;
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            int s = prefs.getInteger(Constants.PREF_LB_PREFIX + i, 0);
            if (s > 0) {
                hasScores = true;
                Label entry = new Label((i + 1) + ".  " + s, game.skin);
                table.add(entry).padBottom(6f).row();
            }
        }

        if (!hasScores) {
            table.add(new Label("No scores yet", game.skin, "small")).padBottom(20f).row();
        }

        TextButton menuBtn = new TextButton("MAIN MENU", game.skin);
        table.add(menuBtn).size(280f, 62f).pad(20f).row();

        stage.addActor(table);

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
