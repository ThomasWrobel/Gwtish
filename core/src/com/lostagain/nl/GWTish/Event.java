/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

/**
 * A GWTish event is represented by this class<br>
 * This for the moment, represents some mouse and touch actions.<br>
 * This class uses the same static numereicial representions of a few things as GWT does, but other then that does not sure
 * any functionality. It merely in a very broud sense repesents the same thing. <br>
 * <br>
 * The only real use of this class should be in the manager, where it creates a event to pass to objects to react too.<br>
 * 
 */
public class Event extends NativeEvent {
	
	private static String logstag="ME.NativeEvent";
	
	static Event currentEvent;


	private  Vector3 EventsCurrentLocation;
	
	
	//NOTE: this might move to the superclass? Not sure where it fits better
	/**
	 * The current 3d location this event just fired at
	 * @return (not a copy)
	 */
	  public Vector3 getEventsCurrentLocation() {
		return EventsCurrentLocation;
	}




	public static void setCurrentEvent(Event currentEvent) {;
				
		Event.currentEvent = currentEvent;
	}

	




	//event types
	public enum EventType {
		ClickEvent,
		MouseDownEvent,
		MouseUpEvent,
		BlurEvent,
		FocusEvent,
		KeyUpEvent,
		KeyDownEvent;
	}

	

	//This might be replaced with a more detailed system that mirrors GWTs way of identifying the type of the event
	EventType thisEventsType;



	

	/**
	   * Unlike GWT, we can create events directly
	   * (this class is not very similar)
	 * @param buttonTypeDown 
	   */
	  public Event(EventButtonType buttonTypeDown, boolean altKeyWasPressed, boolean cntrKeyWasPressed, boolean shiftKeyWasPressed, int xpos,
			int ypos, int currentEventKeyCode) {
		  
		super();
		
		ButtonTypeDown=buttonTypeDown;
		//we also keep a record of the last one down if its set to none
		//This lets us check the buttons that were just pressed when something has been released.
		if (ButtonTypeDown!=EventButtonType.None)
		{
			LastButtonTypeThatWasDown = ButtonTypeDown;
		}
		
		AltKeyWasPressed = altKeyWasPressed;
		CntrKeyWasPressed = cntrKeyWasPressed;
		ShiftKeyWasPressed = shiftKeyWasPressed;
		CurrentEventX = xpos;
		CurrentEventY = ypos;
		CurrentEventKeyCode = currentEventKeyCode;
		
		//TODO: work out how to deal with representing the type of the event as a number?
		//Using the bit-fields below and the button down fields in the superclass
		//	
		
	}

//NOTE: most of the below isnt used, at least not yet
  /**
   * Fired when an element loses keyboard focus.
   */
  public static final int ONBLUR = 0x01000;

  /**
   * Fired when the value of an input element changes.
   */
  public static final int ONCHANGE = 0x00400;

  /**
   * Fired when the user clicks on an element.
   */
  public static final int ONCLICK = 0x00001;

  /**
   * Fired when the user double-clicks on an element.
   */
  public static final int ONDBLCLICK = 0x00002;

  /**
   * Fired when an image encounters an error.
   */
  public static final int ONERROR = 0x10000;

  /**
   * Fired when an element receives keyboard focus.
   */
  public static final int ONFOCUS = 0x00800;

  /**
   * Fired when the user gesture changes.
   */
  public static final int ONGESTURECHANGE = 0x2000000;

  /**
   * Fired when the user gesture ends.
   */
  public static final int ONGESTUREEND = 0x4000000;

  /**
   * Fired when the user gesture starts.
   */
  public static final int ONGESTURESTART = 0x1000000;

  /**
   * Fired when the user depresses a key.
   */
  public static final int ONKEYDOWN = 0x00080;

  /**
   * Fired when the a character is generated from a keypress (either directly or
   * through auto-repeat).
   */
  public static final int ONKEYPRESS = 0x00100;

