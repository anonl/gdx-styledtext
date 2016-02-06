package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;
import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32_ITALIC;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.GdxIntegrationTestRunner;
import nl.weeaboo.gdx.test.GdxRenderTest;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;

@RunWith(GdxIntegrationTestRunner.class)
public class TextRenderTest extends GdxRenderTest {

    private TestFreeTypeFontStore fontStore;
    private int pad = 4;

    @Before
    public void before() {
        // this.generate = true;

        fontStore = new TestFreeTypeFontStore();
    }

    @Test
    public void emptyString() {
        checkRenderResult("empty", renderText(new StyledText("", SERIF_32)));
    }

    @Test
    public void basicText() {
        checkRenderResult("basic-text", renderText(new StyledText(TestSentences.ALPHANUMERIC, SERIF_32)));
    }

    @Test
    public void symbols() {
        checkRenderResult("symbols", renderText(new StyledText(TestSentences.ASCII_SYMBOLS, SERIF_32)));
    }

    @Test
    public void multiStyle() {
        MutableStyledText mts = new MutableStyledText();
        mts.append(new StyledText("f", SERIF_32));
        mts.append(new StyledText("i", SERIF_32_ITALIC));
        checkRenderResult("multi-style", renderText(mts.immutableCopy()));
    }

    @Test
    public void partiallyVisible() {
        String text = "ABC";
        String baseName = "partially-visible-";
        checkRenderResult(baseName + "1_50", renderText(new StyledText(text, SERIF_32), 1.50f));
        checkRenderResult(baseName + "1_75", renderText(new StyledText(text, SERIF_32), 1.75f));
        checkRenderResult(baseName + "3_00", renderText(new StyledText(text, SERIF_32), 3.00f));
    }

    @Test
    public void rightToLeft() {
        LayoutParameters params = new LayoutParameters();
        params.isRightToLeft = true;

        // Render some mirrorable characters in a right-to-left context
        // - renders as: []()
        checkRenderResult("right-to-left", renderText(new StyledText("()[]", SERIF_32), -1f, params));

        // - renders as right-aligned @*
        checkRenderResult("right-to-left-partial", renderText(new StyledText("*@#", SERIF_32), 2f, params));
    }

    @Test
    public void wordWrap() {
        LayoutParameters params = new LayoutParameters();
        params.wrapWidth = 75f;

        String text = "A A A A \nA\n A\n  A\n   A\n\tA";
        checkRenderResult("word-wrap", renderText(new StyledText(text, SERIF_32), -1f, params));
    }

    private void checkRenderResult(String filename, Rectangle r) {
        checkRenderResult(filename, r.x, r.y, r.width, r.height);
    }

    private Rectangle renderText(StyledText text) {
        return renderText(text, -1f);
    }

    private Rectangle renderText(StyledText text, float visibleChars) {
        LayoutParameters params = new LayoutParameters();
        return renderText(text, visibleChars, params);
    }

    private Rectangle renderText(StyledText text, float visibleChars, LayoutParameters params) {
        ITextLayout layout = LayoutUtil.layout(fontStore, text, params);

        float th = layout.getTextHeight();
        float tw = layout.getTextWidth();

        Rectangle bounds = new Rectangle2D.Float(0, 0, tw, th).getBounds();
        bounds.grow(pad * 2, pad * 2);

        clearRect(bounds.x, bounds.y, bounds.width, bounds.height);

        batch.begin();
        GdxFontUtil.draw(batch, layout, pad, pad + th, visibleChars);
        batch.end();

        return bounds;
    }

}
