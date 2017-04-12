package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.mygdx.game.Constants.VIEWP_MIN_SIZE;

class SettingsScreen extends BaseScreen {

	private Stage stage;
	private Skin skin;
	private Label volText, impText, velText;
	private Slider volumenSlid, impulsoSlid, velocidadSlid;
	private CheckBox musica, efectos, fullScreenCheck;
	private SelectBox<Object> dificultad;
	private TextField nickname;
	private ExtendViewport viewport;
	private OrthographicCamera camera;

	SettingsScreen(final MainGame game) {
		super(game);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y);
		viewport = new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y, camera);

		stage = new Stage(viewport);
		skin = game.getManager().get("skin/uiskin.json");

		// Fondo
		Background backGround = new Background(game, stage, "sky/sky.png");
		stage.addActor(backGround);

		// Nombre
		Label etiquetaNickname = new Label("Nombre Jugador: ", skin);
		nickname = new TextField(game.nickname, skin);

		// Audio
		Label etiquetaAudio = new Label("AUDIO", skin);
		musica = new CheckBox(" Musica", skin);
		efectos = new CheckBox(" Efectos", skin);
		volText = new Label("Volumen: 999%", skin);
		volumenSlid = new Slider(0, 100, 10, false, skin);
		volumenSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				volText.setText("Volumen: " + volumenSlid.getValue() + "%");
			}
		});

		// Juego
		Label etiquetaJuego = new Label("JUEGO", skin);
		impText = new Label("Impulso: 999%", skin);
		impulsoSlid = new Slider(10, 30, 5, false, skin);
		impulsoSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				impText.setText("Impulso: " + impulsoSlid.getValue());
			}
		});

		velText = new Label("Velocidad: 999%", skin);
		velocidadSlid = new Slider(1, 8, 1, false, skin);
		velocidadSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				velText.setText("Velocidad: " + velocidadSlid.getValue());
			}
		});

		fullScreenCheck = new CheckBox("Full Screen", skin);

		// DIFICULTAD
		Label etiquetaDificultad = new Label("Dificultad:", skin);
		dificultad = new SelectBox<Object>(skin);
		dificultad.setItems("Facil", "Normal", "Dificil");

		// Botones
		TextButton save = new TextButton("Save", skin);
		save.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				guardarConf();
				game.setScreen(game.menuScreen);
			}
		});

		TextButton back = new TextButton("Cancel", skin);
		back.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.menuScreen);
			}
		});


		/* Tabla principal */
		Table tabla = new Table();
		//tabla.setDebug(true);
		tabla.pad(50);
		int padT = 50,
				padR = 100;
		tabla.defaults().padTop(15).expandX().align(Align.left);
		tabla.setSize(stage.getWidth(), stage.getHeight());

		tabla.row().pad(0);
		tabla.add(etiquetaNickname).colspan(2).align(Align.right);
		tabla.add(nickname).colspan(2).align(Align.left).minSize(300, 50);

		tabla.row().padTop(padT);
		tabla.add(etiquetaAudio).colspan(2).align(Align.center).padRight(padR);
		tabla.add(etiquetaJuego).colspan(2).align(Align.center);

		tabla.row().padTop(padT / 2);
		tabla.add(efectos).colspan(2).align(Align.left).padRight(padR);
		tabla.add(impText).size(impText.getWidth(), impText.getHeight());
		tabla.add(impulsoSlid).fillX();

		tabla.row().padTop(padT);
		tabla.add(musica).colspan(2).align(Align.left).padRight(padR);
		tabla.add(velText).size(velText.getWidth(), velText.getHeight());
		tabla.add(velocidadSlid).fillX();

		tabla.row().padTop(padT);
		tabla.add(volText).size(volText.getWidth(), volText.getHeight());
		tabla.add(volumenSlid).fillX().padRight(padR);
		tabla.add(fullScreenCheck).colspan(2).align(Align.left);

		tabla.row().padTop(padT);
		tabla.add(new Label("", skin)).colspan(2);
		tabla.add(etiquetaDificultad).padRight(padR);
		tabla.add(dificultad).fillX().align(Align.left);

		tabla.row().padTop(padT * 1.5f).size(250, 75);
		tabla.add(save).colspan(2).align(Align.center);
		tabla.add(back).colspan(2).align(Align.center);

		stage.addActor(tabla);
	}

	@Override
	public void show() {
		// Setear valores configuracion
		dificultad.setSelected(game.dificultad);
		efectos.setChecked(game.efectos);
		musica.setChecked(game.musica);
		volText.setText("Volumen: " + game.volumen + "%");
		volumenSlid.setValue(game.volumen);
		impText.setText("Impulso: " + game.impulso);
		impulsoSlid.setValue(game.impulso);
		velText.setText("Velocidad: " + game.velocidad);
		velocidadSlid.setValue(game.velocidad);
		fullScreenCheck.setChecked(game.fullScreen);

		InputManage.set(this, game, stage);
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
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
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	/** Guarda la conf en disco y la carga en el MainGame */
	private void guardarConf() {
		game.settings.putString("dificultad", dificultad.getSelected().toString());
		game.settings.putFloat("volumen", volumenSlid.getValue());
		game.settings.putBoolean("efectos", efectos.isChecked());
		game.settings.putBoolean("musica", musica.isChecked());
		game.settings.putString("nickname", nickname.getText());
		game.settings.putFloat("impulso", impulsoSlid.getValue());
		game.settings.putFloat("velocidad", velocidadSlid.getValue());
		game.settings.putBoolean("fullScreen", fullScreenCheck.isChecked());
		game.settings.flush();
		game.cargarConfig();
	}
}