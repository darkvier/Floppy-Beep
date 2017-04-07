package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.mygdx.game.entities.*;

import static com.mygdx.game.Constants.*;

import java.util.ArrayList;
import java.util.List;


public class GameScreen extends BaseScreen{

    public MainGame game;
    private Stage stage;
    private World world;
    private PlayerEntity player;
    private List<MuroEntity> muroList = new ArrayList<MuroEntity>();
    private Sound jumpSound, dieSound;
    private Music backgroundMusic;
    private Skin skin;
    private Label puntuacion, puntuacionRecord, pressStart;
    private Vector3 position;
    private EntityFactory factory;
    private int numMuro;
    private static Texture backgroundTexture;
    private static Sprite sprite;
    private Batch batch;
    private TextureRegion imgTextureRegion;

    public MainGame getGame() {
        return game;
    }

    public GameScreen(MainGame game) {
        super(game);
        this.game = game;

        // Cantidad de pixeles a representar (no es el tamaño ventana)
        stage = new Stage(new ExtendViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));

        // Imagen de fondo
        backgroundTexture = game.getManager().get("sky/sky.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        imgTextureRegion = new TextureRegion(backgroundTexture);
        imgTextureRegion.setRegion(0, 0, backgroundTexture.getWidth() * 2000, backgroundTexture.getHeight());


        sprite = new Sprite(imgTextureRegion);
        //TODO sky no se muestra con resoluciones pequeñas (setScale() < 1
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

        //TODO texto detras de muros
        pressStart.setZIndex(5);
        pressStart.toFront();
    }

    /**
     * Renderiza la pantalla inicialmente (una sola vez)
     */
    @Override
    public void show() {
        // Sin graveda hasta que empecemos
        world.setGravity(new Vector2(0, 0));

        // Crear el Player en su posicion inicial y meterlo al Stage
        player = factory.createPlayer(world, game, PLAYER_POS);
        //player.setScale(3f);
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

        // Texto "pulsa para comenzar"
        stage.addActor(pressStart);

        numMuro = 0;
        game.scoreTmp = 0;

        // Generar los muros iniciales (los que se ven al inicio)
        do {
            generaMuro();
        }
        while (muroList.get(muroList.size() - 1).getX() < stage.getCamera().position.x + stage.getWidth());
    }

    /**
     * Las cosas de pantalla se actualizan aqui
     */
    @Override
    public void render(float delta) {
        // Do not forget to clean the screen.
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.end();


        // Si el jugador pulsa, la partida se inicia, se oculta el texto "Press to start"
        if (player.isStarted())
            pressStart.remove();

        // Update the stage. This will update the player speed.
        stage.act();

        // Comprueba si se ha superado el siguiente muro
        if (numMuro < muroList.size() && muroList.get(numMuro).getX() <= player.getX()) {
            game.scoreTmp++;
            numMuro += 2;
            generaMuro();
        }

        // Step the world. This will update the physics and update entity positions. No tocar
        world.step(delta, 6, 2);


        // La camara sigue al Player a partir de un punto del Stage
        if (player.getX() > 150 && player.isAlive()) {
            float speed = Constants.PLAYER_SPEED * delta * PIXELS_IN_METER;
            stage.getCamera().translate(speed, 0, 0);
        }

        if (game.efectos && Gdx.input.justTouched() && player.isAlive()) {
            jumpSound.play();
        }


        // Si Player toca el techo muere
        if (player.getY() + player.getHeight() >= stage.getHeight() && player.isAlive()) {
            playerDie();
        }

        // Si Player toca el suelo muere
        if (player.getY() < 0 && player.isAlive()) {
            playerDie();
        }

        // Cambio textura player
        player.update(delta);

        // Actualizar y mostrar puntuacion
        showPoints();

        Funciones.screenModeListener(game);

        // Ultimo paso del metodo obligatoriamente
        stage.draw();
    }


    /**
     * Se ejecuta cuando una pantalla pasa a segundo plano
     */
    @Override
    public void hide() {
        stage.clear();
        player.detach();
        for (MuroEntity m : muroList) {
            m.detach();
        }
        muroList.clear();
        puntuacionRecord.remove();
    }


    /**
     * Elimina la screen de pantalla
     */
    @Override
    public void dispose() {
        stage.clear();  // Borrar todos los actores
        stage.dispose(); // Borrar la escena

        player.detach();
        for (MuroEntity m : muroList) {
            m.detach();
        }

        // Dispose the world to remove the Box2D native data (C++ backend, invoked by Java).
        world.dispose();
    }


    // El motor detecta que el jugador a muerto
    private void playerDie() {
        // Llamada al objeto del player
        player.die();

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
        if (game.scoreTmp > game.score[game.dificultadInt]) {
            posX = stage.getCamera().position.x - puntuacionRecord.getWidth() + stage.getWidth() / 2 - 20;
            puntuacionRecord.setPosition(posX, posY - 20);
            puntuacionRecord.toFront();
            stage.addActor(puntuacionRecord);
        }
    }

    /* Genera muros continuamente mientras el jugador avanza */
    private void generaMuro() {
        // Made in Ale
        int df = game.dificultad.equals("Normal") ? 1 : game.dificultad.equals("Dificil") ? 2 : 0;

        int aMin = PARAM_DIFIC[df][0],
                aMax = PARAM_DIFIC[df][1],
                xMin = PARAM_DIFIC[df][2],
                xMax = PARAM_DIFIC[df][3],
                yMin = PARAM_DIFIC[df][4],
                yMax = PARAM_DIFIC[df][5];

        float a, x, y;
        // Generador a
        a = Funciones.generadorFloat(aMin, aMax);

        // Generador x
        if (muroList.size() < 2) {
            x = 10;
        } else {
            float dist = Funciones.generadorFloat(xMin, xMax);
            float ant = muroList.get(muroList.size() - 1).getX() / PIXELS_IN_METER;
            x = ant + dist;
        }

        // Generador y
        if (muroList.size() < 2) {
            y = 10;
        } else {
            float dist = Funciones.generadorFloat(yMin, yMax);
            float ant = muroList.get(muroList.size() - 2).getHeight() / PIXELS_IN_METER;
            if (ant + dist + a / 2 > 18.0) {
                y = ant - dist;
            } else if (ant - dist - a / 2 < 0.0) {
                y = ant + dist;
            } else {
                if (Funciones.generadorBoolean()) {
                    y = ant + dist;
                } else {
                    y = ant - dist;
                }
            }
        }

        // Genera muros
        factory.createMuros(world, x, y, a, muroList, stage);
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
            // Si Player toca un Muro -> muere
            if (areCollided(contact, "player", "muro")) {
                // Solo los matamos si esta vivo...
                if (player.isAlive()) {
                    playerDie();
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
