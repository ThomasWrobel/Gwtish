# Gwtish
A 3D UI,style and interaction library for [libgdx](https://libgdx.badlogicgames.com/)
Makes it easy to add streetsigns, readable newspapers or even interactive computer terminals to your game.

## **Whos it for?**
- LibGDX users working on 3D games or apps
- GWT users looking to make 3D games using a familiar syntax to what they are used too
- Or people who want to make use of GWTishs text shader, or [AnimatedModelInstance](https://github.com/ThomasWrobel/Gwtish/wiki/AnimatedModelInstance) systems separately. 

## Whoes it _not_  for?
- LibGDX users working on 2D games, or games where all the text is over the 3D, would be better of using Scene2D built into gdx
(ie, if Occlusions aren't a concern)

## **Pros/Cons of alternatives;**
- GWTish is likely slower then other options, and likely still has bugs.

- Using Scene2D and converting to a texture via a frame buffer:

   _Pros:_  use more mature built in system, very flexible if you put the work in.

   _Cons:_  More processing needed at runtime. Harder to setup. Resolution issues.

- Using static prebuilt textures:

   _Pros:_  Most simple. No libs needed. Any style you can draw.

   _Cons:_  Time taking to update. More space used. Pain to do multiple languages. Looks bad up close unless very high res.


## **GWTish strengths:**

- Quickly add text to your game:

`    Label examplelab = new Label("Hello World");`		

- These objects are model instances ready to use:

`    examplelab.setToPosition(new Vector3(550,-50,0));`

- Text looks good upclose thanks to [Valves Distance Field Shader method](http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf)

- Change text whenever you like easily:

`     examplelab.setText("New text!...");`

- Make the object interactive easily:

- CSS like styleing...

   ` examplelab.getStyle().setFontSize(42, Unit.PX); `
    `examplelab.getStyle().setColor(Color.MAGENTA);`
    `examplelab.getStyle().setFont(FontHandling.RuslanDisplayFont);`
    `examplelab.getStyle().setShadowColor(Color.BLACK); `
    
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

                 VerticalPanel overallFrame = new VerticalPanel();
                 HorizontalPanel dateandprice = new HorizontalPanel();

		Label price = new Label("$1.49");
		Label spacer = new Label(" ~~ ");		
		Label date = new Label("16th Oct");

		final Label headline = new Label("Headline",35);
		final Label subheadline = new Label("subheadline",35);
		HorizontalPanel horizontalFrame = new HorizontalPanel();
		Label randomtext1 = new Label("Story text 1 in left column under headline",14);

		VerticalPanel rightPanelFrame = new VerticalPanel();
		Image npimage =new Image ("badlogic.jpg");
		npimage.setSizeAs(12, 12);

		Label randomtext2 = new Label("Text under image",14);
		//layout
		dateandprice.add(price,spacer,date);
		horizontalFrame.setCellVerticalAlignment(rightPanelFrame, VerticalAlignment.Top);

		overallFrame.add(dateandprice,headline,subheadline,horizontalFrame);
		horizontalFrame.add(randomtext1,rightPanelFrame);
		rightPanelFrame.add(npimage,randomtext2);


