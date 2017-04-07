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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.mygdx.game.Constants.VIEWPORT_SIZE;

public class SettingsScreen extends BaseScreen {

    // TODO full screen PC
    // TODO nombre del jugador *editable*
    // TODO ajuste cantidad impulso?
    // TODO ajuste nivel musica, sonidos...
    //TODO valores iniciales preConfigurados

    private Stage stage;
    private Skin skin;
    private Label credits, volText, nickname, etiquetaAudio, etiquetaJuego, etiquetaDificultad;
    private TextButton back;
    private Slider volumenSlid;
    private CheckBox musica, efectos;
    private SelectBox<Object> dificultad;

    public SettingsScreen(final MainGame game) {
        super(game);

        stage = new Stage(new ExtendViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        back = new TextButton("Back", skin);
        back.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });


        nickname = new Label("Nombre Jugador: " + game.nickname, skin);

        credits = new Label("By Attribution 3.0", skin);

        // Audio
        etiquetaAudio = new Label("AUDIO", skin);
        musica = new CheckBox(" Musica", skin);
        efectos = new CheckBox(" Efectos", skin);
        volText = new Label("Volumen", skin);
        volumenSlid = new Slider(0, 100, 10, false, skin);
        volumenSlid.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                volText.setText(volumenSlid.getValue() + "%");
            }
        });

        // Juego
        etiquetaJuego = new Label("JUEGO", skin);
//        musica = new CheckBox(" Musica", skin);
//        musica.setChecked(true);
//        efectos = new CheckBox(" Efectos", skin);
//        efectos.setChecked(true);
//        volText = new Label("Volumen", skin);
//        volumenSlid = new Slider(0, 100, 10, false, skin);
//        volumenSlid.addListener(new ChangeListener() {
//            public void changed (ChangeEvent event, Actor actor) {
//                Gdx.app.log("UITest", "slider: " + volumenSlid.getValue());
//                volText.setText(volumenSlid.getValue()+"%");
//            }
//        });


        // Dificultad

        etiquetaDificultad = new Label("DIFICULTAD", skin);
        dificultad = new SelectBox<Object>(skin);
        dificultad.setItems("Facil", "Normal", "Dificil", "Leyenda");

        // Sizes & Positions
        nickname.setPosition(400, 500 - nickname.getHeight());

        etiquetaAudio.setPosition(60, 410 - etiquetaAudio.getHeight());
        efectos.setPosition(50, 380 - efectos.getHeight());
        musica.setPosition(50, 360 - musica.getHeight());
        volText.setPosition(85, 330 - volText.getHeight());
        volumenSlid.setPosition(50, 310 - volumenSlid.getHeight());
//        volumenSlid.setSize(200, 100);

        etiquetaJuego.setPosition(300, 410 - etiquetaJuego.getHeight());
//        efectos.setPosition(50, 380 - efectos.getHeight());
//        musica.setPosition(50, 360 - musica.getHeight());
//        volText.setPosition(85, 330 - volText.getHeight());
//        volumenSlid.setPosition(50, 310 - volumenSlid.getHeight());

        etiquetaDificultad.setPosition(500, 410 - etiquetaDificultad.getHeight());
        dificultad.setSize(100, 20);
        dificultad.setPosition(500, 350);

        credits.setPosition(500, 40 - credits.getHeight());
        back.setSize(200, 80);
        back.setPosition(40, 50);


        // addActors
        stage.addActor(back);
        stage.addActor(nickname);
        stage.addActor(credits);

        stage.addActor(etiquetaAudio);
        stage.addActor(efectos);
        stage.addActor(musica);
        stage.addActor(volText);
        stage.addActor(volumenSlid);

        stage.addActor(etiquetaJuego);

        stage.addActor(etiquetaDificultad);
        stage.addActor(dificultad);


    }

    @Override
    public void show() {
        System.out.println(game.dificultad);
        System.out.println(game.efectos);
        System.out.println(game.musica);
        System.out.println(game.volumen);

        // Setear valores configuracion
        dificultad.setSelected(game.dificultad);
        efectos.setChecked(game.efectos);
        musica.setChecked(game.musica);
        volText.setText(game.volumen + "%");
        volumenSlid.setValue(game.volumen);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // Guardar config y cargar en game
        game.settings.putString("dificultad", dificultad.getSelected().toString());
        game.dificultad = dificultad.getSelected().toString();
        game.settings.putFloat("volumen", volumenSlid.getValue());
        game.settings.putBoolean("efectos", efectos.isChecked());
        game.settings.putBoolean("musica", musica.isChecked());
        game.settings.flush();
        game.cargarConfig();
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
}