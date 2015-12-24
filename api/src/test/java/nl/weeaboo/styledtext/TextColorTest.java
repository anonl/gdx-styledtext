package nl.weeaboo.styledtext;

import org.junit.Test;

public class TextColorTest {

    @Test
    public void packFloat() {
        assertPacked(0x20ff8040, 1f, .5f, .25f, .125f);
        // Check that values are clipped when out of range
        assertPacked(0x00ff8040, 2f, .5f, .25f, -.123f);
    }

    @Test
    public void packInt() {
        assertPacked(0x20ff8040, 0xff, 0x80, 0x40, 0x20);
        // Check that values are clipped when out of range
        assertPacked(0x00ff8040, 333, 0x80, 0x40, -123);
    }

    private void assertPacked(int expected, float r, float g, float b, float a) {
        int packed = TextColor.packColorFloat(r, g, b, a);
        TextColorTestUtil.assertColor(expected, packed);
    }

    private void assertPacked(int expected, int r, int g, int b, int a) {
        int packed = TextColor.packColorInt(r, g, b, a);
        TextColorTestUtil.assertColor(expected, packed);
    }

}
