package com.lostagain.nl.GWTish.Management;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement.RenderOrder;

/**
 * In order to more easily handle animations on objects we make them all extend this, and use this ones functions for all updates.
 * 
 * Aside from easily animating position,rotation and scale, this class also adds "lookAt" abilities, positioning objects relative, 
 * and showing/hiding them by automatically adding/removing them from the render list in model management
 * 
 * 
 * @author Tom on the excellent and helpful advice of Xoppa
 *
 */
public class AnimatableModelInstance extends ModelInstance implements IsAnimatableModelInstance, hitable {
	final static String logstag = "ME.AnimatableModelInstance";

	//Use this instead of the models matrix
	public final PosRotScale transState = new PosRotScale();

	//This is only here to mask the superclasss one. (or at least help prevent it from accidently being used, as it still can with a cast)
	//If this is used in any way there is a problem with the code!
	//check for the yellow squiggle under it in eclipse, if its missing theres likely a problem!
	private Matrix4 transform = new Matrix4();

	/** a temp variable used to remember if this should be drawn on the background or foreground. 
	 * This is used to help hide/show objects remembering their place **/
	RenderOrder currentRenderPlacement; //default

	/** list of things attached to this object. These things will all move and rotate with it **/
	HashMap<IsAnimatableModelInstance,PosRotScale> attachlist = new HashMap<IsAnimatableModelInstance,PosRotScale>();

	/** What THIS object is attached too, if anything **/
	private IsAnimatableModelInstance parentObject = null;




	//how the parent object (if any) effects this one;
	/** Determines if the position is inherited **/
	//in future we can have x/y/z separate for more advanced behaviors
	boolean inheritedPosition = true;
	/** Determines if the rotation is inherited **/
	//in future we can have x/y/z separate for more advanced behaviors
	boolean inheritedRotation = true;
	/** Determines if the scale is inherited **/
	boolean inheritedScale = true;

	/**
	 * The local bounding box is the boundary of this object, not counting anything attached to it.
	 * If its bull, it needs to be created again which any call like getWidth() will do automatically.
	 */
	private BoundingBox localBoundingBox;

	/**
	 * The collision box is the bounding box multiplied by its current transform.
	 * So, effectively, its the real boundary's the object currently has in the co-ordinate system of
	 * where its positioned.
	 * The collisionBox is updated every time its moved assuming its ever been requested.
	 * collisionBox.mul(super.getMatrixTransform());
	 **/
	private BoundingBox collisionBox;

	/**
	 * Determines if we inherit visibility from parent.
	 * NOTE: If inheriting both the parents visibility AND the local visibility have to be set to true for this object to render
	 * @param model
	 */
	public boolean inheritVisibility = true;

	/**
	 * Determines if we should render this icon or not.
	 * 
	 * NOTE: If inheriting, both the parents visibility AND the local visibility have to be set to true for this object to render
	 * Please use "isVisible()" to check for effective visibility
	 * @param model
	 */	
	public boolean localVisibility = true;


	/**
	 * If this object should be added to the hitable list
	 */
	private boolean Hitable = false;

	/** Necessary as part of hit detection **/
	private float lastHitDistance; 

	// this is just an example constructor, make sure to implement the constructor you need
	public AnimatableModelInstance (Model model) {
		super(model);
	}

