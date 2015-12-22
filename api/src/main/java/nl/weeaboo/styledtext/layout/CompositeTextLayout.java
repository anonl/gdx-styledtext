package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.weeaboo.styledtext.TextStyle;

public class CompositeTextLayout implements ITextLayout {

    /**
     * No assumptions are made about the visual layout of the text. These layout elements may span multiple
     * lines, switch directions, etc.
     */
    private final List<Elem> elems = new ArrayList<Elem>();

    /**
     * Line information (line start/end, bounds, etc.)
     */
    private final List<Line> lines = new ArrayList<Line>();

    private int glyphCount;
    private float minX, minY, maxX, maxY;

    private float originY;

    void addLine(Collection<ILayoutElement> elems, float startY, float endY) {
        final int startGlyphIndex = glyphCount;

        for (ILayoutElement elem : elems) {
            add(elem);
        }

        lines.add(new Line(startGlyphIndex, glyphCount, startY, endY));
    }

    private void add(ILayoutElement elem) {
        int len = LayoutUtil.getGlyphCount(elem);
        int start = glyphCount;
        int end = start + len;

        elems.add(new Elem(elem, start, end));
        glyphCount += len;

        minX = Math.min(minX, elem.getX());
        minY = Math.min(minY, elem.getY());
        maxX = Math.max(maxX, elem.getX() + elem.getLayoutWidth());
        maxY = Math.max(maxY, elem.getY() + elem.getLayoutHeight());
    }

    @Override
    public float getOriginY() {
        return originY;
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
    public Iterable<ILayoutElement> getElements() {
        return getElements(0, glyphCount);
    }

    /** @return All layout elements that lie (partly) within the range {@code [glyphStart, glyphEnd)} */
    private Collection<ILayoutElement> getElements(int glyphStart, int glyphEnd) {
        List<ILayoutElement> result = new ArrayList<ILayoutElement>();
        for (Elem elem : elems) {
            if (elem.glyphEnd > glyphStart && elem.glyphStart <= glyphEnd) {

            }
            result.add(elem.elem);
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
        CompositeTextLayout result = new CompositeTextLayout();
        if (endLine > startLine) {
            result.originY = getLineTop(startLine);

            for (int lineNum = startLine; lineNum < endLine; lineNum++) {
                Line line = getLine(lineNum);
                result.addLine(getElements(line.glyphStart, line.glyphEnd), line.startY, line.endY);
            }
        }
        return result;
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        return Math.abs(getLineBottom(endLine) - getLineTop(startLine));
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

        public final ILayoutElement elem;
        public final int glyphStart;
        public final int glyphEnd;

        public Elem(ILayoutElement elem, int glyphStart, int glyphEnd) {
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
            if (elem instanceof IGlyphSequence) {
                IGlyphSequence seq = (IGlyphSequence)elem;
                return seq.getGlyphId(glyphIndex - glyphStart);
            }
            return 0;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public TextStyle getGlyphStyle(int glyphIndex) {
            if (elem instanceof IGlyphSequence) {
                IGlyphSequence seq = (IGlyphSequence)elem;
                return seq.getGlyphStyle(glyphIndex - glyphStart);
            }
            return null;
        }

    }

}
