package com.lostagain.nl.GWTish.Management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attribute;


/**
 * Lets us override the normal draw order. Things with positive z-index go infront of natural ordering
 * negative goes behind. In order for this to work the render sorter must support it.
 * 
 * @author Tom
 */
public class ZIndexAttribute extends Attribute {
	
	public final static String Alias = "ZIndexAttribute";
	public final static long ID = register(Alias);		
	
	public int zIndex = 0;
	
	/**
	 * we only compare to the same group atm, in future we might also have a  "_GLOBAL" group, so don't use that keyword
	 */
	public ZIndexGroup group;
	
	
	/**
	 * The presence of this parameter will override the normal draw order
	 */
	public ZIndexAttribute (int zindex,String groupname ) {		
		super(ID);				
		this.zIndex=zindex;
		this.group = ZIndexGroup.getZIndexGroup(groupname);
		
		//add to the group
		group.add(this);
		
		
	}

	public ZIndexAttribute(int zindex, ZIndexGroup group2) {
		super(ID);				
		this.zIndex=zindex;
		this.group = group2;
		
		//add to the group
		group.add(this);
		//Gdx.app.log("zindex", "____group "+group.group_id+" size is "+group.size+" ___");
	}
	

	@Override
	public Attribute copy () {
		return new ZIndexAttribute(zIndex,group);
	}

	@Override
	protected boolean equals (Attribute other) {				
		if (
			(((ZIndexAttribute)other).zIndex == zIndex) &&
			(((ZIndexAttribute)other).group == (group))
		   )
		{				
			return true;
		}
			return false;
	}
	
	@Override
	public int compareTo(Attribute o) {

		Gdx.app.log("zindex", "co_z..");
		
		if (o.type == ID ){
							
			int     co_z = ((ZIndexAttribute)o).zIndex;
			ZIndexGroup gname = ((ZIndexAttribute)o).group;
			
			
			if (this.group.equals(gname)){
				 return co_z-zIndex;	
			} else {
				 return 0;	
			}
			//Gdx.app.log("zindex", "co_z="+co_z+","+zIndex);
			
							
		}
		
		 return 0;		        
	}
	
	
	
	
	
	
	
	
}