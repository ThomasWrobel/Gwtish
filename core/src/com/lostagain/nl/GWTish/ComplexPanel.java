package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.GWTish.Widget.MODELALIGNMENT;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;

/**
 * Abstract base class for panels that can contain multiple child widgets.
 * (aprox emulation of gwts ComplexPanel)
 * 
 * @author darkflame
 *
 */
public abstract class ComplexPanel extends Widget {

	final static String logstag = "GWTish.ComplexPanel";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag

	
	
	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
	
	
	/**
	 * the currently largest width of any stored element (doesn't yet update when elements removed)
	 */
	protected float largestWidthOfStoredWidgets = 0f;
	/**
	 * the currently largest height of any stored element (doesn't update when elements removed)
	 */
	protected float largestHeightOfStoredWidgets = 0f;


	
	//Runnable updateContainerSize; 

	//--
		//The follow is used for widgets attached to it when we want to specify where they go
		//Note; These may be moved elsewhere as we introduce other classes for widgets-in-widgets
		public enum HorizontalAlignment {
			Left,Center,Right
		}
		
		public enum VerticalAlignment {
			Top,Middle,Bottom
		}
		
		
		/**
		 * A class that stores the widget with its horizontal and vertical alignments
		 * @author Tom
		 */
		static public class Alignment {		
			HorizontalAlignment horizontal = null;
			VerticalAlignment   vert = null;

			/**
			 * default middle center alignment
			 */
			public Alignment() {
				
				this.horizontal = HorizontalAlignment.Center;
				this.vert = VerticalAlignment.Middle;
				
			}			
			public Alignment(HorizontalAlignment horizontal,VerticalAlignment vert) {
				
				this.horizontal = horizontal;
				this.vert = vert;
				
			}			
						
		}
	
		/**
		 * the contents of this panel
		 */
		protected ArrayList<Widget> contents = new ArrayList<Widget>();
		
		
		/**
		 * if a widget has a specified alignment, it is stored here
		 */
		protected HashMap<Widget,Alignment> contentAlignments = new HashMap<Widget,Alignment>();
		
		
		
		
		
	//	private float topPadding    = 0f;
	//	private float bottomPadding = 0f;
	///	private float leftPadding   = 0f;
	//	private float rightPadding  = 0f;		
				
		public void setPadding (float padding){
			setTopPadding(padding);
			setBottomPadding(padding);
			setLeftPadding(padding);
			setRightPadding(padding);
		}
		
		
		float getRightPadding() {			
			return this.getStyle().PaddingRight;			
		}


		void setRightPadding(float rightPadding) {
			this.getStyle().setPaddingRight(rightPadding);
			//this.rightPadding = rightPadding;
		}


		float getLeftPadding() {
			return this.getStyle().PaddingLeft;
		}


		void setLeftPadding(float leftPadding) {
			this.getStyle().setPaddingLeft(leftPadding);
			//this.leftPadding = leftPadding;
		}


		float getBottomPadding() {
			return this.getStyle().PaddingBottom;
		}


		void setBottomPadding(float bottomPadding) {
		//	this.bottomPadding = bottomPadding;
			this.getStyle().setPaddingBottom(bottomPadding);
		}


		float getTopPadding() {
			return this.getStyle().PaddingTop;
		}

		void setTopPadding(float topPadding) {
			this.getStyle().setPaddingTop(topPadding);
		}




	//--------------------------------------------------------------------
	public ComplexPanel(float sizeX, float sizeY, MODELALIGNMENT align) {
		super(sizeX, sizeY, align);
		super.setZIndex(1, Widget.generateUniqueGroupID("ComplexPanel")); //everything gets a default zindex now, so children will be placed above
		
		this.MinSizX = sizeX;
		this.MinSizY = sizeY;
		/*
		//this will be given to child widgets to inform the parent of size changes
		updateContainerSize = new Runnable(){
			@Override
			public void run() {

				Log.info("updating position due to size change");
				boolean changed = recalculateLargestWidgets();

				Log.info("_________vis on panel:"+isVisible()+" ");
				repositionWidgets();
				Log.info("_________vis on panel2:"+isVisible()+" ");
			}			
		};*/

	}
	
	/**
	 * when a child widget resizes, this is fired to ensure the parent widgets
	 * size changes to keep it contained
	 */
	@Override
	protected void onChildResize(){

		Log.info("updating positions on "+getName()+" due to child size change");
		boolean changed = recalculateLargestWidgets();

		repositionWidgets();

		//update back size

		Log.info("updating size of "+getName()+" due to child size change");
		sizeToFitContents();
	}
	

