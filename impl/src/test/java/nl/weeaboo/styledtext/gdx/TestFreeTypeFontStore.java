package nl.weeaboo.styledtext.gdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;

public class TestFreeTypeFontStore extends GdxFontStore {

    public static final TextStyle SERIF_16 = textStyle("RobotoSlab", 16);
    public static final TextStyle SERIF_32 = textStyle("RobotoSlab", 32);
    public static final TextStyle SERIF_32_ITALIC = new TextStyle("RobotoSlab", EFontStyle.ITALIC, 32);

    private final List<BitmapFont> bitmapFonts = new ArrayList<BitmapFont>();

    public TestFreeTypeFontStore() {
        try {
            register(SERIF_16);
            register(SERIF_32);
            register(SERIF_32_ITALIC);
        } catch (IOException ioe) {
            Assert.fail("Unable to load font(s): " + ioe);
        }
    }

    private static TextStyle textStyle(String fontName, int size) {
        return new TextStyle(fontName, size);
    }

    void register(TextStyle style) throws IOException {
        String fontName = style.getFontName();
        Assert.assertNotNull(fontName);

        String filename = "font/" + fontName + ".ttf";
        int pixelSize = Math.round(style.getFontSize());
        BitmapFont bitmapFont = GdxFontUtil.load(filename, style, pixelSize);
        registerFont(style, bitmapFont, pixelSize);
    }

    @Override
    public void registerFont(TextStyle style, BitmapFont font, int pixelSize) {
        bitmapFonts.add(font);

        super.registerFont(style, font, pixelSize);
    }

    @Override
    public GdxFontMetrics getFontMetrics(TextStyle style) {
        return (GdxFontMetrics)super.getFontMetrics(style);
    }

    public List<BitmapFont> getBitmapFonts() {
        return bitmapFonts;
    }

}

