package com.mobisys.android.autocompletetextviewcomponent;

import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.DisplayStringInterface;

public interface SelectionListener {
	/*
	 * Called when user selects an item from autocomplete view
	 */
	void onItemSelection(DisplayStringInterface selectedItem);
	
	/*
	 * Called only in case of Google Places API (autocomplete_url = null)
	 */
	void onReceiveLocationInformation(double lat, double lng);
}
