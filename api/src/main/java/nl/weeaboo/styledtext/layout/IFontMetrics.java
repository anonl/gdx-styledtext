package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IFontMetrics {

    /** Horizontal advance of a space character */
    float getSpaceWidth();

    /** Total line height for layout purposes */
    float getLineHeight();

    UnderlineMetrics getUnderlineMetrics();

    ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel, LayoutParameters params);

    ILayoutElement layoutSpacing(CharSequence text, TextStyle style, LayoutParameters params);

}
