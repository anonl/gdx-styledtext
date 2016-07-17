package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontInfo implements Disposable {

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

    @Override
    public void dispose() {
        font.dispose();
    }

    /** @return The amount of additional scaling required to reach the desired size */
    public float getScaleFor(TextStyle style) {
        return style.getFontSize() / nativePixelSize;
    }

}