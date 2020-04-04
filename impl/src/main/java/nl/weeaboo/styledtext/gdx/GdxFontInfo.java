package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontInfo implements Disposable {

    public final TextStyle style;
    public final BitmapFont font;
    public final int nativePixelSize;
    public final UnderlineMetrics underlineMetrics;

    // Dedicated generator for this font (only used for incrementally generated fonts)
    FreeTypeFontGenerator generator = null;

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

        if (generator != null) {
            generator.dispose();
            generator = null;
        }
    }

    /** @return The amount of additional scaling required to reach the desired size */
    public float getScaleFor(TextStyle style) {
        return style.getFontSize() / nativePixelSize;
    }

}