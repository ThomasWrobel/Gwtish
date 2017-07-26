
#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

// version 0.53
//
// The Goal of this shader is to allow a very very aproximate emulatation of a styled div with text in it.
// This shader should be applied to a rectangle only.
//
// Currently supported;
// - Displays text using a image containing a distance field rendering of a font (to allow it to be sharp at any distance)
// - Text can have its colour changed
// - Text can have a shadow applied
// - Text can have a glow applied
// - Text can have a outline applied
// - The background to the text can be set to any color or image
// - Background can have borders of varying size
// - Background can have curved corners to the border
////
// - brighhtness/contrast filter can be applied to the result
// - hue/sat/lum filter can be applied to the result (Note: currently messes up brightness)

// TODO (maybe)
// Convert a lot to uniforms 
// (maybe) Background shadow support (to emulate CSS Box shadow). This would require making the vertex shader increase the size of the rectangle to take into account the borders
//

// this calculated in vertex shader
// width/height = triangle/quad width/height in px;
//vec2 pixel_step = vec2(1/width, 1/height);  
varying vec2 pixel_step;
    
uniform vec2 u_sizeDiff;

//
//float fwidth = abs(dx) + abs(dy);     
//----------------------------

//incoming texture
//uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;
varying vec4 v_textColor;
//varying vec4 v_backColor;

//padding
varying float v_textPaddingX;
varying float v_textPaddingY;

//glow
uniform vec4  u_glowColor;
uniform float u_glowSize; //size of glow (values above 1 will look strange)
		
//outline
varying vec4  v_outColor;
varying float v_outlinerInnerLimit; //Arbitrarily big size for no outline
varying float v_outlinerOuterLimit; //Arbitrarily big size for no outline

//shadow
uniform float u_shadowXDisplacement;
uniform float u_shadowYDisplacement;
uniform float u_shadowBlur;
uniform vec4  u_shadowColour;
//--

uniform float u_colorModeFlag;
 

varying MED vec2 v_diffuseUV;
 
//uniform sampler2D u_diffuseTexture;

uniform sampler2D u_texture;
uniform sampler2D u_backSample2D;


//background style data
uniform float u_backBorderWidth; 
uniform vec4  u_backBackColor;
uniform vec4  u_backCoreColor; //border color for some reason
uniform float u_backCornerRadius;

varying vec2 iResolution;
varying vec2 fPosition;

//varying vec2 center;
varying vec2 hsize;

//post filtering
//bc filter
uniform  float u_filterBrightness;
uniform  float u_filterContrast;
//hsv filter
uniform float u_filterHue;
uniform float u_filterSaturation;
uniform float u_filterValue;

//The effective tex co-ordinate is the TexCoord we are drawing at, after its been corrected to account for padding
vec2 effective_vTexCoord;

//the effectives color
vec4  effective_textColor;
vec4  effective_outColor;
vec4  effective_glowColor;

// Background method Based and modified from;
// Created by Marc Lepage - mlepage/2015
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Rect center
//vec2 center = iResolution.xy / 2.0; //TODO: moved to inside main in vert

// Rect half size
//vec2 hsize  = iResolution.xy / 2.0;//TODO: moved to inside main


//WORKAROUND if fwidth/above extension is not supported
//http://stackoverflow.com/questions/22442304/glsl-es-dfdx-dfdy-analog
float myFunc(vec2 p){
	return p.x*p.x - p.y; // that's our function. We want derivative from it.
}


//todo; move these "myFunc" constants into main (compatability issue on some platforms)

//These things are to get around the fwidth function being missing on some GPUs
//float current = myFunc(vTexCoord);
float current = 0.0;
//float dfdx    = myFunc(vTexCoord + pixel_step.x) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
//float dfdy    = myFunc(vTexCoord + pixel_step.y) - current; //same for y
  float dfdx = 0.0;
  float dfdy = 0.0;
  
