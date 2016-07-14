package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.AbstractFontMetrics;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

final class GdxFontMetrics extends AbstractFontMetrics {

    private final BitmapFont font;
    private final float scaleXY;

    public GdxFontMetrics(BitmapFont font, float scaleXY, UnderlineMetrics underlineMetrics) {
        super(font.getSpaceWidth() * scaleXY, font.getLineHeight() * scaleXY,
                underlineMetrics.scaledCopy(scaleXY));

        this.font = font;
        this.scaleXY = scaleXY;
    }

    public float getPixelSize() {
        return getSize(font);
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel,
            LayoutParameters params) {

        return new GdxTextElement(str, style, bidiLevel, font, scaleXY, getUnderlineMetrics());
    }

    public static float getScale(TextStyle style, BitmapFont font) {
        return style.getFontSize() / getSize(font);
    }

    private static float getSize(BitmapFont font) {
        return font.getCapHeight() - font.getDescent();
    }

}
