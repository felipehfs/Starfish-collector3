package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.compression.lzma.Base;

public class StarfishCollectorCh3 extends GameBeta {

	private Turtle turtle;
	private boolean win;
	private Rock rock;

	@Override
	public void initialize() {
		BaseActor ocean = new BaseActor(0, 0, mainStage);
		ocean.loadTexture("assets/water.jpg");
		ocean.setSize(800, 600);

		new Starfish(400, 400, mainStage);
		new Starfish(500, 100, mainStage);
		new Starfish(100, 450, mainStage);
		new Starfish(200, 250, mainStage);

		new Rock(200, 150, mainStage);
		new Rock(100, 300, mainStage);
		new Rock(300, 350, mainStage);
		new Rock(450, 200, mainStage);

		turtle = new Turtle(20, 20, mainStage);

		win = false;
	}

	@Override
	public void update(float delta) {
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

		if (BaseActor.count(mainStage, "com.mygdx.game.Starfish") == 0  && !win) {
			win = true;
			BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
			youWinMessage.loadTexture("assets/you-win.png");
			youWinMessage.centerAtPosition(400, 300);
			youWinMessage.setOpacity(0);
			youWinMessage.addAction(Actions.delay(1));
			youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
		}
	}
}
