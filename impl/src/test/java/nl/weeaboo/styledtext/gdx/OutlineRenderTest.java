package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyleParseException;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

@RunWith(GdxLwjgl3TestRunner.class)
public class OutlineRenderTest extends GdxRenderTest {

    private TextStyle red;
    private TextStyle green;
    private TextStyle blue;
    private TextStyle outlinePlusColor;

    @Before
    public void before() throws IOException, StyleParseException {
        red = createOutlineStyle(0xFFFF0000, 2f);
        fontStore.register(red);

        green = createOutlineStyle(0xFF00FF00, 1f);
        fontStore.register(green);

        blue = createOutlineStyle(0xFF0000FF, 3f);
        fontStore.register(blue);

        outlinePlusColor = red.extend(TextStyle.fromString("color=00FF00"));
        fontStore.register(outlinePlusColor);
    }

    private TextStyle createOutlineStyle(int argb, float size) {
        MutableTextStyle mts = SERIF_32.mutableCopy();
        mts.setOutlineColor(argb);
        mts.setOutlineSize(size);
        return mts.immutableCopy();
    }

    @Test
    public void basicOutline() {
        MutableStyledText mst = new MutableStyledText("ABC");
        mst.setStyle(red, 0);
        mst.setStyle(green, 1);
        mst.setStyle(blue, 2);
        checkRenderResult("basic-outline", renderText(mst.immutableCopy()));
    }

    /** Outline + text color */
    @Test
    public void outlinePlusColor() {
        // Green text with a red outline
        checkRenderResult("outline-plus-color", renderText(new StyledText("ABC", outlinePlusColor)));
    }

}
