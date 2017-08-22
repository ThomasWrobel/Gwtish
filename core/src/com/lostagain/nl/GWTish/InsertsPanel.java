package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;

/**
 * Crudely emulates some aspects of gwts htmlpanel widget
 * 
 * A htmlpanel is a panel that contains text, and which can attach child widgets to identified elements within that text.
 * As we have no real browser engine, dont expect real html support.<br>
 * However, positioning elements by ID will be done the same.<br>
 *  {@literal <div id="(elementIDToInsertObjectAt)"></div>} <br>
 * <br>
 * This class is based of a extension of FlowPanel. You can consider it a flowpanel with Label objects being thte automatic default contents<br>
 * <br>
 * Note;<br>
 * 		getElement().setInnerHTML(newText);		<br>
 * will not work. Use:<br>
 * <br>
 * setInnerText<br>
 * addInnerText<br>
 * <br>
 * instead
 * //TODO: resizing this panel wont work correctly yet. Widgets will reflow, but labels wont re-wrap, and certainly not recombine when once they were split (which would be ideal)
//In future we need to at least ensure all labels have correct width set. Even with this though, newlines might be set needlessly early

 * @author darkflame
 *
 */		
//TODO: resizing this panel wont work correctly yet. Widgets will reflow, but labels wont re-wrap, and certainly not recombine when once they were split (which would be ideal)
//In future we need to at least ensure all labels have correct width set. Even with this though, newlines might be set needlessly early
//TODO: remove pointless namedpoint copying &or text-parseing from some places
//TODO: changing styles wont effect sub-labels atm
//TODO: we might need a option to split all labels by their newlines if this widget might resize
//this should only be ondemand though, else there might be a lot of wasted objects
public class InsertsPanel extends FlowPanel {
	public static Logger Log = Logger.getLogger("Gwtish.InsertsPanel"); 
	Label currentActiveLabel;
	private boolean splitnextnl=false;
	
	//[[oldlab][randomobject][oldlab][currentlabel...]]

	boolean debugMode = true;

