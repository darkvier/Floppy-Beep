package com.mygdx.game.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.MainGame;

import java.util.List;

import static com.mygdx.game.Constants.*;

/** Clase encargada de generar las distintas Entitys de forma comoda */
public class EntityFactory {

	private AssetManager manager;

	public EntityFactory(AssetManager manager) {
		this.manager = manager;
	}

	/**
	 * Crea un PlayerEntity
	 *
	 * @param world     world
	 * @param posInicio vector con la posicion inicial (metros)
	 */
	public PlayerEntity createPlayer(World world, MainGame game, Vector2 posInicio) {
		return new PlayerEntity(world, game, posInicio);
	}


	/***
	 * Crea dos TuboEntity en la misma coor x, usando t como centro del hueco entre los dos tubos.
	 * Los añade a la lista de tubos y al stage
	 * @param world world
	 * @param x coordenada x de los dos tubos (metros)
	 * @param t altura donde se ubica el centro de la apertura entre los dos tubos (metros)
	 * @param apertura tamaño de la apertura (metros)
	 * @param muroList lista con todos los tubos ya existentes
	 * @return Object[2] con los dos tubos creados
	 */
	public Object[] createTubos(World world, float x, float t, float apertura, List<TuboEntity> muroList, Stage stage) {
		Texture pipe = manager.get("pipe.png");
		Texture pipeTop = manager.get("pipeTop.png");
		float stageHeight = VIEWPORT_SIZE.y / PIXELS_IN_METER;

		// Datos para tubo inferior
		float yInf = 0;
		float heightInf = t - (apertura / 2);

		// Datos tubo superior
		float ySup = t + (apertura / 2);
		float heightSup = stageHeight - ySup + 2;

		// Crear tubos y añadir a los 2 arrays
		Object[] res = new Object[2];
		res[0] = new TuboEntity(world, pipe, pipeTop, x, yInf, ANCHO_MURO, heightInf);
		res[1] = new TuboEntity(world, pipe, pipeTop, x, ySup, ANCHO_MURO, heightSup);
		muroList.add((TuboEntity) res[0]);
		muroList.add((TuboEntity) res[1]);
		stage.addActor((TuboEntity) res[0]);
		stage.addActor((TuboEntity) res[1]);
		return res;
	}
}
