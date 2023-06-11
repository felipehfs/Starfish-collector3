package com.mygdx.game;

import com.badlogic.gdx.Game;

public class BaseGame extends Game {

    private static BaseGame baseGame;

    public BaseGame() {
        baseGame = this;
    }

    @Override
    public void create() {

    }

    public static void setActiveScreen(BaseScreen s) {
        baseGame.setScreen(s);
    }
}
