package com.lostagain.nl.GWTish.Management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement.RenderOrder;

/**
 * Handles the renderable list for widgets
 * 
 * @author Tom
 *
 */
public class ModelManagment {


	/** All the 3d models that should be rendered **/
	public static ObjectSet<ModelInstance> allInstances = new ObjectSet<ModelInstance>();
	
	
	
	/***
	 * Adds the model to the render list.
	 * If adding a AnimatableModelInstance we also check all its attachments are added too 
	 ****/
	public static void addModel(AnimatableModelInstance model) {	

		addModel((ModelInstance)model); //note we cast so as to call the non-AnimatableModelInstance specific method below

		for (IsAnimatableModelInstance attachedModel : model.getAttachments()) {	
			if (attachedModel.isInheriteingVisibility() && attachedModel.isVisible()){
				addModel((ModelInstance)attachedModel);
			}
		}

	}
	/*
 static	public class ZIndexGroup {

		public String groupName; 
	    public Array<Renderable> renderables = new Array<Renderable>();
	    
			public ZIndexGroup(String group, Renderable model) {
				this.groupName = group;
				this.renderables.add(model); //first time doesn't matter								
			}
			
			public void add(Renderable newInstance, int ZIndex){
				//we add it in the right place using the comparable in its zindex
				for (Renderable iterable_element : renderables) {
					
				}
				
			}
			
			public void getRenderableBefore(Renderable instance){
				//should return the renderable that is immediately behind this one in the renderable list
				//if its the first item in the list, return null
			}
	}
	//NOTE: using strings for the names now, this might change though
	public static HashMap<String,ZIndexGroup> zIndexGroups = new HashMap<String,ZIndexGroup>();
	
	*/
	
	/***
	 * Adds the model to the render list.
	 * If adding a AnimatableModelInstance we also check all its attachments are added too.
	 * 
	 * If the model has a zindex attribute we check if others have the same groupname, and if so add it to that set
	 * NOTE: The zindex system only supports one zindex attribute per model
	 **/
	public static void addModel(ModelInstance model) {			
		allInstances.add(model);		
	
		
				
	}
	
	
	public static boolean removeModel(ModelInstance model) {	
		
		boolean wasRemoved = allInstances.remove(model);
		
		//check if we need to update any zindex sets
		//check if any of the used materials has a zindex
	
		
		return wasRemoved;
	}


	
	
	
	
	
	
	

}
