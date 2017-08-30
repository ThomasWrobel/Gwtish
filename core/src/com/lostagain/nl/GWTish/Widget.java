package com.lostagain.nl.GWTish;

import java.util.HashSet;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector2;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.GWTish.Management.IsAnimatableModelInstance;
import com.lostagain.nl.GWTish.Management.ModelMaker;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;
import com.lostagain.nl.GWTish.Management.objectInteractionType;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute;
/**
 * This will approximate a similar function as GWTs Widget class does
 * 
 * @author Tom *
 */
public class Widget extends Element {

	private static final String SHADERFORBACKGROUND = "Background";

	//final static String logstag = "GWTish.Widget";
	public static Logger Log = Logger.getLogger("GWTish.Widget");
	
	static Material DefaultWhiteBackground = new Material(
			SHADERFORBACKGROUND,
			new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
			new GwtishWidgetShaderAttribute(1f,Color.CLEAR,Color.RED,1.0f));



	//Handlers
	/**
	 * A HashSet of all the size change handlers associated with this object
	 * @param object
	 */
	HashSet<Runnable> SizeChangeHandlers = new HashSet<Runnable>();

	/**
	 * If this widget is attached to another widget, this is its parent widget
	 * If this widget is resized, its parent will be informed.
	 */
	Widget parentWidget = null;


	/**
	 * Width, not including padding
	 */
	float Width = 0f;
	/**
	 * Height, not including padding
	 */
	float Height = 0f;



	/**
	 * Min Size X - the widget will never go smaller then this in Xwhen resized
	 */
	float MinSizX = 0f;
	/**
	 * Min Size Y - the widget will never go smaller then this in Y when resized
	 */
	float MinSizY = 0f;


	/**
	 * Specifies where the pivot should go on the backing model for this widget
	 * TOPLEFT is effective normal for GWT like behavior, but we are keeping it flexible here for now
	 * @author Tom
	 *
	 **/
	public static enum MODELALIGNMENT {
		TOPLEFT,
		TOPRIGHT,
		CENTER,
		BOTTOMRIGHT,
		BOTTOMLEFT
	}
	
	public MODELALIGNMENT alignment = MODELALIGNMENT.TOPLEFT;

	/**
	 * If supplying your own model remember to call
	 * super.setStyle();

	 * @param object
	 */
	public Widget(Model object) {
		super(object);
		 alignment = MODELALIGNMENT.TOPLEFT;
	}


	/**
	 * 
	 * @param sizeX - minimumSize in X
	 * @param sizeY - minimumSize in Y
	 * @param align - where this model will be in relation to its centerpoint
	 */
	public Widget(float sizeX,float sizeY, MODELALIGNMENT align) {
		super(generateBackground(sizeX,sizeY,generateMaterial(),align)); 
		super.setStyle(getBackgroundMaterial());	
		Width = sizeX;
		Height=sizeY;

		alignment = align;
		
		//	this.setMinSize(sizeX, sizeY); //we no longer set the min size by default		
	}

	public Widget(float sizeX,float sizeY) {
		this(sizeX,sizeY,MODELALIGNMENT.TOPLEFT); //alignment topleft by default

		//	super(generateBackground(sizeX,sizeY,generateMaterial(),MODELALIGNMENT.TOPLEFT)); //alignment topleft by default
		//	super.setStyle(getBackgroundMaterial());		
		//	this.setMinSize(sizeX, sizeY);	//we no longer set the min size by default
	}


	static protected Material generateMaterial(){
		//return  new Material(
		//		SHADERFORBACKGROUND,
		//		new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
		//		new GwtishWidgetShaderAttribute(1f,Color.CLEAR,Color.RED,1.0f));

		return DefaultWhiteBackground.copy();
	}


	/**
	 * Sets the opacity of the background (which will include text as well if this is a Label)
	 * @param opacity
	 */
	public void setOpacity(float opacity){		

		//	Log.info("______________setOpacity:"+opacity);

		//get the material from the model
		//Material infoBoxsMaterial = getMaterial();
		//((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;	

		this.getStyle().setOpacity(opacity);


	}

