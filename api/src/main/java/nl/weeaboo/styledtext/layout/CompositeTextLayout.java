package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.weeaboo.styledtext.TextStyle;

final class CompositeTextLayout implements ITextLayout {

    /**
     * No assumptions are made about the visual layout of the text. These layout elements may span multiple
     * lines, switch directions, etc.
     */
    private final List<Elem> elems = new ArrayList<Elem>();

    /**
     * Line information (line start/end, bounds, etc.)
     */
    private final List<Line> lines = new ArrayList<Line>();

    /**
     * Y-axis direction; {@code -1} for y-up, {@code 1} for y-down.
     *
     * @see LayoutParameters#ydir
     */
    private final int ydir;

    private int glyphCount;
    private float minX, minY, maxX, maxY;

    private float dx, dy;

    /**
     * @see #getOffsetY()
     */
    public CompositeTextLayout(float dx, float dy, int ydir) {
        this.dx = dx;
        this.dy = dy;
        this.ydir = ydir;
    }

    /**
     * @param startY Line top y-position, relative to the layout's origin.
     */
    void addLine(Collection<ITextElement> elems, float startY, float endY) {
        final int startGlyphIndex = glyphCount;

        for (ITextElement elem : elems) {
            add(elem);
        }

        lines.add(new Line(startGlyphIndex, glyphCount, startY, endY));
    }

    private void add(ITextElement elem) {
        int len = LayoutUtil.getGlyphCount(elem);
        int start = glyphCount;
        int end = start + len;

        elems.add(new Elem(elem, start, end));
        glyphCount += len;

        if (elems.size() == 1) {
            minX = elem.getX();
            minY = elem.getY();
            maxX = elem.getX() + elem.getLayoutWidth();
            maxY = elem.getY() + elem.getLayoutHeight();
        } else {
            minX = Math.min(minX, elem.getX());
            minY = Math.min(minY, elem.getY());
            maxX = Math.max(maxX, elem.getX() + elem.getLayoutWidth());
            maxY = Math.max(maxY, elem.getY() + elem.getLayoutHeight());
        }
    }

    @Override
    public float getOffsetX() {
        return dx;
    }

    @Override
    public float getOffsetY() {
        return dy;
    }

    @Override
    public float getTextWidth() {
        return maxX - minX;
    }

    @Override
    public float getTextHeight() {
        return maxY - minY;
    }

    @Override
    public int getGlyphCount() {
        return glyphCount;
    }

    @Override
    public Iterable<ITextElement> getElements() {
        return getElements(0, glyphCount);
    }

    /** @return All layout elements that lie (partly) within the range {@code [glyphStart, glyphEnd)} */
    private Collection<ITextElement> getElements(int glyphStart, int glyphEnd) {
        List<ITextElement> result = new ArrayList<ITextElement>();
        for (Elem elem : elems) {
            if (elem.glyphEnd > glyphStart && elem.glyphStart < glyphEnd) {
                result.add(elem.elem);
            }
        }
        return result;
    }

    @Override
    public int getGlyphId(int glyphIndex) {
        Elem elem = findByGlyphIndex(glyphIndex);
        if (elem == null) {
            throw new ArrayIndexOutOfBoundsException(glyphIndex);
        }
        return elem.getGlyphId(glyphIndex);
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        Elem elem = findByGlyphIndex(glyphIndex);
        if (elem == null) {
            throw new ArrayIndexOutOfBoundsException(glyphIndex);
        }
        return elem.getGlyphStyle(glyphIndex);
    }

    private Elem findByGlyphIndex(int glyphIndex) {
        for (Elem elem : elems) {
            if (elem.containsGlyph(glyphIndex)) {
                return elem;
            }
        }
        return null;
    }

    private Line getLine(int lineNum) {
        if (lineNum < 0 || lineNum >= getLineCount()) {
            throw new ArrayIndexOutOfBoundsException(lineNum);
        }
        return lines.get(lineNum);
    }

    @Override
    public int getGlyphOffset(int line) {
        if (line < 0) {
            return 0;
        } else if (line >= getLineCount()) {
            return glyphCount;
        }
        return getLine(line).glyphStart;
    }

    @Override
    public int getLineCount() {
        return lines.size();
    }

    private float getLineTop(int line) {
        return getLine(line).startY;
    }

    private float getLineBottom(int line) {
        return getLine(line).endY;
    }

    @Override
    public ITextLayout getLineRange(int startLine, int endLine) {
        CompositeTextLayout result = new CompositeTextLayout(dx, dy, ydir);
        if (endLine > startLine) {
            result.dy -= ydir * getLineTop(0);
            result.dy += ydir * getLineTop(startLine);

            for (int lineNum = startLine; lineNum < endLine; lineNum++) {
                Line line = getLine(lineNum);
                result.addLine(getElements(line.glyphStart, line.glyphEnd), line.startY, line.endY);
            }
        }
        return result;
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        if (endLine <= startLine) {
            return 0f;
        }

        float startY = getLineTop(startLine);
        float endY = getLineBottom(endLine - 1);
        return Math.abs(endY - startY);
    }

    private static class Line {

        public final int glyphStart;
        public final int glyphEnd;

        public final float startY;
        public final float endY;

        public Line(int glyphStart, int glyphEnd, float startY, float endY) {
            this.glyphStart = glyphStart;
            this.glyphEnd = glyphEnd;
            this.startY = startY;
            this.endY = endY;
        }

    }

    private static class Elem {

        public final ITextElement elem;
        public final int glyphStart;
        public final int glyphEnd;

        public Elem(ITextElement elem, int glyphStart, int glyphEnd) {
            this.elem = elem;
            this.glyphStart = glyphStart;
            this.glyphEnd = glyphEnd;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public boolean containsGlyph(int glyphIndex) {
            return glyphIndex >= glyphStart && glyphIndex < glyphEnd;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public int getGlyphId(int glyphIndex) {
            return elem.getGlyphId(glyphIndex - glyphStart);
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public TextStyle getGlyphStyle(int glyphIndex) {
            return elem.getGlyphStyle(glyphIndex - glyphStart);
        }

    }

}