	public ComplexPanel(float sizeX, float sizeY) {
		this(sizeX, sizeY,MODELALIGNMENT.TOPLEFT);
		
	}
	public ComplexPanel(Model object) {
		super(object);
		

		super.setZIndex(1, Widget.generateUniqueGroupID("ComplexPanel")); //everything gets a default zindex now, so children will be placed above
		
		
		//this will be given to child widgets to inform the parent of size changes
		/*
				updateContainerSize = new Runnable(){
					@Override
					public void run() {

						Log.info("updating position due to size change");
						boolean changed = recalculateLargestWidgets();
						
						repositionWidgets();
					}			
				};*/
		
	}
	
	

	public void clear() {
		clear(false);
	}
	
	public void clear(boolean disposeWidgets) {

		for (Widget widget : contents) {
			
			//Widget widget = widgetdata.widget;
			
			widget.hide(false); //dont change its local visibility setting
			
			//widget.removeOnSizeChangeHandler(updateContainerSize);	
			widget.setParent(null);
			
			this.removeAttachment(widget);
			if (disposeWidgets){
			widget.dispose(); //err...should we despose? what if its to be reused?
			}
		}
		contents.clear();
	}
	
	
	/**
	 * 
	 * @return true if there was a change AND its bigger then the minimum size of this widget
	 */
	protected boolean recalculateLargestWidgets() {

		boolean changed=false;

		Log.info("recalculateLargestWidgets");

		float oldLargestHeightOfStoredWidgets = largestHeightOfStoredWidgets;
		float oldlargestWidthOfStoredWidgets = largestWidthOfStoredWidgets;
		largestHeightOfStoredWidgets=0;
		largestWidthOfStoredWidgets=0;
		
		for (Widget widget : contents) {

			//Widget widget = widgetdata.widget;
			
			//get size of widget
			BoundingBox size = widget.getLocalBoundingBox();  //TODO; we will need to support percentage based sizes at some point, and this isnt goof enough for that. It needs instead to ask for the minimum acceptable size

			float scaleY = widget.transState.scale.y;
			float scaleX = widget.transState.scale.x;

			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;

			//multiply back UP by our own scale
			//This is because we need the native size of the widgets, not what we have scaled them too
			//By being attached to us
			//if we arnt attached yet, we dont do this (widgets in the process of being attached might call recalc before attachment to test if the size will change
			if (this.hasAttachment(widget)){
				width=width/super.transState.scale.x; 
				height=height/super.transState.scale.y;
			}
			
			
		//	Log.info("width of "+widget.getClass().getName()+" is "+scaleX+"*"+size.getWidth());
			//Log.info(" / "+super.transState.scale.x+" is "+width);
			
			if (width>largestWidthOfStoredWidgets){
				
				if (width>this.MinSizX){
					//note we only flag as changed if we are exceeding the minimum size
				//	changed=true;
				}
				
				//update largest
				largestWidthOfStoredWidgets=width;
								
			}
			if (height>largestHeightOfStoredWidgets){
				if (height>this.MinSizY){
					//changed=true;
				}
				largestHeightOfStoredWidgets=height;
				
			}

		}

		if (largestHeightOfStoredWidgets!=oldLargestHeightOfStoredWidgets){
			changed=true;
		}
		if (largestWidthOfStoredWidgets!=oldlargestWidthOfStoredWidgets){
			changed=true;
		}

		Log.info("largestWidthOfStoredWidgets:"+largestWidthOfStoredWidgets);
		Log.info("largestHeightOfStoredWidgets:"+largestHeightOfStoredWidgets);

		return changed;

	}

	


	
	
	@Override
	public void setSizeAs(float newWidth, float newHeight, boolean FireSizeChangeEvents) {
		super.setSizeAs(newWidth, newHeight, FireSizeChangeEvents);
		repositionWidgets();//reposition wdgets as they might depend on the total size, which has just changed
		
	}


	@Override
	public void setOpacity(float opacity) {		
		super.setOpacity(opacity);
		//repeat for our attached widgets
		for (Widget widget : contents) {
			
		//	Widget widget = widgetdata.widget;
			
			widget.setOpacity(opacity);			
		}

	}

	/**
	 * This function should size the widget to fit its contents.
	 * This normally is called after a reposition,add or remove and relays upon recalculateLargestWidgets being up to date.
	 * A horizontal panel might implement this by calling;
	 *
	 * this.setSizeAs(leftPadding+currentTotalWidgetWidth+rightPadding,
	 *	     bottomPadding+largestHeightOfStoredWidgets+topPadding);
	 *
	 * as that information will determine its size. A vertical panel will be similar but use the total height instead of width, and the largest widget instead of height
	 *		     
	 */
	abstract void sizeToFitContents();
	
	/**
	 * All subtypes must implement the ability to reposition all the widgets correctly.
	 * This will be called if a size of one of the widgets changes (for example, a new max size might
	 * expanded the overall widget size, and thus mean anything centrally aligned needs to be updated)
	 * internally it should remove all widgets and call internalAdd to ensure the methods stay sinked
	 */
	abstract void repositionWidgets();
	
