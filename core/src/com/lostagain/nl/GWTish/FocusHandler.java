package com.lostagain.nl.GWTish;


public abstract class FocusHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onFocus();
	
	Event.EventType getType(){
		return Event.EventType.FocusEvent;
	}
	protected void fireHandler(){
		onFocus();
	}
}