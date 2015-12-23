package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class FontStyleTest {

    @Test
    public void fromString() {
        // Basic cases
        assertFromString(EFontStyle.PLAIN, "plain");
        assertFromString(EFontStyle.ITALIC, "italic");
        assertFromString(EFontStyle.BOLD, "bold");
        assertFromString(EFontStyle.BOLD_ITALIC, "bolditalic");

        // Invalid cases
        assertFromString(null, "PLAIN"); // id comparison is case sensitive
        assertFromString(null, "invalid"); // invalid id
        assertFromString(null, null); // Doesn't crash on null (not specified, but shouldn't change)
    }

    /**
     * Test behavior of {@link EFontStyle#combine(EFontStyle, EFontStyle)} when one or both arguments are
     * {@code null}.
     */
    @Test
    public void combineWithNull() {
        // Combining anything with null produces the non-null value
        for (EFontStyle style : EFontStyle.values()) {
            assertCombine(style, style, null);
        }

        // Combining null with null produces null
        assertCombine(null, null, null);
    }

    /**
     * Test behavior of {@link EFontStyle#combine(EFontStyle, EFontStyle)} with two non-null values.
     */
    @Test
    public void combineWithOther() {
        // Combining anything with itself does nothing
        for (EFontStyle style : EFontStyle.values()) {
            assertCombine(style, style, style);
        }

        // Test specific cases of combining different styled
        assertCombine(EFontStyle.ITALIC, EFontStyle.PLAIN, EFontStyle.ITALIC);
        assertCombine(EFontStyle.BOLD, EFontStyle.PLAIN, EFontStyle.BOLD);
        assertCombine(EFontStyle.BOLD_ITALIC, EFontStyle.PLAIN, EFontStyle.BOLD_ITALIC);

        assertCombine(EFontStyle.BOLD_ITALIC, EFontStyle.ITALIC, EFontStyle.BOLD);
        assertCombine(EFontStyle.BOLD_ITALIC, EFontStyle.ITALIC, EFontStyle.BOLD_ITALIC);

        assertCombine(EFontStyle.BOLD_ITALIC, EFontStyle.BOLD, EFontStyle.BOLD_ITALIC);
    }

    /**
     * Asserts that combining alpha and beta produces the expected result. Also asserts that the order of
     * arguments to combine() doesn't matter.
     */
    private void assertCombine(EFontStyle expected, EFontStyle alpha, EFontStyle beta) {
        Assert.assertEquals(expected, EFontStyle.combine(alpha, beta));
        Assert.assertEquals(expected, EFontStyle.combine(beta, alpha));
    }

    private static void assertFromString(EFontStyle expected, String string) {
        Assert.assertEquals(expected, EFontStyle.fromString(string));
    }

}
