package com.lostagain.nl.GWTish;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.lostagain.nl.GWTish.Event.EventType;

/**
 * currently just a widget that has a runnable that fires when clicked.
 * This will naturally change a lot, but having a basic button to test stuff is useful
 * 
 * TODO: Turn this class into a ToggleButton, and make a similar class for Button that doesnt swap two widgets
 * but rather just changes the colour of the background
 * 
 * @author Tom
 *
 */
public class Button extends DeckPanel {
	//	
	//protected Widget up; 
	//protected Widget down;


	final static String logstag = "GWTish.Button";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag


	Color DOWNCOLOR = Color.DARK_GRAY;
	Color UPCOLOR  = Color.LIGHT_GRAY;


	public Label Caption;

	public Label getCaption() {
		return Caption;
	}

	/**
	 * creates a button with specified size and runnable when clicked
	 * @param sizeX
	 * @param sizyY
	 * @param onClick
	 */
	public Button(float sizeX, float sizyY, ClickHandler onClick) {
		super(sizeX, sizyY);
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(UPCOLOR);

		//by default we have two panels of different greys
		//setupDefaultBackground(sizeX, sizyY);

		//no caption

		setZIndex(0, Widget.generateUniqueGroupID("Button"));

		super.showWidget(0);

		//---		
		if (onClick!=null){					
			addClickHandler(onClick);
		}
	}

	//these are overridden just to give feedback to pressing the button
	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
		super.getStyle().setBackgroundColor(DOWNCOLOR);
		//	super.showWidget(down);
		//	super.showWidget(Caption,false);


	}
	@Override
	public void fireTouchUp() {
		super.fireTouchUp();
		super.getStyle().setBackgroundColor(UPCOLOR);
		//	super.showWidget(up);
		//	super.showWidget(Caption,false);


	}
	/*
	private void setupDefaultBackground(float sizeX, float sizyY) {

		down = new Widget(sizeX,sizyY);	
		down.getStyle().setBackgroundColor(Color.DARK_GRAY);
		super.add(down);

		up   = new Widget(sizeX,sizyY);
		up.getStyle().setBackgroundColor(Color.LIGHT_GRAY);	
		super.add(up);
	}

	 */

	public Button(String caption) {
		this(caption, null);
	}
	/**
	 * 
	 * @param caption
	 * @param onClick
	 */
	public Button(String caption, ClickHandler onClick) {
		super(1, 1); //Arbitrary, we have to resize after the font is made

		//create the caption first as this determines the size
		Caption = new Label(caption);
		Caption.getStyle().clearBackgroundColor();
		Caption.getStyle().clearBorderColor();

		Caption.userData = "button_"+caption;
		
		
	//	float xsize = Caption.getWidth()+10;
	//	float ysize = Caption.getHeight()+10;

		//setSizeAs(xsize, ysize);

		//by default we have two panels of grey and dark grey as the background
		//setupDefaultBackground(xsize, ysize);

		//and the caption ontop
		super.add(Caption);

		//the caption should always be visible, but only one background
		//super.showWidget(Caption,true);
		//super.showWidget(up,false);



		//	setupCaption(caption);
		Log.info("_-(total widgets in button "+this.contents.size()+" )-_");


		//for testing
		//in future we need a way to get a unique name for the zIndex group
		//Also change the set function to set everything attached to Zindex +1 as a option
		setZIndex(0, Widget.generateUniqueGroupID("Button"));

		//Caption.setZIndex(2, "testbutton");


		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(UPCOLOR);

		if (onClick!=null){
			addClickHandler(onClick);
		}

	}
	/*
	private void setupCaption(String caption) {
		this.Caption = new Label(caption);
		Caption.getStyle().clearBackgroundColor();
		Caption.getStyle().clearBorderColor();
		this.setSizeAs(Caption.getWidth()+10, Caption.getHeight()+10);

		PosRotScale captionPosition = new PosRotScale(5f,5f,3f);

		this.attachThis(Caption, captionPosition);


	}
	 */



	public void click(){
		fireHandlersForType(Event.EventType.ClickEvent);
	}

	public void setText(String text) {
		if (Caption!=null){
			Caption.setText(text);
		} else {

			Caption = new Label(text);
			Caption.getStyle().clearBackgroundColor();
			Caption.getStyle().clearBorderColor();


			float xsize = Caption.getWidth()+10;
			float ysize = Caption.getHeight()+10;

			setSizeAs(xsize, ysize);

			//resize background
			//up.setSizeAs(xsize, ysize);
			//down.setSizeAs(xsize, ysize);

			//and the caption ontop
			super.add(Caption);

			setZIndex(0, Widget.generateUniqueGroupID("Button"));


		}

	}

	/*

	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
		super.getStyle().setBackgroundColor(Color.RED);
	}

	@Override
	public void fireTouchUp() {
		super.fireTouchDown();
		super.getStyle().setBackgroundColor(Color.GRAY);
		if (onClick!=null){
			onClick.run();
		}

	}*/
}
