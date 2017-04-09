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
	 * Crea dos MuroEntity en la misma coor x, usando t como centro del hueco entre los dos muros.
	 * Los añade a la lista de muros y al stage
	 * @param world world
	 * @param x coordenada x de los dos muros (metros)
	 * @param t altura donde se ubica el centro de la apertura entre los dos muros (metros)
	 * @param apertura tamaño de la apertura (metros)
	 * @param muroList lista con todos los muros ya existentes
	 * @return Object[2] con los dos muros creados
	 */
	public Object[] createMuros(World world, float x, float t, float apertura, List<MuroEntity> muroList, Stage stage) {
		Texture floorTexture = manager.get("floor.png");
		float stageHeight = VIEWPORT_SIZE.y / PIXELS_IN_METER;

		// Datos para muro inferior
		float yInf = 0;
		float heightInf = t - (apertura / 2);

		// Datos muro superior
		float ySup = t + (apertura / 2);
		float heightSup = stageHeight - ySup;

		// Crear muros y añadir a los 2 arrays
		Object[] res = new Object[2];
		res[0] = new MuroEntity(world, floorTexture, x, yInf, ANCHO_MURO, heightInf);
		res[1] = new MuroEntity(world, floorTexture, x, ySup, ANCHO_MURO, heightSup);
		muroList.add((MuroEntity) res[0]);
		muroList.add((MuroEntity) res[1]);
		stage.addActor((MuroEntity) res[0]);
		stage.addActor((MuroEntity) res[1]);
		return res;
	}
}
