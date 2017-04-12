package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mygdx.game.Constants.URL_RANKING;
import static com.mygdx.game.Constants.VIEWP_MIN_SIZE;

/** Screen cuando el jugador muere */
class GameOverScreen extends BaseScreen {

	private final TextButton retry, menu;
	private Stage stage;
	private Skin skin;
	private Image imgRecord, gameover;
	private Label puntuacion;
	private Music musica;
	private ExtendViewport viewport;
	private OrthographicCamera camera;

	GameOverScreen(final MainGame game) {
		super(game);


		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y);
		viewport = new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y, camera);

		stage = new Stage(viewport);
		skin = game.getManager().get("skin/uiskin.json");
		musica = game.getManager().get("audio/gameOver.mp3");

		// Fondo
		Background backGround = new Background(game, stage, "sky/sky.png");
		stage.addActor(backGround);

		// Game Over
		gameover = new Image(game.getManager().get("gameover.png", Texture.class));
		int logoX = (int) (stage.getWidth() / 2 - gameover.getWidth() / 2),
				logoY = (int) (stage.getHeight() - stage.getHeight() / 3);

		gameover.setPosition(logoX, logoY);
		stage.addActor(gameover);

		//Puntuacion
		puntuacion = new Label("", skin);
		stage.addActor(puntuacion);
		Texture textureNew = game.getManager().get("medalla.png");
		imgRecord = new Image(textureNew);


		// Botones
		retry = new TextButton("Reintentar", skin);
		retry.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.gameScreen);
			}
		});

		menu = new TextButton("Menu", skin);
		menu.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.menuScreen);
			}
		});

		Table tabButton = new Table();
		tabButton.row().fill().pad(35).size(250, 75);
		tabButton.setSize(stage.getWidth() / 2, stage.getHeight() / 5);

		int tabY = (int) (stage.getHeight() - stage.getHeight() / 1.25);
		tabButton.setPosition(stage.getWidth() / 2 - tabButton.getWidth() / 2, tabY);

		tabButton.add(retry);
		tabButton.add(menu);
		stage.addActor(tabButton);
	}

	@Override
	public void show() {
		mostrarPuntuacion();
		enviarPuntuacion();
		musica.play();
		InputManage.set(this, game, stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.update();
	}


	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		imgRecord.remove();
		musica.stop();
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

	private void mostrarPuntuacion() {
		float posX, posY;
		// Puntuacion
		puntuacion.setText("Score: " + game.scoreTmp);
		puntuacion.pack();
		int puntX = (int) (stage.getWidth() / 2 - puntuacion.getWidth() / 2),
				puntY = (int) (stage.getHeight() - stage.getHeight() / 2);
		puntuacion.setPosition(puntX, puntY);
		puntuacion.toFront();

		// Nuevo record
		if (game.scoreTmp > game.scoreRecord[game.dificultadInt]) {

			imgRecord.setSize(101, 125);
			posX = puntuacion.getX() + puntuacion.getWidth() + 15;
			posY = puntuacion.getY() + (puntuacion.getHeight() / 2) - (imgRecord.getHeight() / 2);
			imgRecord.setPosition(posX, posY);
			stage.addActor(imgRecord);
		}
	}

	private void enviarPuntuacion() {
		if (game.scoreTmp <= game.scoreRecord[game.dificultadInt])
			return;

		// Actualizar record local
		game.scoreRecord[game.dificultadInt] = game.scoreTmp;

		// Enviar record a internet
		HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_RANKING).newBuilder();
		urlBuilder.addQueryParameter("accion", "insertar");
		urlBuilder.addQueryParameter("Nickname", game.nickname);
		urlBuilder.addQueryParameter("Puntuacion", String.valueOf(game.scoreTmp));
		urlBuilder.addQueryParameter("Dificultad", game.dificultad);
		String url = urlBuilder.build().toString();


		Request request = new Request.Builder()
				.url(url)
				.build();

		OkHttpClient client = new OkHttpClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				System.out.print("#######\nEnvio de puntuacion fallido ");
				//e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				String body = response.body().string();
				System.out.print("#######\nEnvio de puntuacion: ");

				if (!response.isSuccessful()) {
					System.out.println("\nError inesperado: " + response.message());
				} else {
					// Comprobar la consulta
					Pattern p = Pattern.compile("^true$", Pattern.MULTILINE);
					Matcher m = p.matcher(body);
					if (m.lookingAt()) {
						System.out.println("Exito");
					} else {
						System.out.println("Fallo: Respuesta inesperada");
						//System.out.println(body);
					}
				}
			}
		});
	}
}
