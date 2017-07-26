package com.lostagain.nl.GWTish.Management;

/**
 * Used so a object can specify how its interacted with 
 * @author Tom
 *
 */
public enum objectInteractionType {
	/** clicks and firings interact with this object (assuming its set to hitable) and we do not block whats under it from also getting the interactions**/
	Normal,
	/** interactions happen with this object and it blocks what's under it  **/
	Blocker,
	/** this object is interface only, and the gun wont fire at it **/
	Interface, 
	/** no interactions at all. No touch/mouse events will fire, however the object may still be in the hitables list if added**/
	None
	
}