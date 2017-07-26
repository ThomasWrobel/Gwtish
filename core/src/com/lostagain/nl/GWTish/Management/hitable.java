package com.lostagain.nl.GWTish.Management;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.GWTish.PosRotScale;

public interface hitable {
	
	
	//public Vector3 getCenterOfBoundingBox();
	//public int getRadius(); //hitradius
	public PosRotScale getTransform();
	public void fireTouchDown();
	public void fireTouchUp();
	/** fired after the touchup event provided the object was previously toucheddown on */
	public void fireClick();
	
	public void fireDragStart();
	
	public void setLastHitsRange(float range); //set the distance squared from the shot origin to this object 
	public float getLastHitsRange(); //returns the above
	
	public objectInteractionType getInteractionType(); //if this blocks hits below it
	public Vector3 rayHits(Ray ray);
	public Vector3 getCenterOnStage();
	
	/**
	 * should this hitable be considered ontop of all others? (used for interface elements)
	 * @return
	 */
	public boolean isOverlay();
	
	/**
	 * to help debugging you can optionally supply a name for this hitable
	 * @return
	 */
	public String getName();
	
	
}