package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lostagain.nl.GWTish.Style.TextAlign;
import com.lostagain.nl.GWTish.Style.Unit;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute.TextScalingMode;

/**
 * A Libgdx label that will eventually emulate most of the features of a GWT label (ish. VERY ish.)<br>
 * <br>
 * The most significant thing here though is we enable it to use distance mapped fonts in a 3d view. <br>
 * This lets things look sharp regardless of how close the camera gets.<br>
 * <br>
 * For details of how this works see;<br>
 * <br>
 * https://github.com/libgdx/libgdx/wiki/Distance-field-fonts<br>
 * <br>
 * and the original Valve paper;<br>
 * <br>
 * http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf<br>
 * <br>
 * <br> * 
 * With the DistanceFieldShader we can also emulate shadows,outlines and glows - sort of letting the label have "css styles" <br>
 * These are controlled in the objects style object, accessible with functions like .getStyle().setTextGlow(..)<br>
 *
 ***/
public class Label extends LabelBase {

	final static String logstag = "GWTish.Label";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag

	/**
	 * The name of the label material
	 */
	public static final String LABEL_MATERIAL = "LabelMaterial";

	/**
	 * contents of this text label;
	 */
	String contents = "TextNotSetError";


	//static int LabelNativeWidth =512;
	//static int LabelNativeHeight=512;



	/**
	 * flags if we need to do any initial setup work or not.
	 * (might be unnecessary)
	 */
	static Boolean labelSetupDone = false;

	/**
	 * we can optionally interpret html like br tags as newlines
	 */
	private boolean interpretBRasNewLine=false;

	public boolean isInterpretBRasNewLine() {
		return interpretBRasNewLine;
	}
	/**
	 * we can optionally interpret html like br tags as newlines
	 * defaults to false. Does not refresh text
	 */
	public void setInterpretBRasNewLine(boolean interpretBRasNewLine) {
		this.interpretBRasNewLine = interpretBRasNewLine;
	}

	private boolean interpretBackslashNasNewLine=true;

	public boolean isInterpretBackslashNasNewLine() {
		return interpretBackslashNasNewLine;
	}
	/**
	 * interpret \n as newlines
	 * Note;  \r\n or \n\r will be converted to just \n
	 * 
	 * defaults to true	
	 * @param interpretBackslashNasNewLine
	 */
	public void setInterpretBackslashNasNewLine(boolean interpretBackslashNasNewLine) {
		this.interpretBackslashNasNewLine = interpretBackslashNasNewLine;
	}

	//defaults
	BitmapFont defaultFont;

	/** default scale factor of the text **/
	float ModelScale = 1.0f;

	enum SizeMode {
		/** label is a fixed, specified size and everything is scaled to fit.
		 *  Padding does not increase size. **/
		Fixed,
		/** label expands till it contains the text **/
		ExpandXYToFit,
		/**
		 * Expands variably with new lines, but wraps to the width.
		 * This is somewhat like a HTML DIV with a style width specified
		 */
		ExpandHeightMaxWidth,

	}

	SizeMode labelsSizeMode = SizeMode.ExpandXYToFit;


	/**
	 * under fixed width mode this marks the maximum width of the widget. Any expansion of the text beyond it will result
	 * in word wrapping.
	 * Under fixed mode this will be the size regardless of its its all needed or not.
	 */
	float maxWidth = -1; //default for no max
	/**
	 * Not used unless we are on fixed mode, then its the height.
	 */
	float maxHeight = -1; //default for no max

	/**
	 * The text alignment used to last generate the text texture
	 */
	private TextAlign lastUsedTextAlignment;



