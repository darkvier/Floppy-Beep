package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;


class InputManage extends InputAdapter {

	private final MainGame game;
	private final boolean isMenu;
	private final boolean isGame, isGameOver;
	private GameScreen gameScreen = null;
	private boolean altLeftPressed, enterPressed;

	// Metodo estatico para usar esta misma clase de forma amigable
	static void set(Screen screen, MainGame game, Stage stage) {
		InputMultiplexer multiplexer = new InputMultiplexer(stage, stage, new InputManage(screen, game));
		Gdx.input.setInputProcessor(multiplexer);
	}

	// Constructor
	private InputManage(Screen screen, MainGame game) {
		this.game = game;
		this.isMenu = screen.getClass().getSimpleName().equals("MenuScreen");
		this.isGame = screen.getClass().getSimpleName().equals("GameScreen");
		this.isGameOver = screen.getClass().getSimpleName().equals("GameOverScreen");

		if (isGame)
			gameScreen = (GameScreen) screen;
	}


	@Override
	public boolean keyDown(int keycode) {

		switch (keycode) {
			case Keys.ENTER:
				pressEnter();
				break;
			case Keys.ALT_LEFT:
				pressAltLeft();
				break;
			case Keys.BACK:
				pressExit();
				break;
			case Keys.ESCAPE:
				pressExit();
				break;
			case Keys.UP:
				pressUP();
				break;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Keys.ENTER:
				enterPressed = false;
				break;
			case Keys.ALT_LEFT:
				altLeftPressed = false;
				break;
		}
		return super.keyDown(keycode);
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		pressUP();
		return super.touchDown(screenX, screenY, pointer, button);
	}

	// Que hacer al pulsar Enter
	private void pressEnter() {
		if (altLeftPressed) {
			Funciones.screenModeChange();
			altLeftPressed = false;
			enterPressed = false;
		} else {
			enterPressed = true;
			if (isMenu || isGameOver)
				game.setScreen(game.gameScreen);
		}
	}

	// Que hacer al pulsar Alt-Left
	private void pressAltLeft() {
		if (enterPressed) {
			Funciones.screenModeChange();
			altLeftPressed = false;
			enterPressed = false;
		} else {
			altLeftPressed = true;
		}
	}

	// Al pulsar la flecha arriba
	private void pressUP() {
		if (isGame) {
			gameScreen.saltar();
		}
	}

	// Al pulsar Escape o Back
	private void pressExit() {
		if (isMenu) {
			Gdx.app.exit();
		} else {
			game.setScreen(game.menuScreen);
		}
	}
}