//modified above into;  
//float dfdx    = myFunc(vTexCoord + vec2(pixel_step.x,0.0)   ) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
//float dfdy    = myFunc(vTexCoord + vec2(0.0,pixel_step.y)   ) - current; //same for y
//because   myFunc expects a  vec2, and you cant add a vec1 and a vec2 without a error on some platforms.
//therefor we add a vec2 to a vec2 instead

//the following were attempts at smoothing the font 
const float smoothing =  0.25/(4.0*32.0); //TODO: move to inside main
//0.25/(filesmooth*fontfilescale)                     //0.001953125//1.0/16.0;  

float contour(in float d, in float w) {
    // smoothstep(lower edge0, upper edge1, x)
    return smoothstep(0.5 - w, 0.5 + w, d);
}

float samp(in vec2 uv, float w) {
    return contour(texture2D(u_texture, effective_vTexCoord).a, w);
}



//http://iquilezles.org/www/articles/distfunctions/distfunctions.htm
//Rounded rect distance function
float udRoundRect(vec2 p, vec2 b, float r)
{
	return length(max(abs(p) - b, 0.0)) - r;
}

//Alternative to the above we now use instead;
//Adapted from;
//https://www.shadertoy.com/view/4dfXDn
float boxDist2D(vec2 p, vec2 size, float radius)
{
	size -= vec2(radius);
	vec2 d = abs(p) - size;
  	return min(max(d.x, d.y), 0.0) + length(max(d, 0.0)) - radius;
}
//---------------------------------

//Simple function to move a point by a certain amount
vec2 translate(vec2 point, vec2 dis)
{
	return point - dis;
}

//creates a bordered version given the backcolor
vec4 createBorders(vec4 effectivebackcolour){

  	vec4  BorderColor = u_backCoreColor; 
  	vec2  fragCoord    = fPosition*iResolution.xy; 
	  
  	//
  	//If no border is specified, we change the border to the backcolor
  	//This is to ensure there is no gap at the edge. (it seems even when set to zero the border has a slight size - likely due to AA)
  	//This check should rarely be neeed though as ideally the createBorders function should not run at all if we arnt using borders
  	//(its possible to have a zero sized border with curved corners, however, so its here for that case)
  	//
    if (u_backBorderWidth==0.0){
      BorderColor=effectivebackcolour;//u_backBackColor;
    }
  
  //The final result of working out the background colour at this point goes into resultColour  
    vec4  resultColor = BorderColor;
    
   
    float hbw = u_backBorderWidth / 2.0;
    
   //old function
   // float rr = udRoundRect(fragCoord - center, hsize - radius , radius); //- radius        
	
	//new function for 2D Rectangular Distance
	vec2 p = fragCoord;// (fPosition + vec2(0.0))*iResolution.xy;		
    float rr = boxDist2D(translate(p, hsize), (iResolution.xy/2.0)-u_backBorderWidth, u_backCornerRadius); //Half resolution works....why?
          
	//Notes on Box "Distance Function" above     
    //rr==0 seems to be the edge returned 
    //need to seperate transitions
    //rr>0 means "towards edge" ie. interpolate between the border colour and transparent
    //rr<0 means "towards center" ie. interpolate between the border colour and background
      //  rr = 1.0-abs(rr);
        //float a = clamp(rr, 0.0,1.0);
    
    if (rr>hbw){
    	//u_backBackColor =  ;    	    	
   	    rr = 1.0-abs(rr);
        float a = clamp(rr, 0.0,1.0);
    	resultColor =mix(BorderColor, vec4(0.0,0.0,0.0,0.0), 1.0-a); 
    	
    } else if (rr <=-hbw){
    	    	
   	    rr = 1.0-abs(rr);
       float a = clamp(rr, 0.0,1.0);
    	resultColor =mix(BorderColor, effectivebackcolour, 1.0-a); //v_backBackcolor
    } else {
   	 	resultColor = BorderColor;
    }
    
   // 
   //(if we add shadow support, it will created before that if statement and replace the vec4(0,0,0,0), as that currently represents the transparent
   //region of the shader)
   //
   return resultColor;
}

