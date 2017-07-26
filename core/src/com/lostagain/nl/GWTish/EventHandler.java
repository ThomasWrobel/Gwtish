package com.lostagain.nl.GWTish;


abstract class EventHandler {
	abstract Event.EventType getType(); 
	protected abstract void fireHandler(); 
}