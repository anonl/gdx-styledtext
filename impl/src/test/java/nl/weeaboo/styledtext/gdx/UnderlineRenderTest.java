package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

@RunWith(GdxLwjgl3TestRunner.class)
public class UnderlineRenderTest extends GdxRenderTest {

    private static final float EPSILON = 0.001f;

    private TextStyle underlineStyle;

    @Before
    public void before() throws IOException {
        MutableTextStyle mts = SERIF_32.mutableCopy();
        mts.setUnderlined(true);
        underlineStyle = mts.immutableCopy();
        fontStore.register(underlineStyle);
    }

    @Test
    public void underlineMetrics() {
        UnderlineMetrics metrics = fontStore.getFontMetrics(SERIF_32).getUnderlineMetrics();
        Assert.assertEquals(-3f, metrics.getUnderlinePosition(), EPSILON);
        Assert.assertEquals(2f, metrics.getUnderlineThickness(), EPSILON);
    }

    @Test
    public void basicUnderline() {
        checkRenderResult("basic-underline", renderText(new StyledText("ABC", underlineStyle)));
    }

    @Test
    public void partialUnderline() {
        MutableStyledText mst = new MutableStyledText("ABC");
        mst.setBaseStyle(SERIF_32);
        mst.setStyle(underlineStyle, 1);

        // Only 'B' has the underline style
        checkRenderResult("underline-partial1", renderText(mst.immutableCopy()));

        // Not all chars visible
        mst.setStyle(underlineStyle);
        checkRenderResult("underline-partial2", renderText(mst.immutableCopy(), 2f));

        // Not all chars visible, Right-to-left
        LayoutParameters params = new LayoutParameters();
        params.isRightToLeft = true;
        // - renders as right-aligned @*
        checkRenderResult("underline-partial3", renderText(new StyledText("*@#", underlineStyle), 2f, params));
    }

}
