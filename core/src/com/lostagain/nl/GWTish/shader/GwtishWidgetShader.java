package com.lostagain.nl.GWTish.shader;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.shader.GwtishWidgetShaderAttribute.TextScalingMode;

/**
 * The goal of this shader is to combine the features of the distancefieldshader with the glowingrectangle background shader
 * as such it has two sets of attributes, one for each.
 * 
 * @author Tom
 *
 */
public class GwtishWidgetShader implements Shader {
	public static Logger Log = Logger.getLogger("GwtishWidgetShader"); //not we are using this rather then gdxs to allow level control per tag
	
	
	ShaderProgram program;



	Camera camera;
	RenderContext context;

	final static String logstag = "ME.GwtishWidgetShader";


	//-----------------------------------------------
	//This shader heavily relies on attribute type
	//
	//GwtishWidgetShaderAttribute
	//
	//This is in its own file purely for neatness
	//
	//-----------------------------------------------
	//-------------------------------------
	//------------------------
	//-------------
	//----

	int u_projViewTrans;
	int u_worldTrans;
	int u_sampler2D; 

	/**
	 * -1 = no text rendering
	 */
	int u_colorModeFlag;	
	
	
	int u_textColour;
	
	//int u_backColour;

	int u_texture_pixel_step;
	int u_resolution;
	int u_sizeDiff;

	//padding
	int u_textPaddingX;
	int u_textPaddingY;

	//glow
	int  u_textGlowColor;
	int  u_textGlowSize  ; //size of glow (values above 1 will look strange)

	//outline
	int  u_outColor ;
	int  u_outlinerInnerLimit; //Arbitrarily big size for no outline
	int  u_outlinerOuterLimit; //Arbitrarily big size for no outline

	//shadow
	int  u_shadowXDisplacement;
	int  u_shadowYDisplacement;
	int  u_shadowBlur;
	int  u_shadowColour;

	//background
	int u_backSample2D;
	int u_backBorderWidth;
	int u_backBackColor;
	int u_backCoreColor; 		
	int u_backGlowColor;
	int u_backCornerRadius;


	//filters
	int u_filterBrightness;
	int u_filterContrast;
	
	//hsv filters
	int u_filterHue;
	int u_filterSaturation;
	int u_filterValue;
	
	//transform
	int u_transformValue;
	
	public GwtishWidgetShader(Renderable renderable) {
		
		Gdx.app.log(logstag, "initialising gwtish widget shader");
		
		String prefix = createPrefix(renderable);
		
		String vert =  Gdx.files.internal("shaders/gwtishwidgetshader_vert.glsl").readString();
		String frag =  Gdx.files.internal("shaders/gwtishwidgetshader_frag.glsl").readString();
		
		program = new ShaderProgram(prefix+vert, prefix+frag);

		if (!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}

		Log.info("______________________________________________________________________________________created new shader with prefix:\n"+prefix);
		
	}
	

