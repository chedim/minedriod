package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Contexted;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.*;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * View that shows a scrollable list of objects
 */
public class ListView extends LinearLayout {
    /**
     * Array of objects that should be shown
     */
    protected List mObjects;
    /**
     * ViewHolders for different objects types
     */
    protected HashMap<Class, Class<Holder>> mHolders = new HashMap<Class, Class<Holder>>();
    /**
     * Cached views that are ready to be reused
     */
    protected HashMap<Class, List<View>> mReusable = new HashMap<Class, List<View>>();

    /**
     * Drawable to be shown when there is no objects in list
     */
    protected Drawable mEmptyDrawable;

    /**
     * Service information
     */
    protected int mOffset = 0, mFirstShown = 0, mLastShown = 0, mShownSize = 0;

    public ListView(Context context) {
        super(context);
    }

    /**
     * Sets list of objects to be shown
     *
     * @param objects
     */
    public void setObjects(List objects) {
        mObjects = objects;
        mOffset = 0;
        mFirstShown = 0;
        mLastShown = 0;
    }

    /**
     * @return List of objects to be shown
     */
    public List getObjects() {
        return mObjects;
    }

    /**
     * Registers a ViewHolder for a particular Object class
     *
     * @param objectClass
     * @param holder
     */
    public void setHolder(Class objectClass, Class<? extends Holder> holder) {
        mHolders.put(objectClass, (Class<Holder>) holder);
    }

    @Override
    public void resolveLayout(Layout layout) {
        super.resolveLayout(layout);
        if (mFirstShown == mLastShown && mLastShown == 0 && getInnerSize() > 0) {
            fill();

            // we need to re-resolve layout :)
            super.resolveLayout(layout);
        }
    }

    @Override
    public Layout measure(Point boundaries) {
        return super.measure(boundaries);
    }

    @Override
    public void onDraw(float partialTicks) {
        super.onDraw(partialTicks);
//        BorderDrawable d = new BorderDrawable(new Color(0x66ff0000));
//        d.setSize(resolvedLayout.getInnerSize());
//        d.draw(position.add(resolvedLayout.padding.coords()));
//
//        int reusables = 0;
//        if (mReusable.containsKey(String.class)) reusables = mReusable.get(String.class).size();
//        String debugText = getChildrenCount() + ": " + mShownSize + " ("+ reusables +")"+
//                "; " + resolvedLayout.padding.top + " - " + mOffset;
//        TextDrawable debug = new TextDrawable(debugText, 0xffff0000);
//        debug.draw(position.add(layout.getOuterSize()));
    }

    @Override
    public void drawContents(float partialTicks) {
        if (mObjects == null || mObjects.size() == 0) {
            if (mEmptyDrawable != null) {
                Rect inner = resolvedLayout.getInnerRect();
                Point innerSize = inner.getSize();
                innerSize.x /= 2;
                innerSize.y /= 2;
                Point where = position.add(inner.coords()).add(innerSize);

                Point drawableSize = mEmptyDrawable.getOriginalSize().clone();
                mEmptyDrawable.setSize(drawableSize.clone());
                drawableSize.x /= 2;
                drawableSize.y /= 2;


                mEmptyDrawable.draw(where.sub(drawableSize));
            }
            return;
        }


        Layout tmp = resolvedLayout.clone();
        resolvedLayout = resolvedLayout.clone();

        if (this.orientation == Orientation.HORIZONTAL) {
            resolvedLayout.padding.left -= mOffset;
        } else {
            resolvedLayout.padding.top -= mOffset;
        }

        super.drawContents(partialTicks);

        resolvedLayout = tmp;

        // rendering scrollbar
        int itemsCount = mObjects.size();
        if (mLastShown == mFirstShown || itemsCount == 0) return;
        float approximateItemSize = mShownSize * 1f / (mLastShown - mFirstShown);
        float approximateHeight = approximateItemSize * itemsCount;
        int scrollerHeight = (int) Math.floor(getInnerSize() / approximateHeight * getInnerSize());

        int scrollerPos = (int) Math.floor(getInnerSize() * ((mFirstShown) * 1f / (itemsCount))) + mOffset;

        int scrollArea = scrollerPos + scrollerHeight;
        if(getInnerSize() - scrollArea > 0 && getInnerSize() - scrollArea < 1.5) {
            scrollerPos += 1;
        }

        RoundedCornerDrawable scroller = new RoundedCornerDrawable(0x33000000, 1);

        Point scPosition;
        if (orientation == Orientation.VERTICAL) {
            scroller.setSize(new Point(2, scrollerHeight));
            scPosition = position.add(new Point(resolvedLayout.getInnerWidth() - 4, scrollerPos))
                    .add(resolvedLayout.getInnerRect().coords());
        } else {
            scroller.setSize(new Point(scrollerHeight, 2));
            scPosition = position.add(new Point(scrollerPos, resolvedLayout.getInnerHeight() - 4))
                    .add(resolvedLayout.getInnerRect().coords());
        }
        scroller.draw(scPosition);

        if (debug) {
            DebugDrawable d = new DebugDrawable(new Color(0x66000000));
            if (orientation == Orientation.HORIZONTAL) {
                d.setSize(new Point(mShownSize, resolvedLayout.getInnerHeight()));
                d.draw(position.sub(new Point(mOffset, 0)));
            } else {
                d.setSize(new Point(resolvedLayout.getInnerWidth(), mShownSize));
                d.draw(position.sub(new Point(0, mOffset)));
            }
            TrueTypeDrawable t = new TrueTypeDrawable("(" +mOffset+") " + mFirstShown + "-" + mLastShown
                    +"("+getChildrenCount()+")", 0xFF000000);
            t.setTextSize(10);
            t.setSize(t.getOriginalSize());
            t.draw(position.add(new Point(5, 5)));
        }
    }


