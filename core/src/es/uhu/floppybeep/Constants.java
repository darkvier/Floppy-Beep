package es.uhu.floppybeep;

import com.badlogic.gdx.math.Vector2;

public class Constants {

	public static final Vector2 GRAVEDAD = new Vector2(0, -10);

	public static final Vector2 PLAYER_POS = new Vector2(1f, 10f);

	//public static final Vector2 VIEWP_MIN_SIZE = new Vector2(640, 360); //16:9
	//public static final Vector2 VIEWP_MIN_SIZE = new Vector2(720, 480); //16:9
	// public static final Vector2 VIEWP_MIN_SIZE = new Vector2(800, 480);
	//public static final Vector2 VIEWP_MIN_SIZE = new Vector2(960, 540); //16:9
	public static final Vector2 VIEWP_MIN_SIZE = new Vector2(1280, 720);
	//public static final Vector2 VIEWP_MIN_SIZE = new Vector2(1366, 768); //16:9
	//public static final Vector2 VIEWP_MIN_SIZE = new Vector2(1920, 1080); //16:9

	// Los valores en metros iran de 0 a 19
	public static float PIXELS_IN_METER = VIEWP_MIN_SIZE.y / 20;

	//public static final String URL_RANKING = "http:///127.0.0.1/query.php";
	public static final String URL_RANKING = "http://darkvier.site40.net/query.php";

	// aMin, aMax,	xMin, xMax,		yMin, yMax,		minDistY,	minDifY(dif y ->yAnt)
	// a -> apertura entre tubos
	// x...
	// y del hueco entre ambos tubos
	// minDistY
	// minDifY diferencia minima entre yAnt e yActual
	public static final int[][] PARAM_DIFIC = new int[][]{
			new int[]{6, 10, 5, 7, 6, 15, 12, 3},    // Facil
			new int[]{4, 6, 1, 2, 5, 16, 12, 4},    // Normal
			new int[]{3, 3, 5, 5, 4, 17, 4, 6}};    // Dificil
}