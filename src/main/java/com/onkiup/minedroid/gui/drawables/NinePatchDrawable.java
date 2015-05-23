package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.themes.Theme;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Created by chedim on 4/25/15.
 */
public class NinePatchDrawable implements Drawable {

    protected BitmapDrawable[][] drawables = new BitmapDrawable[3][3];
    protected Point size = new Point(0, 0);
    protected Point originalSize;

    public NinePatchDrawable() {
    }

    public NinePatchDrawable(ResourceLocation location) throws IOException, OutOfMemoryError {
        setDrawables(location);
    }

    public void setDrawables(ResourceLocation location)  throws IOException, OutOfMemoryError {
        String name = location.getResourcePath();
        for (int x=0; x < 3; x++) {
            for (int y=0; y<3; y++) {
                drawables[x][y] =
                        new BitmapDrawable(new ResourceLocation(location.getResourceDomain(),
                                name + "/" + x + "." + y + ".png"));
                size = size.add(drawables[x][y].getSize());
            }
        }
        originalSize = size.clone();
    }

    @Override
    public void draw(Point where) {
        Point target = where.clone();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                drawables[x][y].draw(target);
                target.x += drawables[x][y].getSize().x;
            }
            target.y += drawables[x][1].getSize().y;
            target.x = where.x;
        }
    }

    @Override
    public void setSize(Point size) {
        Point borderSize = getBorderSize();
        Point center = size.sub(borderSize);

        if (center.x < 0 || center.y < 0) {
            return;
        }
        Point top = drawables[0][1].getSize();
        Point right = drawables[1][2].getSize();
        Point bottom = drawables[2][1].getSize();
        Point left = drawables[1][0].getSize();

        // setting middle sizes
        top.x = center.x;
        right.y = center.y;
        bottom.x = center.x;
        left.y = center.y;

        // setting center size
        drawables[1][1].setSize(center);
    }

    public Point getBorderSize() {
        Point borderSize = new Point(0, 0);
        borderSize = borderSize.add(drawables[0][0].getOriginalSize());
        borderSize = borderSize.add(drawables[2][2].getOriginalSize());
        return borderSize;
    }

    @Override
    public Point getSize() {
        return size;
    }

    @Override
    public Point getOriginalSize() {
        return originalSize;
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        try {
            setDrawables((ResourceLocation) node.getResourceAttr(MineDroid.NS, "src", null));
            setSize(node.getSize(MineDroid.NS, size));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NinePatchDrawable clone() {
        NinePatchDrawable result = new NinePatchDrawable();
        for (int x = 0; x < 3; x++)
            for (int y = 0; y <3; y++)
                if (drawables[x][y] != null)
                    result.drawables[x][y] = drawables[x][y].clone();

        if (size != null) result.setSize(size.clone());

        return result;
    }
}
