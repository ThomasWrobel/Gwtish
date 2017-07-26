package com.lostagain.nl.GWTish.Management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array; //NOTE: This is like an arraylist but better optimized for libgdx stuff
import com.badlogic.gdx.utils.ObjectSet; //NOTE: This is like a hashset but apparently is better optimized for libgdx stuff

import com.lostagain.nl.GWTish.Event;
import com.lostagain.nl.GWTish.NativeEvent;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.GWTish.shader.GwtishDefaultShaderProvider;

public class GWTishModelManagement {

	private static String logstag="ME.GWTishModelManagement";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag



	/** All the 3d models we want to render in the standard draw order should go into this list **/
	public static ObjectSet<ModelInstance> allStandardInstances = new ObjectSet<ModelInstance>();

	/** All the 3d models that appear INFRONT of the Standard ones.
	 * You can use this for special overlay objects - effects, interfaces, etc.
	 * Remember fine-grain controll within grouped objects should be dealt with the Zindex attribute, not this **/
	public static ObjectSet<ModelInstance> allOverlayInstances = new ObjectSet<ModelInstance>();


	public static ShaderProvider myshaderprovider = new GwtishDefaultShaderProvider();



	public static ModelBatch modelBatch;

	public static RenderableSorter mysorter;


	/**all hitable models **/
	public static ObjectSet<hitable> hitables = new ObjectSet<hitable>(); //should be changed to a set to stop duplicates

	/** everything the mouse is currently down over **/
	public static ObjectSet<hitable> mousedownOn = new ObjectSet<hitable>();

	/** Has a drag started? (that is, has the cursor moved after its been down, but not released?)**/
	static boolean dragStarted = false;


	/** All model with texture animations **/
	public static ObjectSet<Animating> animatingobjects = new ObjectSet<Animating>();

	/** All models currently moving **/
	public static ObjectSet<Moving> movingObjects = new ObjectSet<Moving>();


	static public enum RenderOrder {
		STANDARD,OVERLAY;//,zdecides; //zdecides not used
	}

	//test objects
	static AnimatableModelInstance lookAtTester;


	public static void addmodel(AnimatableModelInstance model) {	
		addmodel(model,RenderOrder.STANDARD);
	}

	/** 
	 * Adds the model to the render list.
	 * RenderOrder determines if its rendered in front or behind the spritestage.
	 * 
	 * You can also  it chooses if its a background or foreground object based on its Z position
	 * If Z is less then the stage Z 5 its behind
	 * If its more then 5its in front
	  If adding a AnimatableModelInstance we also check all its attachments are added too. **/
	public static void addmodel(AnimatableModelInstance model, GWTishModelManagement.RenderOrder order) {	

		addmodel((ModelInstance)model,order); //note we cast so as to call the non-AnimatableModelInstance specific method below

		for (IsAnimatableModelInstance attachedModel : model.getAttachments()) {	
			if (attachedModel.isInheriteingVisibility() && attachedModel.isVisible()){
				addmodel((AnimatableModelInstance)attachedModel,order); //we call this function so as to get objects attached to objects attached to this, etc
			}
		}

	}

	public static void addmodel(ModelInstance model) {	
		addmodel(model,RenderOrder.STANDARD);
	}
	/** Adds the model to the render list.
	 * RenderOrder determines if its rendered in front or behind the spritestage.
	 * 
	 * You can also  it chooses if its a background or foreground object based on its Z position
	 * If Z is less then the stage Z 5 its behind
	 * If its more then 5its in front**/
	public static void addmodel(ModelInstance model, RenderOrder order) {	

		//temp test putting it all in front of the stage while we test material based ordering
		//order = GWTishModelManagement.RenderOrder.OVERLAY;		


		//ignore if present already
		if (allStandardInstances.contains(model) || allOverlayInstances.contains(model)){
			Log.info("________model already on a render list");
			return;
		}
		Vector3 position = new Vector3();
		model.transform.getTranslation(position);
		float Z = position.z; //is this always correct?
		//float Z = model.transform.getValues()[Matrix4.M23];

		//	Gdx.app.log(logstag,"z = "+Z);

		if (order == RenderOrder.STANDARD ){
			allStandardInstances.add(model);
			return;
		}

		if (order == RenderOrder.OVERLAY){
			allOverlayInstances.add(model);
			return;
		}

		allStandardInstances.add(model); //temp till we remove zdecides

		//depending on if we are above/below the stage position we add it accordingly		
		//if (Z<5){
		//	allBackgroundInstances.add(model);
		//} else {
		//	allForgroundInstances.add(model);
		//}

	}