	@Override
	public void init () {
		
		Gdx.app.log(logstag, "initialising gwtish widget shader");
		/*
		String prefix = createPrefix();
		

		String vert =  Gdx.files.internal("shaders/gwtishwidgetshader_vert.glsl").readString();
		String frag =  Gdx.files.internal("shaders/gwtishwidgetshader_frag.glsl").readString();

		//String prefix = createPrefix(renderable, this.get);

		program = new ShaderProgram(prefix+vert, prefix+frag);

		if (!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}*/


		Gdx.app.log(logstag, "setting GwtishWidgetShader uniform locations");
		Gdx.app.log(logstag, "(first the distance field ones....");

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_sampler2D =   program.getUniformLocation("u_texture");

		u_colorModeFlag =  program.getUniformLocation("u_colorModeFlag");

		u_texture_pixel_step =  program.getUniformLocation("u_pixel_step");
		u_resolution  =  program.getUniformLocation("u_resolution");
		u_sizeDiff=  program.getUniformLocation("u_sizeDiff");
		//text and back color
		u_textColour =  program.getUniformLocation("u_textColor");
		//u_backColour =  program.getUniformLocation("u_backColor");

		//padding
		u_textPaddingX = program.getUniformLocation("u_textPaddingX");
		u_textPaddingY = program.getUniformLocation("u_textPaddingY");


		//glow
		u_textGlowColor = program.getUniformLocation("u_glowColor");
		u_textGlowSize  = program.getUniformLocation("u_glowSize"); //size of glow (values above 1 will look strange)

		//outline
		u_outColor           = program.getUniformLocation("u_outColor");
		u_outlinerInnerLimit = program.getUniformLocation("u_outlinerInnerLimit"); //Arbitrarily big size for no outline
		u_outlinerOuterLimit = program.getUniformLocation("u_outlinerOuterLimit"); //Arbitrarily big size for no outline

		//shadow
		u_shadowXDisplacement = program.getUniformLocation("u_shadowXDisplacement");
		u_shadowYDisplacement = program.getUniformLocation("u_shadowYDisplacement");
		u_shadowBlur          = program.getUniformLocation("u_shadowBlur");
		u_shadowColour        = program.getUniformLocation("u_shadowColour");

		//background
		
		//image back
		u_backSample2D  = program.getUniformLocation("u_backSample2D"); 
		//Procedural color/border back
		Gdx.app.log(logstag, "(now the background ones....");
		u_backBorderWidth   = program.getUniformLocation("u_backBorderWidth"); 
		u_backBackColor     = program.getUniformLocation("u_backBackColor"); 
		u_backCoreColor     = program.getUniformLocation("u_backCoreColor");  		
		u_backGlowColor     = program.getUniformLocation("u_backGlowColor"); 
		u_backCornerRadius  = program.getUniformLocation("u_backCornerRadius"); 

		
		//post processing filters
		u_filterContrast   = program.getUniformLocation("u_filterContrast"); 
		u_filterBrightness = program.getUniformLocation("u_filterBrightness"); 
		
		u_filterHue = program.getUniformLocation("u_filterHue");
		u_filterSaturation = program.getUniformLocation("u_filterSaturation");
		u_filterValue = program.getUniformLocation("u_filterValue");
				
		u_transformValue = program.getUniformLocation("u_transformValue");
		
		
		Gdx.app.log(logstag, "....)");


	}

	boolean hasText = false;
	boolean hasBackgroundImage = false;
	boolean hasBCFilter = false;
	boolean hasHSVFilter=false;
	boolean hasProcedralBackground = false;
	
	private String createPrefix(Renderable renderable) {
		

		/**
		 * no text /no back image / other stuff (ie, borders, curved corners)
		 * text /no back image    / other stuff
		 * no text / back image   / other stuff
		 * text / back image      / other stuff
		 */
		
		//get shader attribute
		GwtishWidgetShaderAttribute textStyleData = (GwtishWidgetShaderAttribute)renderable.material.get(GwtishWidgetShaderAttribute.ID);
		
		//prefix result
		String prefix = "";
		
		//work out what we need
		if (textStyleData.hasText()){
			//has texture defining some text
			 prefix = prefix+ "#define hasText\n";
			 hasText = true;
		} else {
	//		 prefix = prefix+ "#define hasText\n"; //remove in a mo, just for testing
				
			 hasText = false;
		}
	
		if (renderable.material.get(TextureAttribute.Diffuse)!=null){
			//has texture defining background
			 prefix = prefix+ "#define hasBackgroundImage\n";
			 hasBackgroundImage = true;				
		} else {
			 hasBackgroundImage = false;
		}
		
		//filters
		if (textStyleData.hasBCFilter()){
			//has post filters defining some text
			 prefix = prefix+ "#define hasBCFilter\n";
			 hasBCFilter = true;
		} else {				
			hasBCFilter = false;
		}
		
		
		if (textStyleData.hasHSVFilter()){
			//has post filters defining some text
			 prefix = prefix+ "#define hasHSVFilter\n";
			 hasHSVFilter = true;
		} else {				
			hasHSVFilter = false;
		}
		
		if (textStyleData.hasProcedralBackground()){
			//has post filters defining some text
			 prefix = prefix+ "#define hasProcedralBackground\n";
			 hasProcedralBackground = true;
		} else {				
			hasProcedralBackground = false;
		}
		
		
		//we could also have a "hasProcedralBackground" which is true by default, but could be disabled for things without any background at all?
		//(currently it just sets it to transparent)
		
			
		return prefix;
	}