	/**
	 * Note; This might be null.
	 * In future we might ensure all shaders have the same name within a single widget?
	 * Maybe set the shader name the same time as the shader?
	 *  
	 * @return
	 */
	public Material getBackgroundMaterial(){
		return getMaterial(SHADERFORBACKGROUND);
	}
	/**
	 * Sets the opacity of the background
	 * @param opacity

	public void setBackgroundColor(Color backcol){		


		Log.info("______________backcol:"+backcol);

		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)infoBoxsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));

		backtexture.backColor = backcol;
	}
	 */
	/*
	public void setBorderColor(Color bordercol){		

		this.getStyle().setBorderColor(bordercol);

	}*/

	/**
	 * makes a arbitrarily sized background that will be expanded as widgets are added
	 * @return
	 */
	protected static Model generateBackground(float sizeX,float sizeY,Material mat,MODELALIGNMENT alignment) {    

		//we first get the offset 
		Vector2 offset =  getOffsetForSize(sizeX, sizeY,alignment);

		Log.info(" offsetFor "+alignment+" of "+sizeX+","+sizeY+" is "+offset);
		
		Model newModel = ModelMaker.createRectangle(offset.x, (offset.y), sizeX+offset.x,(sizeY+offset.y), 0, null,mat,true); 	
		
		
		
		
		Log.info(" rect; "+(offset.x)+","+ (offset.y)+"," 
				+(sizeX+offset.x)+","+(sizeY+offset.y));
		
		return newModel;
	}


	//handles
	protected void fireAllSizeChangeHandlers (){
		Log.info(" firing all size change handlers:"+SizeChangeHandlers.size());
		for (Runnable handler : SizeChangeHandlers) {

			Log.info(" firing all size change handler:"+handler.hashCode());
			handler.run();

		}		
	}

	public void addOnSizeChangeHandler(Runnable onSizeChange){

		SizeChangeHandlers.add(onSizeChange);		
	}
	public void removeOnSizeChangeHandler(Runnable onSizeChange){
		SizeChangeHandlers.remove(onSizeChange);		
	}
	public Widget getParent() {
		return parentWidget;
	}



	//the offset tells us where the top left corner will be relative to the pivot point.
	//Effectively it lets us have a custom position for the pivot by messuring everything relative to that point 
	//when creating the polygon vectexs
	protected static Vector2 getOffsetForSize(float newWidth, float newHeight,MODELALIGNMENT alignment )
	{
		
		Vector2 offset = new Vector2(0,0);
		switch(alignment) 
		{
		case CENTER:
			offset.x = -newWidth/2;
			offset.y = newHeight/2;  //remember y is measured bottom up
			break;
		case TOPLEFT:
			offset.x = 0;
			offset.y = 0;
			break;
		case BOTTOMRIGHT:
			offset.x = -newWidth;
			offset.y = newHeight;
			break;
		case BOTTOMLEFT:
			offset.x = 0;
			offset.y = newHeight;			
			break;
		case TOPRIGHT:
			offset.x = -newWidth;
			offset.y = 0;			
			break;
		default:
			break;

		}

		return offset;


	}
	

	public void setWidth(float newWidth) {					
		this.setWidth(newWidth, true);					
	}
	
	/**Sets the object's size. This size does not include padding, which will be added automatically to get the final size	**/
	public void setWidth(float newWidth,boolean FireSizeChangeEvents) {

		setSizeAs(newWidth,  Height,FireSizeChangeEvents);

	}


	public void setHeight(float newHeight) {					
		this.setHeight(newHeight, true);					
	}

	/**Sets the object's size. This size does not include padding, which will be added automatically to get the final size	**/
	public void setHeight(float newHeight,boolean FireSizeChangeEvents) {
		setSizeAs(Width,  newHeight,FireSizeChangeEvents);
	}


	public void setSizeAs(float newWidth, float newHeight) {					
		this.setSizeAs(newWidth, newHeight, true);					
	}

