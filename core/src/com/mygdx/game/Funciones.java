package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;

import java.util.Random;

class Funciones {

	static Random rng = new Random(System.nanoTime());

	/**
	 * Detecta ALT+ENTER para cambiar el modo pantalla completa-ventana
	 */
	static void screenModeListener(MainGame game) {
		if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
			Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
			if (game.fullScreen) {
				Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			} else {
				Gdx.graphics.setFullscreenMode(currentMode);
			}
			game.fullScreen = !game.fullScreen;
		}
	}


	static float generadorFloat(float low, float high) {
		return rng.nextFloat() * (high - low) + low;
	}

	public static int generadorInt(int low, int high) {
		return rng.nextInt(high - low + 1) + low;
	}

	static boolean generadorBoolean() {
		return rng.nextBoolean();
	}

}
