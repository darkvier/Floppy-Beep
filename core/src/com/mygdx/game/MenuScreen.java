package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.badlogic.gdx.Application.*;
import static com.badlogic.gdx.Application.ApplicationType.*;
import static com.mygdx.game.Constants.*;


public class MenuScreen extends BaseScreen {

    private Stage stage;
    private Skin skin;
    private Image logo;
    private TextButton play, rank, settings, credits;
    private Label nickText;

    public MenuScreen(final MainGame game) {
        super(game);

        stage = new Stage(new ExtendViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        play = new TextButton("Jugar", skin);
        System.out.println(Gdx.app.getType());
        switch(Gdx.app.getType()) {
            case Android:
                PIXELS_IN_METER += 20;
                System.out.println(PIXELS_IN_METER);
                break;
            case Desktop:
                break;
            case HeadlessDesktop:
                break;
        }
        rank = new TextButton("Ranking", skin);
        settings = new TextButton("Opciones", skin);
        credits = new TextButton("Creditos", skin);
        nickText = new Label(game.nickname, skin);
        logo = new Image(game.getManager().get("logo.png", Texture.class));


        // Funciones a ejecutar cuando se pulsan los distintos botones
        play.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.gameScreen);
            }
        });

        rank.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.rankScreen);
            }
        });

        settings.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.settingsScreen);
            }
        });

        credits.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.creditsScreen);
            }
        });

        // Tamaño de elementos
        play.setSize(200, 60);
        rank.setSize(200, 60);
        settings.setSize(200, 60);
        credits.setSize(200, 60);

        // Posiciones
        logo.setPosition(500 - logo.getWidth() / 2, 370 - logo.getHeight());
        play.setPosition(40, 220);
        rank.setPosition(100, 150);
        settings.setPosition(160, 80);
        credits.setPosition(220, 10);
        nickText.setPosition(stage.getWidth()- nickText.getWidth()-20, 10);

        // Añadir al Stage
        stage.addActor(logo);
        stage.addActor(play);
        stage.addActor(rank);
        stage.addActor(settings);
        stage.addActor(credits);
        stage.addActor(nickText);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Si no se ha definido el nombre se pide
        if(game.nickname == "")
            askNickname();
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

        Funciones.screenModeListener(game);
    }


    /** Muestra un popup pidiendo al usuario que introduzca su nombre */
    private void askNickname(){

        // Campo texto para escribir
        final TextField nickTxt = new TextField("", skin);

        // Pop-Up
        Dialog dialog = new Dialog("Elige tu nombre de jugador", skin, "dialog") {
            public void result(Object obj) {
                // Una vez se pulsas aceptar
                if(obj.equals(true)) {
                    // Se actualiza el nombre y se muestra en un Label
                    game.nickname = nickTxt.getText();
                    nickText = new Label(nickTxt.getText(), skin);

                    nickText.setPosition(stage.getWidth()- nickText.getWidth()-20, 10);
                    stage.addActor(nickText);

                    // Guardar el nickname
                    game.settings.putString("nickname", game.nickname);
                    game.settings.flush();
                }
            }
        };

        // Propiedades del Pop-Up
        dialog.getBackground().setMinHeight(110);
        dialog.getContentTable().add(nickTxt).pad(10);
        dialog.button("Aceptar", true).padBottom(10);
        dialog.button("Cancelar", false).padBottom(10);
        dialog.key(Input.Keys.ENTER, true);
        nickTxt.setMessageText("Your nickname");
        dialog.show(stage);
        stage.setKeyboardFocus(nickTxt);
        //nickTxt.getOnscreenKeyboard.show(true);
    }
}
