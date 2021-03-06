package nl.weeaboo.styledtext.gdx;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import nl.weeaboo.styledtext.MirrorChars;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.TextElement;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

final class GdxTextElement extends TextElement {

    private static Texture blankTexture;

    private final TextStyle style;
    private final GdxFont font;
    private final float originalScaleX, originalScaleY;
    private final float scaleXY;

    private GlyphLayout glyphLayout;
    private float ascent; // Distance to baseline
    private int glyphCount;
    private boolean ydown;

    public GdxTextElement(CharSequence str, TextStyle style, int bidiLevel, GdxFont font, float scaleXY) {
        super(style.getAlign(), bidiLevel);

        this.style = style;
        this.font = font;

        BitmapFont bitmapFont = font.getBitmapFont();
        originalScaleX = bitmapFont.getScaleX();
        originalScaleY = bitmapFont.getScaleY();
        this.scaleXY = scaleXY;

        initGlyphLayout(str, style);
    }

    private void initGlyphLayout(CharSequence str, TextStyle style) {
        Color color = Color.WHITE;
        if (GdxFontUtil.isColorizable(style)) {
            color = GdxFontUtil.argb8888ToColor(style.getColor());
        }

        BitmapFont bitmapFont = getBitmapFont();
        applyScale();
        {
            if (isRightToLeft()) {
                // Reverse codepoints for layout if RTL
                StringBuilder sb = new StringBuilder(str).reverse();
                str = mirrorGlyphs(sb);
            }

            glyphLayout = new GlyphLayout(bitmapFont, str, color, 0, Align.left, false);
            if (glyphLayout.runs.size > 1) {
                throw new IllegalArgumentException(
                        "Arguments result in a layout with multiple glyph runs: " + glyphLayout.runs.size);
            }
            ascent = bitmapFont.getCapHeight() + bitmapFont.getAscent();

            glyphCount = getGlyphCount(glyphLayout);
            setLayoutWidth(glyphLayout.width);
            setLayoutHeight(bitmapFont.getLineHeight());
        }
        resetScale();
    }

    private CharSequence mirrorGlyphs(StringBuilder sb) {
        for (int n = 0; n < sb.length(); n++) {
            char original = sb.charAt(n);
            char mirrored = MirrorChars.getMirrorChar(original);
            if (original != mirrored) {
                sb.setCharAt(n, mirrored);
            }
        }
        return sb;
    }

    public void draw(Batch batch, float dx, float dy, float visibleGlyphs) {
        draw(batch, dx, dy, visibleGlyphs, Color.WHITE);
    }

    public void draw(Batch batch, float dx, float dy, float visibleGlyphs, Color tint) {
        if (visibleGlyphs == 0 || glyphLayout.runs.size == 0) {
            return; // Nothing to draw
        }

        applyScale();
        {
            if (visibleGlyphs < 0f || visibleGlyphs >= glyphCount) {
                // Text fully visible
                drawUnderline(batch, glyphLayout, dx, dy, tint);
                drawLayout(batch, glyphLayout, dx, dy, tint);
            } else {
                // Text partially visible
                int visible = (int)visibleGlyphs;

                GlyphRun run = glyphLayout.runs.first();
                Array<Glyph> glyphs = run.glyphs;
                FloatArray xAdvances = run.xAdvances;

                Object[] oldGlyphs = glyphs.items;
                float[] oldXAdvances = xAdvances.items;
                int oldSize = glyphs.size;
                if (isRightToLeft()) {
                    int invisible = oldSize - visible;
                    for (int n = 0; n < invisible; n++) {
                        dx += xAdvances.get(n);
                    }

                    setGlyphs(glyphs, Arrays.copyOfRange(oldGlyphs, invisible, oldSize));
                    xAdvances.items = Arrays.copyOfRange(oldXAdvances, invisible, xAdvances.size);
                }
                glyphs.size = visible;

                drawUnderline(batch, glyphLayout, dx, dy, tint);
                drawLayout(batch, glyphLayout, dx, dy, tint);

                if (isRightToLeft()) {
                    setGlyphs(glyphs, oldGlyphs);
                    xAdvances.items = oldXAdvances;
                }
                glyphs.size = oldSize;
            }
        }
        resetScale();
    }

