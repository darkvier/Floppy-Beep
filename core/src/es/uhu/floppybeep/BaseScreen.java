package es.uhu.floppybeep;

import com.badlogic.gdx.Screen;

/** Screen desde la que heredan todas las demas */
abstract class BaseScreen implements Screen {

	MainGame game;

	BaseScreen(MainGame game) {
		this.game = game;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
