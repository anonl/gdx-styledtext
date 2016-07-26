package nl.weeaboo.styledtext.layout;

/**
 * Represents a (word-wrapped) piece of styled text.
 */
public interface ITextLayout extends IGlyphSequence {

    Iterable<ITextElement> getElements();

    /**
     * Relative x-offset of the layout when rendering.
     *
     * @see #getOffsetY()
     */
    float getOffsetX();

    /**
     * Relative y-offset of the layout when rendering.
     *
     * @see #getOffsetX()
     */
    float getOffsetY();

    /**
     * The position of the y-origin to be used when rendering this layout. For example if a layout element has
     * a y of {@code 100} and the origin is {@code 20}, the element should be rendered at {@code +80}.
     *
     * @deprecated Replaced with {@link #getOffsetY}
     */
    @Deprecated
    float getOriginY();

    float getTextWidth();
    float getTextHeight();

    /**
     * The glyph index at which the requested line starts. If a line is empty, the next line will start at the
     * same glyph index.
     */
    int getGlyphOffset(int line);

    /** The number of lines within this layout */
    int getLineCount();

    /**
     * @param startLine (inclusive)
     * @param endLine (exclusive)
     * @return A new layout containing only the requested lines.
     */
    ITextLayout getLineRange(int startLine, int endLine);

    /**
     * @param startLine (inclusive)
     * @param endLine (exclusive)
     * @return The absolute height of the requested line range.
     */
    float getTextHeight(int startLine, int endLine);

}
