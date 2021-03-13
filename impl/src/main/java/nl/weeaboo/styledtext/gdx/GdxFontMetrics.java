package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.AbstractFontMetrics;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.LayoutParameters;

final class GdxFontMetrics extends AbstractFontMetrics {

    private final GdxFont font;
    private final float scaleXY;

    GdxFontMetrics(GdxFont font, TextStyle style) {
        this(font, font.getScaleFor(style));
    }

    private GdxFontMetrics(GdxFont font, float scaleXY) {
        super(font.getBitmapFont().getSpaceXadvance() * scaleXY,
                font.getBitmapFont().getLineHeight() * scaleXY,
                font.getUnderlineMetrics().scaledCopy(scaleXY));

        this.font = font;
        this.scaleXY = scaleXY;
    }

    public float getPixelSize() {
        return getSize(font.getBitmapFont());
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel,
            LayoutParameters params) {

        return new GdxTextElement(str, style, bidiLevel, font, scaleXY);
    }

    public static float getScale(TextStyle style, BitmapFont font) {
        return style.getFontSize() / getSize(font);
    }

    private static float getSize(BitmapFont font) {
        return font.getCapHeight() - font.getDescent();
    }

}
