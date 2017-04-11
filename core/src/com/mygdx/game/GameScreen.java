package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.entities.EntityFactory;
import com.mygdx.game.entities.PlayerEntity;
import com.mygdx.game.entities.TuboEntity;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.Constants.PARAM_DIFIC;
import static com.mygdx.game.Constants.PIXELS_IN_METER;
import static com.mygdx.game.Constants.PLAYER_POS;
import static com.mygdx.game.Constants.VIEWP_MIN_SIZE;
import static java.lang.Math.abs;


class GameScreen extends BaseScreen {

	private static Sprite sprite;
	public MainGame game;
	private Stage stage;
	private World world;
	private PlayerEntity player;
	private List<TuboEntity> tuboList = new ArrayList<TuboEntity>();
	private Sound jumpSound, dieSound;
	private Music backgroundMusic;
	private Skin skin;
	private Label puntuacion, puntuacionRecord, pressStart;
	private Vector3 position;
	private EntityFactory factory;
	private int numTubo;
	private Batch batch;

	GameScreen(MainGame game) {
		super(game);
		this.game = game;

		// Cantidad de pixeles a representar (no es el tamaño ventana)
		stage = new Stage(new ExtendViewport(VIEWP_MIN_SIZE.x, VIEWP_MIN_SIZE.y));

		// Imagen de fondo
		Texture backgroundTexture = game.getManager().get("sky/sky.png");
		backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		TextureRegion imgTextureRegion = new TextureRegion(backgroundTexture);
		imgTextureRegion.setRegion(0, 0, backgroundTexture.getWidth() * 2000, backgroundTexture.getHeight());

		sprite = new Sprite(imgTextureRegion);

		// Si la screen es mayor que la textura, escalar textura
		if (stage.getHeight() > backgroundTexture.getHeight())
			sprite.setScale(stage.getHeight() / backgroundTexture.getHeight());

		sprite.setCenterY(stage.getHeight() / 2);
		batch = new SpriteBatch();

		position = new Vector3(stage.getCamera().position);

		factory = new EntityFactory(game.getManager());

		// Create a new Box2D world sin gravedad inicialmente
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new GameContactListener());

		// Carga de Assets
		jumpSound = game.getManager().get("audio/jump.ogg");
		dieSound = game.getManager().get("audio/die.ogg");
		backgroundMusic = game.getManager().get("audio/song.ogg");
		skin = game.getManager().get("skin/uiskin.json");

		// Texto puntuacion
		puntuacion = new Label("", skin);
		puntuacionRecord = new Label("¡Record personal!", skin);