//Gets the background col at the specified point
//This will include the border, which can be coloured and curved at the corners if needed.
//
//In future we could emulate css style BoxShadow here too, unlike css we cant go outside
//the edges of the box, so the background would have to be made smaller to give the shadow room
//We could possibly do this automatically via vertex manipulation?
vec4 getBackColour()
{
    
    //uv based color (put it in as a option?)
	//vec4  contentColor = vec4(uv,1.0,u_backCoreColor.a); //vec4(1.0,0.0,1.0,1.0); //vec4(uv, 0.5 + 0.5*sin(iGlobalTime), 1.0);
  	
  	//if theres a background image use that
  	vec4 effectivebackcolour = u_backBackColor;
  	
  	#ifdef hasBackgroundImage  	
  	 	vec4 backdiffuse = texture2D(u_backSample2D, vTexCoord);//vec4(0.0,1.0,0.0,1.0);  	 
  	 	
  	 	//mix the background ontop of any coloured background already specified
  	 	//(in the distance future we might support gradients before this - in either case u_backBackColor in this equation
  	 	//should represent the color under the specified backdiffuse)
  	 	
  		//effectivebackcolour = (backdiffuse*backdiffuse.a)+(u_backBackColor*(1-backdiffuse.a));  	
  		//effectivebackcolour.a=backdiffuse.a+u_backBackColor.a; //alpha total should be sum of alpha back and alpha front colors
  	 	//fix;
  	 	//vec4 backdiffusefull = backdiffuse;
  		//backdiffusefull.a=1.0f;
		//effectivebackcolour =  mix(u_backBackColor, backdiffusefull ,backdiffuse.a);
		
		
	 	backdiffuse.rgb *= backdiffuse.a; //premultiply	 	
	 //	u_backBackColor.rgb *= u_backBackColor.a; //premultiply //TODO: cant change v_back... make new var
	 	
	 	effectivebackcolour.rgb *= effectivebackcolour.a; //premultiply //TODO: cant change v_back... make new var
	 	
		effectivebackcolour.rgb = backdiffuse.rgb + (effectivebackcolour.rgb * (1.0 - backdiffuse.a));		
		effectivebackcolour.a = backdiffuse.a + (effectivebackcolour.a * (1.0 - backdiffuse.a));
		
		//if needed the above can be converted to maths as mix is pretty simple
  	
	#endif 
  	
  	#ifdef hasProcedralBackground 
  		effectivebackcolour.rgb = effectivebackcolour.rgb/effectivebackcolour.a;
  		 vec4 resultColor = createBorders(effectivebackcolour); //err...needs to work with premultiplied input to save conversions
  		 resultColor.rgb = resultColor.rgb*resultColor.a;
    #else
  		 vec4 resultColor = effectivebackcolour;
	#endif 
	
   //return the background colour as a result
   return resultColor;//mix(contentColor, u_backBackColor, 1.0-a); 
}


