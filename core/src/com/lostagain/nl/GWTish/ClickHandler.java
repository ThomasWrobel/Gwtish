package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Event.EventType;

//Handler interfaces and stuff (experimental)
//Really feeling my way here as I don't know what's needed
public abstract class ClickHandler extends EventHandler {
	
	/** 
	 * called when the element with this handler added is clicked on.
	 * Specifically the release of the click 
	 ***/
	public abstract void onClick();
	
	Event.EventType getType(){
		return Event.EventType.ClickEvent;
	}
	
	protected void fireHandler(){
		onClick();
	}
}