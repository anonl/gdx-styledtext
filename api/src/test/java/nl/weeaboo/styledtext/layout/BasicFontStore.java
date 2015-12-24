package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

final class BasicFontStore implements IFontStore {

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        return new BasicFontMetrics(style.getFontSize(), style.getFontSize());
    }

}
