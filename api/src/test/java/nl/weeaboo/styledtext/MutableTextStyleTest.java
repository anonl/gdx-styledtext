package nl.weeaboo.styledtext;

import static nl.weeaboo.styledtext.StyledTextTestData.ALIGN;
import static nl.weeaboo.styledtext.StyledTextTestData.FONT_NAME;
import static nl.weeaboo.styledtext.StyledTextTestData.FONT_SIZE;
import static nl.weeaboo.styledtext.StyledTextTestData.FONT_STYLE;
import static nl.weeaboo.styledtext.StyledTextTestData.OUTLINE_COLOR;
import static nl.weeaboo.styledtext.StyledTextTestData.OUTLINE_SIZE;
import static nl.weeaboo.styledtext.StyledTextTestData.SHADOW_COLOR;
import static nl.weeaboo.styledtext.StyledTextTestData.SHADOW_DX;
import static nl.weeaboo.styledtext.StyledTextTestData.SHADOW_DY;
import static nl.weeaboo.styledtext.StyledTextTestData.SPEED;
import static nl.weeaboo.styledtext.StyledTextTestData.TEXT_COLOR;
import static nl.weeaboo.styledtext.StyledTextTestData.UNDERLINE;

import org.junit.Assert;
import org.junit.Test;

public class MutableTextStyleTest {

    private static final float EPSILON = 1e-4f;

    @Test
    public void basicSetters() {
        MutableTextStyle mts = new MutableTextStyle();
        mts.setFontName(FONT_NAME);
        mts.setFontStyle(FONT_STYLE);
        mts.setFontSize(FONT_SIZE);
        mts.setAlign(ALIGN);
        mts.setColor(TEXT_COLOR);
        mts.setUnderlined(UNDERLINE);

        mts.setOutlineSize(OUTLINE_SIZE);
        mts.setOutlineColor(OUTLINE_COLOR);

        mts.setShadowColor(SHADOW_COLOR);
        mts.setShadowDx(SHADOW_DX);
        mts.setShadowDy(SHADOW_DY);

        mts.setSpeed(SPEED);

        Assert.assertEquals(FONT_NAME, mts.getFontName());
        Assert.assertEquals(FONT_STYLE, mts.getFontStyle());
        Assert.assertEquals(FONT_SIZE, mts.getFontSize(), EPSILON);
        Assert.assertEquals(ALIGN, mts.getAlign());
        TextColorTestUtil.assertColor(TEXT_COLOR, mts.getColor());

        Assert.assertEquals(UNDERLINE, mts.isUnderlined());

        Assert.assertEquals(OUTLINE_SIZE, mts.getOutlineSize(), EPSILON);
        TextColorTestUtil.assertColor(OUTLINE_COLOR, mts.getOutlineColor());

        TextColorTestUtil.assertColor(SHADOW_COLOR, mts.getShadowColor());
        Assert.assertEquals(SHADOW_DX, mts.getShadowDx(), EPSILON);
        Assert.assertEquals(SHADOW_DY, mts.getShadowDy(), EPSILON);

        Assert.assertEquals(SPEED, mts.getSpeed(), EPSILON);
    }

    /** Test additional convenience overloads for setters */
    @Test
    public void extendedSetters() {
        MutableTextStyle mts = new MutableTextStyle();
        mts.setFont(FONT_NAME, FONT_STYLE, FONT_SIZE);
        Assert.assertEquals(FONT_NAME, mts.getFontName());
        Assert.assertEquals(FONT_SIZE, mts.getFontSize(), EPSILON);
        Assert.assertEquals(FONT_STYLE, mts.getFontStyle());

        mts.setColor(.5f, .25f, .125f);
        TextColorTestUtil.assertColor(0xFF804020, mts.getColor());
        mts.setColor(0x20, 0x40, 0x80);
        TextColorTestUtil.assertColor(0xFF204080, mts.getColor());

        mts.setOutlineColor(.25f, .125f, .5f);
        TextColorTestUtil.assertColor(0xFF402080, mts.getOutlineColor());
        mts.setOutlineColor(0x40, 0x80, 0x20);
        TextColorTestUtil.assertColor(0xFF408020, mts.getOutlineColor());

        mts.setShadowColor(.125f, .5f, .25f);
        TextColorTestUtil.assertColor(0xFF208040, mts.getShadowColor());
        mts.setShadowColor(0x80, 0x20, 0x40);
        TextColorTestUtil.assertColor(0xFF802040, mts.getShadowColor());
    }

