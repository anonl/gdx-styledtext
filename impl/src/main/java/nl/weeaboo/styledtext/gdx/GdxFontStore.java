package nl.weeaboo.styledtext.gdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.styledtext.layout.IFontStore;

public class GdxFontStore implements IFontStore {

    private static final int SCORE_NAME  = 1000000;
    private static final int SCORE_STYLE = 100000;
    private static final int SCORE_SIZE  = 10000;

    private final List<FontInfo> fonts = new ArrayList<FontInfo>();

    public void registerFont(String name, EFontStyle style, BitmapFont font, int pixelSize) {
        fonts.add(new FontInfo(name, style, font, pixelSize));
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

    private FontInfo findFont(TextStyle style) {
        int bestScore = Integer.MIN_VALUE;
        FontInfo bestInfo = null;

        for (FontInfo info : fonts) {
            int score = 0;

            // Check name
            if (info.name.equalsIgnoreCase(style.getFontName())) {
                score += SCORE_NAME;
            }

            // Check style
            EFontStyle fontStyle = style.getFontStyle();
            if (fontStyle.isItalic() == info.style.isItalic()) {
                score += SCORE_STYLE;
            }
            if (fontStyle.isBold() == info.style.isBold()) {
                score += SCORE_STYLE;
            }

            // Check size
            score -= SCORE_SIZE * Math.abs(GdxFontMetrics.getScale(style, info.font) - 1f);

            if (score > bestScore) {
                bestScore = score;
                bestInfo = info;
            }
        }

        return bestInfo;
    }

    private static class FontInfo {

        public final String name;
        public final EFontStyle style;
        public final BitmapFont font;
        private final int nativePixelSize;

        public FontInfo(String name, EFontStyle style, BitmapFont font, int nativePixelSize) {
            this.name = name;
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
