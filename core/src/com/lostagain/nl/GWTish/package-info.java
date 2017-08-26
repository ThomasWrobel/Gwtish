/**
 * Gwtish is a 3D widget libbary for libgdx. <br><br>
 * That is, a collection of model instances, model manager, and specific shader to easily allow you to do things like the following in your 3d game;<br>
 * <br>
 *  - Streetsigns (One line "label" object can do this)<br>
 *  - Newspapers (built from various layout panel widgets)<br>
 *  - Computer interfaces (Labels and images can be compossed in panels of various sorts, and interactivity added by simple .addClickHandler(..) on the objects)<br>
 *  <br>
 *  - All widgets can have interactivity, but special buttons are supplied too.<br>
 *  - Widgets can be arbitarily attached together, if one moves the other will too as if fixed too it<br>
 *  - Label objects can have lots of css-like styling applied very easily....including animations!<br>
 *  - Widgets can be placed very close together using z-index emulation (at cost of sort speed)<br>
 *  - Text is rendered from distance field fonts using a special shader that keeps it sharp up close <br>
 *  - Image objects can have their hsv as well as brightness/contrast adjusted in a css like way.(inc transitions)<br>
 *  <br>
 *  The libary is designed to match GWT syntax and some html/css behavour, this was a combination of my own preferance, and wanting
 *  to port some of my GWT code over.<br>
 *  <br>
 *  FAQ:<br>
 *  * why not use Scene2D?<br>
 *  Because its 2D :) If libgdxs built in lib is good for your game/app, use it! Its more stable/mature then this.<br>
 *  If, however, you need text objects that correctly occlude behind objects in your 3d game, this is probably easier.<br>
 *  <br>
 *  * why not use just a texture for signs?<br>
 *  Sure, if you just got one or two, and only want one language supported. <br>
 *  If you need a selection of text, or it potential can change, GWTish labels will probably save you a lot of work<br>
 *  Label signpost= new Label("Danger!");<br>
 *  Not to mention as the shader is distance field based, they stay sharp up close!<br>
 *  
 *<br>
 *  How to setup:<br>
 *  <br>
 *  1. Add project to build path<br>
 *  2. Add inherit to gwt.xml if using html:<br>
 *  <inherits name='com.lostagain.nl.GWTish' /><br>
	<br>
 *  3. Add setup to your code in create()<br>
 *  	GWTishModelManagement.setup(); <br>
 *    Use .setup(boolean useSmartSorter, ShaderProvider shaderprovider) to provide your own shaderprovider, but ensure it assigns the widget shader<br>
 *  	    if (renderable.material.has(GwtishWidgetShaderAttribute.ID)){<br>
			    return new GwtishWidgetShader(renderable);<br>
		        }<br>
 *<br>
	* 4. Add in render() for interactivity support;<br>
	* 	GWTishModelManagement.updateTouchState();	<br>
		GWTishModelManagement.updateObjectMovementAndFrames(delta);<br>
	<br>	
 *  <br>
 *  You can also look at the example scene in:<br>
 *  com.lostagain.nl.GWTish.tests.DemoScene<br>
 *<br>
 * @author Thomas Wrobel  (with help from Xoppa and thanks to the whole libgdx community)
 *
 */
package com.lostagain.nl.GWTish;