	/**
	 * 
	 * @param contents
	 */
	public Label (String contents){ 
		this(contents, false,true, -1,-1, SizeMode.ExpandXYToFit, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot with no max width
	}
	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 */
	public Label (String contents,float MaxWidth){ 
		this( contents, false, true,MaxWidth,-1, SizeMode.ExpandHeightMaxWidth, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot
	}

	/**
	 * fixed size label - texture scales into it (non-gwt like)
	 * @param contents
	 * @param Width
	 * @param Height
	 * @param modelalignment
	 * @param textalign
	 */
	public Label(String contents, float Width, float Height, MODELALIGNMENT modelalignment, TextAlign textalign) {
		this( contents, false, true,Width, Height, SizeMode.Fixed, modelalignment,textalign); //defaults to top left alignment of pivot
	}
	/**
	 * * fixed size label - texture scales into it (non-gwt like)
	 * @param contents
	 * @param MaxWidth
	 * @param MaxHeight
	 */
	public Label (String contents,float MaxWidth,float MaxHeight){ 
		this( contents, false,true, MaxWidth,MaxHeight, SizeMode.Fixed, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot
	}

	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 *  @param MaxHeight
	 */
	public Label (String contents,float Width,float Height, MODELALIGNMENT alignment){ 
		this( contents, false,true, Width,Height, SizeMode.Fixed, alignment,TextAlign.LEFT); 
	}

	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 */
	public Label (String contents,float MaxWidth,MODELALIGNMENT alignment){ 
		this( contents, false,true, MaxWidth,-1, SizeMode.ExpandHeightMaxWidth, alignment,TextAlign.LEFT); 
	}
	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 * @param modelAlignement
	 */
	public Label (String contents,boolean interpretBRasNewLine,boolean interpretNLasNewLine,float MaxWidth,float MaxHeight, SizeMode sizeMode, MODELALIGNMENT modelAlignement, TextAlign textAlignment){ 

		super(generateObjectData(true, true, contents, interpretBRasNewLine,interpretNLasNewLine,sizeMode, MaxWidth, MaxHeight, modelAlignement,textAlignment,null));
		super.setStyle(this.getTextMaterial()); //no style settings will work before this is set
		contents = standardiseNewlines(contents);
		
		this.lastUsedTextAlignment = textAlignment;
		this.userData="label_"+contents;


		if (!labelSetupDone){
			firstTimeSetUp();
			labelSetupDone=true;
		}


		labelsSizeMode = sizeMode;
		this.maxWidth = MaxWidth;
		this.maxHeight = MaxHeight;
		this.contents=contents;

		Material materialAccordingToStyle = this.getStyle().getMaterial();
		Material materialAccordingToGetMaterial = this.getTextMaterial();

		//debug checks
		if (materialAccordingToStyle!=materialAccordingToGetMaterial){
			Log.info( "materials dont match!"); 
		}
		if (materialAccordingToStyle==null){
			Log.info( "materialAccordingToStyle is null"); 
		}
		if (materialAccordingToGetMaterial==null){
			Log.info( "materialAccordingToGetMaterial is null"); 
		}
		//--------------------------------------------------------


		GwtishWidgetShaderAttribute matttest = (GwtishWidgetShaderAttribute) materialAccordingToGetMaterial.get(GwtishWidgetShaderAttribute.ID);

		Log.info( "<---------------------------------------------text :   "+contents); 
		Log.info( "matt test for label has Text:"+matttest.hasText()); 
		Log.info( "text col:   "+matttest.textColour); 

		if (sizeMode==SizeMode.Fixed){
			//calc needed shader scaleing. Fixed size mode enlarges and shrinks texture to fit model - but does so in the shader, not by changing the underlying texture resolution
			//(which is pointless - as you could only ever lose information and make it look worse.)

			calculateCorrectShaderTextScale(1.0f); //default to a 1.0f, which means we use the native bitmap font size 

			//old;
			//if fixed mode we might need to pad the texture to ensure it keeps its ratio			
			//setPaddingToPreserveTextRatio(TextAlign.LEFT,  maxWidth , maxHeight, this.textureSize.x, this.textureSize.y);

		}

	}


	/*
	 * Generates a label with the specified contents, auto expanding its size to fit
	 * Newlines in the content are respected 
	 * 
	 * @param contents
	 *
	public Label (String contents){
		super(generateObjectData(true, true, contents, SizeMode.ExpandXYToFit,-1));//No max width

		super.setStyle(this.getMaterial(LABEL_MATERIAL));

		labelsSizeMode = SizeMode.ExpandXYToFit;
		this.maxWidth = -1;
		this.contents=contents;

		if (!labelSetupDone){
			firstTimeSetUp();
			labelSetupDone=true;
		}


	}
	 */




	/**
	 * The object data needed on creation is just the background mesh instance and the cursor position.
	 * This shouldn't need to be run outside the objects first creation, allthough a resize might be required.
	 * 
	 * After its created everything should eventually be alterable separately without full recreation
	 * 
	 * @param regenTexture - probably can be removed
	 * @param regenMaterial
	 * @param contents
	 * @param labelsSizeMode
	 * @param maxWidth
	 * @param alignment - the meshs pivot alignment
	 * @param textAlignment 
	 * @return
	 */
	private static backgroundAndCursorObject generateObjectData(
			boolean regenTexture,
			boolean regenMaterial,
			String contents,
			boolean interpretBRasNewLine,
			boolean interpretNLasNewLine,
			SizeMode labelsSizeMode,
			float maxWidth ,
			float maxHeight,
			MODELALIGNMENT alignment, 
			TextAlign textAlignment,
			Style style) {


		TextureAndCursorObject textureData = null;


		BitmapFont font;     //     = getEffectiveFont(style);
		float NativeToSceneRatio;// = getNativeToSceneResizeRatio(style, font);

		if (contents.isEmpty()){
			//if no contents we dont bother getting the font
			//(this allows the creation of empty labels before the font is ready)
			font=null;
			NativeToSceneRatio=1.0f; //randomvalue, no real effect with empty texture
		} else {
			
			font          = getEffectiveFont(style);
			NativeToSceneRatio = getNativeToSceneResizeRatio(style, font);

		}
		if (interpretNLasNewLine){
		contents = standardiseNewlinesStatic(contents);
		}
		int startFromX =0;
		int startFromY =0;
		Pixmap addToThis = null;


		if (regenTexture){			

			textureData = generateTexture(labelsSizeMode, contents,interpretBRasNewLine,interpretNLasNewLine,
					NativeToSceneRatio,maxWidth,textAlignment,style,font,
					startFromX,startFromY,addToThis); //left default	

		}

		Texture newTexture = textureData.textureItself;

		newTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders

		//DistanceFieldAttribute textStyle = null;
		//GwtishWidgetDistanceFieldAttribute textStyle = null;

		//if (textStyle==null){
		//	textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		GwtishWidgetShaderAttribute textStyle = new GwtishWidgetShaderAttribute(GwtishWidgetShaderAttribute.presetTextStyle.whiteWithShadow);

		//assign texture to special shader
		textStyle.distanceFieldTextureMap = newTexture;
		//}


		//we normally get the model size from the generated material unless its specified as fixed
		float textureSizeX = newTexture.getWidth();
		float textureSizeY = newTexture.getHeight();

		float SizeX = textureSizeX * (1.0f/NativeToSceneRatio);
		float SizeY = textureSizeY * (1.0f/NativeToSceneRatio);

		if (labelsSizeMode==SizeMode.Fixed){

			SizeX = maxWidth;			
			SizeY = maxHeight;

			textStyle.setTextScaleing(TextScalingMode.fitPreserveRatio); 

			Log.info( "fitarea detected texture size mode set as:"+textStyle.textScaleingMode); 
			//setPaddingToPreserveTextRatio(TextAlign.LEFT,  maxWidth , maxHeight, sizeX, sizeY);

		}

		//TODO:also ensure its bigger then the minimum size?
		//


		Material mat = 	
				new Material(LABEL_MATERIAL,
						new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
						//TextureAttribute.createDiffuse(newTexture),
						//	ColorAttribute.createDiffuse(defaultBackColour), //needs to be passed into this function
						textStyle);



		Model newModel = Widget.generateBackground(SizeX, SizeY, mat, alignment);

		//GwtishWidgetShaderAttribute matttest2 = (GwtishWidgetShaderAttribute) newModel.getMaterial(LABEL_MATERIAL).get(GwtishWidgetShaderAttribute.ID);

		//Log.info( "1fitarea set as:"+matttest2.textScaleingMode); 
		//Log.info( "paddingLeft:   "+matttest2.paddingLeft); 



		backgroundAndCursorObject setupData = new backgroundAndCursorObject(newModel,
				textureData.Cursor.x,
				textureData.Cursor.y,
				new Vector2(textureSizeX,textureSizeY),
				textureData.rawPixelData,
				textureData.newline_indexs
				);


		GwtishWidgetShaderAttribute testAttribute = (GwtishWidgetShaderAttribute)setupData.object.getMaterial(LABEL_MATERIAL).get(GwtishWidgetShaderAttribute.ID);

		Log.info( "<---------------------------------------------setupData :   "+testAttribute.hasText()); 



		return setupData;


	}

	private static BitmapFont getEffectiveFont(Style style) {

		BitmapFont rawFont = Style.getDefaultFont(); // FontHandling.standdardFont;
		BitmapFont adjustedFont = rawFont;

		if (style!=null){


			rawFont = style.getFont(); // FontHandling.standdardFont;

			//make a copy of the font so we can customize the line height data
			//probably not very efficient?
			adjustedFont = new BitmapFont(
					rawFont.getData(),
					rawFont.getRegion(), 
					rawFont.usesIntegerPositions());

			float LineHeight = (float) style.getLineHeightValue();
			Style.Unit LineHeightUnit = style.getLineHeightUnit();

			//we only support PX or Unitless at the moment		
			if (LineHeightUnit == Style.Unit.PX){
				adjustedFont.getData().setLineHeight(LineHeight);
			}
			if (LineHeightUnit == Style.Unit.UNITLESS){				
				//only supported if font size is pixels
				if (style.getFontSizeUnit() == Unit.PX){


					//double fontsize = style.getFontSize(); //wait, we  dont want real size as the texture map is generated at native resolution regardless of what the display size will be.
					double fontsize = FontHandling.getNativeFontSize(rawFont) ;//rawFont.getLineHeight();
					double effectiveSize = fontsize*LineHeight;

					Log.info( "native fontsize:   "+fontsize+"   LineHeight:"+LineHeight); 

					Log.info( "effective lineheight:   "+effectiveSize); 
					adjustedFont.getData().setLineHeight((float) effectiveSize);

				}
			}

		}
		return adjustedFont;
	}

	/**
	 * 
	 * @param text
	 * @param NativeSceneRatio
	 * @param effectiveMaxWidth
	 * @param align
	 * @param stylesettings
	 * @param font
	 * @return
	 */
	static public TextureAndCursorObject generatePixmapForShader(
			String text, 
			boolean interpretBRasNewLine,
			boolean interpretNLasNewLine,
			float NativeSceneRatio, 
			float effectiveMaxWidth,
			TextAlign align, 
			Style stylesettings,
			BitmapFont font,
			int startFromX,
			int startFromY,
			Pixmap addToThis
			) {

		//  BitmapFontData data = DefaultStyles.standdardFont.getData();

		GlyphLayout layout = getNewLayout(text, interpretBRasNewLine, interpretNLasNewLine,NativeSceneRatio, effectiveMaxWidth, align, font);
		TextureAndCursorObject textureDAta = generateTexture_fromLayout(layout, font,startFromX,startFromY,addToThis); 

		//Font size  trying to figure out

		//Note; in order to scale text to fit in other modes we still render at the native size, but dont effect the mesh size
		//the texture will then auto-scale into the space. 
		//
		//However...this "solution" doesn't deal with maxwidth because the width of the characters determines how many per line can fit.
		//So this might take some thinking about.
		//a) there should be a minimum texture pixel size per character, regardless of final visual size
		//b) for the wrapping to work, however, at least the correct ratio needs to end up in the layout?
		//ii) or maybe we change the width proportionally the other way? (then when its sized up to the widget size it will be correct? err...seems weird but should work? )
		//
		//font size is stored in style right now, so whatever we do we use that 




		//old;


		//   float currentWidth  = layout.width;
		//   float currentHeight = layout.height;
		/*
	    for (GlyphRun grun : layout.runs) {

	    	Log.info("______________run width:"+grun.width+" at "+grun.y);	
	    	String runstring = "";

	    	for (Glyph g : grun.glyphs) {

	    	//	Log.info("___g:"+g.toString());
	    		runstring=runstring+g.toString();

			}
	    	Log.info("___runstring:"+runstring);



		}
		 */

		//	Log.info("______________predicted size = "+currentWidth+","+currentHeight);

		//	TextureAndCursorObject textureDAta = generateTexture( text, 0, 0,  sizeratio, true,maxWidth); //note zeros as size isn't used




		return textureDAta;

	}
	private static GlyphLayout getNewLayout(String text, 
			boolean interpretBRasNewLine, boolean interpretNLasNewLine,
			float NativeSceneRatio,
			float effectiveMaxWidth, TextAlign align, BitmapFont font) {
		GlyphLayout layout = new GlyphLayout();	    
		//layout.setText(DefaultStyles.standdardFont, text);



		//if maxWidth is zero or -1 then we dynamically work it out instead
		//this means the texture will just be as horizontally long as it needs too, without forced wrapping
		if (effectiveMaxWidth<1){
			layout.setText(font, text); 
			effectiveMaxWidth = layout.width; //gets width at native font size
		}	else {

			//if the effectiveMaxWidth is set however,we might have to shrink it by the NativeSceneRatio
			//This is because font size effects when wraps have to happen.
			//Bigger font = bigger letters = must wrap sooner
			//
			//so this "fake shrunk" newwidth is used to determine the wrapping of the font, which is then scaled up again at the end.
			//Note; the actual per-character pixel size of the map never changes, we are just using this to cropit different

			effectiveMaxWidth = effectiveMaxWidth * NativeSceneRatio;


		}



		Log.info(text+"___layout width:"+effectiveMaxWidth);
		Log.info(text+"___layout line height:"+font.getLineHeight());

		//convert from text align to layout align
		int layoutAlignment = Align.center;

		switch (align) {
		case CENTER:
			layoutAlignment = Align.center;
			break;
		case JUSTIFY:		    
			Log.info("___JUSTIFY NOT SUPPORTED. DEFAULTING TO CENTER");
			layoutAlignment = Align.center;
			break;
		case LEFT:
			layoutAlignment = Align.left;
			break;
		case RIGHT:
			layoutAlignment = Align.right;
			break;
		default:
			layoutAlignment = Align.center;
			break;
		}

		if (!interpretNLasNewLine){
			text=text.replace("\n", "");	
		}
		//if we are set to interpret <br> as newline, then we replace them with \n
		if (interpretBRasNewLine){	
			text=text.replace("<br>", "\n");					
		}


		layout.setText(font, text, Color.BLACK, effectiveMaxWidth, layoutAlignment, true); //can't centralize without width
		return layout;
	}


	//note implemented yet
	//private static Vector2 caclculateModelSizeFromTexture(float getNativeToSceneResizeRatio, Style stylesettings) {

	//textureSize.x, textureSize.y

	//	Vector2 test = new Vector2(0,0);

	//	return test;

	//}

	/**
	 * The ratio between the internal pixmap font size and the one requested in the style as the output size.
	 * The actual widget size should thus be;
	 * 
	 * (texturemap size * ratio) + padding
	 * 
	 * @param stylesettings
	 * @param font
	 * @return
	 */
	private static float getNativeToSceneResizeRatio(Style stylesettings, BitmapFont font) {
		float ShrinkWidthRatio = 1.0f;

		//if font size is set we work out the changes here
		if (stylesettings!=null && stylesettings.fontSizeUnit!=Unit.NOTSET){

			///font.getData().setScale(3.5f); //scaling only effects spacing, not font size
			//how to do font size ?
			//maybe work out the difference between native and result width/height as ratio?
			//scale down the width so it wraps at the correct letters, then..umm...set the shader font scale up? to....err.something?		

			//lets try getting te ratio of the native map height to the result height;
			float baseToAssent = font.getCapHeight()+font.getAscent();
			Log.info("__native cap+ascent:"+baseToAssent);	
			float baseToDecent = -font.getDescent();
			Log.info("__native baseToDecent:"+baseToDecent);	

			float fontHeight = baseToAssent+baseToDecent; //seems too small?
			fontHeight=32; //actual size		
			Log.info("__native fontHeight:"+fontHeight);

			double fontSizeRequested = stylesettings.getFontSize();
			Log.info("__requested fontHeight:"+fontSizeRequested+" ("+stylesettings.getFontSizeUnit()+")");
			double ratio = fontHeight/fontSizeRequested;
			ShrinkWidthRatio = (float) ratio;

			Log.info("__ratio:"+ShrinkWidthRatio); //shrink width by this amount (if fixed width)

			//now sale up again using the ratio in the shader? or just geometry

			//-------------------------
		} else {
			ShrinkWidthRatio = 1f; //default no change
		}
		return ShrinkWidthRatio;
	}



	/**
	 * new method using the layout function to give us the data needed to...well..layout the glyphs
	 * @param layout
	 * @param standdardFont - should match the one used to generate the layout
	 * @return
	 **/
	static public TextureAndCursorObject generateTexture_fromLayout(GlyphLayout layout, BitmapFont standdardFont,
			int startFromX,int startFromY,Pixmap addToThis){

		//create according to predicted size 
		int currentWidth  = (int) layout.width;
		int currentHeight = (int) (layout.height+standdardFont.getCapHeight()); //not sure if cap  height is correct
		//.info("_________standdardFont.getCapHeight()="+standdardFont.getCapHeight()+" ");

		Pixmap textPixmap;

		//new pixmap with old data
		if (addToThis!=null){

			int newRequiredWidth  = startFromX+currentWidth;			 
			int newRequiredHeight = startFromY+currentHeight;

			//if new width or height is bigger we need to make a new map
			if (newRequiredWidth>addToThis.getWidth() || newRequiredHeight>addToThis.getHeight()){

				//should pick the biggest values for both dimensions
				if (newRequiredWidth<addToThis.getWidth()){
					newRequiredWidth=addToThis.getWidth();
				}
				if (newRequiredHeight<addToThis.getHeight()){
					newRequiredHeight=addToThis.getHeight();
				}

				Log.info("_________creating new pixmap of size:"+newRequiredWidth+","+newRequiredHeight+" ");

				textPixmap = new Pixmap(newRequiredWidth, newRequiredHeight, Format.RGBA8888);
				textPixmap.drawPixmap(addToThis, 0, 0);

				//dispose old
				addToThis.dispose();

			} else {
				Log.info("_________reuse old map!:"+newRequiredWidth+","+newRequiredHeight+" ");

				//reuse old map!
				textPixmap=addToThis;
			}


		} else {
			Log.info("________New pixmap with no data:"+currentWidth+","+currentHeight+" ");

			//new pixmap with no data
			textPixmap = new Pixmap(currentWidth, currentHeight, Format.RGBA8888);

		}

		Log.info("_______ ");


		Pixmap fontPixmap = getFontPixmap(standdardFont);

		int currentTargetX = 0;
		int currentTargetY = 0;

		float advance = 0;
		
		int current_index = 0; //used only to keep track of newlines
		ArrayList<Integer> newline_indexs = new ArrayList<Integer>();

		Log.info("_______ "+startFromX+" "+startFromY);//0,0
		Log.info("_______runs "+layout.runs.size);// Firefox crashs after this point
		//now loop over each run of letters. 
		for (GlyphRun grun : layout.runs) {

			String runstring = "";
			float currentRunX= 0; 
			advance = 0;

			//store as newline location
			if (current_index>0){
				newline_indexs.add(current_index);
			}
			
			//now draw each letter
			//	Log.info("_________grun="+grun.x+","+grun.y+" ");
			int i =0;
			for (Glyph glyph : grun.glyphs) {
				current_index++;
				
				advance = grun.xAdvances.get(i);
				i++;
				currentRunX=currentRunX   +   advance    ; //1 should not be needed

				currentTargetX = startFromX+ (int)grun.x  + (int)currentRunX;		//used to use startfrom to add to the pixmap, now we add to the texture instead		
				currentTargetY = startFromY+ (int)grun.y;

			 	int destX = currentTargetX + glyph.xoffset;
				int destY = currentTargetY + glyph.yoffset;
//				
//				if (destX<0){
//					destX=0;
//				}
//
//				if (destY<0){
//					destY=0;
//				}
				Log.info("___g:"+glyph.toString());//$ in chrome, space in FF

				Log.info("___textPixmap:"+textPixmap.getWidth()+","+textPixmap.getHeight()+", "+
				"___fontPixmap:"+fontPixmap.getWidth()+","+fontPixmap.getHeight()+", "+
				        +glyph.srcX+", "+glyph.srcY+", "+glyph.width+", "+glyph.height+", "+
						destX+", "+
						destY+", "+						
						glyph.width+", "+glyph.height);	//is minus 7 offsetx ok?
				textPixmap.drawPixmap(
						fontPixmap,
						glyph.srcX,
						glyph.srcY, 
						glyph.width, 
						glyph.height,
						destX,
						destY,						
						glyph.width, 
						glyph.height);

				//	Log.info("___g:"+g.toString());

				Log.info("___g:"+glyph.toString());
				runstring=runstring+glyph.toString();

			}
			if (grun.glyphs.size>0){
				advance = grun.xAdvances.get(i);
				i++;
				currentRunX=currentRunX+advance;
				Log.info("______________last run width:"+grun.width+" drawn was till "+currentRunX);	
			}
			//Log.info("___runstring drawen:"+runstring);



		}

		//work out final cursor position, which is currentTargetX + last advance
		int cx = (int) (currentTargetX+advance);
		int cy = (int) (currentTargetY); 

		//PixmapAndCursorObject pixmapAndCursor = new PixmapAndCursorObject(textPixmap, cx, cy);

		Texture textureData; 
		//	if (startFromX==0 && startFromY==0 || addToThis==null){
		textureData = new Texture(textPixmap);
		/*
		} else {
			//add pixmap to existing texture
			//textureData = new Texture(pixmapAndCursor.textureItself);

			//enlarge?
			//
			Log.info("______________textPixmap size: "+textPixmap.getWidth()+","+textPixmap.getHeight());	
			Log.info("______________adding at: "+startFromX+","+startFromY);	
			Log.info("______________texture size: "+addToThis.getWidth()+","+addToThis.getHeight());	

		//	textPixmap.fill();			

			//addToThis.getTextureData().prepare();
		//	Pixmap existingTexture = addToThis.getTextureData().consumePixmap();


			addToThis.draw(textPixmap, startFromX, startFromY); //todo: startFromY is wrong?? seems to draw offscreen if set

			//note; drawing doesnt work if ANY of it is outside the bounds

			//add the startfroms to the cursor pos
			cx=startFromX+cx;
			cy=startFromY+cy;

			textureData=addToThis;
		}*/
		
		newline_indexs.add(current_index);

		TextureAndCursorObject textureAndCursorObject = new TextureAndCursorObject(
				textureData,
				cx,
				cy,
				textPixmap,
				newline_indexs);

		//textPixmap.dispose();


		Log.info("_______generateTexture_fromLayout done");
		return textureAndCursorObject;


	}


	static HashMap<BitmapFont,Pixmap> fontCache = new HashMap<BitmapFont,Pixmap>();

	/**
	 * Gets a pixmap of the specified font, using a cache if its already been created before
	 * @param standdardFont
	 * @return
	 */
	private static Pixmap getFontPixmap(BitmapFont standdardFont) {

		Pixmap	fontPixmap = fontCache.get(standdardFont);

		if (fontPixmap==null){		
			BitmapFontData data = standdardFont.getData(); 
			fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0])); 
			fontCache.put(standdardFont, fontPixmap);
		}

		return fontPixmap;
	}





