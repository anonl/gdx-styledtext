package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.ETextAlign;

public abstract class TextElement extends AbstractElement implements IGlyphSequence {

    private final ETextAlign align;
    private final int bidiLevel;

    public TextElement(ETextAlign align, int bidiLevel) {
        this.align = align;
        this.bidiLevel = bidiLevel;
    }

    public final ETextAlign getAlign() {
        return align;
    }

    public abstract float getAscent();

    @Override
    public final boolean isWhitespace() {
        return false;
    }

    public final int getBidiLevel() {
        return bidiLevel;
    }

    public final boolean isRightToLeft() {
        return LayoutUtil.isRightToLeftLevel(bidiLevel);
    }

    /**
     * @param glyphId Glyph id, see {@link IGlyphSequence#getGlyphId(int)}
     * @return The appropriate horizontal kerning offset when appending the given glyph to this text element
     */
    public float getKerning(int glyphId) {
        return 0f;
    }

}
