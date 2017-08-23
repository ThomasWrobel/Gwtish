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
 * used to setup scene tests
 * @author darkflame
 *
 */
public class Gwtish extends ApplicationAdapter {

	

	final static String logstag = "GdxScoreTester";
	public static Logger Log = Logger.getLogger(logstag); 
	public static PerspectiveCamera cam;
//	public CameraInputController camController;
	//-----------------------------------
	//------------
	FirstPersonCameraController fpscontrols;
	
	Environment environment;
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();		
		float h = Gdx.graphics.getHeight();

		cam = new PerspectiveCamera(67,w,h);

		cam.position.set(-150f, 150f, 75f); //overhead
		cam.lookAt(0,150,0);
		cam.near = 0.5f;
		cam.far = 1000f;
		cam.update();

		Log.info("camera setup ");
		
		//camController = new CameraInputController(cam);
		 fpscontrols = new FirstPersonCameraController(cam);
		 fpscontrols.setVelocity(60f);
		Gdx.input.setInputProcessor(fpscontrols);

		
		Log.info("camera controller setup ");
		
		//GWTish has its own model manager that must be setup
		//This is required for distance field fonts to render correctly.
		GWTishModelManagement.setup(); 

		Log.info("GWTishModelManagement setup  ");
		
		sceneTest();

		
		Log.info("setup ended");

	}
	  
	public static AssetManager assets;
	boolean loading = false;
	  
	private void sceneTest() {
		
        assets = new AssetManager();
        loading = true;
        
		// a test scene to show/test gwtish functionality (this will add stuff to load)
		DemoScene.setup();
		
		
		
	}

	@Override
	public void render () {
		  if (loading && assets.update())
		  {
			  DemoScene.doneLoading();
			  loading = false;
		  }
		  
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime(); //in seconds!
		GWTishModelManagement.updateTouchState();		

		
		fpscontrols.update(delta);
		
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
	public void dispose () {

		GWTishModelManagement.dispose();
	}
}