    @Override
    public void handleMouseEvent(MouseEvent event) {
        try {
            super.handleMouseEvent(event);
        } catch (NullPointerException e) {

        }
        if (event.type == OnScroll.class && !event.cancel) {
            moveViewport(-event.wheel.y);
            int inner = getInnerSize();
            if (mShownSize - mOffset < inner) {
                mOffset = mShownSize - inner;
            }

            if (mOffset < 0) {
                mOffset = 0;
            }
        }

    }

    /**
     * Fills the ListView with views
     */
    protected void fill() {
        super.removeAllChildren();
        mShownSize = 0;
        int mySize = getInnerSize();
        for (int i = mFirstShown; i < mObjects.size(); i++) {
            View v = getViewFor(i);
            int size = getItemSize(v);
            addChild(v);
            mLastShown++;
            mShownSize += size;
            if (mShownSize - mOffset >= mySize) {
                break;
            }
        }
    }


    /**
     * Moves current viewport
     *
     * @param offset amount of items to move viewport on
     */
    protected void moveViewport(int offset) {

        if (offset == 0) return;

        int newOffset = mOffset + offset;
        if (offset < 0) {
            // moving top
            if (newOffset >= 0) {
                mOffset = newOffset;
                return;      // there is nothing to do...
            }

            for (int i = 1; i < mFirstShown; i++) {
                int size = getItemSize(mFirstShown - i);
                newOffset += size;
                if (newOffset > 0) {
                    mOffset = newOffset;
                    moveToFirst(mFirstShown - i);
                    return;
                }
            }
            mOffset = 0;
            moveToFirst(0);
        } else {
            // moving bottom
            int newBottom = mShownSize - newOffset;
            int mySize = getInnerSize();
            if (newBottom >= mySize) {
                mOffset = newOffset;
                return; // there is nothing to do...
            }

            for (int i = 1; i < mObjects.size() - mLastShown; i++) {
                int size = getItemSize(mLastShown + i);
                newBottom += size;
                if (newBottom >= mySize) {
                    mOffset = newOffset;
                    moveToLast(mLastShown + i);
                    return;
                }
            }

            // oops!
            moveToLast(mObjects.size() - 1);
            mOffset = mShownSize - getInnerSize();
        }
    }

    protected int getInnerSize() {
        if (orientation == Orientation.HORIZONTAL) {
            return resolvedLayout.getInnerWidth();
        } else {
            return resolvedLayout.getInnerHeight();
        }
    }

    /**
     * Sets the first item that should be visible in the ListView
     *
     * @param position item index
     */
    protected void moveToFirst(int position) {
        int addedSize = 0;
        for (int i = 0; i < mFirstShown - position; i++) {
            int itemPosition = mFirstShown - i - 1;
            View v = getViewFor(itemPosition);
            Holder holder = (Holder) v.getHolder();
            holder.setPosition(itemPosition);
            holder.setObject(mObjects.get(itemPosition));

            addChildAt(0, v);
            addedSize += getItemSize(v);
        }

        mFirstShown = position;
        mShownSize += addedSize;
        dropInvisibleTail();
    }

    /**
     * Sets the last item to be visible in the ListView
     *
     * @param position item index
     */
    protected void moveToLast(int position) {
        int addedSize = 0;
        for (int i = 0; i < position - mLastShown; i++) {
            int itemPosition = mLastShown + 1 + i;
            View v = getViewFor(itemPosition);

            addChild(v);
            addedSize += getItemSize(v);
        }

        mLastShown = position;
        mShownSize += addedSize;
        dropInvisibleHead();
    }

    /**
     * Removes child views that represent items with indexes before the first visible item
     */
    protected void dropInvisibleHead() {
        int newOffset = mOffset;
        for (int i = 0; i < getChildrenCount(); i++) {
            int size = getItemSize(mFirstShown);
            if (newOffset - size <= 0) {
                break;
            }
            newOffset -= size;
            mFirstShown++;
            removeChildAt(0);
        }
        mShownSize -= mOffset - newOffset;
        mOffset = newOffset;
    }