	//Method used to update the transform
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setTransform(com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void setTransform ( PosRotScale newState) {

		transState.position.set(newState.position);
		transState.rotation.set(newState.rotation);
		transState.scale.set(newState.scale);

		sycnTransform();
	}

	//Method used to update the transform from a parent
	//Works exactly like setTransform, but firsts tests if a particular type of transform should be inherited
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#inheritTransform(com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void inheritTransform ( PosRotScale newState) {

		if (inheritedPosition){
			transState.position.set(newState.position);
		}
		if (inheritedRotation){
			transState.rotation.set(newState.rotation);
		}
		if (inheritedScale){

			//transState.scale.set(newState.scale);
			setToscale(newState.scale,false);


		}

		sycnTransform();
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#sycnTransform()
	 */
	@Override
	public void sycnTransform() {
		super.transform.set(transState.position, transState.rotation, transState.scale);

		//now we check if there's a collision box to update
		if (collisionBox!=null){
			recalculateCollisionBox();
		}

		//now update all attached objects too;
		updateAllAttachedObjects();
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setToPosition(com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public void setToPosition(Vector3 vector3) {		
		transState.position.set(vector3);
		sycnTransform();
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setToRotation(com.badlogic.gdx.math.Quaternion)
	 */
	@Override
	public void setToRotation(Quaternion angle) {		
		transState.rotation.set(angle);
		sycnTransform();
	}

	/**
	 * Sets the transform of this meshobject to a certain scale
	 * It will also scale all things attached to this, if they have been set to inherit the scale, 
	 * as well as scaling their relative displacement so they remain attached to the same place
	 *  
	 **/
	@Override
	public void setToScale(Vector3 scale) {	
		setToscale(scale,true);
	}

	//we have a separate method with the option to not sycn
	//this is so internal methods like inheritTransform can call setToScale without wastefully sycning
	//when they are about to do that anyway.
	private void setToscale(Vector3 scale,boolean sycn) {		

		transState.scale.set(scale);

		//if we have had our scale change we need to also scale our attachment points position
		//this is so they stayed pinned to the same place
		//for (AnimatableModelInstance object : attachlist.keySet()) {

		//	PosRotScale displacement = attachlist.get(object);
		//	displacement.position.scl(scale);
		//	updateAtachment(object, displacement);

		//}
		if (sycn){
			sycnTransform();
		}
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getMatrixTransform()
	 */
	@Override
	public Matrix4 getMatrixTransform() {		
		return super.transform;
	}

	public void hide(){	
		hide(true);
	}	


	public void hide(boolean setlocalVisibility){		
		currentRenderPlacement = GWTishModelManagement.removeModel(this);	

		if (setlocalVisibility){
			localVisibility = false;
		}

		//ensure its not on the hitable list
		GWTishModelManagement.removeHitable(this);

		//we also hide things positioned relatively to this. Nothing overrides this
		for (IsAnimatableModelInstance object : attachlist.keySet()) {
			if (object.isInheriteingVisibility()){
				object.hide(false);
			}
		}
	}

	public void show(){	
		show(true);
	}


	public void show(boolean setlocalVisibility){		

		//if we are not inheritVisibility, or our parent is visible
		if (!inheritVisibility
				|| (getParentObject()!=null && this.getParentObject().isVisible())){

			GWTishModelManagement.addmodel(this,currentRenderPlacement);

			if (Hitable){
				GWTishModelManagement.addHitable(this);
			}


		}


		if (setlocalVisibility){
			localVisibility = true;
		}

		//we also show things positioned relatively to this unless they have visible false set
		for (IsAnimatableModelInstance object : attachlist.keySet()) {
			if (object.isInheriteingVisibility()  && object.isVisible() ){
				object.show(false);
			}
		}
	}


	/**
	 * returns scaled width
	 */
	@Override
	public float getScaledWidth(){		
		return this.getWidth()*this.transState.scale.x;
	}

	/**
	 * returns scaled height
	 */
	@Override
	public float getScaledHeight(){		
		return this.getHeight()*this.transState.scale.y;
	}
	/**
	 * returns unscaled total width, based on a bounding box
	 */
	@Override
	public float getWidth(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getWidth();
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getHeight()
	 */
	/**
	 * returns unscaled total height, based on a bounding box
	 */
	@Override
	public float getHeight(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getHeight();
	}

	/**
	 * the position of the pivot relative to the bounding box center
	 * @return
	 */
	public Vector3 getPivotsDisplacementFromCenterOfBoundingBox(){

		Vector3 centerOnStage =  getCenterOnStage();
		Vector3 currentPos = this.getTransform().position;

		return centerOnStage.sub(currentPos);

	}


	/**
	 * = getCenterOfBoundingBox() * position matrix
	 * @return
	 */
	public Vector3 getCenterOnStage(){
		return getCenterOfBoundingBox().mul(getMatrixTransform());
	}

	/**
	 * Note; Gets the center position of the bounding box if it was scaled but not positioned, you need to multiply by the position to get the center on the stage
	 */
	public Vector3 getCenterOfBoundingBoxScaled(){
		return getCenterOfBoundingBox().scl(getTransform().scale);
	}

	/**
	 * Note; Gets the center position of the bounding box if it was unscaled, you need to multiply by the position to get the center on the stage
	 */
	@Override
	public Vector3 getCenterOfBoundingBox(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		Vector3 center = new Vector3();
		return localBoundingBox.getCenter(center);
	}

	/**
	 * The bounding box of this object if it was not scaled
	 */
	private void createBoundBox() {

		localBoundingBox = new BoundingBox();

		super.calculateBoundingBox(localBoundingBox);

	}


	private void recalculateCollisionBox() {

		//the bounding box is a prerequisite
		if (localBoundingBox==null){
			createBoundBox();

		}
		//so is an existing collisionBox
		if (collisionBox==null){
			collisionBox = new BoundingBox();

		}
		//ok, now we know we have both we set one to the other
		collisionBox.set(localBoundingBox);

		//Then the collision box gets multiplied by our current position so its boundary's match the real space co-ordinates
		collisionBox.mul(getMatrixTransform());

		//Gdx.app.log(logstag,"collision box matrix="+getMatrixTransform());
		//Gdx.app.log(logstag,"collision box="       +collisionBox);
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getLocalCollisionBox()
	 */

	/**
	 * 
	 * @param onceOnly - indicate that you dont want the collision box to auto-update when this object is moved
	 * @return
	 */
	@Override
	public BoundingBox getLocalCollisionBox(boolean onceOnly) {

		if (collisionBox==null){
			recalculateCollisionBox();
		}
		BoundingBox collisionBoxTemp = collisionBox;

		if (onceOnly){
			collisionBox=null; //clear so we dont update again
		}

		return collisionBoxTemp;
	}

	@Override
	public BoundingBox getLocalCollisionBox() {
		return getLocalCollisionBox(false); 
		/*
		if (collisionBox==null){
			recalculateCollisionBox();
		}
		return collisionBox;*/
	}


	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getLocalBoundingBox()
	 */
	@Override
	public BoundingBox getLocalBoundingBox() {
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox;
	}

	/**
	 * should not really need to be public only temp while testing things to ensure it isn't the bounding box being inaccurate
	 */
	public void wasResized(){
		if (localBoundingBox!=null){
			createBoundBox();
		}
		if (collisionBox!=null){
			recalculateCollisionBox();
		}

	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#attachThis(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void attachThis(IsAnimatableModelInstance objectToAttach, PosRotScale displacement){

		//	Gdx.app.log(logstag,"_____________________________________adding object "); 

		//add if not already there
		if (!attachlist.containsKey(objectToAttach))
		{
			attachlist.put(objectToAttach, displacement);

			//associate this as the parent object
			//objectToAttach.parentObject=this;
			objectToAttach.setParentObject(this);

			//give it a initial update
			updateAttachedObject(objectToAttach);

			//PosRotScale newposition = transState.copy().displaceBy(displacement); //attachlist.get(objectToAttach)
			//objectToAttach.inheritTransform(newposition);


		} else {
			Gdx.app.log(logstag,"_____________________________________already attached so repositioning to new displacement"); 
			this.updateAtachment(objectToAttach, displacement);

			//give it a initial update
			updateAttachedObject(objectToAttach);

			//PosRotScale newposition = transState.copy().displaceBy(displacement);
			//objectToAttach.inheritTransform(newposition);

		}

		//	Gdx.app.log(logstag,"_____________________________________total objects now: "+attachlist.size()); 

	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#removeAttachment(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public void removeAttachment(IsAnimatableModelInstance objectToRemove){

		if (attachlist.containsKey(objectToRemove))
		{
			attachlist.remove(objectToRemove);
			//remove this as the parent object
			//objectToRemove.parentObject=null;
			objectToRemove.setParentObject(null);

		}


	}

	private void updateAttachedObject(IsAnimatableModelInstance objectToUpdate){

		PosRotScale displacement = attachlist.get(objectToUpdate).copy();			
		displacement.position = displacement.position.scl(transState.scale); //displacement needs to be scaled			
		PosRotScale newposition = transState.copy().displaceBy(displacement);


		objectToUpdate.inheritTransform(newposition);
	}

	protected void updateAllAttachedObjects(){

		//Gdx.app.log(logstag,"_____________________________________updateAllAttachedObjects ="+attachlist.size()); 

		for (IsAnimatableModelInstance object : attachlist.keySet()) {


			updateAttachedObject(object);


		}

	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#updateAtachment(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void updateAtachment(IsAnimatableModelInstance object,
			PosRotScale displacement) {

		attachlist.put(object, displacement);
		updateAttachedObject(object);


	}

	public PosRotScale getAttachmentsPoint(AnimatableModelInstance object){
		return attachlist.get(object);
	}






	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#lookAt(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public void lookAt(IsAnimatableModelInstance target){			
		Quaternion angle = getAngleTo(target);			
		setToRotation(angle);			
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#lookAt(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public void lookAt(IsAnimatableModelInstance target, Vector3 Axis){			
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);			
	}

	@Override
	public void lookAt(Vector3 target, Vector3 Axis){			
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);			
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAngleTo(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public Quaternion getAngleTo(IsAnimatableModelInstance target) {
		return  getAngleTo(target, new Vector3(1,0,0));
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAngleTo(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public Quaternion getAngleTo(IsAnimatableModelInstance target, Vector3 Axis) {
		return getAngleTo(target.getTransform().position, Axis);
		//	return getAngleTo(target.transState.position, Axis);
	}

	@Override
	public Quaternion getAngleTo(Vector3 target, Vector3 Axis) {

		Vector3 thisPoint   = this.transState.position.cpy();
		//Vector3 targetPoint = target.transState.position.cpy();
		Vector3 targetPoint = target.cpy();

		//get difference (which is the same as target relative to 0,0,0 if this point was 0,0,0)
		targetPoint.sub(thisPoint);			
		targetPoint.nor();

		//test if they are in the same the same place, if so just return default angle
		if (targetPoint.len() < 0.01f) 
		{
			return new Quaternion();
		}

		//else we use this difference and find its angle relative to the Axis
		//Vector3 xAxis = new Vector3(1,0,0);

		Quaternion result = new Quaternion();		

		result.setFromCross(Axis,targetPoint);


		return result;

	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAttachments()
	 */
	@Override
	public Set<IsAnimatableModelInstance> getAttachments() {

		return attachlist.keySet();
	}


	/**
	 * gets all attachments and child attachments
	 */
	@Override
	public Set<IsAnimatableModelInstance> getAllAttachments() {

		Set<IsAnimatableModelInstance> attachments = new HashSet();
		Set<IsAnimatableModelInstance> direct_children = new HashSet<IsAnimatableModelInstance>(attachlist.keySet());

		attachments.addAll(direct_children);

		for (IsAnimatableModelInstance childAttach : direct_children) {

			attachments.addAll(childAttach.getAllAttachments());

		}				


		return attachments;
	}



	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedPosition(boolean)
	 */
	@Override
	public void setInheritedPosition(boolean inheritedPosition) {
		this.inheritedPosition = inheritedPosition;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedRotation(boolean)
	 */
	@Override
	public void setInheritedRotation(boolean inheritedRotation) {
		this.inheritedRotation = inheritedRotation;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedScale(boolean)
	 */
	@Override
	public void setInheritedScale(boolean inheritedScale) {
		this.inheritedScale = inheritedScale;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedVisibility(boolean)
	 */
	/**
	 * sets the visibility to be inherited from any parent object.
	 */
	@Override
	public void setInheritedVisibility(boolean inheritVisibility) {
		this.inheritVisibility = inheritVisibility;

		//update our visibility
		//if our local visibility is false we just ensure we are hidden, nothing else to change
		if (localVisibility==false){
			this.hide(false);


			return;
		} else {
			//if our local visibility is true then we base it on the parent setting
			if (getParentObject()!=null && this.getParentObject().isVisible()){
				this.show(false); //NOTE the false, this is used so we dont disturb the local visibility setting
			} else {
				this.hide(false);
			}


		}

	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#isInheriteingVisibility()
	 */
	@Override
	public boolean isInheriteingVisibility() {
		return inheritVisibility;
	}

	@Override
	public boolean isVisible() {

		//if local visibility is false, or we are not inheriting the visibility, then our localvisibility should match are visibility
		if (getParentObject()==null || localVisibility==false || !inheritVisibility ){
			return localVisibility;
		}

		///if we are inheriting and we are not hidden then our visibility should match our parents
		return getParentObject().isVisible();

	}

	/**
	 * If this is set to render in the overlay list
	 * @return
	 */
	public boolean isOverlay() {

		if (currentRenderPlacement == RenderOrder.OVERLAY){
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Adds it to the hitable list. This is a list of things designed to be clicked on or shot at.
	 * ie. Fires events when a ray hits.
	 * You will need to override the public boolean rayHits(Ray ray) for your own logic to determine
	 * if a ray hits.
	 * Note; When a object is hidden it will automatically be removed from the hitables list.
	 *  
	 * @param hitable
	 */
	public void setAsHitable(boolean hitable){
		this.Hitable = hitable;

		if (Hitable){
			if (isVisible()){
				GWTishModelManagement.addHitable(this);
			}
		} else {
			GWTishModelManagement.removeHitable(this);
		}


	}

	@Override
	public PosRotScale getTransform() {
		return this.transState;
	}

	@Override
	public void fireTouchDown() {

	}

	@Override
	public void fireTouchUp() {

	}
	@Override
	public void fireClick() {

	}
	@Override
	public void fireDragStart() {

	}

	//The following two just keep track of where this hit was in the stack of hits for a given ray
	@Override
	public void setLastHitsRange(float range) {
		lastHitDistance = range;

	}

	@Override
	public float getLastHitsRange() {
		return lastHitDistance;
	}


	/**
	 * does this object block whats behind it?
	 * @return
	 */
	@Override
	public objectInteractionType getInteractionType() {
		return objectInteractionType.Normal;
	}

	/**
	 * Defaults to intersectRayBounds against bounding box, but you can override for more precision if needed.
	 * Note; You can also override to make this FASTER!
	 * If you dont need precise distances to work out which object is "in front" of other ones, you can override
	 * to use intersectRayBoundsFast instead, and just return a null for no hit, and your own values for the Vector3
	 * to set which is in front.
	 * For example, a 2d game could be optimised just to return the Z distance as its hit point.
	 * As the higher z values will always be "in front" on a 2d game.
	 */
	@Override
	public Vector3 rayHits(Ray ray) {
		//boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
		//Gdx.app.log(logstag,"testing for hit on object:"+hit);


		//new more precise distance test
		Vector3 intersection = new Vector3();
		boolean hit = Intersector.intersectRayBounds(ray, this.getLocalCollisionBox(), intersection);
		//Gdx.app.log(logstag,"testing for hit on object:"+hit);
		if (hit){
			return intersection;
		}
		return null;
	}

	protected boolean isHitable() {

		return GWTishModelManagement.hitables.contains(this);

	}


	public boolean hasAttachment(AnimatableModelInstance object) {
		return attachlist.containsKey(object);
	}


	public void setAsOverlay(boolean status){

		if (status){
			currentRenderPlacement = RenderOrder.OVERLAY;
			GWTishModelManagement.setAsOverlay(this);			
		} else {
			currentRenderPlacement = RenderOrder.STANDARD;
			GWTishModelManagement.setAsStandard(this);			
		}

	}

	/**
	 * @return the parentObject
	 */
	public IsAnimatableModelInstance getParentObject() {
		return parentObject;
	}

	/**
	 * @param parentObject the parentObject to set, detaching from any existing ones first
	 */
	public void setParentObject(IsAnimatableModelInstance parentObject) {
		if (this.parentObject!=null){
			this.parentObject.removeAttachment(this);	
		}
		this.parentObject = parentObject;
	}


	/**
	 * Note; returns the displacement specification of the specified object from this one. NOT a copy of it.
	 * Changing the returned value will change the displacement
	 */
	public PosRotScale getAttachmentsPoint(IsAnimatableModelInstance object){
		return attachlist.get(object);
	}

	/**
	 * To help debugging you can optionally give objects a name
	 */
	@Override
	public String getName() {
		return "AnimatedModalInstance";
	}



}
