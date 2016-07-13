package nl.weeaboo.styledtext.gdx;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.ITextLayout;

public final class GdxFontUtil {

    private GdxFontUtil() {
    }

    public static void draw(Batch batch, ITextLayout layout, float dx, float dy, float visibleGlyphs) {
        if (visibleGlyphs < 0) {
            visibleGlyphs = 1e9f;
        }

        dy -= layout.getOriginY();
        for (ILayoutElement elem : layout.getElements()) {
            if (!(elem instanceof GdxTextElement)) {
                continue;
            }

            GdxTextElement textElem = (GdxTextElement)elem;
            textElem.draw(batch, dx, dy, visibleGlyphs);

            // Decrease visible glyphs
            visibleGlyphs -= textElem.getGlyphCount();
            if (visibleGlyphs <= 0) {
                break;
            }
        }
    }

    /** @deprecated Use */
    @Deprecated
    public static BitmapFont load(String fontPath, int size) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        return load(fontFile, new int[] { size })[0];
    }
    /** @deprecated Use */
    @Deprecated
    public static BitmapFont[] load(FileHandle fontFile, int[] sizes) throws IOException {
        return load(fontFile, TextStyle.defaultInstance(), sizes);
    }

    public static BitmapFont load(String fontPath, TextStyle style, int size) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        return load(fontFile, style, new int[] { size })[0];
    }
    public static BitmapFont[] load(FileHandle fontFile, TextStyle style, int[] sizes) throws IOException {
        if (!fontFile.exists()) {
            throw new FileNotFoundException(fontFile.toString());
        }

        BitmapFont[] result = new BitmapFont[sizes.length];

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        try {
            for (int n = 0; n < sizes.length; n++) {
                FreeTypeFontParameter parameter = new FreeTypeFontParameter();
                parameter.size = sizes[n];

                if (!GdxFontUtil.isColorizable(style)) {
                    parameter.color = GdxFontUtil.argb8888ToColor(style.getColor());
                }

                parameter.borderColor = argb8888ToColor(style.getOutlineColor());
                parameter.borderWidth = style.getOutlineSize();

                parameter.shadowColor = argb8888ToColor(style.getShadowColor());
                parameter.shadowOffsetX = Math.round(style.getShadowDx());
                parameter.shadowOffsetY = Math.round(style.getShadowDy());

                BitmapFont bmFont = generator.generateFont(parameter);
                bmFont.setUseIntegerPositions(true);
                result[n] = bmFont;
            }
        } finally {
            generator.dispose();
        }

        return result;
    }

    static Color argb8888ToColor(int argb) {
        Color color = new Color();
        Color.argb8888ToColor(color, argb);
        return color;
    }

    /**
     * @return {@code true} if the resulting glyphs can be color-tinted. Colored shadows or outlines result in
     *         glyphs that can't be colorized.
     */
    static boolean isColorizable(TextStyle style) {
        return !style.hasOutline() && !style.hasShadow();
    }
}