	static public TextureAndCursorObject generateTextureNormal(String text,int TITLE_WIDTH,int TITLE_HEIGHT, float sizeratio) {

		TextureAndCursorObject textureDAta = generateTexture( text, TITLE_WIDTH, TITLE_HEIGHT,  sizeratio,false,-1);

		return textureDAta;
	}

	static public TextureAndCursorObject generateTexture(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit, float maxWidth) {

		PixmapAndCursorObject data = generatePixmap(text, DefaultWidth, DefaultHeight, sizeratio, expandSizeToFit,maxWidth);
		TextureAndCursorObject textureAndCursorObject = new TextureAndCursorObject(
				new Texture(data.textureItself),
				data.Cursor.x,data.Cursor.y,
				data.textureItself,
				null
				);
		//	data.textureItself.dispose();


		return textureAndCursorObject;
	}

	static public PixmapAndCursorObject generatePixmap(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit, float maxWidth) {

		//if maxWidth = -1 then theres no max width

		String Letters    = text;
		Pixmap textPixmap = new Pixmap(DefaultWidth, DefaultHeight, Format.RGBA8888);

		if (!expandSizeToFit){

			textPixmap = new Pixmap(DefaultWidth, DefaultHeight, Format.RGBA8888);

		} else {
			//start arbitrarily big (this will be fixed later
			textPixmap = new Pixmap(500, 500, Format.RGBA8888);

		}

		//	textPixmap.setColor(1, 0, 0, 1);					
		//	textPixmap.drawRectangle(3, 3, TITLE_WIDTH-3, TITLE_HEIGHT-3);

		BitmapFontData data = FontHandling.standdardFont.getData(); //new BitmapFontData(Gdx.files.internal(data.imagePaths[0]), true);

		Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));

		// draw the character onto our base pixmap

		int totalwidth=0;
		int current_testedwidth=0;

		int currentX=0;

		float scaledown = sizeratio;

		//Glyph defaultglyph = data.getGlyph(Letters.charAt(0));


		//int totalheight=defaultglyph.height+9;

		Log.info("scaledown="+scaledown);
		double lastremainder =0;
		int yp=0;

		int destX = 0;
		int destY = 0;
		int cheight = 0;

		//the bottom right corner of the texture map thats used
		int biggestX = 0;
		int biggestY = 0;

		for (int i = 0; i < Letters.length(); i++) {

			Glyph glyph = data.getGlyph(Letters.charAt(i));

			if (glyph==null){
				Log.info("_______(current glyph not valid not in character set, setting glyph to space)");
				glyph=data.getGlyph(' '); //temp


			}


			int cwidth =  (int)(glyph.width  * scaledown);
			cheight = (int)(glyph.height * scaledown);

			int yglyphoffset = (int) (glyph.yoffset * scaledown);

			destX = 0+currentX+glyph.xoffset;


			//Log.info("Letters.charAt(i)="+Letters.charAt(i));

			if (Letters.charAt(i) == '\n' || (destX>maxWidth && maxWidth!=-1) ){

				//new line  NB; defaultglyph.height seems to be zero for some reason
				yp=(int) (yp+(data.lineHeight* scaledown)+5);
				currentX=0;
				destX=glyph.xoffset;
				lastremainder=0;
				//Log.info("______________adding line. (yp now="+yp+") next char is:"+Letters.charAt(i));

				//we skip \n as we don't want to really write that
				if (Letters.charAt(i) == '\n'){
					continue;
				}
				//---
			}

			destY = 0+(yp+(yglyphoffset ));



			//note if we are going to go of the edge, and we are on expand mode, we have to quickly get a bigger map to work in
			boolean hadToEnlarge = false;
			int cbiggestX = destX+cwidth;
			int cbiggestY = destY+cheight;

			//ensure its bigger then anything we have already (remember cbiggest is just the current lines largest x value, not the overall for all lines)
			if (cbiggestX>biggestX){
				biggestX = cbiggestX;
			}
			if (cbiggestY>biggestY){
				biggestY = cbiggestY;
			}
			if (expandSizeToFit && (biggestX>textPixmap.getWidth())){
				Log.info("______________x ("+biggestX+") out of range, having to make canvas bigger");
				//we just double the X size, as we are cropping later anyway
				cbiggestX=biggestX*2;
				hadToEnlarge =  true;					
			} else {
				cbiggestX = textPixmap.getWidth();
			}

			if (expandSizeToFit && (biggestY>textPixmap.getHeight())){
				Log.info("______________y ("+cbiggestY+") out of range, having to make canvas bigger");
				//we just double the Y size, as we are cropping later anyway
				cbiggestY=biggestY*2;
				hadToEnlarge =  true;					
			} else {
				cbiggestY = textPixmap.getHeight();
			}

			if (hadToEnlarge){
				textPixmap = sizePixmapTo(textPixmap, cbiggestX, cbiggestY);
			}
			//--------------------


			textPixmap.drawPixmap(
					fontPixmap,
					glyph.srcX,
					glyph.srcY, 
					glyph.width, 
					glyph.height+1,
					destX,
					destY,//+(TILE_HEIGHT - (cheight)) / 2,						
					cwidth, 
					cheight);



			double newprecisepos =  ((glyph.xadvance+2)  * scaledown)+lastremainder;//glyph.width+3
			lastremainder = newprecisepos - Math.floor(newprecisepos);
			int newpos = (int) (Math.floor(newprecisepos));
			//	Log.info("newpos="+newpos);
			//	Log.info("lastremainder="+lastremainder);
			currentX=currentX + newpos;
		}

		if (expandSizeToFit){
			//crop down to final size
			// biggestX = currentX;
			// biggestY = destY+cheight;

			Log.info("______________final cropped size="+biggestX+","+biggestY);

			textPixmap = sizePixmapTo(textPixmap, biggestX, biggestY);

			//	LabelNativeWidth  = biggestX;
			//LabelNativeHeight = biggestY;

		}

		//0,0 should be current cursor position after this update
		return new PixmapAndCursorObject(textPixmap,0,0);

	}


	private static Pixmap sizePixmapTo(Pixmap textPixmap, int biggestX, int biggestY) {
		Pixmap croppedPixMap = new Pixmap(biggestX, biggestY, Format.RGBA8888);
		croppedPixMap.drawPixmap(textPixmap, 0, 0);

		textPixmap.dispose();
		textPixmap = croppedPixMap;
		return textPixmap;
	}

	/**
	 * Sets the text and regenerates the texture 
	 **/
	public void setText(String text){
		text = standardiseNewlines(text);
		
		this.contents=text;

		//todo; check if we can add instead?

		regenerateTexture(text,null);

	}
	
	public String standardiseNewlines(String text) {
		if (interpretBackslashNasNewLine){
			text = standardiseNewlinesStatic(text);			
		}
		return text;
	}
	static String standardiseNewlinesStatic(String text) {
		text=text.replaceAll("\\r\\n|\\r|\\n", "\n");
		return text;
	}

	/**
	 * if possible, adds text to existing text rather then redrawing the lot.
	 * sometimes a redraw is needed, however
	 * @param text
	 */
	public void addText(String text) {
		//if empty string ignore
		if (text.isEmpty()){
			return; // no op
		}
		text = standardiseNewlines(text);
		this.contents=contents+text;

		Log.info("adding: "+text+" to existing text");

		boolean textWraps=false;

		//	if (labelsSizeMode==SizeMode.Fixed || labelsSizeMode==SizeMode.ExpandXYToFit){ //these things never wrap

		//	Log.info("(label doesnt wrap) ");
		//		textWraps=false;
		//	} else {
		//we might still wrap, but we need to work it out based on if we have a new height

		//get new predicted size based on existing settings
		float effectiveMaxWidth = maxWidth;
		if (maxWidth!=-1){
			effectiveMaxWidth = maxWidth - (this.getStyle().getPaddingLeft() + this.getStyle().getPaddingRight());
		}
		BitmapFont font          = getEffectiveFont(this.getStyle());
		float NativeToSceneRatio = getNativeToSceneResizeRatio(this.getStyle(), font);

		GlyphLayout layout = getNewLayout(contents, interpretBRasNewLine,interpretBackslashNasNewLine, NativeToSceneRatio, effectiveMaxWidth, this.lastUsedTextAlignment, font);

		int newheight = (int) (layout.height +font.getCapHeight());
		int newwidth = (int) layout.width;

		Log.info(" new height = "+newheight);
		Log.info(" current text height = "+textureSize.y);

		//notes;
		//cursor y seems to slowly get displaced (ie, a few pixels too big each time)
		//also x might be too small
		if (newheight>textureSize.y){
			Log.info(" size changed, so we need to regenerate ");
			textWraps=true;
		} else {
			textWraps=false;
		}

		//	}

		if (textWraps){

			//get rid of old texture
			//((TextureAttribute)infoBoxsMaterial.get(TextureAttribute.Diffuse)).textureDescription.texture.dispose();


			Log.info(" regenerating texture ");
			regenerateTexture(contents,null);

		} else {
			Log.info(" adding to texture ");

			//	Texture addToExisting = new Texture(newwidth, newheight, Format.RGBA8888);
			//	addToExisting.draw(super.currentPixmap, 0, 0);


			regenerateTexture(text,super.currentPixmap);
		}



	}



	/**
	 * 
	 */
	private void regenerateTexture(String text,Pixmap addToExisting) {

		TextAlign align = this.getStyle().getTextAlignment();
		lastUsedTextAlignment = align;

		//note; we subtract the left/right padding from maxwidth to get the required width of the texture itself
		float effectiveMaxWidth = maxWidth;
		if (maxWidth!=-1){
			effectiveMaxWidth = maxWidth - (this.getStyle().getPaddingLeft() + this.getStyle().getPaddingRight());
		}


		BitmapFont font = getEffectiveFont(this.getStyle());
		float NativeToSceneRatio = getNativeToSceneResizeRatio(this.getStyle(), font);

		Log.info("_________regenerating texture; labelsSizeMode:"+labelsSizeMode+" (width="+effectiveMaxWidth+")");

		int startFromX =0;
		int startFromY =0;

		if (addToExisting!=null){
			startFromX =(int) super.Cursor.x;
			startFromY =(int) super.Cursor.y;
			Log.info("Cursor is currently at: "+startFromX+","+startFromY);
		}


		TextureAndCursorObject textureAndData = generateTexture(
				labelsSizeMode, 
				text, //contents
				interpretBRasNewLine,
				interpretBackslashNasNewLine,
				NativeToSceneRatio,
				effectiveMaxWidth,//-1 is the default max width which means "any size"
				align,
				this.getStyle(),
				font,
				startFromX,
				startFromY,
				addToExisting); 

		Material ourMaterial = getTextMaterial();// this.getMaterial(LABEL_MATERIAL);	

		Texture newTexture = textureAndData.textureItself;

		//dispose of previous texture 
		//((TextureAttribute)infoBoxsMaterial.get(TextureAttribute.Diffuse)).textureDescription.texture.dispose();
		//

		//infoBoxsMaterial.set(TextureAttribute.createDiffuse(newTexture));


		GwtishWidgetShaderAttribute textStyleData = (GwtishWidgetShaderAttribute)ourMaterial.get(GwtishWidgetShaderAttribute.ID);

		//dispose of previous texture if there was one
		if (textStyleData!=null && textStyleData.distanceFieldTextureMap!=null){
			textStyleData.distanceFieldTextureMap.dispose();
		}

		textStyleData.distanceFieldTextureMap = newTexture;
		//


		//if (textStyle==null){
		//textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);

		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));	
		//  TextureAttribute.createDiffuse(NewTexture.textureItself)	,	
		// ColorAttribute.createDiffuse(defaultBackColour)


		float textureSizeX = textureAndData.textureItself.getWidth();
		float textureSizeY = textureAndData.textureItself.getHeight();

		if (textureSizeX>10000){
			Log.severe("Texture SizeX Over 10,000! Consider spliting label");
		}
		if (textureSizeY>10000){
			Log.severe("Texture SizeY Over 10,000! Consider spliting label");
		}

		//update stats on our texture size
		super.updateData(new Vector2(textureSizeX,textureSizeY), textureAndData.Cursor,textureAndData.rawPixelData,textureAndData.newline_indexs); //TODO: cursor position should always be set to the next position to type. Not sure textureAndData returns this yet
		//---------------------


		//new widget size (without padding, as the  setSizeAs adds it itself)
		float x = textureSizeX * (1.0f/NativeToSceneRatio);
		float y = textureSizeY * (1.0f/NativeToSceneRatio);


		Log.info("_________setting text to;"+text+" texturesize:"+textureSizeX+","+textureSizeY);
		Log.info("_________NativeToSceneRatio;"+(1.0f/NativeToSceneRatio));

		//boolean autoPadToPreserveRatio = true;
		Log.info("_________setting text to;"+text+" size:"+x+","+y);

		switch (labelsSizeMode) {
		case ExpandHeightMaxWidth:
			this.setSizeAs(x, y); //width should be locked thanks to generateTexture wrapping, but we could insert a test here to be sure? Or only set Y?
			break;
		case ExpandXYToFit:
			this.setSizeAs(x, y); 
			break;
		case Fixed:		
			Log.info("_________(fixed mode, so size doesnt change)");
			//real size should only set if not on fixed size mode. However, we do want to effect the padding as the real widget ratio might not match the text texture, so we need to pad the widget to compensate
			//setPaddingToPreserveTextRatio(align, maxWidth, maxHeight, x, y);

			break;
		}


		//ensure the text scale in the shader is correct
		//this effectively "fills" the mesh with the text and a surrounding border of padding
		calculateCorrectShaderTextScale(NativeToSceneRatio); 

	}

	/*
	//now handled in shader
	private void setPaddingToPreserveTextRatio(TextAlign align, float maxWidth , float maxHeight, float textureSizeX, float textureSizeY) {

		boolean autoPadToPreserveRatio = true;

		if (autoPadToPreserveRatio){

			Log.info("____(using padding to correct aspect ratio of text. Raw texture size is:"+textureSizeX+","+textureSizeY+")__");
			Log.info("____(using padding to correct aspect ratio of text. fixed size is:"+maxWidth+","+maxHeight+")__");

			//we need to work out which dimension needs to be scaled down more to fit 
			float diffX = maxWidth  - textureSizeX;
			float diffY = maxHeight - textureSizeY;

			//ie
			//200 - 50
			//50 - 40gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggcdddddddddddddddddddddddddddddddddddxggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg < - THIS line contributed by a visiting cat
			//+150,+10
			float newX =0;
			float newY =0;
			boolean textureSmallerInX = false;
			boolean textureSmallerInY = false;

			if (diffX>0){
				//texture size is smaller then width in X
				textureSmallerInX = true;
			}
			if (diffY>0){
				//texture size is smaller then width in X
				textureSmallerInY = true;
			}

			//if they are both smaller
			if (textureSmallerInX && textureSmallerInY){
				Log.info("____(both dimensions are smaller)__");
				//find the smaller difference and set that equal to the limit
				if (diffX<diffY){
					float textureSizeRatio = textureSizeY/textureSizeX;
					Log.info("______y needs more padding, ratio to x is"+textureSizeRatio);
					 newX = maxWidth;
					 newY = textureSizeY*textureSizeRatio;
				} else {
					float textureSizeRatio = textureSizeX/textureSizeY;
					Log.info("______x needs more padding, ratio to y is"+textureSizeRatio);
					 newX = textureSizeX*textureSizeRatio;
					 newY = maxHeight;
				}
			}


			//which is the bigger difference?
			/**
			if (diffX>diffY){
				//x needs more shrinking
				float textureSizeRatio = textureSizeY/textureSizeX;
				Log.info("______x needs more shrinking, ratio to y is"+textureSizeRatio);
				 newX = maxWidth;
				 newY = textureSizeY*textureSizeRatio;

			} else {
				//y needs more				
				float textureSizeRatio = textureSizeX/textureSizeY;
				Log.info("______y needs more shrinking, ratio to x is"+textureSizeRatio);
				 newX = textureSizeX*textureSizeRatio;
				 newY = maxHeight;
			}/

			float paddingX = maxWidth  - newX;
			float paddingY = maxHeight - newY;


			Log.info("______"+this.contents+"____paddingX="+paddingX+" paddingY="+paddingY);

			//this becomes the padding, and its applied based on alignment
			if (align == TextAlign.CENTER){

				Log.info("__________setting padding both sides="+(paddingX/2));
				this.getStyle().setPaddingLeft(paddingX/2);
				this.getStyle().setPaddingRight(paddingX/2);
			}
			if (align == TextAlign.LEFT){

				Log.info("__________padding on right="+paddingX);
				this.getStyle().setPaddingRight(paddingX);
			}
			if (align == TextAlign.RIGHT){                             
				this.getStyle().setPaddingLeft(paddingX);
			}                                                                                  



		}
	}
	 */



	/**
	 * 
	 * @param labelsSizeMode
	 * @param contents
	 * @param NativeToSceneRatio
	 * @param effectiveMaxWidth - max width of the texts texture (will wrap to this)
	 * @param align
	 * @param style
	 * @param font
	 * @return
	 */
	static private TextureAndCursorObject generateTexture(
			SizeMode labelsSizeMode,			
			String contents, 
			boolean interpretBRasNewLine,
			boolean interpretNLasNewLine,
			float NativeToSceneRatio,
			float effectiveMaxWidth,
			TextAlign align, 
			Style style,
			BitmapFont font,
			int startFromX,
			int startFromY,
			Pixmap addToThis
			) {


		TextureAndCursorObject NewTexture = null;

		//if empty we use a quick function
		if (contents.isEmpty()){
			NewTexture  = generateEmptyTexture();
			return NewTexture;

		}

		switch (labelsSizeMode) {
		case ExpandHeightMaxWidth:
			NewTexture = generatePixmapForShader(contents,interpretBRasNewLine,interpretNLasNewLine,NativeToSceneRatio,effectiveMaxWidth,align,style,font,startFromX,startFromY,addToThis); //-1 = no max width
			break;
		case Fixed:			
			//Note; the textures internal size is not related to the widgets size directly, but it needs to know
			//the final width/height ratio in order to pad the Pixmap enough to preserve its ratio.
			NewTexture = generatePixmapForShader(contents,interpretBRasNewLine,interpretNLasNewLine,NativeToSceneRatio,-1,align,style,font,startFromX,startFromY,addToThis); //-1 = no max width
			break;
			//expand to fit is also the default
		case ExpandXYToFit:
		default:
			Log.info("______________generating expand to fit text ");
			NewTexture = generatePixmapForShader(contents,interpretBRasNewLine,interpretNLasNewLine,NativeToSceneRatio,-1,align,style,font,startFromX,startFromY,addToThis); //-1 = no max width
			break;

		}

		NewTexture.textureItself.setFilter(TextureFilter.Linear, TextureFilter.Linear);//ensure mipmaping is disabled, else distance field shaders wont work
		//NewTexture.textureItself.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);//ensure mipmaping is disabled, else distance field shaders wont work
		return NewTexture;
	}

	/**
	 * should be a more optimized way to do this with zero  length strings
	 * @return
	 */
	private static TextureAndCursorObject generateEmptyTexture() {

		Pixmap textPixmap = new Pixmap(1, 1, Format.RGBA8888);
		Texture textureData = new Texture(textPixmap);


		TextureAndCursorObject textureAndCursorObject = new TextureAndCursorObject(
				textureData,
				0,
				0,
				textPixmap,null);

		return textureAndCursorObject;
	}



	static public void firstTimeSetUp(){
		//setup font size cache		
		FontHandling.cacheFontSizes();


	}







	//
	//
	//--------------
	// Styling functions below.
	// These are all subject to a lot of change
	// Especially as we
	// Try to make it as GWT-like as possible in its api

	/**
	 * sets the back color
	 * @param labelBackColor
	 */
	public void setLabelBackColor(Color labelBackColor) {
		//	labelBackColor = Color.PINK; //TEMP during testing. Currently another shader bug - the background colour isn't being used correctly for the transparancy, its only effecting the shadows blending
		//Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));

		//infoBoxsMaterial.set( ColorAttribute.createDiffuse(labelBackColor));


		super.getStyle().setBackgroundColor(labelBackColor);

	}

	/**
	 *   Gets this object's text.
	 * @return
	 */
	public String getText() {		
		return this.contents;
	}


	public Material getTextMaterial(){
		return this.getMaterial(LABEL_MATERIAL);
	}

	public Material getMaterial(){
		return this.getMaterial(LABEL_MATERIAL);
	}

	@Override
	public void setOpacity(float opacity){


		super.setOpacity(opacity);


	}

	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
		labelsSizeMode = SizeMode.ExpandHeightMaxWidth;
		layoutStyleChanged();
	}

	//regenerates the texture if the style layout is changed (ie, text alignment or padding)
	@Override
	public void layoutStyleChanged() {
		super.layoutStyleChanged();

		//If the padding has changed and we are on fixed width mode, we need to regenerate
		//the texture to ensure its still contained within the widget

		///if (this.getStyle().getTextAlignment() != lastUsedTextAlignment){
		regenerateTexture(contents,null);

		//}

		//work out a appropriate text scale for the shader so it fits
		//calculateCorrectShaderTextScale();


		//the shader takes care of text positioning, but not sizing, so we need to recalculate sizes based on mode.
		/*
		float newTotalWidth  = -1;
		float newTotalHeight = -1;	
		float newTextWidth  = -1;
		float newTextHeight = -1;	


		//different behavior based on sizemode
		switch (this.labelsSizeMode){
		case ExpandHeightMaxWidth:


			break;
		case ExpandXYToFit:


			break;
		case Fixed:
			//same totals as before
			newTotalWidth  = maxWidth;
			newTotalHeight = maxHeight;

			//new text size is the total size, minus all the padding		
			newTextWidth  = newTotalWidth  - (getStyle().getPaddingLeft()+getStyle().getPaddingRight());
			newTextHeight =	newTotalHeight - (getStyle().getPaddingTop()+getStyle().getPaddingBottom());

			//regenerate texture
			regenerateTexture(contents);

			break;
		default:
			break;

		}
		/**
		 * For fixed size labels;

Size is still w/h
Label size is (w-(left+right), h-(top+bottom)

For dynamic sized labels

Label size is  w+(leftpadding+rightpadding), h+(top+bottom)
Text size remains just h/w, however


		 */

		//		Log.info(" size now::"+this.getWidth()+","+this.getHeight());


	}
	/**
	 * work out a appropriate text scale for the shader so it fits
	 * This is most important for fixedSize mode, where the text scales to the widget
	 * 		
	 * @param nativeToSceneRatio - scales the text on the shader down or up to match requested font size (ie, 0.5 = half size)
	 */
	private void calculateCorrectShaderTextScale(float nativeToSceneRatio) {
		float textScale =1;
		if (labelsSizeMode==SizeMode.Fixed){			
			//fixed works differently, as its based on widget size, not font size			
			//the smaller ratio  - either the width or height
			float effectiveMaxWidth = this.maxWidth - (this.getStyle().getPaddingLeft()+this.getStyle().getPaddingRight());			 
			float effectiveMaxHeight = this.maxHeight- (this.getStyle().getPaddingBottom()+this.getStyle().getPaddingTop());

			textScale = Math.min(
					effectiveMaxWidth / (textureSize.x),
					effectiveMaxHeight / (textureSize.y)
					); 		

		} else {			
			textScale =  (1.0f/nativeToSceneRatio);			
		}

		this.getStyle().setTextScale(textScale);
		/*

		float widgetWidth  = this.getWidth(); //returns size with padding
		float widgetHeight = this.getHeight();

		Log.info("_________setting shader to textScale based on real size ;"+widgetWidth+","+widgetHeight);
		Log.info("_________setting shader to textScale based on texture size ;"+textureSize.x+","+textureSize.y);

		float totalPaddingWidth =  (getStyle().getPaddingLeft()+getStyle().getPaddingRight());
		if (totalPaddingWidth>widgetWidth){
			totalPaddingWidth=widgetWidth;
		}
		float totalPaddingHeight =  (getStyle().getPaddingTop()+getStyle().getPaddingBottom());
		if (totalPaddingHeight>widgetHeight){
			totalPaddingHeight=widgetHeight;
		}

		//now get the widget size without padding
		float widgetWidthWithoutPadding  = widgetWidth-totalPaddingWidth;		
		float widgetHeightWithoutPadding = widgetHeight-totalPaddingHeight;
		//---

		//the smaller ratio  - either the width or height
		float textScale = Math.min(
									widgetWidthWithoutPadding / (textureSize.x),
				                    widgetHeightWithoutPadding/ (textureSize.y)
				                   ); 

		Log.info("_________setting shader to textScale;"+textScale);

		this.getStyle().setTextScale(textScale);*/

	}



	/**
	 * disposes of textures and pixmaps used to make this label
	 */
	public void dispose(){
		super.dispose();
		super.currentPixmap.dispose();

		Material material = this.getMaterial(LABEL_MATERIAL);	
		GwtishWidgetShaderAttribute textStyleData = (GwtishWidgetShaderAttribute)material.get(GwtishWidgetShaderAttribute.ID);
		//dispose of previous texture 
		textStyleData.distanceFieldTextureMap.dispose();

		//	((TextureAttribute)material.get(TextureAttribute.Diffuse)).textureDescription.texture.dispose();

	}


	/**
	 *  br or nl
	 */
	boolean containsNewlines() {

		String labtext = this.getText();
		boolean countbr = this.isInterpretBRasNewLine();

		return containsNewlines(labtext,countbr);

	}

	static boolean containsNewlines(String labtext,boolean countbr) {

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

	/**
	 * Returns the index of the last newline.<br>
	 * Or , to be exact, the last bundles <br>
	 * <br>
	 * <br>
	 * A newline can be originally caused  by a \n , a {@literal<br>} , or auto-wrapping<br>
	 * <br>
	 * @return
	 */
	public int getLastNewLineLocation(){
		
		if (newline_indexs.size()<2){
			return -1; //There was no newlines
		}
		
		//last element of array is the length
		int internalLength = newline_indexs.get(newline_indexs.size()-1); //internal text length (not same as this.contents see below for reasons)
	
		
		Integer lastBundleIndex = newline_indexs.get(newline_indexs.size()-2); //Location of last bundle, HOWEVER:
		//The index is currently likely not correct, and we have to do a trick to fix it.
		int lastBundleIndex_fromEnd = internalLength-lastBundleIndex ;
//		Why the end? glade you asked..
//		This is because the character count internally is bassed of a string where {@literal <br>'}s have been replaced by \n
//		Therefor the indexs dont match the ones from the original string you set, or those from "getText()"
//		As a work-around we thus count from the end of the string. As we are looking for the very last newline, no others could have come
//		in-between, and thus no other <br>s swapped for \ns. There for the distance from the strings end is the same in each.
//		
		//We can now convert back using the original string length. (contents is what the user specified, and getText() returns)
		lastBundleIndex = this.contents.length() -  lastBundleIndex_fromEnd;
				
		return lastBundleIndex;		
	}
	
	
	
	
	public int getFirstNewLineLocation(){
		if (newline_indexs.size()<2){
			return -1; //There was no newlines (the last element iin array is always there even if there is no newlines)
		}
		
		Integer nextBundleIndex = newline_indexs.get(0); //works only as its the first newline. See getLastNewLineLocation() for why others are more complex
		return nextBundleIndex;		
	}

	/**
	 * actually, currently bundle locations
	 * @return
	 */
	public ArrayList<Integer> getNewlineLocations(){
		return super.newline_indexs;
	}
	
	
}
