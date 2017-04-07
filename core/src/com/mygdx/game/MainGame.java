package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

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

import static com.mygdx.game.Constants.PIXELS_IN_METER;
import static com.mygdx.game.Constants.URL_RANKING;
import static com.mygdx.game.Constants.VIEWPORT_SIZE;

public class MainGame extends Game {

    private AssetManager manager;
    public BaseScreen loadingScreen, menuScreen, gameScreen, gameOverScreen, rankScreen, settingsScreen, creditsScreen;

    protected int[] score = new int[3];
    protected String nickname, dificultad;
    protected int dificultadInt, scoreTmp;
    protected boolean fullScreen, efectos, musica;
    protected float volumen;

    /**
     * Almacen de la configuracion
     */
    public Preferences settings;


    @Override
    public void create() {
        // Carga asincrona de los Assets
        manager = new AssetManager();
        manager.load("skin/uiskin.json", Skin.class);
        manager.load("bird/frame-1.png", Texture.class);
        manager.load("bird/frame-2.png", Texture.class);
        manager.load("bird/frame-3.png", Texture.class);
        manager.load("bird/frame-4.png", Texture.class);
        manager.load("bird/frame-5.png", Texture.class);
        manager.load("bird/frame-6.png", Texture.class);
        manager.load("bird/frame-7.png", Texture.class);
        manager.load("bird/frame-8.png", Texture.class);
        manager.load("bird/frame-7.png", Texture.class);
        manager.load("bird/frame-8.png", Texture.class);
        manager.load("bird/gotHit/frame-1.png", Texture.class);
        manager.load("bird/gotHit/frame-2.png", Texture.class);
        manager.load("sky/sky.png", Texture.class);
        manager.load("floor.png", Texture.class);
        manager.load("gameover.png", Texture.class);
        manager.load("logo.png", Texture.class);
        manager.load("audio/die.ogg", Sound.class);
        manager.load("audio/jump.ogg", Sound.class);
        manager.load("audio/song.ogg", Music.class);
        manager.load("audio/gameOver.mp3", Music.class);
        manager.load("new.png", Texture.class);

        // Cargar configuracion
        settings = Gdx.app.getPreferences("MiConfig");
        cargarConfig();

        // Mientras carga, mostrar esta pantalla
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);

        score = new int[3];
        consultaHTTPRanking();
    }

    /**
     * Manda consulta HTTP de ranking
     */
    public void consultaHTTPRanking() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_RANKING).newBuilder();
        urlBuilder.addQueryParameter("accion", "listarRecords");
        urlBuilder.addQueryParameter("Nickname", nickname);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print("#######\nConsulta de records personales:");
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String body = response.body().string();
                System.out.print("#######\nConsulta de records personales:");

                if (!response.isSuccessful()) {
                    throw new IOException("\nError inesperado: " + body);
                } else {
                    // Comprobar la consulta
                    Pattern p = Pattern.compile("^true$", Pattern.MULTILINE);
                    Matcher m = p.matcher(body);

                    if (m.lookingAt()) {
                        System.out.println("Exito");
                        procesarRecords(body.substring(5, body.length()));
                    } else {
                        System.out.println("Respuesta inesperada");
                        System.out.println(body);
                    }
                }
            }
        });
    }

    private void procesarRecords(String datos) {
        // Cada dificultad
        String[] dificultad = datos.split("\n");
        for (String dif : dificultad) {

            // Por cada campo (Dificultad, Puntuacion)
            String[] campos = dif.split("\t");
            int puntuacion = 0;

            if (campos.length == 2)
                puntuacion = Integer.parseInt(campos[1]);

            switch (campos[0].charAt(0)) {
                case 'F':
                    score[0] = puntuacion;
                    break;
                case 'N':
                    score[1] = puntuacion;
                    break;
                case 'D':
                    score[2] = puntuacion;
                    break;
            }
        }
    }


    protected void cargarConfig() {
        this.nickname = settings.getString("nickname", "");
        this.dificultad = settings.getString("dificultad", "Normal");
        switch (dificultad.charAt(0)) {
            case 'F':
                dificultadInt = 0;
                break;
            case 'N':
                dificultadInt = 1;
                break;
            case 'D':
                dificultadInt = 2;
                break;
        }

        this.efectos = Boolean.valueOf(settings.getString("efectos", "true"));
        this.musica = Boolean.valueOf(settings.getString("musica", "true"));
        this.fullScreen = Boolean.valueOf(settings.getString("fullScreen", "true"));

        this.volumen = Float.parseFloat(settings.getString("volumen", "75"));
    }

    /**
     * Metodo invocado una vez esta to cargao
     */
    public void finishLoading() throws IOException {
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this);
        rankScreen = new RankScreen(this);
        settingsScreen = new SettingsScreen(this);
        creditsScreen = new CreditsScreen(this);

        PIXELS_IN_METER = VIEWPORT_SIZE.y / 20;

        System.out.println("VIEWPORT_SIZE :" + (int) VIEWPORT_SIZE.x + "x" + (int) VIEWPORT_SIZE.y);
        System.out.println("Tamaño ventana: "+Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        System.out.println("Monitor: "+Gdx.graphics.getDisplayMode());
        System.out.println("PIXELS_IN_METER :" + (int) PIXELS_IN_METER);



        // Pantalla Principal del juego
        setScreen(menuScreen);
        //setScreen(gameScreen);
    }

    public AssetManager getManager() {
        return manager;
    }
}
