package com.lostagain.nl.GWTish;

import com.badlogic.gdx.graphics.Color;

/**
 * used purely in FlowPanels to designate newlines
 * think of it like a BR
 * 
 * @author darkflame
 *
 */
public class NewLinePlacer extends Widget {

	public NewLinePlacer() {
		super(0, 0);
	}

	public NewLinePlacer(boolean visible) {
		super(0, 0);
		if (visible){
			setMinSize(15, 15); //to help tests make it visible
			getStyle().setBackgroundColor(Color.RED);
		
		}
	}
}
