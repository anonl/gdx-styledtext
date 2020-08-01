package nl.weeaboo.styledtext.layout;

import javax.annotation.CheckForNull;

import nl.weeaboo.styledtext.TextStyle;

public interface IFontRegistry {

    /**
     * Finds most appropriate font data for the given text style.
     *
     * @return The best match (may not be an exact match), or {@code null} if there's no reasonable match.
     */
    @CheckForNull
    IFontMetrics getFontMetrics(TextStyle style);

}
