package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.mygdx.game.Constants.VIEWPORT_SIZE;

class CreditsScreen extends BaseScreen {

	//TODO CreditScreen
	private Stage stage;
	private Skin skin;
	private Label credits;
	private TextButton back;

	CreditsScreen(final MainGame game) {
		super(game);

		stage = new Stage(new ExtendViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		back = new TextButton("Back", skin);
		back.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// Take me to the game screen!
				game.setScreen(game.menuScreen);
			}
		});
		back.setSize(200, 80);
		back.setPosition(40, 50);
		stage.addActor(back);

		credits = new Label("Jump Don't Die v1.0.2\n" +
				"Copyright (C) 2015-2016 Dani Rodriguez\n" +
				"This game is GNU GPL. Get the code at github.com/danirod/JumpDontDie\n\n" +

				"Music: \"Long Time Coming\" Kevin MacLeod (incompetech.com)\n" +
				"Licensed under Creative Commons: By Attribution 3.0", skin);
	}

	@Override
	public void show() {
		// Now this is important. If you want to be able to click the button, you have to make
		// the Input system handle input using this Stage. Stages are also InputProcessors. By
		// making the Stage the default input processor for this game, it is now possible to
		// click on buttons and even to type on input fields.
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// When the screen is no more visible, you have to remember to unset the input processor.
		// Otherwise, input might act weird, because even if you aren't using this screen, you are
		// still using the stage for handling input.
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		// Dispose assets.
		stage.dispose();
		skin.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
}
