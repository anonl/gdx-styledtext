package nl.weeaboo.styledtext.gdx;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontGenerator {

    private YDir yDir = YDir.UP;
    private boolean incremental = true;
    private TextureFilter minFilter = TextureFilter.MipMapLinearLinear;
    private TextureFilter magFilter = TextureFilter.Linear;
    private boolean monochrome = false;

    public GdxFontGenerator() {
    }

    /** Y-axis direction */
    public void setYDir(YDir dir) {
        this.yDir = dir;
    }

    /**
     * Switches between incremental font rendering, and rendering all characters immediately on load.
     *
     * @see FreeTypeFontParameter#incremental
     */
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    /**
     * Changes filtering settings to disable all anti-aliasing.
     */
    public void disableAntiAlias() {
        setMonochrome(true);
        setMinFilter(TextureFilter.Nearest);
        setMagFilter(TextureFilter.Nearest);
    }

    /**
     * Toggles monochrome rendering mode (disables anti-aliasing in the generated glyphs). To disable all
     * anti-aliasing, the min/mag filters must also be set to {@link TextureFilter#Nearest}.
     *
     * @see #setMinFilter
     * @see #setMagFilter
     */
    public void setMonochrome(boolean mono) {
        this.monochrome = mono;
    }

    /**
     * Sets the minification filter, used when rendering at a downscaled size.
     */
    public void setMinFilter(TextureFilter minFilter) {
        this.minFilter = minFilter;
    }

    /**
     * Sets the magnification filter, used when rendering at an upscaled size.
     */
    public void setMagFilter(TextureFilter magFilter) {
        this.magFilter = magFilter;
    }

    public GdxFontInfo load(String fontPath, TextStyle style) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        return load(fontFile, style);
    }

    public GdxFontInfo load(FileHandle fontFile, TextStyle style) throws IOException {
        int pixelSize = Math.round(style.getFontSize());
        return load(fontFile, style, new int[] { pixelSize })[0];
    }

    public GdxFontInfo[] load(FileHandle fontFile, TextStyle style, int[] sizes) throws IOException {
        if (!fontFile.exists()) {
            throw new FileNotFoundException(fontFile.toString());
        }

        GdxFontInfo[] result = new GdxFontInfo[sizes.length];

        for (int n = 0; n < sizes.length; n++) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

            FreeTypeFontParameter params = getParams(style, sizes[n]);

            BitmapFont bmFont = generator.generateFont(params);
            bmFont.setUseIntegerPositions(true);

            UnderlineMetrics underlineMetrics = GdxFontUtil.deriveUnderlineMetrics(generator, sizes[n]);

            GdxFontInfo fontInfo = new GdxFontInfo(style, bmFont, sizes[n], underlineMetrics);
            if (incremental) {
                fontInfo.generator = generator;
            } else {
                generator.dispose();
            }
            result[n] = fontInfo;
        }

        return result;
    }

    // @VisibleForTesting
    FreeTypeFontParameter getParams(TextStyle style, int fontSize) {
        FreeTypeFontParameter params = new FreeTypeFontParameter();

        params.flip = (yDir == YDir.DOWN);
        params.size = fontSize;

        params.minFilter = minFilter;
        params.genMipMaps = minFilter.isMipMap();
        params.magFilter = magFilter;

        params.mono = monochrome;

        if (!GdxFontUtil.isColorizable(style)) {
            params.color = GdxFontUtil.argb8888ToColor(style.getColor());
        }

        params.borderColor = GdxFontUtil.argb8888ToColor(style.getOutlineColor());
        params.borderWidth = style.getOutlineSize();

        params.shadowColor = GdxFontUtil.argb8888ToColor(style.getShadowColor());
        params.shadowOffsetX = Math.round(style.getShadowDx());
        params.shadowOffsetY = Math.round(style.getShadowDy());

        params.incremental = incremental;

        return params;
    }

}
