package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

import org.junit.Assert;
import org.junit.Test;

public class StyledTextTest {

    private final StyledTextTestData testData = new StyledTextTestData();
    private final StyledText st = new StyledText("abcd", testData.fullStyle);

    @Test
    public void characterIterator() {
        CharacterIterator itr = st.getCharacterIterator();
        Assert.assertEquals(0, itr.getBeginIndex());
        Assert.assertEquals(st.length(), itr.getEndIndex());

        Assert.assertEquals('d', itr.last());
        Assert.assertEquals(st.length() - 1, itr.getIndex());
    }

    @Test
    public void substring() {
        assertSubstringValid(false, st, -1, 0);
        assertSubstringValid(false, st, 1, 0);
        assertSubstringValid(true, st, 0, 4);
        assertSubstringValid(true, st, 2, 2);
        assertSubstringValid(false, st, 3, 5);

        Assert.assertEquals(st.substring(2, st.length()), st.substring(2));
    }

    private static void assertSubstringValid(boolean expectedValid, StyledText text, int from, int to) {
        try {
            text.substring(from, to);
            Assert.assertTrue(expectedValid);
        } catch (RuntimeException re) {
            Assert.assertFalse(expectedValid);
        }
    }

    @Test
    public void concat() {
        StyledText concat = st.concat("efgh");
        // Check if text is concatenated
        Assert.assertEquals("abcdefgh", concat.toString());
        // Check if styles are concatenated
        Assert.assertEquals(testData.fullStyle, concat.getStyle(0));
        Assert.assertEquals(TextStyle.defaultInstance(), concat.getStyle(4));
    }

    @Test
    public void bidi() {
        StyledText bidiSt = new StyledText("abcdעברית");

        Assert.assertEquals(false, st.isBidi());
        Assert.assertEquals(true, bidiSt.isBidi());

        // Check if base direction is correctly propagated
        // Text with no characters that are specifically left-to-right or right-to-left
        StyledText directionless = new StyledText("*");
        Assert.assertEquals(true, directionless.getBidi(false).baseIsLeftToRight());
        Assert.assertEquals(false, directionless.getBidi(true).baseIsLeftToRight());

        // Check if bidi algorithm works
        Assert.assertEquals(true, bidiSt.getBidi(false).isMixed());
        Assert.assertEquals(true, bidiSt.substring(4).getBidi(false).isRightToLeft());
        Assert.assertEquals(false, bidiSt.substring(0, 4).getBidi(false).isRightToLeft());
    }

    @Test
    public void asCharSequence() {
        Assert.assertEquals(4, st.length());

        Assert.assertEquals('a', st.charAt(0));
        Assert.assertEquals('b', st.charAt(1));
        Assert.assertEquals('c', st.charAt(2));
        Assert.assertEquals('d', st.charAt(3));

        Assert.assertEquals("abcd", st.toString());

        // Try every possible subsequence -- should always be equal to substring
        for (int from = 0; from < st.length(); from++) {
            for (int to = from; to <= st.length(); to++) {
                Assert.assertEquals(st.substring(from, to), st.subSequence(from, to));
            }
        }
    }

}