//
//And now, a rather complex function for the styled text using the distance field image of it supplied
//
vec4 getStyledText()
{
	float colorFlag = u_colorModeFlag;
	vec4 diffuse = vec4(1.0,0.0,0.0,1.0);
	 
	 
	if ((colorFlag==0.0))
	{
		//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
		diffuse = texture2D(u_texture, effective_vTexCoord);//vec4(0.0,1.0,0.0,1.0);
	}
	else 
	{
	//	v_diffuseColor = vec4(0.0,0.0,1.0,1.0); //temp for testing
		diffuse = effective_textColor;// texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
		//diffuse = vec4(1.0,1.0,1.0,1.0);
	}


 	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 	float dist = texture2D(u_texture, effective_vTexCoord).a;
 	 	 	
 	// fwidth helps keep outlines a constant width irrespective of scaling
    // GLSL's fwidth = abs(dFdx(uv)) + abs(dFdy(uv))
    
  //  float width = fwidth(dist); //<---------------correct formula (works fine desktop)
       	 	
   //float width = abs(dFdx(dist)) + abs(dFdy(dist));  //<-----------(was attempt at replacement for web, does not work)          
     
   float width = abs(dfdx*dist) + abs(dfdx*dist);  //<-----------(was attempt at replacement for web, does not work?)          
     
       	 	
 	 	 	// supersampled version
//width
	width=width+0.01; //0.01 makes edges a bit softer
    float alpha = contour( dist,width); //width doesnt seem correct always? probably mistake with web replacement for fwidth
  
    //float alpha = aastep( 0.5, dist );

    // ------- (comment this block out to get your original behavior)
    //Supersample doesnt seem to work well right now? at least its still pixely from far away
    
    // Supersample, 4 extra points
    float dscale = 0.354; // half of 1/sqrt2; you can play with this
   
   // vec2 duv = dscale * (dFdx(vTexCoord) + dFdy(vTexCoord)); //<---------------correct formula (works fine desktop)
   
    vec2 duv = vec2(dscale * dfdx,dscale * dfdy); //<-------------web replacement for now
    
    vec4 box = vec4(effective_vTexCoord-duv, effective_vTexCoord+duv);

    float asum = samp( box.xy, width )
               + samp( box.zw, width )
               + samp( box.xw, width )
               + samp( box.zy, width );

    // weighted average, with 4 extra points having 0.5 weight each,
    // so 1 + 0.5*4 = 3 is the divisor
    alpha = (alpha + 0.5 * asum) / 3.0;

    // -------
    
	vec4 newCol = vec4(diffuse.rgb,diffuse.a*alpha);
	
	//outline (optional)
	if (v_outColor.a>0.0){
		if (dist>v_outlinerOuterLimit){ //outermost limit (0 is max/outer edge)  0.05 is default for v_outlinerOuterLimit
			if (dist<v_outlinerInnerLimit){ //inner limit  0.2 is a good default 
			
					newCol   = v_outColor;
    				//newCol.a = 1.0;
    		}
    	}
    }
    
	
    //glow (the glow replaces the normal texture, it doesnt glow over it)
    if (u_glowColor.a>0.0){
    	if (dist>0.0) {
    		if (dist<0.5){ 
    	     //inner limit
    	     float glowSize = u_glowSize;
    		 alpha= smoothstep(0.5-u_glowSize, 0.5+u_glowSize, dist);
    		
    		   float newalpha = newCol.a + (u_glowColor.a * alpha);
    		   
    		 //now blend glow with whats there already
    		 newCol = (u_glowColor * alpha) + (newCol * (1.0-alpha));
    		
    		 
    		// newCol   = u_glowColor;
    		 newCol.a = newalpha;
    		    		 
    		}
    
   		 }
    }
    
    //shadow (the shadow will go under the normal texture, hence we need to create both and blend)
    //first we only create a shadow if theres one set (detected by shadow alpha being >0)
    if (u_shadowColour.a>0.0){
    	
    	//now we know we have a shadow we need to do a second texture look up, this time using our offset
    	float xo = effective_vTexCoord.x -(u_shadowXDisplacement * pixel_step.x);//u_shadowXDisplacement;
    	float yo = effective_vTexCoord.y -(u_shadowYDisplacement * pixel_step.y);//;
    	
    	//ensure inside texture still, else do nothing
    	  if (xo>0.0 && yo>0.0 && xo<1.0 && yo<1.0  ) {
     
    	
    	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 		float sdist = texture2D(u_texture, vec2(xo,yo)).a;
 		
    			
    			vec4 shadowCol   = u_shadowColour;
    			
    			float blurSize = u_shadowBlur;
    		    float salpha = smoothstep(0.5-blurSize, 0.5+blurSize, sdist);
    			
    		    shadowCol.a =  shadowCol.a * salpha;
    		    float olda = newCol.a;
    		    
    		   float newalpha = newCol.a+shadowCol.a;
    		   
    		    //now blend with original (under it!)
    		    newCol = (newCol * newCol.a) + (shadowCol * (1.0-newCol.a));
    		  
    		    newCol.a = newalpha;
    			//newCol.a = shadowCol.a + newCol.a ;
				}
    			
    	
    }
    
    return newCol;


}

