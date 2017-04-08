package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Constants {
	public static float PIXELS_IN_METER = 30f;

	public static final Vector2 GRAVEDAD = new Vector2(0, -10);

	public static final int ANCHO_MURO = 1;

	static final Vector2 PLAYER_POS = new Vector2(1f, 10f);

	// public static final Vector2 VIEWPORT_SIZE = new Vector2(800, 480);
	//public static final Vector2 VIEWPORT_SIZE = new Vector2(640, 360); //16:9
	//public static final Vector2 VIEWPORT_SIZE = new Vector2(960, 540); //16:9
	public static final Vector2 VIEWPORT_SIZE = new Vector2(1280, 720);
	//public static final Vector2 VIEWPORT_SIZE = new Vector2(1366, 768); //16:9
	//public static final Vector2 VIEWPORT_SIZE = new Vector2(1920, 1080); //16:9
	//public static final Vector2 VIEWPORT_SIZE = new Vector2(720, 480); //16:9

	//public static final String URL_RANKING = "http:///127.0.0.1/query.php";
	static final String URL_RANKING = "http://darkvier.site40.net/query.php";

	// TODO muros adaptados a la resolucion
	static final int[][] PARAM_DIFIC = new int[][]{
			new int[]{4, 6, 6, 8, 3, 5},
			new int[]{4, 6, 6, 8, 5, 7},
			new int[]{3, 5, 5, 7, 4, 6}};
}