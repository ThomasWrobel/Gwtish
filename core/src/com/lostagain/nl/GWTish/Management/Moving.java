package com.lostagain.nl.GWTish.Management;

public interface Moving {
	/**
	 * Updates the position of the thing moving
	 * (note; animations of appearance and position are kept separate to help optimize later - after all, moving
	 * objects can go offscreen, things just changeing their apperance will not)
	 * 
	 * @param deltatime = The time in seconds since the last render
	 */
	public void updatePosition(float deltatime);
}
