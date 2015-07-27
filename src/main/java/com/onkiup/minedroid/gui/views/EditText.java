package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.ColorDrawable;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TextView that allows user to edit it's content
 */
public class EditText extends TextView {

    protected int selectionStart, selectionEnd;
    protected Drawable cursor;
    protected Timer drawCursorTimer;
    protected boolean multiline;

    protected static int[] nonPrintables = {
            0, 1, 14, 15, 28, 29, 42, 54, 56, 59, 60, 61, 62, 63, 64, 65,
            66, 67, 68, 69, 70, 87, 88, 100, 101, 102, 103, 104, 105,
            112, 113, 121, 123, 148, 149, 150, 151, 156, 157, 183, 184,
            196, 197, 199, 200, 201, 203, 205, 207, 208, 209, 210, 211, 219,
            220, 221, 222, 223
    };

    public EditText(Context context) {
        this(context, "");
    }

    public EditText(Context context, String text) {
        super(context, text);
        cursor = new ColorDrawable(0xff000000);
        drawCursorTimer = new Timer();
        drawCursorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                drawCursor = !drawCursor;
            }
        }, 500, 500);

        ResourceLocation bg = MineDroid.getTheme(context).getStyle("edit_text").getResource("background", null);
        if (bg != null) {
            setBackground(MineDroid.inflateDrawable(this, bg));
        }
    }

    @Override
    public void handleKeyboardEvent(KeyEvent event) {
        super.handleKeyboardEvent(event);
        int code = event.keyCode;
        if (!event.cancel) {
            if (!processControl(event)) {
                for (int nonPrintable : nonPrintables) {
                    if (nonPrintable == code) return;
                }
                processInput(event);
            }
        }
    }

    private boolean processControl(KeyEvent event) {
        try {
            Method m = this.getClass().getDeclaredMethod("key_" + event.keyCode, KeyEvent.class);
            m.invoke(this, event);
            return true;
        } catch (NoSuchMethodException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkCursorBounds() {
        if (selectionStart < 0) selectionStart = 0;
        if (selectionEnd < 0) selectionEnd = 0;
        int max = getText().length();
        if (selectionStart > max) selectionStart = max;
        if (selectionEnd > max) selectionEnd = max;
        System.out.println("Cursor position: "+selectionEnd);
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        if (background == null) {
            ResourceLocation bg = style.getResource("background", null);
            if (bg != null) {
                setBackground(MineDroid.inflateDrawable(this, bg));
            }
        }
    }

    /**
     * handles Up key
     * @param event Keyboard event
     */
    private void key_200(KeyEvent event) {
        if (getText().length() == 0) return;
        List<String>[] lines = text.getSplitLines(getText(), resolvedLayout.getInnerWidth(), selectionEnd);
        String bottom;
        String top;

        if (lines[0].size() == 0) {
            return;
        } else if (lines[0].size() == 1) {
            selectionStart = selectionEnd = 0;
        } else {
            bottom = lines[0].get(lines[0].size() - 1);
            top = lines[0].get(lines[0].size() - 2);
            int x = bottom.length();
            int topL = x, topR = 0;
            if (topL >= top.length()) {
                topL = top.length();
                char check = top.charAt(top.length() - 1);
                if (check == ' ' || check == '\n') {
                    topL--;
                    topR++;
                }
            } else {
                topR = top.length() - topL;
            }
            selectionStart = selectionEnd = selectionEnd - x - topR;
        }

        checkCursorBounds();
    }

    /**
     * Handles left key
     * @param event Keyboard event
     */
    private void key_203(KeyEvent event) {
        if (getText().length() == 0) return;
        selectionEnd = --selectionStart;
        checkCursorBounds();
    }

    /**
     * Handles right key
     * @param event Keyboard event
     */
    private void key_205(KeyEvent event) {
        if (getText().length() == 0) return;
        selectionStart = ++selectionEnd;
        checkCursorBounds();
    }

    /**
     * Handles down key
     * @param event Keyboard event
     */
    private void key_208(KeyEvent event) {
        if (getText().length() == 0) return;
        List<String>[] lines = text.getSplitLines(getText(), resolvedLayout.getInnerWidth(), selectionEnd);
        String current;
        String target;
        if (lines[1].size() == 0) {
            return;
        } else if (lines[1].size() == 1) {
            selectionStart = selectionEnd = getText().length();
        } else {
            current = lines[1].get(0);
            target = lines[1].get(1);
            String left = "";
            if (lines[0].size() != 0) {
                left = lines[0].get(lines[0].size() - 1);
            }
            int l = left.length(), r = current.length();
            if (left.length() > 0 && left.charAt(left.length() - 1) == '\n') l = 0;

            int tl = l, tr = 0;
            if (tl >= target.length()) {
                tl = target.length();
                char check = target.charAt(target.length() - 1);
                if (check == ' ' || check == '\n') {
                    tl--;
                    tr++;
                }
            }
            selectionStart = selectionEnd = selectionEnd + r + tl;
        }

        checkCursorBounds();
    }

    private void key_14(KeyEvent event) {
        if (selectionStart == selectionEnd) {
            selectionStart--;
        }
        checkCursorBounds();
        replaceSelectedText("");
    }

    private void key_15(KeyEvent event) {

    }

    /**
     * Handles home key
     * @param event Keyboard event
     */
    private void key_199(KeyEvent event) {
        if (getText().length() == 0) return;
        List<String>[] lines = text.getSplitLines(getText(), resolvedLayout.getInnerWidth(), selectionEnd);
        if (lines[0].size() == 0) return;
        selectionStart = selectionEnd = selectionEnd - lines[0].get(lines[0].size() - 1).length();
    }

    /**
     * Handles end key
     * @param event Keyboard event
     */
    private void key_207(KeyEvent event) {
        if (getText().length() == 0) return;
        List<String>[] lines = text.getSplitLines(getText(), resolvedLayout.getInnerWidth(), selectionEnd);
        if (lines[1].size() == 0) return;
        selectionEnd = selectionEnd + lines[1].get(0).length();
        if (lines[1].get(0).length() > 0) {
            if (lines[1].get(0).charAt(lines[1].get(0).length() - 1) == '\n') {
                selectionEnd--;
            }
        }

        selectionStart = selectionEnd;
    }

    private void key_211(KeyEvent event) {
        if (selectionStart == selectionEnd) {
            selectionEnd++;
        }
        checkCursorBounds();
        replaceSelectedText("");
    }

    private void key_28(KeyEvent event) {
        replaceSelectedText("\n");
    }

    private void replaceSelectedText(String with) {
        String text = getText();
        String left = text.substring(0, selectionStart);
        String right = text.substring(selectionEnd);
        text = left+with+right;
        selectionStart += with.length();
        selectionEnd = selectionStart;
        setText(text);
    }

    protected void processInput(KeyEvent event) {
        replaceSelectedText(String.valueOf(event.keyChar));
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    boolean drawCursor = true;

    @Override
    public void drawContents() {
        // drawing the caret element
        Point textSize = getTextSize();
        Point cursorPosition = position.add(resolvedLayout.getInnerRect().coords());
        int charHeight = text.getCharHeight();
        cursor.setSize(new Point(1, charHeight));
        Point end = text.calculatePosition(getText(), resolvedLayout.getInnerWidth(), selectionEnd)
                .add(getGravityOffset(textSize));
        Point cursorFix = new Point(0, 0);
        if (end.x > resolvedLayout.getInnerWidth()) {
            cursorFix.x = resolvedLayout.getInnerWidth() - end.x;
            end.x = resolvedLayout.getInnerWidth();
        } else if (end.x < 0) {
            cursorFix.x = -end.x;
            end.x = 0;
        }

        if (end.y > resolvedLayout.getInnerHeight()) {
            cursorFix.y = resolvedLayout.getInnerHeight() - end.y;
            end.y = resolvedLayout.getInnerHeight();
        } else if (end.y - charHeight < 0) {
            cursorFix.y = - (end.y - charHeight - 1);
            end.y = charHeight;
        }

        if (drawCursor) {
            cursorPosition = cursorPosition.add(end);
            cursor.draw(cursorPosition.sub(new Point(0, charHeight + 1)));
        }

        text.setSize(textSize);
        Point offset = getGravityOffset(textSize);
        Point textPosition = position.add(offset).add(resolvedLayout.padding.coords()).add(cursorFix);
        text.draw(textPosition);

    }

    @Override
    protected String getThemeStyleName() {
        return "edit_text";
    }
}
