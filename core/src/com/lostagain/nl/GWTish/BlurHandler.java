package com.lostagain.nl.GWTish;


public abstract class BlurHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onBlur();
	
	Event.EventType getType(){
		return Event.EventType.BlurEvent;
	}
	protected void fireHandler(){
		onBlur();
	}
}