	/**
	 * Sets the object's size. This size does not include padding, which will be added automatically to get the final size	
	 * @param newWidth
	 * @param newHeight
	 * 
	 * @param FireSizeChangeEvents - tells children and parent that its size has been changed. This should normally be true unless your reacting to a size change event and you want to suppress potential feedback loops
	 */
	public void setSizeAs(float newWidth, float newHeight, boolean FireSizeChangeEvents) {
		Log.info("_________newWidth:"+"("+getStyle().PaddingLeft+","+newWidth+","+getStyle().PaddingRight+")");		
		Log.info("_________newHeight:"+"("+getStyle().PaddingTop+","+newHeight+","+getStyle().PaddingBottom+")");
		Width = newWidth;
		Height= newHeight;

		//adjust for padding
		newWidth  = getStyle().PaddingLeft+newWidth+getStyle().PaddingRight;
		newHeight = getStyle().PaddingTop+newHeight+getStyle().PaddingBottom;

		//we first get the offset 
		Vector2 offset =  getOffsetForSize(newWidth, newHeight,alignment);

		Log.info(" offsetFor "+alignment+" of "+newWidth+","+newHeight+" is "+offset);
		
		//the offset tells us where the top left corner will be relative to the pivot point.
		//Effectively it lets us have a custom position for the pivot by measuring everything relative to that point 
		//when creating the polygon vectexs

		setSizeAs(newWidth,  newHeight, -offset.x,offset.y,FireSizeChangeEvents);//todo: invert the sign of the offset in the function itself

	}

	/**
	 * Changes this objects rect mesh background to the new specified size
	 * The internal texture will be stretched
	 * 
	 * @param newWidth
	 * @param newHeight
	 */
	private void setSizeAs(float newWidth, float newHeight,float offsetX,float offsetY, boolean FireSizeChangeEvents) {

		

		//ensure not smaller then minimum
		if (newWidth<this.MinSizX){
			newWidth = MinSizX;

		}
		if (newHeight<this.MinSizY){
			newHeight = MinSizY;
		}

		Log.info(" sizing to ::"+newWidth+","+newHeight);

		//note we can optimize here by checking current size against requested and ensuring its different?
		if (newHeight==this.getHeight() && newWidth==this.getWidth()){
			Log.info("______________already at requested or minimum size:"+newWidth+","+newHeight);
			return;			
		}

		//

		
		
		Mesh IconsMesh = this.model.meshes.get(0);

		final VertexAttribute posAttr = IconsMesh.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = IconsMesh.getNumVertices();
		final int vertexSize = IconsMesh.getVertexSize() / 4;

		final float[] vertices = new float[numVertices * vertexSize];
		IconsMesh.getVertices(vertices);
		int idx = offset;

		float w = newWidth-offsetX;
		float topY = newHeight+offsetY;
		float bottomY = offsetY;

		//move up by the height 
		topY = topY -newHeight;
		bottomY = bottomY - newHeight;
		
		
		Log.info("______________offsetX:"+offsetX);
		Log.info("______________bottomY:"+offsetY);
		Log.info("______________w:"+w);
		Log.info("______________topY:"+topY);
		
		
		//centerl
		//float newSizeArray[] = new float[] { -hw,-hh,0,
		//									  hw,-hh,0,
		//									  hw,hh,0,
		//									 -hw,hh,0 };
		//
//
//		float newSizeArray[] = new float[] { 
//				-offsetX,-offsetY,0,
//				w,-offsetY,0,
//				w,h,0,
//				-offsetX,h,0 };


		float newSizeArray[] = new float[] { 
				-offsetX,bottomY,0,
				w,bottomY,0,
				w,topY,0,
				-offsetX,topY,0 };
		
		//can be optimized latter by pre-calcing the size ratio and just multiply
		for (int i = 0; i < 12; i=i+3) {


			//currently just scale up a bit
			vertices[idx    ] = newSizeArray[i];
			vertices[idx + 1] = newSizeArray[i+1];
			vertices[idx + 2] = newSizeArray[i+2];

			idx += vertexSize;
		}


		IconsMesh.setVertices(vertices);

		Log.info(" old size::"+this.getWidth()+","+this.getHeight());

		//recalc bounding box if theres one
		wasResized();

		Log.info(" new size::"+this.getWidth()+","+this.getHeight());

		//ensure things attached are repositioned
		updateAllAttachedObjects();

		Log.info(" new size2::"+this.getWidth()+","+this.getHeight());

		if (FireSizeChangeEvents){

			//inform parent
			if (parentWidget!=null){
				Log.info("updating parent of "+this.getClass());		
				parentWidget.onChildResize();
			}
			//inform children
			for (IsAnimatableModelInstance child : this.getAttachments()){
				//ensure its a widget before casting
				if (child instanceof Widget){
					Log.info("updating child of "+this.getClass());	

					((Widget)child).onParentResize();
				}

			}

			//fire size change handlers		
			fireAllSizeChangeHandlers();

		}
	}

