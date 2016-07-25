package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class StyledTextFormatTest {

    /** Simple cases combining various unstyled strings */
    @Test
    public void basicFormat() {
        // Zero format parameters
        assertFormat("plain", "plain");

        // One format parameter
        assertFormat("Ax", "{}x", "A");
        assertFormat("xA", "x{}", "A");
        assertFormat("A", "{}", "A");

        // Two format parameter
        assertFormat("ABx", "{}{}x", "A", "B");
        assertFormat("xAB", "x{}{}", "A", "B");
        assertFormat("AxB", "{}x{}", "A", "B");
        assertFormat("AB", "{}{}", "A", "B");
    }

    /** Escaped/invalid uses of '{}' */
    @Test
    public void escapedAndInvalidFormat() {
        assertFormat("{", "{");
        assertFormat("}", "}");
        assertFormat("{ }", "{ }"); // Space in the middle -> ignore
        assertFormat("{0}", "{0}");
        assertFormat("{1}", "{1}");
        assertFormat("{}", "\\{}");
        assertFormat("{}", "{\\}");
        assertFormat("{A}", "{{}}", "A");
        assertFormat("\\", "\\"); // Unterminated escape sequence
    }

    /** Format strings merging styled text parts */
    @Test
    public void styledTextParts() {
        MutableStyledText expected = new MutableStyledText();
        expected.append("x");
        expected.append(bold("A"));
        expected.append("y");

        // Styled arg
        assertFormat(expected.immutableCopy(), "x{}y", bold("A"));
        // Mutable styled arg
        assertFormat(expected.immutableCopy(), "x{}y", bold("A").mutableCopy());

        // Styled template
        MutableStyledText format = new MutableStyledText();
        format.append(bold("x"));
        format.append(italic("{}"));
        format.append(italic("y"));

        expected = new MutableStyledText();
        expected.append(bold("x"));
        expected.append(bold("A"));
        expected.append(italic("y"));
        assertFormat(expected.immutableCopy(), format.immutableCopy(), bold("A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyArgs() {
        assertFormat("", "{}{}", "A", "B", "C");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooFewArgs() {
        assertFormat("", "{}");
    }

    @Test
    public void nullArgs() {
        assertFormat("xnully", "x{}y", new Object[] { null });
    }

    @Test
    public void nonStringArgs() {
        assertFormat("123x8.5", "{}x{}", 123, 8.5);
    }

    private static StyledText bold(String text) {
        return new StyledText(text, TextStyle.BOLD);
    }

    private static StyledText italic(String text) {
        return new StyledText(text, TextStyle.ITALIC);
    }
    private void assertFormat(String expected, CharSequence format, Object... args) {
        assertFormat(new StyledText(expected), format, args);
    }
    private void assertFormat(StyledText expected, CharSequence format, Object... args) {
        StyledText actual = StyledText.format(format, args);
        Assert.assertEquals(expected, actual);
    }

}
