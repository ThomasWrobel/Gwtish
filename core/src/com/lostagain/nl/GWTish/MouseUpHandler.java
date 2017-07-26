package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Event.EventType;

public abstract class MouseUpHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onMouseUp();
	
	Event.EventType getType(){
		return Event.EventType.MouseUpEvent;
	}
	protected void fireHandler(){
		onMouseUp();
	}
}