    @Test
    public void addRemoveProperties() {
        MutableTextStyle mts = new MutableTextStyle();

        // Initially has no properties
        for (ETextAttribute attr : ETextAttribute.values()) {
            Assert.assertFalse(mts.hasProperty(attr));
        }

        // Add and remove some properties
        mts.setProperty(ETextAttribute.FONT_NAME, "test");
        mts.setProperty(ETextAttribute.COLOR, 0xFF804020);
        Assert.assertTrue(mts.hasProperty(ETextAttribute.FONT_NAME));
        Assert.assertEquals("test", mts.getProperty(ETextAttribute.FONT_NAME, "ERROR"));
        Assert.assertTrue(mts.hasProperty(ETextAttribute.COLOR));
        TextColorTestUtil.assertColor(0xFF804020, (Integer)mts.getProperty(ETextAttribute.COLOR, 0));

        mts.removeProperty(ETextAttribute.COLOR);
        Assert.assertFalse(mts.hasProperty(ETextAttribute.COLOR));
        TextColorTestUtil.assertColor(0, (Integer)mts.getProperty(ETextAttribute.COLOR, 0));
    }

    /** Check the default value for each attribute */
    @Test
    public void defaultValues() {
        MutableTextStyle mts = new MutableTextStyle();
        Assert.assertEquals(null, mts.getFontName());
        Assert.assertEquals(EFontStyle.PLAIN, mts.getFontStyle());
        Assert.assertEquals(12f, mts.getFontSize(), EPSILON);
        Assert.assertEquals(ETextAlign.NORMAL, mts.getAlign());
        TextColorTestUtil.assertColor(0xFFFFFFFF, mts.getColor());

        Assert.assertEquals(false, mts.isUnderlined());

        Assert.assertEquals(0f, mts.getOutlineSize(), EPSILON);
        TextColorTestUtil.assertColor(0, mts.getOutlineColor());

        TextColorTestUtil.assertColor(0, mts.getShadowColor());
        Assert.assertEquals(0f, mts.getShadowDx(), EPSILON);
        Assert.assertEquals(0f, mts.getShadowDy(), EPSILON);

        Assert.assertEquals(1f, mts.getSpeed(), EPSILON);
    }

    @Test
    public void convenienceConstructor() {
        // fontName/fontSize version
        MutableTextStyle mts = new MutableTextStyle(FONT_NAME, FONT_SIZE);
        Assert.assertEquals(FONT_NAME, mts.getFontName());
        Assert.assertEquals(FONT_SIZE, mts.getFontSize(), EPSILON);

        // Try to pass null for fontName
        mts = new MutableTextStyle(null, FONT_SIZE);
        // Font name property not set if null
        Assert.assertFalse(mts.hasProperty(ETextAttribute.FONT_NAME));

        // fontName/fontStyle/fontSize version
        mts = new MutableTextStyle(FONT_NAME, FONT_STYLE, FONT_SIZE);
        Assert.assertEquals(FONT_NAME, mts.getFontName());
        Assert.assertEquals(FONT_STYLE, mts.getFontStyle());
        Assert.assertEquals(FONT_SIZE, mts.getFontSize(), EPSILON);

    }

    @Test
    public void hasOutline() {
        MutableTextStyle mts = new MutableTextStyle();
        assertHasOutline(false, mts);
        mts.setOutlineColor(OUTLINE_COLOR);
        // Only color set, size is still 0
        assertHasOutline(false, mts);
        mts.setOutlineSize(OUTLINE_SIZE);
        // Only considered to have an outline is size > 0 and not transparent
        assertHasOutline(true, mts);
        mts.setOutlineColor(0);
        assertHasOutline(false, mts);
    }

    @Test
    public void hasShadow() {
        MutableTextStyle mts = new MutableTextStyle();
        assertHasShadow(false, mts);
        mts.setShadowDx(SHADOW_DX);
        // Only considered to have a shadow if offset != 0 and not transparent
        assertHasShadow(false, mts);

        mts.setShadowColor(SHADOW_COLOR);
        assertHasShadow(true, mts);

        mts.setShadowDx(0);
        mts.setShadowDy(SHADOW_DY);
        // Either dx or dy must be != 0
        assertHasShadow(true, mts);
        mts.setShadowDy(0);
        assertHasShadow(false, mts);
    }

    private static void assertHasOutline(boolean expected, AbstractTextStyle ts) {
        Assert.assertEquals(expected, ts.hasOutline());
    }

    private static void assertHasShadow(boolean expected, AbstractTextStyle ts) {
        Assert.assertEquals(expected, ts.hasShadow());
    }

}