	/**
	 * should also update width/height if needed
	 * @param width
	 * @param height
	 * @param widget
	 * @return
	 */
	abstract Vector3 getNextPosition(float width, float height,Widget widget); //, boolean b
	
	
	
	public boolean add(Widget widget) {
		
	
		
		return insert(widget,contents.size()); // removed+1
		
	}
	
	
	/**
	 * Adds a widget below the current ones.
	 * This class should be extended by subclasses in order to call setSizeAs(w,h) with the correct new total size afterwards)
	 * 
	 * @param widget
	 * @return true on success, false if not added (ie, was already there)
	 */
	public boolean insert(Widget widget,int beforeIndex) {
		
	    //if widget has a z-index, the child should be +1 automatically
		
		prepareWidgetToInsertsZIndex(widget);
		
		//------------
		int atIndex = beforeIndex; // removed -1
		
		if (contents.contains(widget)){
			Log.info("______________already contains widget");
			
			//do nothing as its already contained
			return false;			
		}
		
		//add to the widget list
		contents.add(atIndex,widget); //not scaled yet
		
		//recalculate biggest widgets (used for centralization vertical or horizontal depending on panel)
		boolean changed = recalculateLargestWidgets(); //needs to be scaled before running this
		
		if (changed || atIndex!=contents.size()){
			repositionWidgets(); //reposition all widgets with the new one	
			sizeToFitContents();
			return true;
		}
		
		//else we just add the new one
		internalAdd(widget); /// scales here
		
		//resize
		sizeToFitContents();
		
		return true;
	}

	

	protected void prepareWidgetToInsertsZIndex(Widget widget) {
		if (this.getStyle().hasZIndex()){
			
			int zindex = this.getStyle().getZIndexValue();
			ZIndexGroup groupname = this.getStyle().getZIndexGroup();
			//widget.getStyle().setZIndex(zindex+1, groupname);
			
			widget.setZIndex(zindex+1, groupname); // we should also deal with child widgets, each one +1 to the parent
			
			
			
		}
	}
	
	/**
	 * removes a widget from this panel and hides it.
	 * Note; The widget will still exist if you wish to unhide it or reattach it, it just wont be attached to this panel anymore
	 * 
	 * @param widget
	 * @return false is widget was not found
	 */
	public boolean remove(Widget widget) {
		
		boolean removedSuccessfully = contents.remove(widget);
		
		if (!removedSuccessfully){
			return false;
		}
		contentAlignments.remove(widget);
		
		widget.hide(false); //dont change its internal vis setting
		
		widget.setParent(null);
		
		this.removeAttachment(widget);
		boolean changed = recalculateLargestWidgets();
		
		//regenerate list
		repositionWidgets(); //we can optimize if we only reposition after the last one removed
		
		sizeToFitContents();
		
		return true;
	}

	public Widget getWidget(int index) {
		return contents.get(index);
	}
/**
 * 
 * @param  child - the widget to be found
 * @return the widget's index, or -1 if it is not a child of this panel
 */
	public int getWidgetIndex(Widget child) {
		 for (int i = 0; i < contents.size(); i++) {
			Widget array_element = contents.get(i);
			if (array_element==child){
				return i;
			}
			
		} 
		return -1;
	}
	
