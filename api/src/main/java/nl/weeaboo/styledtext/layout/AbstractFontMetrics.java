package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public abstract class AbstractFontMetrics implements IFontMetrics {

    private final float spaceWidth;
    private final float lineHeight;

    public AbstractFontMetrics(float spaceWidth, float lineHeight) {
        this.spaceWidth = spaceWidth;
        this.lineHeight = lineHeight;
    }

    @Override
    public final float getSpaceWidth() {
        return spaceWidth;
    }

    @Override
    public final float getLineHeight() {
        return lineHeight;
    }

    @Override
    public ILayoutElement layoutSpacing(CharSequence text, TextStyle style, LayoutParameters params) {
        int numSpaces = 0;
        for (int n = 0; n < text.length(); n++) {
            char c0 = text.charAt(n);
            int codepoint = c0;
            if (Character.isHighSurrogate(c0)) {
                n++;
                codepoint = Character.toCodePoint(c0, text.charAt(n));
            }

            if (codepoint == '\t') {
                numSpaces += 4;
            } else {
                numSpaces += 1;
            }
        }

        return new SpacingElement(numSpaces * getSpaceWidth(), getLineHeight());
    }

}
