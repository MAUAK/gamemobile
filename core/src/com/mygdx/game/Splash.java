package com.mygdx.game;

import static com.badlogic.gdx.Gdx.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGdxGame;

public class Splash implements Screen{
    private SpriteBatch batch;
    private Texture ttrSplash;
    private float timeToShowSplashScreen = 2f;


    public Splash(MyGdxGame myGdxGame) {
        super();

    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        desenharTexturas();
        ttrSplash = new Texture("capa.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        desenharTexturas();
    }

    private void desenharTexturas(){
        batch.begin();
        batch.draw(ttrSplash, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        timeToShowSplashScreen -= 10;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        ttrSplash.dispose();
        batch.dispose();
    }
}
