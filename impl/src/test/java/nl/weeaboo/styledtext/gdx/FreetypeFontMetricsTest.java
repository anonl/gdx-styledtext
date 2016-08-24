package nl.weeaboo.styledtext.gdx;

import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_16;
import static nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore.SERIF_32;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.gdx.test.junit.GdxUiTest;

@Category(GdxUiTest.class)
@RunWith(GdxLwjgl3TestRunner.class)
public class FreetypeFontMetricsTest {

    private static final float SIZE_EPSILON = .01f;

    private TestFreeTypeFontStore fontStore;

    @Before
    public void before() {
        fontStore = new TestFreeTypeFontStore(YDir.DOWN);
    }

    /** Check that the generated bitmap fonts match expectations */
    @Test
    public void checkGeneratedBitmapFonts() {
        // Default fonts should not be scaled
        for (BitmapFont font : fontStore.getBitmapFonts()) {
            Assert.assertEquals(1f, font.getScaleX(), SIZE_EPSILON);
            Assert.assertEquals(1f, font.getScaleY(), SIZE_EPSILON);
        }
    }

    /** Ensure scaling calculations don't change between releases */
    @Test
    public void testScale() {
        GdxFontMetrics metrics16 = fontStore.getFontMetrics(SERIF_16);
        Assert.assertEquals(16, metrics16.getPixelSize(), SIZE_EPSILON);

        GdxFontMetrics metrics32 = fontStore.getFontMetrics(SERIF_32);
        /*
         * Generated fonts aren't required to exactly match the requested pixel sizes. This behavior comes
         * from FreeType.
         */
        Assert.assertEquals(31, metrics32.getPixelSize(), SIZE_EPSILON);
    }

}
