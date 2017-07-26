package com.lostagain.nl.GWTish;


public abstract class KeyDownHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onKeyDown();
	
	Event.EventType getType(){
		return Event.EventType.KeyDownEvent;
	}
	protected void fireHandler(){
		onKeyDown();
	}
}