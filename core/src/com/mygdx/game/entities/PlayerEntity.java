package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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

/**
 * Clase con las propiedades y metodos del jugador
 */
public class PlayerEntity extends Actor {

    private MainGame game;
    private World world;
    private Body body;
    private Fixture fixture;
    private Animation animatLive, animatDie;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean alive = true;

    /**
     * La stage GameScreen ha cargado y el jugador ha pulsado, la partida se ha iniciado
     */
    private boolean started = false;
    private boolean touched;


    public PlayerEntity(World world, MainGame game, Vector2 position) {
        Input input = Gdx.input;
        input.justTouched();
        this.world = world;
        this.game = game;
        setDebug(true);

        // Create the player body.
        BodyDef def = new BodyDef();
        def.position.set(position);
        def.type = BodyDef.BodyType.DynamicBody;    //Entidad con movimiento
        body = world.createBody(def);

        // Give it some shape.
        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);                   // (2) 1x1 meter box.
        fixture = body.createFixture(box, 3);
        fixture.setUserData("player");
        box.dispose();

        cargarAnimacion();

        // Asignar tamaño convirtiendo metros a pixeles
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
        if (touched != Gdx.input.justTouched()) {
            touched = !touched;
            System.out.println(touched);
        }


        if (Gdx.input.justTouched()) {

            // Si la partida no ha comenzado aun, se activa la gravedad
            if (!started) {
                started = true;
                world.setGravity(Constants.GRAVEDAD);
            } else {
                jump();
            }
        }


        // If the player is alive, change the speed so that it moves.
        if (alive && started) {
            float speedY = body.getLinearVelocity().y;
            body.setLinearVelocity(Constants.PLAYER_SPEED, speedY);
        }
    }

    private void jump() {

        // Solo salta si esta vivo
        if (alive) {
            Vector2 position = body.getPosition();
            body.applyLinearImpulse(0, Constants.IMPULSE_JUMP, position.x, position.y, true);
        }
    }

    public void detach() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
    }


    // Metodo que se invoca cuando muere
    public void die() {
        body.setLinearVelocity(0, 0);
        Vector2 position = body.getPosition();
        body.applyLinearImpulse(0, -10, position.x, position.y, true);
        alive = false;
    }

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
