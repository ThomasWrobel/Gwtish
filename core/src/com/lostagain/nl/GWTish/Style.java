package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.Style.Unit;
import com.lostagain.nl.GWTish.Management.ZIndexAttribute;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute.StyleParam;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute.presetTextStyle;

/**
 * stores style parameters for Elements
 * @author Tom
 *
 */
public class Style {
	final static String logstag = "GWTish.Style";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag
	
	
	Element elementWithStyle;
	Material objectsMaterial = null;

	GwtishWidgetShaderAttribute styleAttribute;
	
//	GwtishWidgetBackgroundAttribute    backStyle;
	
	
	
	//enums for shader changes (layout is below)
	
	
	//Not used yet, but shouldn't be too hard to modify a shader to add a underline or overline
		public enum TextDecoration {
			     NONE ,
			     UNDERLINE,
			     OVERLINE ,
			     LINE_THROUGH;
		}
		
		

		/**
		 * The style object must be given the objects material, which for most functions needs to use the distancefieldshader
		 * Its this material which will have its attributes changed
		 * 
		 * @param objectsMaterial
		 **/
		public Style(Element elementWithStyle,Material mat) {
			this(elementWithStyle,mat,false);
		}
	
	/**
	 * The style object must be given the objects material, which for most functions needs to use the distancefieldshader
	 * Its this material which will have its attributes changed
	 * 
	 * @param objectsMaterial
	 **/
	public Style(Element elementWithStyle,Material mat,boolean clearExistingGWTishStyleSettings) {
		this.objectsMaterial=mat;
		this.elementWithStyle=elementWithStyle;
		
		//get existing gwtish style
		styleAttribute     = ((GwtishWidgetShaderAttribute)objectsMaterial.get(GwtishWidgetShaderAttribute.ID));
		
		//clear if requested
		if (styleAttribute !=null && clearExistingGWTishStyleSettings){
			styleAttribute.resetToDefaults();
		}
		
		//create new if needed	

		createTextAttributeIfNeeded();
		//if (styleAttribute==null){
		//	styleAttribute = new GwtishWidgetShaderAttribute(Color.BLACK); //default
		//} 
	}

	/**
	 * empty style with GWTishshader - used only for testing
	 */
	public Style() {
		//styleAttribute = new GwtishWidgetShaderAttribute(Color.BLACK);
		objectsMaterial = new Material();

		createTextAttributeIfNeeded();
	}

	/**
	 * Sets the color of the object.
	 * This is normally the text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setColor(Color col){

		nullParameterCheck(col);
		
		if (styleAttribute!=null){

		//	Log.info("_________setting color to:"+col);
			//Log.info("_________setting color to:"+col);
			
			styleAttribute.textColour.set(col);
		}

	}
	
	 /* This is normally the shadow text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setShadowColor(Color col){
		//	Log.info("_________setting shadow color to:"+col);			
			styleAttribute.shadowColour.set(col);
		
	}
	
	public void setShadowBlur(float bluramount){	
			styleAttribute.shadowBlur=bluramount;
		
	}
	
	public void setShadowX(float xdisplacement){
			styleAttribute.shadowXDisplacement=xdisplacement;
		
	}
	public void setShadowY(float ydisplacement){	
			styleAttribute.shadowYDisplacement=ydisplacement;
		
	}
	
	public void setTextGlowColor(Color col) {
		if (styleAttribute!=null){
			
			Log.info("_________setting glow color to:"+col);
			
			styleAttribute.glowColour.set(col);
		}
	}
	/**
	 * 
	 * @param size - 0 to 1.0
	 */
	public void setTextGlowSize(float size) {
		if (styleAttribute!=null){			
			Log.info("_________setting glow size to:"+size);			
			styleAttribute.glowSize= size;
		}
	}
	/**
	 * subject to change to make values more intuitive 
	 * works bad with shadow
	 * @param inner - try 0.2
	 * @param outer - try 0.05
	 */
	public void setTextOutineLimits(float inner,float outer ) {
		if (styleAttribute!=null){
			
			Log.info("_________setting inner limit to:"+inner);			
			styleAttribute.outlinerInnerLimit = inner;
			Log.info("_________setting outer limit to:"+outer);			
			styleAttribute.outlinerOuterLimit = outer;
		}
	}
	public void setTextOutlineColor(Color col) {
		if (styleAttribute!=null){
			
			Log.info("_________setting outline color to:"+col);
			
			styleAttribute.outlineColour.set(col); 
		}
	}
	
	
	/**
	 * Sets the width of the background border.
	 * Should be in world units, but it looks odd if too big.
	 * 
	 * @param borderWidth
	 */
	public void setBorderWidth(float borderWidth) {
		
	//	nullParameterCheck(bordercol);
		
	//	createBackgroundAttributeIfNeeded();
	//	Log.info("_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

			styleAttribute.borderWidth = borderWidth;
			
		//	glowingSquare.glowColor = bordercol;
	//	}
			styleAttribute.checkShaderRequirements();
			
	}
	/**
	 * @param bordercol
	 */
	public void setBorderColor(Color bordercol) {
		
		nullParameterCheck(bordercol);
		
	//	createBackgroundAttributeIfNeeded();
	//	Log.info("_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

		styleAttribute.borderColour.set(bordercol);
		//	glowingSquare.glowColor = bordercol;
	//	}
		styleAttribute.checkShaderRequirements();
		

	}
	
