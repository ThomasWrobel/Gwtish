package com.lostagain.nl.GWTish;


public abstract class KeyUpHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onKeyUp();
	
	Event.EventType getType(){
		return Event.EventType.KeyUpEvent;
	}
	protected void fireHandler(){
		onKeyUp();
	}
}