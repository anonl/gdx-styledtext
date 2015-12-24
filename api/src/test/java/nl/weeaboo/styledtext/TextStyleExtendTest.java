package nl.weeaboo.styledtext;

import static nl.weeaboo.styledtext.StyledTextTestData.FONT_NAME;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleExtendTest {

    private static final StyledTextTestData testData = new StyledTextTestData();
    private static final TextStyle fullStyle = testData.fullStyle;
    private static final float EPSILON = 1e-4f;

    @Test
    public void extendMutable() {
        MutableTextStyle mts = new MutableTextStyle();
        mts.setFontName(FONT_NAME);
        Assert.assertEquals(FONT_NAME, mts.getFontName());
        Assert.assertFalse(mts.hasProperty(ETextAttribute.FONT_SIZE));

        TextStyle ts = new TextStyle("othername", 10f);
        mts.extend(ts);
        Assert.assertEquals("othername", mts.getFontName());
        Assert.assertTrue(mts.hasProperty(ETextAttribute.FONT_SIZE));
        Assert.assertEquals(10f, mts.getFontSize(), EPSILON);
    }

    @Test
    public void extendImmutable() {
        TextStyle ts = new TextStyle("othername", 10f);
        TextStyle extended = fullStyle.extend(ts);

        // Check modified attribute
        Assert.assertEquals("othername", extended.getFontName());
        Assert.assertEquals(10f, extended.getFontSize(), EPSILON);
        // All other attributes remain unchanged
        for (ETextAttribute attr : ETextAttribute.values()) {
            if (attr == ETextAttribute.FONT_NAME || attr == ETextAttribute.FONT_SIZE) {
                continue;
            }

            Assert.assertEquals(fullStyle.getProperty(attr, null), extended.getProperty(attr, null));
        }
    }

    @Test
    public void extendSelfMutable() {
        MutableTextStyle mts = fullStyle.mutableCopy();

        // Extend with null is a no-op
        mts.extend(null);
        Assert.assertEquals(fullStyle, mts.immutableCopy());

        // Extend with self is a no-op
        mts.extend(mts);
        Assert.assertEquals(fullStyle, mts.immutableCopy());
    }

    @Test
    public void extendSelfImmutable() {
        // Extend with null is a no-op and returns the unmodified object
        Assert.assertSame(fullStyle, fullStyle.extend(null));

        // Extend with self is a no-op and returns the unmodified object
        Assert.assertSame(fullStyle, fullStyle.extend(fullStyle));
    }

    @Test
    public void extendArray() {
        TextStyle[] out = new TextStyle[4];
        TextStyle[] base = new TextStyle[4];
        TextStyle[] ext = new TextStyle[4];

        TextStyle ts = new TextStyle("othername", 10f);
        base[0] = fullStyle;
        base[1] = fullStyle;
        ext[1] = ts;
        ext[2] = ts;
        ext[3] = ts;

        TextStyle.extend(out, base, ext);

        Assert.assertEquals(fullStyle, out[0]);
        Assert.assertEquals(TextStyle.extend(fullStyle, ts), out[1]);
        Assert.assertEquals(ts, out[2]);
        /*
         * Test for optimization: extending the same base/ext pair twice in a row reuses the result of the
         * previous index
         */
        Assert.assertSame(out[2], out[3]);
    }

    /** Passing arrays of different lengths to extend isn't allowed */
    @Test(expected = IllegalArgumentException.class)
    public void extendArrayInvalid() {
        TextStyle.extend(new TextStyle[2], new TextStyle[1], new TextStyle[1]);
    }
}
