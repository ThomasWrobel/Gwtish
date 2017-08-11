# Gwtish
A 3d ui,style and interaction library for libgdx
Makes it easy to add streetsigns, readable newspapers or even interactive computer terminals to your game.

Whos it for?
- LibGDX users working on 3d games or apps
- GWT users looking to make 3d games using a familiar syntex to what they are used too

Whoes it not for?
- LibGDX users working on 2d games, or games where all the text is over the 3d, would be better of using Scene2D built into gdx
(ie, if Occusions arnt a concern)

Pros/Cons of alternatives;
- GWTish is likely slower then other options, and likely still has bugs.
- Using Scene2d and converting to a texture via a frame buffer:
- Using static prebuilt textures:

GWTish strengths:

- Quickly add text to your game
    Label examplelab = new Label("Hello World");		

- These objects are model instances ready to use
    examplelab.setToPosition(new Vector3(550,-50,0));

- Text looks good upclose thanks to a Distance Field Shader

- Change text whenever you like easily;

     examplelab.setText("New text!...");

- Make the object interactive easily:

- CSS like styleing...

    examplelab.getStyle().setFontSize(42, Unit.PX); 
    examplelab.getStyle().setColor(Color.MAGENTA);
    examplelab.getStyle().setFont(FontHandling.RuslanDisplayFont);
    examplelab.getStyle().setShadowColor(Color.BLACK); 
    
- Even css like animations..

		examplelab.getStyle().addTransitionState(StyleParam.color,0.0f,Color.RED);
		examplelab.getStyle().addTransitionState(StyleParam.color,0.2f,Color.ORANGE);
		examplelab.getStyle().addTransitionState(StyleParam.color,0.4f,Color.YELLOW);
		examplelab.getStyle().addTransitionState(StyleParam.color,0.6f,Color.GREEN);
		examplelab.getStyle().addTransitionState(StyleParam.color,0.8f,Color.BLUE);
		examplelab.getStyle().addTransitionState(StyleParam.color,0.9f,Color.PURPLE);
		//set transition time
		examplelab.getStyle().setTransitionLength(5000.0f); 
    
- Build complex layouts using various panels;

(recently split from my meshexplorer project, so details are still there for now)

see;
https://github.com/ThomasWrobel/MeshExplorerGDX/wiki/Gwtish-widget-examples


