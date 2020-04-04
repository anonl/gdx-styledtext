package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.PIXEL_32;
import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;
import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32_ITALIC;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;

@RunWith(GdxLwjgl3TestRunner.class)
public class TextRenderTest extends GdxRenderTest {

    @Before
    public void before() {
        generate = false;
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
    public void unicode() {
        checkRenderResult("unicode", renderText(new StyledText(TestSentences.UNICODE_SYMBOLS, SERIF_32)));
    }

    @Test
    public void antiAlias() {
        checkRenderResult("anti-alias-100", renderText(new StyledText(TestSentences.ALPHANUMERIC, SERIF_32)));

        setScale(.5f);
        checkRenderResult("anti-alias-050", renderText(new StyledText(TestSentences.ALPHANUMERIC, SERIF_32)));
    }

    @Test
    public void noAntiAlias() {
        checkRenderResult("no-anti-alias-100", renderText(new StyledText(TestSentences.ALPHANUMERIC, PIXEL_32)));

        setScale(.5f);
        // TODO: Due to rounding differences, this test fails on some systems
        // checkRenderResult("no-anti-alias-050", renderText(new StyledText(TestSentences.ALPHANUMERIC, PIXEL_32)));
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

    /** Whitespace is normally collapsed, but multiple newlines should never be collapsed together. */
    @Test
    public void multipleNewlines() {
        String text = "A\n\nA";
        checkRenderResult("multiple-newlines", renderText(new StyledText(text, SERIF_32)));
    }

    @Test
    public void subLayout() {
        LayoutParameters params = new LayoutParameters();
        params.x = 10;
        params.y = 20;

        StyledText text = new StyledText("ABC\nDEF\nGHI", SERIF_32);
        ITextLayout full = layout(text, params);

        checkRenderResult("sublayout0", renderText(full, -1f, params));

        // Sub-layout from line 1+ onwards
        checkRenderResult("sublayout1", renderText(full.getLineRange(1, 3), -1f, params));

        // Sub-sub-layout (generated from another sub-layout)
        checkRenderResult("sublayout2", renderText(full.getLineRange(1, 3).getLineRange(1, 2), -1f, params));
    }

}
