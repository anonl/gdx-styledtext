package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class TextAlignTest {

    @Test
    public void fromString() {
        // Basic cases
        assertFromString(ETextAlign.NORMAL, "normal");
        assertFromString(ETextAlign.REVERSE, "reverse");
        assertFromString(ETextAlign.LEFT, "left");
        assertFromString(ETextAlign.CENTER, "center");
        assertFromString(ETextAlign.RIGHT, "right");

        // Invalid cases
        assertFromString(null, "NORMAL"); // id comparison is case sensitive
        assertFromString(null, "invalid"); // invalid id
        assertFromString(null, null); // Doesn't crash on null (not specified, but shouldn't change)
    }

    @Test
    public void horizontalAlign() {
        assertHorizontalAlign(-1, ETextAlign.NORMAL, false);
        assertHorizontalAlign(1, ETextAlign.NORMAL, true);
        // Reverse alignment should be opposite of normal
        assertHorizontalAlign(1, ETextAlign.REVERSE, false);
        assertHorizontalAlign(-1, ETextAlign.REVERSE, true);

        // Other alignments are independent of right-to-left
        assertHorizontalAlign(-1, ETextAlign.LEFT);
        assertHorizontalAlign(0, ETextAlign.CENTER);
        assertHorizontalAlign(1, ETextAlign.RIGHT);
    }

    private static void assertFromString(ETextAlign expected, String string) {
        Assert.assertEquals(expected, ETextAlign.fromString(string));
    }

    /** Assert for alignments that are independent of right-to-left */
    private static void assertHorizontalAlign(int expected, ETextAlign align) {
        assertHorizontalAlign(expected, align, true);
        assertHorizontalAlign(expected, align, false);
    }

    private static void assertHorizontalAlign(int expected, ETextAlign align, boolean isRightToLeft) {
        Assert.assertEquals(expected, align.getHorizontalAlign(isRightToLeft));
    }

}
