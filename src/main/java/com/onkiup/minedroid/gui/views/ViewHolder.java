package com.onkiup.minedroid.gui.views;

/**
 * Interface for all ViewHolders
 */
public interface ViewHolder<T> {
    /**
     * Sets data source object for the ViewHolder
     * @param object Object to display
     */
    void setObject(T object);

    /**
     * Sets view that ViewHolder should control and populate with data
     * @param view New View for the ViewHolder
     */
    void setView(View view);

    /**
     *
     * @return Controlled by ViewHolder view
     */
    View getView();
}
