package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CharArrayIteratorTest {

    private static final char DONE = CharacterIterator.DONE;

    private char[] testChars;

    @Before
    public void before() {
        testChars = new char[] { 'a', 'b', 'c', 'd' };
    }

    @Test
    public void iterate() {
        CharArrayIterator itr = new CharArrayIterator(testChars, 1, 2);
        /*
         * getBeginIndex always returns 0, pretending the character iterator runs from 0..len makes internal
         * bookkeeping slightly easier
         */
        Assert.assertEquals(0, itr.getBeginIndex());
        Assert.assertEquals(0, itr.getIndex());
        Assert.assertEquals(2, itr.getEndIndex());

        Assert.assertEquals('b', itr.first());
        Assert.assertEquals('c', itr.last());
        Assert.assertEquals('b', itr.previous());
        Assert.assertEquals(DONE, itr.previous());
        // Even repeated calls to previous won't get your index < 0
        Assert.assertEquals(DONE, itr.previous());
        Assert.assertEquals('b', itr.current());

        Assert.assertEquals('c', itr.next());
        Assert.assertEquals(DONE, itr.next());
        // Even repeated calls to next won't get your index > len
        Assert.assertEquals(DONE, itr.next());
        Assert.assertEquals(DONE, itr.current());
        Assert.assertEquals('c', itr.previous());
    }

    @Test
    public void zeroLength() {
        CharArrayIterator itr = new CharArrayIterator(testChars, 1, 0);

        Assert.assertEquals(0, itr.getBeginIndex());
        Assert.assertEquals(0, itr.getIndex());
        Assert.assertEquals(0, itr.getEndIndex());
        Assert.assertEquals(DONE, itr.current());
        Assert.assertEquals(DONE, itr.first());
        Assert.assertEquals(0, itr.getIndex());
        Assert.assertEquals(DONE, itr.last());
        // Position is usually len-1 after a last(), empty string is a special case
        Assert.assertEquals(0, itr.getIndex());
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unused")
    public void arrayMustBeNonNull() {
        new CharArrayIterator(null, 0, 0);
    }

    @Test
    public void invalidOffsetLength() {
        assertInvalidOffsetLength(testChars, -1, 0);
        assertInvalidOffsetLength(testChars, 5, 0);
        assertInvalidOffsetLength(testChars, 0, -1);
        assertInvalidOffsetLength(testChars, 1, 4);
    }

    @Test
    public void setIndex() {
        CharArrayIterator itr = new CharArrayIterator(testChars, 1, 2);
        assertIndex(null, itr, -1);
        assertIndex(0, itr, 0);
        assertIndex(1, itr, 1);
        assertIndex(2, itr, 2);
        assertIndex(null, itr, 3);
    }

    /** Asserts that the constructor does bounds checking */
    private static void assertInvalidOffsetLength(char[] chars, int off, int len) {
        try {
            @SuppressWarnings("unused")
            CharArrayIterator itr = new CharArrayIterator(chars, off, len);
            Assert.fail("Expected an exception: off=" + off + ", len=" + len);
        } catch (IllegalArgumentException iae) {
            // Expected behavior
        }
    }

    /**
     * @param expected The expected new index after this method, or {@code null} if the index is expected to
     *        be invalid.
     */
    private void assertIndex(Integer expected, CharArrayIterator itr, int newIndex) {
        try {
            itr.setIndex(newIndex);
            Assert.assertEquals(expected, Integer.valueOf(itr.getIndex()));
        } catch (IllegalArgumentException iae) {
            Assert.assertTrue(expected == null);
        }
    }

    @Test
    public void cloneImpl() {
        CharArrayIterator base = new CharArrayIterator(testChars, 1, 2);
        base.setIndex(1);
        Assert.assertEquals(1, base.getIndex());
        Assert.assertEquals(2, base.getEndIndex());
        Assert.assertEquals('c', base.current());

        CharArrayIterator clone = base.clone();
        base.first(); // Alter base to ensure that clone state is independent

        Assert.assertEquals(1, clone.getIndex());
        Assert.assertEquals(2, clone.getEndIndex());
        Assert.assertEquals('c', clone.current());

        // The underlying array is not copied, so changes to it affect both iterators
        testChars[1] = 'x';
        Assert.assertEquals('x', base.first());
        Assert.assertEquals('x', clone.first());
    }

}
