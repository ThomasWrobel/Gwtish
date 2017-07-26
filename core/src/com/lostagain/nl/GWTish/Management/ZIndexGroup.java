package com.lostagain.nl.GWTish.Management;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;

/**
 * Everything in the same ZIndexGroup is considered equal distance from the camera
 * Things, instead, use the internal position in the group to determain what gets drawn ontop
 * 
 * @author darkflame
 *
 */
public class ZIndexGroup extends Array<ZIndexAttribute> {
	
	public String group_id = ""; //the identifier for the group, currently a string
	
	//not used in future;
	public int drawOrderPosition  = -1; //the position of the earliest element in the draw order. -1 for unordered.
	
	//new method;
	
	public float drawOrderDistance  = -1; //the canonical distance of this group. -1 for not known yet. 
	
	public static HashMap<String,ZIndexGroup> AllZIndexGroups = new HashMap<String,ZIndexGroup>();

	
	/**
	 * Returns a existing zIndexGroup is one exists with this name, else makes a new one.
	 */
	public static ZIndexGroup getZIndexGroup(String groupname){
		
		ZIndexGroup group = AllZIndexGroups.get(groupname);
		
		if (group!=null){
			return group;
		} else {
			return new ZIndexGroup(groupname);
		}
		
	}
	
	private ZIndexGroup(String groupname) {
		super(true,5); //capacity of 5 by default
		group_id = groupname;
		 AllZIndexGroups.put(groupname, this);
		 
	}

	@Override
	public void add(ZIndexAttribute newZindex) {
		//validate it should be part of this group
		if (newZindex.group!=this){
			Gdx.app.log("ZIndexGroup", "Attempted to add zindex to a different group then it belongs");
			return;
		}
		
		
		 for (int i = 0; i < this.size; i++) {
			 ZIndexAttribute zin = this.get(i);
			 
			 if (newZindex.zIndex<zin.zIndex){
				 super.insert(i, newZindex);
				 return;
			 }
			 
			 
		 }
		
		//if we arnt less then anything we add at the end
		 super.add(newZindex);
		 
	}

	@Override
	public boolean removeValue(ZIndexAttribute value, boolean identity) {
		return super.removeValue(value, identity);
	}
	
	
	
	private AnimatableModelInstance getLowestModelZIndexInGroup() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Calculates the distances from all of the zindex groups to the specified camera
	 * The distance is calculated by;
	 * [center point of lowest item in zindex] to camera squared
	 * Note; the result is left squared, as we only need comparative values and its a waste to route it
	 */
	public static void calculateAllZIndexGroupDistances(Camera camera){
		
		for (ZIndexGroup group : AllZIndexGroups.values()) {
						
			AnimatableModelInstance ani = group.getLowestModelZIndexInGroup();
			
				
		}
		
		
	}


	public static void clearAllDrawOrderPositions(){
		for (ZIndexGroup group : AllZIndexGroups.values()) {
		//	group.drawOrderPosition = -1;
			group.drawOrderDistance = -1;
			
		}
	}

	
	
}