package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Stores position, rotation and scale and some useful functions
 * 
 * The intention is to use this rather then constantly converting to a Matrix4 and back again to interpolate stuff between states
 * 
 * Note;
 * Position - position in world space
 * Rotation - rotation in world space
 * Scale - The objects scale (I think this is thus different to a matrix where it would be the scale or the co-ordinates relative to 0,0,0 or something)
 * 
 * @author Tom
 *
 */
public class PosRotScale {

	private static String logstag="ME.PosRotScale";
	
	public Vector3 position = new Vector3();
	public Quaternion rotation = new Quaternion();		
	public Vector3 scale = new Vector3(1f,1f,1f);
	
	
	public PosRotScale(Vector3 position, Quaternion rotation, Vector3 scale) {
		super();
		
		this.position = position.cpy();
		this.rotation = rotation.cpy();
		this.scale = scale.cpy();
		
	}
	/**
	 *  Stores a position, rotation and scale.
	 *  
	 *  Scale defaults to 1f,1f,1f
	 *  The rest 0,0,0
	 *  
	 *  
	 */
	public PosRotScale() {
		
	}
	
	public PosRotScale(Matrix4 setToThis) {
		
		this.setToMatrix(setToThis);
		
		
	}
	
	/**<br>
	 * Displays Pos,Rot and Scale in the form<br>
	 * <br>
	 * [0,0,0][0,0,0,0][0,0,0]<br>
	 *  which is<br>
	 * [x,y,z][[angleaxis],[angle]][scale x,scale y,scale z]<br>
	 * <br>
	 *  axis angle is in degrees<br>
	 */
	@Override
	public String toString(){
		
		Vector3 axis = new Vector3();
		float angle = rotation.getAxisAngle(axis);
		
		
		String stateAsString = "["+position.x+","+position.y+","+position.z+"]"
				             + "["+axis.x+","+axis.y+","+axis.z+","+angle+"]"
				             + "["+scale.x+","+scale.y+","+scale.z+"]";
		
		return stateAsString;
		
		
	}

	/**
	 * creates a new pos rot scale with only a position set to x,y,z  scale remains 1,1,1 and rotation 0,0,0
	 * @param x
	 * @param y
	 * @param z
	 */
	public PosRotScale(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	/**
	 * creates a new pos rot scale with only a position set to the one supplied  scale remains 1,1,1 and rotation 0,0,0
	 * @param x
	 * @param y
	 * @param z
	 */
	public PosRotScale(Vector3 newpos) {
		position.x = newpos.x;
		position.y = newpos.y;
		position.z = newpos.z;
	}

	/**
	 * This displaces one PosRotScale by another
	 * The incoming changeBy is treated as being as relative to current object and its rotation
	 * So a change of x/y/z  50,0,0 in position on a object at 10,10,10 will result in 60,10,10 if its angle is zero.  
	 * If the angle is not zero however, it will move 50,0,0 relative to where its currently "looking"  
	 * 
	 * Locations are added relative to the objects rotation
	 * Rotations also just get added 
	 * Scaling, however, gets multiplied
	 * 
	 * @return
	 */
	public PosRotScale displaceBy(PosRotScale changeByThisAmount) {
		
		//prerotate its location (this ensures we are working in co-ordinates relative to the current object)
		PosRotScale changeByThisAmountRot = changeByThisAmount.copy();
		
		Vector3 existingAxis = new Vector3();
		
		float existingAngle = rotation.getAxisAngle(existingAxis);		
		changeByThisAmountRot.position.rotate(existingAxis,existingAngle);
		
		
		
		
		//------------------	
		position = position.add(changeByThisAmountRot.position);		
		rotation = rotation.mul(changeByThisAmountRot.rotation);
		
		scale = scale.scl(changeByThisAmountRot.scale);

		//Gdx.app.log(logstag, " new scale: "+scale.toString() );	
		
		
		return this;
	}
	
	public PosRotScale copy() {
		
		return new PosRotScale(position.cpy(),rotation.cpy(),scale.cpy());
	}

	/**
	 * Not yet sure of this function, especially about scaling position(?)
	 * @param lastLocation
	 */
	public void setToMatrix(Matrix4 lastLocation) {
		
		lastLocation.getTranslation(position); 
		lastLocation.getRotation(rotation);
		lastLocation.getScale(scale); 
		
	}

	/**
	 * replaces the Rotation with the new Rotation  (doesn't effect anything else)
	 * @param this
	 */
	public PosRotScale setToRotation(float i, float j, float k, float angleInDeg) {
		rotation.set(new Vector3(i,j,k), angleInDeg);
		//Gdx.app.log(logstag, " setting rot to angle :"+rotation.getAngle());		
		
		return this;
	}

	
	/**
	 * replaces the position with the new position  (doesn't effect anything else)
	 * @param destination_loc
	 */
	public PosRotScale setToPosition(Vector3 newposition) {
		position = newposition.cpy();

		return this;
	}

	/**
	 * replaces the scale with the new scale (doesn't effect anything else)
	 * @param newscale
	 * @return
	 */
	public PosRotScale setToScaling(Vector3 newscale) {
		scale = newscale.cpy();
		
		return this;
	}

	/**
	 * makes a matrix from this PosRotScale 
	 * 
	 * basically; new Matrix4(position,rotation.nor(),scale);
	 * 
	 * Note the rotation is normalized, which is required by Matirx4
	 * 
	 * @return
	 */
	public Matrix4 createMatrix() {
		
		// TODO Auto-generated method stub
		return  new Matrix4(position,rotation.nor(),scale);
	}
	
	public void setTo(PosRotScale objectsOrigin) {
		position = objectsOrigin.position.cpy();
		rotation = objectsOrigin.rotation.cpy();
		scale = objectsOrigin.scale.cpy();
		
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PosRotScale other = (PosRotScale) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (rotation == null) {
			if (other.rotation != null)
				return false;
		} else if (!rotation.equals(other.rotation))
			return false;
		if (scale == null) {
			if (other.scale != null)
				return false;
		} else if (!scale.equals(other.scale))
			return false;
		return true;
	}
	
}
