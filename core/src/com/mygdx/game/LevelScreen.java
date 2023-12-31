package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class LevelScreen extends BaseScreen{

    private Turtle turtle;
    private boolean win;
    @Override
    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTexture("assets/water-border.jpg");
        ocean.setSize(1200, 900);
        BaseActor.setWorldBounds(ocean);

        new Starfish(400, 400, mainStage);
        new Starfish(500, 100, mainStage);
        new Starfish(100, 450, mainStage);
        new Starfish(200, 250, mainStage);

        new Rock(200, 150, mainStage);
        new Rock(100, 300, mainStage);
        new Rock(300, 350, mainStage);
        new Rock(450, 200, mainStage);

        new Shark(600, 400, mainStage);

        turtle = new Turtle(20, 20, mainStage);

        win = false;
    }

    @Override
    public void update(float deltaTime) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "com.mygdx.game.Rock"))
            turtle.preventOverlap(rockActor);

        for (BaseActor starfishActor: BaseActor.getList(mainStage, "com.mygdx.game.Starfish")) {
            Starfish starfish = (Starfish) starfishActor;

            if (turtle.overlaps(starfish) && !starfish.isCollected()) {
                starfish.collect();

                Whirlpool whirlpool = new Whirlpool(0, 0, mainStage);
                whirlpool.centerAtActor(starfish);
                whirlpool.setOpacity(0.25f);

            }
        }

        for (BaseActor actor:  BaseActor.getList(mainStage, "com.mygdx.game.Shark")) {
            Shark shark = (Shark) actor;
            if (turtle.overlaps(shark)) {
                turtle.remove();
                BaseActor gameOverMessage = new BaseActor(0, 0, uiStage);
                gameOverMessage.loadTexture("assets/game-over.png");
                gameOverMessage.centerAtPosition(300, 300);
                gameOverMessage.setOpacity(0);
                gameOverMessage.addAction(Actions.delay(1));
                gameOverMessage.addAction(Actions.after(Actions.fadeIn(1)));
            }
        }

        if (BaseActor.count(mainStage, "com.mygdx.game.Starfish") == 0  && !win) {
            win = true;
            BaseActor youWinMessage = new BaseActor(0, 0, uiStage);
            youWinMessage.loadTexture("assets/you-win.png");
            youWinMessage.centerAtPosition(400, 300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));

            BaseActor continueMessage = new BaseActor(0, 0, uiStage);
            continueMessage.loadTexture("assets/message-continue.png");
            continueMessage.centerAtPosition(400, 100);
            continueMessage.setOpacity(0);
            continueMessage.addAction(Actions.delay(1));
            continueMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.C) && win) {
            StarfishCollectorCh3.setActiveScreen(new Level2Screen());
        }
    }
}
