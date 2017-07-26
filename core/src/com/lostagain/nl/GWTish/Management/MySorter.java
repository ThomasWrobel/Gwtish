package com.lostagain.nl.GWTish.Management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.GWTish.Management.ZIndexAttribute;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;

public class MySorter extends DefaultRenderableSorter {
	public static Logger Log = Logger.getLogger("MySorter");

	/**
	 * used to snapshot all the objects being sorted
	 */
	boolean snapshotNextSort = false;
	Array<Renderable> preSortSnapshot;
	Array<Renderable> postSortSnapshot;

	private Comparator<? super Renderable> zindexSwapper = new Comparator<Renderable>() {

		@Override
		public int compare(Renderable o1, Renderable o2) {

			ZIndexAttribute o1zindex   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID));		
			if (o1zindex!=null){
				ZIndexAttribute o2zindex   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));		

				if (o2zindex!=null){
					//do their groups match?
					if (o1zindex.group == o2zindex.group){
						//then return based  on higher zindex
						return o1zindex.zIndex-o2zindex.zIndex;
					}

				}			
			}



			return 0;
		}
	};


	@Override
	public void sort(Camera usethiscamera, Array<Renderable> renderables) {
		if (snapshotNextSort){

			Gdx.app.log("zindex", "____snapping___");
			preSortSnapshot =  new Array<Renderable>(renderables);
		}
		camera=usethiscamera;
		
		//need to reset positions if camera moved
		ZIndexGroup.clearAllDrawOrderPositions(); //might be a better way to do this (that is, clear all the positions back to default for the next sort)
		
		//precalcute zindex
		//not used yet
		//in future I cam considering having the distances to the lowest zindex in each group pre-calculated
		//This makes the lowest widget the "canonical distance" rather then the first in the renderable list
		//This might make things more predictable in regards to draw order
		//ZIndexGroup.calculateAllZIndexGroupDistances(usethiscamera);
		
		
		super.sort(usethiscamera, renderables); //first normal sort
	//	customSorter(renderables); //then swap any z-indexs which are wrongly placed
	
		//customSorter(usethiscamera, renderables);

		if (snapshotNextSort){
			postSortSnapshot =  new Array<Renderable>(renderables);
			snapshotNextSort = false;
			logSnapshots();
		}
	}

	
	@Override
	public int compare (final Renderable o1, final Renderable o2) {
		
		final boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o1.material.get(BlendingAttribute.Type)).blended;
		final boolean b2 = o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o2.material.get(BlendingAttribute.Type)).blended;
		if (b1 != b2){ //if one of them isn't blended and the other one is
			
			return b1 ? 1 : -1; 
		}
		
		//Comment from libgdx;
		// FIXME implement better sorting algorithm
		// final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
		// o1.material.equals(o2.material);
		//-----
		
			
			// getCenterOfBoundingBox().mul(getMatrixTransform());
			
		
				o1.worldTransform.getTranslation(tmpV1);
				o2.worldTransform.getTranslation(tmpV2);
				
				
				//First we work out the distances to the camera (or, rather distance squared - we only need relative order so its a waste
				//to do the squareroute to get the real distance. Internally that dst2 compared is just doing pythagoras)
				
			//	float o1distance = (int)(1000f * camera.position.dst2(tmpV1)); 
			//	float o2distance = (int)(1000f * camera.position.dst2(tmpV2));
				
				float o1distance = (  camera.position.dst2(tmpV1)); //float is needed as int probably too small for squared distances
				float o2distance = ( camera.position.dst2(tmpV2));
				
				
				
		//now look for z-index groups
		ZIndexAttribute o1zindex   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID));
		ZIndexAttribute o2zindex   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));
		
		//if either has a z-index and a canonical distance we use that as its distance
		if (o1zindex!=null){
				
			
			
			//if the distance of this group hasn't been set we set it
			if (o1zindex.group.drawOrderDistance==-1){
				
				//get a more accurate distance based on the center of the bounding box
				o1.meshPart.update();
				tmpV1.set((o1.meshPart.center).mul(o1.worldTransform));			
			//	 o1distance = (int)(1000f * camera.position.dst2(tmpV1));
				 o1distance = (camera.position.dst2(tmpV1));
					
				
				o1zindex.group.drawOrderDistance = o1distance;
			} else {
				//else we use the distance from the group
				//(we ignore the "real" distance from this point on, the groups own order takes over)
				o1distance = o1zindex.group.drawOrderDistance;
			}
		}
		if (o2zindex!=null){
			
		
				//	o2.meshPart.update();
		//	tmpV2.sub(o2.meshPart.center);
		//	 o2distance = (int)(1000f * camera.position.dst2(tmpV2));
			
			//if the distance of this group hasn't been set we set it
			if (o2zindex.group.drawOrderDistance==-1){
				
				//get a more accurate distance based on the center of the bounding box
				o2.meshPart.update();
				tmpV2.set((o2.meshPart.center).mul(o2.worldTransform));			
				// o2distance = (int)(1000f * camera.position.dst2(tmpV2));
				 o2distance = (camera.position.dst2(tmpV2));
					 
				
				o2zindex.group.drawOrderDistance = o2distance;
			} else {
				//else we use the distance from the group
				//(we ignore the "real" distance from this point on, the groups own order takes over)
				o2distance = o2zindex.group.drawOrderDistance;
			}
		}
		
		//if, however, we are both z-indexes of the same group, we use the internal order instead of distance
		if (o1zindex!=null && o2zindex!=null && (o1zindex.group == o2zindex.group)){
			return (o1zindex.zIndex - o2zindex.zIndex);
		}
		
		final float dst = o1distance - o2distance;	
		
		//if, weirdly, the distances are equal but one isnt a zindex, we put the zindex ontop
		//the goal is to get zindexs together in the order list, we should leave no possibility's
		//for other objects inbetween zindexs of the same group		
		if (dst==0){
			
			if (o1zindex!=null && o2zindex==null){
				return -1;			
			}
			if (o2zindex!=null && o1zindex==null){
				return 1;			
			}
			//if they both don't have a group we don't change anything
			if (o1zindex==null && o2zindex==null){
				final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
				return b1 ? -result : result;
			}
			//if the distances are equal but the zindex groups are different, we put use the name of the group to determine what goes ontop
			if (o1zindex.group!=o2zindex.group){
				return o1zindex.group.group_id.compareTo(o2zindex.group.group_id);
			}
		}
		
		final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
		return b1 ? -result : result;
	}

	
	
	private void newCustomSorter(Array<Renderable> resultList) {
		
		//if neither are zindex then normal sort
		
		//if one is a zindex and it has a canonical distance already, we replace its distance with that one and sort as normal
		//if it does not have a canonic distance we sort as normal, but we also set the canonical distance
		
		//if both are zindex's in the same group then distances should match. Therefor we use their group position instead
		//(only do this after we confirm all similizindex stuff gets grouped)
		
		
	}

	private void customSorter(Array<Renderable> resultList) {
		Gdx.app.log("zindex", "____customSort trigger___");
		
		for (int i = 0; i < resultList.size; i++) {

			//swap with next based on compare
			Renderable o1 = resultList.get(i);
		
			//Everything should already be sorted except for the z-index adjustments
			//we thus dont do anything if o1 hasn't got a z-index
			ZIndexAttribute o1zindex   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID));
			
			if (o1zindex==null){
				
				continue;
				
			} else {				
				Gdx.app.log("zindex", "____o1zindex group:___"+o1zindex.group.group_id);
				
				//get the z-index details
				ZIndexGroup group = o1zindex.group;
				int positionWithinGroup = group.indexOf(o1zindex, true);
				
				//has this group been placed yet? 
				if (group.drawOrderPosition==-1){

					Gdx.app.log("zindex", "____position in group is:___"+positionWithinGroup);
					group.drawOrderPosition = i; //(the first found member of group determines the groups zero point
					
					//ensure draw order position is at least groupsize from end to allow room for the full group
					if (group.drawOrderPosition+group.size>resultList.size){
						Gdx.app.log("zindex", "____draw order position too high("+group.drawOrderPosition+")"+group.group_id);
						group.drawOrderPosition = resultList.size-group.size;
					}					
				
					//however, the first found member of the group isn't necessarily at this first position 
					//for that reason we add its position within the group to this value
					int targetPosition = positionWithinGroup + i;
					Gdx.app.log("zindex", "____draw order position set to:___"+group.drawOrderPosition);
					Gdx.app.log("zindex", "____first targetPosition it thus:___"+targetPosition);
					
					resultList.swap(i, targetPosition);

				} else {

					//if a position exists already we swap with it as they should all be together
					int groupPosition = group.drawOrderPosition;
					int targetPosition = positionWithinGroup + groupPosition;
					Gdx.app.log("zindex", "____targetPosition:___"+targetPosition);
					resultList.swap(i, targetPosition);
					
					
					//now get our position within the group
					//int positionWithinGroup = group.indexOf(o1zindex, true);
					
					/*
					//find first renderable after that position not in the group
					while ((targetPosition+1)< resultList.size) {

						targetPosition=targetPosition+1;
						Renderable o2 = resultList.get(targetPosition);
						ZIndexAttribute o2zindex   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));	
						
						if (o2zindex==null || o2zindex.group!=group){
							break;
						} 					

					}
					
					Gdx.app.log("zindex", "____targetPosition:___"+targetPosition);
					resultList.swap(i, targetPosition);
					 */

				}


			}


			//--test for zindex
			//ZIndexAttribute o1zindex   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID));		
			//if (o1zindex!=null){
			//	ZIndexAttribute o2zindex   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));		
			//}
			//compare

			//loop over remainder?
			//loop up insertion sorts?

			//	int compare = this.normalcompare(o1, o2);
			//	if (compare<0){
			//		resultList.swap(i, i+1);

			//	} else {
			//do nothing
			//}



		}

		ZIndexGroup.clearAllDrawOrderPositions(); //might be a better way to do this (that is, clear all the positions back to default for the next sort)


	}

	/**
	 * The goal of this sorter is to sort the renderables the same way LibGDX would do normally (in DefaultRenderableSorter)<br>
	 * except if they have a ZIndex Attribute.<br>
	 * A Zindex attribute provides a groupname string and a number.<br>
	 * Renderables with the attribute are placed next to others of the same group, with the order within the group determined by the number<br>
	 * 
	 * For example an array of renderables like;<br><br>
	 * 0."testgroup",20<br>
	 * 1."testgroup2",10<br>
	 * 2.(no zindex attribute)<br>
	 * 3."testgroup",50<br>
	 * <br>Should become;<br><br>
	 * 0."testgroup",20<br>
	 * 1."testgroup",50<br>
	 * 2.(no zindex attribute)<br>
	 * 3."testgroup2",10<br>
	 * <br> 
	 * assuming the object in testgroup2 is closer to the camera, the one without a index second closest, and the rest furthest<br>
	 * (It is assumed that things within the same group wont be drastically different distances)<br>
	 * 
	 * @param camera
	 * @param resultList
	 */
	private void customSorter_slow(Camera camera, Array<Renderable> resultList) {

		//make a copy of the list to sort. (This is probably a bad start)
		Array <Renderable> renderables = new Array <Renderable> (resultList);

		//we work by clearing and rebuilding the Renderables array (probably not a good method)
		resultList.clear();

		//loop over the copy we made
		for (Renderable o1 : renderables) {

			//depending of if the Renderable as a ZIndexAttribute or not, we sort it differently
			//if it has one we do the following....
			if (o1.material.has(ZIndexAttribute.ID)){

				//get the index and index group name of it.
				int           o1Index   =  ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).zIndex;
				ZIndexGroup o1GroupName =  ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).group;


				//setup some variables
				boolean placementFound = false; //Determines if a placement was found for this renderable (this happens if it comes across another with the same groupname)
				int defaultPosition = -1; //if it doesn't find another renderable with the same groupname, this will be its position in the list. Consider this the "natural" position based on distance from camera

				//start looping over all objects so far in the results (urg, told you this was probably not a good method)
				for (int i = 0; i < resultList.size; i++) {

					//first get the renderable and its ZIndexAttribute (null if none found)
					Renderable o2 = resultList.get(i);
					ZIndexAttribute o2szindex = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));

					if (o2szindex!=null){
						//if the renderable we are comparing too has a zindex, then we get its information
						int    o2index    = o2szindex.zIndex;
						ZIndexGroup o2groupname = o2szindex.group;		


						//if its in the same group as o1, then we start the processing of placing them next to eachother
						if (o2groupname == (o1GroupName)){

							Gdx.app.log("zindex", "groupnames match:"+(i+1));


							//we either place it in front or behind based on z-index
							if (o1Index<o2index){
								//if lower z-index then behind it
								resultList.insert(i, o1);
								placementFound = true;
								break;
							}

							if (o1Index>o2index){
								//if higher z-index then it should go in front UNLESS there is another of this group already there too
								//in which case we just continue (which will cause this to fire again on the next renderable in the inner loop)
								if (resultList.size>(i+1)){

									Renderable o3 = resultList.get(i+1);
									ZIndexAttribute o3szindex = ((ZIndexAttribute)o3.material.get(ZIndexAttribute.ID));

									if (o3szindex!=null){
										ZIndexGroup o3groupname = o3szindex.group;	

										if (o3groupname!=null && (o3groupname==o1GroupName)){
											//the next element is also a renderable with the same groupname, so we loop and test that one instead	
											continue;
										}
									}

								}

								//Gdx.app.log("zindex", "__..placeing at:"+(i+1));

								//else we place after the current one
								resultList.insert(i+1, o1);
								placementFound = true;
								break;
							}

						}

					}


					//if no matching groupname found we need to work out a default placement.
					int placement = normalcompare(o1, o2); //normal compare is the compare function in DefaultRenderableSorter. 

					if (placement>0){
						//after then we skip
						//(we are waiting till we are either under something or at the end

					} else {
						//if placement is before, then we remember this position as the default (but keep looking as there still might be matching groupname, which should take priority)											
						defaultPosition = i;
						//break; //break out the loop
					}


				}

				//if we have checked all the renderables positioned in the results list, and none were found with matching groupname
				//then we use the defaultposition to insert it
				if (!placementFound){
					//Gdx.app.log("zindex", "__no placement found using default which is:"+defaultPosition);
					if (defaultPosition>-1){
						resultList.insert(defaultPosition, o1);
					} else {
						resultList.add(o1);
					}

				}

				continue;

			}

			//...(breath out)...
			//ok NOW we do placement for things that have not got a ZIndexSpecified
			boolean placementFound = false;

			//again, loop over all the elements in results
			for (int i = 0; i < resultList.size; i++) {

				Renderable o2 = resultList.get(i);

				//if not we compare by default to place before/after
				int placement = normalcompare(o1, o2);

				if (placement>0){
					//after then we skip
					//(we are waiting till we are either under something or at the end)
					continue;
				} else {
					//before					
					resultList.insert(i, o1);
					placementFound = true;
					break; //break out the loop
				}


			}
			//if no placement found we go at the end by default
			if (!placementFound){
				resultList.add(o1);

			};


		} //go back to check the next element in the incomeing list of renderables (that is, the copy we made at the start)

		//done


	}


	//Copy of the default sorters compare function
	//;
	private Camera camera;
	private final Vector3 tmpV1 = new Vector3();
	private final Vector3 tmpV2 = new Vector3();

	public int normalcompare (final Renderable o1, final Renderable o2) {
		final boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o1.material.get(BlendingAttribute.Type)).blended;
		final boolean b2 = o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o2.material.get(BlendingAttribute.Type)).blended;
		if (b1 != b2) return b1 ? 1 : -1;
		// FIXME implement better sorting algorithm
		// final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
		// o1.material.equals(o2.material);
		o1.worldTransform.getTranslation(tmpV1);
		o2.worldTransform.getTranslation(tmpV2);
		final float dst = (int)(1000f * camera.position.dst2(tmpV1)) - (int)(1000f * camera.position.dst2(tmpV2));
		final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
		return b1 ? -result : result;
	}

	public void snapShotNextSort()
	{
		snapshotNextSort = true;

	}


	private void logSnapshots() {

		Log.info( "____(all "+preSortSnapshot.size+" objects)___");
		Log.info( "____(presort)___");
		debugtest(preSortSnapshot);
		Log.info("____(postsort)___");
		debugtest(postSortSnapshot);


	}

	public void testSort(){
		Array<Renderable> renderables = new Array<Renderable>();
		
		int numberOfTestObjects = 20;
		
		for (int i = 0; i < numberOfTestObjects; i++) {
			//create object
			Widget newWidget  = new Widget(11f,11f);
			
			//give it a random position
			float x = (float) (Math.random()*50);
			float y = (float) (Math.random()*50);
			float z = (float) (Math.random()*50);
			Vector3 newpos = new Vector3(x,y,z);
			newWidget.setToPosition(newpos);	
			
			//give it a material (random if z-index or not)
			int ranGroupNum = (int) (Math.random()*3);
			newWidget.getBackgroundMaterial().set(new ZIndexAttribute(i,"testgroup"+ranGroupNum));
			//(for the moment everything is the same position in the group, 20)
			
			//add its renderable to the renderable array
			Renderable nr   = new Renderable();
			newWidget.getRenderable(nr);
			nr.userData = "object "+i;
			
			renderables.add(nr);
			
		}

		/*
		Widget testWidget1  = new Widget(11f,11f);
		Widget testWidget2  = new Widget(11f,11f);
	//	ConceptObject testWidget2 = new ConceptObject(StaticSSSNodes.ability);   //new Widget(11f,11f);
		Widget testWidget3  = new Widget(11f,11f);
		Widget testWidget4  =  new Widget(11f,11f);
		Widget testWidget5  = new Widget(11f,11f);
		Widget testWidget6  = new Widget(11f,11f);

		//add to array
		ArrayList<Widget> testWidgets = new ArrayList<Widget>();
		testWidgets.add(testWidget1);
		testWidgets.add(testWidget2);
		testWidgets.add(testWidget3);
		testWidgets.add(testWidget4);
		testWidgets.add(testWidget5);
		testWidgets.add(testWidget6);		
		
		//give them random positions in z
		for (Widget cw : testWidgets) {
			float x = (float) (Math.random()*50);
			float y = (float) (Math.random()*50);
			float z = (float) (Math.random()*50);
			Vector3 newpos = new Vector3(x,y,z);
			cw.setToPosition(newpos);	
			
		}
		

		testWidget1.getMaterial().set(new ZIndexAttribute(20,"testgroupA"));
		testWidget2.getMaterial().set(new ZIndexAttribute(1,"testgroupA"));
		testWidget3.getMaterial().set(new ZIndexAttribute(1,"testgroupB"));
		//testWidget4
		testWidget5.getMaterial().set(new ZIndexAttribute(10,"testgroupB"));
		
		testWidget6.getMaterial().set(new ZIndexAttribute(25,"testgroupA"));
		
		Renderable one   = new Renderable();
		testWidget1.getRenderable(one);
		one.userData = "object one";

		Renderable two   = new Renderable();
		testWidget2.getRenderable(two);
		two.userData = "object two (c)";

		Renderable three = new Renderable();
		testWidget3.getRenderable(three);
		three.userData = "object three ";

		Renderable four = new Renderable();
		testWidget4.getRenderable(four);
		four.userData = "object four";

		Renderable five = new Renderable();
		testWidget5.getRenderable(five);
		five.userData = "object five";

		Renderable six = new Renderable();
		testWidget6.getRenderable(six);
		six.userData = "object six";
		
		Array<Renderable> renderables = new Array<Renderable>();
		renderables.add(four);
		renderables.add(one);
		renderables.add(two);
		renderables.add(three);
		renderables.add(five);
		renderables.add(six);
	
		
	//	Gdx.app.log("zindex", "_____________________________________________(groups are:)___");
		
	//	for (ZIndexGroup group : ZIndexGroup.AllZIndexGroups.values()) {
			

	//		Gdx.app.log("zindex", "_________group:"+group.group_id+" elements:"+group.size);
			
	//	}*/
		debugtest(renderables);

		Gdx.app.log("zindex", "_____________________________________________(sorting)___");
		//this.sort(MainExplorationView.camera,renderables);
	//	camera=MainExplorationView.camera;
		
		super.sort(camera, renderables); //first normal sort
	//	customSorter(renderables); //then swap any z-indexs which are wrongly placed

		Gdx.app.log("zindex", "______________________________________________(sort done)___");

		debugtest(renderables);

	}

	//In order to allow arbitrary render order overrides,
	//this function needs to check if o1 or o2 has a special material parameter?
	//That parameter will give a z-index value which compares to the other renderable's z-index value
	//If one of them doesn't have a value, its assumed to be zero
	//Then we use those values to compare them.

	private void debugtest(Array<Renderable> renderables) {
		//	Gdx.app.log("z-index", "____debugging object order___");

		for (Renderable renderable : renderables) {

			String name = (String) renderable.userData;

			if (renderable.material.has(ZIndexAttribute.ID)){

				int zindex1  = ((ZIndexAttribute)renderable.material.get(ZIndexAttribute.ID)).zIndex;
				ZIndexGroup group = ((ZIndexAttribute)renderable.material.get(ZIndexAttribute.ID)).group;
				Log.info("name="+name+ " (zindex = "+zindex1+" , "+group.group_id+" Group DISTANCE:"+group.drawOrderDistance+")");



			} else {

				Log.info("no zindex name="+name +" shader("+renderable.material.id+")");

			}


		}


	}



	/**
	 * Orders by drawdistance except when within the same ZIndex group.
	 * This is not ideal sorting for a few reasons, but I cant yet work out how to do it better.
	 * Problems are;
	 * 
	 * 1. Relays upon things in the same group already being next to eachother in the natural draw order
	 * 2. Rather inefficient if lots are in the same group, as the group IS ALREADY SORTED upon its creation, and yet here we are sorting it again.
	 * 
	 * See;
	 * http://stackoverflow.com/questions/32364279/optimal-render-draw-order-function-with-specified-z-index-values/32370227#comment52657109_32370227
	 * For a better way to do the sort, but sadly that proved quite hard to intergrate into the existing renderable rendering system

	@Override
	public int compare(Renderable o1, Renderable o2) {

		ZIndexAttribute o1zindex   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID));		
		if (o1zindex!=null){
			ZIndexAttribute o2zindex   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));		

			if (o2zindex!=null){
				//do their groups match?
				if (o1zindex.group == o2zindex.group){
					//then return based  on higher zindex
					return o1zindex.zIndex-o2zindex.zIndex;
				}

			}			
		}



		return super.compare( o1,  o2);

	}
	 */

	/*
	//If neither has a z-index, the normal compare function is used.
	@Override
	public int compare(Renderable o1, Renderable o2) {

		if (o1.material.has(ZIndexAttribute.ID)){

			int obj_zindex1   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).zIndex;
			String groupname1 = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).group;

			if (o2.material.has(ZIndexAttribute.ID)){

				int obj_zindex2   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;
				String groupname2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).group;

			//	Gdx.app.log("zindex", "obj_zindex1="+obj_zindex1+",obj_zindex2="+obj_zindex2);

				//int resultorder = (obj_zindex1 - obj_zindex2);

				//Gdx.app.log("zindex", "resultorder="+obj_zindex1+","+obj_zindex2);
				boolean oneIsGlobal=false;
				if (   groupname1.equalsIgnoreCase("global") ||    groupname2.equalsIgnoreCase("global") 		)
				{
					 oneIsGlobal=true;
				}


				if ( obj_zindex1==obj_zindex2  ){

					return super.compare(o1, o2);

				} else if (obj_zindex1<obj_zindex2 && (groupname1.equalsIgnoreCase(groupname2)  || oneIsGlobal)         ) {
				//	Gdx.app.log("zindex", "resultorder1="+obj_zindex1+","+obj_zindex2);
					return -1;
				} else if (obj_zindex1>obj_zindex2 && (groupname1.equalsIgnoreCase(groupname2)  || oneIsGlobal)         ) {
				//	Gdx.app.log("zindex", "resultorder2="+obj_zindex1+","+obj_zindex2);
					return 1;
				}


				//Gdx.app.log("zindex", "resultorder="+resultorder);

				//if they both have a z-index we just compare them directly
				//return resultorder;

			} else {
				//if 1 has and 2 doesn't we assume 2 is zero
				return (obj_zindex1 - 0); //super.compare(o1, o2);//
			}

		} else {			
			//if o1 does not have index, but o2 does; 
			if (o2.material.has(ZIndexAttribute.ID)){
				int obj_zindex2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;				

				return (0 - obj_zindex2); //super.compare(o1, o2);//
			}
		}



		//if nether has z index we use the normal compare
		return super.compare(o1, o2);
	}


	 */






}