	@Override
	public void dispose () {

		program.dispose();

	}

	@Override
	public void begin (Camera camera, RenderContext context) {  
		this.camera = camera;
		this.context = context;

		
		program.begin();
		
		//the the variable for the cameras projectino to be passed to the shader
		program.setUniformMatrix(u_projViewTrans, camera.combined);

		// context.setDepthTest(GL20.GL_LEQUAL);    	  
		//context.setCullFace(GL20.GL_BACK);
		
		//Standard blending;
		//	context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA); //straight alpha
			context.setBlending(true,GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA); //premultiplied
		
		context.setDepthTest(GL20.GL_LESS);  
		context.setDepthTest(GL20.GL_NONE); //NEW: Completely disable depth testing as Jamgames have lots of things at the same position
		//instead we manuallt sort with zindex attributes
		//TODO: have a setting for turning this GL20.GL_NONE on/off?
		
		
		//http://stackoverflow.com/questions/32487074/libgdx-eliminate-transparency-artifacts-when-using-cameragroupstrategy-with-dec
		//Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//-------------
		//(currently we need a way to optionally use GL_NONE for overlays
		
		
		//	context.setDepthTest(GL20.GL_NONE);    	
		//	context.setDepthTest(GL20.GL_GREATER); 
	}

	@Override	
	public void render (Renderable renderable) {  


		
		//set the variable for the objects world transform to be passed to the shader
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		
		//float w = renderable.mesh.calculateBoundingBox().getWidth();
		//float h = renderable.mesh.calculateBoundingBox().getHeight();

		float w = renderable.meshPart.mesh.calculateBoundingBox().getWidth();
		float h = renderable.meshPart.mesh.calculateBoundingBox().getHeight();
		
		//GWTish widgets are controlled by a style attributes
		//A distance field shader for text styling with additional features for background control
		//in essence it gives css-like specifications for a texture
		GwtishWidgetShaderAttribute textStyleData = (GwtishWidgetShaderAttribute)renderable.material.get(GwtishWidgetShaderAttribute.ID);
		
		//update delta if animating
		textStyleData.updateDelta( Gdx.graphics.getDeltaTime()*1000.0);
		//(various style settings can be animated)
		//
		
		//background used to be a seperate shaderattribute, not used now;
		//GwtishWidgetBackgroundAttribute backgroundParameters = (GwtishWidgetBackgroundAttribute)renderable.material.get(GwtishWidgetBackgroundAttribute.ID);
		//if textStyleData is null, we assume the null dataset for it (no londer needed, there is always textStyleData now
		/*
		if (textStyleData==null){
			textStyleData = new GwtishWidgetShaderAttribute(GwtishWidgetShaderAttribute.presetTextStyle.NULL_DONTRENDERTEXT);
			//note; 
			//this is currently not very efficient - we should have a flag system for "no text" rather then needing COLOR 0,0,0,0 to be set on all color settings  
			
			//distance field attribute shader should also have a texture to go with it
			
		}*/
		
		
		
		//sometimes extra padding has to be added when scaling
		//this is because we centralize by default when the scaling has resulted in extra space either vertically or horizontally.
		//maybe in future we have other options?
		float textScale_height_pad = 0;
		float textScale_width_pad  = 0;	
		
		if (textStyleData.hasText()) {
		//if (renderable.material.get(TextureAttribute.Diffuse)!=null){		
			//Texture distanceFieldTextureMap = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
		
			Texture distanceFieldTextureMap =  textStyleData.distanceFieldTextureMap;
			distanceFieldTextureMap.setFilter(TextureFilter.Linear, TextureFilter.Linear); //not needed
			
			program.setUniformi(u_sampler2D, context.textureBinder.bind(distanceFieldTextureMap));    		    		 
	
			//Now we need to supply the pixel step of the texture as this is different to the overall one
			float tw = distanceFieldTextureMap.getWidth(); //add padding here?
			float th = distanceFieldTextureMap.getHeight();

			
			//depending on sizemode though, we might still want to use the models size - thus stretching the texture		
			if (textStyleData.textScaleingMode.equals(TextScalingMode.fitarea)){
				
				//fit area does not support padding (it wouldn't make sense really,unless you deliberately wanted to crop text off?)
				 tw = w;
				 th = h;				 
					
			} else if (textStyleData.textScaleingMode == TextScalingMode.fitPreserveRatio || 
				     	textStyleData.textScaleingMode == TextScalingMode.natural ){ //natural might end up redundant?
							
				//padding totals can only, at most, be the size of the widget
				//if they exceed it, we should take midpoint between them
				/*
				float totalPaddingWidth =  (textStyleData.paddingLeft*2f);
				if (totalPaddingWidth>w){
					totalPaddingWidth=w;
				}
				float totalPaddingHeight =  (textStyleData.paddingTop*2f);
				if (totalPaddingHeight>h){
					totalPaddingHeight=h;
				}
				*/
			//	tw = tw + (totalPaddingWidth); //should also be right
			//	th = th + (totalPaddingHeight); // should also be +bottom
				
				//float scale = Math.min((w-totalPaddingWidth)/tw, (h-totalPaddingHeight)/th); //amount texture should be scaled down by
				
				
				//Experiment (seems to centralize text successfully);
				float scale = textStyleData.textScale;

				//scale the texture size
				tw = scale*tw;
				th = scale*th;		
				
				/*
				//autopad the smaller dimension to centralize (that is, find the topleft corner needed to centralize the text)
				//this might not be correct, see commented out one that will require padding information on all 4 sides
				if (th<(h)){
					//pad height
					textScale_height_pad = (((h/2)-textStyleData.paddingTop)-(th/2));					
					
				}
				if (tw<(w)){
					//pad width
					textScale_width_pad = (((w/2)-textStyleData.paddingLeft)-(tw/2));					
				}*/
				//--------------------------------------------------------------
				
				//but we dont want center, we want correct padding!
				
				//Perhaps not do this at all if there's uneven padding?
				/*
				if (th<(h-totalPaddingHeight)){
					//pad height
					textScale_height_pad = ((h-totalPaddingHeight)-th)/2;					
					
				}
				if (tw<(w-totalPaddingWidth)){
					//pad width
					textScale_width_pad = ((w-totalPaddingWidth)-tw)/2;					
				}*/
				
				switch(textStyleData.textAlignmentHorizontal){
				case CENTER:
				case JUSTIFY:
					if (tw<(w)){
						//pad width
						textScale_width_pad = (((w/2)-textStyleData.paddingLeft)-(tw/2));					
					}				
					break;
				case LEFT:
					textScale_width_pad=0f;					
					break;
				case RIGHT:
					textScale_width_pad=(w-textStyleData.paddingLeft)-tw;		
					break;
				
				}
				
				switch(textStyleData.textAlignmentVertical){
				case BOTTOM:
					textScale_height_pad=(h-textStyleData.paddingTop)-th;
					break;
				case MIDDLE:
					if (th<(h)){
						//pad height
						textScale_height_pad = (((h/2)-textStyleData.paddingTop)-(th/2));	
					}				
					break;
				case TOP:
					textScale_height_pad=0f;
					break;
				
				}
			}
			
			
			program.setUniformf(u_texture_pixel_step,(1/tw), (1/th));
			
			
			//we also supply the ratio between the image and the overall model size
			//this lets us have a texture at a arbitrary position within the models shader
			float sizeDiffX = w / tw;
			float sizeDiffY = h / th;

			program.setUniformf(u_sizeDiff,sizeDiffX, sizeDiffY);
			

			program.setUniformf(u_colorModeFlag,1.0f);
			
			//if (renderable.userData.equals("InfoBox")){
			//	Log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!a_colorFlag set to 1.0 on infobox");
			//}
			
		} else {
			
			program.setUniformf(u_colorModeFlag, -1.0f);
			
		}
		
		//test for image background if this shader is set to use them and a image was supplied we have one 
		if (hasBackgroundImage && renderable.material.get(TextureAttribute.Diffuse)!=null){
			
			Texture backgroundMap = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
			
			//distanceFieldTextureMap.setFilter(TextureFilter.Linear, TextureFilter.Linear); //not needed			
			program.setUniformi(u_backSample2D, context.textureBinder.bind(backgroundMap));    

		}
		
		
		
		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);
		setSizeUniform(w,h);//,textStyleData.paddingLeft,textStyleData.paddingTop


