package com.lostagain.nl.GWTish.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * To help manage shader.
 * Stores custom attributes that can be used for object materials, these custom attributes will then trigger a specific shader
 * 
 * @author Tom
 *
 */
public class GwtishDefaultShaderProvider extends DefaultShaderProvider {
	public final DefaultShader.Config config;
	final static String logstag = "ME.MyShaderProvider";
	
	//known shaders
	static public enum shadertypes {		
		distancefieldfordataobjects
	}

	public GwtishDefaultShaderProvider (final DefaultShader.Config config) {
		this.config = (config == null) ? new DefaultShader.Config() : config;
	}

	public GwtishDefaultShaderProvider (final String vertexShader, final String fragmentShader) {
		this(new DefaultShader.Config(vertexShader, fragmentShader));
		
		
	}

	public GwtishDefaultShaderProvider (final FileHandle vertexShader, final FileHandle fragmentShader) {
		this(vertexShader.readString(), fragmentShader.readString());
	}

	public GwtishDefaultShaderProvider () {
		this(null);
	}
	
	public void testListShader(Renderable instance){
		
		for (Shader shader : shaders) {
			
			Gdx.app.log(logstag, "shader="+shader.getClass().getName());
			
			Gdx.app.log(logstag, "can render="+shader.canRender(instance));
			
		}
	}
	
	@Override
	protected Shader createShader (final Renderable renderable) {
		
		//New method for selection (we should slowly move the things from the switch statement to this method)
		if (renderable.material.has(GwtishWidgetShaderAttribute.ID)){
			return new GwtishWidgetShader(renderable);
		}
		/*
		if (renderable.material.has(ConceptBeamShader.ConceptBeamAttribute.ID)){
			return new ConceptBeamShader();			
		}		
		if (renderable.material.has(ConceptBeamImpactShader.ConceptBeamImpactAttribute.ID)){
			return new ConceptBeamImpactShader();			
		}	
		if (renderable.material.has(NoiseShader.NoiseShaderAttribute.ID)){
			return new NoiseShader(renderable);			
		}		
		if (renderable.material.has(PrettyNoiseShader.PrettyNoiseShaderAttribute.ID)){
			return new PrettyNoiseShader();			
		}		
	//	if (renderable.material.has(DistanceFieldShader.DistanceFieldAttribute.ID)){
	//		return new DistanceFieldShader();			
	//	}				
		if (renderable.material.has(PrettyBackground.PrettyBackgroundAttribute.ID)){
			return new PrettyBackground();		
		}		
		if (renderable.material.has(InvertShader.InvertAttribute.ID)){
			return new InvertShader(renderable);		
		}
		if (renderable.material.has(TextureNoiseShader.TextureNoiseAttribute.ID)){
			return new TextureNoiseShader(renderable);		
		}	
	////	if (renderable.material.has(GwtishWidgetBackgroundAttribute.ID)){
	//		return new GwtishWidgetShader();
	//	}
		if (renderable.material.has(NormalMapShaderAttribute.ID)){
			return new NormalMapShader(renderable);
		}
		if (renderable.material.has(GameBackgroundShaderAttribute.ID)){
			return new GameBackgroundShader(renderable);
		}
		//------------------------------------------------------------------------------------------------------
		//------------------------------------------------------------------------------------------------------
		
		
		//pick shader based on renderable? (old method, being replaced with the above)

		
		if (renderable.userData==null || renderable.userData instanceof String){
			return super.createShader(renderable);
		}
		shadertypes shaderenum = (shadertypes) renderable.userData;
			
		Gdx.app.log(logstag, "shaderenum="+shaderenum.toString());
			
			
		switch (shaderenum) {
							
		case distancefieldfordataobjects:
		{
			return new DistanceFieldShaderForDataObjects();
		}
		default:
			return super.createShader(renderable);
			
		}*/
		
		return new DefaultShader(renderable, new DefaultShader.Config());
		
	}
}