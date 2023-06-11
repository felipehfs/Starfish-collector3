package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Turtle extends BaseActor {
    public Turtle(float x, float y, Stage stage) {
        super(x, y, stage);

        String[] filenames = {
                "assets/turtle-1.png", "assets/turtle-2.png", "assets/turtle-3.png", "assets/turtle-4.png",
                "assets/turtle-5.png", "assets/turtle-6.png"
        };

        loadAnimationFromFiles(filenames, 0.1f, true);
        setAcceleration(400);
        setMaxSpeed(100);
        setDeceleration(400);
        setBoundaryPolygon(8);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            accelerationAtAngle(180);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            accelerationAtAngle(0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            accelerationAtAngle(90);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            accelerationAtAngle(270);

        applyPhysics(delta);

        setAnimationPaused(!isMoving());

        if (getSpeed() > 0) {
            setRotation(getMotionAngle());
        }

        boundToWorld();
    }
}
