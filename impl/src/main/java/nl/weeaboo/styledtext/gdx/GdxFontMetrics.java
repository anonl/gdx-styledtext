package nl.weeaboo.styledtext.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.AbstractFontMetrics;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.LayoutParameters;

public class GdxFontMetrics extends AbstractFontMetrics {

    private final BitmapFont font;

    public GdxFontMetrics(BitmapFont font, float scale) {
        super(font.getData().spaceWidth * scale, font.getData().lineHeight * scale);

        this.font = font;
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel,
            LayoutParameters params) {

        return new GdxTextElement(str, style, bidiLevel, font);
    }

    public static float getScale(TextStyle style, BitmapFont font) {
        return style.getFontSize() / getSize(font);
    }

    private static float getSize(BitmapFont font) {
        return font.getCapHeight() - font.getDescent();
    }

}
