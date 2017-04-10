package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.mygdx.game.Constants.PIXELS_IN_METER;

/** Clase con las propiedades y metodos de los muros */
public class MuroEntity extends Actor {

	private final Texture texturaTubo, texturaTop;
	private World world;
	private Body body;
	private Fixture fixture;

	/** Crea un Entity Muro (todo en metros) */
	MuroEntity(World world, Texture texturaTubo, Texture texturaTop, float x, float y, float width, float height) {
		this.world = world;
		this.texturaTubo = texturaTubo;
		this.texturaTop = texturaTop;

		// Create body
		BodyDef def = new BodyDef();
		def.position.set(x + width / 2, y + height / 2);
		body = world.createBody(def);

		// Give it a box shape.
		PolygonShape box = new PolygonShape();
		box.setAsBox(width / 2, height / 2);
		fixture = body.createFixture(box, 1);
		fixture.setUserData("muro");
		box.dispose();

		// Ubicar el actor en la stage convirtiendo metros a pixeles
		setSize(width * PIXELS_IN_METER, height * PIXELS_IN_METER);
		setPosition(x * PIXELS_IN_METER, y * PIXELS_IN_METER);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		// Dibujar tubo
		batch.draw(texturaTubo, getX(), getY(), getWidth(), getHeight());

		// Coordenadas parte ancha del tubo dependiendo si es superior o inferior
		float x, y,
				width = getWidth() + 0.5f * PIXELS_IN_METER,
				height = 1 * PIXELS_IN_METER;

		// Tubo de la parte inferior
		if (getY() == 0) {
			x = getX() - 0.25f * PIXELS_IN_METER;
			y = getHeight() - 1 * PIXELS_IN_METER;
		} else {
			//Tubo de la parte superior
			x = getX() - 0.25f * PIXELS_IN_METER;
			y = getY();
		}
		batch.draw(texturaTop, x, y, width, height);
	}

	public void detach() {
		body.destroyFixture(fixture);
		world.destroyBody(body);
	}
}
