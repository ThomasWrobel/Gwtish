package com.lostagain.nl.GWTish;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.lostagain.nl.GWTish.Event.EventType;

/**
 * currently just a widget that has a runnable that fires when clicked.
 * This will naturally change a lot, but having a basic button to test stuff is useful
 * 
 * TODO: Make this extend something closer too http://www.gwtproject.org/javadoc/latest/com/google/gwt/user/client/ui/CustomButton.html
 * which in turn extends deckpanel
 * We then have potential widgets for all states, not just up and down.
 * 
 * We might possibly also want a option for using modelinstances? This would allow true 3d buttons easily - as well as switches, leavers....hmmm
 * 
 * 
 * @author Tom
 *
 */
public class ToggleButton extends DeckPanel {
	//	
	final static String logstag = "GWTish.ToggleButton";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag

	public Widget up; //only public for testing	
	protected Widget down;

	public Label Caption;
	private boolean stateup=true;


	public ToggleButton(String upPic, String downPic, ClickHandler onClick) {
		super(1, 1);
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.GRAY);

		up   = new Image(upPic);
		down = new Image(downPic);

		super.add(up);
		super.add(down);

		super.showWidget(0);
		setZIndex(0, "ToggleButton");

		if (onClick!=null){					
			addClickHandler(onClick);
		}
	}

	/**
	 * creates a button with specified size and runnable when clicked
	 * @param sizeX
	 * @param sizyY
	 * @param onClick
	 */
	public ToggleButton(float sizeX, float sizyY, ClickHandler onClick) {
		super(sizeX, sizyY);
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.GRAY);

		setZIndex(0, Widget.generateUniqueGroupID("togglebutton"));
		//by default we have two panels of different greys
		setupDefaultBackground(sizeX, sizyY);

		//no caption


		super.showWidget(0);

		//---		
		if (onClick!=null){					
			addClickHandler(onClick);
		}
	}

	//these are overridden just to give feedback to pressing the button
	/*
	@Override
	public void fireTouchDown() {
		super.fireTouchDown();

		super.showWidget(down);
		super.showWidget(Caption,false);


	}
	@Override
	public void fireTouchUp() {
		super.fireTouchUp();

		super.showWidget(up);
		super.showWidget(Caption,false);


	}
	 */
	private void setupDefaultBackground(float sizeX, float sizyY) {

		down = new Widget(sizeX,sizyY);	
		down.getStyle().setBackgroundColor(Color.DARK_GRAY);
		super.add(down);

		up   = new Widget(sizeX,sizyY);
		up.getStyle().setBackgroundColor(Color.LIGHT_GRAY);	
		super.add(up);
	}



	public ToggleButton(String caption) {
		this(caption, null);
	}
	/**
	 * 
	 * @param caption
	 * @param onClick
	 */
	public ToggleButton(String caption, ClickHandler onClick) {
		super(1, 1); //Arbitrary, we have to resize after the font is made

		//create the caption first as this determines the size
		Caption = new Label(caption);
		Caption.getStyle().clearBackgroundColor();
		Caption.getStyle().clearBorderColor();

		float xsize = Caption.getWidth()+10;
		float ysize = Caption.getHeight()+10;

		setSizeAs(xsize, ysize);
		
		setZIndex(0, Widget.generateUniqueGroupID("togglebutton"));
		
		//by default we have two panels of grey and dark grey as the background
		setupDefaultBackground(xsize, ysize);

		//and the caption ontop
		super.add(Caption);
		//we want the caption higher then the background so we deliberately increasezindex more then the natural +1
		Caption.getStyle().setZIndex(getStyle().getZIndexValue()+5, getStyle().getZIndexGroup());
		
		
		//the caption should always be visible, but only one background
		super.showWidget(Caption);
		super.showWidget(up,false);
		
		//Caption.getStyle().setZIndex(getStyle().getZIndexValue()+20, getStyle().getZIndexGroup());
		
		

		//	setupCaption(caption);
		Log.info("total widgets in button "+this.contents.size()+" )-_");


		//for testing
		//in future we need a way to get a unique name for the zIndex group
		//Also change the set function to set everything attached to Zindex +1 as a option
		
		//Caption.setZIndex(2, "testbutton");


		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.YELLOW);

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
		fireClick();
	}

	@Override
	public void fireClick() {
		super.fireClick();
		//toggle state
		stateup = !stateup;
		Gdx.app.log(logstag,"_-(stateup is now "+stateup+" )-_");


		if (stateup){
			this.showWidget(0);
		} else {
			this.showWidget(1);	
		}

		//fire handler
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
			up.setSizeAs(xsize, ysize);
			down.setSizeAs(xsize, ysize);

			//and the caption ontop
			super.add(Caption);

			setZIndex(0, "testbutton");

		}

	}

	public boolean isDown() {
		return !stateup;
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
