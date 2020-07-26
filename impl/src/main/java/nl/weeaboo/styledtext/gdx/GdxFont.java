package nl.weeaboo.styledtext.gdx;

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFont implements Disposable {

    private final TextStyle style;
    private final BitmapFont bitmapFont;
    private final int nativePixelSize;
    private final UnderlineMetrics underlineMetrics;

    // Dedicated generator for this font (only used for incrementally generated fonts)
    @Nullable FreeTypeFontGenerator generator = null;

    private boolean disposed;

    public GdxFont(TextStyle style, BitmapFont bitmapFont, int nativePixelSize,
            UnderlineMetrics underlineMetrics) {

        this.style = style;
        this.bitmapFont = bitmapFont;
        this.nativePixelSize = nativePixelSize;
        this.underlineMetrics = underlineMetrics;
    }

    @Override
    public void dispose() {
        bitmapFont.dispose();

        if (generator != null) {
            generator.dispose();
            generator = null;
        }
    }

    public boolean isDisposed() {
        return disposed;
    }

    /** @return The amount of additional scaling required to reach the desired size */
    public float getScaleFor(TextStyle style) {
        return style.getFontSize() / nativePixelSize;
    }

    public TextStyle getStyle() {
        return style;
    }

    BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public UnderlineMetrics getUnderlineMetrics() {
        return underlineMetrics;
    }

    public int getNativePixelSize() {
        return nativePixelSize;
    }

}