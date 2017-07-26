package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;

public class ToggleButton_old extends DeckPanel {

	boolean isDown = false;
	private Runnable onClick;
	
	/**
	 * Use two widgets as the up and down states.
	 * NOTE: any hit detection on these widgets will be removed
	 * NOTE2: Currently this button wont be very neat as its based on deckpanel which spaces things verticaly a bit 
	 * This is so many widgets can be seen at once over eachother if needed. Investigation into clipping needs to be done
	 * to have them on the same or close level (shader?)
	 * 
	 * @param Up
	 * @param Down
	 * @param onClick
	 */
	public ToggleButton_old(Widget Up,Widget Down, Runnable onClick){
		super(10,10); //start arbitrarily small, things added will enlarge it as needed
		
		//remove hit detection from widgets
		Up.setAsHitable(false);
		Down.setAsHitable(false);
		
		this.add(Up);
		this.add(Down);
		
		this.showWidget(0, true);
		this.setAsHitable(true);
		
		this.onClick=onClick;
		
		
	}

	@Override
	public void fireTouchUp() {
		super.fireTouchUp();
		
		isDown = !isDown; //invert state

		Gdx.app.log(logstag,"--isDown--"+isDown);
		//show the correct image
		if (isDown){
			//show the down widget
			showWidget(1, true);
			//fire the down runnable
		
		} else {
			//show the up widget
			showWidget(0, true);
		}
		
		onClick.run();
	}

	@Override
	public void fireTouchDown() {

		Gdx.app.log(logstag,"--fireTouchDown--");
		super.fireTouchDown();
	}

	public boolean getValue() {
		return isDown;
	}
	
	
	
	
	
}
