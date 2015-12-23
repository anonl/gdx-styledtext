package nl.weeaboo.styledtext;

public enum EFontStyle {

    PLAIN("plain"),
    ITALIC("italic"),
    BOLD("bold"),
    BOLD_ITALIC("bolditalic");

    private final String id;

    private EFontStyle(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * @return The enum value matching the given string representation, or {@code null} if no match is found.
     *         The matching algorithm is case-sensitive.
     */
    public static EFontStyle fromString(String str) {
        for (EFontStyle style : values()) {
            if (style.toString().equals(str)) {
                return style;
            }
        }
        return null;
    }

    public boolean isBold() {
        return this == BOLD || this == BOLD_ITALIC;
    }

    public boolean isItalic() {
        return this == ITALIC || this == BOLD_ITALIC;
    }

    /**
     * Merges the two styles together, combining the bold and italic attributes of both styles.
     *
     * @param a First style, may be {@code null}.
     * @param a Second style, may be {@code null}.
     */
    public static EFontStyle combine(EFontStyle a, EFontStyle b) {
        if (a == null) return b;
        if (b == null) return a;

        if (a.isItalic()) {
            return (b.isBold() ? BOLD_ITALIC : a);
        }

        if (a.isBold()) {
            return (b.isItalic() ? BOLD_ITALIC : a);
        }

        // a is neither bold nor italic, so only b's attributes are relevant
        return b;
    }

}
