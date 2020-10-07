package ru.serdjuk.zxsna;

import com.badlogic.gdx.Game;


public class zxsna extends Game {

    // gradlew desktop:dist for jar create in path ..../ desktop / build / libs

    StartZXSNA startZXSNA;

    @Override
    public void create() {
        startZXSNA = new StartZXSNA();
        setScreen(startZXSNA);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
