package com.onkiup.minedroid.gui.views;

/**
 * Created by chedim on 5/12/15.
 */
public interface ViewHolder<T> {
    public void setObject(T object);
    public void setView(View view);
    public View getView();
}
