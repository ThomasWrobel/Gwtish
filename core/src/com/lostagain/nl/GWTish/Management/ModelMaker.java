package com.lostagain.nl.GWTish.Management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;

public class ModelMaker {

	final static String logstag = "ME.ModelMaker";
	static public ModelInstance createRectangleAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(0, 0, w,h, 0,null,mat,true ));

		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	
	static public ModelInstance createRectangleEndCenteredAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {
		return createRectangleEndCenteredAt( x,  y, z,  w,  h, MColor, mat,0,0);
	}
	
	static public ModelInstance createRectangleEndCenteredAt(int x, int y,int z, int w, int h,Color MColor,Material mat,float disX,float disY) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(-(w/2)+disX, disY, (w/2)+disX,h+disY, 0,null, mat,true));

		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	
	
	/**
	 * Creates a model rectangle. At points x1/y1 to x2/y2 at height z.
	 * If material is null it uses a default one
	 *
	 * @param x1 - left
	 * @param y1 - top 
	 * @param x2 - right
	 * @param y2 - bottom
	 * @param z
	 * @param mat
	 * @return
	 */
	static public Model createRectangle(float x1,float y1,float x2,float y2,float z,Color mColour ,Material mat,boolean topleftPin ) {
		
		if (topleftPin){
		//move y values down so its "pinned" at the top left rather then bottom left
		float h = y1-y2;
		y1=y1+h;
		y2=y2+h;
		}
		
		//
		Vector3 corner1 = new Vector3(x1,y1,z); //top left
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z); //bottom right
		Vector3 corner4 = new Vector3(x1,y2,z);	
	
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshPartBuilder;

		
		
		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		
		if (mColour==null){
		 mColour = Color.WHITE; //default
		}
		
		if (mat!=null){
			meshPartBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);
			//this will create a nodepart called "bit" under a new node automatically called "node1"
			
		} else {
			
			Material defaultmaterial = new Material(ColorAttribute.createDiffuse(mColour), 
					ColorAttribute.createSpecular(mColour),new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f));
			
			meshPartBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, defaultmaterial);
			//this will create a nodepart called "bit" under a new node automatically called "node1"
			
		}
		
		if (true){
		
			meshPartBuilder.rect(
					x1, y1, 0, 
					x2, y1, 0,
					x2, y2, 0, 
					x1, y2, 0, 
					0, 1, 0);
			
			
			
			return modelBuilder.end();
		}
		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, mColour, new Vector2(0f,0f)); // is this uv order correct? 

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, mColour, new Vector2(1f,0f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, mColour, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, mColour, new Vector2(0f,1f));
		
		meshPartBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		Model model = modelBuilder.end();
		

		return model;
	}

	

}
