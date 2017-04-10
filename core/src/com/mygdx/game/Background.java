package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.mygdx.game.Constants.VIEWPORT_SIZE;

class Background extends Actor {

	private final TextureRegion textureRegion;
	private final MainGame game;
	private final Stage stage;
	private float scala;
	private Rectangle textureRegionBounds1;
	private Rectangle textureRegionBounds2;
	private int velocFondo = 100;
	private Texture texture;

	Background(MainGame game, Stage stage, String textura) {
		this.game = game;
		this.stage = stage;

		// Textura para el fondo
		this.texture = game.getManager().get(textura);

		// Ajustar la textura a la stage
		scala = stage.getHeight() / texture.getHeight();

		// Regiones del fondo animadas que se van a pintar
		textureRegion = new TextureRegion(texture);
		textureRegionBounds1 = new Rectangle(0 - texture.getWidth() * scala / 2, 0, texture.getWidth(), texture.getHeight());
		textureRegionBounds2 = new Rectangle(texture.getWidth() * scala / 2, 0, stage.getWidth(), texture.getHeight());
	}

	@Override
	public void act(float delta) {
		if (leftBoundsReached(delta)) {
			resetBounds();
		} else {
			updateXBounds(-delta);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(textureRegion, textureRegionBounds1.x, textureRegionBounds1.y, 0, 0, texture.getWidth(),
				texture.getHeight(), scala, scala, 0);
		batch.draw(textureRegion, textureRegionBounds2.x, textureRegionBounds2.y, 0, 0, texture.getWidth(),
				texture.getHeight(), scala, scala, 0);
	}

	/** indica si un region se ha salido de la pantalla */
	private boolean leftBoundsReached(float delta) {
		return (textureRegionBounds2.x - (delta * velocFondo)) <= -(texture.getWidth() * scala) + stage.getWidth();
	}

	/** Actualiza las coor de las regiones del fondo a pintar */
	private void updateXBounds(float delta) {
		textureRegionBounds1.x += delta * velocFondo;
		textureRegionBounds2.x += delta * velocFondo;
	}

	/** Reemplaza la region que se ha dejado de ver por la otra y esta ultima por una region nueva */
	private void resetBounds() {
		textureRegionBounds1 = textureRegionBounds2;
		textureRegionBounds2 = new Rectangle(VIEWPORT_SIZE.x, 0, VIEWPORT_SIZE.x, VIEWPORT_SIZE.y);
	}
}