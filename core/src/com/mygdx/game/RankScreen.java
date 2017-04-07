package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mygdx.game.Constants.*;

public class RankScreen extends BaseScreen {

    private Stage stage;
    private Skin skin;
    private Label titulo, HTTP_Error, ll;
    private Table[] tablaRank;
    private TextButton back;
    private String[] datosRanking;
    private Table tablaMain;
    private Button botFacil, botNormal, botDificil;
    private ScrollPane[] scrolls;
    public RankScreen(final MainGame game) {
        super(game);

        stage = new Stage(new ExtendViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Titulo
        titulo = new Label("Ranking", skin);
        titulo.setFontScale(1.2f);
        titulo.setPosition(stage.getWidth() / 2 - titulo.getWidth() / 2, stage.getHeight() - 50);
        stage.addActor(titulo);

        //Botones dificultad
        tablaMain = new Table();

        //Tablas de rankings
        tablaRank = new Table[3];
        for (int i = 0; i < 3; i++) {
            tablaRank[i] = new Table();
        }
        scrolls = new ScrollPane[3];

        // Back Button
        back = new TextButton("Back", skin);
        back.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });
        back.setSize(200, 80);
        back.setPosition(40, 50);
        stage.addActor(back);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        consultaHTTP();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        tablaMain.clear();
        for (Table tr : tablaRank)
            tr.clear();
        if (HTTP_Error != null)
            HTTP_Error.remove();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Manda consulta HTTP de ranking
     */
    public void consultaHTTP() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_RANKING).newBuilder();
        urlBuilder.addQueryParameter("accion", "listar");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print("#######\nConsulta de ranking:");
                System.out.println(e.toString());
                mostrarErrorHTTP();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String body = response.body().string();
                System.out.print("#######\nConsulta de ranking: ");

                if (!response.isSuccessful()) {
                    mostrarErrorHTTP();
                    throw new IOException("\nError inesperado: " + body);
                } else {
                    // Comprobar la consulta
                    Pattern p = Pattern.compile("^true$", Pattern.MULTILINE);
                    Matcher m = p.matcher(body);

                    if (m.lookingAt()) {
                        System.out.println("Exito");
                        mostrarTablas(body.substring(5, body.length()));
                    } else {
                        System.out.println("Respuesta inesperada");
                        System.out.println(body);
                        mostrarErrorHTTP();
                    }
                }
            }
        });
    }

    /**
     * Procesa los datos del ranking y crea las tablas de puntuacion
     */
    private void mostrarTablas(String datos) {
        HorizontalGroup botGroup = new HorizontalGroup();
        botFacil = new TextButton("Facil", skin, "toggle");
        botNormal = new TextButton("Normal", skin, "toggle");
        botDificil = new TextButton("Dificil", skin, "toggle");
        botGroup.addActor(botFacil);
        botGroup.addActor(botNormal);
        botGroup.addActor(botDificil);
        botGroup.space(20);
        tablaMain.add(botGroup);
        tablaMain.row();

        tablaMain.setWidth(stage.getWidth());
        tablaMain.setPosition(stage.getWidth() / 2 - tablaMain.getWidth() / 2, stage.getHeight() - 100);
        tablaMain.center().top();

        // Cargar datos
        datosRanking = datos.split("\t\t\n");

        // Bucle dificultad
        Stack content = new Stack();
        for (int i = 0; i < 3; i++) {

            // Cada jugador
            String[] jugador = datosRanking[i].split("\n");
            for (String jug : jugador) {

                // Cada campo de la tabla
                String[] campos = jug.split("\t");

                Label nickname = new Label(campos[0], skin);
                Label puntuacion = new Label(campos[1], skin);
                Label fecha = new Label(campos[2], skin);

                tablaRank[i].top();
                tablaRank[i].add(nickname).left().pad(10);
                tablaRank[i].add(puntuacion).pad(10);
                tablaRank[i].add(fecha).right().pad(10);
                tablaRank[i].row();
            }
            content.add(tablaRank[i]);
        }
        tablaMain.add(content);

        // Listen to changes in the tab button
        ChangeListener tab_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tablaRank[0].setVisible(botFacil.isChecked());
                tablaRank[1].setVisible(botNormal.isChecked());
                tablaRank[2].setVisible(botDificil.isChecked());
            }
        };
        botFacil.addListener(tab_listener);
        botNormal.addListener(tab_listener);
        botDificil.addListener(tab_listener);

        // Let only one tab button be checked at a time
        ButtonGroup grupoBotones = new ButtonGroup();
        grupoBotones.setMinCheckCount(1);
        grupoBotones.setMaxCheckCount(1);
        grupoBotones.add(botFacil);
        grupoBotones.add(botNormal);
        grupoBotones.add(botDificil);
        stage.addActor(tablaMain);
    }


    /**
     * Muestra un error si el HTTP ha fallado
     */
    private void mostrarErrorHTTP() {
            BitmapFont labelFont = skin.get("default-font", BitmapFont.class);
            labelFont.getData().markupEnabled = true;
            HTTP_Error = new Label("[RED]Error al obtener datos del ranking", skin);
            //HTTP_Error.setFontScale(1.2f);
            HTTP_Error.setPosition(stage.getWidth() / 2 - HTTP_Error.getWidth() / 2, stage.getHeight() - 200);
            stage.addActor(HTTP_Error);
    }
}