	public boolean isDebugMode() {
		return debugMode;
	}





	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}





	public InsertsPanel() {
		super();
		currentActiveLabel=setupLabel("",null);
		super.add(currentActiveLabel);
	}





	public InsertsPanel(float maxWidth) {
		super(maxWidth);

		currentActiveLabel=setupLabel("",null);
		super.add(currentActiveLabel);
	}

	
	public InsertsPanel(String contents) {
		super();
		currentActiveLabel=setupLabel(contents,null);
		super.add(currentActiveLabel);
	}





	public InsertsPanel(String contents, float maxWidth) {
		super(maxWidth);

		currentActiveLabel=setupLabel(contents,null);
		super.add(currentActiveLabel);
	}





	protected Label getDefaultLabelType(String contents){
		
		//make default label
		Label label = new Label(contents);
		
		//now set style stuff
		label.setInterpretBRasNewLine(interpretBRasNewLine); //as this sort of emulates html
		label.setInterpretBackslashNasNewLine(interpretBackslashNasNewLine);
		
		label.setMaxWidth(super.maxWidth); //ensures never wider then this container. However, inserts may still make textlabels go outside the boundary right now

		
		//copy the text styles from the parent flowpanel? (ie, ourselves)
		label.getStyle().setStyleToMatch(getStyle(),false,true);
		//but we dont want any padding, background or border stuff
		label.getStyle().clearBackgroundColor();
		label.getStyle().clearBackgroundImage();
		label.getStyle().clearBorderColor();
		label.getStyle().setPadding(0);
		//
		Log.info("created label:"+label.getText()+" padding:"+label.getStyle().getPadding());
		
		
		//label.getStyle().setColor(Color.RED);
		//(except for debugging)
		if (debugMode){
			label.getStyle().setBorderColor(Color.WHITE);
		}
		return label;
	}
	
	
	/**
	 * all the known labels insert points
	 */
	HashMap<Label,NamedPointSet> allKnowenInsertPoints = new HashMap<Label,NamedPointSet>(); 
	
	
	/**
	 * @author darkflame
	 *
	 */
	static class NamedPointSet extends HashSet<NamedPoint> {

		public int getPointForID(String iDstring) {
			
			for (NamedPoint point : this) {
				if (point.ID.equalsIgnoreCase(iDstring)){
					return point.index;
				}
			}
			
			return -1;
		}

		public NamedPointSet getPointsBefore(int insertstart) {
			
			NamedPointSet newset = new NamedPointSet();
			
			for (NamedPoint point : this) {
				
				if (point.index<=insertstart){
					newset.add(new NamedPoint(point.index, point.ID));
					}
				
			}
			
			return newset;
		}		
		
		/**
		 * subtracts start from points loc
		 * @param insertstart
		 * @return
		 */
		public NamedPointSet getPointsAfter(int insertstart) {
			
			NamedPointSet newset = new NamedPointSet();
			
			for (NamedPoint point : this) {
				
				if (point.index>insertstart){
					newset.add(new NamedPoint(point.index - insertstart, point.ID));
					
					
				}
				
			}
			
			return newset;
		}	
	}

	/**
	 * a named point on a label
	 * @author darkflame
	 *
	 */
	static class NamedPoint {
		int index;
		String ID;
		public NamedPoint(int index, String iD) {
			super();
			this.index = index;
			ID = iD;
		}		
	}
	
	
	 Label setupLabel(String contents,Label existing) {
		 return this.setupLabel(contents, existing, false);
	 }
	 
	//private
	 Label setupLabel(String contents,Label existing,boolean addToExisting) {
		 
		 //normalize newlines to \n
		 if (interpretBackslashNasNewLine){
		 contents=Label.standardiseNewlinesStatic(contents);
		 }
		 
		//
		//first we pre-process to remember any insert points
		//	detect insert points by  <div style=\"display:inline-block\" ID=\"Item_\"></div> or <div ID=\"Item_\"></div>
		//we also should strip of that and any other html.
		//We do this all now rather then later, as once labels are wrapped or split detecting the div tags will be much harder
		//
		 
		int fromIndex=0;
		int nextdivstart = contents.toLowerCase().indexOf("<div", fromIndex);
		NamedPointSet inserts = new NamedPointSet(); 
		
		if (nextdivstart!=-1){
		do {
			int nextdivend = contents.toLowerCase().indexOf("</div>", nextdivstart+4) + "</div>".length();
			
			String before_contents = contents.substring(0,nextdivstart);
			String div_element = contents.substring(nextdivstart, nextdivend);
			
			Log.info("div_element:"+div_element);
			
			String insertPointID = getInsertID(div_element);
			if (insertPointID!=null){

				Log.info("insertPointID:"+insertPointID);
				Log.info("insertPointIndex:"+nextdivstart);
				
				//make named point
				NamedPoint insertpoint = new NamedPoint(nextdivstart,insertPointID);
				inserts.add(insertpoint);
				
			}
					
		//	Log.info("nextdivend:"+nextdivend);
			//Log.info("contents:"+contents.length());
			
			String after_contents = contents.substring(nextdivend); //+"</div>".length()
			
			contents = before_contents+after_contents; //strips div bit out
			
			fromIndex = nextdivstart; //because till divend was stripped, the previous startdiv is where we start looking for the next 
			
			nextdivstart = contents.toLowerCase().indexOf("<div", fromIndex); //get next one and loop
		} while (nextdivstart!=-1); //(if there wasa next div)
		}
		
		Log.info("stripped text:"+contents);
		
				

		
		 if (existing==null){
			 //if none supplied use default label type
			 existing = getDefaultLabelType(contents);
		 } else {
			 //else set what was given
			 if (addToExisting){
				 existing.addText(contents);
			 } else {
				 existing.setText(contents);
			 }
			
		 }
		 
			//now store the insert points as named points (if there was any)
		 if (inserts.size()>0){
			allKnowenInsertPoints.put(existing, inserts);
		 }
		
		return existing;
	}
	 

	 @Override
	public void dispose() {
		super.dispose();
		allKnowenInsertPoints.clear();
		currentActiveLabel.dispose();
		currentActiveLabel=null;
		allKnowenInsertPoints=null;
	}


	static String insrt_prefix1 = "<div style=\"display:inline-block\" id=\"";
	 static String insrt_prefix2 = "<div id=\"";
	 
	 private String getInsertID(String div_element) {
		  div_element = div_element.toLowerCase();
		 //currently	detect insert points by  <div style=\"display:inline-block\" ID=\"Item_\"></div>
		 //or 
		 //<div ID=\"Item_\"></div>
		 // <div id="partlineinsert"></div>
		  
			
		 if (div_element.startsWith(insrt_prefix1)){
				 
			 //ID is straight after prefix, before quote end
			 int prefix_length = insrt_prefix1.length();
			 int  quote_end = div_element.indexOf("\"",prefix_length);			 
			 String ID = div_element.substring(prefix_length, quote_end);
			return ID;
		 }
		 
		
	 	if (div_element.startsWith(insrt_prefix2)){
	 		
	 		 int  quote_end = div_element.indexOf("\"",insrt_prefix2.length());				
			 //ID is straight after prefix, before quote end
	 		 int prefix_length = insrt_prefix2.length();
	 		 String ID = div_element.substring(prefix_length, quote_end);
	 		return ID;
			 
		}
	 
	 
		return null;
	}





	////needs to support auto-wrap newlines
	public void addInnerText(String addThisText){

		boolean countbr = currentActiveLabel.isInterpretBRasNewLine();		
		boolean hasNewlines = Label.containsNewlines(addThisText,countbr);
				
		//if we dont have to split, or theres no newlines we can just add the text
		if (!splitnextnl || !hasNewlines){			
			Log.info("adding text to existing");
			//currentActiveLabel.addText(addThisText);
			this.setupLabel(addThisText, currentActiveLabel, true);
			return;
		}
		//
		//else we need to split and add two labels and a newline between
		//this takes a lot more work;
		//.......
		//...
		//.

		Log.info("adding text to existing, splitting by newline");
		int next_nl = addThisText.indexOf("\n");
		String newlinetype="\n";
		if (countbr){
			int lastnlbr = addThisText.indexOf("<br>");
			if ((lastnlbr<next_nl) && (lastnlbr!=-1)){
				next_nl=lastnlbr;
				newlinetype="<br>";
			}
		}
		//------------------------------
		

		String before = addThisText.substring(0, next_nl);
		String after = addThisText.substring(next_nl+newlinetype.length());

		if (!before.isEmpty()){ 						//we dont make empty labels
			Label beforelab = setupLabel(before,null);			
			prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(beforelab); //inserts it before
		};
		
		NewLinePlacer newline = new NewLinePlacer(debugMode); 
		prepareWidgetToInsertsZIndex(newline); 
		contents.add(newline);
		
		if (!after.isEmpty()){ 						//we dont make empty labels
			Label afterlab = setupLabel(after,null);			
			prepareWidgetToInsertsZIndex(afterlab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(afterlab); //inserts it before
			
		};

		//setup the current active label to point to new end label
		ensureCurrentActiveLabelSet();
		
		//now we recalculate and re-draw to put contents in correct order
		//(normally done in super.insert or super.add automatically, but as we are manually changing the contents we need to do it ourselves)
		recalculateLargestWidgets(); 		
		repositionWidgets(); 
		sizeToFitContents();
		//
		splitnextnl=false;
		
		
	}

	public void setInnerText(String setThisText){
		super.clear(true);
		currentActiveLabel=setupLabel(setThisText,null);
		super.add(currentActiveLabel);
		//this.setupLabel(setThisText, currentActiveLabel);
	//	currentActiveLabel.setText(addThisText);

	}
	
	
	public String getAllKnowenInsertPoints(){
		String debuglog ="";
		for (Entry<Label, NamedPointSet> set : allKnowenInsertPoints.entrySet()) {
			
			debuglog=debuglog+"\n"+set.getKey().contents+"\n";
			
			for (NamedPoint namedPoint : set.getValue()) {
				debuglog=debuglog+namedPoint.ID+":"+namedPoint.index+"\n";				
			} 
			
		}
		
		return debuglog;
	}

	/**
	 * Inserts a widget at a location specified by a (fake) div
	 * 
	 * @param newwidget
	 * @param element_id  - will look for   {@literal <div id="(element_id)"></div>} to insert newwidget at <br>
	 */
	//Note: This method manipulates the "contents" array directly, then tells the widget to refresh
	//While easier, using super.Insert(..) directly to change the widgets will needlessly fire 
	//recalculateLargestWidgets,repositionWidgets and sizeToFitContents each time - when they only need to fire once after all the 
	//contents array manipulation is done		
	public void add(Widget newwidget,String element_id){
		Log.info("\n \n inserting at="+element_id);
		
		
		//allKnowenInsertPoints

		String IDstring = "<div id=\""+element_id+"\"></div>"; //id to look in string for. The Label will be split either side of this into two new labels with newwidget inbetween
		IDstring="";
		
		ArrayList<Widget> arraycopy= new ArrayList<>(super.contents);

		for (Widget widget : arraycopy) {

			if (widget instanceof Label){

				Label lab = (Label)widget;
				String labtext=lab.getText();

				Log.info("testing label:"+labtext);
				
				NamedPointSet namedpoints = allKnowenInsertPoints.get(lab);
				if (namedpoints==null){ //no points
					Log.info("(no insertpoints)");
					continue;
				}
				
				int insertstart=namedpoints.getPointForID(element_id);
				Log.info("insertstart="+insertstart);
				
				
				//int insertstart = labtext.indexOf(IDstring);

				if (insertstart>-1){

					Log.info("testing label: match found within:"+labtext);
					String before = labtext.substring(0, insertstart);
					String after = labtext.substring(insertstart+IDstring.length());
					
					
					Log.info(before+"[widget]"+after);


					int index=contents.indexOf(lab)+1; //location of current label to replace

					Log.info("[index]"+index);
					Label afterlab = null;

					if (!after.isEmpty()){ 						//we dont make empty labels
						afterlab = setupLabel(after,null);			
						prepareWidgetToInsertsZIndex(afterlab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
						contents.add(index,afterlab); //inserts it before
					}

					prepareWidgetToInsertsZIndex(newwidget); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
					contents.add(index,newwidget); //inserts it before
					
					Label beforelab = null;
					if (!before.isEmpty()){						//we dont make empty labels

						beforelab = setupLabel(before,null);	
						prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
						contents.add(index,beforelab); //inserts it before
						splitbylastnewline(beforelab);	//see below
					}
					//
					//need to get beforer/after insertpoints too, to copy into new labels
					//
					splitInsertPointsBetween(lab, insertstart, insertstart,afterlab, beforelab);
					
					
					//now we remove the old label we split
					allKnowenInsertPoints.remove(lab);
					contents.remove(lab);
					contentAlignments.remove(lab);		
					lab.hide(false); //dont change its internal vis setting		
					lab.setParent(null);		
					this.removeAttachment(lab);
					lab.dispose();
					///----------------------


					Log.info("[count:]"+getWidgetCount());

					//split after as well
					//a) use splitbynextnewline if theres already a widget after
					//b) flag to use it next time if not
					if (afterlab!=null){
						splitbynextnewline(afterlab);
						splitnextnl =false;
					} else {
						splitnextnl =true;
					}

					//TODO: we also need to split by whenever the previous newline was, this is too ensure the text is devided squarely
					//
					//[[oldlab.........\n
					//.................\n
					//.................\n
					//......][divid]
					//
					//convert too:
					//[[oldlab.........\n
					//.................\n
					//.................]
					//[lab2.][divid]
					//
					//And split next text too!
					//
				}

			}

		}


		Log.info("debug:\n"+getWidgetDebugString());

		//setup the current active label to point to new end label
		ensureCurrentActiveLabelSet();


		//now we recalculate and re-draw to put contents in correct order
		//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
		recalculateLargestWidgets(); 		
		repositionWidgets(); 
		sizeToFitContents();
		//


	}





	private void splitInsertPointsBetween(Label sourcelab, int splitpoints, int splitpointe, Label afterlab, Label beforelab) {
		NamedPointSet sourcepoints = this.allKnowenInsertPoints.get(sourcelab);
		if (sourcepoints!=null){
			NamedPointSet beforepoints = sourcepoints.getPointsBefore(splitpoints);
			NamedPointSet afterpoints = sourcepoints.getPointsAfter(splitpointe);
			if (afterlab!=null){	
				allKnowenInsertPoints.put(afterlab, afterpoints);
			}
			if (beforelab!=null){	
				allKnowenInsertPoints.put(beforelab, beforepoints);
			}
		}
	}





	/**
	 * repositioning of widgets must be run after this
	 */
	private void ensureCurrentActiveLabelSet() {
		Widget lastwidget = contents.get(contents.size()-1);

		//currentActiveLabel should always be the last label, ensuring there is one first
		if (lastwidget instanceof Label){			
			currentActiveLabel=(Label) lastwidget;
		} else {
			Label newcurrentlabel = this.setupLabel("",null);
			prepareWidgetToInsertsZIndex(newcurrentlabel); 
			currentActiveLabel=newcurrentlabel;
			contents.add(newcurrentlabel);
		}
	}




	private void splitbylastnewline(Label lab) {
		String labtext=lab.getText().toLowerCase();

		//boolean countbr = lab.isInterpretBRasNewLine();
		
		//return if no newlines
//		boolean hasNewlines = lab.containsNewlines();
//		
//		if (!hasNewlines){
//			Log.info("no nextnl or br ");
//			return;
//		}
		//-------------

		// loc of last newline
		/*
		int lastnl = labtext.lastIndexOf("\n");
		String newlinetype="\n";
		if (countbr){
			int lastnlbr = labtext.lastIndexOf("<br>");
			if (lastnlbr>lastnl){
				lastnl=lastnlbr;
				newlinetype="<br>";
			}
		}
*/
		
		int lastnl =lab.getLastNewLineLocation();
		
		if (lastnl==-1){
			Log.info("no newlines");
			return;
		}
		
		//the returned lastnl point might follow a <br> or nl that caused it (if it wasnt auto wrapped)
		//these should be removed, so we have a extra number of chars we count back based on the length of what caused the newline
		int extracountback=0;
		if (lab.isInterpretBRasNewLine() && labtext.substring(0,lastnl).endsWith("<br>")){
			extracountback="<br>".length();	
		} else	if (lab.isInterpretBackslashNasNewLine() && labtext.substring(0,lastnl).endsWith("\n")){
			extracountback="\n".length();	
		}
		
		
		Log.info("lastnl:"+lastnl);
		Log.info("prev chars:"+labtext.substring(0,lastnl-extracountback));
		Log.info("next chars:"+labtext.substring(lastnl));
		
		int newlineendloc = lastnl;//+newlinetype.length();

		insertNewLinebetween(lab, lastnl-extracountback, newlineendloc);


		return ;
	}







	private void splitbynextnewline(Label lab) {
		String labtext=lab.getText().toLowerCase();

/*
		
		Log.info("splitbynextnewline:\n"+labtext);
		boolean countbr = lab.isInterpretBRasNewLine();



		//return if no newlines
		if (countbr){
			//if it contains no nlss && no brs
			if (!labtext.contains("\n") && !labtext.contains("<br>"))
			{
				Log.info("no nextnl or br ");
				return ;
			}			
		} else {
			//if it contains no nlss 
			if (!labtext.contains("\n") )
			{
				Log.info("no nextnl ");
				return ;
			}
		}
		//-------------

		// loc of next newline
		int next_nl = labtext.indexOf("\n");
		String newlinetype="\n";
		if (countbr){
			int lastnlbr = labtext.indexOf("<br>");
			if ((lastnlbr<next_nl) && (lastnlbr!=-1)){
				next_nl=lastnlbr;
				newlinetype="<br>";
			}
		}*/


		int next_nl =lab.getFirstNewLineLocation();

		if (next_nl==-1){
			Log.info("no newlines");
			return;
		}
		
		//the returned nextnl point might be followed by a <br> or nl that caused it (if it wasnt auto wrapped)
		//these should be removed, so we have a extra number of chars we count forward based on the length of what caused the newline
		int extracountforward=0;
		if (lab.isInterpretBRasNewLine() && labtext.substring(next_nl).startsWith("<br>")){
			extracountforward="<br>".length();	
		} else if (lab.isInterpretBackslashNasNewLine() && labtext.substring(next_nl).startsWith("\n")){
			extracountforward="\n".length();	
		}
		
		Log.info("next_nl:"+next_nl+" cf:"+extracountforward+"  "+lab.isInterpretBackslashNasNewLine());
		Log.info("prev chars:"+labtext.substring(0,next_nl));
		String substring = labtext.substring(next_nl+extracountforward);
		Log.info("next chars:"+substring);
//		if (substring.startsWith("\n")){
//			Log.info("______________true");					
//		}
//		if (substring.startsWith("\r\n")){
//			Log.info("______kl;________true");					
//		}
//		if (substring.contains("\n")){
//			Log.info("______nl at:"+substring.indexOf("\n"));		
//			Log.info("______nl at:"+substring.substring(0, 2));	
//			Log.info("______nl at:"+substring.charAt(0));
//			
//				
//		}
		int newlineendloc = next_nl;//+newlinetype.length();

		insertNewLinebetween(lab, next_nl, newlineendloc+extracountforward);


		return ;
	}








	private void insertNewLinebetween_oldunoptimised(Label lab, int newlinestartloc, int newlineendloc) {
		String lab_text=lab.getText();		
		String before = lab_text.substring(0, newlinestartloc);
		String after = lab_text.substring(newlineendloc);

		Log.info(before+"[nl]"+after);

		NewLinePlacer testnewline = new NewLinePlacer(debugMode);
		//testnewline.setMinSize(15, 15); //to help tests make it visible
	//	testnewline.getStyle().setBackgroundColor(Color.RED);
		int index=contents.indexOf(lab)+1; //location of current label to replace


		if (!after.isEmpty()){ 						//we dont make empty labels
			Label afterlab = setupLabel(after,null);						
			super.insert(afterlab, index); //inserts it before
		}
		super.insert(testnewline, index); //inserts it before	

		if (!before.isEmpty()){						//we dont make empty labels
			Label beforelab = setupLabel(before,null);	
			super.insert(beforelab, index); //inserts it before
		}

		super.remove(lab); //remove old
		lab.dispose();
	}

	/**
	 * Inserts a newline object between two locations in a label, splitting the label into two new labels either side
	 * Text Contents between newlinestartloc and newlineendloc is removed 
	 * (the idea being you remove the {@literal "<br>"} or "\n" from the string at the same time)
	 * 
	 * @param lab
	 * @param newlinestartloc
	 * @param newlineendloc
	 */
	//Note: This method manipulates the "contents" array directly, then tells the widget to refresh
	//While easier, using super.Insert(..) directly to change the widgets will needlessly fire 
	//recalculateLargestWidgets,repositionWidgets and sizeToFitContents each time - when they only need to fire once after all the 
	//contents array manipulation is done

	private void insertNewLinebetween(Label lab, int newlinestartloc, int newlineendloc) {
		String lab_text=lab.getText();		
		String before = lab_text.substring(0, newlinestartloc);
		String after = lab_text.substring(newlineendloc);

		Log.info(before+"[nl]"+after);

		NewLinePlacer testnewline = new NewLinePlacer(debugMode); //These are dummywidgets to specify where newlines go in flowpanels
		int index=contents.indexOf(lab)+1; //location of current label to replace
		Label afterlab = null;
		if (!after.isEmpty()){ 						//we dont make empty labels
			 afterlab = setupLabel(after,null);
			prepareWidgetToInsertsZIndex(afterlab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(index, afterlab);
		}

		prepareWidgetToInsertsZIndex(testnewline); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
		contents.add(index, testnewline);
		Label beforelab = null ;
		if (!before.isEmpty()){						//we dont make empty labels
			 beforelab = setupLabel(before,null);	
			prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(index, beforelab);
		}
		//
		//need to get beforer/after insertpoints too, to copy into new labels
		//
		splitInsertPointsBetween(lab, newlinestartloc,newlineendloc, afterlab, beforelab);
		
		
		//now we remove the old label we split
		allKnowenInsertPoints.remove(lab);
		contents.remove(lab);
		contentAlignments.remove(lab);		
		lab.hide(false); //dont change its internal vis setting		
		lab.setParent(null);		
		this.removeAttachment(lab);
		lab.dispose();
		///----------------------


		//now we recalculate and re-draw to put contents in correct order
		//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
		recalculateLargestWidgets(); 		
		repositionWidgets(); 
		sizeToFitContents();
		//

	}





	public void add_unoptimised(Widget newwidget,String id){
		//search all elements
		//look in Labels for id match
		//if found...
		//create labelbefore
		//create labelafter
		//replace

		String IDstring = "<div id=\""+id+"\"></div>";

		ArrayList<Widget> arraycopy= new ArrayList<>(super.contents);

		for (Widget widget : arraycopy) {

			if (widget instanceof Label){

				Log.info("testing label");
				Label lab = (Label)widget;
				String labtext=lab.getText();

				int insertstart = labtext.indexOf(IDstring);

				if (insertstart>-1){

					Log.info("testing label: match found within:"+labtext);
					String before = labtext.substring(0, insertstart);
					String after = labtext.substring(insertstart+IDstring.length());

					Log.info(before+"[widget]"+after);


					int index=contents.indexOf(lab)+1; //location of current label to replace

					Log.info("[index]"+index);
					Label afterlab = null;
					if (!after.isEmpty()){ 						//we dont make empty labels
						afterlab = setupLabel(after,null);						
						super.insert(afterlab, index); //inserts it before
					}
					super.insert(newwidget, index); //inserts it before	

					if (!before.isEmpty()){						//we dont make empty labels



						Label beforelab = setupLabel(before,null);	
						super.insert(beforelab, index); //inserts it before

						splitbylastnewline(beforelab);	//see below
					}

					super.remove(lab); //remove old
					lab.dispose();

					Log.info("[count:]"+getWidgetCount());

					//split after as well
					//a) use splitbynextnewline if theres already a widget after
					//b) flag to use it next time if not
					if (afterlab!=null){
						splitbynextnewline(afterlab);
					} else {

					}

					//	TODO: somehow batch add? insert does a lot of redundant work

					//TODO: we also need to split by whenever the previous newline was, this is too ensure the text is devided squarely
					//
					//[[oldlab.........\n
					//.................\n
					//.................\n
					//......][divid]
					//
					//convert too:
					//[[oldlab.........\n
					//.................\n
					//.................]
					//[lab2.][divid]
					//
					//And split next text too!
					//
					//we need some wy in flowpanel to force newlines too? a dummy newline widget?
				}

			}

		}


		Log.info("debug:\n"+getWidgetDebugString());
		Widget lastwidget = contents.get(contents.size()-1);

		//currentActiveLabel should always be the last label, ensuring there is one first
		if (lastwidget instanceof Label){			
			currentActiveLabel=(Label) lastwidget;
		} else {
			Label newcurrentlabel = this.setupLabel("",null);
			currentActiveLabel=newcurrentlabel;
			super.add(newcurrentlabel);
		}


		//

	}
	public void setMaxWidth(int sizeInt) {
		super.setWidth(sizeInt);
		
	}
	public String getText() {
		String totalText = "";
		//we need to combine all the labels that are in this object
		for (Widget widget : super.contents) {

			if (widget instanceof Label){
				totalText = totalText+((Label)widget).getText();
			}
			
		}

		
		return totalText;
	}
	

	/**
	 * we can optionally interpret html like br tags as newlines
	 */
	private boolean interpretBRasNewLine=true;

	private boolean interpretBackslashNasNewLine=true;
	
	public void setInterpretBRasNewLine(boolean interpretBRasNewLine) {
		 this.interpretBRasNewLine=interpretBRasNewLine;
		
	}
	public void setInterpretBackslashNasNewLine(boolean interpretNLasNewLine) {
		 this.interpretBackslashNasNewLine=interpretNLasNewLine;
	}
}