//
//filters the result col brightness and contrast
//
vec4 getBCFilteredCol(vec4 incoming) {

   vec4 result = incoming;

    result.rgb = result.rgb / result.a;
  
  	//adjust contrast
    result.rgb = ((result.rgb-1.0)*max(u_filterContrast,0.0));
    
    //adjust brightness
    result.rgb = result.rgb+u_filterBrightness;       
    result.rgb *= result.a;
    

   return result;
}


//
//filters the result col hue sat and value
//
// saturation = difference in highest and lowest values?
//a) get each differance
//b) get biggest diff
//c) get middle of diff and each differance from middle
//d) scale those distances (0=middle 1=normal)
//e) reapply to rgb values
// err...seems messy.
//
//better to use a optimised combo function?
//source of hsv conversion;  http://stackoverflow.com/questions/15095909/from-rgb-to-hsv-in-opengl-glsl
//                    from;  http://lolengine.net/blog/2013/07/27/rgb-to-hsv-in-glsl
//adjusted by thomaswrobel to fit in this shader

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 getHSVFilteredCol(vec4 incoming) {

    vec4 result = incoming;

    result.rgb = result.rgb / result.a;
  
    //convert to hsl
    vec3 hsv  = rgb2hsv(result.rgb);
    
    //make our adjustments
    //u_filterHue
    hsv.x = hsv.x + u_filterHue;
    hsv.y = hsv.y * u_filterSaturation;
    hsv.z = hsv.z * u_filterValue;
    
    //loop hue
    //if ( hsv.x < 0.0)
	//{
		 hsv.x = mod(hsv.x,1.0);
	//}
	
    
    //convert back    
    result.rgb = hsv2rgb(hsv);
    
    result.rgb *= result.a;
    

   return result;
}



