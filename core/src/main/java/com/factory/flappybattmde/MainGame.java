package com.factory.flappybattmde;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.factory.flappybattmde.screens.SplashScreen;

public class MainGame extends Game {

    public SpriteBatch  batch;
    public AssetManager assetManager;
    public Skin         skin;

    @Override
    public void create() {
        batch        = new SpriteBatch();
        assetManager = new AssetManager();
        skin         = buildSkin();
        setScreen(new SplashScreen(this));
    }

    private Skin buildSkin() {
        Skin s = new Skin();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        s.add("white", new Texture(pm));
        pm.dispose();

        BitmapFont fontDefault = new BitmapFont();
        fontDefault.getData().setScale(2.5f);
        s.add("default-font", fontDefault);

        BitmapFont fontSmall = new BitmapFont();
        fontSmall.getData().setScale(1.8f);
        s.add("small-font", fontSmall);

        BitmapFont fontTitle = new BitmapFont();
        fontTitle.getData().setScale(4.5f);
        s.add("title-font", fontTitle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up        = s.newDrawable("white", new Color(0.10f, 0.05f, 0.30f, 0.95f));
        btnStyle.down      = s.newDrawable("white", new Color(0.05f, 0.02f, 0.18f, 0.95f));
        btnStyle.over      = s.newDrawable("white", new Color(0.20f, 0.10f, 0.45f, 0.95f));
        btnStyle.font      = s.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        s.add("default", btnStyle);

        Label.LabelStyle lblDefault = new Label.LabelStyle();
        lblDefault.font      = s.getFont("default-font");
        lblDefault.fontColor = Color.WHITE;
        s.add("default", lblDefault);

        Label.LabelStyle lblTitle = new Label.LabelStyle();
        lblTitle.font      = s.getFont("title-font");
        lblTitle.fontColor = Color.CYAN;
        s.add("title", lblTitle);

        Label.LabelStyle lblSmall = new Label.LabelStyle();
        lblSmall.font      = s.getFont("small-font");
        lblSmall.fontColor = Color.LIGHT_GRAY;
        s.add("small", lblSmall);

        return s;
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        assetManager.dispose();
        skin.dispose();
    }
}
