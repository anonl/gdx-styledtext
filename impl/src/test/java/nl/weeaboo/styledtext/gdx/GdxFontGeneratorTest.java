package nl.weeaboo.styledtext.gdx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import nl.weeaboo.styledtext.TextStyle;

public class GdxFontGeneratorTest {

    private final GdxFontGenerator fontGenerator = new GdxFontGenerator();

    @Before
    public void before() {
        fontGenerator.setIncremental(false);
    }

    /**
     * Change the minification/magnification filters.
     */
    @Test
    public void setMinMagFilters() {
        FreeTypeFontParameter defaultFont = getParams();
        Assert.assertEquals(TextureFilter.MipMapLinearLinear, defaultFont.minFilter);
        Assert.assertEquals(true, defaultFont.genMipMaps);
        Assert.assertEquals(TextureFilter.Linear, defaultFont.magFilter);
        Assert.assertEquals(false, defaultFont.mono);

        fontGenerator.disableAntiAlias();

        FreeTypeFontParameter nonAaFont = getParams();
        Assert.assertEquals(TextureFilter.Nearest, nonAaFont.minFilter);
        Assert.assertEquals(false, nonAaFont.genMipMaps);
        Assert.assertEquals(TextureFilter.Nearest, nonAaFont.magFilter);
        Assert.assertEquals(true, nonAaFont.mono);
    }

    private FreeTypeFontParameter getParams() {
        return fontGenerator.getParams(TextStyle.defaultInstance(), 16);
    }

}
