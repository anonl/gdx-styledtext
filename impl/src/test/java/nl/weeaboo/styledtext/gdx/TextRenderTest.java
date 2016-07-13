package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;
import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32_ITALIC;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.GdxIntegrationTestRunner;
import nl.weeaboo.gdx.test.GdxRenderTest;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.layout.LayoutParameters;

@RunWith(GdxIntegrationTestRunner.class)
public class TextRenderTest extends GdxRenderTest {

    @Before
    public void before() {
        // this.generate = true;
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

}
