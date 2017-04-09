package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Constants;
import com.mygdx.game.MainGame;

/** Clase con las propiedades y metodos del jugador */
public class PlayerEntity extends Actor {

	private MainGame game;
	private World world;
	private Body body;
	private Fixture fixture;
	private Animation animatLive, animatDie;
	private TextureRegion currentFrame;
	private float stateTime;
	private boolean alive = true;

	/** La stage GameScreen ha cargado y el jugador ha pulsado, la partida se ha iniciado */
	private boolean started = false;


	PlayerEntity(World world, MainGame game, Vector2 position) {
		this.world = world;
		this.game = game;

		// Create the player body.
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(def);

		// Give it some shape.
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.5f); //Tamaño caja colision (metros)
		fixture = body.createFixture(box, 3); // Peso del player
		fixture.setUserData("player");
		box.dispose();

		cargarAnimacion();

		// Asignar tamaño textura (pixeles)
		setSize(Constants.PIXELS_IN_METER, Constants.PIXELS_IN_METER);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		// Always update the position of the actor when you are going to draw it, so that the
		// position of the actor on the screen is as accurate as possible to the current position
		// of the Box2D body.
		setPosition((body.getPosition().x - 0.5f) * Constants.PIXELS_IN_METER,
				(body.getPosition().y - 0.5f) * Constants.PIXELS_IN_METER);
		batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
	}

	public void update(float delta) {
		// Animacion player vivo/muerto
		stateTime += delta;
		if (isAlive())
			currentFrame = (TextureRegion) animatLive.getKeyFrame(stateTime, true);
		else
			currentFrame = (TextureRegion) animatDie.getKeyFrame(stateTime, true);
	}

	@Override
	public void act(float delta) {
		// Cuando se pulsa la pantalla
		if (Gdx.input.justTouched()) {

			// Si la partida no ha comenzado aun, se activa la gravedad
			if (!started) {
				started = true;
				world.setGravity(Constants.GRAVEDAD);
			} else {
				jump();
			}
		}

		// Velocidad de movimiento del jugador
		if (alive && started) {
			body.setLinearVelocity(game.velocidad, body.getLinearVelocity().y);
		}
	}

	/** Aplicar impulso al saltar */
	private void jump() {
		// Solo salta si esta vivo
		if (alive) {
			Vector2 position = body.getPosition();
			body.applyLinearImpulse(0, game.impulso, position.x, position.y, true);
		}
	}

	public void detach() {
		body.destroyFixture(fixture);
		world.destroyBody(body);
	}


	/** Detiene el muñeco al morir */
	public void die(String causa) {
		if(causa.equals("techo")){
			body.setLinearVelocity(body.getLinearVelocity().x, 0);
		}
		//body.setLinearVelocity(0, 0);
		Vector2 position = body.getPosition();
		//body.applyLinearImpulse(0, -10, position.x, position.y, true);
		alive = false;
	}

	/** Carga las imagenes que animan al player vivo y muerto */
	@SuppressWarnings("unchecked")
	private void cargarAnimacion() {
		TextureRegion[] textRegLive = new TextureRegion[8];
		for (int i = 0; i < textRegLive.length; i++) {
			textRegLive[i] = new Sprite((Texture) game.getManager().get("bird/frame-" + (i + 1) + ".png"));
		}
		animatLive = new Animation(0.15f, textRegLive);

		TextureRegion[] textRegDie = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			textRegDie[i] = new Sprite((Texture) game.getManager().get("bird/gotHit/frame-" + (i + 1) + ".png"));
		}
		animatDie = new Animation(0.15f, textRegDie);
	}

	// Getters and Setters
	public boolean isAlive() {
		return alive;
	}

	public boolean isStarted() {
		return started;
	}
}
