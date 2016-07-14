package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontInfo {

    public final TextStyle style;
    public final BitmapFont font;
    public final int nativePixelSize;
    public final UnderlineMetrics underlineMetrics;

    public GdxFontInfo(TextStyle style, BitmapFont font, int nativePixelSize,
            UnderlineMetrics underlineMetrics) {

        this.style = style;
        this.font = font;
        this.nativePixelSize = nativePixelSize;
        this.underlineMetrics = underlineMetrics;
    }

    /** @return The amount of additional scaling required to reach the desired size */
    public float getScaleFor(TextStyle style) {
        return style.getFontSize() / nativePixelSize;
    }

}