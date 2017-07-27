package com.lostagain.nl.GWTish;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.ComplexPanel.Alignment;
import com.lostagain.nl.GWTish.ComplexPanel.HorizontalAlignment;
import com.lostagain.nl.GWTish.ComplexPanel.VerticalAlignment;

/**
 * A panel that formats its child widgets using the default html-like layout behavior.
 * 
 * Note; currently alignment not supported
 * @author darkflame
 *
 */
public class FlowPanel extends CellPanel {
	final static String logstag = "GWTish.FlowPanel";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag

	//current stats
	float currentTotalWidgetWidth   = 0f;
	float currentTotalHeight   = 0f;
	
	//VerticalAlignment DefaultAlignmentinCell = VerticalAlignment.Middle;

	float maxWidth = -1; //no max width, means it acts much like a horizontal panel with the widgets alligned to the top


	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public FlowPanel() {
		super(10,10); //default size and background
	}
	
	//maxWidth
	public FlowPanel(float maxWidth) {
		super(10,10); //default size and background
		this.maxWidth = maxWidth;

	}
	

	float widthOfCurrentRow     = 0;
	float maxHeightOfCurrentRow = 0;
	float rowsCurrentYHeight = 0;
	
	Vector3 getNextPosition(float incomingWidth,float incomingHeight,Widget widget){

		int index = contents.indexOf(widget);

		Alignment align = contentAlignments.get(widget);
		if (align == null) {
			align = new Alignment(HorizontalAlignment.Left,
					defaultVerticalAlignment);
			contentAlignments.put(widget, align);
		}

		float newLocationX = 0;	
		float newLocationY = rowsCurrentYHeight;
		
		float newTotalWidthOfCurrentRow = widthOfCurrentRow + incomingWidth + spaceing;    //currentTotalWidgetWidth+incomingWidth+spaceing;
	

		//was previous widget a NewLinePlacer?		
		boolean forceNewLine = false;
		if (index>0){
			Widget previous = contents.get(index-1);
			forceNewLine = previous instanceof NewLinePlacer;
		}

		
		//if we beat the current maxHeightOfCurrentRow for this row
		if (incomingHeight>maxHeightOfCurrentRow && !forceNewLine){ //dont update height if we are being put on newline. hmz...what if its not forced, but still a newline? would it be wrong
			maxHeightOfCurrentRow = incomingHeight;
		}
		
		
		
		
		
		//if theres no maxwidth, this also becomes the new total width
		if (maxWidth==-1 && !forceNewLine){
					
			newLocationX = widthOfCurrentRow;
			
			widthOfCurrentRow = 	newTotalWidthOfCurrentRow;	
					
		} else {	
			
			//else we work out if we need a new row or not	
			//NewLinePlacer will force newlines regardless of width
			if (newTotalWidthOfCurrentRow>=maxWidth || forceNewLine){ 
				
				Log.info("___________________________________Setting new row on flow panel (projected width was:"+newTotalWidthOfCurrentRow+")");
				
				//need new row
				rowsCurrentYHeight = rowsCurrentYHeight+maxHeightOfCurrentRow+spaceing;
				newLocationY = rowsCurrentYHeight;
				newLocationX = 0;

				Log.info("new position is:"+newLocationX+","+newLocationY);
				
				//new row width
				newTotalWidthOfCurrentRow = 0+incomingWidth+spaceing;
				widthOfCurrentRow = newTotalWidthOfCurrentRow;
				maxHeightOfCurrentRow = incomingHeight; //reset
				
			} else {
				//Don't need new row
				
				//x is current position in row
				newLocationX = widthOfCurrentRow;
				//y is unchanged (rowsCurrentYHeight)
				
				//update current row width
				widthOfCurrentRow = newLocationX + incomingWidth+spaceing;
			}
			
			
		}
		
		//if newwidth is greater then old we update
		if (newTotalWidthOfCurrentRow>this.currentTotalWidgetWidth){
			currentTotalWidgetWidth=newTotalWidthOfCurrentRow;
		}
		//update maxheight as well

		
		
		float maxH = maxHeightOfCurrentRow;//(largestHeightOfStoredWidgets);
		//ensure its at least min height
		//if (maxH<MinSizY){
		//	maxH=MinSizY;
		//}
		
		float bottomOfCurrentRow = rowsCurrentYHeight + incomingHeight;
		

		switch (align.vert) {
		case Bottom:
			//newLocationY = -(maxH - incomingHeight);
			newLocationY = -(bottomOfCurrentRow-incomingHeight);
			
			break;
		case Top:
			newLocationY = -(rowsCurrentYHeight);
			break;
			
		case Middle:
		default:
		//	newLocationY =  -(maxH - incomingHeight)/2; //center in panel
			//	newLocationY =  -(maxH - incomingHeight)/2; //center in panel
			newLocationY = -(bottomOfCurrentRow+rowsCurrentYHeight)/2.0f;
			break;
	

		}


		currentTotalHeight = rowsCurrentYHeight + maxHeightOfCurrentRow;
		
		Log.info("___________________________________maxHeightOfCurrentRow:"+maxHeightOfCurrentRow+")"+currentTotalHeight);
		
		/*

		if (DefaultAlignmentinCell == VerticalAlignment.Middle){


			newLocationY =  (maxH - incomingHeight)/2; //center in panel



		}*/

		//the following option shouldn't be needed I think
	//	if (updateWidth){
		//	currentTotalWidgetWidth=newTotalWidthOfCurrentRow;
	//	}
		
		

		return new Vector3(getLeftPadding()+newLocationX,-getTopPadding()+newLocationY,3f); //Measuring from topleft downwards hence inverting the padding


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
		
		widthOfCurrentRow =0;
		 maxHeightOfCurrentRow = 0;
		 rowsCurrentYHeight = 0;
		


		for (Widget widget : contents) {	

			//	super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add

		}
		Log.info("new size:"+currentTotalWidgetWidth+","+currentTotalHeight);
		//update back size
		//sizeToFitContents(); 

	}

	private VerticalAlignment defaultVerticalAlignment = VerticalAlignment.Top;

	/**
	 * Sets the default horizontal alignment to be used for widgets added to this
	 * panel. It only applies to widgets added after this property is set.
	 * 
	 */
	public void setHorizontalAlignment(VerticalAlignment align) {
		defaultVerticalAlignment = align;
	}


	@Override
	public void setWidth(float newWidth) {
		this.maxWidth= newWidth;
		super.setWidth(newWidth);
		sizeToFitContents();
	}

	//
	@Override
	void sizeToFitContents() {
		
		Log.info("size to fit contents new size:"+currentTotalWidgetWidth+","+currentTotalHeight);
		
		
		this.setSizeAs(getLeftPadding()+currentTotalWidgetWidth+getRightPadding(),
				     getBottomPadding()+currentTotalHeight+getTopPadding());
	}

	public String getWidgetDebugString() {
		String debug = "";
		for (Widget widget : contents) {
			
			int zindex=widget.getStyle().getZIndexValue();
	
			
			if (widget instanceof Label){
				
				Label lab = (Label)widget;
				String labtext=lab.getText();
	
				debug=debug+"[label:"+labtext+"]"; //+"("+zindex+")
			} else {
	
				debug=debug+"["+widget.getClass().getName()+"]";
			}
		}
		return debug;
	}

}
