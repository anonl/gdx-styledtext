package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class MutableStyledTextTest {

    private final StyledTextTestData testData = new StyledTextTestData();
    private final TextStyle fullStyle = testData.fullStyle;
    private final StyledText st = new StyledText("abcd", fullStyle);
    private final TextStyle newStyle = new TextStyle("newFont", 13f);

    @Test
    public void appendStyledText() {
        MutableStyledText mst = st.mutableCopy();
        mst.append(st);

        assertEquals(StyledText.concat(st, st), mst.immutableCopy());
    }

    @Test
    public void appendSingle() {
        MutableStyledText mst = st.mutableCopy();
        mst.append('e', newStyle);

        Assert.assertEquals('e', mst.getChar(4));
        Assert.assertEquals(newStyle, mst.getStyle(4));
    }

    @Test
    public void copyConstructor() {
        MutableStyledText mst = new MutableStyledText();
        mst.append('a', fullStyle);

        MutableStyledText copy = mst.copy();
        // Initially, the copy should be equal
        assertEquals(mst, copy);

        // Ensure the copy is independent of the original
        copy.setStyle(null);
        assertNotEquals(mst, copy);
    }

    @Test
    public void extendStyle() {
        MutableStyledText mst = st.mutableCopy();
        mst.setStyle(null, 0);
        mst.extendStyle(newStyle);

        // Check that the style at every index has indeed been extended
        Assert.assertEquals(TextStyle.extend(null, newStyle), mst.getStyle(0));
        for (int n = 1; n < mst.length(); n++) {
            Assert.assertEquals(TextStyle.extend(fullStyle, newStyle), mst.getStyle(n));
        }
    }

    /**
     * This method does exactly the same as extendStyle(), only in the opposite order -- it extends the given
     * style with the current style.
     */
    @Test
    public void setBaseStyle() {
        MutableStyledText mst = st.mutableCopy();
        mst.setStyle(null, 0);
        mst.setStyle(newStyle, 1, mst.length());
        mst.setBaseStyle(fullStyle);

        // Check that the style at every index has indeed been extended
        Assert.assertEquals(TextStyle.extend(fullStyle, null), mst.getStyle(0));
        for (int n = 1; n < mst.length(); n++) {
            Assert.assertEquals(TextStyle.extend(fullStyle, newStyle), mst.getStyle(n));
        }
    }

    /** Test hashCode() and equals() implementations */
    @Test
    public void hashCodeEquals() {
        MutableStyledText mst = new MutableStyledText("abc", fullStyle);
        // Self equals works
        assertEquals(mst, mst);
        // Equals with null doesn't crash
        assertNotEquals(null, mst);
        // Mutable or not mutable doesn't matter to equals
        assertEquals(mst, new StyledText("abc", fullStyle));
        // Style matters to equals
        assertNotEquals(mst, new StyledText("abc", newStyle));
        // Text matters to equals
        assertNotEquals(mst, new StyledText("abd", fullStyle));
        // Equals works with substrings
        MutableStyledText sub = new MutableStyledText("*abc", fullStyle);
        sub.setStyle(null, 0);
        sub = sub.substring(1);
        assertEquals(mst, sub);
    }

    private static void assertEquals(AbstractStyledText<?> expected, AbstractStyledText<?> actual) {
        Assert.assertEquals(expected, actual);
        // If both values are equal, their hashcodes must be equal as well
        Assert.assertEquals(expected.hashCode(), actual.hashCode());
    }

    private static void assertNotEquals(AbstractStyledText<?> expected, AbstractStyledText<?> actual) {
        Assert.assertNotEquals(expected, actual);
    }

}
