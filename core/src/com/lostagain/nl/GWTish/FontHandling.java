package com.lostagain.nl.GWTish;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FontHandling {
	
	//our static font collection
	//n future load on demand?

	public static Texture texturespaced = new Texture(Gdx.files.internal("data/fonttest_spaced.png"), true);
	public static BitmapFont standdardFont = new BitmapFont(Gdx.files.internal("data/fonttest_spaced.fnt"), new TextureRegion(texturespaced), true); //font file says size is 32
	
	public static Texture texture = new Texture(Gdx.files.internal("data/standardfont.png"), true);
	
	//Note:mesh explorer will need to extend this and load these itself now we have split:
	//public static BitmapFont scramabledFont = new BitmapFont(Gdx.files.internal("data/dfieldscrambled.fnt"), new TextureRegion(texture), true);
	//public static BitmapFont standdardFont_interface = new BitmapFont(Gdx.files.internal("data/standardfont.fnt"), new TextureRegion(texture), false);

	//ArialBlackBold
	//public static Texture ArialBlackBoldTexture = new Texture(Gdx.files.internal("data/fonts/Arial_Black_BOLD.png"), true);
	//public static BitmapFont ArialBlackBoldFont = new BitmapFont(Gdx.files.internal("data/fonts/Arial_Black_BOLD.fnt"), new TextureRegion(ArialBlackBoldTexture), true); //font file says size is 32
	
	//Nirmala_BOLD
	//public static Texture Nirmala_BOLDTexture = new Texture(Gdx.files.internal("data/fonts/Nirmala_BOLD.png"), true);
	//public static BitmapFont Nirmala_BOLDFont = new BitmapFont(Gdx.files.internal("data/fonts/Nirmala_BOLD.fnt"), new TextureRegion(Nirmala_BOLDTexture), true); //font file says size is 32
	
	//Segoe UI Black_BOLD   does not work
	//public static Texture SegoeUIBlack_BOLDTexture = new Texture(Gdx.files.internal("data/fonts/SegoeUIBlackBOLD.png"), true);
	//public static BitmapFont SegoeUIBlack_BOLDFont = new BitmapFont(Gdx.files.internal("data/fonts/SegoeUIBlackBOLD.fnt"), new TextureRegion(SegoeUIBlack_BOLDTexture), true); //font file says size is 32
	
	//TektonPro_Ext_BOLD
	//public static Texture TektonPro_Ext_BOLDTexture = new Texture(Gdx.files.internal("data/fonts/TektonPro_Ext_BOLD.png"), true);
	//public static BitmapFont TektonPro_Ext_BOLDFont = new BitmapFont(Gdx.files.internal("data/fonts/TektonPro_Ext_BOLD.fnt"), new TextureRegion(TektonPro_Ext_BOLDTexture), true); //font file says size is 32

	//
	//public static Texture HomemadeAppleTexture = new Texture(Gdx.files.internal("data/fonts/HomemadeApple1.png"), true); //err
	//public static Texture HomemadeAppleTexture2 = new Texture(Gdx.files.internal("data/fonts/HomemadeApple2.png"), true); //err	 two page fonts not supported
	//public static BitmapFont HomemadeAppleFont = new BitmapFont(Gdx.files.internal("data/fonts/HomemadeApple.fnt"), new TextureRegion(HomemadeAppleTexture), true); //font file says size is 32

	//IndieFlower
	//public static Texture IndieFlowerTexture = new Texture(Gdx.files.internal("data/fonts/IndieFlower.png"), true);
	//public static BitmapFont IndieFlowerFont = new BitmapFont(Gdx.files.internal("data/fonts/IndieFlower.fnt"), new TextureRegion(IndieFlowerTexture), true); //font file says size is 32

	//Metamorphous-Regular
	//public static Texture MetamorphousRegularTexture = new Texture(Gdx.files.internal("data/fonts/Metamorphous-Regular.png"), true);
	//public static BitmapFont MetamorphousRegularFont = new BitmapFont(Gdx.files.internal("data/fonts/Metamorphous-Regular.fnt"), new TextureRegion(MetamorphousRegularTexture), true); //font file says size is 32

	//RuslanDisplay
	//public static Texture RuslanDisplayTexture = new Texture(Gdx.files.internal("data/fonts/RuslanDisplay.png"), true);
	//public static BitmapFont RuslanDisplayFont = new BitmapFont(Gdx.files.internal("data/fonts/RuslanDisplay.fnt"), new TextureRegion(RuslanDisplayTexture), true); //font file says size is 32

	//Schoolbell
	//public static Texture SchoolbellTexture = new Texture(Gdx.files.internal("data/fonts/Schoolbell.png"), true);
	//public static BitmapFont SchoolbellFont = new BitmapFont(Gdx.files.internal("data/fonts/Schoolbell.fnt"), new TextureRegion(SchoolbellTexture), true); //font file says size is 32

	
	static HashMap<BitmapFont,Float> sizecache = new HashMap<BitmapFont,Float>();
	
	public static void cacheFontSizes(){
		
		sizecache.put(standdardFont,standdardFont.getLineHeight());
		
		
		//sizecache.put(ArialBlackBoldFont,ArialBlackBoldFont.getLineHeight());
		//sizecache.put(Nirmala_BOLDFont,Nirmala_BOLDFont.getLineHeight());
//		
//		sizecache.put(standdardFont_interface,standdardFont_interface.getLineHeight());
//		sizecache.put(scramabledFont,scramabledFont.getLineHeight());
//		sizecache.put(TektonPro_Ext_BOLDFont,TektonPro_Ext_BOLDFont.getLineHeight());
//		
//		sizecache.put(IndieFlowerFont,IndieFlowerFont.getLineHeight());
//		sizecache.put(MetamorphousRegularFont,MetamorphousRegularFont.getLineHeight());
//		sizecache.put(RuslanDisplayFont,RuslanDisplayFont.getLineHeight());
//		sizecache.put(SchoolbellFont,SchoolbellFont.getLineHeight());
//	
	}
	
	
	
	/**
	 * gets the native font size.
	 * This is currently a bit of a hack job, using a cache of line-heights when the font first loads.
	 * (as lineheights might be changed later)
	 * @param font
	 * @return
	 */
	public static float getNativeFontSize(BitmapFont font){
		Float height = sizecache.get(font);
		
		if (height==null){
			height=font.getLineHeight();
			sizecache.put(font,height);
			
		}
		
		return height;
	}
	
}
