package nl.weeaboo.styledtext.gdx;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontGenerator {

    private YDir ydir = YDir.UP;
    private boolean incremental = true;

    public GdxFontGenerator() {
    }

    /** Y-axis direction */
    public void setYDir(YDir dir) {
        this.ydir = dir;
    }

    /**
     * Switches between incremental font rendering, and rendering all characters immediately on load.
     *
     * @see FreeTypeFontParameter#incremental
     */
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
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

            final FreeTypeFontParameter params = new FreeTypeFontParameter();
            params.flip = (ydir == YDir.DOWN);
            params.size = sizes[n];

            if (!GdxFontUtil.isColorizable(style)) {
                params.color = GdxFontUtil.argb8888ToColor(style.getColor());
            }

            params.borderColor = GdxFontUtil.argb8888ToColor(style.getOutlineColor());
            params.borderWidth = style.getOutlineSize();

            params.shadowColor = GdxFontUtil.argb8888ToColor(style.getShadowColor());
            params.shadowOffsetX = Math.round(style.getShadowDx());
            params.shadowOffsetY = Math.round(style.getShadowDy());

            params.incremental = incremental;

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

}
