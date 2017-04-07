package com.mygdx.game;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mygdx.game.Constants.URL_RANKING;

/**
 * Created by Javier on 03/04/2017.
 */

public class Otros {


    // Consulta el ranking al servidor remoto
    public static String peticionHTTP(String parametros) throws IOException {
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(URL_RANKING+parametros);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStreamHTTP(in);
        }catch (Exception e){
            return "false\nError peticionHTTP(): "+e.toString();
        }finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    // Descodifica la respuesta del servidor HTTP
    public static String readStreamHTTP(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            System.out.println(bo.toString());
            return bo.toString();
        } catch (IOException e) {
            return "false\nError readStreamHTTP(): "+e.toString();
        }
    }
}
