package es.uhu.floppybeep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import java.util.Random;

public class Funciones {

	private static Random rng = new Random(System.nanoTime());
	public static MainGame game;

	/**
	 * Cambia el modo pantalla completa-ventana
	 */
	static void screenModeChange() {
		Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		if (game.fullScreen) {
			Gdx.graphics.setWindowedMode(currentMode.width / 2, currentMode.height / 2);
		} else {
			Gdx.graphics.setFullscreenMode(currentMode);
		}
		game.fullScreen = !game.fullScreen;
		game.settings.putBoolean("fullScreen", game.fullScreen);
	}

	public static float generadorFloat(float low, float high) {
		return rng.nextFloat() * (high - low) + low;
	}

	public static int generadorInt(int low, int high) {
		return rng.nextInt(high - low + 1) + low;
	}

	static boolean generadorBoolean() {
		return rng.nextBoolean();
	}
}
