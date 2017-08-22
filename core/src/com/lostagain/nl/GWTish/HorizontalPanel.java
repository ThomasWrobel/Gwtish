package com.lostagain.nl.GWTish;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.ComplexPanel.Alignment;
import com.lostagain.nl.GWTish.ComplexPanel.HorizontalAlignment;
import com.lostagain.nl.GWTish.ComplexPanel.VerticalAlignment;


public class HorizontalPanel extends CellPanel {
	final static String logstag = "GWTish.HorizontalPanel";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag

	//current stats
	float currentTotalWidgetWidth   = 0f;

	VerticalAlignment DefaultAlignmentinCell = VerticalAlignment.Middle;



	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public HorizontalPanel() {
		super(10,10); //default size and background


	}

	Vector3 getNextPosition(float incomingWidth,float incomingHeight,Widget widget){ //,boolean updateWidth

		int index = contents.indexOf(widget);

		Alignment align = contentAlignments.get(widget);
		if (align == null) {
			align = new Alignment(HorizontalAlignment.Left,
					defaultVerticalAlignment);
			contentAlignments.put(widget, align);
		}

		float newLocationX = currentTotalWidgetWidth;		

		float newLocationY = 0;

		float maxH = (largestHeightOfStoredWidgets);
		//ensure its at least min height
		if (maxH<MinSizY){
			maxH=MinSizY;
		}

		switch (align.vert) {
		case Bottom:
			newLocationY = -(maxH - incomingHeight);
			break;
		case Middle:
			newLocationY =  -(maxH - incomingHeight)/2; //center in panel
			break;
		case Top:
			newLocationY = 0;
			break;
		default:
			newLocationY =  -(maxH - incomingHeight)/2; //center in panel
			break;

		}



		/*

		if (DefaultAlignmentinCell == VerticalAlignment.Middle){


			newLocationY =  (maxH - incomingHeight)/2; //center in panel



		}*/

		//the following option shouldnt be needed I think
	//	if (updateWidth){
			currentTotalWidgetWidth=currentTotalWidgetWidth+incomingWidth+spaceing;
	//	}

		float zdisplace = 0f; //3f
		return new Vector3(getLeftPadding()+newLocationX,-getTopPadding()+newLocationY,zdisplace); //messureing from topleft downwards hence inverting the padding


	}

	/**
	 * Refreshes the position of all widgets 
	 * 
		recalculateLargestWidgets(); should be run first
	 */
	void repositionWidgets() {
		Log.info("repositionWidgets in hp");
		//simply clear and re-add them all

		//reset  stats

		currentTotalWidgetWidth = 0f;
		//largestWidthOfStoredWidgets = 0f;
		//largestHeightOfStoredWidgets = 0f;


		for (Widget widget : contents) {	

			//	super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add

		}
		Log.info("new size:"+currentTotalWidgetWidth+","+largestHeightOfStoredWidgets);
		//update back size
		//sizeToFitContents(); 

	}

	private VerticalAlignment defaultVerticalAlignment = VerticalAlignment.Middle;

	/**
	 * Sets the default horizontal alignment to be used for widgets added to this
	 * panel. It only applies to widgets added after this property is set.
	 * 
	 */
	public void setHorizontalAlignment(VerticalAlignment align) {
		defaultVerticalAlignment = align;
	}


	//
	@Override
	void sizeToFitContents() {
		
		Log.info("size to fit contents new size:"+currentTotalWidgetWidth+","+largestHeightOfStoredWidgets);
		
		
		this.setSizeAs(getLeftPadding()+currentTotalWidgetWidth+getRightPadding(),
				getBottomPadding()+largestHeightOfStoredWidgets+getTopPadding());
	}

}
