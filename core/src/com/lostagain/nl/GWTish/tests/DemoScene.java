package com.lostagain.nl.GWTish.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.ComplexPanel.VerticalAlignment;
import com.lostagain.nl.GWTish.ClickHandler;
import com.lostagain.nl.GWTish.Gwtish;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Image;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement;
import com.lostagain.nl.GWTish.Style.Unit;

/**
 * makes a demo scene to show how  Labels, widgets for layour, and interaction works. 
 * 
 * @author darkflame
 *
 */
public class DemoScene {

//	static Model wallmodel;
//	static ModelInstance wallInstance;
	public static FirstPersonCameraController fpscontrols;
	

	/**
	 * fires Gwtish.assets.load(..) for all 3d models used in this scene.
	 * doneLoading() should be fired after that loading is done and the assets ready to use.
	 */
	public static  void setup()
	{
	
		setup(Gwtish.getAssetManager()); //default to Gwtish.java 's asset manager
	}
	
	static  AssetManager assetsmanager;
	
	/**
	 * fires Gwtish.assets.load(..) for all 3d models used in this scene.
	 * doneLoading() should be fired after that loading is done and the assets ready to use.
	 *
	 * @param assetsmanager - supply your own asset manager
	 */
	public static  void setup( AssetManager assetsmanager){

		DemoScene.assetsmanager=assetsmanager;
		//set all "background" models loading

		//Walls etc;
		assetsmanager.load("walls.g3db", Model.class);
		assetsmanager.load("floor.g3db", Model.class);
		assetsmanager.load("roof_reflect.g3db", Model.class);
		assetsmanager.load("roof.g3db", Model.class);

		//Edge:
		assetsmanager.load("flooredge.g3db", Model.class);
		//Desk	
		assetsmanager.load("table_centered.g3db", Model.class);
		//Computer:	
		assetsmanager.load("computer.g3db", Model.class);
		//paper:	
		assetsmanager.load("paper.g3db", Model.class);


		assetsmanager.load("cal.g3db", Model.class);
	}
	/*
	 * ideas:
	 * Newspaper head changes when clicked to "man clicks headline!" "doesnt understand media"
	 * Maybe small article "Small text not lorem ipsum shock!"
	 * 
	 * what on computer? whiteboard?
	 * Computer probably should demo css-ish animated text effect somewhere.
	 */
	
