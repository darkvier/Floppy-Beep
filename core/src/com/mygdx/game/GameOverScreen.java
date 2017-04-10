package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

	private Stage stage;
	private Skin skin;
	private Image imgRecord;
	private Label puntuacion;
	private Music musica;

	GameOverScreen(final MainGame game) {
		super(game);

		stage = new Stage(new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y));
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		musica = game.getManager().get("audio/gameOver.mp3");


		Image gameover = new Image(game.getManager().get("gameover.png", Texture.class));
		gameover.setPosition(320 - gameover.getWidth() / 2, 320 - gameover.getHeight());
		stage.addActor(gameover);

		TextButton retry = new TextButton("Retry", skin);
		retry.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.gameScreen);
			}
		});
		retry.setSize(200, 80);
		retry.setPosition(60, 50);
		stage.addActor(retry);

		TextButton menu = new TextButton("Menu", skin);
		menu.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.menuScreen);
			}
		});
		menu.setSize(200, 80);
		menu.setPosition(380, 50);
		stage.addActor(menu);

		puntuacion = new Label("", skin);
		stage.addActor(puntuacion);
		Texture textureNew = game.getManager().get("new.png");
		imgRecord = new Image(textureNew);
	}

	@Override
	public void show() {
		mostrarPuntuacion();
		enviarPuntuacion();

		musica.play();
		Gdx.input.setInputProcessor(stage);
	}

	private void mostrarPuntuacion() {
		float posX, posY;
		// Puntuacion
		puntuacion.setText("Score: " + game.scoreTmp);
		puntuacion.pack();
		posX = stage.getWidth() / 2 - puntuacion.getWidth() / 2;
		puntuacion.setPosition(posX, 200);
		puntuacion.toFront();

		// Aviso de nuevo record
		if (game.scoreTmp > game.scoreRecord[game.dificultadInt]) {
			imgRecord.setSize(50, 50);
			imgRecord.toFront();
			//imgRecord.pack();
			posX = puntuacion.getX() + puntuacion.getWidth() + 15;
			posY = puntuacion.getY() + (puntuacion.getHeight() / 2) - (imgRecord.getHeight() / 2);
			imgRecord.setPosition(posX, posY);
			stage.addActor(imgRecord);
		}
	}

	@Override
	public void hide() {
		imgRecord.remove();

		musica.stop();
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		// Dispose assets.
		skin.dispose();
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		// Just render things.
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
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
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				String body = response.body().string();
				System.out.print("#######\nEnvio de puntuacion: ");

				if (!response.isSuccessful()) {
					System.out.println("Error (" + response.code() + ") " + response.message());
					throw new IOException(response.toString());
				} else {
					// Comprobar la consulta
					Pattern p = Pattern.compile("^true$", Pattern.MULTILINE);
					Matcher m = p.matcher(body);
					if (m.lookingAt()) {
						System.out.println("Exito");
					} else {
						System.out.println("Respuesta inesperada");
						System.out.println(body);
					}
				}
			}
		});
	}
}