void main() {

	//effective result colours (purely as they might be set to transparent)
     effective_textColor = v_textColor;
     effective_outColor  = v_outColor;
     effective_glowColor = u_glowColor;

	//if theres padding the co-ordinates need to be scalled down by the paddings step size
	float paddingXstepSize = (v_textPaddingX * pixel_step.x);
    float paddingYstepSize = (v_textPaddingY * pixel_step.y);
    
    //likewise we have effective vTexCoord as the scale will change    
	effective_vTexCoord = vTexCoord;

    //now we need to scale down the TexCoord by the size difference between that and the resolution
    //(as the TexCoords should NOT including padding size)    
    effective_vTexCoord.x =  effective_vTexCoord.x *  u_sizeDiff.x;    
    effective_vTexCoord.y =  effective_vTexCoord.y *  u_sizeDiff.y;

    //alter by padding to get padded co-ordinates
    effective_vTexCoord.x = effective_vTexCoord.x - paddingXstepSize;
    effective_vTexCoord.y = effective_vTexCoord.y - paddingYstepSize;
    
	//-------------------------------------------------------
	//These things are to get around the fwidth function being missing on some GPUs
	current = myFunc(effective_vTexCoord);
	//float dfdx    = myFunc(vTexCoord + pixel_step.x) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
	//float dfdy    = myFunc(vTexCoord + pixel_step.y) - current; //same for y  
	//modified above into;  
	dfdx    = myFunc(effective_vTexCoord + vec2(pixel_step.x,0.0)   ) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
	dfdy    = myFunc(effective_vTexCoord + vec2(0.0,pixel_step.y)   ) - current; //same for y
	//because   myFunc expects a  vec2, and you cant add a vec1 and a vec2 without a error on some platforms.
	//therefor we add a vec2 to a vec2 instead
	//--------------------------------------------------------------------
    
   
   
    //if either of the co-ordinates are now less then zero or greater then the limits we set the text color settings to nothing
    if (
    	 effective_vTexCoord.x<=0.0 || 
   		 effective_vTexCoord.y<=0.0 || 
   		 effective_vTexCoord.x>=1.0 || 
	     effective_vTexCoord.y>=1.0 
    ) {
   
     	effective_textColor   = vec4(0.0,0.0,0.0,0.0);
     	effective_outColor    = vec4(0.0,0.0,0.0,0.0);
     	effective_glowColor   = vec4(0.0,0.0,0.0,0.0);
     	//	u_shadowColour= vec4(0.0,0.0,0.0,0.0);  //shadow has a slightly different tollerance so its dealt with in its generation
    
	 }
    
   
    
	//mix with back colour	
    
	
	//      = vec4(0.0,0.0,0.0,1.0);
	//newCol = clamp(newCol,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	//v_backColor = vec4(1.0,1.0,1.0,1.0);
	//v_backColor = clamp(v_backColor,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	
	//                addColor*addColor.a + sceneColor*(1-addColor.a);
	
	vec4 newCol = vec4(0.0,0.0,0.0,0.0);
	
	//-1 means no texture set, so we ensure we are over 0.0
	
	//if ( u_colorModeFlag > -0.1 &&	
	// if we have text we get it here
	#ifdef hasText
	if 
	    (
	    effective_textColor != vec4(0.0,0.0,0.0,0.0)  || 
	    effective_outColor  != vec4(0.0,0.0,0.0,0.0)  || 
	    effective_glowColor != vec4(0.0,0.0,0.0,0.0)  ||  
	    u_shadowColour      != vec4(0.0,0.0,0.0,0.0)
	    ) 
	{
		
		newCol = getStyledText();
		
	}
	#endif 
	
	vec4 v_backColor = vec4(0.0,0.0,0.0,0.0);
	
	//if theres a background colour or a border colour, then we get the backcolor for this particular spot
	//note; OR has a image
	#ifdef hasBackgroundImage
  	    v_backColor = getBackColour();	//alpha premulti out	//if backimage, no need for a if test
  	
  	#else	
  	  	
	if ( u_backBackColor!= vec4(0.0,0.0,0.0,0.0) || u_backCoreColor!= vec4(0.0,0.0,0.0,0.0)  ){ //else we test for visible borders
		
		v_backColor = getBackColour(); //alpha premulti out
		
	}	
	#endif
	
	//old (straight alpha)
	//vec4 finalCol =  (newCol * newCol.a) + (v_backColor * (1.0-newCol.a));
	//finalCol.a =  newCol.a + v_backColor.a;
	
	//new alpha premulti
	vec4 finalCol  = vec4(0.0,0.0,0.0,0.0);
	
	newCol.rgb = newCol.rgb*newCol.a; //convert to premultiplied
	finalCol.rgb = newCol.rgb + (v_backColor.rgb * (1.0 - newCol.a));		
	finalCol.a = newCol.a + (v_backColor.a * (1.0 - newCol.a));
	
	
	//if alpha is near zero dont do more
	//if (finalCol.a>0.01){
	
	
	//if we use a post filter (brightness, saturation adjustment etc)
	//
	//might not work right now input is premultipied
	#ifdef hasBCFilter
	 finalCol = getBCFilteredCol(finalCol);
	#endif
	
	#ifdef hasHSVFilter
	//todo:something wrong here. brightness seemss to go up as sat goes down
		finalCol.rgb = finalCol.rgb/finalCol.a;
		 finalCol = getHSVFilteredCol(finalCol);
	 	finalCol.rgb = finalCol.rgb*finalCol.a;
	#endif
	
	//}
	
	
		
    gl_FragColor =  finalCol; 
 	 	 	
 	 	
    
}


