package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;


class InputManage extends InputAdapter {

	private final Screen screen;
	private final boolean isMenu;
	private final MainGame game;
	private boolean altLeftPressed, enterPressed;

	// Metodo estatico para usar esta misma clase
	static void set(Screen screen, MainGame game, Stage stage) {
		InputMultiplexer multiplexer = new InputMultiplexer(stage, stage, new InputManage(screen, game));
		Gdx.input.setInputProcessor(multiplexer);
	}

	// Constructor
	InputManage(Screen screen, MainGame game) {
		this.screen = screen;
		this.game = game;
		this.isMenu = screen.getClass().getSimpleName().equals("MenuScreen");
	}


	@Override
	public boolean keyDown(int keycode) {

		switch (keycode) {
			case Keys.ENTER:
				if (altLeftPressed) {
					Funciones.screenModeChange();
				} else {
					enterPressed = true;
				}
				break;
			case Keys.ALT_LEFT:
				if (enterPressed) {
					Funciones.screenModeChange();
				} else {
					altLeftPressed = true;
				}
				break;
			case Keys.BACK:
				exit();
				break;
			case Keys.ESCAPE:
				exit();
				break;
			case Keys.UP:
				//TODO saltar player
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

	private void exit() {
		if (isMenu) {
			Gdx.app.exit();
		} else {
			game.setScreen(game.menuScreen);
		}
	}
}
