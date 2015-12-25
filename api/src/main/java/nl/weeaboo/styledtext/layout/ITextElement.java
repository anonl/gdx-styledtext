package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.ETextAlign;

public interface ITextElement extends ILayoutElement, IGlyphSequence {

    ETextAlign getAlign();
    float getAscent();
    boolean isRightToLeft();
    int getBidiLevel();

    float getKerning(int glyphId);

}
