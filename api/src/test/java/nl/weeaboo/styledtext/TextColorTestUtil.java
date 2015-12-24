package nl.weeaboo.styledtext;

import java.util.Locale;

import org.junit.Assert;

public final class TextColorTestUtil {

    private TextColorTestUtil() {
    }

    public static void assertColor(int expected, int actual) {
        Assert.assertEquals(toHexString(expected), toHexString(actual));
    }

    private static String toHexString(int value) {
        return String.format(Locale.ROOT, "%08x", value);
    }

}
