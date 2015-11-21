package com.Naos.Boxed;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class BoxedMain extends ApplicationAdapter {
	
	@Override
	public void create () {
		ScreenManager.FirstScreen(new Game(),"Game",false);
	}

	@Override
	public void render () {
        ScreenManager.Update();
	}
}
