package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Constants;
import com.mygdx.game.MainGame;

import java.net.MalformedURLException;
import static com.mygdx.game.Constants.*;

public class DesktopLauncher {
	public static void main (String[] arg) throws MalformedURLException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) VIEWPORT_SIZE.x;
		config.height = (int) VIEWPORT_SIZE.y;
		config.addIcon("icon/icon16.png", Files.FileType.Internal);
		config.addIcon("icon/icon32.png", Files.FileType.Internal);
		config.addIcon("icon/icon48.png", Files.FileType.Internal);
		config.addIcon("icon/icon72.png", Files.FileType.Internal);
		config.addIcon("icon/icon96.png", Files.FileType.Internal);
		config.addIcon("icon/icon128.png", Files.FileType.Internal);
		config.fullscreen = false;
		new LwjglApplication(new MainGame(), config);
	}
}