	public void setBorderRadius(float radius) {
	//	createBackgroundAttributeIfNeeded();
			styleAttribute.cornerRadius = radius;
			styleAttribute.checkShaderRequirements();
			
	}

	/**
	 * Sets the background color
	 * @param backcol
	 */
	public void setBackgroundColor(Color backcol){	

		Log.info("_________________ set backcol to:"+backcol.toString());
		nullParameterCheck(backcol);
		
		
		//createBackgroundAttributeIfNeeded();

		
		
		//Log.info("______________backcol:"+backcol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){
			//	GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
		styleAttribute.backColor.set(backcol);
		//}
		Log.info("_________________ set backcol to:"+styleAttribute.backColor.toString());
		
	//	if (textStyle!=null){
	//		objectsMaterial.set( ColorAttribute.createDiffuse(backcol));
	//	}
		styleAttribute.checkShaderRequirements();

		
	}

	private void nullParameterCheck(Color backcol) {
		if (backcol==null){
			Gdx.app.log(logstag,"colour can not be null",new Throwable("null specified for backcolour, this will break shader rendering"));
		}
	}
	
	/*
	private void createBackgroundAttributeIfNeeded() {
		
		if (backStyle==null){
			
			Log.info("_________(creating default background shader attribute)");
			
			backStyle =  new GwtishWidgetBackgroundAttribute(1f,Color.CLEAR,Color.CLEAR,1.0f);
			this.addAttributeToShader(backStyle);
			
		}
		
	}*/
	
	private void createTextAttributeIfNeeded() {
		
		if (styleAttribute==null){			

			Log.info("_________(creating default text shader attribute)");
			//if we are creating one automatically on demand, everything is set to clear
			styleAttribute  = new GwtishWidgetShaderAttribute(GwtishWidgetShaderAttribute.presetTextStyle.NULL_DONTRENDERTEXT);
			addAttributeToShader(styleAttribute);			
		}
		
	}
	
	//BlendingAttribute blendAttribute;
	
