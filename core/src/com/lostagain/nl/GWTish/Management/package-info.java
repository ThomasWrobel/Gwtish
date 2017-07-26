/**
 * This package will handle the "bureaucracy" surrounding getting GWTish widgets rendered.<br>
 * While its technically not needed - GWTish widgets are renderables already - by using this package<br>
 * widgets can show and hide themselves, as well as correctly showing and hiding their child widgets as appropriate.<br>
 * <br>
 * Its made from two parts;<br>
 * <br>
 * ModalManagement.java - Handles adding/remove widgets from the render list, as well as their children. <br>
 * 						   It also pre-orders widgets that are grouped together (designated by being in the same zIndexd group). This makes the ModelSorter work a lot quicker 
 *                        while allowing widgets to be at the same distance from the camera yet still be "in front" or "behind" others<br>
 * <br>
 * ModelSorter.java - Sorts the models out by distance but takes the pre-sorted z-index groups into account, the idea is this replaces any other sorter being used
 * <br>
 */
/**
 * @author Tom
 *
 */
package com.lostagain.nl.GWTish.Management;


//Does it have a z-index group?
//has the group been placed yet? (if so, skip)
//if so get the whole group from the z-index attribute
//find the correct spot in the array and put them in in-order there



//customer sorter;
//compare r1 and r2

//if r1 has zindex check if it knows what it should be in front of
//if r2 isnt that object, keep looking

//if there is no object to go infront of 