	/**
	 * Attaches the widget at the end of the current ones without resizing or adding to lists
	 * @param widget
	 */
	protected void internalAdd(Widget widget) {
		
		//get size of widget (unscaled)

		BoundingBox size = widget.getLocalBoundingBox();
		
		
		boolean isAttachedAlready = this.hasAttachment(widget);
		
		if (!isAttachedAlready) {
			
	
			float scaleY = widget.transState.scale.y;
			float scaleX = widget.transState.scale.x;
					
			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;
			
					
		//	Vector3 newLoc = getNextPosition(width,height,true,widget); //contents.indexOf(widget)
			Vector3 newLoc = getNextPosition(width,height,widget); //contents.indexOf(widget)
			
			
			float newLocationX = newLoc.x;
			float newLocationY = newLoc.y; 
			float newLocationZ = newLoc.z;
					
			//the above will return alignments based on top left, we need to correct for if the widgets pin is elsewhere			
			//NEW: Compensate for our pivots offset
			Vector2 offset  =  getOffsetForSize(this.getWidth(), this.getHeight(),alignment);

			//Log.info(" vp offsetFor "+alignment+" of "+getWidth()+","+getHeight()+" is "+offset);
			
			newLocationY = newLocationY +offset.y;
			newLocationX = newLocationX +offset.x;

		//	Log.info(" newLocation= "+newLocationX+","+newLocationY);
			//-------------
	
	
			
			PosRotScale newLocation = new PosRotScale(newLocationX,newLocationY,newLocationZ); 
			
			
			//set the scale of the newLocation to match the scale of the incoming object too (so its size is preserved
			newLocation.setToScaling(widget.transState.scale);
			
		//	Log.info("______________placing new "+widget.getClass()+" widget at: "+newLocationY+" its scaled size is:"+width+","+height);
		
			attachThis(widget, newLocation);
		
			//set widget to inherit visibility
			widget.setInheritedVisibility(true);		
			
			//tell the widget we are its parent
			widget.setParent(this);
			
		} else {
			//we take the existing displacement and only change the position.
			//This is in order to preserve the scale :)
			PosRotScale currentDisplacement  = this.getAttachmentsPoint(widget);
			
			//get new location (note the widget and height now use the scaling from their existing attachment
			float scaleY = currentDisplacement.scale.x;
			float scaleX = currentDisplacement.scale.y;
					
			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;
						
			Vector3 newLoc = getNextPosition(width,height,widget); //true
					
			
			float newLocationX = newLoc.x;
			float newLocationY = newLoc.y;
			float newLocationZ = newLoc.z;
			//the above will return alignments based on top left, we need to correct for if the widgets pin is elsewhere			
			//NEW: Compensate for our pivots offset
			Vector2 offset  =  getOffsetForSize(this.getWidth(), this.getHeight(),alignment);

			//Log.info(" vp offsetFor "+alignment+" of "+getWidth()+","+getHeight()+" is "+offset);
			
			newLocationY = newLocationY +offset.y;
			newLocationX = newLocationX +offset.x;

		//	Log.info(" newLocation= "+newLocationX+","+newLocationY);
			//-------------
	
	
			
			PosRotScale newLocation = new PosRotScale(newLocationX,newLocationY,newLocationZ); 
			
			currentDisplacement.setToPosition(newLocation.position);
			
			//just update position without the other gunk
			updateAtachment(widget, currentDisplacement);
			
			
		}
		
		
		
		
	}
	
	
	
	/**
	   * Sets the horizontal alignment of the given widget within its cell and triggers the widgets to be repositioned
	   * 
	   * @param w the widget whose horizontal alignment is to be set
	   * @param align the widget's horizontal alignment
	   */
	  public void setCellHorizontalAlignment(Widget widget, HorizontalAlignment align) {
		  
		  Alignment currentAlignment =contentAlignments.get(widget);
		  
		  if (currentAlignment==null){
			  currentAlignment = new Alignment(align, VerticalAlignment.Middle);
			  contentAlignments.put(widget, currentAlignment);	
			  return;
		  } else {
			  currentAlignment.horizontal = align;
		  }
		  
		  this.repositionWidgets();
		  	
	  }

	  /**
	   * Sets the vertical alignment of the given widget within its cell  and triggers the widgets to be repositioned
	   * 
	   * @param w the widget whose vertical alignment is to be set
	   * @param align the widget's vertical alignment
	   */
	  public void setCellVerticalAlignment(Widget widget, VerticalAlignment align) {
		  
		  Alignment currentAlignment = contentAlignments.get(widget);		  
		  if (currentAlignment==null){
			  currentAlignment = new Alignment(HorizontalAlignment.Center,align);
			  contentAlignments.put(widget, currentAlignment);	
			  return;
		  } else {
			  currentAlignment.vert = align;
		  }
		  
		  this.repositionWidgets();
	  }

	  
	/**
	 * sets both the vertical and horizontal alignment of a widget    and triggers the widgets to be repositioned
	 * @param widget
	 * @param contentAlignment
	 */
	public void setContentAlignments(Widget widget, Alignment contentAlignment) {		
		contentAlignments.put(widget, contentAlignment);	
		 this.repositionWidgets();
	}

	
	
	public ArrayList<Widget> getChildren() {
		return new ArrayList<Widget>(contents);
	}
	
	
	@Override
	/**
	 * sets the index on this and all child objects.
	 * child objects get index+1, unless we are in a DeckPanel subclass, in which case
	 * they should get index+n where n increases based on their position in the deck.
	 * 
	 * @param index  - higher valued objects go infront of lower objects
	 * @param group  - things in the same group get ordered next to eachother according to index value 
	 */
	public void setZIndex(int index, String group) {
		Log.info("_-(setZIndex on complexpanel to:"+index+","+group+")-_");
	
		//set zindex of back material
		super.setZIndex(index,group);
	
		//but we also need to apply it to all subobjects (only a little higher!)
		for (Widget childwidget : contents) {
			
			Log.info("_-(setZIndex on complexpanelchild to:"+(index+1)+","+group+")-_");

			childwidget.setZIndex(index+1,group); 
		}				
	}


	public int getWidgetCount() {
		
		return this.contents.size();
	}

}
