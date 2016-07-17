package nl.weeaboo.styledtext.gdx;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.styledtext.layout.UnderlineMetrics;

public class GdxFontStore implements IFontStore, Disposable {

    private static final int SCORE_NAME     = 10000000;
    private static final int SCORE_STYLE    = 1000000;
    private static final int SCORE_COLOR    = 100000;
    private static final int SCORE_SIZE     = 10000;
    private static final int SCORE_OUTLINE  = 100;
    private static final int SCORE_SHADOW   = 100;

    private final CopyOnWriteArrayList<GdxFontInfo> fonts = new CopyOnWriteArrayList<GdxFontInfo>();

    @Override
    public void dispose() {
        for (GdxFontInfo font : fonts) {
            font.dispose();
        }
        fonts.clear();
    }

    /**
     * @deprecated Use {@link #registerFont(GdxFontInfo)} instead.
     */
    @Deprecated
    public void registerFont(String name, EFontStyle style, BitmapFont font, int pixelSize) {
        TextStyle ts = new TextStyle(name, style, pixelSize);
        registerFont(new GdxFontInfo(ts, font, pixelSize, UnderlineMetrics.NONE));
    }
    public void registerFont(GdxFontInfo fontInfo) {
        fonts.add(fontInfo);
    }

    public void disposeFont(GdxFontInfo fontInfo) {
        fonts.remove(fontInfo);
        fontInfo.dispose();
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        GdxFontInfo bestMatch = findFont(style);
        if (bestMatch == null) {
            return null;
        }

        BitmapFont font = bestMatch.font;
        return new GdxFontMetrics(font, bestMatch.getScaleFor(style), bestMatch.underlineMetrics);
    }

    public List<GdxFontInfo> getFonts() {
        return Collections.unmodifiableList(fonts);
    }

    private GdxFontInfo findFont(TextStyle paramStyle) {
        int bestScore = Integer.MIN_VALUE;
        GdxFontInfo bestInfo = null;

        for (GdxFontInfo info : fonts) {
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

            // Check color (only if not colorizable)
            if (!GdxFontUtil.isColorizable(infoStyle) && paramStyle.getColor() != infoStyle.getColor()) {
                score -= SCORE_COLOR;
            }

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

}
