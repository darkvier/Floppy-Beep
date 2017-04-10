package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.mygdx.game.Constants.VIEWP_MIN_SIZE;

class SettingsScreen extends BaseScreen {

	//TODO boton resetear config
	//TODO al cambiar modo ventana los botones no responde bien, ¿cambian de coordenada?
	private Stage stage;
	private Skin skin;
	private Label volText, impText, velText;
	private Slider volumenSlid, impulsoSlid, velocidadSlid;
	private CheckBox musica, efectos, fullScreenCheck;
	private SelectBox<Object> dificultad;
	private TextField nickname;

	SettingsScreen(final MainGame game) {
		super(game);

		stage = new Stage(new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y));

		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

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


		// Nombre
		Label etiquetaNickname = new Label("Nombre Jugador: ", skin);
		nickname = new TextField(game.nickname, skin);


		// Audio
		Label etiquetaAudio = new Label("AUDIO", skin);
		musica = new CheckBox(" Musica", skin);
		efectos = new CheckBox(" Efectos", skin);
		volText = new Label("Volumen", skin);
		volumenSlid = new Slider(0, 100, 10, false, skin);
		volumenSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				volText.setText("Volumen: " + volumenSlid.getValue() + "%");
			}
		});


		// Juego
		Label etiquetaJuego = new Label("JUEGO", skin);
		impText = new Label("Impulso", skin);
		impulsoSlid = new Slider(10, 30, 5, false, skin);
		impulsoSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				impText.setText("Impulso: " + impulsoSlid.getValue());
			}
		});

		velText = new Label("Velocidad", skin);
		velocidadSlid = new Slider(1, 8, 1, false, skin);
		velocidadSlid.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				velText.setText("Velocidad: " + velocidadSlid.getValue());
			}
		});

		fullScreenCheck = new CheckBox("Full Screen", skin);


		// DIFICULTAD
		Label etiquetaDificultad = new Label("DIFICULTAD", skin);
		dificultad = new SelectBox<Object>(skin);
		dificultad.setItems("Facil", "Normal", "Dificil");

		// Sizes & Positions
		etiquetaNickname.setPosition(200, 500 - etiquetaNickname.getHeight());
		nickname.setPosition(350, 500 - nickname.getHeight());

		etiquetaAudio.setPosition(60, 410 - etiquetaAudio.getHeight());
		efectos.setPosition(50, 380 - efectos.getHeight());
		musica.setPosition(50, 360 - musica.getHeight());
		volText.setPosition(85, 330 - volText.getHeight());
		volumenSlid.setPosition(50, 310 - volumenSlid.getHeight());

		etiquetaJuego.setPosition(300, 410 - etiquetaJuego.getHeight());
		impText.setPosition(300, 380 - impText.getHeight());
		impulsoSlid.setPosition(300, 360 - impulsoSlid.getHeight());
		velText.setPosition(300, 350 - velText.getHeight());
		velocidadSlid.setPosition(300, 330 - velocidadSlid.getHeight());
		fullScreenCheck.setPosition(300, 310 - fullScreenCheck.getHeight());

		etiquetaDificultad.setPosition(500, 410 - etiquetaDificultad.getHeight());
		dificultad.setSize(100, 20);
		dificultad.setPosition(500, 350);

		back.setSize(200, 80);
		back.setPosition(40, 50);
		save.setSize(200, 80);
		save.setPosition(500, 50);


		// addActors
		stage.addActor(back);
		stage.addActor(save);
		stage.addActor(etiquetaNickname);
		stage.addActor(nickname);
		stage.addActor(etiquetaAudio);
		stage.addActor(efectos);
		stage.addActor(musica);
		stage.addActor(volText);
		stage.addActor(volumenSlid);
		stage.addActor(impText);
		stage.addActor(impulsoSlid);
		stage.addActor(velText);
		stage.addActor(velocidadSlid);
		stage.addActor(fullScreenCheck);
		stage.addActor(etiquetaJuego);
		stage.addActor(etiquetaDificultad);
		stage.addActor(dificultad);


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

		Gdx.input.setInputProcessor(stage);
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

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
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