    private void drawLayout(Batch batch, GlyphLayout glyphLayout, float dx, float dy, Color tint) {
        BitmapFont bitmapFont = getBitmapFont();
        dy -= bitmapFont.getAscent();

        BitmapFontCache cache = bitmapFont.getCache();
        cache.clear();
        cache.addText(glyphLayout, getX() + dx, getY() + dy);
        cache.tint(tint);
        cache.draw(batch);
    }

    private void drawUnderline(Batch batch, GlyphLayout glyphLayout, float dx, float dy, Color tint) {
        if (!style.isUnderlined()) {
            // Not underlined -> abort
            return;
        }

        BitmapFont bitmapFont = font.getBitmapFont();
        UnderlineMetrics underlineMetrics = font.getUnderlineMetrics();
        float thickness = underlineMetrics.getUnderlineThickness();

        float underlineDy = underlineMetrics.getUnderlinePosition() - bitmapFont.getCapHeight();
        if (ydown) {
            underlineDy = -underlineDy;
        }
        underlineDy -= bitmapFont.getAscent();

        float x = getX() + dx;
        float y = getY() + dy + underlineDy - thickness / 2;

        Color oldColor = batch.getColor();
        try {
            batch.setColor(tint);
            for (GlyphRun run : glyphLayout.runs) {
                float runX = run.x + run.xAdvances.get(0);

                float runWidth = 0f;
                for (int n = 0; n < run.glyphs.size; n++) {
                    runWidth += run.xAdvances.get(1 + n); // Glyph offsets start at index 1
                }

                batch.draw(getBlankTexture(), x + runX, y + run.y, runWidth, thickness);
            }
        } finally {
            batch.setColor(oldColor);
        }
    }

    private static synchronized Texture getBlankTexture() {
        if (blankTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            blankTexture = new Texture(pixmap);
        }
        return blankTexture;
    }

    /** Ugly code needed because Array uses an unchecked cast from Object[] to T[] */
    private static void setGlyphs(Array<Glyph> array, Object[] newGlyphs) {
        array.clear();
        for (Object obj : newGlyphs) {
            array.add((Glyph)obj);
        }
    }

    private void applyScale() {
        font.getBitmapFont().getData().setScale(scaleXY);
    }

    private void resetScale() {
        font.getBitmapFont().getData().setScale(originalScaleX, originalScaleY);
    }

    @Override
    public int getGlyphId(int index) {
        return getGlyph(glyphLayout, index).id;
    }

    @Override
    public int getGlyphCount() {
        return glyphCount;
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        return style;
    }

    @Override
    public float getKerning(int glyphId) {
        if (glyphCount == 0) {
            return 0f;
        } else if (glyphId != (char)glyphId) {
            return 0f; // libGDX Glyph.getKerning uses char instead of int
        }

        Glyph finalGlyph = getGlyph(glyphLayout, glyphCount - 1);
        int kerning = finalGlyph.getKerning((char)glyphId);
        return scaleXY * kerning;
    }

    private static Glyph getGlyph(GlyphLayout layout, int index) {
        int offset = 0;
        for (GlyphRun run : layout.runs) {
            if (index < offset) {
                break;
            }
            if (index - offset < run.glyphs.size) {
                return run.glyphs.get(index - offset);
            }
            offset += run.glyphs.size;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    private static int getGlyphCount(GlyphLayout layout) {
        int count = 0;
        for (GlyphRun run : layout.runs) {
            count += run.glyphs.size;
        }
        return count;
    }

    @Override
    public float getAscent() {
        return ascent;
    }

    private BitmapFont getBitmapFont() {
        return font.getBitmapFont();
    }

    @Override
    public void setParameters(LayoutParameters params) {
        super.setParameters(params);

        ydown = params.ydir > 0;
    }

}
