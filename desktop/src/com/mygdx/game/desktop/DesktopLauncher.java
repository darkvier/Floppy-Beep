package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainGame;

import java.net.MalformedURLException;

import static com.mygdx.game.Constants.VIEWP_MIN_SIZE;

public class DesktopLauncher {
	public static void main (String[] arg) throws MalformedURLException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = (int) VIEWP_MIN_SIZE.x;
		config.height = (int) VIEWP_MIN_SIZE.y;
		config.forceExit = true; //Forzar terminacion app al salir

		config.addIcon("icon/icon16.png", Files.FileType.Internal);
		config.addIcon("icon/icon32.png", Files.FileType.Internal);
		config.addIcon("icon/icon48.png", Files.FileType.Internal);
		config.addIcon("icon/icon72.png", Files.FileType.Internal);
		config.addIcon("icon/icon96.png", Files.FileType.Internal);
		config.addIcon("icon/icon128.png", Files.FileType.Internal);

		new LwjglApplication(new MainGame(), config);
	}
}