	/**
	 * removes the model from the render lists, and returns where it was in the render order (either forground or background)
	 * @param model
	 * @return 
	 */
	public static GWTishModelManagement.RenderOrder removeModel(ModelInstance model) {		
		Boolean wasInForground = allStandardInstances.remove(model);

		if (wasInForground){
			return GWTishModelManagement.RenderOrder.STANDARD;			
		}

		Boolean wasInBackground= allOverlayInstances.remove(model);

		if (wasInBackground){
			return GWTishModelManagement.RenderOrder.OVERLAY;			
		}

		return null;
	}


	/** the current state of mouse or touch **/
	public static GWTishModelManagement.TouchState currentTouchState = GWTishModelManagement.TouchState.NONE;

	public static Vector2 touchStartedAt = null;


	/**
	 * @return the lastHits
	 */
	public static ArrayList<hitable> getLastUnderCursor() {
		return underCursorList;
	}

	//--

	//	public static  GWTishModelManagement the3dscene = new GWTishModelManagement();

	private static ArrayList<hitable> lastHits = new ArrayList<hitable>();


	/**
	 * @return the lastHits
	 */
	public static ArrayList<hitable> getLastHits() {
		return lastHits;
	}



	/**
	 * tests the sort order of foreground objects
	 * @param deltatime
	 */
	//public void testSortOrder(float deltatime){

	//	modelBatch.getRenderableSorter().sort(MainExplorationView.camera,);


	//}


	public void updateAnimatedBacks(float deltatime){


		//replaced with noise shader !
		/*
		TextureRegion currentimage = testNoise.getKeyFrame(deltatime);

		for (ModelInstance instance : animatedbacks) {

			TextureAttribute attribute = instance.materials.get(0).get(TextureAttribute.class, TextureAttribute.Diffuse);

			if (attribute!=null){			
				attribute.set(currentimage );		
			} else {
				Gdx.app.log(logstag,"________************________________attribute is null:");
			}

		}*/


	}


	/**
	 * run setup() before using gwtish widgets
	 * by default the smartsort is turned on, and a internal shader provider is used
	 */
	static public void setup(){
		setup(true,null);
	}
	
	/**
	 * @param shaderprovider - use your own shaderprovider
	 * @param useSmartSorter - to support zindex set to true
	 */
	static public void setup(boolean useSmartSorter, ShaderProvider shaderprovider){

		// String vert = Gdx.files.internal("shaders/test.vertex.glsl").readString();//"shaders/distancefield.vert"
		// String frag = Gdx.files.internal("shaders/test.fragment.glsl").readString();

		//modelBatch = new ModelBatch(vert,frag);
		//	 MyShaderProvider myshaderprovider = new MyShaderProvider();

		if (useSmartSorter){
			mysorter = new MySorter();
		} else {
			mysorter = new DefaultRenderableSorter();
		}
		if (shaderprovider==null){
			shaderprovider=myshaderprovider;
		}
		modelBatch = new ModelBatch(shaderprovider,mysorter);

		//First we add one object at the center with a defaultshader used
		// Its VERY important to use a defaultshader object as the first thing created, else
		//the default shader will get confused and think it can render things with other shaders too.
		//This is because to figure out if it can render something it always compared to the first object it gets.

		//We also have to have transparency on the first shader we make else transparency wont be supported at all
		Material testmaterial = new Material(
				ColorAttribute.createDiffuse(Color.RED), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));

