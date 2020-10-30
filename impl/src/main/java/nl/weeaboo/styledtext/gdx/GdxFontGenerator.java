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
    private int renderCount = 2;
    private float gamma = 1f;
    private TextureFilter minFilter = TextureFilter.Linear;
    private TextureFilter magFilter = TextureFilter.Linear;
    private boolean monochrome = false;
    private boolean renderSnapToGrid = true;

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

        // If filtering is set to nearest, you really want to render only at integer positions
        setRenderSnapToGrid(true);
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

    /**
     * If {@code true}, round coordinates to the nearest integer during rendering. This increases sharpness at
     * the expense of slightly incorrect positioning.
     * <p>
     * Do note that this only rounds coordinates within the font rendering system. For this to work, the
     * parent transform used for rendering must produce pixel-aligned results (no rotation, translation/scale
     * must be integers).
     */
    public void setRenderSnapToGrid(boolean renderSnapToGrid) {
        this.renderSnapToGrid = renderSnapToGrid;
    }

    /**
     * Adjusts gamma curve compensation of the rendered glyphs. This value can be used to tweak the strength
     * of anti-aliasing applied to the rendered glyphs.
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    /**
     * Number of times to render the main glyph. Increase this number to reduce bleed-through of border/shadow colors.
     *
     * @see FreeTypeFontParameter#renderCount
     */
    public void setRenderCount(int renderCount) {
        this.renderCount = renderCount;
    }

    public GdxFont load(String fontPath, TextStyle style) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        return load(fontFile, style);
    }

    public GdxFont load(FileHandle fontFile, TextStyle style) throws IOException {
        int pixelSize = Math.round(style.getFontSize());
        return load(fontFile, style, new int[] { pixelSize })[0];
    }

    public GdxFont[] load(FileHandle fontFile, TextStyle style, int[] sizes) throws IOException {
        if (!fontFile.exists()) {
            throw new FileNotFoundException(fontFile.toString());
        }

        GdxFont[] result = new GdxFont[sizes.length];

        for (int n = 0; n < sizes.length; n++) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

            FreeTypeFontParameter params = getParams(style, sizes[n]);

            BitmapFont bmFont = generator.generateFont(params);
            bmFont.setUseIntegerPositions(renderSnapToGrid);

            UnderlineMetrics underlineMetrics = GdxFontUtil.deriveUnderlineMetrics(generator, sizes[n]);

            GdxFont font = new GdxFont(style, bmFont, sizes[n], underlineMetrics);
            if (incremental) {
                font.generator = generator;
                GdxCleaner.get().register(font, generator);
            } else {
                generator.dispose();
            }
            result[n] = font;
        }

        return result;
    }

    // @VisibleForTesting
    FreeTypeFontParameter getParams(TextStyle style, int fontSize) {
        FreeTypeFontParameter params = new FreeTypeFontParameter();

        params.flip = (yDir == YDir.DOWN);
        params.size = fontSize;

        params.renderCount = renderCount;

        params.minFilter = minFilter;
        params.genMipMaps = minFilter.isMipMap();
        params.magFilter = magFilter;

        params.gamma = gamma;
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
