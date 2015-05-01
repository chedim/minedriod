package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;

/**
 * Created by chedim on 4/25/15.
 */
public class LinearLayout extends ViewGroup {

    protected Orientation orientation = Orientation.HORIZONTAL;
    protected int weightSum;

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public LinearLayout() {
        super();
        layout = new Layout(Layout.MATCH_PARENT, Layout.MATCH_PARENT);
    }

    @Override
    public Layout measure(Point boundaries) {
        Layout result = layout.clone();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.getLayout();
            if (!childLayout.isResolved()) {
                // resolving layout
                if (childLayout.shouldBeMeasured()) {
                    // we won't limit child demands here :)
                    childLayout = child.measure(null);
                } else {
                    // this will be fun
                    // layout tries to be the same size as us. WTF? :)
                    if (childLayout.width == Layout.MATCH_PARENT) {
                        if (orientation == Orientation.HORIZONTAL)
                            throw new RuntimeException("MATCH_PARENT width in HORIZONTAL LinearLayout makes no sense...");      // that's true!!
                        // making sure that this child will not affect our measure
                        childLayout.width = result.width - childLayout.margin.left - childLayout.margin.right;
                    }
                    if (childLayout.height == Layout.MATCH_PARENT) {
                        if (orientation == Orientation.VERTICAL)
                            throw new RuntimeException("MATCH_PARENT height in VERTICAL LinearLayout makes no sense...");       // that's also true!!
                        // making sure that this child will not affect our measure
                        childLayout.height = result.height - childLayout.margin.top - childLayout.margin.bottom;
                    }
                }
            }

            if (orientation == orientation.HORIZONTAL) {
                // adding item width & margins to ours.
                result.width += childLayout.getOuterWidth();
                // correcting our height to make all of elements fully visible
                result.height = Math.max(childLayout.getOuterHeight(), result.height);
            } else {
                // correcting our width to make all of elements fully visible
                result.width = Math.max(childLayout.getOuterWidth(), result.width);
                // adding item height to ours
                result.height += childLayout.getOuterHeight();
            }
        }

        // applying limits from parent. Parent margins? That's his margins, so let him manage them.
        if (boundaries != null) {
            if (boundaries.x > -1) {
                result.setOuterWidth(Math.min(boundaries.x, result.getOuterWidth()));
            }
            if (boundaries.y > -1) {
                result.setOuterHeight(Math.min(boundaries.y, result.getOuterHeight()));
            }
        }

        // WE DID IT!!! RETURNING PROFIT!!!111!
        return result;
    }

    @Override
    public void drawContents() {
        // drawing our children
        Point target = position.add(resolvedLayout.padding.coords());
        for (int i=0; i< getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.getResolvedLayout();
            Point childTarget = target.clone().add(childLayout.margin.coords());
            child.setPosition(childTarget);
            child.onDraw();
            if (orientation == Orientation.HORIZONTAL) {
                target.x += childLayout.getOuterWidth();
            } else {
                target.y += childLayout.getOuterHeight();
            }
        }
    }

    @Override
    public void resolveLayout(Layout layout) {
        super.resolveLayout(layout);

        // calculating items layouts
        Point areaLeft = layout.getInnerSize();
        for (int i = 0; i< getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.measure(areaLeft);
            if (orientation == Orientation.HORIZONTAL) {
                childLayout.setOuterHeight(resolvedLayout.getInnerHeight());
                if (childLayout.width == 0) {
                    childLayout.setOuterWidth(resolvedLayout.getInnerWidth() / weightSum * child.getLayoutWeight());
                }
            }
            if (orientation == Orientation.VERTICAL) {
                childLayout.setOuterWidth(resolvedLayout.getInnerWidth());
                if (childLayout.height == 0) {
                    childLayout.setOuterHeight(resolvedLayout.getInnerHeight() / weightSum * child.getLayoutWeight());
                }
            }

            child.resolveLayout(childLayout);
        }
    }

    @Override
    public void addChild(View child) {
        super.addChild(child);
        weightSum += child.getLayoutWeight();
    }

    @Override
    public void addChildAt(int position, View child) {
        super.addChildAt(position, child);
        weightSum += child.getLayoutWeight();
    }

    @Override
    public void removeChild(View child) {
        super.removeChild(child);
        weightSum -= child.getLayoutWeight();
    }

    @Override
    public void removeChildAt(int position) {
        weightSum -= getChildAt(position).getLayoutWeight();
        super.removeChildAt(position);
    }

    public static enum Orientation {
        VERTICAL, HORIZONTAL
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        orientation = (Orientation) node.getEnumAttr("mc", "orientation", Orientation.VERTICAL);
    }
}
