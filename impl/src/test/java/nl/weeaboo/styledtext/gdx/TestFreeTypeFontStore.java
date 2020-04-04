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

    public static final TextStyle PIXEL_32 = textStyle("pixel", 32);

    private final GdxFontGenerator generator = new GdxFontGenerator();

    public TestFreeTypeFontStore(YDir ydir) {
        generator.setYDir(ydir);

        try {
            register(SERIF_16);
            register(SERIF_32);
            register(SERIF_32_ITALIC);

            generator.disableAntiAlias();
            register(PIXEL_32);
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
        registerFont(generator.load(filename, style));
    }

    @Override
    public GdxFontMetrics getFontMetrics(TextStyle style) {
        return (GdxFontMetrics)super.getFontMetrics(style);
    }

    public List<BitmapFont> getBitmapFonts() {
        List<BitmapFont> result = new ArrayList<>();
        for (GdxFontInfo fontInfo : getFonts()) {
            result.add(fontInfo.font);
        }
        return result;
    }

}

