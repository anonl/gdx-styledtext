package nl.weeaboo.styledtext.gdx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Face;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.SizeMetrics;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Logger;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public final class GdxFontUtil {

    private static final Logger LOG = new Logger(GdxFontUtil.class.getName());

    private GdxFontUtil() {
    }

    public static void draw(Batch batch, ITextLayout layout, float dx, float dy, float visibleGlyphs) {
        if (visibleGlyphs < 0) {
            visibleGlyphs = 1e9f;
        }

        dx += layout.getOffsetX();
        dy += layout.getOffsetY();
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

    public static GdxFontInfo load(String fontPath, TextStyle style) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        return load(fontFile, style);
    }
    public static GdxFontInfo load(FileHandle fontFile, TextStyle style) throws IOException {
        int pixelSize = Math.round(style.getFontSize());
        return load(fontFile, style, new int[] { pixelSize })[0];
    }
    public static GdxFontInfo[] load(FileHandle fontFile, TextStyle style, int[] sizes) throws IOException {
        if (!fontFile.exists()) {
            throw new FileNotFoundException(fontFile.toString());
        }

        GdxFontInfo[] result = new GdxFontInfo[sizes.length];

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

                UnderlineMetrics underlineMetrics = deriveUnderlineMetrics(generator, sizes[n]);

                result[n] = new GdxFontInfo(style, bmFont, sizes[n], underlineMetrics);
            }
        } finally {
            generator.dispose();
        }

        return result;
    }

    private static UnderlineMetrics deriveUnderlineMetrics(FreeTypeFontGenerator generator, int size) {
        try {
            // Size metrics aren't publicly accessible (as of 1.9.3). (Ab)use reflection to gain access.
            Field faceField = FreeTypeFontGenerator.class.getDeclaredField("face");
            faceField.setAccessible(true);
            Face face = (Face)faceField.get(generator);

            SizeMetrics sizeMetrics = face.getSize().getMetrics();

            int yScale = sizeMetrics.getYscale(); // 16.16 fixed point
            float position = FreeType.toInt(face.getUnderlinePosition() * yScale >> 16);
            float thickness = FreeType.toInt(face.getUnderlineThickness() * yScale >> 16);
            return new UnderlineMetrics(position, thickness);
        } catch (Exception e) {
            LOG.error("Error fetching FreeType underline metrics", e);
        }

        // Return a reasonable default
        return UnderlineMetrics.defaultInstance(size);
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
