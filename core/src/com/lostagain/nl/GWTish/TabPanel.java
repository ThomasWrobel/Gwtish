package com.lostagain.nl.GWTish;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 * @author darkflame
 *
 */
public class TabPanel extends VerticalPanel{


	/**
	 */
	private class UnmodifiableTabBar extends HorizontalPanel {
		TabPanel associatedtabpanel;
		
		public UnmodifiableTabBar(TabPanel tabPanel) {
			associatedtabpanel=tabPanel;
		}
		

		public void selectTabVisually(int index, boolean fireEvents) {
			// TODO Auto-generated method stub

		}

	}

	
	

	DeckPanel panel = new DeckPanel();

	private UnmodifiableTabBar tabbar = new UnmodifiableTabBar(this);

	private int currentlySelected;

	public TabPanel() {
		super();

		super.add(tabbar,panel);
		
		this.getStyle().setBorderColor(Color.WHITE);
		tabbar.getStyle().setBorderColor(Color.RED);
		panel.getStyle().setBorderColor(Color.CORAL);
		
	}



	@Override
	public boolean add(Widget w) {
		throw new UnsupportedOperationException(
				"A tabText parameter must be specified with add().");
	}


	/**
	 * Adds a widget to the tab panel. TODO:If the Widget is already attached to the
	 * TabPanel, it will be moved to the right-most index.
	 *
	 * @param w the widget to be added
	 * @param tabText the text to be shown on its tab
	 */
	public void add(Widget w, String tabText) {
		insert(w, tabText, getWidgetCount());
	}



	/**
	 * Adds a widget to the tab panel. 
	 * 
	 * TODO:If the Widget is already attached to the
	 * TabPanel, it will be moved to the right-most index.
	 *
	 * @param w the widget to be added
	 * @param tabWidget the widget to be shown in the tab
	 */
	public void add(Widget w, Widget tabWidget) {
		insert(w, tabWidget, getWidgetCount());
	}
	/*

	  @Override
	  public HandlerRegistration addBeforeSelectionHandler(
	      BeforeSelectionHandler<Integer> handler) {
	    return addHandler(handler, BeforeSelectionEvent.getType());
	  }

	  @Override
	  public HandlerRegistration addSelectionHandler(
	      SelectionHandler<Integer> handler) {
	    return addHandler(handler, SelectionEvent.getType());
	  }
	 */


	@Override
	public void clear() {
		while (getWidgetCount() > 0) {
			remove(getWidget(0));
		}
	}

	/**
	 * Gets the deck panel within this tab panel. Adding or removing Widgets from
	 * the DeckPanel is not supported 
	 *
	 * @return the deck panel
	 */
	public DeckPanel getDeckPanel() {
		return panel;
	}

	/**
	 * Gets the tab bar within this tab panel. Adding or removing tabs from the
	 * TabBar is not supported 
	 *
	 * @return the tab bar
	 */
	public UnmodifiableTabBar getTabBar() {
		return tabbar;
	}

	@Override
	public Widget getWidget(int index) {
		return panel.getWidget(index);
	}

	@Override
	public int getWidgetCount() {
		return panel.getWidgetCount();
	}


	@Override
	public int getWidgetIndex(Widget widget) {
		return panel.getWidgetIndex(widget);
	}


	/**
	 * Inserts a widget into the tab panel. TODO:If the Widget is already attached to
	 * the TabPanel, it will be moved to the requested index.
	 *
	 * @param widget the widget to be inserted
	 * @param tabText the text to be shown on its tab
	 * @param beforeIndex the index before which it will be inserted
	 */
	public void insert(Widget widget, String tabText, int beforeIndex) {
		insert(widget,new Label( tabText), beforeIndex);
	}

	/**
	 * Inserts a widget into the tab panel. TODO:If the Widget is already attached to
	 * the TabPanel, it will be moved to the requested index.
	 *
	 * @param widget the widget to be inserted.
	 * @param tabWidget the widget to be shown on its tab.
	 * @param beforeIndex the index before which it will be inserted.
	 */
	public void insert(final Widget widget, Widget tabWidget, int beforeIndex) {

		//add tab
		tabbar.insert(tabWidget, beforeIndex);
	
		tabWidget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				panel.showWidget(widget, true);

			}
		});


		//add new widget to deck
		panel.insert(widget, beforeIndex);
		
		//ensure hidden? reselect current tab
		this.selectTab(currentlySelected, false);
	}
	

	//
	//	  @Override
	//	  public Iterator<Widget> iterator() {
	//	    // The Iterator returned by DeckPanel supports removal and will invoke
	//	    // TabbedDeckPanel.remove(), which is an active function.
	//	    return panel.iterator();
	//	  }


	public boolean remove(int index) {
		
		Widget widget_at_index = panel.getWidget(index);
		boolean removedFromDeck =  panel.remove(widget_at_index);

		widget_at_index = tabbar.getWidget(index);
		boolean removedFromTabbar =   tabbar.remove(widget_at_index);

		boolean success = removedFromDeck && removedFromTabbar;

		return success;
	}

	/**
	 * Removes the given widget, and its associated tab.
	 *
	 * @param widget the widget to be removed
	 */
	@Override
	public boolean remove(Widget widget) {
		int widget_index = panel.getWidgetIndex(widget);
		boolean removedFromDeck =  panel.remove(widget);

		Widget  widget_at_index = tabbar.getWidget(widget_index);
		boolean removedFromTabbar =   tabbar.remove(widget_at_index);

		boolean success = removedFromDeck && removedFromTabbar;

		return success;
	}



	/**
	 * Programmatically selects the specified tab and fires events.
	 *
	 * @param index the index of the tab to be selected
	 */
	public void selectTab(int index) {
		selectTab(index, true);
	}

	/**
	 * Programmatically selects the specified tab.
	 *
	 * @param index the index of the tab to be selected
	 * @param fireEvents true to fire events, false not to (selection events not yet implemented)
	 */
	public void selectTab(int index, boolean fireEvents) {
		tabbar.selectTabVisually(index, fireEvents);
		panel.showWidget(index, true);
		currentlySelected = index;
		
		
	}


}
