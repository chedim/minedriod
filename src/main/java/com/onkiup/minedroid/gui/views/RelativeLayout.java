package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.CenteredRect;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.themes.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 4/26/15.
 */
public class RelativeLayout extends ViewGroup {

    @Override
    public void resolveLayout(View.Layout layout) {
        super.resolveLayout(layout);

        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            child.unresolveLayout();
        }

        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            resolveChildLayout(child);
        }
    }

    protected void resolveChildLayout(View child) {
        if (child.isLayoutResolved()) return;


        Layout measured = getMeasured(child);
        List<View> deps = measured.getDependencies();
        for (View dep: deps) {
            if (dep == child || dep == this) continue;
            resolveChildLayout(dep);
        }

        Rect outline = getOuter(child, true);
        Point outlineSize = outline.getSize();

        // applying resolved parent layout
        if (outline.right == Layout.MATCH_PARENT) {
            outline.right = layout.getInnerWidth();
            if (outline.left == Layout.UNSPECIFIED) {
                outline.left = outline.right - measured.getOuterWidth();
            }
        }

        if (outline.bottom == Layout.MATCH_PARENT) {
            outline.bottom = layout.getInnerHeight();
            if (outline.top == Layout.UNSPECIFIED) {
                outline.top = outline.top - measured.getOuterHeight();
            }
        }

        // centering item
        int center;
        if (outline.left == Layout.CENTER_PARENT) {
            center = layout.getInnerWidth() / 2;
            if (outline.right == Layout.CENTER_PARENT) {
                outline.left = center - measured.getOuterWidth() / 2;
                outline.right = center + measured.getOuterWidth() / 2;
            } else {
                outline.left = center - (outline.right / 2);
            }
        }

        if (outline.top == Layout.CENTER_PARENT) {
            center = layout.getInnerHeight() / 2;
            if (outline.bottom == Layout.CENTER_PARENT) {
                outline.top = center - measured.getOuterHeight() / 2;
                outline.bottom = center + measured.getOuterHeight() / 2;
            } else {
                outline.top = center - (outline.bottom / 2);
            }
        }

        Point oSize = outline.getSize();
        measured.setOuterWidth(oSize.x);
        measured.setOuterHeight(oSize.y);

        child.resolveLayout(measured);
    }

    @Override
    public void addChild(View child) {
        super.addChild(child);
        injectLayout(child);
    }

    @Override
    public void addChildAt(int position, View child) {
        super.addChildAt(position, child);
        injectLayout(child);
    }

    private void injectLayout(View child) {
        View.Layout childLayout = child.getLayout();
        if (childLayout instanceof Layout) return;
        if (childLayout == null) child.setLayout(new Layout());
        else child.setLayout(new Layout(childLayout));
    }

    @Override
    public void drawContents() {
        Point zero = position.add(resolvedLayout.padding.coords());
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Rect outline = getOuter(child, true);
            if (outline instanceof CenteredRect) outline = ((CenteredRect) outline).getRect();

            child.setPosition(zero.add(outline.coords()).add(child.getLayout().margin.coords()));
            child.onDraw();
        }
    }

    protected HashMap<View, Layout> measured = new HashMap<View, Layout>();
    protected HashMap<View, Rect> outers = new HashMap<View, Rect>();

    @Override
    public View.Layout measure(Point boundaries) {
        measured.clear();
        outers.clear();
        View.Layout result = layout.clone();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            Rect outer = getOuter(child);
            Point oSize = outer.getSize();

            if (outer.left + oSize.x >= 0) {
                if (outer.left + oSize.x > result.getInnerWidth()) {
                    result.setInnerWidth(outer.left + oSize.x);
                }
            }

            if (outer.top + oSize.y >= 0) {
                if (outer.top + oSize.y > result.getInnerHeight()) {
                    result.setInnerHeight(outer.top + oSize.y);
                }
            }
        }

        return result;
    }

    protected Layout getMeasured(View view) {
        if (!measured.containsKey(view)) {
            Layout childLayout = (Layout) view.getLayout().clone();
            if (childLayout.shouldBeMeasured()) {
                Layout result = childLayout;
                View.Layout m = view.measure(null);
                result.width = m.width;
                result.height = m.height;
                measured.put(view, result);
            } else {
                measured.put(view, childLayout);
            }
        }
        return measured.get(view);
    }

    // some additional fields for views layout
    public static class Layout extends View.Layout {

        public final static int UNSPECIFIED = -30000;
        public final static int CENTER_PARENT = -40000;
        public final static int RELATIVE = -100000;

        public boolean allowDeffered = true;

        public View toLeftOf, toRightOf, below, above,
                alignCenter, alignMiddle, alignLeft, alignTop, alignBottom, alignRight;


        public Layout() {
            this(0, 0, new Rect(), new Rect());
        }

        public Layout(int width, int height, Rect margin, Rect padding) {
            super(width, height, margin, padding);
        }

        public Layout(int width, int height, Rect margin) {
            this(width, height, margin, new Rect());
        }

        public Layout(int width, int height) {
            this(width, height, new Rect(), new Rect());
        }

        public Layout(View.Layout layout) {
            this(layout.width, layout.height, layout.margin.clone(), layout.padding.clone());
        }

        public boolean validate() {
            boolean error = false;
            error |= alignLeft != null && alignCenter != null && alignRight != null;
            error |= alignTop != null && alignMiddle != null && alignBottom != null;
            error |= alignLeft != null && toRightOf != null;
            error |= alignRight != null && toLeftOf != null;
            error |= alignTop != null && below != null;
            error |= alignBottom != null && above != null;
            return !error;
        }

        /**
         * Returns children outer rect
         *
         * @return
         */
        public Rect getOuterRect(RelativeLayout parent) {

            if (!validate()) {
                throw new RuntimeException("invalid layout arguments");
            }

            Rect relyRect;


            Rect rect = calculateOuterPosition(parent);
            calculateOuterSize(rect, parent);

            return rect;
        }

        private Rect calculateOuterSize(Rect rect, RelativeLayout parent) {
            View.Layout pl = parent.getResolvedLayout();
            if (pl == null) pl = parent.getLayout();

            if (width >= 0) {
                if (rect.left == UNSPECIFIED) {
                    if (rect.right == UNSPECIFIED) {
                        rect.left = 0;
                        rect.right = getOuterWidth();
                    } else if (rect.right != MATCH_PARENT) {
                        rect.left = rect.right - getOuterWidth();
                    }
                } else if (rect.right == UNSPECIFIED) {
                    if (rect instanceof CenteredRect) {
                        rect.right = getOuterWidth() / 2;
                    } else {
                        rect.right = rect.left + getOuterWidth();
                    }
                }
            } else if (width == MATCH_PARENT) {
                if (pl.width > 0) rect.right = rect.left + pl.getInnerWidth();
            }

            if (height >= 0) {
                if (rect.top == UNSPECIFIED) {
                    if (rect.bottom == UNSPECIFIED) {
                        rect.top = 0;
                        rect.bottom = getOuterHeight();
                    } else if (rect.bottom != MATCH_PARENT) {
                        rect.top = rect.bottom - getOuterHeight();
                    }
                } else if (rect.bottom == UNSPECIFIED) {
                    if (rect instanceof CenteredRect) {
                        rect.bottom = getOuterHeight() / 2;
                    } else {
                        rect.bottom = rect.top + getOuterHeight();
                    }
                }
            } else if (height == MATCH_PARENT) {
                if (pl.height > 0) rect.bottom = rect.top + pl.getInnerHeight();
            }

            return rect;
        }

        private Rect calculateOuterPosition(RelativeLayout parent) {
            Rect rect = new Rect(UNSPECIFIED, UNSPECIFIED, UNSPECIFIED, UNSPECIFIED);

            if (toRightOf != null) {
                rect.left = getOuterRect(toRightOf, parent).right;
            } else if (alignLeft != null) {
                rect.left = getOuterRect(alignLeft, parent).left;
            }

            if (below != null) {
                rect.top = getOuterRect(below, parent).bottom;
            } else if (alignTop != null) {
                rect.top = getOuterRect(alignTop, parent).top;
            }

            if (toLeftOf != null) {
                rect.right = getOuterRect(toLeftOf, parent).left;
            } else if (alignRight != null) {
                rect.right = getOuterRect(alignRight, parent).right;
            }

            if (above != null) {
                rect.bottom = getOuterRect(above, parent).top;
            } else if (alignBottom != null) {
                rect.bottom = getOuterRect(alignBottom, parent).bottom;
            }

            if (alignCenter != null || alignMiddle != null) {
                Point center;
                if (alignCenter != null) {
                    center = getOuterCenter(alignCenter, parent);
                    if (rect.right >= 0) {
                        if (center.x == CENTER_PARENT) {
                            rect.left = CENTER_PARENT;
                        } else {
                            rect.left = center.x - (rect.right - center.x);
                        }
                    } else if (rect.left >= 0) {
                        if (center.x == CENTER_PARENT) {
                            rect.right = CENTER_PARENT;
                        } else {
                            rect.right = center.x + (center.x - rect.left);
                        }
                    } else if (width >= 0) {
                        if (center.x == CENTER_PARENT) {
                            rect.left = CENTER_PARENT;
                            rect.right = CENTER_PARENT;
                        } else {
                            rect.left = center.x - width / 2;
                        }
                    } else if (width == WRAP_CONTENT) {
                        rect.left = CENTER_PARENT;
                        rect.right = CENTER_PARENT;
                    } else if (width == MATCH_PARENT) {
                        rect.left = 0;
                        rect.right = MATCH_PARENT;
                    } else {
                        throw new RuntimeException("Unable to align center: the width is unknown");
                    }
                }

                if (alignMiddle != null) {
                    center = getOuterCenter(alignMiddle, parent);
                    if (rect.bottom >= 0) {
                        if (center.y == CENTER_PARENT) {
                            rect.top = CENTER_PARENT;
                        } else {
                            rect.top = center.y - (rect.bottom - center.y);
                        }
                    } else if (rect.top >= 0) {
                        if (center.y == CENTER_PARENT) {
                            rect.bottom = CENTER_PARENT;
                        } else {
                            rect.bottom = center.y + (center.y - rect.top);
                        }
                    } else if (height >= 0) {
                        if (center.y == CENTER_PARENT) {
                            rect.top = CENTER_PARENT;
                            rect.bottom = CENTER_PARENT;
                        } else {
                            rect.top = center.y - height / 2;
                        }
                    } else if (height == WRAP_CONTENT) {
                        rect.top = CENTER_PARENT;
                        rect.bottom = CENTER_PARENT;
                    } else if (height == MATCH_PARENT) {
                        rect.top = 0;
                        rect.bottom = MATCH_PARENT;
                    } else {
                        throw new RuntimeException("Unable to align middle: the height is unknown");
                    }
                }

            }

            return rect;
        }

        private Point getOuterCenter(View view, RelativeLayout parent) {
            Point res;
            if (view == parent) {
                if (!allowDeffered) {
                    View.Layout l = parent.getResolvedLayout();
                    res = new Point(l.getInnerWidth() / 2, l.getInnerHeight() / 2);
                } else {
                    res = new Point(CENTER_PARENT, CENTER_PARENT);
                }
            } else {
                Rect rect = getOuterRect(view, parent);
                Point size = rect.getSize();
                res = new Point(rect.left + size.x / 2, rect.top + size.y / 2);
            }

            return res;
        }

        public boolean isRelativeToParent(RelativeLayout parent) {
            return alignLeft == parent || alignCenter == parent || alignRight == parent
                    || alignTop == parent || alignMiddle == parent || alignBottom == parent;
        }

        public List<View> getDependencies() {
            List<View> result = new ArrayList<View>();
            if (alignLeft != null) result.add(alignLeft);
            if (alignCenter != null) result.add(alignCenter);
            if (alignRight != null) result.add(alignRight);
            if (alignTop != null) result.add(alignTop);
            if (alignMiddle != null) result.add(alignMiddle);
            if (below != null) result.add(below);
            if (above != null) result.add(above);

            return result;
        }

        private Rect getOuterRect(View view, RelativeLayout parent) {
            if (view == parent) {
                if (allowDeffered) {
                    return new Rect(0, 0, MATCH_PARENT, MATCH_PARENT);
                } else {
                    Point s = parent.getResolvedLayout().getInnerSize();
                    return new Rect(0, 0, s.x, s.y);
                }
            }

            Layout l = (Layout) view.getLayout();
            if (!allowDeffered) l = (Layout) view.getResolvedLayout();
            Rect outer = parent.getOuter(view);
            if (outer != null && allowDeffered) return outer;
            l.allowDeffered = allowDeffered;

            if (!l.shouldBeMeasured()) {
                outer = l.getOuterRect(parent);
            } else {
                View.Layout measured = parent.getMeasured(view);
                outer = l.getOuterRect(parent);
                if (outer.right == WRAP_CONTENT) {
                    outer.right = outer.left + measured.width + measured.margin.left + measured.margin.right;
                }
                if (outer.bottom == WRAP_CONTENT) {
                    outer.bottom = outer.top + measured.height + margin.top + margin.bottom;
                }
            }

            if (outer.left < 0) outer.left += RELATIVE;
            if (outer.right < 0) outer.right += RELATIVE;
            if (outer.top < 0) outer.top += RELATIVE;
            if (outer.bottom < 0) outer.bottom += RELATIVE;

            return outer;
        }

        @Override
        public View.Layout clone() {
            Layout res = new Layout(width, height, margin.clone(), padding.clone());
            res.alignLeft = alignLeft;
            res.alignCenter = alignCenter;
            res.alignRight = alignRight;
            res.alignTop = alignTop;
            res.alignMiddle = alignMiddle;
            res.alignBottom = alignBottom;

            res.toLeftOf = toLeftOf;
            res.toRightOf = toRightOf;
            res.below = below;
            res.above = above;

            return res;
        }
    }

    private Rect getOuter(View view) {
        return getOuter(view, false);
    }

    private Rect getOuter(View view, boolean real) {
        if (!outers.containsKey(view) || real) {
            Layout measured;
            measured = getMeasured(view);

            measured.allowDeffered = !real;
            Rect outer = measured.getOuterRect(this);
            outers.put(view, outer);
        }
        return outers.get(view);
    }

    public Layout getChildLayout(View child) {
        return (Layout) child.getLayout();
    }

    public Layout getChildLayout(int position) {
        return (Layout) getChildAt(position).getLayout();
    }

    @Override
    public View inflateChild(XmlHelper node, Theme theme) {
        View child = super.inflateChild(node, theme);

        Layout layout = new Layout(child.getLayout());
        layout.alignLeft = findViewById(node.getIdAttr("mc", "alignLeft"));
        layout.toRightOf = findViewById(node.getIdAttr("mc", "toRightOf"));
        layout.alignCenter = findViewById(node.getIdAttr("mc", "alignCenter"));
        layout.alignRight = findViewById(node.getIdAttr("mc", "alignRight"));
        layout.toRightOf = findViewById(node.getIdAttr("mc", "toRightOf"));

        layout.alignTop = findViewById(node.getIdAttr("mc", "alignTop"));
        layout.below = findViewById(node.getIdAttr("mc", "below"));
        layout.alignMiddle = findViewById(node.getIdAttr("mc", "alignMiddle"));
        layout.alignBottom = findViewById(node.getIdAttr("mc", "alignBottom"));
        layout.above = findViewById(node.getIdAttr("mc", "above"));
        child.setLayout(layout);

        return child;
    }
}