	protected String widgetName = this.getClass().getName();
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}


	/**
	 * purely for debugging work, all widgets can be named. You can then use this name in the logs.
	 * @return
	 */
	public  String getName() {
		return widgetName;
	}

	/**
	 * If needed, override this method to rearrange widgets or resize stuff after a size change of a child widget
	 */
	protected void onChildResize() {

	}

	/**
	 * If needed, override this method to rearrange widgets or resize stuff after a size change of a parent widget
	 */
	protected void onParentResize() {

	}

	public MODELALIGNMENT getAlignment() {
		return alignment;
	}
	/** sets the alignment var and recalcs the mesh to match **/
	public void setPivotAlignment(MODELALIGNMENT alignment) {
		this.alignment = alignment;

		Log.info("______________getWidth"+this.getWidth());
		setSizeAs(this.getWidth(),this.getHeight()); //we should check if before/after it matches

		Log.info("______________getWidth"+this.getWidth());


	}

	

	public void setMinWidth(float minSizeX) {
		this.setMinSize(minSizeX, MinSizY);
	}
	public void setMinHeight(float minSizeY) {
		this.setMinSize(MinSizX, minSizeY);
		
	}
	
	public void setMinSize(float minSizeX,float minSizeY) {
		
		MinSizX = minSizeX;
		MinSizY = minSizeY;
		
		float currentWidth = getWidth();
		float currentHeight = getHeight();
		
		//should recheck if too small?
		if (currentWidth<MinSizX || currentHeight<MinSizY){
		
			if (currentWidth<MinSizX){
				currentWidth = MinSizX;
			}
			if (currentHeight<MinSizY){
				currentHeight = MinSizY;
			}
			
			//resize to min
			this.setSizeAs(currentWidth, currentHeight);
		}
		
		
	}

	protected void setParent(Widget parentWidget) {
		this.parentWidget=parentWidget;
	}

	/**
	 * sets this widget to the zindex and groupname, also sets child widgets to +1 the supplied value
	 * @param index
	 * @param groupname
	 */
	public void setZIndex(int index, String groupname) {
		//getStyle().setZIndex(index,group);
		//ZIndexGroup.getZIndexGroup(groupname);
		this.setZIndex(index, ZIndexGroup.getZIndexGroup(groupname), true);
		
	}
	/**
	 * sets this widget to the zindex and groupname, also sets child widgets to +1 the supplied value
	 * @param index
	 * @param groupname
	 */
	
	public void setZIndex(int index, ZIndexGroup group) {
		//getStyle().setZIndex(index,group);
		this.setZIndex(index, group, true);
		
	}

	/**
	 * 
	 * @param index
	 * @param group
	 * @param setChildWidgets - sets child widgets to +1 the supplied value
	 */
	public void setZIndex(int index, ZIndexGroup group,boolean setChildWidgets) {
		getStyle().setZIndex(index,group);
		
		if (setChildWidgets){
			for (IsAnimatableModelInstance model : this.getAttachments()) {
				
				if (model instanceof Widget){
					//we can only set z-index on widgets, so we need to ensure they are before casting
					Widget child = (Widget) model;
					child.setZIndex(index+1, group, true);
					
				}
				
			}
			
		}
		
	}

	

	objectInteractionType currentInteractionType = objectInteractionType.Blocker;
	
	public void setInteractionType(objectInteractionType currentInteractionType) {
		this.currentInteractionType=currentInteractionType;		
		return;
	}


	/**
	 * widgets are all click blockers by default meaning they block interaction behind them.
	 * 
	 * remember to change this if you need a widget that lets clicks though it
	 */
	@Override
	public objectInteractionType getInteractionType() {
			return currentInteractionType;
	}

	static int uniqueNamesGeneratedCount = 0;
	/**
	 * Gets a unique string identifier for a group IP.
	 * IDs are generated simple with a counter that goes up for each one generated
	 * 
	 * @param prefix - a prefix for the ID
	 * @return
	 */
	static public String generateUniqueGroupID(String prefix){
		uniqueNamesGeneratedCount++;				
		return prefix+uniqueNamesGeneratedCount;

	}

	
	/**
	 * override with any needed disposes
	 */
	public void dispose(){
		SizeChangeHandlers.clear();
		SizeChangeHandlers=null;
	}
	
}