	/**
	 * Sets the opacity of this widget.
	 * Specifically it adds a blending style with the opacity set
	 * 
	 * This opacity will be used in the shader to effect both the backcolour and text colour without altering their colour setting
	 * (ie, if there colour is only 0.5 opacity anyway, then setting the opacity to 1.0 means it will still be 0.5 opacity)
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {

		//create blend shader if needed, else change the one we have
		
/*
		Log.info("_________(request opacity setting)");
		Log.info("_________(attributes:)"+objectsMaterial.size());

		Log.info("_________(objectsMaterial)"+BlendingAttribute.Type);
		Log.info("_________(objectsMaterial)"+objectsMaterial.has(BlendingAttribute.Type));
		*/
		
		
		if (!objectsMaterial.has(BlendingAttribute.Type)){
			
			BlendingAttribute blendAttribute = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,opacity);
			this.addAttributeToShader(blendAttribute);
			
		} else {
			
			((BlendingAttribute) objectsMaterial.get(BlendingAttribute.Type)).opacity = opacity;
		}
		
		
		
	}
	
	public float getOpacity() {		
		if (objectsMaterial.has(BlendingAttribute.Type)){
			return ((BlendingAttribute) objectsMaterial.get(BlendingAttribute.Type)).opacity;
		}
		return 1.0f;
	}
	
	
	

	
	/**
	 * sets a background image on this style<br>
	 * The string specifies a local location. To help with CSS compatibility<br>
	 * you can specify it as;<br>
	 * <br>
	 *  url("paper.gif")<br>
	 *  <br>
	 *  The url, brackets, and quotes will all be stripped off automatically.<br>
	 *  <br>
	 *  setting the value to "none" will remove any set image <br><br>
	 * @param internalLocation
	 */
	public void setBackgroundImage(String internalLocation) {
		
		//if set to none we clear instead
		if (internalLocation.equalsIgnoreCase("none")){
			clearBackgroundImage();
			return;
		}
		
		//strip css url trappings
		if (internalLocation.startsWith("url(")){
			internalLocation = internalLocation.trim(); //remove any spaces at end
			internalLocation = internalLocation.substring(4,internalLocation.length()-1); //crop out url brackets
			//remove quotes if present
			if (internalLocation.startsWith("\"")){
				internalLocation=	internalLocation.substring(1, internalLocation.length()-1);
			}
			
		}

		Log.info("_________(loading:"+internalLocation+":)");
		Texture image = new Texture(internalLocation);
		setBackgroundImage(image);
		
		
	}
		
	/**
	 * adds a texture attribute with the specified texture, replacing any existing
	 * @param testimage
	 */
	public void setBackgroundImage(Texture image) {
	
		addAttributeToShader(	TextureAttribute.createDiffuse(image));			
	
	}
	
	/**
	 * removes background if one was set
	 * you can also use setBackgroundImage("none")
	 * 
	 * @param testimage
	 */
	public void clearBackgroundImage() {
	
		removeAttributeFromShader(	TextureAttribute.Diffuse);	
			
	
	}


	
	/**
	 * Sets z-index value and groupname
	 * 
	 * @param opacity
	 */
	public void setZIndex(int index, String group) {

			objectsMaterial.set( new ZIndexAttribute(index,group) );
		
		
	}
	
	/**
	 * Sets z-index value and group
	 * 
	 * @param opacity
	 */
	public void setZIndex(int index, ZIndexGroup group) {

			objectsMaterial.set( new ZIndexAttribute(index,group) ); 
		
	}
	
	
	public void clearZIndex() {
		if (hasZIndex()){
			objectsMaterial.remove(ZIndexAttribute.ID);
		}
	}


	
	
	public boolean hasZIndex() {
		return objectsMaterial.has(ZIndexAttribute.ID);		
	}
	
	/**
	 * returns -1 if no zindex set
	 * @return
	 */
	public int getZIndexValue() {
		
		if (hasZIndex()){
			return ((ZIndexAttribute)objectsMaterial.get(ZIndexAttribute.ID)).zIndex;			
		}
		
		return -1;
	}

	
	
	public ZIndexGroup getZIndexGroup() {
		
		if (hasZIndex()){
			return ((ZIndexAttribute)objectsMaterial.get(ZIndexAttribute.ID)).group;
		}
		
		return null;		
	}
	
	/**
	 * Sets the background color
	 * @param opacity
	 */
	public void addAttributeToShader(Attribute attribute){		

			objectsMaterial.set( attribute);
		
	}
	
	private void removeAttributeFromShader(long ID) {
		objectsMaterial.remove(ID); 
		
	}

	
	/**
	 * FOR TESTING ONLY, don't use
	 * @return
	 */
	public Material getMaterial() {
		return objectsMaterial;
	}
	public void clearShadowColor() {
		setShadowColor(Color.CLEAR); 		
	}
	public void clearGlowColor() {
		setTextGlowColor(Color.CLEAR); 		
	}
	public void clearBorderColor() {
		setBorderColor(Color.CLEAR);		
	}
	public void clearColor() {
		setColor(Color.CLEAR); 
		
	}
	public void clearBackgroundColor() {

		Log.info("_________________ Clear a:"+Color.CLEAR.a);
		
		setBackgroundColor(Color.CLEAR); 

	}
	

	public void setTextStyle(presetTextStyle standardwithshadow) {
		if (styleAttribute!=null){
			styleAttribute.setToPreset(standardwithshadow);
		}
	}
	
	
	//--------------------------
	//------filter handling
	//--------------------------
	
	/**
	 * 0=black
	 * 1=original image
	 * >1 = brighter
	 * //not implemented yet
	 * 
	 * @param brightness
	 */
	public void setBrightnessFilter(float brightness){
		styleAttribute.filter_brightness = brightness;
		styleAttribute.checkShaderRequirements();
		
	}
	
	/**
	 * 0= completely grey
	 * 1=original image
	 * >1=more contrast
	 * 
	 * //not implemented yet
	 * 
	 * @param contrast
	 */
	public void setContrastFilter(float contrast){
			styleAttribute.filter_contrast=contrast;
			styleAttribute.checkShaderRequirements();
			
	}
	
	
	//private void ensureBCFilterEnabled() {		
	//	styleAttribute.usesBCPostFilter=true; //ensures the post filter part of the shader will be compiled		
	//	//TODO:we could test if the filters are all set to default values and turn it off if it can?
	//}
	
	
	
	//hsv filter settings;

	public void setHueFilter(float hue){
		styleAttribute.filter_hue=hue;
		styleAttribute.checkShaderRequirements();
		
	}

	/**
	 * 0= completely desaturated
	 * 1=original image
	 * >1=more saturation
	 * 
	 * //not implemented yet
	 * 
	 * @param saturation
	 */
	public void setSaturationFilter(float saturation){
		
		styleAttribute.filter_saturation=saturation;
		styleAttribute.checkShaderRequirements();
		
	}
	public void setValueFilter(float value){
		
		styleAttribute.filter_value=value;
		styleAttribute.checkShaderRequirements();
		
	}
	
	
	
	//exp transform
	public void setTransformPosition(Vector3 displacement){
		
		styleAttribute.transform.position.set(displacement);
		styleAttribute.checkShaderRequirements();
		
	}
	public void setTransformScale(Vector3 scale){
		
		styleAttribute.transform.scale.set(scale);
		styleAttribute.checkShaderRequirements();
		
	}
	
	/**
	 * 
	 * @param rot - eg new Quaternion(Vector3.Z,45) for 45deg
	 */
	public void setTransformRotation(Quaternion rot){
	
		styleAttribute.transform.rotation.set(rot);
		styleAttribute.checkShaderRequirements();
	
	}

	
