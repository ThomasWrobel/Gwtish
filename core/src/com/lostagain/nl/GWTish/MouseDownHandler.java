package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Event.EventType;

public abstract class MouseDownHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onMouseDown();
	
	Event.EventType getType(){
		return Event.EventType.MouseDownEvent;
	}
	
	protected void fireHandler(){
		onMouseDown();
	}
}