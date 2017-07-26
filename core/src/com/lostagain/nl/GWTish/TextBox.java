package com.lostagain.nl.GWTish;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
//
//[ERROR] Errors in 'file:/E:/TomsProjects/MeshExplorerV2/core/src/com/lostagain/nl/GWTish/TextBox.java'
//[ERROR] Line 177: Character.UnicodeBlock cannot be resolved to a type
//[ERROR] Line 177: UnicodeBlock cannot be resolved or is not a field
//[ERROR] Line 178: The method isISOControl(char) is undefined for the type Character
//[ERROR] Line 181: UnicodeBlock cannot be resolved or is not a field
//
//TODO:need gwt frfiendly isPrintableChar
/**
 * A standard single-line input text box.
 ***/
public class TextBox extends Label implements InputProcessor {
	final static String logstag = "ME.TextBox";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag
	
	
	//static demand a input multiplexer
	static InputMultiplexer inputMultiplexer;
	/**
	 * Required to be set before any input boxs will work 
	 * @param inputMultiplexer
	 */
	static public void setInputMultiplexer(InputMultiplexer inputMultiplexer) {
		TextBox.inputMultiplexer = inputMultiplexer;
	}
	
	/**
	 * has a inputMultiplexer been set yet?
	 * A inputMultiplexer is required to be set before any input boxs will work, set one with  setInputMultiplexer(..)
	 *
	 * @return
	 */
	static public boolean isInputMultiplexerSet(){
		if (TextBox.inputMultiplexer!=null){
			return true;			
		} else {
			return false;
		}
	}

	
	/**
	 * NOTE; input boxs wont receive key events till you set a input multiplexer
	 * TextBox.setInputMultiplexer()
	 * This only needs to be done once. All Input boxes will then add themselves too it automatically
	 * 
	 * TextBoxs have a default border and backstyle to show focus, you can either extend and override, or use Blur and Focus handlers to set your own.
	 * 
	 * @param defaulttext
	 */
	public TextBox(String defaulttext) {
		super(defaulttext);
		super.setAsHitable(true);
		
		if (inputMultiplexer==null){
			Log.severe("attempted to use TextBox but no inputMultiplexer has been set. Use TextBox.setInputMultiplexer(..)  to set one");
			return;
		}

		this.getStyle().setBackgroundColor(Color.DARK_GRAY);
        this.getStyle().setBorderColor(Color.LIGHT_GRAY);
        
        
		
	}

	//only accept keyboard when focused
	@Override
	protected void onFocus() {
		super.onFocus();
		inputMultiplexer.addProcessor(this);

		
		this.getStyle().setBackgroundColor(Color.GRAY);
		
	}

	@Override
	protected void onBlur() {
		super.onBlur();
		inputMultiplexer.removeProcessor(this);

		this.getStyle().setBackgroundColor(Color.DARK_GRAY);
	}
	
	public void dispose(){
		super.dispose();
		inputMultiplexer.removeProcessor(this); //ensure we are removed
	}
	
	

	boolean dontProcessNextTyped = false;
	boolean backspaceHeld = false;
	
	@Override
	public boolean keyDown(int keycode) {
		fireKeyDown();
		if (keycode == Input.Keys.BACKSPACE){
			//backspace
			//recreate (crude)
			deleteCharacter();

			dontProcessNextTyped = true;
			backspaceHeld = true;
			return false;
		} 
		
		//other not allowed characters (if we make a text area widget, enter will be allowed
		if (	   keycode == Input.Keys.ENTER
				|| keycode == Input.Keys.DEL
				|| keycode == Input.Keys.ESCAPE){

			dontProcessNextTyped = true;
			return false;
		}
		
		

		//dontProcessNextTyped = false;
		return false;
	}


	private void deleteCharacter() {
		String currentText = super.getText();
		String text=currentText.substring(0, currentText.length()-1);
		Log.info("setting text too:"+text);			
		super.setText(text);
	}

	@Override
	public boolean keyUp(int keycode) {		
		fireKeyUp();
		backspaceHeld=false;
		return false;
	}
	
	public void fireKeyUp() {

		fireHandlersForType(Event.EventType.KeyUpEvent);
	}
	public void fireKeyDown() {

		fireHandlersForType(Event.EventType.KeyDownEvent);
	}
	
	@Override
	public boolean keyTyped(char character) {

		if (dontProcessNextTyped){		
			dontProcessNextTyped=false;
			return false;
		}
		
		if (backspaceHeld){
			deleteCharacter();
			return false;
		}
		
		if (!dontProcessNextTyped){			
			//ensure visible character
			if (CharacterUtils.isPrintableChar(character)){
				super.addText(""+	character);
			}
		} 
		
		dontProcessNextTyped=false;
		return false;
	}
	
	/*
	private boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }*/

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	

	/**
	 * 
	 * @param keyDownHandler
	 */
	public void addKeyDownHandler(KeyDownHandler keyDownHandler) {
		super.addHandler(keyDownHandler);
	}
	public void addKeyUpHandler(KeyUpHandler keyDownHandler) {
		super.addHandler(keyDownHandler);
	}
	
	
	

}
