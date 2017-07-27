package com.lostagain.nl.GWTish;

import java.util.ArrayList;
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
 * 
 * @author darkflame
 *
 */		

//TODO: we might need a option to split all labels by their newlines if this widget might resize
//this should only be ondemand though, else there might be a lot of wasted objects
public class InsertsPanel extends FlowPanel {
	public static Logger Log = Logger.getLogger("Gwtish.InsertsPanel"); 
	Label currentActiveLabel;
	private boolean splitnextnl=false;
	
	//[[oldlab][randomobject][oldlab][currentlabel...]]

	boolean debugMode = true;

	public InsertsPanel() {
		super();
		currentActiveLabel=getDefaultLabelType("");
		super.add(currentActiveLabel);
	}





	public InsertsPanel(float maxWidth) {
		super(maxWidth);

		currentActiveLabel=getDefaultLabelType("");
		super.add(currentActiveLabel);
	}


	private Label getDefaultLabelType(String contents) {
		Label label = new Label(contents);
		label.setInterpretBRasNewLine(true); //as this sort of emulates html

		label.setMaxWidth(super.maxWidth); //ensures never wider then this container. However, inserts may still make textlabels go outside the boundary right now

		if (debugMode){
		label.getStyle().setBorderColor(Color.WHITE);
		}
		
		return label;
	}


	public void addInnerText(String addThisText){

		boolean countbr = currentActiveLabel.isInterpretBRasNewLine();		
		boolean hasNewlines = containsNewlines(addThisText, countbr);
				
		//if we dont have to split, or theres no newlines we can just add the text
		if (!splitnextnl || !hasNewlines){			
			Log.info("adding text to existing");
			currentActiveLabel.addText(addThisText);
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
			Label beforelab = getDefaultLabelType(before);			
			prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(beforelab); //inserts it before
		};
		
		NewLinePlacer newline = new NewLinePlacer(debugMode); 
		prepareWidgetToInsertsZIndex(newline); 
		contents.add(newline);
		
		if (!after.isEmpty()){ 						//we dont make empty labels
			Label afterlab = getDefaultLabelType(after);			
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

	public void setInnerText(String addThisText){
		super.clear();
		super.add(currentActiveLabel);
		currentActiveLabel.setText(addThisText);

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

		String IDstring = "<div id=\""+element_id+"\"></div>"; //id to look in string for. The Label will be split either side of this into two new labels with newwidget inbetween

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
						afterlab = getDefaultLabelType(after);			
						prepareWidgetToInsertsZIndex(afterlab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
						contents.add(index,afterlab); //inserts it before
					}

					prepareWidgetToInsertsZIndex(newwidget); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
					contents.add(index,newwidget); //inserts it before	

					if (!before.isEmpty()){						//we dont make empty labels

						Label beforelab = getDefaultLabelType(before);	
						prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
						contents.add(index,beforelab); //inserts it before
						splitbylastnewline(beforelab);	//see below
					}

					//now we remove the old label we split
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





	/**
	 * repositioning of widgets must be run after this
	 */
	private void ensureCurrentActiveLabelSet() {
		Widget lastwidget = contents.get(contents.size()-1);

		//currentActiveLabel should always be the last label, ensuring there is one first
		if (lastwidget instanceof Label){			
			currentActiveLabel=(Label) lastwidget;
		} else {
			Label newcurrentlabel = this.getDefaultLabelType("");
			prepareWidgetToInsertsZIndex(newcurrentlabel); 
			currentActiveLabel=newcurrentlabel;
			contents.add(newcurrentlabel);
		}
	}




	private void splitbylastnewline(Label lab) {
		String labtext=lab.getText().toLowerCase();

		boolean countbr = lab.isInterpretBRasNewLine();
		
		//return if no newlines
		boolean hasNewlines = containsNewlines(labtext, countbr);
		
		if (!hasNewlines){
			Log.info("no nextnl or br ");
			return;
		}
		//-------------

		// loc of last newline
		int lastnl = labtext.lastIndexOf("\n");
		String newlinetype="\n";
		if (countbr){
			int lastnlbr = labtext.lastIndexOf("<br>");
			if (lastnlbr>lastnl){
				lastnl=lastnlbr;
				newlinetype="<br>";
			}
		}


		Log.info("lastnl:\n"+lastnl);
		int newlineendloc = lastnl+newlinetype.length();

		insertNewLinebetween(lab, lastnl, newlineendloc);


		return ;
	}





	private boolean containsNewlines(String labtext, boolean countbr) {
		boolean hasNewlines = true;
		if (countbr){
			//if it contains no nlss && no brs
			if (!labtext.contains("\n") && !labtext.contains("<br>"))
			{
				Log.info("no nextnl or br ");
				hasNewlines = false;
			}			
		} else {
			//if it contains no nlss 
			if (!labtext.contains("\n") )
			{
				Log.info("no nextnl ");
				hasNewlines = false;
			}
		}
		return hasNewlines;
	}



	private void splitbynextnewline(Label lab) {


		String labtext=lab.getText().toLowerCase();

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
		}


		Log.info("nextnl:\n"+next_nl);
		int newlineendloc = next_nl+newlinetype.length();

		insertNewLinebetween(lab, next_nl, newlineendloc);


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
			Label afterlab = getDefaultLabelType(after);						
			super.insert(afterlab, index); //inserts it before
		}
		super.insert(testnewline, index); //inserts it before	

		if (!before.isEmpty()){						//we dont make empty labels
			Label beforelab = getDefaultLabelType(before);	
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

		if (!after.isEmpty()){ 						//we dont make empty labels
			Label afterlab = getDefaultLabelType(after);
			prepareWidgetToInsertsZIndex(afterlab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(index, afterlab);
		}

		prepareWidgetToInsertsZIndex(testnewline); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
		contents.add(index, testnewline);

		if (!before.isEmpty()){						//we dont make empty labels
			Label beforelab = getDefaultLabelType(before);	
			prepareWidgetToInsertsZIndex(beforelab); 	//(normally done in super.insert automatically, but as we are manually changing the contents we need to do it ourselves)
			contents.add(index, beforelab);
		}

		//now we remove the old label we split
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
						afterlab = getDefaultLabelType(after);						
						super.insert(afterlab, index); //inserts it before
					}
					super.insert(newwidget, index); //inserts it before	

					if (!before.isEmpty()){						//we dont make empty labels



						Label beforelab = getDefaultLabelType(before);	
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
			Label newcurrentlabel = this.getDefaultLabelType("");
			currentActiveLabel=newcurrentlabel;
			super.add(newcurrentlabel);
		}


		//

	}


}