    /**
     * Removes child views that represent items with indexes after the last visible item
     */
    protected void dropInvisibleTail() {
        int newSize = mShownSize;
        for (int i = 0; i < getChildrenCount(); i++) {
            int size = getItemSize(mLastShown);
            if (newSize - size - mOffset <= getInnerSize()) {
                break;
            }

            newSize -= size;
            mLastShown--;
            removeChildAt(getChildrenCount() - 1);
        }
        mShownSize = newSize;
    }

    @Override
    public void removeChild(View child) {
        super.removeChild(child);

        reuse(child);
    }

    @Override
    public void removeChildAt(int position) {
        View child = getChildAt(position);
        super.removeChildAt(position);
        reuse(child);
    }

    /**
     * Saves the child view to be reused
     *
     * @param v
     */
    protected void reuse(View v) {
        Holder holder = (Holder) v.getHolder();
        if (holder == null) return;

        Object o = holder.getObject();
        if (o == null) return;

        Class c = o.getClass();

        if (!mReusable.containsKey(c)) {
            mReusable.put(c, new ArrayList<View>());
        }

        if (!mReusable.get(c).contains(v)) {
            mReusable.get(c).add(v);
        }
    }

    /**
     * @param c
     * @return View that can show the given class
     */
    protected View getViewFor(Class c) {
        if (mReusable.containsKey(c)) {
            if (mReusable.get(c).size() > 0) {
                return mReusable.get(c).remove(0);
            }
        }

        try {
            Class<Holder> holderClass = mHolders.get(c);
            Constructor<Holder> constructor = holderClass.getConstructor(Context.class);
            Holder holder = constructor.newInstance(this);
            holder.setList(this);
            return holder.getView();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param position
     * @return View that can show item with given position
     */
    protected View getViewFor(int position) {
        if (position >= mFirstShown && position <= mLastShown) {
            int child = position - mFirstShown;
            if (child > -1 && child < getChildrenCount() - 1) {
                return getChildAt(position - mFirstShown);
            }
        }

        Object o = mObjects.get(position);
        View v = getViewFor(o.getClass());
        Holder holder = (Holder) v.getHolder();
        holder.setPosition(position);
        holder.setObject(mObjects.get(position));
        return v;
    }

    /**
     * @param view
     * @return View's width if list is horizontal, otherwise returns it's height
     */
    protected int getItemSize(View view) {
        int size = 0;
        if (view != null) {
            Layout itemLayout = view.getResolvedLayout();
            if (itemLayout == null) {
                itemLayout = view.measure(null);
            }
            if (orientation == Orientation.HORIZONTAL) {
                size = itemLayout.getOuterWidth();
            } else {
                size = itemLayout.getOuterHeight();
            }
        }
        return size;
    }

    /**
     * @param position
     * @return View's size for given position
     */
    protected int getItemSize(int position) {
        View view = getViewFor(position);

        int size = getItemSize(view);

        return size;
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);

        mEmptyDrawable = node.getDrawableAttr(GuiManager.NS, "empty", style, null);
    }

    /**
     * Holder for list children
     *
     * @param <T>
     */
    public static abstract class Holder<T> extends Contexted implements ViewHolder<T> {
        /**
         * Controlled view
         */
        protected View mView;
        /**
         * List item to populate values from
         */
        protected T mObject;
        /**
         * Position of the list item
         */
        protected int mPosition;
        /**
         * ListView that holds this ViewHolder
         */
        protected ListView mList;

        public Holder(Context context) {
            super(context);
        }

        /**
         * @return Location of the View's XML source
         */
        protected abstract ResourceLocation getViewLocation();

        /**
         * Should populate view with values from given object
         *
         * @param object
         */
        protected abstract void fill(T object);

        /**
         * Should accept the view to be populated
         *
         * @param view
         */
        protected abstract void link(View view);

        @Override
        public void setObject(T object) {
            mObject = object;
            if (mView != null) fill(mObject);
        }

        /**
         * @return list item that is used by this ViewHolder
         */
        public T getObject() {
            return mObject;
        }

        /**
         * Sets ListView that holds this ViewHolder
         *
         * @param list
         */
        public void setList(ListView list) {
            mList = list;
        }

        /**
         * Sets index of the item in the list
         *
         * @param position
         */
        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void setView(View view) {
            mView = view;
            link(mView);
            if (mObject != null) fill(mObject);
        }

        @Override
        public View getView() {
            if (mView == null) {
                mView = GuiManager.inflateLayout(this, getViewLocation());
                mView.setHolder(this);
            }

            return mView;
        }
    }

    @Override
    protected String getThemeStyleName() {
        return "list_view";
    }
}
