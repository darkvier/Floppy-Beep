package es.uhu.floppybeep;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static es.uhu.floppybeep.Constants.PIXELS_IN_METER;
import static es.uhu.floppybeep.Constants.URL_RANKING;
import static es.uhu.floppybeep.Constants.VIEWP_MIN_SIZE;

public class MainGame extends Game {

	//TODO musica para el menu

	BaseScreen menuScreen, gameScreen, gameOverScreen, rankScreen, settingsScreen;
	int[] scoreRecord = new int[3];
	String nickname, dificultad;
	int dificultadInt, scoreTmp;
	boolean fullScreen, efectos, musica;
	float volumen;
	public float impulso;
	public float velocidad;
	/** Almacen de la configuracion */
	Preferences settings;
	private AssetManager manager;
	Skin skin80, skin36, skin24;

	@Override
	public void create() {

		modificarSkin80();
		modificarSkin36();
		modificarSkin24();

		cargarAssets();

		// Mientras carga, mostrar esta pantalla
		BaseScreen loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);

		Funciones.game = this;

		// Se apropia de la tecla BACK de android
		Gdx.input.setCatchBackKey(true);

		// Cargar configuracion en memoria
		settings = Gdx.app.getPreferences("MiConfig");
		cargarConfig();

		scoreRecord = new int[3];
		consultaHTTPRanking();
	}


	/** Carga la conf del "disco" en memoria, en caso de no existir conf, usa valores predefinidos */
	void cargarConfig() {
		this.nickname = settings.getString("nickname", "");
		this.dificultad = settings.getString("dificultad", "Normal");
		switch (dificultad.charAt(0)) {
			case 'F':
				this.dificultadInt = 0;
				break;
			case 'N':
				this.dificultadInt = 1;
				break;
			case 'D':
				this.dificultadInt = 2;
				break;
		}

		this.efectos = settings.getBoolean("efectos", true);
		this.musica = settings.getBoolean("musica", true);
		this.fullScreen = settings.getBoolean("fullScreen", true);

		this.volumen = settings.getFloat("volumen", 75f);
		this.impulso = settings.getFloat("impulso", 20f);
		this.velocidad = settings.getFloat("velocidad", 4f);

		// Modo pantalla completa - ventana
		Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
		if (fullScreen) {
			Gdx.graphics.setFullscreenMode(displayMode);
		} else {
			Gdx.graphics.setWindowedMode(displayMode.width / 2, displayMode.height / 2);
		}
	}


	/** Carga asincronamente los ficheros en memoria */
	private void cargarAssets() {
		manager = new AssetManager();
		manager.load("bird/frame-1.png", Texture.class);
		manager.load("bird/frame-2.png", Texture.class);
		manager.load("bird/frame-3.png", Texture.class);
		manager.load("bird/frame-4.png", Texture.class);
		manager.load("bird/frame-5.png", Texture.class);
		manager.load("bird/frame-6.png", Texture.class);
		manager.load("bird/frame-7.png", Texture.class);
		manager.load("bird/frame-8.png", Texture.class);
		manager.load("bird/frame-7.png", Texture.class);
		manager.load("bird/frame-8.png", Texture.class);
		manager.load("bird/gotHit/frame-1.png", Texture.class);
		manager.load("bird/gotHit/frame-2.png", Texture.class);
		manager.load("sky/sky.png", Texture.class);
		manager.load("audio/die.ogg", Sound.class); //TODO cambiar sonido muerte
		manager.load("audio/jump.ogg", Sound.class); //TODO cambiar sonido salto
		manager.load("audio/song.mp3", Music.class); //TODO cambiar musica fondo
		manager.load("audio/gameOver.mp3", Music.class);
		manager.load("medalla.png", Texture.class);
		manager.load("loading/frame-1.gif", Texture.class);
		manager.load("loading/frame-2.gif", Texture.class);
		manager.load("loading/frame-3.gif", Texture.class);
		manager.load("loading/frame-4.gif", Texture.class);
		manager.load("loading/frame-5.gif", Texture.class);
		manager.load("loading/frame-6.gif", Texture.class);
		manager.load("loading/frame-7.gif", Texture.class);
		manager.load("loading/frame-8.gif", Texture.class);
		manager.load("loading/frame-9.gif", Texture.class);
		manager.load("loading/frame-10.gif", Texture.class);
		manager.load("loading/frame-11.gif", Texture.class);
		manager.load("loading/frame-12.gif", Texture.class);
		manager.load("pipe.png", Texture.class);
		manager.load("pipeTop.png", Texture.class);
	}

	/** Crea las Stages y muestra el menu */
	void finishLoading() throws IOException {

		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		gameOverScreen = new GameOverScreen(this);
		rankScreen = new RankScreen(this);
		settingsScreen = new SettingsScreen(this);

		System.out.println("VIEWP_MIN_SIZE :" + (int) VIEWP_MIN_SIZE.x + "x" + (int) VIEWP_MIN_SIZE.y);
		System.out.println("Tamaño ventana: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
		System.out.println("Monitor: " + Gdx.graphics.getDisplayMode());
		System.out.println("PIXELS_IN_METER :" + (int) PIXELS_IN_METER);

		// Pantalla Principal del juego
		setScreen(menuScreen);
	}


	/** Modifica la skin36 por defecto con una a medida */
	private void modificarSkin80() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/DroidSansBold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.valueOf("#e0e0e0");
		parameter.size = 80;
		parameter.shadowColor = new Color(0, 0, 0, 0.75f);
		parameter.shadowOffsetX = -4;
		parameter.shadowOffsetY = 4;
		parameter.borderStraight = true;
		parameter.borderColor = new Color(0, 0, 0, 0.75f);
		parameter.borderWidth = 1;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		BitmapFont fuente = generator.generateFont(parameter);

		skin80 = new Skin(Gdx.files.internal("skin/uiskin.json"));

		// Fuente de las elementos de la skin36
		new Label("", skin80).getStyle().font = fuente;
		generator.dispose();
	}


	/** Modifica la skin36 por defecto con una a medida */
	private void modificarSkin36() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/DroidSans.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.valueOf("#e0e0e0");
		parameter.size = 36;
		parameter.shadowOffsetX = -2;
		parameter.shadowOffsetY = 2;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		BitmapFont fuente = generator.generateFont(parameter);

		skin36 = new Skin(Gdx.files.internal("skin/uiskin.json"));

		// Fuente de las elementos de la skin36
		new TextButton("", skin36).getStyle().font = fuente;
		new Label("", skin36).getStyle().font = fuente;
		new CheckBox("", skin36).getStyle().font = fuente;
		new TextField("", skin36).getStyle().font = fuente;
		new SelectBox<Object>(skin36).getStyle().font = fuente;
		new SelectBox<Object>(skin36).getStyle().listStyle.font = fuente;


		// Tamaño de los sliders
		Drawable dSlid = new Slider(1, 1, 1, false, skin36).getStyle().knob;
		dSlid.setMinWidth(dSlid.getMinWidth() * 2);
		dSlid.setMinHeight(dSlid.getMinHeight() * 2);

		//Tamaño de los checkBox On
		Drawable dCheckOn = new CheckBox("", skin36).getStyle().checkboxOn;
		dCheckOn.setMinWidth(dCheckOn.getMinWidth() * 2);
		dCheckOn.setMinHeight(dCheckOn.getMinHeight() * 2);

		//Tamaño de los checkBox Off
		Drawable dCheckOff = new CheckBox("", skin36).getStyle().checkboxOff;
		dCheckOff.setMinWidth(dCheckOff.getMinWidth() * 2);
		dCheckOff.setMinHeight(dCheckOff.getMinHeight() * 2);

		generator.dispose();
	}

	/** Modifica la skin36 por defecto con una a medida */
	private void modificarSkin24() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/DroidSans.ttf"));
		//FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/DroidSansBold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.valueOf("#e0e0e0");
		parameter.size = 24;
		parameter.shadowOffsetX = -2;
		parameter.shadowOffsetY = 2;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		BitmapFont fuente = generator.generateFont(parameter);

		skin24 = new Skin(Gdx.files.internal("skin/uiskin.json"));

		new Label("", skin24).getStyle().font = fuente;
		new TextButton("", skin24, "toggle").getStyle().font = fuente;
		TextButton.TextButtonStyle a = new TextButton("", skin24, "toggle").getStyle();
		a.checked.setMinWidth(100);
		a.checked.setMinHeight(40);

		generator.dispose();
	}


	/** Ejecuta consulta HTTP de ranking */
	void consultaHTTPRanking() {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_RANKING).newBuilder();
		urlBuilder.addQueryParameter("accion", "listarRecords");
		urlBuilder.addQueryParameter("Nickname", nickname);
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				System.out.print("#######\nConsulta de records personales: Error");
				//System.out.println(e.toString());
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				String body = response.body().string();
				System.out.print("#######\nConsulta de records personales:");

				if (!response.isSuccessful()) {
					System.out.println("Error inesperado: " + response.message());
				} else {
					// Comprobar la consulta
					Pattern p = Pattern.compile("^true$", Pattern.MULTILINE);
					Matcher m = p.matcher(body);

					if (m.lookingAt()) {
						System.out.println("Exito");
						procesarRecords(body.substring(5, body.length()));
					} else {
						System.out.println("Respuesta inesperada");
						//System.out.println(body);
					}
				}
			}
		});
	}

	/** Carga el record personal de cada nivel de dificultad */
	private void procesarRecords(String datos) {
		// Cada dificultad
		String[] dificultad = datos.split("\n");
		for (String dif : dificultad) {

			// Por cada campo (Dificultad, Puntuacion)
			String[] campos = dif.split("\t");
			int puntuacion = 0;

			if (campos.length == 2)
				puntuacion = Integer.parseInt(campos[1]);

			switch (campos[0].charAt(0)) {
				case 'F':
					scoreRecord[0] = puntuacion;
					break;
				case 'N':
					scoreRecord[1] = puntuacion;
					break;
				case 'D':
					scoreRecord[2] = puntuacion;
					break;
			}
		}
	}

	public AssetManager getManager() {
		return manager;
	}
}
