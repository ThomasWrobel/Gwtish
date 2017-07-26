package com.lostagain.nl.GWTish;

class HandlerRegistration {
	Element handlersObject;
	EventHandler eventHandler;
	
	public HandlerRegistration(Element handlersObject,
			EventHandler eventHandler) {
		super();
		this.handlersObject = handlersObject;
		this.eventHandler = eventHandler;
	}

	public void removeHandler() {
		handlersObject.removeHandler(eventHandler);
		
	}
}