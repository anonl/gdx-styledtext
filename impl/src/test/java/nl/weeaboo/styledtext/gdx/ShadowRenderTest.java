package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.weeaboo.gdx.test.GdxIntegrationTestRunner;
import nl.weeaboo.gdx.test.GdxRenderTest;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;

@RunWith(GdxIntegrationTestRunner.class)
public class ShadowRenderTest extends GdxRenderTest {

    private TextStyle red;
    private TextStyle green;
    private TextStyle blue;

    @Before
    public void before() throws IOException {
        // this.generate = true;

        red = createShadowStyle(0xFFFF0000, -2f, -0f);
        fontStore.register(red);

        green = createShadowStyle(0xFF00FF00, 0f, 2f);
        fontStore.register(green);

        blue = createShadowStyle(0xFF0000FF, 2f, 0f);
        fontStore.register(blue);
    }

    private TextStyle createShadowStyle(int argb, float dx, float dy) {
        MutableTextStyle mts = SERIF_32.mutableCopy();
        mts.setShadowColor(argb);
        mts.setShadowDx(dx);
        mts.setShadowDy(dy);
        return mts.immutableCopy();
    }

    @Test
    public void basicOutline() {
        MutableStyledText mst = new MutableStyledText("ABC");
        mst.setStyle(red, 0);
        mst.setStyle(green, 1);
        mst.setStyle(blue, 2);
        checkRenderResult("basic-shadow", renderText(mst.immutableCopy()));
    }

}
