package com.factory.flappybattmde.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private Stage   stage;
    private Texture background;

    public MainMenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = game.assetManager.get(Constants.BG_MENU, Texture.class);

        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label(Constants.GAME_TITLE, game.skin, "title");

        TextButton playBtn      = new TextButton("PLAY",        game.skin);
        TextButton leaderBtn    = new TextButton("LEADERBOARD", game.skin);
        TextButton settingsBtn  = new TextButton("SETTINGS",    game.skin);
        TextButton howToBtn     = new TextButton("HOW TO PLAY", game.skin);

        final float bw = 280f, bh = 62f, pad = 12f;

        table.add(title).padBottom(70f).row();
        table.add(playBtn).size(bw, bh).pad(pad).row();
        table.add(leaderBtn).size(bw, bh).pad(pad).row();
        table.add(settingsBtn).size(bw, bh).pad(pad).row();
        table.add(howToBtn).size(bw, bh).pad(pad).row();

        stage.addActor(table);

        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        leaderBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
                dispose();
            }
        });

        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
                dispose();
            }
        });

        howToBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HowToPlayScreen(game));
                dispose();
            }
        });

        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
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

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
