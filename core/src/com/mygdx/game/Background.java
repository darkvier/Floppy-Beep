package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

class Background extends Actor {

	private final TextureRegion textureRegion;
	private final MainGame game;
	private Rectangle textureRegionBounds1;
	private Rectangle textureRegionBounds2;
	private int speed = 100;

	Background(MainGame game) {
		this.game = game;
		textureRegion = new TextureRegion((Texture) game.getManager().get("sky/sky.png"));
		textureRegionBounds1 = new Rectangle(0 - Constants.VIEWPORT_SIZE.x / 2, 0, Constants.VIEWPORT_SIZE.x, Constants.VIEWPORT_SIZE.y);
		textureRegionBounds2 = new Rectangle(Constants.VIEWPORT_SIZE.x / 2, 0, Constants.VIEWPORT_SIZE.x, Constants.VIEWPORT_SIZE.y);
	}

	@Override
	public void act(float delta) {
		if (leftBoundsReached(delta)) {
			resetBounds();
		} else {
			updateXBounds(-delta);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(textureRegion, textureRegionBounds1.x, textureRegionBounds1.y, Constants.VIEWPORT_SIZE.x,
				Constants.VIEWPORT_SIZE.y);
		batch.draw(textureRegion, textureRegionBounds2.x, textureRegionBounds2.y, Constants.VIEWPORT_SIZE.x,
				Constants.VIEWPORT_SIZE.y);
	}

	private boolean leftBoundsReached(float delta) {
		return (textureRegionBounds2.x - (delta * speed)) <= 0;
	}

	private void updateXBounds(float delta) {
		textureRegionBounds1.x += delta * speed;
		textureRegionBounds2.x += delta * speed;
	}

	private void resetBounds() {
		textureRegionBounds1 = textureRegionBounds2;
		textureRegionBounds2 = new Rectangle(Constants.VIEWPORT_SIZE.x, 0, Constants.VIEWPORT_SIZE.x, Constants.VIEWPORT_SIZE.y);
	}

}