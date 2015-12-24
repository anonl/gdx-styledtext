package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

final class BasicFontMetrics extends AbstractFontMetrics {

    public BasicFontMetrics(float spaceWidth, float lineHeight) {
        super(spaceWidth, lineHeight);
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel,
            LayoutParameters params) {

        return new BasicTextElement(str, style, bidiLevel);
    }

}
