package nl.weeaboo.styledtext.gdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.styledtext.layout.IFontStore;

public class GdxFontStore implements IFontStore {

    private static final int SCORE_NAME     = 1000000;
    private static final int SCORE_STYLE    = 100000;
    private static final int SCORE_SIZE     = 10000;
    private static final int SCORE_OUTLINE  = 100;
    private static final int SCORE_SHADOW   = 100;

    private final List<FontInfo> fonts = new ArrayList<FontInfo>();

    /**
     * @deprecated Use {@link #registerFont(TextStyle, BitmapFont, int)} instead.
     */
    @Deprecated
    public void registerFont(String name, EFontStyle style, BitmapFont font, int pixelSize) {
        TextStyle ts = new TextStyle(name, style, pixelSize);
        registerFont(ts, font, pixelSize);
    }
    public void registerFont(TextStyle style, BitmapFont font, int pixelSize) {
        fonts.add(new FontInfo(style, font, pixelSize));
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        FontInfo bestMatch = findFont(style);
        if (bestMatch == null) {
            return null;
        }

        BitmapFont font = bestMatch.font;
        return new GdxFontMetrics(font, bestMatch.getScaleFor(style));
    }

    private FontInfo findFont(TextStyle paramStyle) {
        int bestScore = Integer.MIN_VALUE;
        FontInfo bestInfo = null;

        for (FontInfo info : fonts) {
            int score = 0;

            TextStyle infoStyle = info.style;

            // Check name
            if (infoStyle.getFontName().equalsIgnoreCase(paramStyle.getFontName())) {
                score += SCORE_NAME;
            }

            // Check style
            EFontStyle fontStyle = paramStyle.getFontStyle();
            if (fontStyle.isItalic() == infoStyle.getFontStyle().isItalic()) {
                score += SCORE_STYLE;
            }
            if (fontStyle.isBold() == infoStyle.getFontStyle().isBold()) {
                score += SCORE_STYLE;
            }

            // Check size
            score -= SCORE_SIZE * Math.abs(GdxFontMetrics.getScale(paramStyle, info.font) - 1f);

            // Check outline
            if (paramStyle.hasOutline() || infoStyle.hasOutline()) {
                if (paramStyle.getOutlineColor() != infoStyle.getOutlineColor()) {
                    score -= SCORE_OUTLINE;
                } else {
                    score -= SCORE_OUTLINE * getDecorationBadness(paramStyle.getOutlineSize(),
                            infoStyle.getOutlineSize(), info.nativePixelSize);
                }
            }

            // Check shadow
            if (paramStyle.hasShadow() || infoStyle.hasShadow()) {
                if (paramStyle.getShadowColor() != infoStyle.getShadowColor()) {
                    score -= SCORE_SHADOW;
                } else {
                    float badnessX = getDecorationBadness(paramStyle.getShadowDx(), infoStyle.getShadowDx(),
                            info.nativePixelSize);
                    float badnessY = getDecorationBadness(paramStyle.getShadowDy(), infoStyle.getShadowDy(),
                            info.nativePixelSize);
                    score -= SCORE_SHADOW * (badnessX + badnessY) / 2f;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestInfo = info;
            }
        }

        return bestInfo;
    }

    /**
     * Calculates a mismatch fraction between two outline/shadow sizes
     * @return The degree to which the two values mismatch (between 0.0 and 1.0).
     */
    private float getDecorationBadness(float a, float b, int nativePixelSize) {
        float badness = Math.abs(a - b);
        badness = .1f * badness / nativePixelSize;
        return Math.max(0, Math.min(1, badness));
    }

    private static class FontInfo {

        public final TextStyle style;
        public final BitmapFont font;
        public final int nativePixelSize;

        public FontInfo(TextStyle style, BitmapFont font, int nativePixelSize) {
            this.style = style;
            this.font = font;
            this.nativePixelSize = nativePixelSize;
        }

        /** @return The amount of additional scaling required to reach the desired size */
        public float getScaleFor(TextStyle style) {
            return style.getFontSize() / nativePixelSize;
        }

    }

}
