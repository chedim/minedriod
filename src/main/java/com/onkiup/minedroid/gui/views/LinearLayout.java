package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Groups views into a line
 */
public class LinearLayout extends ViewGroup {

    /**
     * Grouping orientation
     */
    protected Orientation orientation = Orientation.HORIZONTAL;
    /**
     * Sum of child elements weights
     */
    protected int weightSum;

    /**
     * Space between the elements
     */
    protected int spacing;

    /**
     * Sets line orientation
     *
     * @param orientation new line orientation
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public LinearLayout(Context context) {
        super(context);
        layout = new Layout(Layout.MATCH_PARENT, Layout.MATCH_PARENT);
    }

    @Override
    public Layout measure(Point boundaries) {
        Layout result = layout.clone();
        if (result.width == Layout.WRAP_CONTENT) {
            result.setInnerWidth(0);
        }
        if (result.height == Layout.WRAP_CONTENT) {
            result.setInnerHeight(0);
        }
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.getLayout().clone();
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

            if (orientation == Orientation.HORIZONTAL) {
                // correcting our height to make all of elements fully visible
                if (layout.height == Layout.WRAP_CONTENT) {
                    result.setInnerHeight(Math.max(childLayout.getOuterHeight(), result.getInnerHeight()));
                }
                if (layout.width == Layout.WRAP_CONTENT) {
                    // adding item width & margins to ours.
                    result.width += childLayout.getOuterWidth();
                }
            } else {
                // correcting our width to make all of elements fully visible
                if (layout.width == Layout.WRAP_CONTENT) {
                    result.setInnerWidth(Math.max(childLayout.getOuterWidth(), result.getInnerWidth()));
                }
                if (layout.height == Layout.WRAP_CONTENT) {
                    // adding item height to ours
                    result.height += childLayout.getOuterHeight();
                }
            }
        }

        // applying limits from parent. Parent margins? That's his margins, so let him manage them.
        if (boundaries != null && result.width > -1) {
            if (boundaries.x > -1) {
                result.setOuterWidth(Math.min(boundaries.x, result.getOuterWidth()));
            }
            if (boundaries.y > -1 && result.height > -1) {
                result.setOuterHeight(Math.min(boundaries.y, result.getOuterHeight()));
            }
        }

        // elements spacing
        if (orientation == Orientation.HORIZONTAL) {
            result.setInnerWidth(result.getInnerWidth() + spacing * getChildrenCount());
        } else {
            result.setInnerHeight(result.getInnerHeight() + spacing * getChildrenCount());
        }

        // WE DID IT!!! RETURNING PROFIT!!!111!
        return result;
    }

    @Override
    public void drawContents(float partialTicks) {
        // drawing our children
        Point target = position.add(resolvedLayout.padding.coords());
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.getResolvedLayout();
            Point childTarget = target.add(childLayout.margin.coords());
            Point gravity = getGravityOffset(childLayout.getOuterSize());
            if (orientation == Orientation.HORIZONTAL) {
                childTarget.y += gravity.y;
                childTarget.x += spacing * i;
            } else {
                childTarget.x += gravity.x;
                childTarget.y += spacing * i;
            }
            child.setPosition(childTarget);
            child.onDraw(partialTicks);
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
        int space = spacing * getChildrenCount();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Layout childLayout = child.measure(areaLeft);
            Layout origChildLayout = child.getLayout();
            if (orientation == Orientation.HORIZONTAL) {
                if (origChildLayout.width == 0) {
                    int inner = resolvedLayout.getInnerWidth() - space;
                    childLayout.setOuterWidth(inner / weightSum * child.getLayoutWeight());
                }
            }
            if (orientation == Orientation.VERTICAL) {
                if (origChildLayout.height == 0) {
                    int inner = resolvedLayout.getInnerHeight() - space;
                    childLayout.setOuterHeight(resolvedLayout.getInnerHeight() / weightSum * child.getLayoutWeight());
                }
            }

            if (childLayout.width == Layout.MATCH_PARENT) {
                if (orientation == Orientation.HORIZONTAL)
                    throw new RuntimeException("MATCH_PARENT width in HORIZONTAL LinearLayout makes no sense...");      // that's true!!
                // making sure that this child will not affect our measure
                childLayout.setOuterWidth(resolvedLayout.getInnerWidth());
            }

            if (childLayout.height == Layout.MATCH_PARENT) {
                if (orientation == Orientation.VERTICAL)
                    throw new RuntimeException("MATCH_PARENT height in VERTICAL LinearLayout makes no sense...");       // that's also true!!
                // making sure that this child will not affect our measure
                childLayout.setOuterHeight(resolvedLayout.getInnerHeight());
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
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        orientation = (Orientation) node.getEnumAttr(GuiManager.NS, "orientation", style, Orientation.VERTICAL);
        spacing = node.getIntegerAttr(GuiManager.NS, "spacing", style, 0);
    }

    @Override
    protected String getThemeStyleName() {
        return "linear_layout";
    }
}
