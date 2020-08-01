package nl.weeaboo.styledtext.gdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;

public class TestFreeTypeFontStore extends GdxFontRegistry {

    public static final TextStyle SERIF_16 = textStyle("RobotoSlab", 16);
    public static final TextStyle SERIF_32 = textStyle("RobotoSlab", 32);
    public static final TextStyle SERIF_32_ITALIC = new TextStyle("RobotoSlab", EFontStyle.ITALIC, 32);

    public static final TextStyle PIXEL_32 = textStyle("pixel", 32);

    private final YDir ydir;

    public TestFreeTypeFontStore(YDir ydir) {
        this.ydir = ydir;

        try {
            register(SERIF_16);
            register(SERIF_32);
            register(SERIF_32_ITALIC);

            GdxFontGenerator generator = new GdxFontGenerator();
            generator.disableAntiAlias();
            register(PIXEL_32, generator);
        } catch (IOException ioe) {
            Assert.fail("Unable to load font(s): " + ioe);
        }
    }

    private static TextStyle textStyle(String fontName, int size) {
        return new TextStyle(fontName, size);
    }

    void register(TextStyle style) throws IOException {
        register(style, new GdxFontGenerator());
    }

    private void register(TextStyle style, GdxFontGenerator generator) throws IOException {
        String fontName = style.getFontName();
        Assert.assertNotNull(fontName);

        String filename = "font/" + fontName + ".ttf";
        generator.setYDir(ydir);
        addFont(generator.load(filename, style));
    }

    @Override
    public GdxFontMetrics getFontMetrics(TextStyle style) {
        return (GdxFontMetrics)super.getFontMetrics(style);
    }

    public List<BitmapFont> getBitmapFonts() {
        List<BitmapFont> result = new ArrayList<BitmapFont>();
        for (GdxFont fontInfo : getFonts()) {
            result.add(fontInfo.getBitmapFont());
        }
        return result;
    }

}

