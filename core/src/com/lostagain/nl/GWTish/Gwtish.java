package com.lostagain.nl.GWTish;

import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector2;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement;

import com.badlogic.gdx.assets.AssetManager;
import com.lostagain.nl.GWTish.tests.DemoScene;

/**
 * Used to setup scene tests
 * 
 * 
 * @author darkflame
 *
 */
public class Gwtish extends ApplicationAdapter {

	

	final static String logstag = "GdxScoreTester";
	public static Logger Log = Logger.getLogger(logstag); 
	public static PerspectiveCamera cam;
	
	Environment environment;
	
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();		
		float h = Gdx.graphics.getHeight();
	
		cam = new PerspectiveCamera(67,w,h);
		
		DemoScene.setupForDemoScene(Gwtish.cam);

		sceneTest();

		
		Log.info("setup ended");
	}


	public  AssetManager assetsmanager;	
	boolean loading = false;
	  
	private void sceneTest() {
		
		assetsmanager = new AssetManager();
        loading = true;
        
		// a test scene to show/test gwtish functionality (this will add stuff to load)
		DemoScene.setup();
		
		
		
	}
	
	
	static public AssetManager getAssetManager(){
		
		return ((Gwtish)Gdx.app.getApplicationListener()).assetsmanager;
		
	}

	@Override
	public void render () {
		  if (loading && assetsmanager.update())
		  {
			  DemoScene.doneLoading();
			  loading = false;
		  }
		  
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime(); //in seconds!
		GWTishModelManagement.updateTouchState();		

		
		DemoScene.fpscontrols.update(delta);
		
		//----------- Handle interactions:
		float xc = Gdx.input.getX();
		float yc = Gdx.input.getY();
		Vector2 screenCursorPosition = new Vector2(xc,yc);	
		GWTishModelManagement.getHitables(screenCursorPosition.x,screenCursorPosition.y,cam);
		//----
		//Handle animations:
		GWTishModelManagement.updateObjectMovementAndFrames(delta);
		//

		GWTishModelManagement.modelBatch.begin(cam);
		GWTishModelManagement.modelBatch.render(GWTishModelManagement.allStandardInstances,environment); //basic environment
		GWTishModelManagement.modelBatch.end();
		
	}
	
	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose () {

		assetsmanager.dispose();
		GWTishModelManagement.dispose();
	}
}
