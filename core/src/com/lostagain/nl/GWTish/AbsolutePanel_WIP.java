package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector3;

/**
 * An aprox emulation of gwts ebsolute panel
 * 
 * An absolutepanel positions all of its children absolutely, relative to the panels pivot. (Default top left like GWT)
 * 
*  Like GWTs version, this panel will not automatically resize itself to allow enough room for its absolutely-positioned children. It must be explicitly sized in order to make room for them.
** 
 * 
 * @author darkflame
 *
 */
//note; placeholder. this class might end up not extending ComplexPanel as the widget adding procedure is needlessly complex for a this panel.
//really widgets just need to be attached and set to inherit visibility, without positioning functions needed.
public class AbsolutePanel_WIP extends ComplexPanel {

	public AbsolutePanel_WIP(float sizeX, float sizeY) {
		super(sizeX, sizeY);
	}
	public AbsolutePanel_WIP(float sizeX, float sizeY, MODELALIGNMENT align) {
		super(sizeX, sizeY, align);
	}

	
	@Override
	void sizeToFitContents() {
		//not used
	}

	@Override
	void repositionWidgets() {
		//not used
	}

	@Override
	Vector3 getNextPosition(float width, float height,  Widget widget) {
		// TODO Auto-generated method stub
		return null;
	}

}
