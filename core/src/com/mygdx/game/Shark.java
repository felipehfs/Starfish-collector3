package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Shark extends BaseActor{
    public Shark(float x, float y, Stage stage) {
        super(x, y, stage);
        loadTexture("assets/sharky.png");
        setBoundaryPolygon(8);
    }
}