  /**
   * Fired when the user releases a key.
   */
  public static final int ONKEYUP = 0x00200;

  /**
   * Fired when an element (normally an IMG) finishes loading.
   */
  public static final int ONLOAD = 0x08000;

  /**
   * Fired when an element that has mouse capture loses it.
   */
  public static final int ONLOSECAPTURE = 0x02000;

  /**
   * Fired when the user depresses a mouse button over an element.
   */
  public static final int ONMOUSEDOWN = 0x00004;

  /**
   * Fired when the mouse is moved within an element's area.
   */
  public static final int ONMOUSEMOVE = 0x00040;

  /**
   * Fired when the mouse is moved out of an element's area.
   */
  public static final int ONMOUSEOUT = 0x00020;

  /**
   * Fired when the mouse is moved into an element's area.
   */
  public static final int ONMOUSEOVER = 0x00010;

  /**
   * Fired when the user releases a mouse button over an element.
   */
  public static final int ONMOUSEUP = 0x00008;

  /**
   * Fired when the user scrolls the mouse wheel over an element.
   */
  public static final int ONMOUSEWHEEL = 0x20000;

  /**
   * Fired when the user pastes text into an input element.
   */
  public static final int ONPASTE = 0x80000;

  /**
   * Fired when a scrollable element's scroll offset changes.
   */
  public static final int ONSCROLL = 0x04000;

  /**
   * Fired when the user cancels touching an element.
   */
  public static final int ONTOUCHCANCEL = 0x800000;

  /**
   * Fired when the user ends touching an element.
   */
  public static final int ONTOUCHEND = 0x400000;

  /**
   * Fired when the user moves while touching an element.
   */
  public static final int ONTOUCHMOVE = 0x200000;

  /**
   * Fired when the user starts touching an element.
   */
  public static final int ONTOUCHSTART = 0x100000;
  /**
   * Fired when the user requests an element's context menu (usually by
   * right-clicking).
   */
  public static final int ONCONTEXTMENU = 0x40000;

  /**
   * A bit-mask covering both focus events (focus and blur).
   */
  public static final int FOCUSEVENTS = ONFOCUS | ONBLUR;

  /**
   * A bit-mask covering all keyboard events (down, up, and press).
   */
  public static final int KEYEVENTS = ONKEYDOWN | ONKEYPRESS | ONKEYUP;

  /**
   * A bit-mask covering all mouse events (down, up, move, over, and out), but
   * not click, dblclick, or wheel events.
   */
  public static final int MOUSEEVENTS = ONMOUSEDOWN | ONMOUSEUP | ONMOUSEMOVE
      | ONMOUSEOVER | ONMOUSEOUT;

  /**
   * A bit-mask covering all touch events (start, move, end, cancel).
   */
  public static final int TOUCHEVENTS = ONTOUCHSTART | ONTOUCHMOVE | ONTOUCHEND | ONTOUCHCANCEL;

  /**
   * A bit-mask covering all gesture events (start, change, end).
   */
  public static final int GESTUREEVENTS = ONGESTURESTART | ONGESTURECHANGE | ONGESTUREEND;

  /**
   * Value returned by accessors when the actual integer value is undefined. In
   * Development Mode, most accessors assert that the requested attribute is
   * reliable across all supported browsers.
   *
   * @see Event
   */
  @Deprecated
  public static final int UNDEFINED = 0;




  /**
   * Gets the current event that is being fired. The current event is only
   * available within the lifetime of the event function. Once the
   *  method returns, the current event is reset to null.
   * 
   * @return the current event
   */
  public static Event getCurrentEvent() {
    return currentEvent;
  }


  

/**
 * Sets the 3d scene position that this event is triggered
 * This should only be called from Gwtishs Model Management class as part of its event fireing
 * @param atThis
 */
public void setCurrentEventLocation(Vector3 atThis) {
	EventsCurrentLocation = atThis;	
}





}
