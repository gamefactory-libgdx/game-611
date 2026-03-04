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

public class SettingsScreen implements Screen {

    private final MainGame game;
    private Stage   stage;
    private Texture background;

    private boolean musicOn;
    private boolean sfxOn;

    private Label musicStatus;
    private Label sfxStatus;

    public SettingsScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = game.assetManager.get(Constants.BG_MENU, Texture.class);

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicOn = prefs.getBoolean(Constants.PREF_MUSIC_ON, true);
        sfxOn   = prefs.getBoolean(Constants.PREF_SFX_ON,   true);

        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("SETTINGS", game.skin, "title");

        Label musicLbl = new Label("MUSIC", game.skin);
        musicStatus    = new Label(musicOn ? "ON" : "OFF", game.skin);
        TextButton musicToggle = new TextButton("TOGGLE", game.skin);

        Label sfxLbl = new Label("SFX", game.skin);
        sfxStatus    = new Label(sfxOn ? "ON" : "OFF", game.skin);
        TextButton sfxToggle = new TextButton("TOGGLE", game.skin);

        TextButton menuBtn = new TextButton("MAIN MENU", game.skin);

        final float bw = 280f, bh = 62f, pad = 10f;

        table.add(title).colspan(2).padBottom(60f).row();
        table.add(musicLbl).padRight(20f);
        table.add(musicStatus).padRight(20f);
        table.add(musicToggle).size(bw * 0.6f, bh).pad(pad).row();
        table.add(sfxLbl).padRight(20f);
        table.add(sfxStatus).padRight(20f);
        table.add(sfxToggle).size(bw * 0.6f, bh).pad(pad).row();
        table.add(menuBtn).colspan(3).size(bw, bh).pad(pad * 2f).row();

        stage.addActor(table);

        musicToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                musicOn = !musicOn;
                musicStatus.setText(musicOn ? "ON" : "OFF");
                savePrefs();
            }
        });

        sfxToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                sfxOn = !sfxOn;
                sfxStatus.setText(sfxOn ? "ON" : "OFF");
                savePrefs();
            }
        });

        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                savePrefs();
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    savePrefs();
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                    return true;
                }
                return false;
            }
        });
    }

    private void savePrefs() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putBoolean(Constants.PREF_MUSIC_ON, musicOn);
        prefs.putBoolean(Constants.PREF_SFX_ON,   sfxOn);
        prefs.flush();
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