		// Texto "pulsa para comenzar"
		pressStart = new Label("Pulsa para comenzar", skin);
		pressStart.setPosition(stage.getWidth() / 2 - pressStart.getWidth() / 2, stage.getHeight() / 2 - 100);
	}


	/** Renderiza la pantalla inicial (una sola vez) */
	@Override
	public void show() {
		// Sin graveda hasta que empecemos
		world.setGravity(new Vector2(0, 0));

		// Crear el Player en su posicion inicial y meterlo al Stage
		player = factory.createPlayer(world, game, PLAYER_POS);
		stage.addActor(player);

		// Reinicia la camara a la posicion de salida
		stage.getCamera().position.set(position);
		stage.getCamera().update();

		// Reproducir musica de fondo
		if (game.musica) {
			backgroundMusic.setVolume(game.volumen / 100);
			backgroundMusic.play();
		}

		// Texto puntuacion
		showPoints();


		numTubo = 0;
		game.scoreTmp = 0;

		// Generar los tubos iniciales (los que se ven al inicio)
		do {
			generaTubo();
		}
		while (tuboList.get(tuboList.size() - 1).getX() < stage.getCamera().position.x + stage.getWidth());

		// Texto "pulsa para comenzar"
		stage.addActor(pressStart);
		InputManage.set(this, game, stage);
	}

	/** Las cosas de pantalla se actualizan aqui muchas veces por segundo */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		sprite.draw(batch);
		batch.setProjectionMatrix(stage.getCamera().combined);
		batch.end();

		// Update the stage. This will update the player speed.
		stage.act();

		// Comprueba si se ha superado el siguiente tubo
		if (numTubo < tuboList.size() && tuboList.get(numTubo).getX() <= player.getX()) {
			game.scoreTmp++;
			numTubo += 2;
			generaTubo();
		}

		// Step the world. This will update the physics and update entity positions. No tocar
		world.step(delta, 6, 2);


		// La camara sigue al Player a partir de un punto del Stage
		if (player.getX() > 150 && player.isAlive()) {
			float speed = game.velocidad * delta * PIXELS_IN_METER;
			stage.getCamera().translate(speed, 0, 0);
		}

		// Si Player toca el techo muere
		if (player.getY() + player.getHeight() >= stage.getHeight() && player.isAlive()) {
			playerDie("techo");
		}

		// Si Player toca el suelo muere
		if (player.getY() < 0 && player.isAlive()) {
			playerDie("suelo");
		}

		// Cambio textura player
		player.update(delta);

		// Actualizar y mostrar puntuacion
		showPoints();

		// Ultimo paso del metodo obligatoriamente
		stage.draw();
	}

	/**
	 * Se ejecuta cuando una pantalla pasa a segundo plano
	 */
	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		stage.clear();
		player.detach();
		for (TuboEntity m : tuboList) {
			m.detach();
		}
		tuboList.clear();
		puntuacionRecord.remove();
	}


	/**
	 * Elimina la screen de pantalla
	 */
	@Override
	public void dispose() {
		stage.clear(); // Borrar todos los actores
		stage.dispose(); // Borrar la escena

		player.detach();
		for (TuboEntity m : tuboList) {
			m.detach();
		}

		// Dispose the world to remove the Box2D native data (C++ backend, invoked by Java).
		world.dispose();
	}

	void saltar() {
		pressStart.remove();

		if (player.isAlive()) {
			player.jump();

			if (game.efectos)
				jumpSound.play();
		}
	}

	// El motor detecta que el jugador a muerto
	private void playerDie(String causa) {
		// Llamada al objeto del player
		player.die(causa);

		// Detener sonidos
		if (game.musica)
			backgroundMusic.stop();
		if (game.efectos)
			dieSound.play();

		// Esperar 1.5 segundo y mostrar el GameOverScreen
		stage.addAction(
				Actions.sequence(
						Actions.delay(1.5f),
						Actions.run(new Runnable() {

							@Override
							public void run() {
								game.setScreen(game.gameOverScreen);
							}
						})
				)
		);
	}

	// Actualiza la puntuacion y su posicion en pantalla
	private void showPoints() {
		// Posicion de la puntuacion en la pantalla
		puntuacion.setText("Score: " + game.scoreTmp);
		puntuacion.pack();
		float posX = stage.getCamera().position.x - puntuacion.getWidth() + stage.getWidth() / 2 - 20;
		float posY = stage.getHeight() - puntuacion.getHeight() - 10;
		puntuacion.setPosition(posX, posY);
		puntuacion.toFront();
		stage.addActor(puntuacion);

		// Aviso de nuevo record
		if (game.scoreTmp > game.scoreRecord[game.dificultadInt]) {
			posX = stage.getCamera().position.x - puntuacionRecord.getWidth() + stage.getWidth() / 2 - 20;
			puntuacionRecord.setPosition(posX, posY - 20);
			puntuacionRecord.toFront();
			stage.addActor(puntuacionRecord);
		}
	}

	/* Genera tubos continuamente mientras el jugador avanza */
	private void generaTubo() {
		// Made in Ale
		int df = game.dificultad.equals("Normal") ? 1 : game.dificultad.equals("Dificil") ? 2 : 0;

		int aMin = PARAM_DIFIC[df][0],
				aMax = PARAM_DIFIC[df][1],
				xMin = PARAM_DIFIC[df][2],
				xMax = PARAM_DIFIC[df][3],
				yMin = PARAM_DIFIC[df][4],
				yMax = PARAM_DIFIC[df][5],
				minDistY = PARAM_DIFIC[df][6],
				minDifY = PARAM_DIFIC[df][7];

		float a, x, y, xAnt, yAnt, distY = 0;

		// Generador a
		a = Funciones.generadorFloat(aMin, aMax);

		// Generador y
		if (tuboList.isEmpty()) {
			y = Funciones.generadorFloat(yMin, yMax);
		}else{
			do {
				y = Funciones.generadorFloat(yMin, yMax);
				yAnt = (tuboList.get(tuboList.size() - 2).getHeight() + (tuboList.get(tuboList.size() - 1).getY() - tuboList.get(tuboList.size() - 2).getHeight()) / 2) / PIXELS_IN_METER;
				distY = abs(y - yAnt);
			}while(distY < minDifY);
		}


		// Generador x
		if (tuboList.isEmpty()) {
			x = 8;
		} else {
			xAnt = tuboList.get(tuboList.size() - 2).getX() / PIXELS_IN_METER;

			if(distY < minDistY){
				x = xAnt + distY + Funciones.generadorFloat(xMin, xMax);
			}else{
				x = xAnt + distY/* - Funciones.generadorFloat(xMin, xMax)*/;
			}
			//x = xAnt + distY + Funciones.generadorFloat(xMin, xMax);
		}


		// Generar los dos tubos
		factory.createTubos(world, x, y, a, tuboList, stage);
	}


	/**
	 * Clase encargada de detectar las colisiones entre Entitys
	 */
	private class GameContactListener implements ContactListener {

		private boolean areCollided(Contact contact, Object userA, Object userB) {
			Object userDataA = contact.getFixtureA().getUserData();
			Object userDataB = contact.getFixtureB().getUserData();

			// Por si acaso algun objeto no tiene userData
			if (userDataA == null || userDataB == null) {
				return false;
			}

			// El orden de los elementos A y B no nos importa
			return (userDataA.equals(userA) && userDataB.equals(userB)) ||
					(userDataA.equals(userB) && userDataB.equals(userA));
		}

		/**
		 * Cuando dos Entidades cualesquiera entran en contacto
		 */
		@Override
		public void beginContact(Contact contact) {
			// Si Player toca un Tubo -> muere
			if (areCollided(contact, "player", "tubo")) {
				// Solo los matamos si esta vivo...
				if (player.isAlive()) {
					playerDie("tubo");
				}
			}
		}

		@Override
		public void endContact(Contact contact) {
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
		}
	}
}
