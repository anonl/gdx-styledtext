package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.RunSplitter.RunHandler;
import nl.weeaboo.styledtext.layout.RunSplitter.RunState;

final class TextLayoutAlgorithm implements RunHandler {

    private final IFontStore fontStore;

    private LayoutParameters params;
    private final List<LineLayout> finishedLines = new ArrayList<LineLayout>();
    private LineLayout currentLine;

    public TextLayoutAlgorithm(IFontStore fontStore) {
        this.fontStore = fontStore;
    }

    private void init(LayoutParameters params) {
        this.params = params;

        finishedLines.clear();
        currentLine = new LineLayout(params);
    }

    public ITextLayout layout(StyledText stext, LayoutParameters params) {
        init(params);

        RunSplitter.run(stext, params.isRightToLeft, this);
        endLine();

        float y = 0f;
        CompositeTextLayout compositeLayout = new CompositeTextLayout();
        for (LineLayout line : finishedLines) {
            List<ILayoutElement> elems = line.layout(0f, y);
            // Note: layoutHeight isn't available until layout() is called
            float layoutHeight = line.getLayoutHeight();
            compositeLayout.addLine(elems, y, layoutHeight);
            y += params.ydir * layoutHeight;
        }
        return compositeLayout;
    }

    @Override
    public void processRun(CharSequence text, RunState rs) {
        TextStyle style = rs.style;
        if (style == null) {
            style = TextStyle.defaultInstance();
        }

        IFontMetrics metrics = fontStore.getFontMetrics(style);
        if (metrics == null) {
            return; // Font not found
        }

        ILayoutElement elem;
        if (rs.isWhitespace) {
            elem = metrics.layoutSpacing(text, style, params);
        } else {
            elem = metrics.layoutText(text, style, rs.bidiLevel, params);
        }

        if (currentLine.fits(elem)) {
            currentLine.add(elem);
        } else {
            if (elem.isWhitespace()) {
                // Allow whitespace to overflow wrap width
                currentLine.add(elem);
                endLine();
            } else {
                endLine();
                currentLine.add(elem);
            }
        }

        if (rs.containsLineBreak) {
            endLine();
        }
    }

    private void endLine() {
        if (currentLine != null && !currentLine.isEmpty()) {
            finishedLines.add(currentLine);
            currentLine = new LineLayout(params);
        }
    }

    private static class LineLayout {

        private final LayoutParameters params;
        private final List<ILayoutElement> elements = new ArrayList<ILayoutElement>();

        private float layoutWidth;
        private float layoutHeight;
        private float maxAscent;
        private boolean isSimpleLTR = true;

        public LineLayout(LayoutParameters params) {
            this.params = params;
        }

        public void add(ILayoutElement elem) {
            elements.add(elem);
            layoutWidth += elem.getLayoutWidth();
            if (elem instanceof TextElement) {
                TextElement textElem = (TextElement)elem;
                maxAscent = Math.max(maxAscent, textElem.getAscent());
                if (textElem.getBidiLevel() != 0) {
                    isSimpleLTR = false;
                }
            }
        }

        public boolean fits(ILayoutElement elem) {
            if (elem.isWhitespace() || params.wrapWidth < 0) {
                return true;
            }

            float dx = 0;
            if (!elements.isEmpty()) {
                dx = LayoutUtil.getKerningOffset(elements.get(elements.size() - 1), elem);
            }

            return layoutWidth + dx + elem.getLayoutWidth() <= params.wrapWidth;
        }

        public List<ILayoutElement> layout(float x, float y) {
            removeTrailingWhitespace();

            List<SpacingElement> paddingElements = new ArrayList<SpacingElement>();
            List<ILayoutElement> newElements = new ArrayList<ILayoutElement>(elements.size());

            addPadding(newElements, paddingElements);

            // Assign widths to the spacing elements
            float freeSpace = (params.wrapWidth >= 0 ? params.wrapWidth - layoutWidth : 0);
            if (!paddingElements.isEmpty() && freeSpace > 0) {
                final float gapSize = freeSpace / paddingElements.size();
                for (SpacingElement padding : paddingElements) {
                    float w = Math.min(freeSpace, gapSize);
                    padding.setLayoutWidth(w);
                    freeSpace -= w;
                }
            }

            // Sort elements in visual order for layout
            if (!isSimpleLTR) {
                newElements = LayoutUtil.visualSortedCopy(newElements);
            }

            // Position text elements and calculate line width/height
            layoutWidth = 0f;
            layoutHeight = 0f;
            ILayoutElement prev = null;
            for (ILayoutElement elem : newElements) {
                x += LayoutUtil.getKerningOffset(prev, elem);

                if (elem instanceof TextElement) {
                    TextElement text = (TextElement)elem;

                    // TODO Only round y position if font.integer==true?
                    float dy = Math.round(maxAscent - text.getAscent());

                    text.setX(x);
                    text.setY(y + params.ydir * dy);
                }

                x += elem.getLayoutWidth();
                layoutWidth += elem.getLayoutWidth();
                layoutHeight = Math.max(layoutHeight, elem.getLayoutHeight());
            }

            // Return elements in logical order, without the additional padding elements
            return elements;
        }

        /** Add padding wherever the horizontal layout switches */
        private void addPadding(List<ILayoutElement> outElements, List<SpacingElement> outSpacing) {
            final boolean isRTL = params.isRightToLeft;
            final int defaultHAlign = (isRTL ? 1 : -1);

            int halign = defaultHAlign;
            for (ILayoutElement elem : elements) {
                if (elem instanceof TextElement) {
                    TextElement textElem = (TextElement)elem;
                    int halign2 = textElem.getAlign().getHorizontalAlign(isRTL);
                    if (halign == 1 && halign2 == -1) {
                        // Don't add padding between elements that want to touch
                    } else if (halign != halign2) {
                        SpacingElement padding = new SpacingElement();
                        outElements.add(padding);
                        outSpacing.add(padding);
                        halign = halign2;
                    }
                }

                outElements.add(elem);
            }
            if (halign != -defaultHAlign) {
                SpacingElement padding = new SpacingElement();
                outElements.add(padding);
                outSpacing.add(padding);
            }
        }

        public float getLayoutHeight() {
            return layoutHeight;
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }

        private void removeTrailingWhitespace() {
            ListIterator<ILayoutElement> litr = elements.listIterator(elements.size());
            while (litr.hasPrevious()) {
                ILayoutElement elem = litr.previous();
                if (!elem.isWhitespace()) {
                    break;
                }

                litr.remove();
                layoutWidth -= elem.getLayoutWidth();
            }
        }

    }

}