	/**
	 * sets up the demo scenes visuals and interactions.
	 * 
	 */
	public static void doneLoading() {

		//Non gwtish models/scanary setup:
		setupBackgroundModels();	
		//(displays 3d model files loaded by asset manager)


		//    
		//________________create newspaper text
		VerticalPanel newspaperFrame = getNewsPaper();

		//position it
		newspaperFrame.setToPosition(new Vector3(12, 90, -65));
		newspaperFrame.setToRotation(new Quaternion(Vector3.X, -90).mul(new Quaternion(Vector3.Z, -104)));

		GWTishModelManagement.addmodel(newspaperFrame);	

		//________________create calander date
		VerticalPanel calframe = getCal();
		//position it
		calframe.setToPosition(new Vector3(5f, 97, -95));
		calframe.setToRotation(new Quaternion(Vector3.Y, -90).mul(new Quaternion(Vector3.X, -30)));

		GWTishModelManagement.addmodel(calframe);	

		//computer screen
		//?
		//whiteboard
		//?

		//now we add simple interactions:
		//
		//Newspaper click
		newspaperFrame.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				// move cam
				activecam.position.set(-80f, 160f, -75f); //focus on newspaper
				activecam.lookAt(12, 90, -65);
			}
		});

		//calframe click
		calframe.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				// move cam
				activecam.position.set(-25f, 120f, -95f); //focus on calender
				activecam.lookAt(5f, 97, -95);
			}
		});

	}



	private static void setupBackgroundModels() {
		//Note:Beware of models and draw order, large models will often need to be broken up or their pivots adjusted if the sorting algorthm is justing using pivot distances
		//

		Model floor = assetsmanager.get("floor.g3db", Model.class);
		ModelInstance floorInstance = new ModelInstance(floor); 
		BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		blendingAttribute.opacity=0.8f;
		floorInstance.materials.get(0).set(blendingAttribute);
		GWTishModelManagement.addmodel(floorInstance);		

		Model wall = assetsmanager.get("walls.g3db", Model.class);
		ModelInstance wallInstance = new ModelInstance(wall); 
		GWTishModelManagement.addmodel(wallInstance);	

		Model flooredge = assetsmanager.get("flooredge.g3db", Model.class);
		ModelInstance flooredgeInstance = new ModelInstance(flooredge); 
		GWTishModelManagement.addmodel(flooredgeInstance);		
		//		
		Model roof = assetsmanager.get("roof.g3db", Model.class);
		ModelInstance roofInstance = new ModelInstance(roof); 
		GWTishModelManagement.addmodel(roofInstance);	
		Model roof_reflect = assetsmanager.get("roof_reflect.g3db", Model.class);
		ModelInstance roof_reflectInstance = new ModelInstance(roof_reflect); 
		GWTishModelManagement.addmodel(roof_reflectInstance);	
		//
		Model table = assetsmanager.get("table_centered.g3db", Model.class);
		AnimatableModelInstance tableInstance = new AnimatableModelInstance(table); 
		tableInstance.setToPosition(new Vector3(-23.609f, 85.806f, -74.658f));
		GWTishModelManagement.addmodel(tableInstance);	
		

		Model cal = assetsmanager.get("cal.g3db", Model.class);
		ModelInstance calInstance = new ModelInstance(cal); 
		GWTishModelManagement.addmodel(calInstance);	

		Model paper = assetsmanager.get("paper.g3db", Model.class);
		ModelInstance paperInstance = new ModelInstance(paper); 
		GWTishModelManagement.addmodel(paperInstance);	

		Model computer = assetsmanager.get("computer.g3db", Model.class);
		ModelInstance computerInstance = new ModelInstance(computer); 
		GWTishModelManagement.addmodel(computerInstance);
	}

	/**
	 * creates the text for a desk calander 
	 * @return
	 */
	private static VerticalPanel getCal() {

		Label DayLab = new Label("17th");
		Label MonthLab = new Label("Oct");

		DayLab.getStyle().setFontSize(4, Unit.PX);
		MonthLab.getStyle().setFontSize(2.5, Unit.PX);	
		DayLab.getStyle().setColor(Color.RED);

		DayLab.getStyle().setShadowX(0.20f);
		DayLab.getStyle().setShadowY(0.20f);
		MonthLab.getStyle().setShadowColor(new Color(0, 0, 0, 0.5f));

		VerticalPanel calfram =  new VerticalPanel();


		calfram.add(DayLab,MonthLab);


		return  calfram;
	}
	/**
	 * creates the text for newspaper
	 * @return
	 */
	private static VerticalPanel getNewsPaper() {

		VerticalPanel overallFrame = new VerticalPanel();

		HorizontalPanel dateandprice = new HorizontalPanel();


		Label price = new Label("$1.49");
		Label spacer = new Label(" ~~ ");		
		Label date = new Label("16th Oct");

		Label headline = new Label("Headline~");
		Label subheadline = new Label("Subheading");
		HorizontalPanel horizontalFrame = new HorizontalPanel();
		Label randomtext1 = new Label("gfhfgfhffghgfhfghfhfhfghgfhfghfhfhfghgfhfghfhfhfghgfhfghfhfhfhfhfghgfhfghfhfhfghgfhfghgfhfghfhfhfghgfhfghfhffhfhfghgfhfghfhfhfghgfhfghfhfhfgh",14);

		VerticalPanel rightPanelFrame = new VerticalPanel();
		Image npimage =new Image ("badlogic.jpg");
		npimage.setSizeAs(12, 12);

		Label randomtext2 = new Label("jkhjkhjkhkjkhkjkhjkhjkhkjkhkjkhjkhjkhkjkhkjkhjkhjkhkjkhkhjkhjkhkjkhk",14);



		//layout
		dateandprice.add(price,spacer,date);
		//		dateandprice.setMinWidth(250);
		//dateandprice.setCellHorizontalAlignment(price, HorizontalAlignment.Left); //Sadly hor alignment is not supported yet
		//dateandprice.setCellHorizontalAlignment(date, HorizontalAlignment.Right);

		horizontalFrame.setCellVerticalAlignment(rightPanelFrame, VerticalAlignment.Top);

		dateandprice.setSpaceing(2f);

		overallFrame.add(dateandprice,headline,subheadline,horizontalFrame);
		horizontalFrame.add(randomtext1,rightPanelFrame);
		//	horizontalFrame.setSpaceing(7f);
		rightPanelFrame.add(npimage,randomtext2);
		horizontalFrame.setPadding(1f);

		//style
		//overallFrame.getStyle().setBackgroundColor(Color.WHITE);

		price.getStyle().setColor(Color.BLACK);
		spacer.getStyle().setColor(Color.BLACK);
		date.getStyle().setColor(Color.BLACK);

		headline.getStyle().setColor(Color.BLACK);
		subheadline.getStyle().setColor(Color.BLACK);
		randomtext1.getStyle().setColor(Color.BLACK);
		randomtext2.getStyle().setColor(Color.BLACK);
		headline.getStyle().setShadowX(0.6f);
		headline.getStyle().setShadowY(0.6f);

		subheadline.getStyle().setShadowX(0.10f);
		subheadline.getStyle().setShadowY(0.10f);
		subheadline.getStyle().setShadowColor(new Color(0, 0, 0, 0.5f));


		date.getStyle().setStyleToMatch(subheadline.getStyle(), false, false);
		price.getStyle().setStyleToMatch(subheadline.getStyle(), false, false);
		randomtext1.getStyle().setStyleToMatch(subheadline.getStyle(), false, false);
		randomtext2.getStyle().setStyleToMatch(subheadline.getStyle(), false, false);
		//randomtext1.getStyle().setStyleToMatch(subheadline.getStyle(), false, false);

		//size
		price.getStyle().setFontSize(3, Unit.PX);
		spacer.getStyle().setFontSize(3, Unit.PX);		
		date.getStyle().setFontSize(3, Unit.PX);

		headline.getStyle().setFontSize(6, Unit.PX);
		subheadline.getStyle().setFontSize(4, Unit.PX);
		randomtext1.getStyle().setFontSize(2, Unit.PX);
		randomtext2.getStyle().setFontSize(2, Unit.PX);


		//




		return overallFrame;
	}
	
	static Camera activecam;
	
	/**
	 * 
	 * @param cam - supply your scene cam, it will be setup for fps controlls as long as you run fpscontrols.update() in your render() method
	 */
	static public void setupForDemoScene(Camera cam) {
		activecam=cam;
		//Camera cam = Gwtish.cam;
		//cam = new PerspectiveCamera(67,w,h);
	
		cam.position.set(-150f, 150f, 75f); //overhead
		cam.lookAt(0,150,0);
		cam.near = 0.5f;
		cam.far = 1000f;
		cam.update();
	
		Gwtish.Log.info("camera setup ");
		
		//camController = new CameraInputController(cam);
		 fpscontrols = new FirstPersonCameraController(cam);
		 fpscontrols.setVelocity(60f);
		Gdx.input.setInputProcessor(fpscontrols);
	
		
		Gwtish.Log.info("camera controller setup ");
		
		//GWTish has its own model manager that must be setup
		//This is required for distance field fonts to render correctly.
		GWTishModelManagement.setup(); 
	
		Gwtish.Log.info("GWTishModelManagement setup  ");
		
	}

}
