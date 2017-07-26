package com.lostagain.nl.GWTish.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Image;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.ComplexPanel.HorizontalAlignment;

/**
 * A class of static demos of widgets and multi-widget constructions
 * @author Tom
 *
 */
public class EXAMPLES {
	

	final static String logstag = "ME.EXAMPLES";
	public static Label getLabelExample(){
		
		//At its most basic, labels can be constructed like this;
		Label exampleLabel = new Label("I'm a label!");
		//The size will auto-fit the string given, according to a default size of the current font
		exampleLabel.getStyle().clearBackgroundColor();
		return exampleLabel;
	}
	
	public static Label getLabelExampleGreenGlow(){
		
		//At its most basic, labels can be constructed like this;
		Label exampleLabel = new Label("I'm green label!");
		//The size will auto-fit the string given, according to a default size of the current font
		exampleLabel.getStyle().clearBackgroundColor();
		exampleLabel.getStyle().clearShadowColor();
		Color brightGreen = new Color(0.3f,1f,0.3f,1f);
		exampleLabel.getStyle().setColor(brightGreen);
		exampleLabel.getStyle().setTextGlowColor(brightGreen);
		exampleLabel.getStyle().setTextGlowSize(1.2f);
		
		
		
		return exampleLabel;
	}
	
	public static Label getLabelExampleFunky(){
		
		//At its most basic, labels can be constructed like this;
		Label exampleLabel = new Label("I'm funky label!");
		//The size will auto-fit the string given, according to a default size of the current font
		exampleLabel.getStyle().clearBackgroundColor();
		exampleLabel.getStyle().clearShadowColor();
		
		exampleLabel.getStyle().setColor(Color.ORANGE);
		exampleLabel.getStyle().setTextOutlineColor(Color.RED);
		exampleLabel.getStyle().setTextOutineLimits(0.3f, 0.09f); //0,0  0=outer edge
		
		
		return exampleLabel;
	}
	
	
	public static Label getLabelExampleLong(){
		
		//At its most basic, labels can be constructed like this;
		Label exampleLabel = new Label("This is a very long label with a maximum width set. The idea is it should automatically word-wrap.",300);
		//The size will auto-fit the string given, according to a default size of the current font
		
		exampleLabel.getStyle().setBackgroundColor(Color.BLUE);
		exampleLabel.getStyle().setBorderColor(Color.WHITE);
		
		exampleLabel.getStyle().setPaddingLeft(150f);
		exampleLabel.getStyle().setPaddingTop(50f);
	//	exampleLabel.getStyle().setTextAlignment(Style.TextAlign.CENTER);
		
		return exampleLabel;
	}
	
	
	public static Label getLabelExampleFixedSize(){
		
		//At its most basic, labels can be constructed like this;
		Label exampleLabel = new Label("(fit\n to\n size)\n",200,450);
		
	///	Gdx.app.log(logstag,"  PaddingLeft:"+exampleLabel.getStyle().PaddingLeft);
	//	Gdx.app.log(logstag,"  PaddingRight:"+exampleLabel.getStyle().PaddingRight);
	
		
		//The size will auto-fit the string given, according to a default size of the current font
		
		exampleLabel.getStyle().setBackgroundColor(Color.BLUE);
		exampleLabel.getStyle().setBorderColor(Color.RED);
		
	//	exampleLabel.getStyle().setPaddingLeft(150f);
	//	exampleLabel.getStyle().setPaddingTop(50f);
	//	exampleLabel.getStyle().setTextAlignment(Style.TextAlign.CENTER);
		
		return exampleLabel;
	}
	
	
	public static HorizontalPanel getHorizontalPanelExample(){
		
		HorizontalPanel hpExample = new HorizontalPanel();
		Label one   = new Label("|one|");
		Label two   = new Label("|2|");
		Image three = new Image(Gdx.files.internal("data/normalmaptests/bricks_handpainted_col.png")); 
		three.setToScale(new Vector3(0.1f,0.1f,0.1f));	//image is big! scale down	-
		Label four  = new Label("|four|");
				
		hpExample.add(one,two,three,four);
	
		
		
		hpExample.getStyle().clearBackgroundColor();
		hpExample.getStyle().setBorderColor(Color.RED);
		
		return hpExample;
	}
	
	
	public static VerticalPanel getMixedExample(){
		
		VerticalPanel vpExample  = new VerticalPanel();
		Label one   = new Label("(1)");
		Label two   = new Label("(2)");
		
		HorizontalPanel threeSubPanel = new HorizontalPanel();
		Label sub1  = new Label("|3a|");
		Label sub2   = new Label("|3b|");
		
		
		Label sub3   = new Label("|3c|");
		
		
		threeSubPanel.add(sub1,sub2,sub3);
		
		Label four  = new Label("(4)");
		
		vpExample.add(one,two,threeSubPanel,four);

		vpExample.setCellHorizontalAlignment(one,   HorizontalAlignment.Left);
		vpExample.setCellHorizontalAlignment(two, HorizontalAlignment.Right);
		
		return vpExample;
	}
	
	
	
}