		//Back color used to come from diffuse (this is being removed)
		//ColorAttribute ColAttribute = ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse));
		//Color backcolor = Color.CLEAR; //default clear back color
		//if (ColAttribute!=null){
		//	backcolor = ColAttribute.color.cpy();
		//}
		//--------------------

		//and we multiply it by the opacity
		BlendingAttribute backgroundOpacity = ((BlendingAttribute)renderable.material.get(BlendingAttribute.Type));
		if (backgroundOpacity!=null){

			//	backcolor.a = backcolor.a*backgroundOpacity.opacity;                    // Temp.Really Blending should effect everything, not just the background

			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(backgroundOpacity.opacity);
			}
			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(backgroundOpacity.opacity);
			}

		} else {

			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(1f);
			}

			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(1f);
			}

		}



		// Color textColour = Color.ORANGE;
		// if (renderable.material.has(ColorAttribute.Diffuse)){	    		     		
		//text from attribute
		Color textColour = textStyleData.getTextColour();   	
		
		if (textColour==null){
			Log.severe("_______________________________________________________________________________________ textColour is null");
		}
		


		// } else {
		//if not we assume default text color
		//	 program.setUniformf(a_colorFlag,0);    	 //this would give color based on texture	 

		/// }


		//text ans back color
		//program.setUniformf(u_backColour, backcolor); //back color is redundant now we have GwtishWidgetBackgroundAttribute as well
		program.setUniformf(u_textColour, textColour);  

		//displacements
		program.setUniformf(u_textPaddingX, textScale_width_pad+textStyleData.paddingLeft);
		program.setUniformf(u_textPaddingY, textScale_height_pad+textStyleData.paddingTop);
		
		
		//glow
		program.setUniformf(u_textGlowColor,textStyleData.getGlowColour());
		program.setUniformf(u_textGlowSize ,textStyleData.glowSize); //size of glow (values above 1 will look strange)

		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);

		//outline
		program.setUniformf(u_outColor,textStyleData.getOutlineColour());
		program.setUniformf(u_outlinerInnerLimit,textStyleData.outlinerInnerLimit); 
		program.setUniformf(u_outlinerOuterLimit,textStyleData.outlinerOuterLimit); 

		//shadow
		program.setUniformf(u_shadowXDisplacement,textStyleData.shadowXDisplacement);
		program.setUniformf(u_shadowYDisplacement,textStyleData.shadowYDisplacement);
		program.setUniformf(u_shadowBlur,textStyleData.shadowBlur);
		program.setUniformf(u_shadowColour,textStyleData.getShadowColour());




		/*
		if (textStyleData==null){
			//(if no background specified its just transparent)	 	
			program.setUniformf(u_backBorderWidth,    0f);  	 
			program.setUniformf(u_backBackColor,    Color.CLEAR);
			program.setUniformf(u_backCoreColor,    Color.CLEAR); 
			program.setUniformf(u_backCornerRadius, 1f); 

		} else {*/

			program.setUniformf(u_backBorderWidth,    textStyleData.borderWidth   );  	 
			program.setUniformf(u_backBackColor,    textStyleData.getBackColor()   );
			program.setUniformf(u_backCoreColor,    textStyleData.getBorderColour()); 
			program.setUniformf(u_backCornerRadius, textStyleData.cornerRadius); 


		//}

	//	program.setUniformf(u_colorModeFlag, 4.0f);    	//-1 means no text rendering. without this we might see old textures used	    		 
	//	program.setUniformf(u_textPaddingX, 90.0f);    	//-1 means no text rendering. without this we might see old textures used	    		 
		
			
			//Finally filter data, if set
			
			program.setUniformf(u_filterContrast,   textStyleData.filter_contrast); 
			program.setUniformf(u_filterBrightness, textStyleData.filter_brightness); 
			
			//hsv filter settings
			program.setUniformf(u_filterHue, textStyleData.filter_hue); 
			program.setUniformf(u_filterSaturation, textStyleData.filter_saturation); 
			program.setUniformf(u_filterValue, textStyleData.filter_value); 


			Matrix4 cssLikeTransformMatrix = textStyleData.getTransform().createMatrix();
			//Experiment
			float halfthickness=1.0f;
			float halfh=h/2.0f;
			float halfw=w/2.0f;
			

			Matrix4 temp = new Matrix4();
			if (textStyleData.usesTransformr){
		
				//get current pos/rot/scale
				//
				Vector3 position = new Vector3();
				renderable.worldTransform.getTranslation(position); //just position?
				
				Vector3 scale = new Vector3();
				renderable.worldTransform.getScale(scale); //just position?
				
				Quaternion rotation = new Quaternion();
				renderable.worldTransform.getRotation(rotation);
				
				//get center of mesh relative to its pivot
				float bbcx =  renderable.meshPart.mesh.calculateBoundingBox().getCenterX() ;//+ position.x;
				float bbcy =  renderable.meshPart.mesh.calculateBoundingBox().getCenterY() ;//+ position.y;
				float bbcz =  renderable.meshPart.mesh.calculateBoundingBox().getCenterZ() ;//+ position.z;
				Vector3 middle = new Vector3(bbcx,bbcy,bbcz);
				 
			//	Matrix4 invworld = renderable.worldTransform.cpy();
			//	invworld.inv();
				
				//move it back so center is at origin
				//temp.translate(-bbcx, -bbcy, -bbcz); 
				//temp.set(renderable.worldTransform);

				//Matrix4 translationmatrix = new Matrix4().setToTranslation(-bbcx, -bbcy, -bbcz);
				//temp.mul(translationmatrix);
				
				//temp.translate(-bbcx,-bbcy,-bbcz);
				
				//random rotation to test
			//	temp.rotate(Vector3.Z, 30);	
				
				//temp.setTranslation(bbcx,bbcy,bbcz);
				
			//	temp.setToRotation(Vector3.Z, 30);
				
				
				//temp = new Matrix4(new Vector3(0,0,0), new Quaternion(Vector3.Z, 30), scale ); //<-- rotates around origin / easy
				//temp.translate(position);
				
				
				
				//translationmatrix.inv().rotate(Vector3.Z, 30);	
				//temp.scl(textStyleData.getTransform().scale); //scaling works - but only if we dont recenter...hu?
				//temp.rotate(textStyleData.getTransform().rotation);
				//textStyleData.getTransform().createMatrix();
				//move back to where we started
				//temp.translate(bbcx, bbcy,bbcz);

				//temp.mul(translationmatrix.inv());
				
				//temp.mul(renderable.worldTransform);
				

			//	position =  middle;//           position.add( middle.scl(pos.scale) ).add(pos.position);
			//	rotation = rotation.add(pos.rotation);
			//	scale    = scale.add(pos.scale);
				
				//However, if you want to rotate an object around a certain point, then it is scale, point translation, rotation and lastly object translation.
				
				//Matrix4 worldmix = new Matrix4();
				//worldmix.setToTranslation(middle);
				//worldmix.rotate(pos.rotation.nor());
				//worldmix.translate(middle.scl(-1));
				
				
			//	worldmix.scl(pos.scale);
			//	worldmix.translate(middle);
				
				//
			//	worldmix.rotate(pos.rotation);
			//	worldmix.translate(position);
				
				
			//	middle=middle.scl(pos.scale);
				//Matrix4 worldmix = new Matrix4(position,rotation.nor(),scale);
			//	worldmix.setTranslation(position.add(middle) );
				
				

				Matrix4 world = renderable.worldTransform.cpy(); //normal position etc
				PosRotScale pos = textStyleData.transform;       //the style information that alters it
				
				//Magic line that does the work;
				world.translate(middle).scl(pos.scale).rotate(pos.rotation).translate(-middle.x,-middle.y,-middle.z).translate(pos.position); //WORKS! rotates about center
				// :)
				
				
				program.setUniformMatrix(u_worldTrans, world,false); //not sure if we should transpose? seems  to effect stuff when we rotate

			}
			//pass this matrix to the shader
			//program.setUniformMatrix(u_transformValue, temp,false); //not sure if we should transpose? seems  to effect stuff when we rotate
			//u_transformValue


		 renderable.meshPart.render(program);
		 
	}

	public void setSizeUniform(float w, float h) {
		//,float paddingLeft, float paddingTop)
		program.setUniformf(u_resolution, w,h);


		//we also need the difference in size between the total widget size and the size of the text on it
		//This is expressed as a ratio
		//   float sizeDiffX = w / (w-paddingLeft);
		//    float sizeDiffY = h / (h-paddingTop);

		//   program.setUniformf(u_sizeDiff,sizeDiffX, sizeDiffY);


		//NOTE: this is the pixel step of the texture.
		//The widget might be much larger, w/h ONLY be the effective size of the text on the widget,ignoring any padding.
		//for this reason we subtract the padding first

		//	w = w - paddingLeft; //in future right as well;
		//	h = h - paddingTop;  //and bottom

		//program.setUniformf(u_texture_pixel_step,(1/w), (1/h));
	}

	@Override
	public void end () { 

		program.end();
	}


	@Override
	public int compareTo (Shader other) {
		if (other == null) return -1;
		if (other == this) return 0;
		return 0; // FIXME compare shaders on their impact on performance
	}
	

	


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GwtishWidgetShader other = (GwtishWidgetShader) obj;
		if (hasBackgroundImage != other.hasBackgroundImage)
			return false;
		if (hasText != other.hasText)
			return false;
		if (hasBCFilter != other.hasBCFilter)
			return false;
		if (hasHSVFilter != other.hasHSVFilter)
			return false;
		if (hasProcedralBackground != other.hasProcedralBackground)
			return false;
		//
		return true;
	}


	@Override
	public boolean canRender (Renderable instance) {

		if (instance.material.has(GwtishWidgetShaderAttribute.ID)) { //|| instance.material.has(GwtishWidgetBackgroundAttribute.ID)){
			
			//also check if our optional parameters match
			//see createPrefix()
			GwtishWidgetShaderAttribute textStyleData = (GwtishWidgetShaderAttribute)instance.material.get(GwtishWidgetShaderAttribute.ID);

			boolean instanceHasBackground = false;
			if (instance.material.get(TextureAttribute.Diffuse)!=null){
				instanceHasBackground=true;
			}
				
			//check we match feature support wise
			if (   textStyleData.hasText()     == hasText 
				&&	textStyleData.hasBCFilter() == hasBCFilter
				&&	textStyleData.hasHSVFilter() == hasHSVFilter	
				&&	textStyleData.hasProcedralBackground() == hasProcedralBackground							
				&&  instanceHasBackground       == hasBackgroundImage){
				
				return true;
				
			}
			
			
			
		}

		return false;


	}
	/*

	public static GwtishWidgetShader Default = new GwtishWidgetShader();
	/**
	 * returns the default copy of this shader, compiling it if needed
	 * @return
	 *
	public static ShaderProgram getProgram() {
		if (Default.program==null){
			Default.init();
			int u_diffuseColor =  Default.program.getUniformLocation("u_diffuseColor");      
			int u_colorFlag    =  Default.program.getUniformLocation("u_colorFlag");
			int u_textColour    =  Default.program.getUniformLocation("u_textColour");

			Default.program.setUniformf(u_colorFlag, 1f);
			Default.program.setUniformf(u_diffuseColor, Color.RED);

			Default.program.setUniformf(u_textColour, Color.RED);  

		}
		// TODO Auto-generated method stub
		return Default.program;
	}*/


}