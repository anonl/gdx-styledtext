package nl.weeaboo.styledtext.gdx;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.styledtext.layout.IFontRegistry;

public class GdxFontStore implements IFontRegistry {

    private static final int SCORE_NAME     = 10000000;
    private static final int SCORE_STYLE    = 1000000;
    private static final int SCORE_COLOR    = 100000;
    private static final int SCORE_SIZE     = 10000;
    private static final int SCORE_OUTLINE  = 100;
    private static final int SCORE_SHADOW   = 100;

    private final CopyOnWriteArrayList<GdxFont> fonts = new CopyOnWriteArrayList<GdxFont>();

    /**
     * Removes the given font from this registry.
     */
    public void addFont(GdxFont font) {
        fonts.add(font);
    }

    /**
     * Removes the given font from this registry.
     * <p>
     * <strong>Note: This doesn't dispose the font</strong>
     */
    public void removeFont(GdxFont font) {
        fonts.remove(font);
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        GdxFont bestMatch = findFont(style);
        if (bestMatch == null) {
            return null;
        }

        return new GdxFontMetrics(bestMatch, style);
    }

    public List<GdxFont> getFonts() {
        return Collections.unmodifiableList(fonts);
    }

    private GdxFont findFont(TextStyle paramStyle) {
        int bestScore = Integer.MIN_VALUE;
        GdxFont bestInfo = null;

        for (GdxFont info : fonts) {
            int score = 0;

            TextStyle infoStyle = info.getStyle();

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
            score -= (int)(SCORE_SIZE * Math.abs(GdxFontMetrics.getScale(paramStyle, info.getBitmapFont()) - 1f));

            // Check color (only if not colorizable)
            if (!GdxFontUtil.isColorizable(infoStyle) && paramStyle.getColor() != infoStyle.getColor()) {
                score -= SCORE_COLOR;
            }

            // Check outline
            int nativePixelSize = info.getNativePixelSize();
            if (paramStyle.hasOutline() || infoStyle.hasOutline()) {
                if (paramStyle.getOutlineColor() != infoStyle.getOutlineColor()) {
                    score -= SCORE_OUTLINE;
                } else {
                    float decorationBadness = getDecorationBadness(paramStyle.getOutlineSize(),
                            infoStyle.getOutlineSize(), nativePixelSize);
                    score -= (int)(SCORE_OUTLINE * decorationBadness);
                }
            }

            // Check shadow
            if (paramStyle.hasShadow() || infoStyle.hasShadow()) {
                if (paramStyle.getShadowColor() != infoStyle.getShadowColor()) {
                    score -= SCORE_SHADOW;
                } else {
                    float badnessX = getDecorationBadness(paramStyle.getShadowDx(), infoStyle.getShadowDx(),
                            nativePixelSize);
                    float badnessY = getDecorationBadness(paramStyle.getShadowDy(), infoStyle.getShadowDy(),
                            nativePixelSize);
                    score -= (int)(SCORE_SHADOW * .5f * (badnessX + badnessY));
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

}
