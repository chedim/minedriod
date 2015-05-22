package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.RoundedCornerDrawable;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.themes.Theme;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 5/12/15.
 */
public class ListView extends LinearLayout {
    protected List mObjects;
    protected HashMap<Class, Class<Holder>> mHolders = new HashMap<Class, Class<Holder>>();
    protected HashMap<Class, List<View>> mReusable = new HashMap<Class, List<View>>();
    protected List<Integer> mSizes = new ArrayList<Integer>();

    protected Drawable mEmptyDrawable;

    protected int mOffset = 0, mFirstShown = 0, mLastShown = 0, mShownSize = 0;


    public void setObjects(List objects) {
        mObjects = objects;
        mOffset = 0;
        mFirstShown = 0;
        mLastShown = 0;
    }

    public List getObjects() {
        return mObjects;
    }

    public void setHolder(Class objectClass, Class<? extends Holder> holder) {
        mHolders.put(objectClass, (Class<Holder>) holder);
    }

    @Override
    public void resolveLayout(Layout layout) {
        super.resolveLayout(layout);
        if (mFirstShown == mLastShown && mLastShown == 0) {
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
    public void onDraw() {
        super.onDraw();
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
    public void drawContents() {
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

        super.drawContents();

        resolvedLayout = tmp;

        // rendering scrollbar
        int itemsCount = mObjects.size();
        if (mLastShown == mFirstShown || itemsCount == 0) return;
        int approximateItemSize = mShownSize / (mLastShown - mFirstShown);
        int approximateHeight = approximateItemSize * itemsCount;
        int scrollerHeight = (int) (getInnerSize() * 1f / approximateHeight * getInnerSize());

        int scrollerPos = (int) (getInnerSize() * 1f * (mFirstShown * 1f / itemsCount));

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

    protected void fill() {
        super.removeAllChildren();
        mShownSize = 0;
        int mySize = getInnerSize();
        for (int i = mFirstShown; i < mObjects.size(); i++) {
            View v = getViewFor(i);
            Holder holder = (Holder) v.getHolder();
            holder.setPosition(i);
            holder.setObject(mObjects.get(i));
            int size = getItemSize(v);
            addChild(v);
            mLastShown++;
            mShownSize += size;
            if (mShownSize - mOffset >= mySize) {
                break;
            }
        }
    }


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
                if (newOffset >= 0) {
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

    protected void moveToLast(int position) {
        int addedSize = 0;
        for (int i = 0; i < position - mLastShown; i++) {
            int itemPosition = mLastShown + 1 + i;
            View v = getViewFor(itemPosition);
            Holder holder = (Holder) v.getHolder();
            holder.setPosition(itemPosition);
            holder.setObject(mObjects.get(itemPosition));

            addChild(v);
            addedSize += getItemSize(v);
        }

        mLastShown = position;
        mShownSize += addedSize;
        dropInvisibleHead();
    }

    protected void dropInvisibleHead() {
        int newOffset = mOffset;
        for (int i = 0; i < getChildrenCount(); i++) {
            int size = getItemSize(mFirstShown);
            if (newOffset - size < 0) {
                break;
            }
            newOffset -= size;
            mFirstShown++;
            removeChildAt(0);
        }
        mShownSize -= mOffset - newOffset;
        mOffset = newOffset;
    }

    protected void dropInvisibleTail() {
        int newSize = mShownSize;
        for (int i = 0; i < getChildrenCount(); i++) {
            int size = getItemSize(mLastShown);
            if (newSize - size - mOffset < getInnerSize()) {
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

    protected View getViewFor(Class c) {
        if (mReusable.containsKey(c)) {
            if (mReusable.get(c).size() > 0) {
                return mReusable.get(c).remove(0);
            }
        }

        try {
            Class<Holder> holderClass = mHolders.get(c);
            Holder holder = holderClass.newInstance();
            holder.setList(this);
            return holder.getView();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected View getViewFor(int position) {
        if (position > mFirstShown && position < mLastShown) {
            return getChildAt(position - mFirstShown);
        }

        Object o = mObjects.get(position);
        return getViewFor(o.getClass());
    }

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

    protected int getItemSize(int position) {
        if (position >= mSizes.size())
            for (int i = mSizes.size(); i <= position; i++) {
                View view = getViewFor(i);

                int size = getItemSize(view);
                mSizes.add(size);
            }

        return mSizes.get(position);
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        ResourceLocation location = node.getResourceAttr("mc", "empty", null);
        if (location != null) {
            mEmptyDrawable = MineDroid.inflateDrawable(location);
        }
    }

    public static abstract class Holder<T> implements ViewHolder<T> {
        protected View mView;
        protected T mObject;
        protected int mPosition;
        protected ListView mList;

        protected abstract ResourceLocation getViewLocation();

        protected abstract void fill(T object);

        protected abstract void link(View view);

        public void setObject(T object) {
            mObject = object;
            if (mView != null) fill(mObject);
        }

        public T getObject() {
            return mObject;
        }

        public void setList(ListView list) {
            mList = list;
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        public void setView(View view) {
            mView = view;
            link(mView);
            if (mObject != null) fill(mObject);
        }

        public View getView() {
            if (mView == null) {
                mView = MineDroid.inflateLayout(getViewLocation());
                mView.setHolder(this);
            }

            return mView;
        }
    }
}