//	private void ensureHSVFilterEnabled() {		
//		styleAttribute.usesHSVPostFilter=true; //ensures the post filter part of the shader will be compiled		
		//TODO:we could test if the filters are all set to default values and turn it off if it can?
//	}
	
	

	
	//-------------------
	//-------------------
	//layout related styles
	//-------------------
	//-------------------
	//ref; http://grepcode.com/file/repo1.maven.org/maven2/com.google.gwt/gwt-user/2.0.4/com/google/gwt/dom/client/Style.java
	

	/**
	 * Enum for the style unit
	 * PX is normally the one supported for now on Label settings, but others will hopefully be supported eventually
	 * Dont use others for now
	 * @author darkflame
	 *
	 */	
	 public enum Unit {
		 /** special unit used to indicate a not-set value **/
		 NOTSET,
		 /** Unitless, this means if used for lineheight the number gets multiplied by the fontsize**/
		 UNITLESS,
		 PX,
		 /**percentage, should be % in strings **/
		 PCT, 
		 MM,
		 CM,
		 IN,
		 PC,
		 PT,
		 EX		 
	 }
	
	/**
	 * Enum for the text-align property.
	 */
	  public enum TextAlign {
	    CENTER ,
	    @Deprecated
	    JUSTIFY,
	    LEFT,
	    RIGHT;	    
	  }
	  
	  TextAlign textHorizontalAlignment       = TextAlign.LEFT;
	  
	  

		/**
		 * Enum for the vertical text-align property.
		 */
		  public enum TextVerticalAlign {
			  TOP,
			  MIDDLE,
			  BOTTOM
		  }
	  
	  TextVerticalAlign textVerticalAlignment = TextVerticalAlign.TOP;
	       
	


	float PaddingLeft = 0f;
	  float PaddingTop  = 0f;
	  float PaddingRight = 0f;
	  float PaddingBottom  = 0f;
	  
	  
	  
	/**
	 * should be fired when any style related to layout is changed.
	 * ie. text alignment,padding etc
	 * pure Shader changes don't need to fire this.
	 * This method then fires a update function on the object with this style
	 **/
	private void layoutStyleChanged(){
		if (elementWithStyle!=null){
			elementWithStyle.layoutStyleChanged();
		}
		
		
	}

	public TextAlign getTextAlignment() {
		return textHorizontalAlignment;
	}

	public void setTextAlignment(TextAlign textAlignment) {
		this.textHorizontalAlignment = textAlignment;
		styleAttribute.textAlignmentHorizontal = textAlignment;
		layoutStyleChanged();
	}
	
	  public void setTextVerticalAlignment(TextVerticalAlign textVerticalAlignment) {
			this.textVerticalAlignment = textVerticalAlignment;

			styleAttribute.textAlignmentVertical = textVerticalAlignment;
			layoutStyleChanged();
		}
	
	double lineHeight   = -1;
	Unit lineHeightUnit = Unit.NOTSET; 
	
	public void setLineHeight(double value,
            Style.Unit unit) {
		lineHeight = value;
		lineHeightUnit=unit;
		layoutStyleChanged();
		
	}
	/**
	 * returns -1 if no line height has been set
	 * @return
	 */
	public double getLineHeightValue(){
		if (lineHeightUnit==Unit.NOTSET){
			return -1;
		}		
		return lineHeight;		
	}
	
	public Unit getLineHeightUnit(){
		return lineHeightUnit;
	}
	
	/**
	 * DONT CHANGE THIS VALUE.
	 * This is only here right now for experiments, allthough the setting itself is needed for fixedsized labels to work internally.
	 * 
	 * Ultimately, this effects the shaders impression of the text size *only*, and doesn't
	 * effect widget size at all
	 * 
	 * In future, hopefully soon, there will be proper font size controll. Dont use this as a replacement!
	 */
	public void setTextScale(float scale){
	//	createTextAttributeIfNeeded();
		styleAttribute.textScale = scale;				
	//	layoutStyleChanged();
	}
	

	//font sizing implementation wip
	double fontSize = -1;
	Unit fontSizeUnit =  Unit.NOTSET;
	
	public double getFontSize() {
		if (fontSizeUnit==Unit.NOTSET){
			return -1;
		}		
		return fontSize;
	}

	public Unit getFontSizeUnit() {
		return fontSizeUnit;
	}

	/**
	 * not implemented yet
	 * @param size
	 * @param unit (px only for now)
	 */
	public void setFontSize(int size, Unit unit) {
		fontSize = size;
		fontSizeUnit = unit;
		//setting the font size might change the layout a lot

		layoutStyleChanged();
	}
	//------------
	
	/**
	 * Sets this widgets padding on all four sides.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * Padding should not exceed widget size/2 if size is fixed
	 * 
	 * @param padding
	 **/
	public void setPadding(float padding){
		PaddingLeft = padding;
		PaddingRight = padding;
		PaddingTop = padding;
		PaddingBottom = padding;
			
		
	//	createTextAttributeIfNeeded();
		styleAttribute.paddingLeft = padding;
		styleAttribute.paddingTop = padding;
				
		layoutStyleChanged();
	}
	
	/**
	 * Sets this widgets left padding.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * 
	 * NOTE; if using fit-area labels, padding has no effect
	 * 
	 * @param Left
	 **/
	public void setPaddingLeft(float Left){
		PaddingLeft = Left;
		
	//	createTextAttributeIfNeeded();
		styleAttribute.paddingLeft = Left;
		layoutStyleChanged();
	}
	
	/**	 
	 * Unit is assumed to be the world units of your stage
	 * This will set the shader to render any text inwards by this amount, as well as setting the top padding variable 
	 *
	 * @param Top
	 **/
	public void setPaddingTop(float Top){
		PaddingTop = Top;
		
		//createTextAttributeIfNeeded();
		styleAttribute.paddingTop = Top;
		layoutStyleChanged();
	}
	
	/**
	 * Sets this widgets Right padding.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * 
	 * @param Right
	 **/
	public void setPaddingRight(float Right){
		PaddingRight = Right;
		
		//createTextAttributeIfNeeded();
		//textStyle.paddingLeft = Left;
		layoutStyleChanged();
	}
	
	/**	 
	 * Unit is assumed to be the world units of your stage
	 * This will set the shader to render any text inwards by this amount, as well as setting the top padding variable 
	 *
	 * @param Bottom
	 **/
	public void setPaddingBottom(float Bottom){
		PaddingBottom = Bottom;
		
		//createTextAttributeIfNeeded();
		//textStyle.paddingTop = Top;
		layoutStyleChanged();
	}

	public float getPaddingLeft() {
		return PaddingLeft;
	}

	public float getPaddingTop() {
		return PaddingTop;
	}

	public float getPaddingRight() {
		return PaddingRight;
	}

	public float getPaddingBottom() {
		return PaddingBottom;
	}
	


	//---------------
	//animation stuff

	public void addTransitionState(StyleParam type, float time, Color value) {
		styleAttribute.addTransitionState(type, time, value);
		
	}
	public void addTransitionState(StyleParam type, float time, float value) {
		styleAttribute.addTransitionState(type, time, value);		
	}
	public void addTransitionState(StyleParam type, float time, Vector3 value) {
		styleAttribute.addTransitionState(type, time, value);		
	}
	/**
	 * 
	 * @param type
	 * @param time
	 * @param value    eg new Quaternion(Vector3.Z,45) for 45deg
	 */
	public void addTransitionState(StyleParam type, float time, Quaternion value) {
		styleAttribute.addTransitionState(type, time, value);		
	}
	
	
	/**
	 * 
	 * @param totalAnimationTime - in ms
	 */
	public void setTransitionLength(float totalAnimationTimeMS) {
		styleAttribute.setTransitionLength(totalAnimationTimeMS);
		
	}
	public void setTransitionIterationCount(int totalAnimationTime) {
		styleAttribute.setTransitionIterationCount(totalAnimationTime);
		
	}
	public void debugTransitionStates(){		
		Log.info(styleAttribute.debugTransitionStates());		
	}
	

	public void setPercentageIntoAnimation(float percentageIntoAnimation){
		styleAttribute.setPercentageIntoAnimation(percentageIntoAnimation);
	}

	/**
	 * updates the animation, if any
	 * @param f
	 */
	public void updateDelta(float f) {
		styleAttribute.updateDelta(f);
	}

	
	/**
	 * FOR TESTING ONLY, DONT CHANGE SETTINGS ON THE RETURNED ATTRIBUTE
	 * @return
	 */
	public GwtishWidgetShaderAttribute getWidgetShaderAttribute() {
		return styleAttribute;
	}


	BitmapFont usedFont = FontHandling.standdardFont;
	
	
	/**
	 * new; get font to use
	 */
	public BitmapFont getFont() {
		return usedFont;
	}

	/**
	 * set font to use
	 */
	public void setFont(BitmapFont usedFont) {
		this.usedFont = usedFont;
		layoutStyleChanged();
	}

	
	
	static public BitmapFont getDefaultFont() {
		return FontHandling.standdardFont;
	}
	

	
	
}