		ModelBuilder modelBuilder = new ModelBuilder();
		//note; maybe these things could be pre-created and stored rather then a new one each time?
		Model model =  modelBuilder.createXYZCoordinates(25f, testmaterial, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		
		
		
		ModelInstance centermaker =  new ModelInstance(model); //ModelMaker.createCenterPoint(testmaterial);


		Renderable renderableWithoutAttribute = new Renderable();
		centermaker.getRenderable(renderableWithoutAttribute);

		//DefaultShader test = new DefaultShader(renderableWithoutAttribute);
/*
		Material beamShader = new Material(
				ColorAttribute.createDiffuse(Color.BLUE), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f),
				new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,5.0f,Color.WHITE)
				);






		ModelInstance beamtest = ModelMaker.createRectangleAt(500, 1000, 30, 200, 200, Color.BLACK, beamShader); // new ModelInstance(model1); 
*/
		//	Renderable renderableWithAttribute = new Renderable();
		//	beamtest.getRenderable(renderableWithAttribute);

		//	Boolean defaultCanRender = test.canRender(renderableWithAttribute);


		//	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);



		//--------------	

		//The following was a test of how the domains colour range should look. Re-enable during testing.

		/*
		Pixmap colourMapAsPixMap = MEDomain.getHomeDomain().getDomainsColourMap().getPixMap(200, 200);
		//Pixmap colourMapAsPixMap = MessyModelMaker.createNoiseImage(200, 200);


		Texture colmap = new Texture(colourMapAsPixMap);

		Material testmaterial3 = new Material
				(
						ColorAttribute.createSpecular(Color.WHITE),
						new BlendingAttribute(1f), 
						FloatAttribute.createShininess(16f),
						TextureAttribute.createDiffuse(colmap)
						);



		ModelInstance colortest = ModelMaker.createRectangleAt(0, 900, 130, 200, 200, Color.BLACK, testmaterial3); 
		 */


		//ModelManagment.addmodel(centermaker,RenderOrder.infrontStage);

/*

		if (GameMode.currentGameMode!=GameMode.Production){
			GWTishModelManagement.addmodel(beamtest,GWTishModelManagement.RenderOrder.OVERLAY);
			//	ModelManagment.addmodel(colortest,RenderOrder.infrontStage);

			addTestModels();
		}

*/







		//A lot of earlier code used for creating and debugging objects and shaders is below
		//While its not critical, please leave for now as its a good reference for cutting and pasting tests

		//NoiseAnimation testNoise = new NoiseAnimation();
		//Gdx.app.log(logstag,"creating testbounc");
		//testNoise.create();

		//FileHandle imageFileHandle3 = Gdx.files.internal("data/badlogic.jpg"); 

		//Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
		// Texture blobtexture = new Texture(imageFileHandle3);
		//  blobtexture.bind(0);

		/*
	        Material testmaterial = new Material(
	        		ColorAttribute.createDiffuse(Color.BLUE), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f),new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,Color.WHITE));

	       // testmaterial.set(TextureAttribute.createDiffuse(blobtexture));

	    	ModelInstance instance = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, testmaterial); // new ModelInstance(model1); 
			Renderable renderableWithAttribute = new Renderable();
			instance.getRenderable(renderableWithAttribute);

	    	DefaultShader test = new DefaultShader(renderableWithAttribute);
	    	Boolean defaultCanRender = test.canRender(renderableWithAttribute);
	    	//If true this means a default shader created with a renderable with the custom attribute will by rendered by DefaultShader
	    	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);


	    	ModelInstance instance2 = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK,null);
	    	Renderable renderableWithoutAttribute = new Renderable();
	    	instance2.getRenderable(renderableWithoutAttribute);

	    	DefaultShader test2 = new DefaultShader(renderableWithoutAttribute);
	    	Boolean defaultCanRender2 = test2.canRender(renderableWithAttribute); //now we test if the shader created without the attribute will render with one.
	    	//If true this means a default shader will think it can render 
	    	Gdx.app.log(logstag,"default created without attribute = "+defaultCanRender2);




			//Model  model1 = modelBuilder.createSphere(150, 150, 150, 20, 20,
			//		blob,Usage.Position | Usage.Normal | Usage.TextureCoordinates );

			//String alias = model1.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias;

		//	Model model1 = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, blob);


			//Gdx.app.log(logstag,"aliasaliasaliasalias = "+alias);
	//

			//model2.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias = "a_texCoord";

			//  Model model3 = createRectangle(-10f,500f,10f,-500f,0f);


			//  ModelInstance model4 = createRectangleAt(50f,50f,10f,200f,0f);



			instance.userData = MyShaderProvider.shadertypes.conceptbeam;

			ModelManagment.addmodel(instance);

			//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
			//CameraOverlay.materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.0f));

			//CameraOverlay.userData =MyShaderProvider.shadertypes.noise; 
			//instance2.userData = MyShaderProvider.shadertypes.distancefield;
			//ModelManagment.addmodel(CameraOverlay);



		 */

		//for some reason transparency's dont work till we add something with a transparent texture for the first time

		/*
	    	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), false);
			texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);

			Model model2 = modelBuilder.createBox(220f, 220f, 15f, 
					new Material(TextureAttribute.createDiffuse(texture),new BlendingAttribute(0.6f)),
					Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	        ModelInstance instance3 = new ModelInstance(model2);      

			ModelManagment.addmodel(instance3);
		 */

		//instance.transform.setToTranslation(200,500,10);

		//  environment = new Environment();
		//  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		// environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	public static void addTestModels() {

		Material blue = new Material
				(
						ColorAttribute.createSpecular(Color.WHITE),
						new BlendingAttribute(1f), 
						FloatAttribute.createShininess(16f),
						ColorAttribute.createDiffuse(Color.BLUE)
						);

		ModelBuilder modelBuilder = new ModelBuilder();

		Model lookAtTesterm =  modelBuilder.createXYZCoordinates(95f, blue, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		lookAtTester = new AnimatableModelInstance(lookAtTesterm);
		lookAtTester.setToPosition(new Vector3 (500,500,0));


		GWTishModelManagement.addmodel(lookAtTester,GWTishModelManagement.RenderOrder.OVERLAY);

	}


	/**
	 * This class stores hits between rays and objects.
	 * NOTE: The equals has been overriden so this can be directly compared with a object list.
	 * this means  a.equals(b) will be true even if a is a rayHit and b is a animatedObject.
	 * This is quite different to how equals normally behaves.
	 * 
	 * @author darkflame	 *
	 */
	static public class rayHit {

		hitable hitthis;
		Vector3 atThis;

		public rayHit(hitable hitthis, Vector3 atThis) {
			super();
			this.hitthis = hitthis;
			this.atThis = atThis;
		}
		/**
		 * NOTE: The equals has been overriden so this can be directly compared with a object list.
		 * this means  a.equals(b) will be true even if a is a rayHit and b is a animatedObject.
		 * This is quite different to how equals normally behaves.
		 */
		//@Override
		//public boolean equals(Object obj) {
		////	if (hitthis.equals(obj)){
		//		return true;
		//	}			
		//	return super.equals(obj);
		//}

	}
	/**
	 * what was in the line of the ray the last time getHitables was run
	 */
	static ArrayList<rayHit> underCursorHits = new ArrayList<rayHit>();

	/**
	 * what was in the line of the ray the last time getHitables was run
	 */
	static ArrayList<hitable> underCursorList = new ArrayList<hitable>();
	/** 
	 * Comparator to sort by distance
	 */
	static public class OrderByDistance implements Comparator<rayHit> {
		@Override
		public int compare(rayHit o1, rayHit o2) {

			//if either is overlay but not the other, put that on top
			if (o1.hitthis.isOverlay() && !o2.hitthis.isOverlay() ){
				return -1;
			}
			//if either is overlay but not the other, put that on top
			if (!o1.hitthis.isOverlay() && o2.hitthis.isOverlay() ){
				return 1;
			}	

			//if both are widgets then we compare zindexs if they are in the same group
			if (o1.hitthis instanceof Widget
					&& o2.hitthis instanceof Widget){

				Widget o1w = (Widget) o1.hitthis;
				Widget o2w = (Widget) o2.hitthis;
				// same group?
				if (o1w.getStyle().getZIndexGroup() == o2w.getStyle().getZIndexGroup()){
					int zin1 = o1w.getStyle().getZIndexValue();
					int zin2 = o2w.getStyle().getZIndexValue();
					return -(zin1-zin2);
				}

			}


			//---
			float hit1 = o1.hitthis.getLastHitsRange();
			float hit2 = o2.hitthis.getLastHitsRange();		

			return (int) (hit1 - hit2);
		}
	};

	/**  mouse state (still being implemented. Will replace newtouch and newup)
	 **/
	public static enum TouchState {
		/** screen is not touched or mouse is not down **/
		NONE,
		/**
		 * the mouse has just gone down or the screen has just been touced
		 */
		NewTouchDown,
		/**
		 * screen is being touched or mouse is down
		 */
		TouchDown,
		/**
		 * mouse or touch just went up
		 */
		NewTouchUp,

		/**
		 * The mouse has just moved a bit since being down  **/
		NewDrag,

		/**
		 * The mouse has moved a bit since being down and is still being held **/
		Dragging;

	}

	static OrderByDistance distanceSorter = new OrderByDistance();


	private static double ScenePixelScaleX=1;
	private static double ScenePixelScaleY=1;






	public static void setSceneScale(double currentSceneRatio, double currentSceneRatio2) 
	{
		ScenePixelScaleX = currentSceneRatio;
		ScenePixelScaleY = currentSceneRatio2;

	}

	/**
	 * note; the coordinates must be given unscaled (ie, real pixels of 1:1 viewport)
	 * If you wish Event.getCurrentEvent().getClientX/Y to return values scaled  to your current display
	 * Set your scene scaleing with setSceneScale(x,y). Values will then be multiplied by this  
	 *  
	 * @param x - unscaled screen x
	 * @param y - unscaled screen y
	 * @param camera
	 * @return
	 */
	public static ArrayList<hitable> getHitables(float x, float y, Camera camera) 
	{


		//generate current event object
		boolean altKeyWasPressed   = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
		boolean cntrKeyWasPressed  = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
		boolean shiftKeyWasPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
		int currentEventX        = (int) x;
		int currentEventY        = (int) y;
		int currentEventKeyCode    = 0; //TODO: fill this in if possible?

		//The mouse button currently down
		NativeEvent.EventButtonType buttonTypeDown = NativeEvent.EventButtonType.None;	

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			buttonTypeDown = NativeEvent.EventButtonType.Left;
		} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
			buttonTypeDown = NativeEvent.EventButtonType.Right;
		}  else if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)){
			buttonTypeDown = NativeEvent.EventButtonType.Middle;
		}



		Event currentEvent = new Event(
				buttonTypeDown,
				altKeyWasPressed,  
				cntrKeyWasPressed,  
				shiftKeyWasPressed,
				(int)(currentEventX*ScenePixelScaleX),
				(int)(currentEventY*ScenePixelScaleY),  
				currentEventKeyCode);	


		//set it as the current event (this also stores the current last event as the previous event)
		Event.setCurrentEvent(currentEvent);


		Ray pickray = camera.getPickRay(x, y);

		TouchState currentTouchState = GWTishModelManagement.currentTouchState;
		ArrayList<hitable> hits = getHitables(pickray,false,currentTouchState); //penitates was previously on true. This seemed wrong so changed to false 

		//clear event
		Event.setCurrentEvent(null);
		lastHits=hits;

		return hits;
	}


	//TODO: make lower statement private in favor of above
	/**
	 * Gets all the hitables the current ray hits.
	 * If penetrate isn't turned on, nothing under the first blocker will be returned.
	 * We always stop at the first interface no mater what.
	 * 
	 * While not returned in order, the first of the array will be the one ontop.
	 * 
	 * @param ray
	 * @param hitsPenetrate
	 * @param processHits
	 * @return
	 */
	public static ArrayList<hitable> getHitables(Ray ray,boolean hitsPenetrate, GWTishModelManagement.TouchState applyTouchAction) {

		underCursorList.clear();
		underCursorHits.clear();

		//We ensure no touch events are canceled. This is only ever set to true during a touch/click event of some sort, when we want to cancel further actions
		cancelCurrentTouchEvent=false;
		//


		//Vector3 position = new Vector3();
		for (hitable newInstance : hitables) {

			//	position = newInstance.getCenterOnStage();

			//first check if it hits at all. We base this on the hitables internal tester
			//this lets different hitables use different intersect types (ie, radius, boundingbox, polygon etc)
			Vector3 hitPoint = newInstance.rayHits(ray);
			if (hitPoint==null){
				continue;
			}
			float dist2 = ray.origin.dst2(hitPoint);
			newInstance.setLastHitsRange(dist2);

			underCursorHits.add(new rayHit(newInstance,hitPoint));				
			underCursorList.add(newInstance);

		}


		//Gdx.app.log(logstag,"_underCursorList_ :"+underCursorList.toString());		

		//If anything is set to auto-hide and not currently under the cursor, then we hide it, provided this was some sort of click event
		if (applyTouchAction!=null && applyTouchAction!=TouchState.NONE){
			Iterator<IsAnimatableModelInstance> autoHideIt = autoHideList.iterator();
			while (autoHideIt.hasNext()) {
				IsAnimatableModelInstance isAnimatableModelInstance = (IsAnimatableModelInstance) autoHideIt.next();

				//	Gdx.app.log(logstag,"_autoHideTesting_ :"+isAnimatableModelInstance.getClass().getName());	

				if (!underCursorList.contains(isAnimatableModelInstance)) {
					Set<IsAnimatableModelInstance> attachments = isAnimatableModelInstance.getAllAttachments();

					//	Gdx.app.log(logstag,"attachments::::"+attachments .toString());	

					//unfortunately we also need to check for children of the autoHidingObject, as we might be clicking
					//something on the object (like a button) but not the object itself)					
					boolean containsNoneOf = Collections.disjoint(underCursorList, attachments);
					if (containsNoneOf){	
						isAnimatableModelInstance.hide();		
						autoHideIt.remove();
					}

				}
			}
		}

		//	Gdx.app.log(logstag,"underCursor:"+underCursor.size());

		//sort by distance
		Collections.sort(underCursorHits,distanceSorter);//todo: might need to take zindex into account 
		//	listUnderCursorToLog(underCursorHits);//helps debug


		ArrayList<hitable> onesHit = new ArrayList<hitable>();

		//crop to the ones on top, hitting as we go
		for (rayHit hit : underCursorHits) {

			//allow a chance to cancel
			if (cancelCurrentTouchEvent){	

				Log.info("canceling current touch event:"+applyTouchAction.name());				
				cancelCurrentTouchEvent = false;
				return onesHit;
			}

			//


			hitable object = hit.hitthis;

			objectInteractionType type = object.getInteractionType();
			onesHit.add(object);

			if (type == objectInteractionType.None){
				//no interactions at all on this object so we just continue to next under mouse
				//(will still be on the onesHit list)
				continue;
			}

			//update the Event data for this specific hit (if hits penetrate each hit might have a different position)
			Event.getCurrentEvent().setCurrentEventLocation(hit.atThis);
			//(no need to make a new event as the rest of the properties will be the same)


			//fire the correct event depending on type
			if (applyTouchAction!=null){
				switch (applyTouchAction) {
				case NONE:
					break;
				case NewTouchDown:
					Log.info("_firing touchdown on :"+object.getClass());
					object.fireTouchDown();
					mousedownOn.add(object);
					break;
				case NewTouchUp:

					Log.info("_firing touchup on :"+object.getClass());
					object.fireTouchUp();					
					boolean wasPreviouslyDownOn = mousedownOn.remove(object);
					if (wasPreviouslyDownOn){
						Log.info("_firing click on :"+object.getClass());
						object.fireClick();
					}
					break;
				case TouchDown:

					break;
				case NewDrag:
					object.fireDragStart();					
					mousedownOn.remove(object);


					break;
				}
			}



			if (type == objectInteractionType.Interface){
				return onesHit;
			}
			if (type == objectInteractionType.Blocker && !hitsPenetrate){

				//	Gdx.app.log(logstag,"_hit blocker :"+object.getClass()+" totalunder cursor:"+underCursorHits.size());


				return onesHit;
			}


		}

		//ensure not canceled for next time
		cancelCurrentTouchEvent=false;

		return onesHit;

	}

	static  boolean cancelCurrentTouchEvent=false;

	/**
	 * cancels the current touch event
	 */
	static	public void cancelCurrentTouchEvents(){
		cancelCurrentTouchEvent = true;
	}

	private static void listUnderCursorToLog(ArrayList<rayHit> underCursorHits2) {
		for (rayHit rayhit : underCursorHits2) {
			Log.info("undercursor last click/touch action    :"+rayhit.hitthis.getName()+"        ("+rayhit.hitthis.getInteractionType()+")");


		}
	}

	/**
	 * 
	 * 
	 * version;
	 * 
	 * 1. Get everything under cursor
	 *    - remember highest hitblocker
	 *    - remember highest hit
	 * 
	 *  2. If not penetrating test highest hit against highest clickblocker
	 *     If higher, then hit and exit.
	 *     
	 *    
	 * 3. If penetrating Loop over potential hits
	 *  - count as hit if higher then highest clickblocker
	 * 
	 * @param ray
	 * @param hitsPenetrate  - if the hits penetrate it means everything under the cursor will count as hit, not just the top.
	 * @param processHits - if the hits should be processed, or if we are just looking for the nearest collision (this can be optimised a lot)
	 * 
	 * @return the nearest hitable
	 * 
	 */
	public static hitable testForHits(Ray ray, boolean hitsPenetrate, boolean processHits) {

		Log.info("_-testing hit in :"+hitables.size+" models");
		Log.info("_-testing ray at :"+ray.origin.x+","+ray.origin.y);


		Vector3 position = new Vector3();

		ArrayList<hitable> everyThingUnderCursor = new ArrayList<hitable>();
		hitable closestNonBlockerTouched = null;	    
		hitable closestBlockerTouched = null;

		for (hitable newInstance : hitables) {




			//for (int i = 0; i < hitables.size; ++i) {

			//final hitable instance = hitables.get(i);

			//instance.getTransform().getTranslation(position);
			//position.add(instance.getCenter());
			position = newInstance.getCenterOnStage();


			//first check if it hits at all. We base this on the hitables internal tester
			//this lets different hitables use different intersect types (ie, radius, boundingbox, polygon etc)

			//	if (Intersector.intersectRaySphere(ray, instance.getCenter(), instance.getRadius(), null)) {
			Vector3 hitPoint = newInstance.rayHits(ray);
			if (hitPoint==null){
				//if it didn't hit we can just skip to the next thing to test
				continue;
			}


			//float dist2 = ray.origin.dst2(position);

			float dist2 = ray.origin.dst2(hitPoint);


			Log.info("_hit "+newInstance.getClass()+" object at distance "+dist2+" position was("+position+")");

			if (newInstance.getInteractionType() == objectInteractionType.Blocker){
				Log.info("(it was blocker)");

			}

			//set last hit range
			newInstance.setLastHitsRange(dist2);


			//if its a blocker we see if its higher then the last blocker
			if (newInstance.getInteractionType() == objectInteractionType.Blocker){

				//if none set just continue
				if (closestBlockerTouched==null){
					closestBlockerTouched=newInstance;
					continue;
				}
				//else we test if its closer
				//Note the > I think this is because the ray goes from furthest to nearest. So higher values are nearer
				if (newInstance.getLastHitsRange()<closestBlockerTouched.getLastHitsRange()){
					Log.info("(new blocker is closer)");
					closestBlockerTouched = newInstance;
					continue;
				}


			} else {

				//add to every not hitable under cursor list
				everyThingUnderCursor.add(newInstance);

				//if its not a blocker we do the same tests for normal objects

				//if none set just continue
				if (closestNonBlockerTouched==null){
					closestNonBlockerTouched=newInstance;
					continue;
				}
				//else we test if its closer
				if (newInstance.getLastHitsRange() < closestNonBlockerTouched.getLastHitsRange()){
					Log.info("(new non-blocker is closer)");
					closestNonBlockerTouched=newInstance;
					continue;
				}


			}







		}

		//now we have all the things potential hit, we check for actual hits 

		//If we arnt penetrating we just see if the highest hitable is higher then the highest blocker
		//if so we hit it and exit
		if (!hitsPenetrate && closestNonBlockerTouched!=null){

			Log.info("ClosestNonBlockerTouched as at:"+closestNonBlockerTouched.getLastHitsRange()+" ("+closestNonBlockerTouched.getClass()+")");

			if (closestBlockerTouched==null){
				if (processHits){
					closestNonBlockerTouched.fireTouchDown();
					mousedownOn.add(closestNonBlockerTouched);
				}
				//	mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
				return closestNonBlockerTouched;
			}

			//if blocker is further then non-blocker
			if (closestBlockerTouched.getLastHitsRange() < closestNonBlockerTouched.getLastHitsRange()){
				Log.info("closestBlockerTouched as at:"+closestBlockerTouched.getLastHitsRange()+" ("+closestBlockerTouched.getClass()+")");

				if (processHits){
					closestNonBlockerTouched.fireTouchDown();
					mousedownOn.add(closestNonBlockerTouched);
				}
				//	mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
				return closestNonBlockerTouched;
			}
		} else{
			Log.info("hits penatrating, mousing down on;"+everyThingUnderCursor.size()+" objects");
		}



		//if not we loop and hit anything above the highest blocker
		for (hitable instance : everyThingUnderCursor) {

			if (closestBlockerTouched==null || instance.getLastHitsRange()<closestBlockerTouched.getLastHitsRange()){

				if (processHits){
					instance.fireTouchDown();
					mousedownOn.add(instance);

				}
			}

		}

		//and if it exists we should hit the highest blocker too
		if (closestBlockerTouched!=null){
			closestBlockerTouched.fireTouchDown();
			mousedownOn.add(closestBlockerTouched);

		}

		// if (highest!=null){
		//  	closestNonBlockerTouched.fireTouchDown();
		//mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
		//	return true;
		//}

		//    if (result!=-1 && !hitsPenetrate){
		//    	Gdx.app.log(logstag,"_closest hit was_"+result);
		//       closestBlockerTouched.fireTouchDown();
		//       mousedownOn.add(closestBlockerTouched);

		//   	return true;
		//   }
		if (closestBlockerTouched!=null){
			return closestBlockerTouched;
		}
		return null;
	}

	public static void addHitable(hitable model) {
		hitables.add(model);

	}

	public static boolean removeHitable(hitable model) {

		return hitables.remove(model);



	}
	public static void addMoving(Moving model) {
		movingObjects.add(model);

	}

	public static void removeMoving(Moving model) 
	{

		movingObjects.remove(model);

	}
	public static void addAnimating(Animating model) {
		animatingobjects.add(model);

	}

	public static void removeAnimating(Animating model) 
	{
		//animatingobjects.removeValue(model,true);
		animatingobjects.remove(model);
	}

	/** fires the drag event on everything the mouse was down on.
	 * This event should be fired once after a mousedown then mouse movement 
	 * @return **/
	public static void fireDragStartOnAll()	 {

		if (!dragStarted) {

			Log.info("_-drag start on all :"+mousedownOn.size);
			for (hitable model : mousedownOn) {
				model.fireDragStart();	

			}
			dragStarted=true;
		}
	}

	public static void fireDragStartEnd() {
		dragStarted=false;
	}

	public static void untouchAll() { //should be renamed we no longer fire touchup

		//Gdx.app.log(logstag,"_-mousedownOn size to untouch:"+mousedownOn.size);

		//for (hitable model : mousedownOn) {
		//	model.fireTouchUp();		

		//}
		mousedownOn.clear();
		//Gdx.app.log(logstag,"_----------removing md:"+mousedownOn.size);
		//Boolean removedtest = mousedownOn.removeValue(model,true);

		//Gdx.app.log(logstag,"_-mousedownOn:"+mousedownOn.size);
	}

	public void dispose() {


		modelBatch.dispose();

	}


	public static void updateObjectMovementAndFrames(float deltatime){

		for (Animating instance : animatingobjects) {

			instance.updateAnimationFrame(deltatime);

		}

		for (Moving instance : movingObjects) {

			instance.updatePosition(deltatime);			

		}

		//temp (testing lookat by linking to camera
		//should point from camera updated each frame		
		//	MECamera.angleTest.lookAt(lookAtTester);


	}

	static HashSet<IsAnimatableModelInstance> autoHideList = new HashSet<IsAnimatableModelInstance>();
	//
	//TODO: do we also need to check for it being over any of their subwidgets as well?
	//
	/**
	 * Models set to auto-hide will have .hide() triggered on the next MouseDown event, if that event is not 
	 * over them
	 *
	 * @param modelinstance
	 * @param b
	 */
	public static void setModelToAutoHide(IsAnimatableModelInstance model, boolean b) {
		if (b){
			autoHideList.add(model);
		} else {
			autoHideList.remove(model);
		}
	}

	static HashSet<IsAnimatableModelInstance> modalObjectList = new HashSet<IsAnimatableModelInstance>();

	/**
	 * Models set to modal are the only ones that can have events run on them IF any models have been set as modal at all.
	 * Keep the modal list empty for normal behavior.
	 *
	 * @param modelinstance
	 * @param b
	 */
	public static void setModelasModalObject(IsAnimatableModelInstance model, boolean b) {
		if (b){
			modalObjectList.add(model);
		} else {
			modalObjectList.remove(model);
		}
	}

	/**
	 * changes the render order of a item to "overlay"
	 * (that is, puts it in the overlay list after taking it out of the normal list
	 * If using a animatablemodelinstance, use its own SetASOverlay function so it stays in sycn
	 * @param model
	 */
	public static void setAsOverlay(ModelInstance model){

		//only add to overlay if it was in standard anyway
		if (allStandardInstances.remove(model)) {
			allOverlayInstances.add(model);
		}


	}
	/**
	 * changes the render order of a item to "standard"
	 * (that is, puts it in the standard list after taking it out of the ovelay list
	 * 
	 * If using a animatablemodelinstance, use its own SetASStandard function so it stays in sycn
	 * @param model
	 * 
	 */
	public static void setAsStandard(ModelInstance model){

		//we only add it to standard if it was in overlay
		if (allOverlayInstances.remove(model)){
			allStandardInstances.add(model);
		}

	}

	public static void updateTouchState() {

		//new touch system
		//first we update the state if needed based on what it was before
		//and what it is now
		if (Gdx.input.isTouched()) {
			//if we are touched we look at state we were before
			switch (GWTishModelManagement.currentTouchState) {
			case NONE:			
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchDown; //if we wernt touching before then its a new touch!


				break;
			case NewTouchDown:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.TouchDown; //if we previously were a new touchdown, then now we are a non-new touchdown

				//currently duplicated elsewhere in MainExplorationView				
				Log.info(" new touch down");
				GWTishModelManagement.touchStartedAt = new Vector2(Gdx.input.getX(),Gdx.input.getY());



				break;
			case NewTouchUp:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchDown; //if we just touched up and then the very next frame touched down....then the user is suspiciously fast
				break;
			case TouchDown:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.TouchDown; //still a touchdown unless the mouse moved;


				Vector2 currentLoc = new Vector2(Gdx.input.getX(),Gdx.input.getY());
				if (GWTishModelManagement.touchStartedAt.dst2(currentLoc)>5){							
					GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewDrag;
				}				

				break;
			case NewDrag:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.Dragging; //newdrags become Dragging 
				break;				
			case Dragging:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.Dragging; //do nothing
				break;
			}

		} else {

			switch (GWTishModelManagement.currentTouchState) {
			case NONE:	
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NONE; //still none
				break;
			case NewTouchDown:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case NewTouchUp:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NONE; //now none
				break;
			case TouchDown:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case NewDrag:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case Dragging:
				GWTishModelManagement.currentTouchState = GWTishModelManagement.TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			}

		}

	}





}
