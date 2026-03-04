package com.factory.flappybattmde.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.factory.flappybattmde.Constants;
import com.factory.flappybattmde.MainGame;

public class SplashScreen implements Screen {

    private final MainGame game;
    private Stage stage;

    public SplashScreen(MainGame game) {
        this.game = game;
        game.assetManager.load(Constants.BG_MENU, Texture.class);
        game.assetManager.load(Constants.BG_GAME, Texture.class);
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title   = new Label(Constants.GAME_TITLE, game.skin, "title");
        Label loading = new Label("Loading…", game.skin, "small");

        table.add(title).padBottom(40f).row();
        table.add(loading);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (game.assetManager.update()) {
            game.setScreen(new MainMenuScreen(game));
        }
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
