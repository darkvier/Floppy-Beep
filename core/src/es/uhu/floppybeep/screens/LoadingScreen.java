package es.uhu.floppybeep.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.IOException;

import es.uhu.floppybeep.MainGame;

import static es.uhu.floppybeep.Constants.VIEWP_MIN_SIZE;

/** Pantalla que se muestra mientras cargan los Assets */
public class LoadingScreen extends BaseScreen {

	private Stage stage;
	private Skin skin;
	private Label loading;

	public LoadingScreen(MainGame game) {
		super(game);

		// Set up the stage and the skin. See GameOverScreen for more comments on this.
		stage = new Stage(new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y));
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		// Create some loading text using this skin file and position it on screen.
		loading = new Label("Loading...", skin);
		stage.addActor(loading);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Si se han caragado todos los archivos devuelve true
		if (game.getManager().update()) {
			// Una vez esta todó cargado, mostramos el menu principal
			try {
				game.finishLoading();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Si no esta cargado, mostramos el porcentaje de carga
			int progress = (int) (game.getManager().getProgress() * 100);
			loading.setText("Cargando... " + progress + "%");
			loading.setFontScale(2);
			loading.pack();
			loading.setPosition(stage.getWidth() / 2 - loading.getWidth() / 2, stage.getHeight() / 2);
		}

		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}
