package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class MirrorCharsTest {

    /** Test what happens when a non-mirrorable character is passed to the function */
    @Test
    public void unmirrored() {
        Assert.assertEquals('a', MirrorChars.getMirrorChar('a'));
    }

    @Test
    public void parens() {
        assertMirror('(', ')');
        assertMirror('{', '}');
        assertMirror('[', ']');
    }

    private static void assertMirror(char alpha, char beta) {
        Assert.assertEquals(beta, MirrorChars.getMirrorChar(alpha));
        Assert.assertEquals(alpha, MirrorChars.getMirrorChar(beta));
    }

}
